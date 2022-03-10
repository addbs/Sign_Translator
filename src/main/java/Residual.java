import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.DataType;
import ai.djl.ndarray.types.Shape;
import ai.djl.nn.*;
import ai.djl.nn.convolutional.Conv2d;
import ai.djl.nn.norm.BatchNorm;
import ai.djl.training.ParameterStore;
import ai.djl.util.PairList;

import java.util.Arrays;

// Classe utilisée ppur Training2 pour la création d'un réseau de neurones résiduel
class Residual extends AbstractBlock {

    private static final byte VERSION = 2;

    public ParallelBlock block;
    // Constructeur
    public Residual(int numChannels, boolean use1x1Conv, Shape strideShape) {
        super(VERSION);

        SequentialBlock b1;
        SequentialBlock conv1x1;
        
        // Création du block résiduel
        b1 = new SequentialBlock();
        
        // La composition du bloc est défini dans le travail de Kaiming He dans "Deep Residual Learning for Image Recognition"
        b1.add(Conv2d.builder()
                .setFilters(numChannels)
                .setKernelShape(new Shape(3, 3))
                .optPadding(new Shape(1, 1))
                .optStride(strideShape)
                .build())
                .add(BatchNorm.builder().build())
                .add(Activation::relu)
                .add(Conv2d.builder()
                        .setFilters(numChannels)
                        .setKernelShape(new Shape(3, 3))
                        .optPadding(new Shape(1, 1))
                        .build())
                .add(BatchNorm.builder().build());

        if (use1x1Conv) {
            conv1x1 = new SequentialBlock();
            conv1x1.add(Conv2d.builder()
                    .setFilters(numChannels)
                    .setKernelShape(new Shape(1, 1))
                    .optStride(strideShape)
                    .build());
        } else {
            conv1x1 = new SequentialBlock();
            conv1x1.add(Blocks.identityBlock());
        }

        block = addChildBlock("residualBlock", new ParallelBlock(
                list -> {
                    NDList unit = list.get(0);
                    NDList parallel = list.get(1);
                    return new NDList(
                            unit.singletonOrThrow()
                                    .add(parallel.singletonOrThrow())
                                    .getNDArrayInternal()
                                    .relu());
                },
                Arrays.asList(b1, conv1x1)));
    }
    // L'ensemble des méthodes qui suivent sont des implémentations nécessaires à 
    @Override
    public String toString() {
        return "Residual()";
    }

    @Override
    public NDList forward(ParameterStore parameterStore, NDList inputs, boolean training) {
        return forward(parameterStore, inputs, training, null);
    }
    
    @Override
    public NDList forwardInternal(ParameterStore parameterStore, NDList inputs, boolean training, PairList<String, Object> pairList) {
        return block.forward(parameterStore, inputs, training);
    }
	
    @Override
    public Shape[] getOutputShapes(NDManager ndManager, Shape[] inputs) {
        Shape[] current = inputs;
        for (Block block : block.getChildren().values()) {
            current = block.getOutputShapes(ndManager, current);
        }
        return current;
    }

    @Override
    public void initializeChildBlocks(NDManager manager, DataType dataType, Shape... inputShapes) {
        block.initialize(manager, dataType, inputShapes);
    }
}