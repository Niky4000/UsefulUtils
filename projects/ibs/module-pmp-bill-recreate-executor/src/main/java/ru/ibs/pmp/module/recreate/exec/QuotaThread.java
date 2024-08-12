//package ru.ibs.pmp.module.recreate.exec;
//
//import java.util.Collection;
//import java.util.List;
//import java.util.concurrent.LinkedBlockingQueue;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import ru.ibs.pmp.module.recreate.exec.bean.OsProcessBean;
//import ru.ibs.pmp.module.recreate.exec.bean.TargetSystemBean;
//
///**
// * @author NAnishhenko
// */
//public class QuotaThread extends Thread {
//
//    private static final int WAIT_TIME = 20 * 1000;
//    private final LinkedBlockingQueue<TargetSystemBean> quotaQueue;
//    private final ExecuteUtils executeUtils;
//    private final Collection<TargetSystemBean> targetSystemBeanCollection;
//
//    public QuotaThread(LinkedBlockingQueue<TargetSystemBean> quotaQueue, ExecuteUtils executeUtils, Collection<TargetSystemBean> targetSystemBeanCollection) {
//        this.quotaQueue = quotaQueue;
//        this.executeUtils = executeUtils;
//        this.targetSystemBeanCollection = targetSystemBeanCollection;
//    }
//
//    @Override
//    public void run() {
//        try {
//            while (true) {
//                for (TargetSystemBean targetSystemBean : targetSystemBeanCollection) {
//                    List<OsProcessBean> processList = executeUtils.getProcessList(targetSystemBean);
//                    if (targetSystemBean.getQuota() >= processList.size()) {
//                        for (int i = processList.size(); i < targetSystemBean.getQuota(); i++) {
//                            quotaQueue.offer(targetSystemBean);
//                        }
//                    }
//                }
//                Thread.sleep(WAIT_TIME);
//            }
//        } catch (InterruptedException ex) {
//            Logger.getLogger(QuotaThread.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//}
