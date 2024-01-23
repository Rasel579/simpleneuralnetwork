package optimizer;

import math.Matrix;
import math.Vec;

/**
 * Updates Weights and biases based on a constant learning rate - i.e. W -= η * dC/dW
 */
public record GradientDescent(double learningRate) implements Optimizer {


    @Override
    public void updateWeights(Matrix weights, Matrix dCdW) {
        weights.sub(dCdW.mul(learningRate));
    }

    @Override
    public Vec updateBias(Vec bias, Vec dCdB) {
        return bias.subtractVectorByValue(dCdB.multiplyByValue(learningRate));
    }

    @Override
    public Optimizer copy() {
        return this;
    }
}
