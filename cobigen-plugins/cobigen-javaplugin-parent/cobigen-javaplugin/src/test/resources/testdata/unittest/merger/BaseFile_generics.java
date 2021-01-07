package com.devonfw;

public abstract class Clazz<T extends Object> extends Map<String, T> {

    private T t;

    public void set(T t) {
        this.t = t;
    }

    public T get() {
        return t;
    }

    public <U extends Number> void inspect(U u) {

    }

}
