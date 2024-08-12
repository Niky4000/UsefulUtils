package ru.ibs.testpumputils.interceptors;

import java.util.function.Function;
import org.hibernate.EmptyInterceptor;

/**
 * @author NAnishhenko
 */
public class SQLInterceptor extends EmptyInterceptor {

    Function<String, String> function;

    public SQLInterceptor(Function<String, String> function) {
        this.function = function;
    }

    @Override
    public String onPrepareStatement(String sql) {
        return function.apply(sql);
    }
}
