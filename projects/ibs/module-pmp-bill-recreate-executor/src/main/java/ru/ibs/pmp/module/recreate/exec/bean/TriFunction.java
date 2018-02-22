package ru.ibs.pmp.module.recreate.exec.bean;

/**
 * @author NAnishhenko
 */
@FunctionalInterface
public interface TriFunction<T, Y, Z, R> {

    R apply(T t, Y y, Z z);
}
