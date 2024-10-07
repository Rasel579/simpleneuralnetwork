package utils;

public class DataUtil {
  public static int maxIndxOfArray(double[] arr ){
     double maxVal = 0;
     int indx = 0;
     for (int i = 0; i < arr.length; i++){
         if ( arr[i] > maxVal ){
             maxVal = arr[i];
             indx = i;
         }
     }
     return indx;
  }
}
