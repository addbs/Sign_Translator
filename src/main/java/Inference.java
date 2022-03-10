import ai.djl.Model;
import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.transform.Resize;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.modality.cv.BufferedImageFactory;
import ai.djl.modality.cv.translator.ImageClassificationTranslator;
import ai.djl.modality.cv.Image;
import ai.djl.translate.Pipeline;
import ai.djl.translate.TranslateException;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Arrays;

// Cette classe sert à la traductiuon du modèle pour une photo qui est fournie
public class Inference {
	
    private static final int NUM_OF_OUTPUT = 26;
    private static final int NEW_HEIGHT = 100;
    private static final int NEW_WIDTH = 100;
    
    String imageFilePath;
    String modelParamsPath = "build/logs/";
    String modelParamsName = "Model_final";
    
    public Inference(String path) {
    	imageFilePath = path;
    }
    // Méthode principale qui permet de faire la prédiction
    public Classifications predict() throws IOException, ModelException, TranslateException  {

        // Chargement de l'image 
        Image img = new BufferedImageFactory().fromFile(Paths.get(imageFilePath));      

        // La classification est ce qui contient l'ensemble des probabilités pour chacunes des étiquettes 
        Classifications predictResult;
        
        // Chargement du modèle enregistré
        Model model = Models.getModel(NUM_OF_OUTPUT, NEW_HEIGHT, NEW_WIDTH);
        model.load(Paths.get(modelParamsPath), modelParamsName);
        
        //Liste des étiquettes possibles 
        List<String> l = Arrays.asList("A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z");
        
        //Le translator que l'on utilise pour ce projet (défini de base pour 
        //le traitement d'images).
        //Un translator sert à : --
        ImageClassificationTranslator translator = ImageClassificationTranslator.builder()
            		.setPipeline(new Pipeline().add(new Resize(100)))
            		.addTransform(new ToTensor())
            		.optFlag(Image.Flag.COLOR)
            		.optSynset(l)
            		.build();
            
        //Exécution de l'inférence en utilisant un prédicteur
        // Un prédicteur est lié au modèle selon le traducteur défini	
        try (Predictor<Image, Classifications> predictor = model.newPredictor(translator)) {
                predictResult = predictor.predict(img);
            }
        return predictResult;
    }
}
