package ru.ibs.kmplib.handlers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import ru.ibs.kmplib.bean.Diagnosis;
import ru.ibs.kmplib.bean.KmpMedicamentPrescribe;
import ru.ibs.kmplib.bean.MvDictVersionsBean;
import ru.ibs.kmplib.bean.NsiMedicament;
import ru.ibs.kmplib.bean.NsiMkbDiagnosesBean;
import ru.ibs.kmplib.utils.DbUtils;
import static ru.ibs.kmplib.utils.DbUtils.ex;

/**
 *
 * @author me
 */
public class DatabaseHandler {

	private org.apache.tomcat.jdbc.pool.DataSource pmpDataSource;
	private org.apache.tomcat.jdbc.pool.DataSource nsiDataSource;

	public DatabaseHandler(String pmpPropsPrefix, String nsiPropsPrefix) {
		try {
			pmpDataSource = createAndSetDataSource(pmpPropsPrefix);
			nsiDataSource = createAndSetDataSource(nsiPropsPrefix);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private org.apache.tomcat.jdbc.pool.DataSource createAndSetDataSource(String propsPrefix) throws FileNotFoundException, IOException {
		Properties properties = new Properties();
		properties.load(new FileInputStream(new File(System.getProperty("pmp.config.path"))));
		org.apache.tomcat.jdbc.pool.DataSource ds = new org.apache.tomcat.jdbc.pool.DataSource();
		PoolProperties p = new PoolProperties();
		p.setName(propsPrefix);
		p.setDriverClassName(properties.getProperty(propsPrefix + ".datasource.pooled.driverClass"));
		p.setUrl(properties.getProperty(propsPrefix + ".datasource.pooled.jdbcUrl"));
		p.setUsername(properties.getProperty(propsPrefix + ".datasource.pooled.user"));
		p.setPassword(properties.getProperty(propsPrefix + ".datasource.pooled.password"));
		p.setInitialSize(0);
		p.setMinIdle(0);
		ds.setPoolProperties(p);
		return ds;
	}

	public List<KmpMedicamentPrescribe> handleKmpMedicamentPrescribeList() {
		try (Connection pmpConnection = pmpDataSource.getConnection();
			Connection nsiConnection = nsiDataSource.getConnection();) {
			List<KmpMedicamentPrescribe> kmpMedicamentPrescribeList = getKmpMedicamentPrescribeList(pmpConnection);
			Set<java.util.Date> periodSet = kmpMedicamentPrescribeList.stream().map(KmpMedicamentPrescribe::getTruncatedDateInj).collect(Collectors.toSet());
			Set<Date> periodCollection = periodSet.stream().map(date -> new java.sql.Date(date.getTime())).collect(Collectors.toSet());
			List<MvDictVersionsBean> mvDictVersions = getMvDictVersions(nsiConnection, periodCollection);
			Map<java.util.Date, MvDictVersionsBean> mvDictVersionsMap = mvDictVersions.stream().collect(Collectors.toMap(MvDictVersionsBean::getPeriod, obj -> obj));
			Set<String> mkb10VersionIdSet = mvDictVersions.stream().map(MvDictVersionsBean::getMkb10Ver).collect(Collectors.toSet());
			Set<String> medicamentVersionIdSet = mvDictVersions.stream().map(MvDictVersionsBean::getMedicamentVer).collect(Collectors.toSet());
			List<NsiMkbDiagnosesBean> mkb10BeanList = getMkb10(nsiConnection, mkb10VersionIdSet);
			List<NsiMedicament> medicamentBeanList = getMedicamentDictionary(nsiConnection, medicamentVersionIdSet);
			Map<String, Map<String, String>> mkb10Map = mkb10BeanList.stream().collect(Collectors.groupingBy(NsiMkbDiagnosesBean::getVersionId, Collectors.collectingAndThen(Collectors.toList(), list -> list.stream().collect(Collectors.toMap(NsiMkbDiagnosesBean::getCode, NsiMkbDiagnosesBean::getName)))));
			Map<String, Map<String, String>> medicamentMap = medicamentBeanList.stream().collect(Collectors.groupingBy(NsiMedicament::getVersionId, Collectors.collectingAndThen(Collectors.toList(), list -> list.stream().collect(Collectors.toMap(NsiMedicament::getCode, NsiMedicament::getName)))));
			kmpMedicamentPrescribeList.forEach(bean -> {
				bean.setName(Optional.ofNullable(mvDictVersionsMap.get(bean.getTruncatedDateInj())).map(MvDictVersionsBean::getMedicamentVer).map(versionId -> medicamentMap.get(versionId)).map(map -> map.get(bean.getSid())).orElse(null));
				Optional.ofNullable(mvDictVersionsMap.get(bean.getTruncatedDateInj())).map(MvDictVersionsBean::getMkb10Ver).map(versionId -> mkb10Map.get(versionId)).map(map -> map.get(bean.getDiagnosis().getDiagnosisCode())).ifPresent(diagnosisName -> bean.getDiagnosis().setDiagnosisName(diagnosisName));
			});
			return kmpMedicamentPrescribeList;
		} catch (SQLException sqlex) {
			throw new RuntimeException(sqlex);
		}
	}

	public void updateKmpMedicamentPrescribe(List<KmpMedicamentPrescribe> kmpMedicamentPrescribeList) {
		String sql = "update kmp_medicament_prescribe set alert=? where id=?";
		try (Connection pmpConnection = pmpDataSource.getConnection()) {
			for (KmpMedicamentPrescribe kmpMedicamentPrescribe : kmpMedicamentPrescribeList) {
				DbUtils.ins(pmpConnection, sql, statement -> {
					try {
						int i = 0;
						statement.setString(++i, kmpMedicamentPrescribe.getAlert());
						statement.setLong(++i, kmpMedicamentPrescribe.getId());
					} catch (SQLException sqlex) {
						throw new RuntimeException(sqlex);
					}
				});
			}
		} catch (SQLException sqlex) {
			throw new RuntimeException(sqlex);
		}
	}

	private static final int LIMIT = 1048576;

	private List<KmpMedicamentPrescribe> getKmpMedicamentPrescribeList(Connection pmpConnection) throws SQLException {
		return DbUtils.ex(pmpConnection, "select id,sid,date_inj,ds from kmp_medicament_prescribe where alert is null", statement -> {
		}, resultSet -> {
			try {
				int i = 0;
				long id = resultSet.getLong(++i);
				String sid = resultSet.getString(++i);
				java.util.Date dateInj = Optional.ofNullable(resultSet.getDate(++i)).map(d -> new java.util.Date(d.getTime())).orElse(null);
				String ds = resultSet.getString(++i);
				return new KmpMedicamentPrescribe(id, sid, dateInj, new Diagnosis(ds));
			} catch (SQLException sqlex) {
				throw new RuntimeException(sqlex);
			}
		}, LIMIT);
	}

	private List<MvDictVersionsBean> getMvDictVersions(final Connection nsiConnection, Collection<java.sql.Date> periodCollection) {
		if (!periodCollection.isEmpty()) {
			return ex(nsiConnection, "select nsi.mkb10__ver,nsi.medicament_ver,nsi.period from mv_dict_versions nsi where nsi.period in(" + periodCollection.stream().map(p -> "?").reduce((s1, s2) -> s1 + "," + s2).get() + ")", statement -> {
				try {
					int i = 0;
					Iterator<Date> iterator = periodCollection.iterator();
					while (iterator.hasNext()) {
						statement.setDate(++i, iterator.next());
					}
				} catch (SQLException sqlex) {
					throw new RuntimeException(sqlex);
				}
			}, resultSet -> {
				try {
					int i = 0;
					String mkb10Ver = resultSet.getString(++i);
					String medicamentVer = resultSet.getString(++i);
					java.util.Date period = new java.util.Date(resultSet.getDate(++i).getTime());
					MvDictVersionsBean b = new MvDictVersionsBean(mkb10Ver, medicamentVer, period);
					return b;
				} catch (SQLException sqlex) {
					throw new RuntimeException(sqlex);
				}
			});
		} else {
			return new ArrayList<>(1);
		}
	}

	// mcb10
	private List<NsiMkbDiagnosesBean> getMkb10(final Connection connection, Collection<String> versionIdCollection) {
		return ex(connection, "select nsi.CODE,nsi.NAME,nsi.version_id from NSI_MKB_DIAGNOSES nsi where version_id in(" + versionIdCollection.stream().map(p -> "?").reduce((s1, s2) -> s1 + "," + s2).get() + ")", statement -> {
			try {
				int i = 0;
				Iterator<String> iterator = versionIdCollection.iterator();
				while (iterator.hasNext()) {
					statement.setString(++i, iterator.next());
				}
			} catch (SQLException sqlex) {
				throw new RuntimeException(sqlex);
			}
		}, resultSet -> {
			try {
				int i = 0;
				String code = resultSet.getString(++i);
				String name = resultSet.getString(++i);
				String versionId = resultSet.getString(++i);
				return new NsiMkbDiagnosesBean(code, name, versionId);
			} catch (SQLException sqlex) {
				throw new RuntimeException(sqlex);
			}
		});
	}

	// medicament
	private List<NsiMedicament> getMedicamentDictionary(final Connection connection, Collection<String> versionIdCollection) {
		return ex(connection, "select nsi.CODE,nsi.NAME,nsi.version_id from PMP_MEDICAMENT nsi where version_id in(" + versionIdCollection.stream().map(p -> "?").reduce((s1, s2) -> s1 + "," + s2).get() + ")", statement -> {
			try {
				int i = 0;
				Iterator<String> iterator = versionIdCollection.iterator();
				while (iterator.hasNext()) {
					statement.setString(++i, iterator.next());
				}
			} catch (SQLException sqlex) {
				throw new RuntimeException(sqlex);
			}
		}, resultSet -> {
			try {
				int i = 0;
				String code = resultSet.getString(++i);
				String name = resultSet.getString(++i);
				String versionId = resultSet.getString(++i);
				return new NsiMedicament(code, name, versionId);
			} catch (SQLException sqlex) {
				throw new RuntimeException(sqlex);
			}
		});
	}
}
