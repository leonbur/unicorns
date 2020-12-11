package com.burdinov.validation;

import io.vavr.control.Either;
import org.junit.jupiter.api.Test;

import java.util.*;

import static com.burdinov.validation.Checks.containsExactly;
import static com.burdinov.validation.Checks.equalTo;
import static com.burdinov.validation.Validator.*;
import static org.assertj.core.api.Assertions.assertThat;

public class ValidatorTest {


    static class Person implements Comparable<Person> {
        String name;
        int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public int compareTo(Person o) {
            return name.compareTo(o.name) == 0 ? 0 : age - o.age;
        }
    }

    @Test
    public void simple_valid_result() {
        Person validPerson = new Person("Alice", 6);

        Either<List<String>, Person> result = validate(validPerson,
                check(p -> p.name.matches("[a-zA-Z\\s]+"), "name should contain only letters and spaces"),
                checkNot(p -> p.age < 0, "age should be positive")
        );

        assertThat(result).isEqualTo(Either.right(validPerson));
    }

    @Test
    public void all_fail() {
        Person invalidPerson = new Person("Al1c3", -6);

        Either<List<String>, Person> result = validate(invalidPerson,
                check(p -> p.name.matches("[a-zA-Z\\s]+"), "name should contain only letters and spaces"),
                checkNot(p -> p.age < 0, "age should be positive")
        );

        List<String> expectedErrors = new ArrayList<>();
        expectedErrors.add("name should contain only letters and spaces");
        expectedErrors.add("age should be positive");

        assertThat(result).isEqualTo(Either.left(expectedErrors));
    }

    @Test
    public void checks_equalTo() {
        Person alice = new Person("Alice", 6);
        Person alice2 = new Person("Alice", 6);

        Either<List<String>, Person> result = validate(alice,
                check(equalTo(alice2), "persons should have the same name and age")
        );

        assertThat(result).isEqualTo(Either.right(alice));
    }

    @Test
    public void checks_containsExactly() {
        Person alice = new Person("Alice", 6);
        Person bobby = new Person("Bobby", 12);

        List<Person> people = Arrays.asList(alice, bobby);

        Either<List<String>, List<Person>> result = validate(people,
                check(containsExactly(alice, bobby), "people should contain alice and bobby")
        );

        assertThat(result).isEqualTo(Either.right(people));
    }
}
