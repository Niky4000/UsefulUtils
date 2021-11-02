package ru.ibs.kmplib.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.tomcat.jdbc.pool.DataSource;
import ru.ibs.kmplib.bean.Diagnosis;
import ru.ibs.kmplib.bean.DrugAlertBean;
import ru.ibs.kmplib.bean.KmpMedicamentPrescribe;
import ru.ibs.kmplib.request.bean.Diseases;
import ru.ibs.kmplib.request.bean.Drugs;
import ru.ibs.kmplib.request.bean.Options;
import ru.ibs.kmplib.request.bean.ScreeningBean;
import ru.ibs.kmplib.response.bean.ScreeningResponseBean;
import ru.ibs.kmplib.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ibs.kmplib.bean.RangeUpdateBean;
import ru.ibs.kmplib.bean.UpdateBean;

/**
 *
 * @author me
 */
public class MainHandler {

	private final String url;
	private final DataSource pmpDataSource;
	private final DataSource nsiDataSource;
	private final DatabaseHandler databaseHandler;
	private final HttpHandler httpHandler = new HttpHandler();
	private static final Logger log = LoggerFactory.getLogger("kmp");

	public MainHandler(String url, DataSource pmpDataSource, DataSource nsiDataSource) {
		this.url = url;
		this.pmpDataSource = pmpDataSource;
		this.nsiDataSource = nsiDataSource;
		databaseHandler = new DatabaseHandler(pmpDataSource, nsiDataSource);
	}

	private static final int TIME_TO_WAIT = 20 * 1000; // 20 seconds

	public void init() {
		log.info("Hello from KmpExternalServiceCallStart!");
		Thread mainThread = new Thread(() -> {
			while (true) {
				try {
					List<Long> idList = databaseHandler.isItTriggered();
					if (!idList.isEmpty()) {
						handle();
					}
					databaseHandler.updateEndTime(idList);
				} catch (Exception ex) {
					log.error("mainThread Exception!", ex);
				}
				waitSomeTime(TIME_TO_WAIT);
			}
		});
		mainThread.setName("MainKmpHandlingThread");
		mainThread.start();
		Thread cacheThread = new Thread(() -> {
			while (true) {
				waitSomeTime(databaseHandler.cleanCache());
			}
		});
		cacheThread.setName("MainKmpCacheCleaningThread");
		cacheThread.start();
		joinToThread(mainThread);
		joinToThread(cacheThread);
	}

	private void waitSomeTime(long timeToWait) {
		try {
			Thread.sleep(timeToWait);
		} catch (InterruptedException ex) {
			log.error("cacheThread InterruptedException!", ex);
		}
	}

	private static void joinToThread(Thread thread) {
		while (true) {
			try {
				thread.join();
				break;
			} catch (InterruptedException ex) {
				log.error("InterruptedException in joinToThread!", ex);
			}
		}
	}

	private void handle() {
		List<KmpMedicamentPrescribe> kmpMedicamentPrescribeList;
		do {
			kmpMedicamentPrescribeList = databaseHandler.handleKmpMedicamentPrescribeList();
			log.info("kmpMedicamentPrescribeList size is " + kmpMedicamentPrescribeList.size() + "!");
			groupIt(kmpMedicamentPrescribeList, databaseHandler);
		} while (!kmpMedicamentPrescribeList.isEmpty());
	}

	private static final int SLICE = 16384;

	private void groupIt(List<KmpMedicamentPrescribe> kmpMedicamentPrescribeList, DatabaseHandler databaseHandler) {
		if (!kmpMedicamentPrescribeList.isEmpty()) {
			List<ScreeningBean> screeningBeanList = createScreeningBean(kmpMedicamentPrescribeList);
			List<ScreeningResponseBean> screeningResponseBeanList = screeningBeanList.stream().map(screeningBean -> httpHandler.sendPost(url, screeningBean, ScreeningResponseBean.class)).collect(Collectors.toList());
			log.info("screeningResponseBeanList size is " + screeningResponseBeanList.size() + "!");
			Map<Diagnosis, Map<String, Set<String>>> allertMap = screeningResponseBeanList.stream().filter(responseBean -> responseBean.getDiseaseContraindications() != null && responseBean.getDiseaseContraindications().getItemsList() != null && !responseBean.getDiseaseContraindications().getItemsList().isEmpty())
				.flatMap(responseBean -> responseBean.getDiseaseContraindications().getItemsList().stream()).filter(item -> item.getAlert() != null && item.getDrugsList() != null && !item.getDrugsList().isEmpty() && item.getDiseasesList() != null && item.getDiseasesList().size() == 1)
				.collect(Collectors.groupingBy(item -> new Diagnosis(item.getDiseasesList().get(0).getCode()), Collectors.collectingAndThen(Collectors.toList(), ff -> ff.stream().flatMap(item -> item.getDrugsList().stream().map(drug -> new DrugAlertBean(drug, item.getAlert())))
				.collect(Collectors.groupingBy(DrugAlertBean::getCode, Collectors.collectingAndThen(Collectors.toList(), ff2 -> ff2.stream().map(DrugAlertBean::getAlert).collect(Collectors.toSet())))))));
			log.info("allertMap size is " + screeningResponseBeanList.size() + "!");
			kmpMedicamentPrescribeList.forEach(kmp -> {
				String alert = Optional.ofNullable(allertMap.get(kmp.getDiagnosis())).map(map2 -> map2.get(kmp.getSid())).filter(alertSet -> alertSet != null && !alertSet.isEmpty()).map(alertSet -> alertSet.stream().sorted().reduce((str1, str2) -> str1 + "\r\n" + str2).get()).orElse("Нет");
				kmp.setAlert(alert);
			});
			List<UpdateBean> updateBeanList = getUpdateBeanList(kmpMedicamentPrescribeList);
			databaseHandler.updateKmpMedicamentPrescribe2(updateBeanList);
//			for (List<KmpMedicamentPrescribe> kmpMedicamentPrescribeSubList : Utils.partition(kmpMedicamentPrescribeList, SLICE)) {
//				databaseHandler.updateKmpMedicamentPrescribe(kmpMedicamentPrescribeSubList);
//			}
			log.info("kmpMedicamentPrescribeSubList was updated!");
		}
	}

	List<UpdateBean> getUpdateBeanList(List<KmpMedicamentPrescribe> kmpMedicamentPrescribeList) {
		int j = 1;
		List<UpdateBean> updateBeanList = new ArrayList<>();
		KmpMedicamentPrescribe lastKmpMedicamentPrescribe = kmpMedicamentPrescribeList.get(0);
		String lastAllert = lastKmpMedicamentPrescribe.getAlert();
		for (Integer i = 1; i < kmpMedicamentPrescribeList.size(); i++) {
			KmpMedicamentPrescribe kmpMedicamentPrescribe = kmpMedicamentPrescribeList.get(i);
			if (!lastAllert.equals(kmpMedicamentPrescribe.getAlert()) || i.equals(kmpMedicamentPrescribeList.size() - 1)) {
				if (j > 1 || (i.equals(kmpMedicamentPrescribeList.size() - 1) && lastAllert.equals(kmpMedicamentPrescribeList.get(i).getAlert()))) {
					updateBeanList.add(new RangeUpdateBean(lastKmpMedicamentPrescribe.getId(), kmpMedicamentPrescribeList.get(!i.equals(kmpMedicamentPrescribeList.size() - 1) ? i - 1 : i).getId(), lastAllert));
				} else {
					updateBeanList.add(new UpdateBean(lastKmpMedicamentPrescribe.getId(), lastAllert));
					if (i.equals(kmpMedicamentPrescribeList.size() - 1)) {
						updateBeanList.add(new UpdateBean(kmpMedicamentPrescribeList.get(i).getId(), kmpMedicamentPrescribeList.get(i).getAlert()));
					}
				}
				j = 1;
				lastKmpMedicamentPrescribe = kmpMedicamentPrescribe;
				lastAllert = kmpMedicamentPrescribe.getAlert();
			} else {
				j++;
			}
		}
		Collections.sort(updateBeanList);
		return updateBeanList;
	}

	private static final int SCREENING_SLICE = 2048;

	private List<ScreeningBean> createScreeningBean(List<KmpMedicamentPrescribe> kmpMedicamentPrescribeList) {
		Map<Diagnosis, List<Drugs>> map = kmpMedicamentPrescribeList.stream().collect(Collectors.groupingBy(KmpMedicamentPrescribe::getDiagnosis, Collectors.collectingAndThen(Collectors.toList(), ff -> ff.stream().map(kmp -> new Drugs(null, true, "DispensableDrug", kmp.getSid(), kmp.getName())).collect(Collectors.toList()))));
		List<ScreeningBean> screeningBeanList = map.entrySet().stream().flatMap(entry -> {
			List<List<Drugs>> partition = Utils.partition(entry.getValue(), SCREENING_SLICE);
			return partition.stream().map(part -> {
				ScreeningBean screeningBean = new ScreeningBean();
				screeningBean.setScreeningTypes("DiseaseContraindications");
				screeningBean.setDrugsList(part);
				screeningBean.setDiseasesList(Arrays.asList(new Diseases(true, "ICD10CM", entry.getKey().getDiagnosisCode(), entry.getKey().getDiagnosisName())));
				screeningBean.setOptions(new Options(true, true, null));
				return screeningBean;
			});
		}).collect(Collectors.toList());
		return screeningBeanList;
	}
}
