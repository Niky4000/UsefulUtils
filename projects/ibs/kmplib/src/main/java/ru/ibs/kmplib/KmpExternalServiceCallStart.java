package ru.ibs.kmplib;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import ru.ibs.kmplib.bean.Diagnosis;
import ru.ibs.kmplib.bean.DrugAlertBean;
import ru.ibs.kmplib.bean.KmpMedicamentPrescribe;
import ru.ibs.kmplib.handlers.DatabaseHandler;
import ru.ibs.kmplib.handlers.HttpHandler;
import ru.ibs.kmplib.handlers.MainHandler;
import ru.ibs.kmplib.request.bean.Allergies;
import ru.ibs.kmplib.request.bean.Diseases;
import ru.ibs.kmplib.request.bean.DopingAlerts;
import ru.ibs.kmplib.request.bean.Drugs;
import ru.ibs.kmplib.request.bean.Options;
import ru.ibs.kmplib.request.bean.Patient;
import ru.ibs.kmplib.request.bean.Schedule;
import ru.ibs.kmplib.request.bean.ScreeningBean;
import ru.ibs.kmplib.response.bean.ScreeningResponseBean;
import ru.ibs.kmplib.utils.Utils;

/**
 *
 * @author me
 */
public class KmpExternalServiceCallStart {

	private static final String url = "https://int.drugscreening.ru/v1/screening?access_token=3C042X3b0d033m3u1E0D1U291R0S1B0E";

	public static void main(String[] args) throws Exception {
		System.out.println("Hello from KmpExternalServiceCallStart!");
		KmpExternalServiceCallStart kmpExternalServiceCallStart = new KmpExternalServiceCallStart();
//		kmpExternalServiceCallStart.test();
//		kmpExternalServiceCallStart.parsingTest();
//		kmpExternalServiceCallStart.groupingTest();
//		kmpExternalServiceCallStart.testDataBase();
//		kmpExternalServiceCallStart.straightHttpTest();
		kmpExternalServiceCallStart.run();
	}

	public void run() {
//		MainHandler mainHandler = new MainHandler(url, DatabaseHandler.createDataSource("bulk-docs.updDs"), DatabaseHandler.createDataSource("bulk-docs.nsi"));
		MainHandler mainHandler = new MainHandler(url, null, null);
		mainHandler.init();
	}

	public void test() throws Exception {
		HttpHandler httpHandler = new HttpHandler();
		ScreeningBean createScreeningBean = createTestScreeningBean();
		System.out.println(httpHandler.marshall(createScreeningBean));
		ScreeningResponseBean screeningResponseBean = httpHandler.sendPost(url, createScreeningBean, ScreeningResponseBean.class);
	}

	public void parsingTest() throws Exception {
		HttpHandler httpHandler = new HttpHandler();
		byte[] readAllBytes = Files.readAllBytes(new File("/home/me/Downloads/15202/response.txt").toPath());
		ScreeningResponseBean screeningResponseBean = httpHandler.unmarshall(readAllBytes, ScreeningResponseBean.class);
		System.out.println("unmarshalling finished!");
	}

	public void straightHttpTest() {
		HttpHandler httpHandler = new HttpHandler();
		httpHandler.sendPost(url, "{\"ScreeningTypes\":\"DiseaseContraindications\",\"Drugs\":[{\"Screen\":true,\"Type\":\"DispensableDrug\",\"Code\":\"DD0014452\",\"Name\":\"Винкристин-Тева р-р для в/в введ. 1мг/мл\"}],\"Diseases\":[{\"Screen\":true,\"Type\":\"ICD10CM\",\"Code\":\"C83.7\",\"Name\":\"Опухоль Беркитта\"}],\"Options\":{\"IncludeInsignificantInactiveIngredients\":true,\"IncludeMonographs\":true},\"IncludeFinishedDrugs\":false}", ScreeningResponseBean.class);
	}

	private static final int SLICE = 16384;

	public void groupingTest(List<KmpMedicamentPrescribe> kmpMedicamentPrescribeList, DatabaseHandler databaseHandler) {
//		List<KmpMedicamentPrescribe> kmpMedicamentPrescribeList = Arrays.asList(new KmpMedicamentPrescribe(1L, "DD0000801", "Аспирин табл. 500мг", new Diagnosis("J03.9", "Острый тонзиллит неуточненный")),
//			new KmpMedicamentPrescribe(2L, "DD0000801", "Аспирин табл. 500мг", new Diagnosis("J10.8", "Грипп")),
//			new KmpMedicamentPrescribe(3L, "SI002679", "Варфарин", new Diagnosis("J03.9", "Острый тонзиллит неуточненный")));
		List<ScreeningBean> screeningBeanList = createScreeningBean(kmpMedicamentPrescribeList);
		HttpHandler httpHandler = new HttpHandler();
		List<ScreeningResponseBean> screeningResponseBeanList = screeningBeanList.stream().map(screeningBean -> httpHandler.sendPost(url, screeningBean, ScreeningResponseBean.class)).collect(Collectors.toList());
		Map<Diagnosis, Map<String, Set<String>>> allertMap = screeningResponseBeanList.stream().filter(responseBean -> responseBean.getDiseaseContraindications() != null && responseBean.getDiseaseContraindications().getItemsList() != null && !responseBean.getDiseaseContraindications().getItemsList().isEmpty())
			.flatMap(responseBean -> responseBean.getDiseaseContraindications().getItemsList().stream()).filter(item -> item.getAlert() != null && item.getDrugsList() != null && !item.getDrugsList().isEmpty() && item.getDiseasesList() != null && item.getDiseasesList().size() == 1)
			.collect(Collectors.groupingBy(item -> new Diagnosis(item.getDiseasesList().get(0).getCode()), Collectors.collectingAndThen(Collectors.toList(), ff -> ff.stream().flatMap(item -> item.getDrugsList().stream().map(drug -> new DrugAlertBean(drug, item.getAlert())))
			.collect(Collectors.groupingBy(DrugAlertBean::getCode, Collectors.collectingAndThen(Collectors.toList(), ff2 -> ff2.stream().map(DrugAlertBean::getAlert).collect(Collectors.toSet())))))));
		kmpMedicamentPrescribeList.forEach(kmp -> {
			String alert = Optional.ofNullable(allertMap.get(kmp.getDiagnosis())).map(map2 -> map2.get(kmp.getSid())).filter(alertSet -> alertSet != null && !alertSet.isEmpty()).map(alertSet -> alertSet.stream().sorted().reduce((str1, str2) -> str1 + "\r\n" + str2).get()).orElse("Нет");
			kmp.setAlert(alert);
		});
		for (List<KmpMedicamentPrescribe> kmpMedicamentPrescribeSubList : Utils.partition(kmpMedicamentPrescribeList, SLICE)) {
			databaseHandler.updateKmpMedicamentPrescribe(kmpMedicamentPrescribeSubList);
		}
	}

	private void testDataBase() {
		DatabaseHandler databaseHandler = new DatabaseHandler("bulk-docs.updDs", "bulk-docs.nsi");
		List<KmpMedicamentPrescribe> kmpMedicamentPrescribeList = databaseHandler.handleKmpMedicamentPrescribeList();
		groupingTest(kmpMedicamentPrescribeList, databaseHandler);
	}

	private ScreeningBean createTestScreeningBean() {
		ScreeningBean screeningBean = new ScreeningBean();
		screeningBean.setScreeningTypes("DrugDrugInteractions, AllergicReactions, AgeContraindications, DiseaseContraindications");
		screeningBean.setPatient(new Patient("1985-11-07", "Female"));
		screeningBean.setDrugsList(Arrays.asList(new Drugs(new Schedule("2021-10-20", "2021-10-25"), true, "DispensableDrug", "DD0000801", "Аспирин табл. 500мг"),
			new Drugs(null, true, "DispensableDrug", "DD0009390", "Варфарекс табл. 5мг"),
			new Drugs(null, true, "DispensableDrug", "DD999999", "Ошибочный препарат")));
		screeningBean.setAllergiesList(Arrays.asList(new Allergies(true, "ScreenableIngredient", "SI002679", "Варфарин"),
			new Allergies(true, "AllergenClass", "ALGC0029", "Салицилаты")));
		screeningBean.setDiseasesList(Arrays.asList(new Diseases(true, "ICD10CM", "J03.9", "Острый тонзиллит неуточненный")));
		screeningBean.setOptions(new Options(true, true, new DopingAlerts(false, false)));
		screeningBean.setIncludeFinishedDrugs(false);
		return screeningBean;
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
