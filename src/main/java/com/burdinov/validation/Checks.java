package com.burdinov.validation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class Checks {

    public static <T> Predicate<T> equalTo(T other) {
        return t -> t.equals(other);
    }

    public static <T extends Comparable<T>> Predicate<T> equalTo(T other) {
        return t -> t.compareTo(other) == 0;
    }

    @SafeVarargs
    public static <T, Coll extends Collection<T>> Predicate<Coll> containsExactly(T... ts) {
        Set<T> set = new HashSet<>();
        Collections.addAll(set, ts);
        return containsExactly(set);
    }

    public static <T, Coll extends Collection<T>> Predicate<Coll> containsExactly(Collection<T> ts) {
        return coll -> {
            Set<T> a = new HashSet<>(coll);
            Set<T> b = new HashSet<>(ts);
            return a.equals(b);
        };
    }
}
