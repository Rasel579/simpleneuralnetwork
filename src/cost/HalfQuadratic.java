package cost;

import math.Vec;

import java.io.Serializable;

/**
 * Функции потерь: Полуквадратичная ошибка, C = 0.5 ∑(y−exp)^2
 */
public class HalfQuadratic implements CostFunction, Serializable {
    @Override
    public String getName() {
        return "HalfQuadratic";
    }

    @Override
    public double getTotal(Vec expected, Vec actual) {
        Vec diff = expected.subtractVectorByValue(actual);
        return diff.multiplyVec(diff) * 0.5;
    }

    @Override
    public Vec getDerivative(Vec expected, Vec actual) {
        return actual.subtractVectorByValue(expected);
    }
}
