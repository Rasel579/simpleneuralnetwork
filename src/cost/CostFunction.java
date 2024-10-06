package cost;

import math.Vec;

/**
 * Функции потерь
 */
public interface CostFunction {
    String getName();
    double getTotal(Vec expected, Vec actual);
    Vec getDerivative(Vec expected, Vec actual);
}
