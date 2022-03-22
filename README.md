# λ Koans

This project is meant to assist you in learning the basics of [lambda](https://github.com/palatable/lambda). It follows the koans idea established by [other](http://rubykoans.com/) [languages](http://clojurekoans.com/), expressed as [JUnit](https://junit.org/junit4/) tests.

## Running the Koans

The koans are built as JUnit tests. You can use your IDE of choice to run all tests, or, by running the following:

```shell
mvn test
```

You can run tests for a concept by running:

```shell
mvn -Dtest=AboutEither test 
```

Or specifically, an individual test

```shell
 mvn -Dtest=AboutEither#toMaybeConvertsEitherToMaybe test
```

## Working through the koans

Each koan will present failing tests on a particular topic. Your task is to reason about what value will make each test pass. It is recommended to work through the tests one by one, and conceptually, in the following order:

* AboutFunctors
* AboutLazy
* AboutSemigroups
* AboutMonoids
* AboutMaybe
* AboutTraversables
* AboutEither
