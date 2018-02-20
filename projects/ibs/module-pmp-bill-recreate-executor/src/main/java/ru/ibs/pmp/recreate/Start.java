package ru.ibs.pmp.recreate;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.ibs.pmp.module.recreate.exec.ExecuteRecreate;
import ru.ibs.pmp.module.recreate.exec.ExecuteUtils;
import ru.ibs.pmp.module.recreate.exec.bean.OsEnum;
import ru.ibs.pmp.module.recreate.exec.bean.TargetSystemBean;

/**
 * @author NAnishhenko
 */
public class Start {

    ApplicationContext applicationContext;

    public Start(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    String remotesConfig;

//    private List<TargetSystemBean> initRemotes() {
//        Pattern pattern = Pattern.compile("\\{(.+?)\\}");
//        Matcher matcher = pattern.matcher(remotesConfig);
//        List<TargetSystemBean> targetSystemBeanList = new ArrayList<>();
//        while (matcher.find()) {
//            String targetSystemBeanStr = matcher.group(1);
//            String[] targetSystemBeanStrSplited = targetSystemBeanStr.split(",");
//            TargetSystemBean targetSystemBean = new TargetSystemBean(
//                    targetSystemBeanStrSplited[0].trim().equals("windows") ? OsEnum.WINDOWS : OsEnum.LINUX,
//                    Integer.valueOf(targetSystemBeanStrSplited[1].trim()).intValue(),
//                    targetSystemBeanStrSplited[2].trim(), targetSystemBeanStrSplited[3].trim(), targetSystemBeanStrSplited[4].trim(),
//                    Integer.valueOf(targetSystemBeanStrSplited[5].trim()).intValue());
//            targetSystemBeanList.add(targetSystemBean);
//        }
//        return targetSystemBeanList;
//    }
    public void start() throws Exception {
        ExecProperties execProperties = (ExecProperties) applicationContext.getBean("propertyConfigurer");
        ExecuteRecreate executeRecreate = applicationContext.getBean(ru.ibs.pmp.module.recreate.exec.ExecuteRecreate.class);
        ExecuteUtils executeUtils = applicationContext.getBean(ru.ibs.pmp.module.recreate.exec.ExecuteUtils.class);

        Object tx = applicationContext.getBean("pmpTransactionTemplate");

//        remotesConfig = ExecProperties.getProperty("runtime.executor.remotes");
//        List<TargetSystemBean> targetSystemBeanList = initRemotes();
//        LinkedBlockingQueue<TargetSystemBean> quotaQueue = new LinkedBlockingQueue<>();
//        QuotaThread quotaThread = new QuotaThread(quotaQueue, executeUtils, targetSystemBeanList);
//        quotaThread.run();
        executeRecreate.mainExecute();
//        executeUtils.getProcessList(new TargetSystemBean(OsEnum.LINUX, 5, "172.29.4.26", "mls", "%TGB5tgb", 22));
        Thread.sleep(10 * 60 * 1000);
        String hello = "";
    }

    public static void main(String[] args) throws Exception {
        Start start = new Start(new ClassPathXmlApplicationContext("module.xml"));
        start.start();
    }
}
