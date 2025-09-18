package com.impulse.shared.utils;

import java.util.Optional;
import java.util.function.Function;

/** Minimal Result type (Java 17 compatible without sealed/records for broader tooling support). */
public abstract class Result<T,E> {
    private Result() {}
    public static final class Ok<T,E> extends Result<T,E> { private final T value; public Ok(T v){this.value=v;} public T value(){return value;} }
    public static final class Err<T,E> extends Result<T,E> { private final E error; public Err(E e){this.error=e;} public E error(){return error;} }
    public static <T,E> Result<T,E> ok(T v){ return new Ok<>(v);} public static <T,E> Result<T,E> err(E e){return new Err<>(e);}
    public boolean isOk(){ return this instanceof Ok; } public boolean isErr(){ return this instanceof Err; }
    public Optional<T> toOptional(){ return this instanceof Ok ? Optional.ofNullable(((Ok<T,E>)this).value()) : Optional.empty(); }
    public <U> Result<U,E> map(Function<T,U> fn){ if(this instanceof Ok){ return ok(fn.apply(((Ok<T,E>)this).value())); } return (Result<U,E>) this; }
    public <F> Result<T,F> mapErr(Function<E,F> fn){ if(this instanceof Err){ return err(fn.apply(((Err<T,E>)this).error())); } return (Result<T,F>) this; }
    public T getOrElse(T fallback){ return this instanceof Ok ? ((Ok<T,E>)this).value() : fallback; }
    public T getOrThrow(Function<E,? extends RuntimeException> fn){
        if (this instanceof Ok) {
            return ((Ok<T,E>)this).value();
        } else {
            return switchToException(fn);
        }
    }
    private T switchToException(Function<E,? extends RuntimeException> fn){ throw fn.apply(((Err<T,E>)this).error()); }
}
