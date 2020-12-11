package com.burdinov.validation;

import io.vavr.control.Either;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Validator {

	/**
	 * The main validation function. accepts multiple checks on the value of T. returns either a valid T, or a list of ERRORS
	 * @param t the object under validation
	 * @param checks a collection of validation functions and their respective error messages in case they fail
	 * @param <T> the type of the object under validation
	 * @param <ERROR> the type of the error. typically a {@code String} or an {@code Exception}
	 * @return and Either object that will contain the object under validation in the {@code right} or a list of errors in the {@code left}
	 */
	@SafeVarargs
	public static <T, ERROR> Either<List<ERROR>, T> validate(T t, Check<T, ERROR>... checks) {
		List<ERROR> errors = Arrays.stream(checks)
				.flatMap(check -> check.tester.apply(t) ? Stream.empty() : Stream.of(check.error))
				.collect(Collectors.toList());

		return errors.isEmpty() ? Either.right(t) : Either.left(errors);
	}

	/**
	 * The main function that holds a single test on the properties on T. also holds the error return in case the validation test failed
	 * @param tester a function of {@code T -> Boolean} where a {@code true} means the validation is successful
	 * @param error the error in case the validation failed
	 * @param <T> the type of the object under validation
	 * @param <ERROR> the type of the error. typically a {@code String} or an {@code Exception}
	 * @return a {@code Check} type that hold the validation test information
	 */
	public static <T, ERROR> Check<T, ERROR> check(Function<T, Boolean> tester, ERROR error) {
		return new Check<>(tester, error);
	}

	/**
	 * A convenience method to flip the result of {@link #check(Function, Object)}
	 * @param tester a function of {@code T => Boolean} where a {@code false} means the validation is successful
	 * @param error the error in case the validation failed
	 * @param <T> the type of the object under validation
	 * @param <ERROR> the type of the error. typically a {@code String} or an {@code Exception}
	 * @return a {@code Check} type that hold the validation test information
	 * @return
	 */
	public static <T, ERROR> Check<T, ERROR> checkNot(Function<T, Boolean> tester, ERROR error) {
		return check(tester.andThen(res -> !res), error);
	}

	public static class Check<T, ERROR> {
		Function<T, Boolean> tester;
		ERROR error;

		private Check() {}

		Check(Function<T, Boolean> tester, ERROR error) {
			this.tester = tester;
			this.error = error;
		}
	}
}
