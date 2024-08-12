package ru.ibs.pmp.module.recreate.exec;

import com.google.common.collect.ImmutableMap;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.TransactionTemplate;
import ru.ibs.pmp.module.recreate.exec.config.ExecPropertiesForDebug;

/**
 * @author NAnishhenko
 */
public class ExecuteRecreateTest {

    @Test
    public void testExecuteRecreate() throws InterruptedException {
        Map<String, String> configs = ImmutableMap.<String, String>builder()
                .put("runtime.pmp.recreate-executor.jarPath", "/home/pmp/recreate/recreate.jar")
                .put("runtime.pmp.start-recreate-executor", "true")
                .put("runtime.pmp.recreate-executor.configPath", "/home/pmp/common/runtime.properties")
                .put("runtime.executor.remotes", "{windows, 15, 192.168.192.116, hello, world, 9084, C:\\\\recreateFor111, 4G, 1G, null, null},{linux, 2, 192.168.192.111, hello, world, 9084, /home/lin1/recreate1, 4G, 1G, null, null},{linux, 2, 192.168.192.112, hello, world, 9084, /home/lin2/recreate2, 4G, 1G, null, null},{linux, 1, 192.168.192.113, hello, world, 9084, /home/lin3/recreate3, 4G, 1G, null, null}")
                .build();
        ExecPropertiesForDebug.setProperties(configs);
        ApplicationContext context = new ClassPathXmlApplicationContext("module_test.xml");
        ExecuteRecreate executeRecreate = context.getBean(ExecuteRecreate.class);
        ReflectionTestUtils.setField(executeRecreate, "canStartExecutor", configs.get("runtime.pmp.start-recreate-executor"), String.class);
        ReflectionTestUtils.setField(executeRecreate, "configPath", configs.get("runtime.pmp.recreate-executor.configPath"), String.class);
        ReflectionTestUtils.setField(executeRecreate, "jarPath", configs.get("runtime.pmp.recreate-executor.jarPath"), String.class);
        ReflectionTestUtils.setField(executeRecreate, "remotesConfig", configs.get("runtime.executor.remotes"), String.class);
        executeRecreate.mainExecute();
        List<ExecuteThread> executeThreadList = (List<ExecuteThread>) ReflectionTestUtils.getField(executeRecreate, "executeThreadList");
        executeThreadList.stream().forEach(thread -> {
            thread.interrupt();
            try {
                thread.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(ExecuteRecreateTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
}
