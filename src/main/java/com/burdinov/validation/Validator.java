package com.burdinov.validation;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Validator {

	/**
	 * The main validation function. accepts multiple checks on the value of T
	 * @param t the object under validation
	 * @param checks a collection of validation functions and their respective error messages in case they fail
	 * @param <T> the type of the object under validation
	 * @return a valid T, or throws and exception with a list of errors
	 */
	@SafeVarargs
	public static <T> T validate(T t, Check<T>... checks) throws ValidationException{
		List<String> errors = Arrays.stream(checks)
				.flatMap(check -> check.tester.apply(t) ? Stream.empty() : Stream.of(check.error))
				.collect(Collectors.toList());

		if (errors.isEmpty())
			return t;
		else
			throw new ValidationException(errors);
	}

	/**
	 * The main function that holds a single test on the properties on T. also holds the error return in case the validation test failed
	 * @param tester a function of {@code T -> Boolean} where a {@code true} means the validation is successful
	 * @param error the error in case the validation failed
	 * @param <T> the type of the object under validation
	 * @return a {@code Check} type that hold the validation test information
	 */
	public static <T> Check<T> check(Function<T, Boolean> tester, String error) {
		return new Check<>(tester, error);
	}

	/**
	 * A convenience method to flip the result of {@link #check(Function, String)}
	 * @param tester a function of {@code T => Boolean} where a {@code false} means the validation is successful
	 * @param error the error in case the validation failed
	 * @param <T> the type of the object under validation
	 * @return a {@code Check} type that hold the validation test information
	 */
	public static <T> Check<T> checkNot(Function<T, Boolean> tester, String error) {
		return check(tester.andThen(res -> !res), error);
	}

	public static class Check<T> {
		Function<T, Boolean> tester;
		String error;

		private Check() {}

		Check(Function<T, Boolean> tester, String error) {
			this.tester = tester;
			this.error = error;
		}
	}
}
