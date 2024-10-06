import math.Function;
import math.Vec;

import java.io.Serializable;

import static java.lang.Math.exp;
import static java.lang.Math.log;

public class Activation implements Serializable {

    private String name;
    private Function fn; //функция
    private Function dFn; //производная функции

    public  Activation(){
    }
    public Activation(String name) {
        this.name = name;
    }

    public Activation(String name, Function fn, Function dFn) {
        this.name = name;
        this.fn = fn;
        this.dFn = dFn;
    }

    //применяем функцию к вектору
    public Vec fn(Vec in) {
        return in.map(fn);
    }
    //применяем производную к вектору
    public Vec dFn(Vec out) {
        return out.map(dFn);
    }

    // Вычисляет градиент функции i.e. ∂C/∂I = ∂C/∂O * ∂O/∂I.
    public Vec dCdI(Vec out, Vec dCdO) {
        return dCdO.addVecByMultiplyValues(dFn(out));
    }

    public String getName() {
        return name;
    }



    public static Activation ReLU = new Activation(
            "ReLU",
            x -> x <= 0 ? 0 : x,                // fn
            x -> x <= 0 ? 0 : 1                 // dFn
    );

    public static Activation Leaky_ReLU = new Activation(
            "Leaky_ReLU",
            x -> x <= 0 ? 0.01 * x : x,         // fn
            x -> x <= 0 ? 0.01 : 1              // dFn
    );


    public static Activation Sigmoid = new Activation(
            "Sigmoid",
            Activation::sigmoidFn,                      // fn
            x -> sigmoidFn(x) * (1.0 - sigmoidFn(x))    // dFn
    );


    public static Activation Softplus = new Activation(
            "Softplus",
            x -> log(1.0 + exp(x)),             // fn
            Activation::sigmoidFn               // dFn
    );

    public static Activation Identity = new Activation(
            "Identity",
            x -> x,                             // fn
            x -> 1                              // dFn
    );


    /**
     * Функция Активации SoftMax
     */
    public static Activation Softmax = new Activation("Softmax") {
        @Override
        public Vec fn(Vec in) {
            double[] data = in.getData();
            double sum = 0;
            double max = in.maxValue();
            for (double a : data)
                sum += exp(a - max);

            double finalSum = sum;
            return in.map(a -> exp(a - max) / finalSum);
        }

        @Override
        public Vec dCdI(Vec out, Vec dCdO) {
            double x = out.addVecByMultiplyValues(dCdO).sumElements();
            Vec sub = dCdO.subtractVectorByValue(x);
            return out.addVecByMultiplyValues(sub);
        }
    };

    private static double sigmoidFn(double x) {
        return 1.0 / (1.0 + exp(-x));
    }

}

