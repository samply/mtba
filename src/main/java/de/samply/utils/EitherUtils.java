package de.samply.utils;

import io.vavr.control.Either;
import java.util.function.Function;

public class EitherUtils {


  @FunctionalInterface
  public interface CheckedFunction<T, R> {

    R apply(T t) throws Exception;
  }

  /**
   * Wraps a function, returning an Either that catches any exception as left.
   *
   * @param function function to be wrapped.
   * @param <T>      Argument of function.
   * @param <R>      Result of function.
   * @return Either with Exception as left und result as right.
   */
  public static <T, R> Function<T, Either> lift(CheckedFunction<T, R> function) {
    return t -> {
      try {
        return Either.right(function.apply(t));
      } catch (Exception ex) {
        return Either.left(ex);
      }
    };
  }

  @FunctionalInterface
  public interface ThrowingConsumer<T, E extends Exception> {

    void accept(T t) throws E;
  }

  /**
   * Wraps a consumer returning Either with exception as left and null as right.
   *
   * @param consumer consumer to be wrapped.
   * @param <T>      argument of consumer.
   * @return Either with Exception as left und result as right.
   */
  public static <T, E extends Exception> Function<T, Either> liftConsumer(
      ThrowingConsumer<T, E> consumer) {
    return t -> {
      try {
        consumer.accept(t);
        return null;
      } catch (Exception ex) {
        return Either.left(ex);
      }
    };
  }

}
