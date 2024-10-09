package cost;

import math.Vec;

import java.io.Serializable;

/**
 * Функции потерь: Среднеквадратичная ошибка, C = 1/n * ∑(y−exp)^2
 */
public class MSE implements CostFunction, Serializable {

    public MSE() {}
    @Override
    public String getName() {
        return "MSE";
    }

    @Override
    public double getTotal(Vec expected, Vec actual) {
        Vec diff = expected.subtractVectorByValue(actual);
        return diff.multiplyVec(diff) / actual.dimension();
    }

    @Override
    public Vec getDerivative(Vec expected, Vec actual) {
        return actual.subtractVectorByValue(expected).multiplyByValue(2.0 / actual.dimension());
    }
}
