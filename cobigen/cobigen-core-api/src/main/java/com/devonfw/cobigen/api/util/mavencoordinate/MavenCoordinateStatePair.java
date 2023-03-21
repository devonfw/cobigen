package com.devonfw.cobigen.api.util.mavencoordinate;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.javatuples.Pair;

/**
 * The wrapper class MavenCoordinatePair that wraps a pair of {@linkplain MavenCoordinateState MavenCoordinateStates}
 * and provides extensive utility to interact with the data structure.
 *
 */
public class MavenCoordinateStatePair implements Iterable<MavenCoordinateState> {

  /**
   * The data holder for a given pair of {@linkplain MavenCoordinateState MavenCoordinateStates}
   */
  private Pair<MavenCoordinateState, MavenCoordinateState> pair;

  /**
   * Initialize a MavenCoordinatePair with the given values. Ensures that the {@linkplain MavenCoordinateState} with a
   * truly {@linkplain MavenCoordinateState#isSource isSource} flag is the first element of the pair.
   *
   * @param sourcesJar any MavenCoordinateState
   * @param classesJar any MavenCoordinateState
   */
  public MavenCoordinateStatePair(MavenCoordinateState sourcesJar, MavenCoordinateState classesJar) {

    if (!sourcesJar.isSource() && classesJar.isSource()) {
      this.pair = Pair.with(classesJar, sourcesJar);
      return;
    }
    this.pair = Pair.with(sourcesJar, classesJar);
  }

  /**
   * @return the first value of a pair
   */
  public MavenCoordinateState getSourcesJar() {

    return this.pair.getValue0();
  }

  /**
   * @return the second value of a pair
   */
  public MavenCoordinateState getClassesJar() {

    return this.pair.getValue1();
  }

  /**
   * @return verifies that the tuple in question consist of a non sources and sources jar
   */
  public boolean isValidJarAndSourcesJarPair() {

    return !getSourcesJar().isSource() && getClassesJar().isSource() || getSourcesJar().isSource() && !getClassesJar().isSource();
  }

  /**
   * @param pairs a list of MavenCoordinatePairs
   * @return returns a flattened list of all the individual MavenCoordinateState objects
   */
  public static List<MavenCoordinateState> flattenPairs(List<MavenCoordinateStatePair> pairs) {

    return pairs.stream().flatMap(pair -> Stream.of(pair.getSourcesJar(), pair.getClassesJar())).collect(Collectors.toList());
  }

  @Override
  public Iterator<MavenCoordinateState> iterator() {

    return new Iterator<>() {

      private int index = 0;

      @Override
      public boolean hasNext() {

        return this.index < 2;
      }

      @Override
      public MavenCoordinateState next() {

        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        return this.index++ == 0 ? getSourcesJar() : getClassesJar();
      }
    };
  }

  @Override
  public int hashCode() {

    int value0_hash = getSourcesJar().hashCode();
    int value1_hash = getClassesJar().hashCode();
    return Objects.hash(value0_hash, value1_hash);
  }

  @Override
  public boolean equals(Object obj) {

    if (this == obj) {
      return true;
    }
    if (!(obj instanceof MavenCoordinateStatePair)) {
      return false;
    }
    MavenCoordinateStatePair other = (MavenCoordinateStatePair) obj;

    return getSourcesJar().equals(other.getSourcesJar()) && getClassesJar().equals(other.getClassesJar());
  }

}
