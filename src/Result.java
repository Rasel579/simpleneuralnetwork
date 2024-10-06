import math.Vec;

import java.util.Set;

public class Result {
    private final Vec output;
    private final Double cost;

    public Result(Vec output) {
        this.output = output;
        cost = null;
    }

    public Result(Vec output, double cost) {
        this.output = output;
        this.cost = cost;
    }

    public Vec getOutput() {
        return output;
    }

    public String getResult(Set<String> data) {
        int idx = 0;
        double maxVal = 0;
        for (int i = 0; i < output.getData().length; i++) {
            if ( output.getData()[i] > maxVal){
                maxVal = output.getData()[i];
                idx = i;
            }
        }

        return (String) data.toArray()[idx];
    }

    public Double getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return "Result{" + "output=" + output +
                ", cost=" + cost +
                '}';
    }
}
