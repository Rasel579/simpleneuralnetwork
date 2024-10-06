package math;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.DoubleStream;

public class Vec implements Serializable {
    @JsonProperty("data")
    private double[] data;
    public Vec() {
    }

    public Vec(double... data) {
        this.data = data;
    }

    public Vec(double[][] data) {

        this.data = Arrays.stream(data).flatMapToDouble(DoubleStream::of)
                .toArray();
    }

    public Vec(int... data) {
        this(Arrays.stream(data).asDoubleStream().toArray());
    }

    public Vec(int size) {
        data = new double[size];
    }

    /**
     * Возвращает размер
     */
    public int dimension() {
        return data.length;
    }

    /**
     * Перемножает элементы вектора и возвращает сумму
     */
    public double multiplyVec(Vec u) {
        assertCorrectDimension(u.dimension());

        double sum = 0;
        for (int i = 0; i < data.length; i++)
            sum += data[i] * u.data[i];

        return sum;
    }

    /**
     * Применяет функцию ко всем элементам вектора
     *
     * @param fn
     * @return
     */
    public Vec map(Function fn) {
        double[] result = new double[data.length];
        for (int i = 0; i < data.length; i++)
            result[i] = fn.apply(data[i]);
        return new Vec(result);
    }

    public double[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Vec{" + "data=" + Arrays.toString(data) + '}';
    }

    /**
     * Вычетание векторов
     */
    public Vec subtractVectorByValue(Vec u) {
        assertCorrectDimension(u.dimension());

        double[] result = new double[u.dimension()];

        for (int i = 0; i < data.length; i++)
            result[i] = data[i] - u.data[i];

        return new Vec(result);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vec vec = (Vec) o;

        return Arrays.equals(data, vec.data);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

    /**
     * Умножает все элементы вектора на значение
     * @param
     * @return
     */
    public Vec multiplyByValue(double s) {
        return map(value -> s * value);
    }

    /**
     * Получить матрицу кол-вом строк вход вектора и столбцов текущего
     * @param u
     * @return
     */
    public Matrix getMatrixByMultiplyValues(Vec u) {
        double[][] result = new double[u.dimension()][dimension()];

        for (int i = 0; i < data.length; i++)
            for (int j = 0; j < u.data.length; j++)
                result[j][i] = data[i] * u.data[j];

        return new Matrix(result);
    }

    /**
     * умножение векторов
     */
    public Vec addVecByMultiplyValues(Vec u) {
        assertCorrectDimension(u.dimension());

        double[] result = new double[u.dimension()];

        for (int i = 0; i < data.length; i++)
            result[i] = data[i] * u.data[i];

        return new Vec(result);
    }

    /**
     * Сложение векторов
     */
    public Vec addVecBySumValues(Vec u) {
        assertCorrectDimension(u.dimension());

        double[] result = new double[u.dimension()];

        for (int i = 0; i < data.length; i++)
            result[i] = data[i] + u.data[i];

        return new Vec(result);
    }

    /**
     * Перемножает вектор на элементы матрицы
     */
    public Vec multiplyByValue(Matrix m) {
        assertCorrectDimension(m.rows());

        double[][] mData = m.getData();
        double[] result = new double[m.cols()];

        for (int col = 0; col < m.cols(); col++) {
            for (int row = 0; row < m.rows(); row++) {
                result[col] += mData[row][col] * data[row];
            }
        }


        return new Vec(result);
    }


    private void assertCorrectDimension(int inpDim) {
        if (dimension() != inpDim)
            throw new IllegalArgumentException(String.format("Разные размерности векторов: Input is %d, Vec is %d", inpDim, dimension()));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public double maxValue() {
        return DoubleStream.of(data).max().getAsDouble();
    }

    public Vec subtractVectorByValue(double a) {
        double[] result = new double[dimension()];

        for (int i = 0; i < data.length; i++)
            result[i] = data[i] - a;

        return new Vec(result);
    }

    public double sumElements() {
        return DoubleStream.of(data).sum();
    }
}
