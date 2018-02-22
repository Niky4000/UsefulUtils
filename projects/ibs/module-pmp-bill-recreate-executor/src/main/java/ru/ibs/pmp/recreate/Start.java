package ru.ibs.pmp.recreate;

import java.lang.reflect.Method;
import java.util.List;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.ibs.pmp.module.recreate.exec.ExecuteRecreate;
import ru.ibs.pmp.module.recreate.exec.ExecuteUtils;
import ru.ibs.pmp.module.recreate.exec.bean.TargetSystemBean;
import ru.ibs.pmp.module.recreate.exec.bean.TargetSystemBeanWrapper;

/**
 * @author NAnishhenko
 */
public class Start {

    ApplicationContext applicationContext;

    public Start(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void getProcessListForDebug() throws Exception {
        ExecuteUtils executeUtils = applicationContext.getBean(ru.ibs.pmp.module.recreate.exec.ExecuteUtils.class);
        ExecuteRecreate executeRecreate = applicationContext.getBean(ru.ibs.pmp.module.recreate.exec.ExecuteRecreate.class);
        Method initRemotes = ExecuteRecreate.class.getDeclaredMethod("initRemotes", null);
        initRemotes.setAccessible(true);
        List<TargetSystemBean> targetSystemBeanCollection = (List<TargetSystemBean>) initRemotes.invoke(executeRecreate, null);
        executeUtils.getProcessList(new TargetSystemBeanWrapper(targetSystemBeanCollection.get(0)));
    }

    public void start() throws Exception {
        ExecuteRecreate executeRecreate = applicationContext.getBean(ru.ibs.pmp.module.recreate.exec.ExecuteRecreate.class);
        while (true) {
            executeRecreate.mainExecute();
            Thread.sleep(20 * 1000);
        }
    }

    public static void main(String[] args) throws Exception {
        Start start = new Start(new ClassPathXmlApplicationContext("module.xml"));
        start.start();
    }
}
