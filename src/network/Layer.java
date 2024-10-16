package network;

import com.fasterxml.jackson.annotation.JsonProperty;
import math.Matrix;
import math.Vec;
import optimizer.Optimizer;

import java.io.Serializable;

/**
 * Слой в нейросети
 * Содержит веса и смещения
 */
public class Layer implements Serializable {

    @JsonProperty("size")
    private int size;
    @JsonProperty("out")
    private Vec out = new Vec();
    @JsonProperty("activation")
    private Activation activation;
    @JsonProperty("optimizer")
    private Optimizer optimizer;
    @JsonProperty("weights")
    private Matrix weights;
    @JsonProperty("bias")
    private Vec bias;
    @JsonProperty("l2")
    private double l2 = 0;
    @JsonProperty("precedingLayer")
    private Layer precedingLayer; //предшествующий слой
    @JsonProperty("deltaWeights")
    private transient Matrix deltaWeights; //разность весов
    @JsonProperty("deltaBias")
    private transient Vec deltaBias; //разность смещений
    @JsonProperty("deltaWeightsAdded")
    private transient int deltaWeightsAdded = 0; //кол-во весов добавлено
    @JsonProperty("deltaBiasAdded")
    private transient int deltaBiasAdded = 0;  //кол-во смещений добавлено
    public Layer(){
    }
    public Layer(int size, Activation activation) {
        this(size, activation, 0);
    }

    public Layer(int size, Activation activation, double initialBias) {
        this.size = size;
        bias = new Vec(size).map(x -> initialBias);
        deltaBias = new Vec(size);
        this.activation = activation;
    }

    public Layer(int size, Activation activation, Vec bias) {
        this.size = size;
        this.bias = bias;
        deltaBias = new Vec(size);
        this.activation = activation;
    }

    public int size() {
        return size;
    }

    /**
     * Принимает входящий вектор, i, через слой.
     * Сохраняет копию выходящего вектора
     *
     * @param i Входяший вектор
     * @return Вызодящий вектор out (i.e. результат out = iW + b)
     */
    public Vec evaluate(Vec i) {
        if (!hasPrecedingLayer()) {
            out = i;    // Не расчитывает, просто сохраняет
        } else {
            out = activation.fn(i.multiplyByValue(weights).addVecBySumValues(bias));
        }
        return out;
    }

    public Vec getOut() {
        return out;
    }

    public Activation getActivation() {
        return activation;
    }

    public void setWeights(Matrix weights) {
        this.weights = weights;
        deltaWeights = new Matrix(weights.rows(), weights.cols());
    }

    public void setOptimizer(Optimizer optimizer) {
        this.optimizer = optimizer;
    }

    public void setL2(double l2) {
        this.l2 = l2;
    }

    public Matrix getWeights() {
        return weights;
    }

    public Layer getPrecedingLayer() {
        return precedingLayer;
    }

    public void setPrecedingLayer(Layer precedingLayer) {
        this.precedingLayer = precedingLayer;
    }

    public boolean hasPrecedingLayer() {
        return precedingLayer != null;
    }

    public Vec getBias() {
        return bias;
    }

    /**
     * Добавляет входящие изменения весов и смещений
     * Не озночает, что нейросеть изменилась
     */
    public synchronized void addDeltaWeightsAndBiases(Matrix dW, Vec dB) {
        deltaWeights.add(dW);
        deltaWeightsAdded++;
        deltaBias = deltaBias.addVecBySumValues(dB);
        deltaBiasAdded++;
    }

    /**
     * Берет все усредненные добавленные веса и смещения и передает
     * оптимизатору применить для текущих весов и смещений
     * <p>
     * Также принимает L2 регуляризацию к весам, если есть
     */
    public synchronized void updateWeightsAndBias() {
        if (deltaWeightsAdded > 0) {
            if (l2 > 0) {
                weights.map(value -> value - l2 * value);
            }

            Matrix averagedW = deltaWeights.mul(1.0 / deltaWeightsAdded);
            optimizer.updateWeights(weights, averagedW);
            deltaWeights.map(a -> 0);   // Зачищаем
            deltaWeightsAdded = 0;
        }

        if (deltaBiasAdded > 0) {
            Vec average_bias = deltaBias.multiplyByValue(1.0 / deltaBiasAdded);
            bias = optimizer.updateBias(bias, average_bias);
            deltaBias = deltaBias.map(a -> 0);  // Зачищаем
            deltaBiasAdded = 0;
        }
    }

    public LayerState getState() {
        return new LayerState(this);
    }

    public static class LayerState {

        double[][] weights;
        double[] bias;
        String activation;

        public LayerState(Layer layer) {
            weights = layer.getWeights() != null ? layer.getWeights().getData() : null;
            bias = layer.getBias().getData();
            activation = layer.activation.getName();
        }

        public double[][] getWeights() {
            return weights;
        }

        public double[] getBias() {
            return bias;
        }
    }
}

