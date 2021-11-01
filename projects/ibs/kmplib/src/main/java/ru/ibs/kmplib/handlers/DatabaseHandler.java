package ru.ibs.kmplib.handlers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import ru.ibs.kmplib.bean.Diagnosis;
import ru.ibs.kmplib.bean.KmpMedicamentPrescribe;
import ru.ibs.kmplib.bean.MvDictVersionsBean;
import ru.ibs.kmplib.bean.NsiMedicament;
import ru.ibs.kmplib.bean.NsiMkbDiagnosesBean;
import ru.ibs.kmplib.request.bean.CacheKey;
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
			pmpDataSource = createDataSource(pmpPropsPrefix);
			nsiDataSource = createDataSource(nsiPropsPrefix);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public DatabaseHandler(DataSource pmpDataSource, DataSource nsiDataSource) {
		this.pmpDataSource = pmpDataSource;
		this.nsiDataSource = nsiDataSource;
	}

	public static org.apache.tomcat.jdbc.pool.DataSource createDataSource(String propsPrefix) {
		try {
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
		} catch (FileNotFoundException ex) {
			throw new RuntimeException(ex);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	private final Map<CacheKey<java.util.Date>, MvDictVersionsBean> mvDictVersionsMap = new HashMap<>();
	private final Map<CacheKey<String>, Map<String, String>> mkb10Map = new HashMap<>();
	private final Map<CacheKey<String>, Map<String, String>> medicamentMap = new HashMap<>();

	public List<KmpMedicamentPrescribe> handleKmpMedicamentPrescribeList() {
		try (Connection pmpConnection = pmpDataSource.getConnection();
			Connection nsiConnection = nsiDataSource.getConnection();) {
			synchronized (mvDictVersionsMap) {
				synchronized (mkb10Map) {
					synchronized (medicamentMap) {
						List<KmpMedicamentPrescribe> kmpMedicamentPrescribeList = getKmpMedicamentPrescribeList(pmpConnection);
						Set<java.util.Date> periodSet = kmpMedicamentPrescribeList.stream().map(KmpMedicamentPrescribe::getTruncatedDateInj).collect(Collectors.toSet());
						Set<Date> periodCollection = periodSet.stream().filter(date -> !mvDictVersionsMap.containsKey(new CacheKey<>(date))).map(date -> new java.sql.Date(date.getTime())).collect(Collectors.toSet());
						List<MvDictVersionsBean> mvDictVersions = getMvDictVersions(nsiConnection, periodCollection);
						mvDictVersionsMap.putAll(mvDictVersions.stream().collect(Collectors.toMap(obj -> new CacheKey<>(obj.getPeriod()), obj -> obj)));
						Set<String> mkb10VersionIdSet = mvDictVersions.stream().map(MvDictVersionsBean::getMkb10Ver).collect(Collectors.toSet());
						Set<String> medicamentVersionIdSet = mvDictVersions.stream().map(MvDictVersionsBean::getMedicamentVer).collect(Collectors.toSet());
						List<NsiMkbDiagnosesBean> mkb10BeanList = getMkb10(nsiConnection, mkb10VersionIdSet);
						List<NsiMedicament> medicamentBeanList = getMedicamentDictionary(nsiConnection, medicamentVersionIdSet);
						mkb10Map.putAll(mkb10BeanList.stream().collect(Collectors.groupingBy(bean -> new CacheKey<>(bean.getVersionId()), Collectors.collectingAndThen(Collectors.toList(), list -> list.stream().collect(Collectors.toMap(NsiMkbDiagnosesBean::getCode, NsiMkbDiagnosesBean::getName))))));
						medicamentMap.putAll(medicamentBeanList.stream().collect(Collectors.groupingBy(bean -> new CacheKey<>(bean.getVersionId()), Collectors.collectingAndThen(Collectors.toList(), list -> list.stream().collect(Collectors.toMap(NsiMedicament::getCode, NsiMedicament::getName))))));
						kmpMedicamentPrescribeList.forEach(bean -> {
							bean.setName(Optional.ofNullable(mvDictVersionsMap.get(new CacheKey<>(bean.getTruncatedDateInj()))).map(MvDictVersionsBean::getMedicamentVer).map(versionId -> medicamentMap.get(new CacheKey<>(versionId))).map(map -> map.get(bean.getSid())).orElse(null));
							Optional.ofNullable(mvDictVersionsMap.get(new CacheKey<>(bean.getTruncatedDateInj()))).map(MvDictVersionsBean::getMkb10Ver).map(versionId -> mkb10Map.get(new CacheKey<>(versionId))).map(map -> map.get(bean.getDiagnosis().getDiagnosisCode())).ifPresent(diagnosisName -> bean.getDiagnosis().setDiagnosisName(diagnosisName));
						});
						return kmpMedicamentPrescribeList;
					}
				}
			}
		} catch (SQLException sqlex) {
			throw new RuntimeException(sqlex);
		}
	}

	private static final int secondsToKeepCache = 60 * 60 * 2; // 2 hours!
	private static final long millisecondsMultiplier = 1000;

	public long cleanCache() {
		synchronized (mvDictVersionsMap) {
			synchronized (mkb10Map) {
				synchronized (medicamentMap) {
					java.util.Date now = new java.util.Date();
					List<java.util.Date> dateList = Arrays.asList(cleanCache(now, mvDictVersionsMap.entrySet().iterator()), cleanCache(now, mkb10Map.entrySet().iterator()), cleanCache(now, medicamentMap.entrySet().iterator())).stream().filter(date -> date != null).collect(Collectors.toList());
					if (!dateList.isEmpty()) {
						Collections.sort(dateList);
						long waitTime = (long) secondsToKeepCache * millisecondsMultiplier - (now.getTime() - dateList.get(0).getTime());
						return waitTime > 0L ? waitTime : 0L;
					} else {
						return (long) secondsToKeepCache * millisecondsMultiplier;
					}
				}
			}
		}
	}

	private <T, K> java.util.Date cleanCache(java.util.Date now, Iterator<Map.Entry<CacheKey<K>, T>> iterator) {
		java.util.Date minimumCreated = null;
		while (iterator.hasNext()) {
			java.util.Date created = iterator.next().getKey().getCreated();
			if (DateUtils.addSeconds(created, secondsToKeepCache).before(now)) {
				iterator.remove();
			} else if (minimumCreated == null || created.before(minimumCreated)) {
				minimumCreated = created;
			}
		}
		return minimumCreated;
	}

	public List<Long> isItTriggered() {
		final String sql = "select id from KMP_PRECALC_TABLE_LOG where PROC_NAME='UPDATE_MEDICAMENT_PRESCRIBE_VIA_SCREENING_SERVICE' and PROC_END_DATE_TIME is null";
		try (Connection pmpConnection = pmpDataSource.getConnection()) {
			List<Long> idList = ex(pmpConnection, sql, statement -> {
			}, resultSet -> {
				try {
					return resultSet.getLong(1);
				} catch (SQLException ex) {
					throw new RuntimeException(ex);
				}
			});
			updateStartTime(pmpConnection, idList);
			return idList;
		} catch (SQLException sqlex) {
			throw new RuntimeException(sqlex);
		}
	}

	private void updateStartTime(Connection pmpConnection, List<Long> idList) {
		updateTime(pmpConnection, "PROC_START_DATE_TIME", idList);
	}

	public void updateEndTime(List<Long> idList) {
		try (Connection pmpConnection = pmpDataSource.getConnection()) {
			updateTime(pmpConnection, "PROC_END_DATE_TIME", idList);
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	private void updateTime(Connection pmpConnection, String timeColumn, List<Long> idList) {
		final String sql = "update KMP_PRECALC_TABLE_LOG set " + timeColumn + "=? where id in(" + idList.stream().map(obj -> "?").reduce((str1, str2) -> str1 + "," + str2).get() + ")";
		DbUtils.ins(pmpConnection, sql, statement -> {
			try {
				statement.setTimestamp(1, new Timestamp(new java.util.Date().getTime()));
				for (int i = 1; i <= idList.size(); i++) {
					statement.setLong(i + 1, idList.get(i));
				}
			} catch (SQLException ex) {
				throw new RuntimeException(ex);
			}
		});
	}

	public void updateKmpMedicamentPrescribe(List<KmpMedicamentPrescribe> kmpMedicamentPrescribeList) {
		try (Connection pmpConnection = pmpDataSource.getConnection()) {
			try {
				pmpConnection.setAutoCommit(false);
				Statement statement = pmpConnection.createStatement();
				for (KmpMedicamentPrescribe kmpMedicamentPrescribe : kmpMedicamentPrescribeList) {
					String sql = "update kmp_medicament_prescribe set alert='" + kmpMedicamentPrescribe.getAlert() + "' where id=" + kmpMedicamentPrescribe.getId().toString();
					statement.addBatch(sql);
				}
				int[] executeBatch = statement.executeBatch();
				pmpConnection.commit();
			} catch (SQLException e) {
				pmpConnection.rollback();
				throw new RuntimeException(e);
			}
		} catch (SQLException sqlex) {
			throw new RuntimeException(sqlex);
		}
	}

	private static final int LIMIT = 1048576;

	private List<KmpMedicamentPrescribe> getKmpMedicamentPrescribeList(Connection pmpConnection) throws SQLException {
		return ex(pmpConnection, "select id,sid,date_inj,ds from kmp_medicament_prescribe where alert is null", statement -> {
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
		if (!versionIdCollection.isEmpty()) {
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
		} else {
			return new ArrayList<>(1);
		}
	}

	// medicament
	private List<NsiMedicament> getMedicamentDictionary(final Connection connection, Collection<String> versionIdCollection) {
		if (!versionIdCollection.isEmpty()) {
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
		} else {
			return new ArrayList<>(1);
		}
	}
}
