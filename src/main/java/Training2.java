import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ai.djl.Model;
import ai.djl.basicdataset.cv.classification.ImageFolder;
import ai.djl.metric.Metrics;
import ai.djl.modality.cv.transform.Resize;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.DataType;
import ai.djl.ndarray.types.Shape;
import ai.djl.nn.Activation;
import ai.djl.nn.SequentialBlock;
import ai.djl.nn.convolutional.Conv2d;
import ai.djl.nn.core.Linear;
import ai.djl.nn.pooling.Pool;
import ai.djl.training.DefaultTrainingConfig;
import ai.djl.training.EasyTrain;
import ai.djl.training.ParameterStore;
import ai.djl.training.Trainer;
import ai.djl.training.TrainingResult;
import ai.djl.training.evaluator.Accuracy;
import ai.djl.training.initializer.XavierInitializer;
import ai.djl.training.listener.TrainingListener;
import ai.djl.training.loss.Loss;
import ai.djl.training.optimizer.Optimizer;
import ai.djl.training.tracker.Tracker;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.Pipeline;
import ai.djl.translate.TranslateException;
import ai.djl.nn.norm.BatchNorm;

// La classe Training2 est une implémentation de l'entrainement détaillé de Resnet
public class Training2 {
	
	private static int batchSize = 128;
	int width = 100;
	int height = 100;
	int inputSize = width*height;
	int outputSize = 26;
	int epochs = 5;
	double avgTrainTimePerEpoch = 0;
	
	List<String> synset;
	String modelName = "Model";
	
	//Loss loss = Loss.softmaxCrossEntropyLoss();
	
	Map<String, double[]> evaluatorMetrics = new HashMap<>();
	
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
		ImageFolder.Builder datasetBuilder = ImageFolder.builder()
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
		// Création d'un manager
		// Les ND Managers sont utilisés pour créer des NDArrays (tableau à n dimensions sur le moteur natif)
		NDManager manager = NDManager.newBaseManager();
		
		// Création des datasets 
		// Création du dataset d'entrainement au format ImageFolder
		ImageFolder trainDataset = initDataset("/Users/addubois/Downloads/Dataset/Train");
        // Création du dataset de validation au format ImageFolder
		ImageFolder valDataset = initDataset("/Users/addubois/Downloads/Dataset/Validate");
        
		//Création du bloc sur lequel le modèle sera basé
		SequentialBlock net = getModel();
		
		// Création d'un residual bloc 
		SequentialBlock blk = new SequentialBlock();
		blk.add(new Residual(3, false, new Shape(1, 1)));
		
		// 
		ParameterStore parameterStore = new ParameterStore(manager, true);
		
		NDArray X = manager.randomUniform(0f, 1f, new Shape(3, 3, 224, 224));
		// L'initialisateur Xavier est conçu pour que l'échelle des gradients reste à peu près la même dans toutes les couches.
		net.setInitializer(new XavierInitializer());
		net.initialize(manager, DataType.FLOAT32, X.getShape());
		Shape currentShape = X.getShape();
		
		// Affichage des différentes couches pour Resnet
		for (int i = 0; i < net.getChildren().size(); i++) {

		    X = net.getChildren().get(i).getValue().forward(parameterStore, new NDList(X), false).singletonOrThrow();
		    currentShape = X.getShape();
		    System.out.println(net.getChildren().get(i).getKey() + " layer output : " + currentShape);
		}
		// Création du modèle Resnet avec le bloc net 
		Model model = Model.newInstance("cnn");
		model.setBlock(net);
		
		// Initialisation de la fonction de perte 
		Loss loss = Loss.softmaxCrossEntropyLoss();
		
		// Initialisation du Tracker 
		Tracker lrt = Tracker.fixed(0.01f);
		// Initialisation du Optimizer 
		// Cette méthode d'optimisation est basé sur le travail de Diederik P. Kingma dans "Adam: A Method for Stochastic Optimization"
		// Basé sur le Tracker défini précédemment 
		Optimizer adam = Optimizer.adam().optLearningRateTracker(lrt).build();
		
		// Création de la configuration de l'entrainement à partir de l'optimizer Adam et de fonction de perte
		DefaultTrainingConfig config = new DefaultTrainingConfig(loss).optOptimizer(adam) 
						// Ajout d'une interface qui permet d'effectuer certaines actions lorsque certains événements se sont produits lors de l'entrainement
		                .addTrainingListeners(TrainingListener.Defaults.logging()); 

		Trainer trainer = model.newTrainer(config);
		
		training(trainDataset, valDataset, epochs, trainer);
		
		TrainingResult result = trainer.getTrainingResult();
        float accuracy = result.getValidateEvaluation("Accuracy");
        model.setProperty("Accuracy", String.format("%.5f", accuracy));
        model.setProperty("Loss", String.format("%.5f", result.getValidateLoss()));
        model.setProperty("Epoch", String.valueOf(epochs));

        model.save(Paths.get("build/logs"), modelName);
	}
	// Méthode création du block du modèle Resnet
	public SequentialBlock getModel() {
		SequentialBlock net = new SequentialBlock();
		net
		// Ajout d'une couche convolutive 7x7 avec 64 canaux de sortie et un stride de 2
		        .add(
		                Conv2d.builder()
		                        .setKernelShape(new Shape(7, 7))
		                        .optStride(new Shape(2, 2))
		                        .optPadding(new Shape(3, 3))
		                        .setFilters(64)
		                        .build())
		        // Ajout d'une couche de normalisation des lots
		        .add(BatchNorm.builder().build())
		        .add(Activation::relu)
		        // Ajout d'une couche de mise en commun maximale 3×3 avec un stride de 2.
		        .add(Pool.maxPool2dBlock(new Shape(3, 3), new Shape(2, 2), new Shape(1, 1)))
		        // ResNet utilise quatre modules constitués de blocs résiduels, 
		        // chacun d'entre eux utilisant plusieurs blocs résiduels avec le même nombre de canaux de sortie.
		        .add(resnetBlock(64, 2, true))
			    .add(resnetBlock(128, 2, false))
			    .add(resnetBlock(256, 2, false))
			    .add(resnetBlock(512, 2, false))
			    // ajout d'une couche de mise en commun de la moyenne globale, suivie de la couche de l'output entièrement connectée
			    .add(Pool.globalAvgPool2dBlock())
			    .add(Linear.builder().setUnits(10).build());
		return net;
	}
	// Méthode d'entrainement 
	public void training(ImageFolder trainIter, ImageFolder testIter, int numEpochs, Trainer trainer) throws IOException, TranslateException {
		double avgTrainTimePerEpoch = 0;
		Map<String, double[]> evaluatorMetrics = new HashMap<>();

		trainer.setMetrics(new Metrics());
	
		EasyTrain.fit(trainer, numEpochs, trainIter, testIter);

		Metrics metrics = trainer.getMetrics();

		trainer.getEvaluators().stream()
		.forEach(evaluator -> {
			evaluatorMetrics.put("train_epoch_" + evaluator.getName(), metrics.getMetric("train_epoch_" + evaluator.getName()).stream()
					.mapToDouble(x -> x.getValue().doubleValue()).toArray());
			evaluatorMetrics.put("validate_epoch_" + evaluator.getName(), metrics.getMetric("validate_epoch_" + evaluator.getName()).stream()
					.mapToDouble(x -> x.getValue().doubleValue()).toArray());
		});

		avgTrainTimePerEpoch = metrics.mean("epoch");

		double[] trainLoss = evaluatorMetrics.get("train_epoch_SoftmaxCrossEntropyLoss");
		double[] trainAccuracy = evaluatorMetrics.get("train_epoch_Accuracy");
		double[] testAccuracy = evaluatorMetrics.get("validate_epoch_Accuracy");

		System.out.printf("loss %.3f," , trainLoss[numEpochs-1]);
		System.out.printf(" train acc %.3f," , trainAccuracy[numEpochs-1]);
		System.out.printf(" test acc %.3f\n" , testAccuracy[numEpochs-1]);
		System.out.printf("%.1f examples/sec \n", trainIter.size() / (avgTrainTimePerEpoch / Math.pow(10, 9)));
	}
	// Méthode du module de blocs résiduel pour Resnet
	public SequentialBlock resnetBlock(int numChannels, int numResiduals, boolean firstBlock) {
        SequentialBlock blk = new SequentialBlock();

        for (int i = 0; i < numResiduals; i++) {

            if (i == 0 && !firstBlock) {
                blk.add(new Residual(numChannels, true, new Shape(2, 2)));
            } else {
                blk.add(new Residual(numChannels, false, new Shape(1, 1)));
            }
        }
        return blk;
}
	
}