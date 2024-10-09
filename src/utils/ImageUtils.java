package utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ImageUtils {
    private static final int TARGET_WIDTH = 34;
    private static final int TARGET_HEIGHT = 34;

    /**
     * Получаем тренировочную выборку выборку
     */
    public static Map<String, File[]> getTrainData(String pathData){
        Map<String, File[]> trainData = new HashMap<>();
        File rootDirectory = new File(pathData);

        if ( rootDirectory.exists()){
            for ( File labelDirectory: rootDirectory.listFiles() ){
                if ( labelDirectory.isDirectory()){
                    trainData.put(labelDirectory.getName(), labelDirectory.listFiles());
                }
            }
        }

        return  trainData;
    }

    /**
     * Приводим к одному размеру картинки
     */
    public static BufferedImage resizeImg(BufferedImage read) {
        Image resultingImage = read.getScaledInstance(TARGET_WIDTH, TARGET_HEIGHT, Image.SCALE_DEFAULT);
        BufferedImage outputImage = new BufferedImage(TARGET_WIDTH, TARGET_HEIGHT, BufferedImage.TYPE_INT_RGB);
        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
        return outputImage;
    }

    /**
     * Переводит чернобелое изображение в матрицу, для цветного понадобится тензор
     */
    public static double[][] convertToMatrix(File img) throws IOException {
        BufferedImage bufImg = ImageUtils.resizeImg(ImageIO.read(img));
        int width = bufImg.getWidth(null);
        int height = bufImg.getHeight(null);
        double[][] pixels = new double[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Color temp = new Color(bufImg.getRGB(i, j));
                pixels[i][j] = temp.getRed(); //ориентация на красный канал
            }
        }
        return pixels;
    }

    /**
     * Получает вектор из целевой метрики
     */
    public static double[] getVecFromData(Set<String> data, String expect){
        double[] result = new double[data.size()];
        int i = 0;
        for ( String keyData: data ){
            if (keyData.equals(expect)){
                result[i] = 1;
            } else {
                result[i] = 0;
            }
            i++;
        }
        return result;
    }
}
