package io.alcatraz.noapplet;

public interface AsyncInterface<T> {
    void onDone(T result);
}
