package network;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import cost.CostFunction;
import cost.Quadratic;
import math.Matrix;
import math.Vec;
import optimizer.GradientDescent;
import optimizer.Optimizer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class NeuralNetwork implements Serializable {

    @JsonProperty("costFunction")
    private CostFunction costFunction;
    @JsonProperty("networkInputSize")
    private int networkInputSize;
    @JsonProperty("l2")
    private double l2;
    @JsonProperty("optimizer")
    private Optimizer optimizer;
    @JsonProperty("layers")
    private List<Layer> layers = new ArrayList<>();

    public NeuralNetwork(){}
    /**
     * Создает нейронную сеть с гиперпараметрами в билдере
     * @param nb билдер с гиперпараметрами сети
     */
    private NeuralNetwork(Builder nb) {
        costFunction = nb.costFunction;
        networkInputSize = nb.networkInputSize;
        optimizer = nb.optimizer;
        l2 = nb.l2;
        //входной слой
        Layer inputLayer = new Layer(networkInputSize, Activation.Identity);
        layers.add(inputLayer);

        Layer precedingLayer = inputLayer;

        for (int i = 0; i < nb.layers.size(); i++) {
            Layer layer = nb.layers.get(i);
            Matrix w = new Matrix(precedingLayer.size(), layer.size());
            nb.initializer.initWeights(w, i);
            layer.setWeights(w);
            layer.setOptimizer(optimizer.copy());
            layer.setL2(l2);
            layer.setPrecedingLayer(precedingLayer);
            layers.add(layer);

            precedingLayer = layer;
        }
    }


    /**
     * Расчитывает входящий вектор и получает результат,
     * без расчета потерь, обновления весов итп.
     */
    public Result evaluate(Vec input) {
        return evaluate(input, null);
    }


    /**
     * Расчитывает входящий вектор и получает результат сети.
     * Если <code>expected</code> это ожидаемый результат
     * то сеть обучиться
     */
    public Result evaluate(Vec input, Vec expected) {
        Vec signal = input;
        for (Layer layer : layers) {
            signal = layer.evaluate(signal);
        }

        if (expected != null) {
            learnFrom(expected);
            double cost = costFunction.getTotal(expected, signal);
            return new Result(signal, cost);
        }

        return new Result(signal);
    }


    /**
     * Сеть обучиться в зависимости <code>expected</code> от ожидаемого вектора
     * и насколько разным будут ожидаемый и предсказания сети.
     * Эта разность (ошибка) пройдет через обратное распространение.
     * Для достижения обучения сети надо использовать мини-batch - i.e.
     * <code>learnFrom</code> не изменит любые веса,
     * Необходимо использовать <code>updateFromLearning()</code> для обновления весов.
     */
    private void learnFrom(Vec expected) {
        Layer layer = getLastLayer();

        // ошибка по функции потерь
        Vec dCdO = costFunction.getDerivative(expected, layer.getOut());

        // итерируем от последнего слоя до входного
        do {
            Vec dCdI = layer.getActivation().dCdI(layer.getOut(), dCdO);
            Matrix dCdW = dCdI.getMatrixByMultiplyValues(layer.getPrecedingLayer().getOut());

            // Сохраняет разность весов и смещений
            layer.addDeltaWeightsAndBiases(dCdW, dCdI);

            // Готовит ошибку для следующего распространения
            dCdO = layer.getWeights().multiply(dCdI);

            layer = layer.getPrecedingLayer();
        }
        while (layer.hasPrecedingLayer());     // Останавливаемся когда доходим до входного слоя
    }


    /**
     * Обновляет веса и смещения разностей
     * собранных через рассчеты и обучения.
     */
    public synchronized void updateFromLearning() {
        for (Layer l : layers)
            if (l.hasPrecedingLayer()) {
                l.updateWeightsAndBias();
            }


    }

    public List<Layer> getLayers() {
        return layers;
    }

    public void saveModelToJson(String path, String name) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(new File(path + name + ".json"), this);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private Layer getLastLayer() {
        return layers.get(layers.size() - 1);
    }

    /**
     * Простой билдер для сети
     */
    public static class Builder {

        private final List<Layer> layers = new ArrayList<>();
        private final int networkInputSize;

        private Initializer initializer = new Initializer.Random(-0.5, 0.5);
        private CostFunction costFunction = new Quadratic();
        private Optimizer optimizer = new GradientDescent(0.005);
        private double l2 = 0;

        public Builder(int networkInputSize) {
            this.networkInputSize = networkInputSize;
        }

        /**
         * Создает билдер из существующей сети
         */
        public Builder(NeuralNetwork other) {
            networkInputSize = other.networkInputSize;
            costFunction = other.costFunction;
            optimizer = other.optimizer;
            l2 = other.l2;

            List<Layer> otherLayers = other.getLayers();
            for (int i = 1; i < otherLayers.size(); i++) {
                Layer otherLayer = otherLayers.get(i);
                layers.add(
                        new Layer(
                                otherLayer.size(),
                                otherLayer.getActivation(),
                                otherLayer.getBias()
                        )
                );
            }

            initializer = (weights, layer) -> {
                Layer otherLayer = otherLayers.get(layer + 1);
                Matrix otherLayerWeights = otherLayer.getWeights();
                weights.fillFrom(otherLayerWeights);
            };
        }

        public Builder initWeights(Initializer initializer) {
            this.initializer = initializer;
            return this;
        }

        public Builder setCostFunction(CostFunction costFunction) {
            this.costFunction = costFunction;
            return this;
        }

        public Builder setOptimizer(Optimizer optimizer) {
            this.optimizer = optimizer;
            return this;
        }

        public Builder l2(double l2) {
            this.l2 = l2;
            return this;
        }

        public Builder addLayer(Layer layer) {
            layers.add(layer);
            return this;
        }

        public NeuralNetwork create() {
            return new NeuralNetwork(this);
        }
        public NeuralNetwork load(String path){
            ObjectMapper mapper = new ObjectMapper();
            try(InputStream is = new FileInputStream(path)) {
                return mapper.readValue(is, NeuralNetwork.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public static class NetworkState {
        String costFunction;
        Layer.LayerState[] layers;

        public NetworkState(NeuralNetwork network) {
            costFunction = network.costFunction.getName();
            layers = new Layer.LayerState[network.layers.size()];
            for (int l = 0; l < network.layers.size(); l++) {
                layers[l] = network.layers.get(l).getState();
            }
        }

        public Layer.LayerState[] getLayers() {
            return layers;
        }
    }
}


