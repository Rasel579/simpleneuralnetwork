import math.Matrix;
import math.Vec;
import optimizer.GradientDescent;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.

        NeuralNetwork network = new NeuralNetwork.Builder(12)
                .addLayer(new Layer(38, Activation.Leaky_ReLU))
                .addLayer(new Layer(12, Activation.Leaky_ReLU))
                .addLayer(new Layer(1, Activation.Softmax))
                .initWeights(new Initializer.XavierNormal())
                .setCostFunction(new CostFunction.Quadratic())
                .setOptimizer(new GradientDescent(0.05))
                .create();

        double[][] l = {{1, 0, 1},
                        {0, 1, 0},
                        {0, 1, 0},
                        {0, 0, 0}
                };

        for (int i = 0; i < l.length; i++ ){
            for (int j = 0; j < l[i].length; j++ ){
                System.out.print(l[i][j] + "\t");
            }
            System.out.println();
        }
        System.out.println(l);
        Vec vec = new Vec(l);
        System.out.println(vec.toString());

        Result result = network.evaluate(new Vec(l), new Vec(1));

        System.out.println(result.getOutput());
    }
}