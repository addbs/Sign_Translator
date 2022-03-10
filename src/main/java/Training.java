import java.util.List;
import java.util.Arrays;
import ai.djl.Model;
import ai.djl.basicdataset.cv.classification.ImageFolder;
import ai.djl.modality.cv.transform.Resize;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.ndarray.types.Shape;
import ai.djl.translate.Pipeline;
import ai.djl.translate.TranslateException;
import ai.djl.training.initializer.Initializer;
import ai.djl.training.initializer.XavierInitializer;
import ai.djl.training.evaluator.Accuracy;
import ai.djl.training.TrainingResult;
import ai.djl.training.loss.Loss;
import ai.djl.training.listener.*;
import ai.djl.training.DefaultTrainingConfig;
import ai.djl.training.Trainer;
import ai.djl.training.tracker.Tracker;
import ai.djl.training.optimizer.Optimizer;
import ai.djl.training.EasyTrain;
import ai.djl.training.util.ProgressBar;

import ai.djl.metric.Metrics;
import java.nio.file.Paths;
import java.io.IOException;

//La classe Training2 est une implémentation de l'entrainement de notre modèle 
public class Training {
	
	private static int batchSize = 30;
	int width = 100;
	int height = 100;
	int inputSize = width*height;
	int outputSize = 26;
	List<String> synset;
	int epochs = 3;
	
	String modelName = "Model_final";
	
	// Constructeur
	public Training() {}
	/* 
	La méthode initDataset permet d'initialiser notre Dataset au format ImageFolder
	ImageFolder n'est recommandé que lorsque l'on souhaite parcourir des images avec leur seule étiquette.
	Notre dataset est donc structuré de la manière qui suit: 
	/dataset_root/A/A1.png/
	/dataset_root/A/A2.png/
	... 
	/dataset_root/B/B1.png/
	/dataset_root/B/B2.png/
	*/
	public ImageFolder initDataset(String path) throws IOException, TranslateException {
		
		ImageFolder.Builder datasetBuilder = 
				ImageFolder.builder()
				.setRepositoryPath(path)
				.optPipeline(new Pipeline())
				.addTransform(new Resize(width, height))
				.addTransform(new ToTensor())
				.setSampling(batchSize, true);
		ImageFolder dataset = datasetBuilder.build();
		
		dataset.prepare(new ProgressBar());
		synset  = dataset.getSynset(); 
		
		return dataset;
		}
	
	/*
	La méthode train est la méthode principale de la classe Training. 
	Elle permet de créer un modèle puis de l'entrainer à partir de nos datasets
	Enfin elle enregistre ce modèle pour pouvoir l'utiliser plus tard 
	Notre réseau de neurone est un Réseau Neuronal Convolutif (Convolutional Neural Network) 
	et plus particulièrement un Residual Network
	Il est basé sur le travail de Kaiming He dans "Deep Residual Learning for Image Recognition"
	 */
	public void train() throws IOException, TranslateException {
				
		// Création du dataset d'entrainement au format ImageFolder
		ImageFolder trainDataset = initDataset("/Users/addubois/Downloads/Dataset/Train");
        // Création du dataset de validation au format ImageFolder
		ImageFolder valDataset = initDataset("/Users/addubois/Downloads/Dataset/Validate");
		
		// Création du modèle
        Model model = Models.getModel(outputSize, height, width);	
		
        // Création de la configuration pour l'entrainement
		DefaultTrainingConfig config = setupTrainingConfig();
		// Création de l'entraineur pour le modèle
		Trainer trainer = model.newTrainer(config);		
		
		// Initialisation des Metrics 
		Metrics metrics = new Metrics();
		trainer.setMetrics(metrics);
		
		// Initialisation de la forme pour l'entrée
		Shape inputShape = new Shape(1, 3, height, width);
		trainer.initialize(inputShape);
		
		// Entrainement du modèle avec les 2 datasets et le nombre d'epochs voulu
		EasyTrain.fit(trainer, epochs, trainDataset, valDataset);
		
		// Affichage des résultats
        TrainingResult result = trainer.getTrainingResult();
        float accuracy = result.getValidateEvaluation("Accuracy");
        model.setProperty("Accuracy", String.format("%.5f", accuracy));
        model.setProperty("Loss", String.format("%.5f", result.getValidateLoss()));
        model.setProperty("Epoch", String.valueOf(epochs));
        
        // Sauvegarde du modèle 
        model.save(Paths.get("build/logs"), modelName);
	}
	// Méthode de la création de la configuration de l'entrainement
	private static DefaultTrainingConfig setupTrainingConfig() {
        
		// nombre d'epoch pour changer le taux d'apprentissage
        int[] epoch = {3, 5, 8};
        int[] steps = Arrays.stream(epoch).map(k -> k * 60000 / batchSize).toArray();

        //Initialisation des poids du réseau neuronal à l'aide de l'initialisateur Xavier.
        //Les poids déterminent l'importance de la valeur d'entrée.
        //Les poids sont d'abord aléatoires, puis modifiés après chaque itération pour corriger les erreurs (utilise le taux d'apprentissage).
        Initializer initializer =
                new XavierInitializer(
                        XavierInitializer.RandomType.UNIFORM, XavierInitializer.FactorType.AVG, 2);

        // On peut également utiliser : new XavierInitializer(RandomType.GAUSSIAN, FactorType.IN, 2)
        
        /*
         * Le taux d’apprentissage est un hyperparamètre — un facteur qui définit 
         * le système ou fixe les conditions de son fonctionnement avant le processus
         * d’apprentissage — qui contrôle l’ampleur du changement que subit le modèle
         * en réponse à l’erreur estimée chaque fois que les poids du modèle sont modifiés.
         * Des taux d’apprentissage trop élevés peuvent entraîner des processus 
         * d’apprentissage instables ou l’apprentissage d’un ensemble de poids sous-optimaux. Des taux d’apprentissage trop faibles peuvent entraîner un processus de formation long qui risque de se bloquer.
         */
        
        Tracker learningRateTracker =
                Tracker.warmUp()
                        .optWarmUpBeginValue(1e-4f)
                        .optWarmUpSteps(200)
                        .setMainTracker(
                                Tracker.multiFactor()
                                        .setSteps(steps)
                                        .setBaseValue(1e-3f)
                                        .optFactor((float) Math.sqrt(.1f))
                                        .build())
                        .build();
        
        // Initialisation du Optimizer 
     	// Cette méthode d'optimisation est basé sur le travail de Diederik P. Kingma dans "Adam: A Method for Stochastic Optimization"
     	// Basé sur le Tracker défini précédemment 
        
        Optimizer optimizer = Optimizer.adam().optLearningRateTracker(learningRateTracker).build();
        
        //renvoie la configuration de l'entrainement avec l'optimizer Adam et la fonction de perte 
        return new DefaultTrainingConfig(Loss.softmaxCrossEntropyLoss())
        		.addEvaluator(new Accuracy())
                .optOptimizer(optimizer)
                .optInitializer(initializer)
                .addTrainingListeners(TrainingListener.Defaults.logging());
        //Le batchsize est déjà défini dans le dataset
    }
}



