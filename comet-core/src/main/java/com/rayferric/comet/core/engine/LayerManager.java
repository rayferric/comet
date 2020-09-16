package com.rayferric.comet.core.engine;

public class LayerManager {
    public LayerManager(int layerCount) {
        layers = new Layer[layerCount];
        for(int i = 0; i < layerCount; i++) {
            layers[i] = new Layer();
        }
    }

    public Layer[] getLayers() {
        return layers;
    }

    private final Layer[] layers;
}
