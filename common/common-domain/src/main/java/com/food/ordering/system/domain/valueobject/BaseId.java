package com.food.ordering.system.domain.valueobject;

public abstract class BaseId<T> {

    private final T value;

    public BaseId(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof BaseId<?> baseId)) return false;

        return getValue().equals(baseId.getValue());
    }

    @Override
    public int hashCode() {
        return getValue().hashCode();
    }
}
