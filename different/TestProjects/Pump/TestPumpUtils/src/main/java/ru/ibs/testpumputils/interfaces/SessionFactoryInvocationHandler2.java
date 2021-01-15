package ru.ibs.testpumputils.interfaces;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import ru.ibs.pmp.dao.hibernate.SqlRewriteInterceptor;
import ru.ibs.testpumputils.interceptors.SQLInterceptor;
import ru.ibs.testpumputils.utils.FieldUtil;

/**
 * @author NAnishhenko
 */
public class SessionFactoryInvocationHandler2 implements InvocationHandler {

    List<Session> sessionList = new ArrayList<>();
    private final SessionFactory sessionFactory;
    private final EmptyInterceptor interceptor;

    public SessionFactoryInvocationHandler2(SessionFactory sessionFactory, EmptyInterceptor interceptor) {
        this.sessionFactory = sessionFactory;
        this.interceptor = interceptor;
    }

//    Session session;
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("cleanSessions")) {
            sessionList.forEach(session -> session.close());
            sessionList.clear();
            return null;
        } else if (method.getName().equals("getCurrentSession") || method.getName().equals("openSession")) {
//            if (session == null) {
            Session session = sessionFactory.openSession();
            if (interceptor != null) {
                FieldUtil.setField(session, interceptor, "interceptor");
            }
            sessionList.add(session);
//            }
            return session;
        } else {
            return method.invoke(sessionFactory, args);
        }
    }

}
