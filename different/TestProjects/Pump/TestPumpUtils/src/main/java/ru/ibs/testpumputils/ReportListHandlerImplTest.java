package ru.ibs.testpumputils;

import java.lang.reflect.Proxy;
import java.net.InetAddress;
import java.util.Calendar;
import org.slf4j.LoggerFactory;
import ru.ibs.pmp.auth.model.AuditEntry;
import ru.ibs.pmp.auth.reps.AuditEntryRepository;
import ru.ibs.pmp.auth.reps.AuditEntryRepositoryImpl;
import ru.ibs.pmp.auth.utils.AuditUtils;
import ru.ibs.pmp.reports.dao.ReportsDao;
import ru.ibs.pmp.reports.dao.impl.ReportDaoHibernate;
import ru.ibs.pmp.reports.engine.Report;
import ru.ibs.pmp.reports.engine.ReportSync;
import ru.ibs.pmp.reports.service.impl.ReportListHandlerImpl;
import ru.ibs.testpumputils.interceptors.SqlRewriteInterceptorExt;
import ru.ibs.testpumputils.interfaces.SessionFactoryInterface;
import ru.ibs.testpumputils.interfaces.SessionFactoryInvocationHandler;

/**
 *
 * @author me
 */
public class ReportListHandlerImplTest {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(ReportListHandlerImpl.class);

	static SessionFactoryInterface sessionFactory;

	public static void test() throws Exception {
		sessionFactory = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler(TestPumpUtilsMain.buildSessionFactory(), new SqlRewriteInterceptorExt()));
		try {
			ReportDaoHibernate reportDaoHibernate = new ReportDaoHibernate();
			AuditEntryRepository auditEntryRepository = new AuditEntryRepositoryImpl();
		} finally {
			sessionFactory.close();
		}
	}

	private static void test(ReportsDao reportDao, AuditEntryRepository auditEntryRepository, Report report, ReportSync sync) {
		try {
			try {
//						if ("custom".equals(template.getId().substring(0, 6))) {
//							ReportBuildTask task = reportCustomService.buildReport(buildTask, template);
//						} else {
//							ReportEngine engine = reportService.getEngine(buildTask, template);
//							if (engine instanceof XmlReport) {
//								((XmlReport) engine).setXmlReportExecutor(xmlReportExecutor);
//							}
//							ReportBuildTask task = engine.buildReport(buildTask, template);
//						}
				Long time = (Calendar.getInstance().getTimeInMillis() - sync.getStartProcessDate().getTimeInMillis()) / 1000;
				reportDao.setReportToSuccess(sync);
//						reportInProcessCount.incrementAndGet();
				logger.debug("Report #" + report.getId() + " finished in " + time + "ms.");

			} catch (Exception e) {
				Long time = (Calendar.getInstance().getTimeInMillis() - sync.getStartProcessDate().getTimeInMillis())
					/ 1000;
				try {
					AuditEntry auditEntry = AuditUtils.createReportAuditEntry(e, time, report, report.getUser(), report.getOrgId(), getLocalIP(), time * 1000);
					logger.debug("auditEntry #" + auditEntry.getUid() + " was created!");
					reportDao.setReportToFailed(sync, auditEntry.getUid());
					logger.info(auditEntry.toString());
					auditEntryRepository.save(auditEntry);
				} catch (Throwable t) {
					Throwable cause = t;
					while (cause.getCause() != null)
						cause = cause.getCause();
					logger.debug("Report #" + report.getId() + " error save audit: " + cause.getClass().getName() + " " + cause.getMessage());
					// записать в БД ошибку не удалось, выкинем ее тут просто в лог
					throw e;
				}
				logger.debug("Report #" + report.getId() + " faults in " + time + "ms.");

			}
		} catch (Throwable e) {
			logger.debug("Report #" + report.getId() + " error", e);
		}
	}

	private static String getLocalIP() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
			return "Unknown";
		}
	}
}
