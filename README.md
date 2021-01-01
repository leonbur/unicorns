# Unicorns
Home of small Java goodies.

## Table of Contents
1. [Collectors](#Collectors)
2. [Validation](#Validation)

## Collectors
Collectors for java `Stream` to perform batching operations.

* _PartitionedList stream collector_ - to collect a stream into list of fixed-size lists.
```java
// splitting students to study groups
int groupSize = 3;
List<Student> students = Arrays.asList(
        new Student("Alice"),
        new Student("Bobby"),
        new Student("Clarice"),
        new Student("Danny"),
        new Student("Eleanor")
);

List<List<Student>> studyGroups = students.stream().collect(Collectors.toPartitionedList(groupSize));
// [[Alice, Bobby, Clarice], [Danny, Eleanor]]
```

* _BatchingForeach stream collector_ - a collector that batches the stream elements and executes a function on each batch. doesn't keep batches in memeory after handling.
```java
// reading a CSV row stream and inserting them to a database in batches
int batchSize = 100;
csvRows.stream()
        .map(toEntity)
        .collect(Collectors.batchingForeach(batchSize, row -> Database.insert(row)));
```

## Validation
`validation.Validator` is lightweight validator to aggregate property checks. example:
```java
// validating the properties of Person
Person validPerson = new Person("Alice", 6);

Person result = validate(validPerson,
        check(p -> p.name.matches("[a-zA-Z\\s]+"), "name should contain only letters and spaces"),
        checkNot(p -> p.age < 0, "age should be positive")
);
// validPerson


// failing on some properties of Person
Person invalidPerson = new Person("Al1c3", -6);

Person result = validate(invalidPerson,
        check(p -> p.name.matches("[a-zA-Z\\s]+"), "name should contain only letters and spaces"),
        checkNot(p -> p.age < 0, "age should be positive")
);
//ValidationException(List("name should contain only letters and spaces", "age should be positive"))
```
`Validator` class contains `validate`, `check` and `checkNot` functions.

In addition, there are also some helpers in `validation.Checks`. for example:
* checking equality between Comparable<T> classes with `equalTo`
```java
Person alice = new Person("Alice", 6);
Person alice2 = new Person("Alice", 6);
        
Person result = validate(alice,
        check(equalTo(alice2), "persons should have the same name and age")
);
```

* checking that a collection contains elements only the specific element with `containsExactly` (accepting varargs or a collection)
```java
Person alice = new Person("Alice", 6);
Person bobby = new Person("Bobby", 12);

List<Person> people = Arrays.asList(alice, bobby);

List<Person> result = validate(people,
    check(containsExactly(alice, bobby), "people should contain alice and bobby")
);
```


<sub><sup>Created by Leonid Burdinov</sup></sub>