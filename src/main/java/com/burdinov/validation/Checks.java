package com.burdinov.validation;

import java.util.*;
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
        return coll -> {
            if (coll.size() != ts.length)
                return false;

            Set<T> set = new HashSet<>();
            for (int i = 0; i < ts.length; i++)
                set.add(ts[i]);
            return containsExactly(set).test(coll);
        };
    }

    public static <T, Coll extends Collection<T>> Predicate<Coll> containsExactly(Collection<T> ts) {
        return coll -> {
            if (coll.size() != ts.size())
                return false;
            return containsExactly(new HashSet<>(ts)).test(coll);
        };
    }

    public static <T, Coll extends Collection<T>> Predicate<Coll> containsExactly(Set<T> ts) {
        return coll -> {
            if (coll.size() != ts.size())
                return false;

            Set<T> collSet = new HashSet<>(coll);
            return collSet.equals(ts);
        };
    }
}
