package ru.ibs.updater;

import org.springframework.stereotype.Component;

/**
 *
 * @author NAnishhenko
 */
public interface UpdaterMain {

    public boolean update(String sql, boolean loop) throws Exception;
}
