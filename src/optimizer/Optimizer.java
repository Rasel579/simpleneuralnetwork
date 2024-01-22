package optimizer;

import math.Matrix;
import math.Vec;

public interface Optimizer {

    void updateWeights(Matrix weights, Matrix dCdW);

    Vec updateBias(Vec bias, Vec dCdB);

    Optimizer copy();

}
