package ru.ibs.testpumputils.interfaces;

import org.hibernate.SessionFactory;

/**
 * @author NAnishhenko
 */
public interface SessionFactoryInterface extends SessionFactory {

    public void cleanSessions();
}
