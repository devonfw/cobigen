package com.devonfw.cobigen.api.util.mavencoordinate;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

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
   * @param value0 any MavenCoordinateState
   * @param value1 any MavenCoordinateState
   */
  public MavenCoordinateStatePair(MavenCoordinateState value0, MavenCoordinateState value1) {

    if (!value0.isSource() && value1.isSource()) {
      this.pair = Pair.with(value1, value0);
      return;
    }
    this.pair = Pair.with(value0, value1);
  }

  /**
   * @return the first value of a pair
   */
  public MavenCoordinateState getValue0() {

    return this.pair.getValue0();
  }

  /**
   * @return the second value of a pair
   */
  public MavenCoordinateState getValue1() {

    return this.pair.getValue1();
  }

  /**
   * @return verifies that the tuple in question consist of a non sources and sources jar
   */
  public boolean isValidJarAndSourcesJarPair() {

    return !getValue0().isSource() && getValue1().isSource() || getValue0().isSource() && !getValue1().isSource();
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
        return this.index++ == 0 ? getValue0() : getValue1();
      }
    };
  }

  @Override
  public int hashCode() {

    int value0_hash = getValue0().hashCode();
    int value1_hash = getValue1().hashCode();
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

    return getValue0().equals(other.getValue0()) && getValue1().equals(other.getValue1());
  }

}
