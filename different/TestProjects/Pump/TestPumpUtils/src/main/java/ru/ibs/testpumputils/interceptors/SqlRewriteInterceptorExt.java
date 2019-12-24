package ru.ibs.testpumputils.interceptors;

import ru.ibs.pmp.dao.hibernate.SqlRewriteInterceptor;

/**
 * @author NAnishhenko
 */
public class SqlRewriteInterceptorExt extends SqlRewriteInterceptor {

    @Override
    public String onPrepareStatement(String sql) {
        return super.onPrepareStatement(sql.replaceAll("PMP_PROD.", ""));
    }

}
