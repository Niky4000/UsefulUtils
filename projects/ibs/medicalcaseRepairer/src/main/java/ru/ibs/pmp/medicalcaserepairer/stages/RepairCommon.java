package ru.ibs.pmp.medicalcaserepairer.stages;

import com.google.common.base.Stopwatch;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.datatype.DatatypeConfigurationException;
import org.erzl.services.GetRepPumpAttachedAgeSexResponse;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import ru.ibs.pmp.api.model.AbstractBaseEntity;
import ru.ibs.pmp.api.model.Bill;
import ru.ibs.pmp.api.model.BillStatistics;
import ru.ibs.pmp.api.model.BillStatisticsTime;
import ru.ibs.pmp.api.model.Invoice;
import ru.ibs.pmp.api.model.PmpSync;
import ru.ibs.pmp.api.model.Requirement;
import ru.ibs.pmp.api.model.RevisionEntity;
import ru.ibs.pmp.api.model.interfaces.AudEntityWithId;
import ru.ibs.pmp.api.model.msk.export.MailGwLogEntry;
import ru.ibs.pmp.api.nsi.interfaces.FindNsiEntries;
import ru.ibs.pmp.api.nsi.interfaces.FindNsiEntry;
import ru.ibs.pmp.api.nsi.model.NsiEntry;
import ru.ibs.pmp.api.nsi.model.NsiEntryList;
import ru.ibs.pmp.api.patients.interfaces.GetNewbornPatientPojo;
import ru.ibs.pmp.api.patients.interfaces.GetNonresidentPatientPojo;
import ru.ibs.pmp.api.patients.interfaces.GetPatientFromCrossTablePojo;
import ru.ibs.pmp.api.patients.interfaces.GetPatientPojo;
import ru.ibs.pmp.api.patients.interfaces.GetSexCompositionPojo;
import ru.ibs.pmp.api.patients.model.Patient;
import ru.ibs.pmp.api.patients.model.UnidentPatient;
import ru.ibs.pmp.common.ex.PmpFeatureException;
import ru.ibs.pmp.dao.BillDAO2;
import ru.ibs.pmp.dao.BillStatisticsDAO;
import ru.ibs.pmp.dao.RevinfoDAO;
import ru.ibs.pmp.dao.SyncDAO;
import ru.ibs.pmp.dao.hibernate.CommonAudDAOHibernate;
import ru.ibs.pmp.dao.hibernate.CostAudDAOHibernate;
import ru.ibs.pmp.dao.hibernate.InvoiceAudDAOHibernate;
import ru.ibs.pmp.dao.hibernate.SyncDAOHibernate;
import ru.ibs.pmp.lpu.dao.MoDao;
import ru.ibs.pmp.module.pmp.bill.recreate.RecreateUtils;
import ru.ibs.pmp.module.pmp.bill.recreate.bean.BillStatisticsBean;
import ru.ibs.pmp.module.pmp.bill.recreate.bean.CheckBean;
import ru.ibs.pmp.module.pmp.bill.recreate.bean.LpuBean;
import ru.ibs.pmp.module.pmp.bill.recreate.bean.MethodKeeperBean;
import ru.ibs.pmp.module.pmp.bill.recreate.bean.PatientIdBean;
import ru.ibs.pmp.module.pmp.bill.recreate.bean.SelectedBillsBean;
import ru.ibs.pmp.module.pmp.bill.recreate.bean.SmoDepartmentBean;
import ru.ibs.pmp.module.pmp.bill.recreate.bean.StopwatchBean;
import ru.ibs.pmp.module.pmp.bill.recreate.dao.psevdo.patients.PsevdoGetNewbornPatientPojo;
import ru.ibs.pmp.module.pmp.bill.recreate.dao.psevdo.patients.PsevdoGetUnidentPatientPojo;
import ru.ibs.pmp.module.pmp.bill.recreate.exceptions.LpuNotFoundException;
import ru.ibs.pmp.module.pmp.bill.recreate.exceptions.PropertiesIsNotResolvedException;
import ru.ibs.pmp.module.pmp.bill.recreate.exceptions.ThisProcessIsAlreadyExistsException;
import ru.ibs.pmp.module.pmp.bill.recreate.report.ExecutePdfRecreateReport;
import ru.ibs.pmp.module.pmp.bill.recreate.stages.RecreateCommon;
import ru.ibs.pmp.module.pmp.bill.recreate.stages.RecreateCommonImpl;
import ru.ibs.pmp.nsi.service.NsiService;
import ru.ibs.pmp.persons.interfaces.PersonDAO;
import ru.ibs.pmp.practitioners.dao.PractitionerDao;
import ru.ibs.pmp.service.export.msk.policy.PolicyInfoProvider;
import ru.ibs.pmp.util.AuditUtil;
import ru.ibs.pmp.util.ProcessUtils;
import ru.ibs.pmp.zlib.module.pmp.bill.recreate.bean.SmoBean;
import ru.ibs.pmp.zlib.module.pmp.bill.recreate.bean.TerritoryBean;
import ru.ibs.pmp.zlib.module.pmp.bill.recreate.dao.psevdo.nsi.PsevdoFindNsiEntries;
import ru.ibs.pmp.zlib.module.pmp.bill.recreate.dao.psevdo.nsi.PsevdoFindNsiEntry;
import ru.ibs.pmp.zlib.module.pmp.bill.recreate.dao.psevdo.nsi.PsevdoNsiService;

/**
 * @author NAnishhenko
 */
public class RepairCommon implements RecreateCommon {

    RecreateCommonImpl recreateCommon = new RecreateCommonImpl() {
        @Override
        public TransactionTemplate getTx() {
            return tx;
        }

        @Override
        public Session getSession() {
            Session session = sessionFactory.openSession();
            return session;
        }

    };

    SessionFactory sessionFactory;
    TransactionTemplate tx;
    RecreateUtils recreateUtils;

    public void setRecreateUtils(RecreateUtils recreateUtils) {
        try {
            Field recreateUtilsField = RecreateCommonImpl.class.getDeclaredField("recreateUtils");
            recreateUtilsField.setAccessible(true);
            recreateUtilsField.set(recreateCommon, recreateUtils);
        } catch (Exception ex) {
            Logger.getLogger(RepairCommon.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.recreateUtils = recreateUtils;
    }

    @Override
    public StopwatchBean createStopWatch() {
        return recreateCommon.createStopWatch();
    }

    @Override
    public void stop(StopwatchBean stopwatch) {
        recreateCommon.stop(stopwatch);
    }

    @Override
    public Session getSession() {
        Session session = sessionFactory.openSession();
        return session;
    }

    @Override
    public Session getSession(SessionFactory sessionFactory) {
        Session session = sessionFactory.openSession();
        return session;
    }

    @Override
    public void closeSession(Session session) throws HibernateException {
        session.flush();
        session.close();
    }

    @Override
    public Requirement getRequirement(String moId, int year, int month) throws TransactionException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public RevisionEntity createRevInfoOnly(String userId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public BillStatisticsBean createBillStatistics(String lpuId, Date period, BillStatistics.BillOperation billOperation, String parameters, String userId, boolean doNotCreateRevisionEntity) throws TransactionException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public BillStatisticsBean createBillStatistics(final Requirement requirement, String lpuId, Date period, BillStatistics.BillOperation billOperation, String parameters, String userId, boolean doNotCreateRevisionEntity) throws TransactionException {
        BillStatisticsBean billStatisticsBean = getTx().execute(new TransactionCallback<BillStatisticsBean>() {
            @Override
            public BillStatisticsBean doInTransaction(TransactionStatus status) {
                Session session = getSession();
                BillStatistics billStatistics = null;
                billStatistics = new BillStatistics(requirement.getId(), requirement, new Date(), null, billOperation, 0, null, parameters, userId, ProcessUtils.getProcessId(), new SyncDAOHibernate().getLocalIP());
                Long id = (Long) session.save(billStatistics);
                if (id != null) {
                    billStatistics.setId(id);
                }
                closeSession(session);
                return new BillStatisticsBean(null, billStatistics);
            }
        });
        recreateUtils.setBillStatistics(billStatisticsBean.getBillStatistics());
        return billStatisticsBean;
    }

    @Override
    public String getUserIdFromPmpSync(PmpSync pmpSync, String lpuId, Date period, String type, Integer year, Integer month) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SelectedBillsBean getSelectedBillIds(String type, String lpuId, Date period, Integer year, Integer month, Requirement requirement) throws NumberFormatException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Future<NsiEntryList> getDocumentsTypeRawData(Date period, List<BillStatisticsTime> billStatisticsTimeList) throws TransactionException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Future<NsiEntryList> getTerritoryCodesRawData(Date period, List<BillStatisticsTime> billStatisticsTimeList) throws TransactionException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Future<List<String[]>> getDomainByOrgIdsRaw(Date period, List<BillStatisticsTime> billStatisticsTimeList) throws TransactionException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toStr(Object obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PsevdoNsiService createPsevdoNsiService(Map<String, TerritoryBean> allTerritoryCodes, Map<String, SmoBean> smosByOgrnCode, Map<String, SmoBean> smosByQq, Map<String, SmoBean> smosById, Map<String, NsiEntry> domainByOrgIdsByObjectId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<SmoDepartmentBean> getSmoDepartmentBeanList(Date period, StopwatchBean stopwatch, List<BillStatisticsTime> billStatisticsTimeList) throws TransactionException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<String, List<SmoDepartmentBean>> getOmsDepartmentsByFilId(List<SmoDepartmentBean> smoDepartmentBeanList) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<String, NsiEntry> getDomainByOrgIdsByCode(List<String[]> list) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String[]> getTerritoryCodesRawDataList(NsiEntryList nsiEntryList) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String[]> getDocumentsTypeRawDataList(NsiEntryList nsiEntryList) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<String, NsiEntry> getDomainByOrgIdsByObjectId(List<String[]> list) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public NsiEntry createDomainByOrgIdNsiEntry(Object[] entry) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<String, String> getAllTerritoryOkatos(List<String[]> list) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<String, NsiEntry> getAllDocumentTypes(List<String[]> list) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void initPsevdoFindNsiEntryProxy(Map<String, String> allTerritoryOkatos, Map<String, NsiEntry> domainByOrgIdsByCode, Map<String, NsiEntry> domainByOrgIdsByObjectId, Map<String, NsiEntry> allDocumentsType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int updateMedicalCases(Session session, boolean canEdit, Date periodStart, Date periodEnd, Collection<String> lpuIds) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<BillStatisticsTime> createBillStatisticsTimeList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public CheckBean checkExecutePosibility(String requestlpuId, Date period, Date periodEnd, StopwatchBean stopwatch, List<BillStatisticsTime> billStatisticsTimeList, boolean notNeedToSetSyncFlag, String featureName, boolean useStage1Cache, ExecutePdfRecreateReport executePdfRecreateReport) throws ThisProcessIsAlreadyExistsException, LpuNotFoundException, PropertiesIsNotResolvedException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getCallData(String moId, Date period) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getSendData(String moId, Date period) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getRecreateData(String moId, Date period) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public GetPatientPojo createPsevdoGetPatientPojoProxy(Map<PatientIdBean, Patient> patientMap) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public GetNonresidentPatientPojo createPsevdoGetNonresidentPatientPojo(Map<PatientIdBean, Patient> patientMap) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public GetNewbornPatientPojo createPsevdoGetNewbornPatientPojo(PsevdoGetNewbornPatientPojo psevdoGetNewbornPatientPojo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public GetPatientFromCrossTablePojo createPsevdoGetPatientFromCrossTablePojo(Map<PatientIdBean, Patient> patientFromCrossTableMap) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PsevdoGetUnidentPatientPojo createPsevdoGetUnidentPatientPojo(Map<PatientIdBean, UnidentPatient> unidentPatientMap, Map<PatientIdBean, Patient> patientFromCrossTableMap, Map<PatientIdBean, Patient> patientMap) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PolicyInfoProvider createPolicyInfoProvider(LpuBean lpuBean, Date period, PsevdoNsiService psevdoNsiService, GetPatientPojo psevdoGetPatientPojoProxy, GetNonresidentPatientPojo psevdoGetNonresidentPatientPojoProxy, GetNewbornPatientPojo psevdoGetNewbornPatientPojoProxy, GetPatientFromCrossTablePojo psevdoGetPatientFromCrossTablePojoProxy, PsevdoGetUnidentPatientPojo psevdoGetUnidentPatientPojo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PolicyInfoProvider createPolicyInfoProvider(LpuBean lpuBean, Date period, PsevdoNsiService psevdoNsiService, Map<PatientIdBean, Patient> patientMap, Map<PatientIdBean, Patient> patientFromCrossTableMap, Map<PatientIdBean, UnidentPatient> unidentPatientMap) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Long calculateInvoiceSum(Collection<Invoice> invoiceList) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Collection<? extends AbstractBaseEntity> createList(Collection<? extends AbstractBaseEntity> objList, TransactionTemplate tx, SessionFactory sessionFactory) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateList(Collection<? extends Object> objList, TransactionTemplate tx, SessionFactory sessionFactory) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeList(Collection<? extends Object> objList, TransactionTemplate tx, SessionFactory sessionFactory) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void createAudList(Collection<? extends AudEntityWithId> objList, TransactionTemplate tx, SessionFactory sessionFactory) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateAllFieldsList(Collection<? extends AbstractBaseEntity> objList, TransactionTemplate tx, SessionFactory sessionFactory, Map<String, Method> setters, Map<String, Method> getters) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateOrCreateAllFieldsList(List<MailGwLogEntry> objList, TransactionTemplate tx, SessionFactory sessionFactory, Map<String, Method> setters, Map<String, Method> getters) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public MethodKeeperBean getGettersAndSetters(Class class_) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PersonDAO getPersonDAOImplWS() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public GetSexCompositionPojo getErzlWsGateway() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public RevinfoDAO getRevinfoDAO() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SyncDAO getSyncDAO() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public BillStatisticsDAO getBillStatisticsDAO() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public MoDao getMoDao() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PractitionerDao getPractitionerDao() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setPractitionerDao(PractitionerDao practitionerDao) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public CommonAudDAOHibernate getCommonAudDAOHibernate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public InvoiceAudDAOHibernate getInvoiceAudDAOHibernate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public CostAudDAOHibernate getCostAudDAOHibernate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SessionFactory getSessionFactory() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SessionFactory getNsiSessionFactory() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SessionFactory getSmoSessionFactory() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SessionFactory getPractSessionFactory() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public TransactionTemplate getTx() {
        return tx;
    }

    @Override
    public TransactionTemplate getNsitx() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public TransactionTemplate getSmotx() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public TransactionTemplate getPracttx() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Boolean getIgnoreCentralSegmentAnswer() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Boolean getErzlQueryForDocumentDate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDbfOutputDir() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setDbfOutputDir(String dbfOutputDir) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getPmpSchemaName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getSmoSchemaName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getMailPrefix() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getMailServerUser() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getMailServerHost() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JavaMailSender getMailSender() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setIgnoreCentralSegmentAnswer(Boolean ignoreCentralSegmentAnswer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setErzlQueryForDocumentDate(Boolean erzlQueryForDocumentDate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public FindNsiEntries getFindNsiEntries() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PsevdoFindNsiEntry getPsevdoFindNsiEntry() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setPsevdoFindNsiEntry(PsevdoFindNsiEntry psevdoFindNsiEntry) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public NsiService getNsiService() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PsevdoFindNsiEntries getFindNsiEntriesPatients() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setPersonDAOImplWS(PersonDAO personDAOImplWS) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setErzlWsGateway(GetSexCompositionPojo erzlWsGateway) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setRevinfoDAO(RevinfoDAO revinfoDAO) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setSyncDAO(SyncDAO syncDAO) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setBillStatisticsDAO(BillStatisticsDAO billStatisticsDAO) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setMoDao(MoDao moDao) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setBillDAO(BillDAO2 billDAO) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setPractitionerDaoImpl(PractitionerDao practitionerDaoImpl) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setCommonAudDAOHibernate(CommonAudDAOHibernate commonAudDAOHibernate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setInvoiceAudDAOHibernate(InvoiceAudDAOHibernate invoiceAudDAOHibernate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setCostAudDAOHibernate(CostAudDAOHibernate costAudDAOHibernate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setNsiService(NsiService nsiService) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setFindNsiEntry(FindNsiEntry findNsiEntry) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setFindNsiEntries(FindNsiEntries findNsiEntries) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setFindNsiEntriesPatients(PsevdoFindNsiEntries findNsiEntriesPatients) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void setNsiSessionFactory(SessionFactory nsiSessionFactory) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setSmoSessionFactory(SessionFactory smoSessionFactory) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setPractSessionFactory(SessionFactory practSessionFactory) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setTx(TransactionTemplate tx) {
        this.tx = tx;
    }

    @Override
    public void setNsitx(TransactionTemplate nsitx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setSmotx(TransactionTemplate smotx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setPracttx(TransactionTemplate practtx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setSmoSchemaName(String smoSchemaName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setMailPrefix(String mailPrefix) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setMailServerUser(String mailServerUser) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setMailServerHost(String mailServerHost) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setMailSender(JavaMailSender mailSender) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public BillDAO2 getBillDAO() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PractitionerDao getPractitionerDaoImpl() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public FindNsiEntry getFindNsiEntry() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public BillStatistics getBillStatistics() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Future<GetRepPumpAttachedAgeSexResponse> getAttachedAgeSex(Date period, String moCode, List<BillStatisticsTime> billStatisticsTimeList, boolean cacheMode) throws NumberFormatException, DatatypeConfigurationException, PmpFeatureException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void flushLogMessage(boolean flushAll, BillStatistics billStatistics, List<BillStatisticsTime> billStatisticsTimeList) {
        recreateCommon.flushLogMessage(flushAll, billStatistics, billStatisticsTimeList);
    }

    @Override
    public boolean setLock(String featureName, String lockData, Integer lpuId, Date period, List<BillStatisticsTime> billStatisticsTimeList) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Bill updateBillOnStage7(Bill bill, TransactionTemplate tx, SessionFactory sessionFactory, boolean good, boolean thisIsZeroBill) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
