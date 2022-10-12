package com.devonfw.cobigen.api.matchers;

import java.util.Objects;

import org.hamcrest.Matcher;
import org.mockito.ArgumentMatcher;

import com.devonfw.cobigen.api.to.MatcherTo;

/**
 * Hamcrest Matcher for {@link MatcherTo}s
 */
public class MatcherToMatcher implements ArgumentMatcher<MatcherTo> {

  /**
   * The matchers type to be matched
   */
  private Matcher<String> type;

  /**
   * The value to match against to be matched
   */
  private Matcher<String> value;

  /**
   * The target object to be matched to be matched
   */
  private Matcher<Object> target;

  /**
   * Creates a matcher, which matches all {@link MatcherTo}s with the given attributes
   *
   * @param type to be matched
   * @param value to be matched
   * @param target to be matched
   */
  public MatcherToMatcher(Matcher<String> type, Matcher<String> value, Matcher<Object> target) {

    this.type = type;
    this.value = value;
    this.target = target;
  }

  @Override
  public boolean matches(MatcherTo item) {

    return item != null && this.type != null && this.type.matches(item.getType()) && this.value != null
        && this.value.matches(item.getValue()) && this.target != null && this.target.matches(item.getTarget());
  }

  @Override
  public int hashCode() {

    return Objects.hash(this.target, this.type, this.value);
  }

  @Override
  public boolean equals(Object obj) {

    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    MatcherToMatcher other = (MatcherToMatcher) obj;
    return Objects.equals(this.target, other.target) && Objects.equals(this.type, other.type)
        && Objects.equals(this.value, other.value);
  }

}
