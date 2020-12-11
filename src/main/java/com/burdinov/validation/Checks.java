package com.burdinov.validation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class Checks {

    public static <T extends Comparable<T>> Function<T, Boolean> equalTo(T other) {
        return t -> t.compareTo(other) == 0;
    }

    @SafeVarargs
    public static <T, Coll extends Collection<T>> Function<Coll, Boolean> containsExactly(T... ts) {
        Set<T> set = new HashSet<>();
        Collections.addAll(set, ts);
        return containsExactly(set);
    }

    public static <T, Coll extends Collection<T>> Function<Coll, Boolean> containsExactly(Collection<T> ts) {
        return coll -> {
            Set<T> a = new HashSet<>(coll);
            Set<T> b = new HashSet<>(ts);
            return a.equals(b);
        };
    }
}
