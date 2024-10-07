package math;

import utils.ImageUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.StringJoiner;

import static java.lang.String.format;
import static java.lang.System.arraycopy;
import static java.util.Arrays.stream;

public class Matrix implements Serializable {
    private double[][] data;
    private int rows;
    private int cols;

    public Matrix(){
    }

    public Matrix(double[][] data) {
        this.data = data;
        rows = data.length;
        cols = data[0].length;
    }

    /**
     * Создаем матрицу из чернобелого изображения, для цветного понадобится тензор
     */
    public Matrix(File img) throws IOException {
        this.data = ImageUtils.convertToMatrix(img);
        rows = data.length;
        cols = data[0].length;
    }

    /**
     * Нормализует матрицу изображения к 0-1
     */
    public void normalize() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                data[i][j] = data[i][j] / 255;
            }
        }
    }

    public Matrix(int rows, int cols) {
        this(new double[rows][cols]);
    }

    public Vec multiply(Vec v) {
        double[] out = new double[rows];
        for (int y = 0; y < rows; y++)
            out[y] = new Vec(data[y]).multiplyVec(v);

        return new Vec(out);
    }

    /**
     * Применение выбранной функции к элементам матрицы
     */
    public Matrix map(Function fn) {
        for (int y = 0; y < rows; y++)
            for (int x = 0; x < cols; x++)
                data[y][x] = fn.apply(data[y][x]);

        return this;
    }

    public int rows() {
        return rows;
    }

    public int cols() {
        return cols;
    }

    /**
     * Умножение матрицы на скаляр
     *
     * @param s
     * @return
     */
    public Matrix mul(double s) {
        return map(value -> s * value);
    }

    public double[][] getData() {
        return data;
    }

    /**
     * Сложение матриц
     */
    public Matrix add(Matrix other) {
        assertCorrectDimension(other);

        for (int y = 0; y < rows; y++)
            for (int x = 0; x < cols; x++)
                data[y][x] += other.data[y][x];

        return this;
    }

    /**
     * Вычетание матриц
     */
    public Matrix sub(Matrix other) {
        assertCorrectDimension(other);

        for (int y = 0; y < rows; y++)
            for (int x = 0; x < cols; x++)
                data[y][x] -= other.data[y][x];

        return this;
    }

    public Matrix fillFrom(Matrix other) {
        assertCorrectDimension(other);

        for (int y = 0; y < rows; y++) {
            if (cols >= 0) {
                arraycopy(other.data[y], 0, data[y], 0, cols);
            }
        }


        return this;
    }

    public double average() {
        return stream(data).flatMapToDouble(Arrays::stream).average().getAsDouble();
    }

    public double variance() {
        double avg = average();
        return stream(data).flatMapToDouble(Arrays::stream).map(a -> (a - avg) * (a - avg)).average().getAsDouble();
    }

    private void assertCorrectDimension(Matrix other) {
        if (rows != other.rows || cols != other.cols)
            throw new IllegalArgumentException(format("Матрица разной размерности: Input is %d x %d, Vec is %d x %d", rows, cols, other.rows, other.cols));
    }

    public Matrix copy() {
        Matrix m = new Matrix(rows, cols);
        for (int y = 0; y < rows; y++)
            if (cols >= 0) arraycopy(data[y], 0, m.data[y], 0, cols);

        return m;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Matrix.class.getSimpleName() + "[", "]")
                .add("data=" + Arrays.deepToString(data))
                .toString();
    }
}
