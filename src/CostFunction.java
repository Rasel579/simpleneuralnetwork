import math.Vec;

public interface CostFunction {

    String getName();

    double getTotal(Vec expected, Vec actual);

    Vec getDerivative(Vec expected, Vec actual);


    // --------------------------------------------------------------

    /**
     * Cost function: Mean square error, C = 1/n * ∑(y−exp)^2
     */
    class MSE implements CostFunction {
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

    /**
     * Cost function: Quadratic, C = ∑(y−exp)^2
     */
    class Quadratic implements CostFunction {
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

    /**
     * Cost function: HalfQuadratic, C = 0.5 ∑(y−exp)^2
     */
    class HalfQuadratic implements CostFunction {
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
}
