package image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ImageUtils {
    private static final String PATH = "./images";
    private static final int TARGET_WIDTH = 255;
    private static final int TARGET_HEIGHT = 255;

    /**
     * Получаем тренировочную выборку выборку
     */
    public static Map<String, File[]> getTrainData(){
        Map<String, File[]> trainData = new HashMap<>();
        File rootDirectory = new File(PATH);

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
                pixels[i][j] = bufImg.getRGB(i, j);
            }
        }
        return pixels;
    }
}
