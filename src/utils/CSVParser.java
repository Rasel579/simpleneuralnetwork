package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CSVParser {
    private String path;
    private String separator;
    private List<List<String>> metrics;
    private Set<String> mainMetrics;

    public CSVParser(String path, String separator) {
        this.path = path;
        this.separator = separator;
        this.metrics = getMetrics();
        this.mainMetrics = getMainMetrics();
    }

    private List<List<String>> getMetrics() {
        List<List<String>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(this.path))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(this.separator);
                records.add(Arrays.asList(values));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return records;
    }

    private Set<String> getMainMetrics() {
        Set<String> mainMetric = new HashSet<>();
        metrics.forEach(metric -> mainMetric.add(metric.get(metric.size() - 1)));
        return mainMetric;
    }

    public List<double[][]> getConvertedData() {
        List<double[][]> data = new ArrayList<>();
        for (List<String> metric : metrics) {
            if (metric == metrics.get(0)) {
                continue;
            }
            double[] row = new double[metric.size() - 1];
            double[] main = new double[mainMetrics.size()];
            for (int i = 0; i < metric.size() - 2; i++) {
                row[i] = Double.parseDouble(metric.get(i));
            }

            for (int i = 0; i < mainMetrics.size(); i++) {
                if (mainMetrics.toArray()[i].equals(metric.get(metric.size() - 1))) {
                    main[i] = 1;
                    continue;
                }
                main[i] = 0;
            }
            data.add(new double[][]{row, main});
        }

        return data;
    }

    public Set<String> getAllMainMetrics(){
        return mainMetrics;
    }

}
