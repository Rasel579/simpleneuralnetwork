import math.Vec;
import optimizer.GradientDescent;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.DoubleStream;

import static java.util.Arrays.stream;
import static java.util.Collections.unmodifiableList;

public class Main {

    private static final int BATCH_SIZE = 32;
    private static final double[][] Y = {{1, 0, 1},
            {0, 1, 0},
            {0, 1, 0},
            {0, 0, 0}
    };

    private static final double[][] X = {{1, 0, 1},
            {0, 1, 0},
            {1, 0, 1},
            {0, 0, 0}
    };

    private static final double[][] I = {{0, 1, 0},
            {0, 1, 0},
            {0, 1, 0},
            {0, 0, 0}
    };

    private static final double[][] L = {{1, 0, 0},
            {1, 0, 0},
            {1, 1, 1},
            {0, 0, 0}
    };

    public static void main(String[] args) {

        List<double[][]> datas = List.of(Y, X, I, L);

        datas.forEach(data -> {
            for (double[] datum : data) {
                for (double v : datum) {
                    System.out.print(v + "\t");
                }
                System.out.println();
            }
            System.out.println();
            System.out.println(stream(data).flatMapToDouble(DoubleStream::of).toString());

        });

        NeuralNetwork network = new NeuralNetwork.Builder(12)
                .addLayer(new Layer(38, Activation.Leaky_ReLU))
                .addLayer(new Layer(12, Activation.Leaky_ReLU))
                .addLayer(new Layer(4, Activation.Softmax))
                .initWeights(new Initializer.XavierNormal())
                .setCostFunction(new CostFunction.Quadratic())
                .setOptimizer(new GradientDescent(0.05))
                .create();


        learn(datas, network);

        Result result = network.evaluate(new Vec(L));
        System.out.println(result.getOutput());
        System.out.println(DataUtil.findByMaxIndx(result.getOutput().indexOfLargestElement()).label);

        result = network.evaluate(new Vec(X));
        System.out.println(result.getOutput());
        System.out.println(DataUtil.findByMaxIndx(result.getOutput().indexOfLargestElement()).label);
    }

    private static List<double[][]> getBatch(int i, List<double[][]> data) {
        int fromIx = i * BATCH_SIZE;
        int toIx = Math.min(data.size(), (i + 1) * BATCH_SIZE);
        return unmodifiableList(data.subList(fromIx, toIx));
    }

    private static int applyDataToNet(List<double[][]> data, NeuralNetwork network, boolean learn) {
        final AtomicInteger correct = new AtomicInteger();

        for (int i = 0; i <= data.size() / BATCH_SIZE; i++) {

            getBatch(i, data).forEach(chared -> {
                Vec input = new Vec(chared);
                Vec define = new Vec(DataUtil.UNDEFINED.array);

                if (chared == Y) {
                    define = new Vec(DataUtil.Y_ALPHABET.array);
                }

                if (chared == I) {
                    define = new Vec(DataUtil.I_ALPHABET.array);
                }

                if (chared == X) {
                    define = new Vec(DataUtil.X_ALPHABET.array);
                }

                if (chared == L) {
                    define = new Vec(DataUtil.L_ALPHABET.array);
                }

                Result result = network.evaluate(input, define);


                if (result.getOutput().indexOfLargestElement() == define.indexOfLargestElement()) {
                    correct.incrementAndGet();
                }

            });

            network.updateFromLearning();
        }

        return correct.get();
    }

    private static void learn(List<double[][]> trainData, NeuralNetwork network) {
        boolean shoodStop = false;
        int epoch = 0;
        double errorRateOnTrainDS = -1;

        while (!shoodStop) {
            epoch++;

            int correctTrainDS = applyDataToNet(trainData, network, true);
            errorRateOnTrainDS = 100 - (100.0 * correctTrainDS / trainData.size());
            shoodStop = 0.1 > errorRateOnTrainDS;
        }
        System.out.println("count epoch: " + epoch);
        System.out.println("error final: " + errorRateOnTrainDS);
    }
}