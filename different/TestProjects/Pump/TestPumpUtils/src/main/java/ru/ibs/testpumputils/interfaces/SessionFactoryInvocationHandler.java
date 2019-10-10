package ru.ibs.testpumputils.interfaces;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * @author NAnishhenko
 */
public class SessionFactoryInvocationHandler implements InvocationHandler {

    List<Session> sessionList = new ArrayList<>();
    private final SessionFactory sessionFactory;

    public SessionFactoryInvocationHandler(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("cleanSessions")) {
            sessionList.forEach(session -> session.close());
            sessionList.clear();
            return null;
        } else if (method.getName().equals("getCurrentSession")) {
            Session session = sessionFactory.openSession();
            sessionList.add(session);
            return session;
        } else {
            return method.invoke(sessionFactory, args);
        }
    }

}
