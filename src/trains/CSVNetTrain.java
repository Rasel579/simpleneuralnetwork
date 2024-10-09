package trains;

import cost.Quadratic;
import math.Vec;
import network.*;
import optimizer.GradientDescent;
import utils.CSVParser;
import utils.DataUtil;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CSVNetTrain {
    private static final int BATCH_SIZE = 2;
    private static final String SAVE_PATH = "./models/";

    public static NeuralNetwork train() {
        CSVParser data = new CSVParser("./trainediris/iris_dataset.csv", ",");

        NeuralNetwork network = new NeuralNetwork.Builder(4)
                .addLayer(new Layer(38, Activation.Leaky_ReLU))
                .addLayer(new Layer(12, Activation.Leaky_ReLU))
                .addLayer(new Layer(4, Activation.Softmax))
                .initWeights(new Initializer.XavierNormal())
                .setCostFunction(new Quadratic())
                .setOptimizer(new GradientDescent(0.005))
                .addTitles(data.getAllMainMetrics())
                .create();

        learn(data.getConvertedData(), network);

        Result result = network.evaluate(new Vec(data.getConvertedData().get(5)[0]));
        System.out.println(result.getResult(network.getTitles()));

        result = network.evaluate(new Vec(data.getConvertedData().get(85)[0]));
        System.out.println(result.getResult(network.getTitles()));

        result = network.evaluate(new Vec(data.getConvertedData().get(140)[0]));
        System.out.println(result.getResult(network.getTitles()));
        return network;
    }

    private static List<double[][]> getBatch(int i, List<double[][]> data) {
        int fromIx = i * BATCH_SIZE;
        int toIx = Math.min(data.size(), (i + 1) * BATCH_SIZE);
        return Collections.unmodifiableList(data.subList(fromIx, toIx));
    }

    private static int applyDataToNet(List<double[][]> dataSet, NeuralNetwork network) {
        final AtomicInteger correct = new AtomicInteger();
        for (int i = 0; i <= dataSet.size() / BATCH_SIZE; i++) {

            getBatch(i, dataSet).forEach(chared -> {

                Vec input = new Vec(chared[0]);
                Vec expect = new Vec(chared[1]);
                Result result = network.evaluate(input, expect);

                if (DataUtil.maxIndxOfArray(result.getOutput().getData()) == DataUtil.maxIndxOfArray(expect.getData())) {
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

            int correctTrainDS = applyDataToNet(trainData, network);

            errorRateOnTrainDS = 100 - (100.0 * correctTrainDS / trainData.size());
            shoodStop = 0.7 > errorRateOnTrainDS;
            System.out.println("error: " + errorRateOnTrainDS);
        }
        network.saveModelToJson(SAVE_PATH, "model" + System.currentTimeMillis());
        System.out.println("count epoch: " + epoch);
        System.out.println("error final: " + errorRateOnTrainDS);
    }
}
