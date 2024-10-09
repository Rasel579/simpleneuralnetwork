package trains;

import cost.Quadratic;
import math.Matrix;
import math.Vec;
import network.*;
import optimizer.GradientDescent;
import utils.DataUtil;
import utils.ImageUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ImagesTrains {

    private static final int BATCH_SIZE = 2;
    private static final String SAVE_PATH = "./models/";

    public static NeuralNetwork train() {
        Map<String, File[]> trainedData = ImageUtils.getTrainData("./trainData/alphabetset");
        Result result;

        NeuralNetwork network = new NeuralNetwork.Builder(1156)
                .addLayer(new Layer(38, Activation.Leaky_ReLU))
                .addLayer(new Layer(12, Activation.Leaky_ReLU))
                .addLayer(new Layer(26, Activation.Softmax))
                .initWeights(new Initializer.XavierNormal())
                .setCostFunction(new Quadratic())
                .setOptimizer(new GradientDescent(0.005))
                .addTitles(trainedData.keySet())
                .create();

        learn(trainedData, network);

        try {
            result = network.evaluate(new Vec(ImageUtils.convertToMatrix(trainedData.get("X")[1])));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println(result.getResult(trainedData.keySet()));
        try {
            result = network.evaluate(new Vec(ImageUtils.convertToMatrix(trainedData.get("C")[1])));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(result.getResult(trainedData.keySet()));

        return network;
    }

    private static List<double[][]> getBatch(int i, List<double[][]> data) {
        int fromIx = i * BATCH_SIZE;
        int toIx = Math.min(data.size(), (i + 1) * BATCH_SIZE);
        return Collections.unmodifiableList(data.subList(fromIx, toIx));
    }

    private static int applyDataToNet(Map<String, File[]> dataSet, NeuralNetwork network) {
        final AtomicInteger correct = new AtomicInteger();

        for (String letter : dataSet.keySet()) {
            List<double[][]> data = Arrays.stream(dataSet.get(letter)).map(file -> {
                try {
                    Matrix mat = new Matrix(ImageUtils.convertToMatrix(file));
                    mat.normalize();
                    return mat.getData();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).toList();

            for (int i = 0; i <= data.size() / BATCH_SIZE; i++) {

                getBatch(i, data).forEach(chared -> {

                    Vec input = new Vec(chared);
                    Vec expect = new Vec(ImageUtils.getVecFromData(dataSet.keySet(), letter));
                    Result result = network.evaluate(input, expect);


                    if (DataUtil.maxIndxOfArray(result.getOutput().getData()) == DataUtil.maxIndxOfArray(expect.getData())) {
                        correct.incrementAndGet();
                    }

                });
                network.updateFromLearning();
            }
        }
        return correct.get();
    }

    private static void learn(Map<String, File[]> trainData, NeuralNetwork network) {
        boolean shoodStop = false;
        int epoch = 0;
        double errorRateOnTrainDS = -1;

        while (!shoodStop) {
            epoch++;

            int correctTrainDS = applyDataToNet(trainData, network);

            errorRateOnTrainDS = 100 - (100.0 * correctTrainDS / trainData.values().stream().flatMap(ab -> Arrays.stream(ab).sequential())
                    .count());
            shoodStop = 1 > errorRateOnTrainDS;
            System.out.println("error: " + errorRateOnTrainDS);
        }
        network.saveModelToJson(SAVE_PATH, "model" + System.currentTimeMillis());
        System.out.println("count epoch: " + epoch);
        System.out.println("error final: " + errorRateOnTrainDS);
    }
}
