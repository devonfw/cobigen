package com.devonfw.cobigen.test.matchers;

import static org.hamcrest.core.AllOf.allOf;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsCollectionContaining;

/**
 * These functions have been ported from hamcrest, whereas the signature has been customized
 */
public class CustomHamcrestMatchers {

    /**
     * Creates a matcher for {@link List}s that matches when consecutive passes over the examined {@link List}
     * yield at least one item that is matched by the corresponding matcher from the specified
     * <code>itemMatchers</code>. Whilst matching, each traversal of the examined {@link List} will stop as
     * soon as a matching item is found.
     * <p>
     * For example:
     *
     * <pre>
     * assertThat(Arrays.asList(&quot;foo&quot;, &quot;bar&quot;, &quot;baz&quot;), hasItems(endsWith(&quot;z&quot;), endsWith(&quot;o&quot;)))
     * </pre>
     *
     * @param <T>
     *            Type of items to be matched
     *
     * @param itemMatchers
     *            the matchers to apply to items provided by the examined {@link List}
     * @return the matcher instance
     */
    @Factory
    @SuppressWarnings("unchecked")
    public static <T> Matcher<List<T>> hasItemsInList(Matcher<? super T>... itemMatchers) {
        List<Matcher<? super List<T>>> all = new ArrayList<>(itemMatchers.length);

        for (Matcher<? super T> elementMatcher : itemMatchers) {
            // Doesn't forward to hasItem() method so compiler can sort out generics.
            all.add(new IsCollectionContaining<>(elementMatcher));
        }

        return allOf(all);
    }
}
