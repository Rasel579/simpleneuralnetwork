public enum DataUtil {

    Y_ALPHABET(new double[]{1, 0, 0, 0}, "Y"),
    X_ALPHABET(new double[]{0, 1, 0, 0}, "X"),
    I_ALPHABET(new double[]{0, 0, 1, 0}, "I"),
    L_ALPHABET(new double[]{0, 0, 0, 1}, "L"),
    UNDEFINED(new double[]{0, 0, 0, 0}, "Undefined");

    public final double[] array;
    public final String label;

    DataUtil(double[] array, String description) {
        this.array = array;
        this.label = description;
    }

    public static DataUtil findByMaxIndx( int maxIndex ) {
        for (DataUtil val : values()) {
            if (val.array[maxIndex] == 1) {
                return val;
            }
        }

        return DataUtil.UNDEFINED;
    }
}
