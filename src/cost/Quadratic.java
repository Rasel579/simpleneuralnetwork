package cost;

import math.Vec;

import java.io.Serializable;

/**
 * Функции потерь: Квадратичная ошибка, C = ∑(y−exp)^2
 */
public class Quadratic implements CostFunction, Serializable {

    public Quadratic(){
    }
    @Override
    public String getName() {
        return "Quadratic";
    }

    @Override
    public double getTotal(Vec expected, Vec actual) {
        Vec diff = actual.subtractVectorByValue(expected);
        return diff.multiplyVec(diff);
    }

    @Override
    public Vec getDerivative(Vec expected, Vec actual) {
        return actual.subtractVectorByValue(expected).multiplyByValue(2);
    }
}
