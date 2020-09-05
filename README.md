# Unicorns
Home of some one-off Java goodies.

## Table of Contents
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

* _PartitionForeach stream collector_ - a collector that batches the stream elements and executes a function on each batch. doesn't keep batches in memeory after handling.
```java
// reading a CSV row stream and inserting them to a database in batches
int batchSize = 100;
csvRows.stream()
        .map(toEntity)
        .collect(Collectors.partitionForeach(batchSize, row -> Database.insert(row)));
```


###### This is a work in progress 