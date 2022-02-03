package utils;

import org.apache.commons.math3.distribution.NormalDistribution;

public class MathUtils {

    public double getNormalDistribution() {
      NormalDistribution normalDistribution = new NormalDistribution(10, 3);
      double randomValue = normalDistribution.sample();
      return randomValue;
    }
}
