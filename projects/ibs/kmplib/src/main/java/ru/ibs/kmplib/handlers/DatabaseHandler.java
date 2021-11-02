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
import java.util.stream.Collectors;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ibs.kmplib.bean.Diagnosis;
import ru.ibs.kmplib.bean.KmpMedicamentPrescribe;
import ru.ibs.kmplib.bean.MvDictVersionsBean;
import ru.ibs.kmplib.bean.NsiMedicament;
import ru.ibs.kmplib.bean.NsiMkbDiagnosesBean;
import ru.ibs.kmplib.bean.RangeUpdateBean;
import ru.ibs.kmplib.bean.UpdateBean;
import ru.ibs.kmplib.request.bean.CacheKey;
import ru.ibs.kmplib.utils.DbUtils;
import static ru.ibs.kmplib.utils.DbUtils.ex;
import ru.ibs.kmplib.utils.Utils;

/**
 *
 * @author me
 */
public class DatabaseHandler {

	private org.apache.tomcat.jdbc.pool.DataSource kmpDataSource; // DataSource для схемы КМП!
	private org.apache.tomcat.jdbc.pool.DataSource nsiDataSource; // DataSource для схемы НСИ!
	private static final Logger log = LoggerFactory.getLogger("kmp");

	/**
	 * Можно создать DataSource при инициализации!
	 *
	 * @param kmpPropsPrefix
	 * @param nsiPropsPrefix
	 */
	public DatabaseHandler(String kmpPropsPrefix, String nsiPropsPrefix) {
		try {
			kmpDataSource = createDataSource(kmpPropsPrefix);
			nsiDataSource = createDataSource(nsiPropsPrefix);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Можно передать имеющийся DataSource!
	 *
	 * @param kmpDataSource
	 * @param nsiDataSource
	 */
	public DatabaseHandler(DataSource kmpDataSource, DataSource nsiDataSource) {
		this.kmpDataSource = kmpDataSource;
		this.nsiDataSource = nsiDataSource;
	}

	/**
	 * Функция создания DataSource!
	 *
	 * @param propsPrefix - префикс настроек
	 * @return - DataSource
	 */
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

	/**
	 * Это полная логика получения данных БД вместе со справочными значениями!
	 *
	 * @return - данные БД
	 */
	public List<KmpMedicamentPrescribe> handleKmpMedicamentPrescribeList() {
		try (Connection kmpConnection = kmpDataSource.getConnection();
			Connection nsiConnection = nsiDataSource.getConnection();) {
			synchronized (mvDictVersionsMap) {
				synchronized (mkb10Map) {
					synchronized (medicamentMap) {
						List<KmpMedicamentPrescribe> kmpMedicamentPrescribeList = getKmpMedicamentPrescribeList(kmpConnection);
						Set<java.util.Date> periodSet = kmpMedicamentPrescribeList.stream().map(KmpMedicamentPrescribe::getTruncatedDateInj).collect(Collectors.toSet()); // Находим набор периодов у KmpMedicamentPrescribe!
						Set<Date> periodCollection = periodSet.stream().filter(date -> !mvDictVersionsMap.containsKey(new CacheKey<>(date))).map(date -> new java.sql.Date(date.getTime())).collect(Collectors.toSet()); // По данному набору получаем набор периодов, которые мы не получали ранее, то есть те, которые за'cache'ированы!
						List<MvDictVersionsBean> mvDictVersions = getMvDictVersions(nsiConnection, periodCollection); // По набору периодов, которые мы не получали ранее, лезем в БД за наборами версий справочников!
						mvDictVersionsMap.putAll(mvDictVersions.stream().collect(Collectors.toMap(obj -> new CacheKey<>(obj.getPeriod()), obj -> obj))); // Пополняем cache, если это необходимо!
						Set<String> existingMkb10Versions = mkb10Map.keySet().stream().map(CacheKey::getValue).collect(Collectors.toSet()); // За'cache'ированные версии справочников МКБ-10!
						Set<String> existingMedicamentMapVersions = medicamentMap.keySet().stream().map(CacheKey::getValue).collect(Collectors.toSet()); // За'cache'ированные версии справочников медикаментов!
						Set<String> mkb10VersionIdSet = mvDictVersions.stream().map(MvDictVersionsBean::getMkb10Ver).filter(ver -> !existingMkb10Versions.contains(ver)).collect(Collectors.toSet()); // Нам нужны только версии справочников МКБ-10, которых ещё нет в cache'е!
						Set<String> medicamentVersionIdSet = mvDictVersions.stream().map(MvDictVersionsBean::getMedicamentVer).filter(ver -> !existingMedicamentMapVersions.contains(ver)).collect(Collectors.toSet()); // Нам нужны только версии справочников медикаментов, которых ещё нет в cache'е!
						List<NsiMkbDiagnosesBean> mkb10BeanList = getMkb10(nsiConnection, mkb10VersionIdSet); // Получаем версии справочников МКБ-10, которых ещё нет в cache'е из БД!
						List<NsiMedicament> medicamentBeanList = getMedicamentDictionary(nsiConnection, medicamentVersionIdSet); // Получаем версии справочников медикаментов, которых ещё нет в cache'е из БД!
						mkb10Map.putAll(mkb10BeanList.stream().collect(Collectors.groupingBy(bean -> new CacheKey<>(bean.getVersionId()), Collectors.collectingAndThen(Collectors.toList(), list -> list.stream().collect(Collectors.toMap(NsiMkbDiagnosesBean::getCode, NsiMkbDiagnosesBean::getName)))))); // Дополняем cache, если это необходимо!
						medicamentMap.putAll(medicamentBeanList.stream().collect(Collectors.groupingBy(bean -> new CacheKey<>(bean.getVersionId()), Collectors.collectingAndThen(Collectors.toList(), list -> list.stream().collect(Collectors.toMap(NsiMedicament::getCode, NsiMedicament::getName)))))); // Дополняем cache, если это необходимо!
						kmpMedicamentPrescribeList.forEach(bean -> { // Заполняем имя медикамента и имя диагноза для всего набора данных в БД!
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

	private static final int secondsToKeepCache = 60 * 60 * 2; // 2 hours! Время хранения cache'а справочников!
	private static final long millisecondsMultiplier = 1000; // Множитель для преобразования в миллисекунды!
	private static final long timeOffset = 20 * 1000; // Чистим cache'и чуть позже, чтобы гарантированно появились данные, которые надо отбросить!

	public long cleanCache() {
		synchronized (mvDictVersionsMap) {
			synchronized (mkb10Map) {
				synchronized (medicamentMap) {
					java.util.Date now = new java.util.Date(); // Фиксируем текущее время!
					List<java.util.Date> dateList = Arrays.asList(cleanCache(now, mvDictVersionsMap.entrySet().iterator()), cleanCache(now, mkb10Map.entrySet().iterator()), cleanCache(now, medicamentMap.entrySet().iterator())).stream().filter(date -> date != null).collect(Collectors.toList());
					if (!dateList.isEmpty()) {
						Collections.sort(dateList); // Определяем время, сколько ждать перед тем, как в cache'е что-то устареет.
						long waitTime = (long) secondsToKeepCache * millisecondsMultiplier - (now.getTime() - dateList.get(0).getTime()); // Если cache свежий, то ждать долго, а если старый, то мало!
						return waitTime > 0L ? Math.min(waitTime + timeOffset, (long) secondsToKeepCache * millisecondsMultiplier) : timeOffset;
					} else {
						return (long) secondsToKeepCache * millisecondsMultiplier; // Если cache пустой, то ждать время жизни cache'а!
					}
				}
			}
		}
	}

	/**
	 * Функция чистки cache'а справочников. Для ускорения мы какое-то время
	 * храним данные справочников в памяти. А зачем каждый раз лазить за ними в
	 * БД?
	 *
	 * @param <T> - тип данных справочника
	 * @param <K> - тип данных ключевого параметра данных справочника
	 * @param now - текущая дата, зафиксированная в прошлом
	 * @param iterator - iterator справочных данных
	 * @return - минимальная дата данных ключевого поля в cache'е.
	 */
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

	/**
	 * Нужно ли начать обработку данных?
	 *
	 * @return - id'шники в БД записей, в которых мы будем отмечать даты начала
	 * и конца данного акта обработки данных!
	 */
	public List<Long> isItTriggered() {
		final String sql = "select id from KMP_PRECALC_TABLE_LOG where PROC_NAME='UPDATE_MEDICAMENT_PRESCRIBE_VIA_SCREENING_SERVICE' and PROC_END_DATE_TIME is null";
		try (Connection kmpConnection = kmpDataSource.getConnection()) {
			List<Long> idList = ex(kmpConnection, sql, statement -> {
			}, resultSet -> {
				try {
					return resultSet.getLong(1);
				} catch (SQLException ex) {
					throw new RuntimeException(ex);
				}
			});
			updateStartTime(kmpConnection, idList);
			return idList;
		} catch (SQLException sqlex) {
			throw new RuntimeException(sqlex);
		}
	}

	/**
	 * Отмечаем дату начала обработки данных!
	 *
	 * @param kmpConnection - соединение к БД КМП
	 * @param idList - id'шники в БД, по которым мы update'им
	 */
	private void updateStartTime(Connection kmpConnection, List<Long> idList) {
		updateTime(kmpConnection, "PROC_START_DATE_TIME", idList);
	}

	/**
	 * Отмечаем дату завершения обработки данных!
	 *
	 * @param idList - id'шники в БД, по которым мы update'им
	 */
	public void updateEndTime(List<Long> idList) {
		try (Connection kmpConnection = kmpDataSource.getConnection()) {
			updateTime(kmpConnection, "PROC_END_DATE_TIME", idList);
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Функция обновления даты начала обработки или её конца.
	 *
	 * @param kmpConnection - соединение к БД КМП
	 * @param timeColumn - название колонки, которую мы update'им
	 * @param idList - id'шники в БД, по которым мы update'им
	 */
	private void updateTime(Connection kmpConnection, String timeColumn, List<Long> idList) {
		if (!idList.isEmpty()) {
			final String sql = "update KMP_PRECALC_TABLE_LOG set " + timeColumn + "=? where id in(" + idList.stream().map(obj -> "?").reduce((str1, str2) -> str1 + "," + str2).get() + ")";
			DbUtils.ins(kmpConnection, sql, statement -> {
				try {
					statement.setTimestamp(1, new Timestamp(new java.util.Date().getTime()));
					for (int i = 1; i <= idList.size(); i++) {
						statement.setLong(i + 1, idList.get(i - 1));
					}
				} catch (SQLException ex) {
					throw new RuntimeException(ex);
				}
			});
		}
	}

	private static final int SLICE = 16384; // Размер транзакции для updateKmpMedicamentPrescribe2!

	/**
	 * Функция записи alert'ов в БД диапазонно-точечным методом! Работает
	 * быстро!
	 *
	 * @param updateBeanList
	 */
	public void updateKmpMedicamentPrescribe2(List<UpdateBean> updateBeanList) {
		if (!updateBeanList.isEmpty()) {
			int i = 0;
			for (i = 0; i < updateBeanList.size(); i++) {
				UpdateBean updateBean = updateBeanList.get(i);
				if (updateBean instanceof RangeUpdateBean) { // В начале update'им массово данные по диапазонам! Это достаточно быстро и update'ится сразу много строк!
					RangeUpdateBean rangeUpdateBean = (RangeUpdateBean) updateBean;
					try (Connection kmpConnection = kmpDataSource.getConnection()) {
						String sql = "update kmp_medicament_prescribe set alert=? where id>=? and id<=? and alert is null";
						DbUtils.ins(kmpConnection, sql, statement -> {
							try {
								statement.setString(1, rangeUpdateBean.getAlert());
								statement.setLong(2, rangeUpdateBean.getId());
								statement.setLong(3, rangeUpdateBean.getId2());
							} catch (SQLException ex) {
								throw new RuntimeException(ex);
							}
						});
					} catch (SQLException ex) {
						throw new RuntimeException(ex);
					}
				} else {
					break;
				}
			} // После этого делаем точечные update'ы, то есть это уникальные внедиапазонные вкрапления alert'ов в общей последовательности! По идее, их не должно быть много!
			ArrayList<UpdateBean> updateBeanSubList = new ArrayList<>(updateBeanList.subList(i, updateBeanList.size()));
			for (List<UpdateBean> updateBeanSubList2 : Utils.partition(updateBeanSubList, SLICE)) {
				try (Connection kmpConnection = kmpDataSource.getConnection()) {
					try {
						kmpConnection.setAutoCommit(false);
						Statement statement = kmpConnection.createStatement();
						for (UpdateBean updateBean : updateBeanSubList2) {
							String sql = "update kmp_medicament_prescribe set alert='" + updateBean.getAlert() + "' where id=" + updateBean.getId().toString();
							statement.addBatch(sql);
						}
						int[] executeBatch = statement.executeBatch();
						kmpConnection.commit();
					} catch (SQLException e) {
						kmpConnection.rollback();
						throw new RuntimeException(e);
					}
				} catch (SQLException sqlex) {
					throw new RuntimeException(sqlex);
				}
			}
		}
	}

	/**
	 * Функция записи alert'ов в БД точечным методом! Работает долго!
	 *
	 * @param kmpMedicamentPrescribeList
	 * @deprecated
	 */
	@Deprecated
	public void updateKmpMedicamentPrescribe(List<KmpMedicamentPrescribe> kmpMedicamentPrescribeList) {
		if (!kmpMedicamentPrescribeList.isEmpty()) {
			try (Connection kmpConnection = kmpDataSource.getConnection()) {
				try {
					kmpConnection.setAutoCommit(false);
					Statement statement = kmpConnection.createStatement();
					for (KmpMedicamentPrescribe kmpMedicamentPrescribe : kmpMedicamentPrescribeList) {
						String sql = "update kmp_medicament_prescribe set alert='" + kmpMedicamentPrescribe.getAlert() + "' where id=" + kmpMedicamentPrescribe.getId().toString();
						statement.addBatch(sql);
					}
					int[] executeBatch = statement.executeBatch();
					kmpConnection.commit();
				} catch (SQLException e) {
					kmpConnection.rollback();
					throw new RuntimeException(e);
				}
			} catch (SQLException sqlex) {
				throw new RuntimeException(sqlex);
			}
		}
	}

	private static final int LIMIT = 1048576; // Размер пачки получаемых данных! Пачка должна быть достаточно большой, так как занимает мало места в памяти и позволяет сократить количество обращений к внешнему сервису, а ещё можно быстрее про'update'ить БД!

	/**
	 * Функция получения данных из БД! Основной запрос!
	 *
	 * @param kmpConnection
	 * @return
	 * @throws SQLException
	 */
	private List<KmpMedicamentPrescribe> getKmpMedicamentPrescribeList(Connection kmpConnection) throws SQLException {
		return ex(kmpConnection, "select id,sid,date_inj,ds from kmp_medicament_prescribe where alert is null", statement -> {
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

	/**
	 * Функция получения mvDictVersions! Получаем версии справочников по набору
	 * периодов, содержащихся в List<KmpMedicamentPrescribe>!
	 *
	 * @param nsiConnection - соединения к НСИ
	 * @param periodCollection - набор периодов, содержащихся в
	 * List<KmpMedicamentPrescribe>!
	 * @return - версии справочников для этих периодов
	 */
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

	/**
	 * Получить справочники МКБ-10 для набора versionId, полученных из набора
	 * периодов, содержащихся в List<KmpMedicamentPrescribe>!
	 *
	 * @param connection - соединения к НСИ
	 * @param versionIdCollection - набор versionId, полученный из набора
	 * периодов, содержащихся в List<KmpMedicamentPrescribe>!
	 * @return - набор справочников МКБ-10 для разных версий или периодов!
	 * Уникальных версий справочников может быть меньше, чем периодов, если в
	 * течение некоторых периодов справочник не менялся!
	 */
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

	/**
	 * Получить справочники медикаментов для набора versionId, полученных из
	 * набора периодов, содержащихся в List<KmpMedicamentPrescribe>!
	 *
	 * @param connection - соединения к НСИ
	 * @param versionIdCollection - набор versionId, полученный из набора
	 * периодов, содержащихся в List<KmpMedicamentPrescribe>!
	 * @return - набор справочников медикаментов для разных версий или периодов!
	 * Уникальных версий справочников может быть меньше, чем периодов, если в
	 * течение некоторых периодов справочник не менялся!
	 */
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
