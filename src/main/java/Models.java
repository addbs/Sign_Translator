import ai.djl.ndarray.types.Shape;
import ai.djl.nn.Activation;
import ai.djl.nn.Block;
import ai.djl.nn.Blocks;
import ai.djl.nn.SequentialBlock;
import ai.djl.nn.convolutional.Conv2d;
import ai.djl.nn.core.Linear;
import ai.djl.nn.pooling.Pool;
import ai.djl.basicmodelzoo.cv.classification.ResNetV1;
import ai.djl.Model;

/*
    L'apprentissage profond utilise un réseau neuronal (ResNet-50) pour entraîner le modèle.
    ResNet-50 est un réseau résiduel profond de 50 couches ; il convient à la classification des images.
 */
public class Models {
	static int width = 100;
	static int height = 100;
	static int inputSize = width*height;
	static int outputSize = 26;
    public static Model getModel(int numOfOutput, int height, int width) {
        // Création d'une nouvelle instance d'un modèle vide
        Model model = Model.newInstance("model");
        
        Block resNet50 =  ResNetV1.builder()
                        .setImageShape(new Shape(3, height, width))
                        .setNumLayers(50)
                        .setOutSize(numOfOutput)
                        .build();

        /*
 		//On peut également faire :
        SequentialBlock block = new SequentialBlock();
        block
        .add(Conv2d.builder()
                    .setKernelShape(new Shape(5, 5))
                    .optPadding(new Shape(2, 2))
                    .optBias(false)
                    .setFilters(6)
                    .build())
        .add(Activation::sigmoid)
        .add(Pool.avgPool2dBlock(new Shape(5, 5), new Shape(2, 2), new Shape(2, 2)))
        .add(Conv2d.builder()
                    .setKernelShape(new Shape(5, 5))
                    .setFilters(16).build())
        .add(Activation::sigmoid)
        .add(Pool.avgPool2dBlock(new Shape(5, 5), new Shape(2, 2), new Shape(2, 2)))
        // Blocks.batchFlattenBlock() will transform the input of the shape (batch size, channel,
        // height, width) into the input of the shape (batch size,
        // channel * height * width)
        .add(Blocks.batchFlattenBlock())
        .add(Linear
                    .builder()
                    .setUnits(120)
                    .build())
        .add(Activation::sigmoid)
        .add(Linear
                    .builder()
                    .setUnits(84)
                    .build())
        .add(Activation::sigmoid)
        .add(Linear
                    .builder()
                    .setUnits(10)
                    .build());
        
        // ou encore ça : 
         
         
        block.add(Blocks.batchFlattenBlock(inputSize));
		block.add(Linear.builder().setUnits(128).build());
		block.add(Activation::relu);
		block.add(Linear.builder().setUnits(64).build());
		block.add(Activation::relu);
		block.add(Linear.builder().setUnits(outputSize).build());
	
        */

        // Définition du réseau neuronal au modèle
        model.setBlock(resNet50);
		//model.setBlock(block);
        return model;
    }
}

