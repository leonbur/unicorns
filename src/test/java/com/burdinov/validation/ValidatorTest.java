package com.burdinov.validation;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static com.burdinov.validation.Checks.containsExactly;
import static com.burdinov.validation.Checks.equalTo;
import static com.burdinov.validation.Validator.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ValidatorTest {

    static class Person {
        String name;
        int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ValidatorTest.Person person = (ValidatorTest.Person) o;

            if (age != person.age) return false;
            return name.equals(person.name);
        }
    }

    static class ComparablePerson implements Comparable<ComparablePerson> {
        String name;
        int age;

        public ComparablePerson(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        private final Comparator<ComparablePerson> comparator = Comparator.comparing(ComparablePerson::getName).thenComparing(ComparablePerson::getAge);

        @Override
        public int compareTo(ComparablePerson o) {
            return comparator.compare(this, o);
        }
    }

    @Test
    public void simple_valid_result() {
        Person validPerson = new Person("Alice", 6);

        Person result = validate(validPerson,
                check(p -> p.name.matches("[a-zA-Z\\s]+"), "name should contain only letters and spaces"),
                checkNot(p -> p.age < 0, "age should be positive")
        );

        assertThat(result).isEqualTo(validPerson);
    }

    @Test
    public void all_fail() {
        Person invalidPerson = new Person("Al1c3", -6);

        assertThatThrownBy(() -> validate(invalidPerson,
                check(p -> p.name.matches("[a-zA-Z\\s]+"), "name should contain only letters and spaces"),
                checkNot(p -> p.age < 0, "age should be positive")
                )
        )
                .isInstanceOf(ValidationException.class)
                .hasMessageContainingAll(
                        "name should contain only letters and spaces",
                        "age should be positive"
                );
    }

    @Test
    public void checks_equalTo() {
        Person alice = new Person("Alice", 6);
        Person alice2 = new Person("Alice", 6);

        Person result = validate(alice,
                check(equalTo(alice2), "persons should have the same name and age")
        );

        assertThat(result).isEqualTo(alice);
    }

    @Test
    public void checks_equalTo_comparable() {
        ComparablePerson alice = new ComparablePerson("Alice", 6);
        ComparablePerson alice2 = new ComparablePerson("Alice", 6);

        ComparablePerson result = validate(alice,
                check(equalTo(alice2), "persons should have the same name and age")
        );

        assertThat(result).isEqualTo(alice);
    }

    @Test
    public void checks_containsExactly() {
        Person alice = new Person("Alice", 6);
        Person bobby = new Person("Bobby", 12);

        List<Person> people = Arrays.asList(alice, bobby);

        List<Person> result = validate(people,
                check(containsExactly(alice, bobby), "people should contain alice and bobby")
        );

        assertThat(result).isEqualTo(people);
    }
}
