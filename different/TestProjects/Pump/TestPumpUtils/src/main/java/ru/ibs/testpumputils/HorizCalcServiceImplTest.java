/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.testpumputils;

import com.opencsv.CSVWriter;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.ibs.pmp.api.model.HorizCalcTempView;
import ru.ibs.pmp.api.model.dto.horizcalc.HORIZ_COLUMN_NAME;
import ru.ibs.pmp.api.model.dto.horizcalc.HorizCalcRequest;
import ru.ibs.pmp.api.model.dto.horizcalc.HorizCalcTempViewDto;
import ru.ibs.pmp.api.model.dto.horizcalc.HorizColumnNameBuilder;
import ru.ibs.pmp.api.model.dto.horizcalc.HorizColumnType;
import ru.ibs.pmp.api.model.dto.horizcalc.HorizGetValue;
import ru.ibs.pmp.api.nsi.model.enums.PatientType;
import ru.ibs.pmp.reports.custom.CustomReportType;
import ru.ibs.pmp.reports.custom.horizcalc.HorizCalcServiceImpl;
import ru.ibs.pmp.reports.engine.ReportBuildTask;
import ru.ibs.pmp.reports.engine.ReportParameter;
import ru.ibs.pmp.reports.engine.ReportParameterTemplate;
import ru.ibs.pmp.reports.engine.ReportParameterType;
import ru.ibs.pmp.reports.engine.ReportTemplate;
import ru.ibs.pmp.util.DateUtils;

/**
 *
 * @author me
 */
public class HorizCalcServiceImplTest {

	public static void test() throws Exception {
		HorizCalcServiceImplTest horizCalcServiceImplTest = new HorizCalcServiceImplTest();
		horizCalcServiceImplTest.buildReport(null, null);
	}

	private final static Logger logger = LoggerFactory.getLogger(HorizCalcServiceImpl.class);

	@Autowired
	@Qualifier("pmpJdbcTemplate")
	private NamedParameterJdbcTemplate jdbc;

	@Value("classpath:sql/horizcalc/horiz_calz_search.sql")
	private Resource resource;

	private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	private ReportParameter getReportParameter(String name, String value) {
		ReportParameter reportParameter = new ReportParameter();
		reportParameter.setName(name);
		reportParameter.setValueAsString(value);
		return reportParameter;
	}

//	@Override
	public ReportBuildTask buildReport(ReportBuildTask task, ReportTemplate template) {
//		try {
//			try (FileOutputStream fos = new FileOutputStream(task.getReport().getReportFileName(), false);
//					OutputStreamWriter osw = new OutputStreamWriter(fos, "windows-1251");
//					Writer bw = new BufferedWriter(osw, 1024 * 1024);
//					CSVWriter writer = new CSVWriter(bw, ';', CSVWriter.DEFAULT_QUOTE_CHARACTER)) {
//				HorizCalcRequest.MODE mode = HorizCalcRequest.MODE.valueOf(getParams(task.getReport().getParameters(), "Mode").get());
		String sql = null;

//  "parameters" : [ {
//    "id" : 2471232,
//    "typeCode" : "3",
//    "type" : "STRING",
//    "name" : "author",
//    "valueAsString" : "Севалкин Е.",
//    "value" : "Севалкин Е."
//  }, {
//    "id" : 2471233,
//    "typeCode" : "4",
//    "type" : "DATE",
//    "name" : "createDate",
//    "valueAsString" : "2021-12-15T15:55:28.901Z",
//    "value" : "2021-12-15T18:55:28.000Z"
//  }, {
//    "id" : 2471234,
//    "typeCode" : "3",
//    "type" : "STRING",
//    "name" : "lpuId",
//    "valueAsString" : "2157",
//    "value" : "2157"
//  }, {
//    "id" : 2471235,
//    "typeCode" : "3",
//    "type" : "STRING",
//    "name" : "mgfoms",
//    "valueAsString" : "0",
//    "value" : "0"
//  }, {
//    "id" : 2471236,
//    "typeCode" : "3",
//    "type" : "STRING",
//    "name" : "PeriodFrom",
//    "valueAsString" : "2021-09-01",
//    "value" : "2021-09-01"
//  }, {
//    "id" : 2471237,
//    "typeCode" : "3",
//    "type" : "STRING",
//    "name" : "PeriodTo",
//    "valueAsString" : "2021-09-30",
//    "value" : "2021-09-30"
//  }, {
//    "id" : 2471238,
//    "typeCode" : "3",
//    "type" : "STRING",
//    "name" : "NonMoscowLpuOrd",
//    "valueAsString" : "false",
//    "value" : "false"
//  }, {
//    "id" : 2471239,
//    "typeCode" : "3",
//    "type" : "STRING",
//    "name" : "MOPrik",
//    "valueAsString" : "2157",
//    "value" : "2157"
//  }, {
//    "id" : 2471240,
//    "typeCode" : "3",
//    "type" : "STRING",
//    "name" : "mode",
//    "valueAsString" : "smp",
//    "value" : "smp"
//  }, {
//    "id" : 2471241,
//    "typeCode" : "3",
//    "type" : "STRING",
//    "name" : "moId",
//    "valueAsString" : "2157",
//    "value" : "2157"
//  } ],
//		List<ReportParameter> params = task.getReport().getParameters();
		List<ReportParameter> params = Arrays.asList(getReportParameter("lpuId", "2157"), getReportParameter("mgfoms", "0"),
				getReportParameter("PeriodFrom", "2021-09-01"), getReportParameter("PeriodTo", "2021-09-30"), getReportParameter("NonMoscowLpuOrd", "false"),
				getReportParameter("MOPrik", "2157"), getReportParameter("mode", "smp"), getReportParameter("moId", "2157"));
		Map<String, Object> criterions = new HashMap<>();
//				if (mode == HorizCalcRequest.MODE.vz_out || mode == HorizCalcRequest.MODE.vz_in) {
//
//					sql = "select "
//							+ "    periodT             \"periodT\"\n"
//							+ "    ,period              \"period\"\n"
//							+ "    ,LPU_ID              \"lpuId\"\n"
//							+ "    ,FIL_ID              \"filId\"\n"
//							+ "    ,SMO_ID              \"smoId\"\n"
//							+ "    ,PATIENT_ID          \"patientId\"\n"
//							+ "	,SN_POL              \"snPol\"\n"
//							+ "	,C_I                 \"ci\"\n"
//							+ "    ,TIP                 \"tip\"\n"
//							+ "    ,IOTD                \"iotd\"\n"
//							+ "    ,PCOD                \"pcod\"\n"
//							+ "    ,PRVS                \"prvs\"\n"
//							+ "    ,DS                  \"ds\"\n"
//							+ "    ,D_U                 \"du\"\n"
//							+ "    ,COD                 \"cod\"\n"
//							+ "    ,K_U                 \"ku\"\n"
//							+ "    ,INVOICE_SUM         \"invoiceSum\"\n"
//							+ "    ,ORD                 \"ord\"\n"
//							+ "    ,LPU_ORD             \"lpuOrd\"\n"
//							+ "    ,DATE_ORD            \"dateOrd\"\n"
//							+ "    ,PRIK                 \"prik\"\n"
//							+ "    ,PRIKS                \"priks\"\n"
//							+ "    ,PRIK_BEGIN_PERIOD   \"prikBeginPeriod\"\n"
//							+ "    ,PRIKS_BEGIN_PERIOD  \"priksBeginPeriod\"\n"
//							+ "    ,VMP146              \"vmp146\"\n"
//							+ "    ,TPN                 \"tpn\"\n"
//							+ "    ,SERVICE_TYPE        \"serviceType\"\n"
//							+ "    ,CASE_ID             \"caseId\"\n"
//							+ "    ,STATUS              \"status\"\n"
//							+ "    ,CASE_TYPE           \"caseType\"\n"
//							+ "    ,ISHOD               \"ishod\"\n"
//							+ "    ,RSLT                \"rslt\"\n"
//							+ "    ,PRVS_ORD            \"prvsOrd\"\n"
//							+ "    ,PRVS_ORD_TARGET     \"prvsOrdTarget\"\n"
//							+ "    ,AGE_CAT             \"ageCat\"\n"
//							+ "    ,PROFOT              \"profot\"\n"
//							+ "    ,PROFIL              \"profil\"\n"
//							+ "    ,SRC                 \"src\"\n"
//							+ "	,N_U                  \"nu\"\n"
//							+ "	,T_UZ                \"tuz\"\n"
//							+ "	,T_UP                \"tup\"\n"
//							+ "	,PROFBR               \"profbr\"\n"
//							+ "	,CODHO               \"codho\"\n"
//							+ "	,PAT_D_TYPE          \"patDType\"\n"
//							+ "	,BILL_D_TYPE         \"billDType\"\n"
//							+ "    ,SMO_NAME            \"smoName\"\n"
//							+ "    ,LPU_NAME            \"lpuName\"\n"
//							+ "    ,LPU_TPN             \"lpuTpn\"\n"
//							+ "    ,LPU_TPNS            \"lpuTpns\"\n"
//							+ "    ,FIL_NAME          \"filName\"  \n"
//							+ "    --,FIL_TPN             \n"
//							+ "    --,FIL_TPNS            \n"
//							+ "    ,PRIK_NAME        \"prikName\"\n"
//							+ "    ,PRIKS_NAME       \"priksName\"\n"
//							+ "    --,PRIK_TPN            \n"
//							+ "    --,PRIKS_TPNS          \n"
//							+ "    ,PRIK_BEGIN_PERIOD_NAME \"prikBeginPeriodName\"\n"
//							+ "    ,PRIKS_BEGIN_PERIOD_NAME \"priksBeginPeriodName\"\n"
//							+ "    ,PRIK_BEGIN_PERIOD_TPN   \"prikBeginPeriodTpn\"\n"
//							+ "    ,PRIKS_BEGIN_PERIOD_TPNS  \"priksBeginPeriodTpns\"\n"
//							+ "    ,LPU_ORD_NAME        \"lpuOrdName\"\n"
//							+ "    ,COD_NAME            \"codName\"\n"
//							+ "    ,DS_NAME              \"dsName\"\n"
//							+ "    ,TIP_NAME            \"tipName\"\n"
//							+ "    ,ORD_NAME            \"ordName\"\n"
//							+ "    ,RSLT_NAME           \"rsltName\"\n"
//							+ "    ,ISHOD_NAME          \"ishodName\"\n"
//							+ "    ,PROFOT_NAME         \"profotName\"\n"
//							+ "    ,PROFIL_NAME          \"profilName\"\n"
//							+ "    ,AGE_CAT_NAME        \"ageCatName\"\n"
//							+ "    ,CODHO_NAME          \"codhoName\"\n"
//							+ "    ,PAT_D_TYPE_NAME     \"patDTypeName\"\n"
//							+ "    ,BILL_D_TYPE_NAME     \"billDTypeName\"\n"
//							+ "    ,PRVS_NAME           \"prvsName\"\n"
//							+ "    ,PRVS_ORD_NAME        \"prvsOrdName\"\n"
//							+ "    ,PRVS_ORD_TARGET_NAME \"prvsOrdTargetName\"\n"
//							+ "    ,PRVS_KIND           \"prvsKind\"\n"
//							+ "	,NOVOR               \"novor\"\n"
//							+ "	,F_TYPE              \"fType\"\n"
//							+ "	,IS_OPERATION_MAIN   \"isOperationMain\"\n"
//							+ "    ,ID                 \"id\" "
//							+ "from table (pmp_horiz_calc_pck.interface_get(:type,:period,:moId,:pageStart,:pageEnd ";
//
//					StringBuilder where = new StringBuilder();
//					criterions = toCriterionPump(params, where);
//					sql = sql + where.toString() + "))";
//				} else {
		sql = horizSql;
		StringBuilder where = new StringBuilder();
		criterions = toCriterion(params, where);
		sql = sql.replace("%where%", where.toString()).replace("%select_start%", "").replace("%select_end%", "")
				.replace("%pagination_start%", "  ").replace("%pagination_end%", " ");

//				}
		BeanPropertyRowMapper<HorizCalcTempView> rowMapper = new BeanPropertyRowMapper<>(
				HorizCalcTempView.class);
		AtomicInteger rowCounter = new AtomicInteger();
//				List<HORIZ_COLUMN_NAME> columns = HorizColumnType.getColumns(mode);
//				HorizColumnNameBuilder horizColumnNameBuilder = new HorizColumnNameBuilder(mode);
//				writer.writeNext(
//						columns.stream().map(horiz_column_name -> horizColumnNameBuilder.getName(horiz_column_name))
//								.toArray(value -> new String[value]));
		List<Method> allMethods = new ArrayList<Method>(
				Arrays.asList(HorizCalcTempViewDto.class.getSuperclass().getDeclaredMethods()));
		allMethods.addAll(Arrays.asList(HorizCalcTempViewDto.class.getDeclaredMethods()));
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator(',');
		DecimalFormat decimalFormat = new DecimalFormat();
		decimalFormat.setParseBigDecimal(true);
		decimalFormat.setDecimalFormatSymbols(symbols);
		decimalFormat.setGroupingUsed(false);
		int logrowsdelta = 1000;
		AtomicInteger loggedrows = new AtomicInteger();
//				jdbc.query(sql, criterions, (ResultSet rs) -> {
//					try {
//						if ((mode == HorizCalcRequest.MODE.vz_out || mode == HorizCalcRequest.MODE.vz_in) && "0".equals(rs.getString(3))) {
//						} else {
////							writer.writeNext(toCsvFormat(toDto(rowMapper.mapRow(rs, rowCounter.incrementAndGet())), mode,
////									allMethods, columns, simpleDateFormat, decimalFormat));
//						}
//						int c = rowCounter.get();
//						if (c >= loggedrows.get() + logrowsdelta) {
//							logger.info("HorizCalcServiceImpl.buildReport processed " + c + " rows");
//							loggedrows.set(c);
//						}
//					} catch (ReflectiveOperationException e) {
//						throw new RuntimeException(e);
//					}
//				});
//			}
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
		return task;
	}

	private String[] toCsvFormat(HorizCalcTempViewDto dto, HorizCalcRequest.MODE mode, List<Method> allMethods,
			List<HORIZ_COLUMN_NAME> columns, SimpleDateFormat simpleDateFormat, DecimalFormat decimalFormat)
			throws ReflectiveOperationException {

		List<String> line = new ArrayList<>();

		for (HORIZ_COLUMN_NAME columnName : columns) {
			Predicate<Method> methodPredicate = method -> method.getAnnotation(HorizGetValue.class).column()
					.equals(HORIZ_COLUMN_NAME.empty)
					? Arrays.asList(method.getAnnotation(HorizGetValue.class).columns()).contains(columnName)
					: method.getAnnotation(HorizGetValue.class).column().equals(columnName);

			List<Method> methods = allMethods.stream().filter(method -> method.isAnnotationPresent(HorizGetValue.class))
					.filter(methodPredicate).collect(Collectors.toList());
			String value = "";
			for (Method opt : methods) {
				if (value.length() > 0) {
					// code - value
					value = value.concat(" - ");
				}
				Object cell = opt.invoke(dto);
				value = value + opt.getAnnotation(HorizGetValue.class).prefix();
				if (opt.getAnnotation(HorizGetValue.class).dateFormat()) {
					if (cell != null && cell instanceof Date) {
						value = value.concat(simpleDateFormat.format(cell));
					} else {
						value = value.concat("");
					}
				} else {
					if (cell != null && cell instanceof BigDecimal) {
						BigDecimal bigDecimal = (BigDecimal) cell;
						value = value.concat(decimalFormat.format(bigDecimal.floatValue()));
					} else {
						String newValue = Objects.toString(cell, "");
						if (value.length() > 0 && newValue.length() == 0) {
							value = "";
						} else {
							value = value.concat(Objects.toString(cell, ""));
						}
					}
				}
			}
			line.add(value);
		}
		return line.toArray(new String[line.size()]);
	}

	private HorizCalcTempViewDto toDto(HorizCalcTempView horizCalcTemp) {
		HorizCalcTempViewDto horizCalcTempDTO = HorizCalcTempViewDto.converted(horizCalcTemp);
		horizCalcTempDTO.setPrikBeginPeriod(horizCalcTemp.getPrikBeginPeriod());
		horizCalcTempDTO.setPriksBeginPeriod(horizCalcTemp.getPriksBeginPeriod());
		return horizCalcTempDTO;
	}

	private static Map<String, Object> toCriterion(List<ReportParameter> params, StringBuilder where) {
		Map<String, Object> criterionList = new HashMap<>();
		where.append(" where period in :period ");
		criterionList.put("period", DateUtils.getPeriods(getDate(params, "PeriodFrom").get(), getDate(params, "PeriodTo").get())
				.stream().map(d -> new java.sql.Date(d.getTime())).collect(Collectors.toList()));
		if (getParams(params, "SmoId").isPresent()) {
			where.append(" and SMO_ID = :smoId ");
			criterionList.put("smoId", getParams(params, "SmoId").get());
		}
		if (getParams(params, "patientId").isPresent()) {
			where.append(" and PATIENT_ID = :patientId ");
			criterionList.put("patientId", getLong(params, "patientId").get());
		}
		if (getParams(params, "SnPol").isPresent()) {
			where.append(" and SN_POL = :snPol ");
			criterionList.put("snPol", getParams(params, "SnPol").get());
		}
		if (getParams(params, "Ci").isPresent()) {
			where.append(" and C_I = :ci ");
			criterionList.put("ci", getParams(params, "Ci").get());
		}
		if (getParams(params, "DsFrom").isPresent() && getParams(params, "DsTo").isPresent()) {
			where.append(" and (DS between :dsFrom and :dsTo) ");
			criterionList.put("dsFrom", getParams(params, "DsFrom").get());
			criterionList.put("dsTo", getParams(params, "DsTo").get());
		} else {
			if (getParams(params, "DsFrom").isPresent()) {
				where.append(" and DS = :dsFrom ");
				criterionList.put("dsFrom", getParams(params, "DsFrom").get());
			}
			if (getParams(params, "DsTo").isPresent()) {
				where.append(" and DS = :dsTo ");
				criterionList.put("dsTo", getParams(params, "DsTo").get());
			}
		}
		if (getParams(params, "DuFrom").isPresent() && getParams(params, "DuTo").isPresent()) {
			where.append(" and ( D_U between :duFrom and :duTo ) ");
			criterionList.put("duFrom", getDate(params, "DuFrom").get());
			criterionList.put("duTo", getDate(params, "DuTo").get());
		} else {
			if (getParams(params, "DuFrom").isPresent()) {
				where.append(" and D_U = :duFrom ");
				criterionList.put("duFrom", getDate(params, "DuFrom").get());
			}
			if (getParams(params, "DuTo").isPresent()) {
				where.append(" and D_U = :duTo ");
				criterionList.put("duTo", getDate(params, "DuTo").get());
			}
		}
		if (getParams(params, "CodUslFrom").isPresent() && getParams(params, "CodUslTo").isPresent()) {
			where.append(" and ( COD between :codUslFrom and :codUslTo ) ");
			criterionList.put("codUslFrom", getLong(params, "CodUslFrom").get());
			criterionList.put("codUslTo", getLong(params, "CodUslTo").get());
		} else {
			if (getParams(params, "CodUslFrom").isPresent()) {
				where.append(" and COD = :codUslFrom ");
				criterionList.put("codUslFrom", getLong(params, "CodUslFrom").get());
			}
			if (getParams(params, "CodUslTo").isPresent()) {
				where.append(" and COD = :codUslTo ");
				criterionList.put("codUslTo", getLong(params, "CodUslTo").get());
			}
		}
		if (getBoolean(params, "CodVMP200").orElse(Boolean.FALSE)) {
			where.append(" and to_char(COD) like '200%' ");
		}
		if (getParams(params, "Ord").isPresent()) {
			where.append(" and ORD = :ord ");
			criterionList.put("ord", getParams(params, "Ord").get());
		}
		if (getBoolean(params, "Napr").isPresent()) {
			if (getBoolean(params, "Napr").get()) {
				where.append(" and ORD <> 0");
			} else {
				where.append(" and ORD = 0");
			}
		}
		if (getLong(params, "LpuOrd").isPresent()) {
			where.append(" and LPU_ORD = :lpuOrd ");
			criterionList.put("lpuOrd", getLong(params, "LpuOrd").get());
		}
		if (getBoolean(params, "NonMoscowLpuOrd").isPresent()) {
			if (getBoolean(params, "NonMoscowLpuOrd").get()) {
				where.append(" and LPU_ORD in :lpuOrds ");
				criterionList.put("lpuOrds", Arrays.asList(7665, 8888, 9999));
			} else {
				where.append(" and LPU_ORD not in (:lpuOrds) ");
				criterionList.put("lpuOrds", Arrays.asList(7665, 8888, 9999));
			}
		}
		if (getInteger(params, "MoPrik").isPresent()) {
			if (getInteger(params, "PrikIsBeginPeriod").orElse(0) == 1) {
				where.append(" and ( PRIK_BEGIN_PERIOD = :prikBeginPeriod or PRIKS_BEGIN_PERIOD = :prikBeginPeriod) ");
				criterionList.put("prikBeginPeriod", getInteger(params, "MoPrik").get());
			} else {
				where.append(String.format(" and ( PRIK = :prikBeginPeriod %s) ",
						(HorizCalcRequest.MODE.vz_out.name().equals(getParams(params, "Mode").get()) ? "" : " or PRIKS = :prikBeginPeriod ")));
				criterionList.put("prikBeginPeriod", getInteger(params, "MoPrik").get());
			}
		}

		if (getParams(params, "Tpn").isPresent()) {
			where.append(" and TPN = :tpn ");
			criterionList.put("tpn", getParams(params, "Tpn").get());
		}
		if (getLong(params, "Prvs").isPresent()) {
			where.append(" and PRVS = :prvs ");
			criterionList.put("prvs", getLong(params, "Prvs").get());
		}
		if (getLong(params, "Ishod").isPresent()) {
			where.append(" and ISHOD = :ishod ");
			criterionList.put("ishod", getLong(params, "Ishod").get());
		}
		if (getLong(params, "Rslt").isPresent()) {
			where.append(" and RSLT = :rslt ");
			criterionList.put("rslt", getLong(params, "Rslt").get());
		}
		if (getParams(params, "PrvsOrd").isPresent()) {
			where.append(" and PRVS_ORD = :prvsOrd ");
			criterionList.put("prvsOrd", getParams(params, "PrvsOrd").get());
		}
		if (getParams(params, "PrvsOrdTarget").isPresent()) {
			where.append(" and PRVS_ORD_TARGET = :prvsOrdTarget ");
			criterionList.put("prvsOrdTarget", getParams(params, "PrvsOrdTarget").get());
		}
		if (getParams(params, "AgeCat").isPresent()) {
			where.append(" and AGE_CAT = :ageCat ");
			criterionList.put("ageCat", getParams(params, "AgeCat").get());
		}
		if (getParams(params, "Profot").isPresent()) {
			where.append(" and PROFOT = :profot ");
			criterionList.put("profot", getParams(params, "Profot").get());
		}
		if (getParams(params, "Profil").isPresent()) {
			where.append(" and PROFIL = :profil ");
			criterionList.put("profil", getParams(params, "Profil").get());
		}
		if (getParams(params, "Src").isPresent()) {
			where.append(" and SRC in :src ");
			criterionList.put("src", getParams(params, "Src").get());
		}
		if (HorizCalcRequest.MODE.hc.name().equals(getParams(params, "Mode").get())) {
			where.append(" and SERVICE_TYPE = :serviceType ");
			criterionList.put("serviceType", "U");
		}
		if (HorizCalcRequest.MODE.smp.name().equals(getParams(params, "Mode").get())) {
			where.append(" and SERVICE_TYPE = :serviceType ");
			criterionList.put("serviceType", "A");
		}
		if (HorizCalcRequest.MODE.hosp.name().equals(getParams(params, "Mode").get())) {
			where.append(" and SERVICE_TYPE in ('V','S')");
		}
		if (HorizCalcRequest.MODE.vz_in.name().equals(getParams(params, "Mode").get())) {
			where.append(" and F_TYPE = :fType ");
			criterionList.put("fType", "vz");

			where.append(" and LPU_ID = :lpuId ");
			criterionList.put("lpuId", getLong(params, "LpuId").get());
		}

		if (HorizCalcRequest.MODE.vz_out.name().equals(getParams(params, "Mode").get())) {
			where.append(" and F_TYPE = :fType ");
			criterionList.put("fType", "vz");
		}

		if (getParams(params, "Status").isPresent()) {
			where.append(" and STATUS = :status ");
			criterionList.put("status", getParams(params, "Status").get());
		}
		if (getParams(params, "Codho").isPresent()) {
			where.append(" and CODHO = :codho ");
			criterionList.put("codho", getParams(params, "Codho").get());
		}
		if (getBoolean(params, "CodhoIsComl").isPresent()) {
			if (getBoolean(params, "CodhoIsComl").get()) {
				where.append(" and CODHO is not null ");
			} else {
				where.append(" and CODHO not null ");
			}
		}
		if (getBoolean(params, "CodhoIsPrimary").isPresent() && getBoolean(params, "CodhoIsPrimary").get()) {
			where.append(" and IS_OPERATION_MAIN = :isOperationMain ");
			criterionList.put("isOperationMain", true);
		}
		if (getBoolean(params, "Stomat").isPresent() && getBoolean(params, "Stomat").get()) {
			where.append(" and VMP146 = :vmp146 ");
			criterionList.put("vmp146", 2);
		}
		if (getParams(params, "Pcod").isPresent()) {
			where.append(" and PCOD = :pcod ");
			criterionList.put("pcod", getParams(params, "Pcod").get());
		}
		if (getLong(params, "CaseId").isPresent()) {
			where.append(" and CASE_ID = :caseId ");
			criterionList.put("caseId", getLong(params, "CaseId").get());
		}
		if (getInteger(params, "FilId").isPresent()) {
			where.append(" and FIL_ID = :filId ");
			criterionList.put("filId", getInteger(params, "FilId").get().longValue());
		}
		if (getParams(params, "PrvsKind").isPresent()) {
			where.append(" and PRVS_KIND = :prvsKind ");
			criterionList.put("prvsKind", 0L);
		}
		if (getParams(params, "PatientType").isPresent()) {
			if (getParams(params, "PatientType").get() == PatientType.INSURED.name()) {
				where.append(" and NOVOR is null ");
			}
			if (getParams(params, "PatientType").get() == PatientType.NEWBORN.name()) {
				where.append(" and NOVOR is not null ");
			}
		}
		return criterionList;
	}

	private static Map<String, Object> toCriterionPump(List<ReportParameter> params, StringBuilder where) {
		Map<String, Object> criterionList = new HashMap<>();
		if (HorizCalcRequest.MODE.vz_in.name().equals(getParams(params, "Mode").get())) {
			criterionList.put("type", 1);
		}
		if (HorizCalcRequest.MODE.vz_out.name().equals(getParams(params, "Mode").get())) {
			criterionList.put("type", 2);
		}
		criterionList.put("period", getDate(params, "PeriodFrom").get() == null ? getDate(params, "PeriodTo").get() : getDate(params, "PeriodFrom").get());
		criterionList.put("moId", getLong(params, "LpuId").get());
		criterionList.put("pageStart", 0L);
		criterionList.put("pageEnd", 10000000L);

		if (getParams(params, "SmoId").isPresent()) {
			where.append(", P_SMO => :smoId ");
			criterionList.put("smoId", getParams(params, "SmoId").get());
		}
		if (getParams(params, "patientId").isPresent()) {
			where.append(", P_PATIENT_ID => :patientId ");
			criterionList.put("patientId", getLong(params, "patientId").get());
		}
		if (getParams(params, "SnPol").isPresent()) {
			where.append(", P_SN_POL => :snPol ");
			criterionList.put("snPol", getParams(params, "SnPol").get());
		}
		if (getParams(params, "Ci").isPresent()) {
			where.append(", P_C_I => :ci ");
			criterionList.put("ci", getParams(params, "Ci").get());
		}
		if (getParams(params, "DsFrom").isPresent()) {
			where.append(", P_DS_FROM => :dsFrom ");
			criterionList.put("dsFrom", getParams(params, "DsFrom").get());
		}
		if (getParams(params, "DsTo").isPresent()) {
			where.append(", P_DS_TO => :dsTo ");
			criterionList.put("dsTo", getParams(params, "DsTo").get());
		}
		if (getParams(params, "DuFrom").isPresent()) {
			where.append(", P_DU_FROM => :duFrom ");
			criterionList.put("duFrom", getDate(params, "DuFrom").get());
		}
		if (getParams(params, "DuTo").isPresent()) {
			where.append(", P_DU_TO => :duTo ");
			criterionList.put("duTo", getDate(params, "DuTo").get());
		}
		if (getParams(params, "CodUslFrom").isPresent()) {
			where.append(", P_COD_FROM => :codUslFrom ");
			criterionList.put("codUslFrom", getLong(params, "CodUslFrom").get());
		}
		if (getParams(params, "CodUslTo").isPresent()) {
			where.append(", P_COD_TO => :codUslTo ");
			criterionList.put("codUslTo", getLong(params, "CodUslTo").get());
		}
		if (getBoolean(params, "CodVMP200").orElse(Boolean.FALSE)) {
			where.append(", P_COD_VMP200 => 1 ");
		}
		if (getParams(params, "Ord").isPresent()) {
			where.append(", P_ORD => :ord ");
			criterionList.put("ord", getParams(params, "Ord").get());
		}
		if (getBoolean(params, "Napr").isPresent()) {
			if (getBoolean(params, "Napr").get()) {
				where.append(", P_NAPR => 1 ");
			} else {
				where.append(", P_NAPR => 0 ");
			}
		}
		if (getLong(params, "LpuOrd").isPresent()) {
			where.append(", LPU_ORD => :lpuOrd ");
			criterionList.put("lpuOrd", getLong(params, "LpuOrd").get());
		}
		if (getBoolean(params, "NonMoscowLpuOrd").isPresent()) {
			if (getBoolean(params, "NonMoscowLpuOrd").get()) {
				where.append(", P_MOS_LPU_ORD => 1 ");
			} else {
				where.append(", P_MOS_LPU_ORD => 0 ");
			}
		}
		/*
		if (request.getMoPrik() != null) {
			if (Optional.ofNullable(request.getPrikIsBeginPeriod()).orElse(0) == 1) {
				where.append(" and ( PRIK_BEGIN_PERIOD = :prikBeginPeriod or PRIKS_BEGIN_PERIOD = :prikBeginPeriod) ");
				criterionList.put("prikBeginPeriod", request.getMoPrik());
			} else {
				where.append(String.format(" and ( PRIK = :prikBeginPeriod %s) ", (request.getMode())== HorizCalcRequest.MODE.vz_out ? "" : " or PRIKS = :prikBeginPeriod "));
				criterionList.put("prikBeginPeriod", request.getMoPrik());
			}
		}
		 */
		if (getParams(params, "Tpn").isPresent()) {
			where.append(", P_TPN => :tpn ");
			criterionList.put("tpn", getParams(params, "Tpn").get());
		}
		if (getLong(params, "Prvs").isPresent()) {
			where.append(", P_PRVS => :prvs ");
			criterionList.put("prvs", getLong(params, "Prvs").get());
		}
		if (getLong(params, "Ishod").isPresent()) {
			where.append(", P_ISHOD => :ishod ");
			criterionList.put("ishod", getLong(params, "Ishod").get());
		}
		if (getLong(params, "Rslt").isPresent()) {
			where.append(", P_RSLT => :rslt ");
			criterionList.put("rslt", getLong(params, "Rslt").get());
		}
		if (getParams(params, "PrvsOrd").isPresent()) {
			where.append(", P_PRVS_ORD => :prvsOrd ");
			criterionList.put("prvsOrd", getParams(params, "PrvsOrd").get());
		}
		if (getParams(params, "PrvsOrdTarget").isPresent()) {
			where.append(", P_PRVS_ORD_TARGET => :prvsOrdTarget ");
			criterionList.put("prvsOrdTarget", getParams(params, "PrvsOrdTarget").get());
		}
		if (getParams(params, "AgeCat").isPresent()) {
			where.append(", P_AGE_CAT => :ageCat ");
			criterionList.put("ageCat", getParams(params, "AgeCat").get());
		}
		if (getParams(params, "Profot").isPresent()) {
			where.append(", P_PROFOT => :profot ");
			criterionList.put("profot", getParams(params, "Profot").get());
		}
		if (getParams(params, "Profil").isPresent()) {
			where.append(", P_PROFIL => :profil ");
			criterionList.put("profil", getParams(params, "Profil").get());
		}
		if (getParams(params, "Codho").isPresent()) {
			where.append(", P_CODHO => :codho ");
			criterionList.put("codho", getParams(params, "Codho").get());
		}
		if (getBoolean(params, "CodhoIsComl").isPresent()) {
			if (getBoolean(params, "CodhoIsComl").get()) {
				where.append(", P_CODHO_IS => 1 ");
			} else {
				where.append(", P_CODHO_IS => 0 ");
			}
		}
		if (getBoolean(params, "CodhoIsPrimary").isPresent() && getBoolean(params, "CodhoIsPrimary").get()) {
			where.append(", P_CODHO_IS_PRIMARY => 1 ");
		}
		if (getBoolean(params, "Stomat").isPresent() && getBoolean(params, "Stomat").get()) {
			where.append(", P_STOMAT => 1 ");
		}
		if (getParams(params, "Pcod").isPresent()) {
			where.append(", P_PCOD => :pcod ");
			criterionList.put("pcod", getParams(params, "Pcod").get());
		}
		if (getLong(params, "CaseId").isPresent()) {
			where.append(", P_CASE_ID => :caseId ");
			criterionList.put("caseId", getLong(params, "CaseId").get());
		}
		if (getInteger(params, "FilId").isPresent()) {
			where.append(", P_FIL_ID => :filId ");
			criterionList.put("filId", getInteger(params, "FilId").get().longValue());
		}
		if (getParams(params, "PrvsKind").isPresent()) {
			where.append(", P_PRVS_KIND => 0 ");
		}
		if (getParams(params, "PatientType").isPresent()) {
			if (getParams(params, "PatientType").get() == PatientType.INSURED.name()) {
				where.append(", P_PATIENT_TYPE => 0 ");
			}
			if (getParams(params, "PatientType").get() == PatientType.NEWBORN.name()) {
				where.append(", P_PATIENT_TYPE => 2 ");
			}
		}
		return criterionList;
	}

	private static Optional<String> getParams(List<ReportParameter> params, String name) {
		return params.stream().filter(reportParameter -> reportParameter.getName().toUpperCase().equals(name.toUpperCase())).map(ReportParameter::getValueAsString).findFirst();
	}

	private static Optional<Date> getDate(List<ReportParameter> params, String name) {
		return getParams(params, name).map(source -> {
			try {
				return SIMPLE_DATE_FORMAT.parse(source);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return null;
		});
	}

	private static Optional<Long> getLong(List<ReportParameter> params, String name) {
		return getParams(params, name).map(Long::valueOf);
	}

	private static Optional<Integer> getInteger(List<ReportParameter> params, String name) {
		return getParams(params, name).map(Integer::valueOf);
	}

	private static Optional<Boolean> getBoolean(List<ReportParameter> params, String name) {
		return getParams(params, name).map(Boolean::valueOf);
	}

//	@Override
	public CustomReportType getType() {
		return CustomReportType.HORIZ_CALC;
	}

//	@Override 
	public List<ReportParameterTemplate> getReportParametersTemplates() {
		return Arrays.asList(
				new ReportParameterTemplate("PeriodFrom", ReportParameterType.STRING, "Период (ГГГГ-ММ)", "period", false),
				new ReportParameterTemplate("PeriodTo", ReportParameterType.STRING, "Период (ГГГГ-ММ)", "period", false),
				new ReportParameterTemplate("SmoId", ReportParameterType.STRING, "Период (ГГГГ-ММ)", "period", false),
				new ReportParameterTemplate("PatientId", ReportParameterType.STRING, "Период (ГГГГ-ММ)", "period", false),
				new ReportParameterTemplate("SnPol", ReportParameterType.STRING, "Период (ГГГГ-ММ)", "period", false),
				new ReportParameterTemplate("CI", ReportParameterType.STRING, "Период (ГГГГ-ММ)", "period", false),
				new ReportParameterTemplate("DsFrom", ReportParameterType.STRING, "Период (ГГГГ-ММ)", "period", false),
				new ReportParameterTemplate("DsTo", ReportParameterType.STRING, "Период (ГГГГ-ММ)", "period", false),
				new ReportParameterTemplate("DUFrom", ReportParameterType.STRING, "Период (ГГГГ-ММ)", "period", false),
				new ReportParameterTemplate("DUTo", ReportParameterType.STRING, "Период (ГГГГ-ММ)", "period", false),
				new ReportParameterTemplate("CodUslFrom", ReportParameterType.STRING, "Период (ГГГГ-ММ)", "period", false),
				new ReportParameterTemplate("CodUslTo", ReportParameterType.STRING, "Период (ГГГГ-ММ)", "period", false),
				new ReportParameterTemplate("Ord", ReportParameterType.ARRAY, "Период (ГГГГ-ММ)", "period", false),
				new ReportParameterTemplate("Napr", ReportParameterType.STRING, "Период (ГГГГ-ММ)", "period", false),
				new ReportParameterTemplate("LpuOrd", ReportParameterType.STRING, "Период (ГГГГ-ММ)", "period", false),
				new ReportParameterTemplate("NonMoscowLpuOrd", ReportParameterType.STRING, "Период (ГГГГ-ММ)", "period", false),
				new ReportParameterTemplate("MOPrik", ReportParameterType.STRING, "Период (ГГГГ-ММ)", "period", false),
				new ReportParameterTemplate("Stomat", ReportParameterType.STRING, "Период (ГГГГ-ММ)", "period", false),
				new ReportParameterTemplate("Tpn", ReportParameterType.ARRAY, "Период (ГГГГ-ММ)", "period", false),
				new ReportParameterTemplate("Prvs", ReportParameterType.STRING, "Период (ГГГГ-ММ)", "period", false),
				new ReportParameterTemplate("Ishod", ReportParameterType.STRING, "Период (ГГГГ-ММ)", "period", false),
				new ReportParameterTemplate("Rslt", ReportParameterType.STRING, "Период (ГГГГ-ММ)", "period", false),
				new ReportParameterTemplate("PrvsOrd", ReportParameterType.STRING, "Период (ГГГГ-ММ)", "period", false),
				new ReportParameterTemplate("PrvsOrdTarget", ReportParameterType.STRING, "Период (ГГГГ-ММ)", "period", false),
				new ReportParameterTemplate("AgeCat", ReportParameterType.STRING, "Период (ГГГГ-ММ)", "period", false),
				new ReportParameterTemplate("Profot", ReportParameterType.STRING, "Период (ГГГГ-ММ)", "period", false),
				new ReportParameterTemplate("Profil", ReportParameterType.STRING, "Период (ГГГГ-ММ)", "period", false),
				new ReportParameterTemplate("Src", ReportParameterType.ARRAY, "Период (ГГГГ-ММ)", "period", false),
				new ReportParameterTemplate("status", ReportParameterType.STRING, "Период (ГГГГ-ММ)", "period", false),
				new ReportParameterTemplate("mode", ReportParameterType.STRING, "Период (ГГГГ-ММ)", "period", false),
				new ReportParameterTemplate("Codho", ReportParameterType.STRING, "Период (ГГГГ-ММ)", "period", false),
				new ReportParameterTemplate("CodhoIsComl", ReportParameterType.STRING, "Период (ГГГГ-ММ)", "period", false),
				new ReportParameterTemplate("CodhoIsPrimary", ReportParameterType.STRING, "Период (ГГГГ-ММ)", "period", false),
				new ReportParameterTemplate("CodVMP200", ReportParameterType.STRING, "Период (ГГГГ-ММ)", "period", false),
				new ReportParameterTemplate("pcod", ReportParameterType.STRING, "Период (ГГГГ-ММ)", "period", false),
				new ReportParameterTemplate("prikIsBeginPeriod", ReportParameterType.STRING, "Период (ГГГГ-ММ)", "period", false),
				new ReportParameterTemplate("caseId", ReportParameterType.STRING, "Период (ГГГГ-ММ)", "period", false),
				new ReportParameterTemplate("filId", ReportParameterType.STRING, "Период (ГГГГ-ММ)", "period", false),
				new ReportParameterTemplate("prvsKind", ReportParameterType.STRING, "Период (ГГГГ-ММ)", "period", false),
				new ReportParameterTemplate("patientTypeperiod", ReportParameterType.STRING, "Период (ГГГГ-ММ)", "period", false));
	}

//	@Override
	public Boolean getDisableGeneration() {
		return Boolean.TRUE;
	}

	String horizSql = "\n"
			+ "%pagination_start%\n"
			+ "SELECT -- horiz_calz_search.sql\n"
			+ "    %select_start%\n"
			+ "    v.period                               \"periodT\",\n"
			+ "    t.period                                 \"period\",\n"
			+ "    t.LPU_ID                                 \"lpuId\",\n"
			+ "    t.FIL_ID                                 \"filId\",\n"
			+ "    t.SMO_ID                                 \"smoId\",\n"
			+ "    t.PATIENT_ID                                 \"patientId\",\n"
			+ "    t.SN_POL                                 \"snPol\",\n"
			+ "    t.C_I                                 \"ci\",\n"
			+ "    t.TIP                                 \"tip\",\n"
			+ "    t.IOTD                                 \"iotd\",\n"
			+ "    t.PCOD                                 \"pcod\",\n"
			+ "    t.PRVS                                 \"prvs\",\n"
			+ "    t.DS                                 \"ds\",\n"
			+ "    t.D_U                                 \"du\",\n"
			+ "    t.COD                                 \"cod\",\n"
			+ "    t.K_U                                 \"ku\",\n"
			+ "    t.INVOICE_SUM                                 \"invoiceSum\",\n"
			+ "    t.ORD                                 \"ord\",\n"
			+ "    t.LPU_ORD                                 \"lpuOrd\",\n"
			+ "    t.DATE_ORD                                 \"dateOrd\",\n"
			+ "    t.PRIK                                 \"prik\",\n"
			+ "    t.PRIKS                                 \"priks\",\n"
			+ "    t.PRIK_BEGIN_PERIOD                                 \"prikBeginPeriod\",\n"
			+ "    t.PRIKS_BEGIN_PERIOD                                \"priksBeginPeriod\",\n"
			+ "    t.VMP146                                 \"vmp146\",\n"
			+ "    t.TPN                                 \"tpn\",\n"
			+ "    t.SERVICE_TYPE                                 \"serviceType\",\n"
			+ "    t.CASE_ID                                 \"caseId\",\n"
			+ "    t.STATUS                                 \"status\",\n"
			+ "    t.CASE_TYPE                                 \"caseType\",\n"
			+ "    t.ISHOD                                 \"ishod\",\n"
			+ "    t.RSLT                                 \"rslt\",\n"
			+ "    t.PRVS_ORD                                 \"prvsOrd\",\n"
			+ "    t.PRVS_ORD_TARGET                                 \"prvsOrdTarget\",\n"
			+ "    t.AGE_CAT                                 \"ageCat\",\n"
			+ "    t.PROFOT                                 \"profot\",\n"
			+ "    t.PROFIL                                 \"profil\",\n"
			+ "    t.SRC                                 \"src\",\n"
			+ "    t.N_U                                 \"nu\",\n"
			+ "    t.T_UZ                                 \"tuz\",\n"
			+ "    t.T_UP                                 \"tup\",\n"
			+ "    t.PROFBR                                 \"profbr\",\n"
			+ "    t.CODHO                                 \"codho\",\n"
			+ "    t.PAT_D_TYPE                                 \"patDType\",\n"
			+ "    t.BILL_D_TYPE                                 \"billDType\",\n"
			+ "    sprsmo.name                             \"smoName\"\n"
			+ "        ,\n"
			+ "    sprlpu1.full_name                        \"lpuName\"\n"
			+ "        ,\n"
			+ "    sprlpu1.tpn                            \"lpuTpn\"\n"
			+ "        ,\n"
			+ "    sprlpu1.tpns                            \"lpuTpns\"\n"
			+ "        ,\n"
			+ "    sprlpu2.name                             \"filName\"\n"
			+ "        ,\n"
			+ "--        sprlpu2.tpn                            FIL_TPN   \"\"\n"
			+ "--         ,\n"
			+ "--        sprlpu2.tpns                           FIL_TPNS   \"\"\n"
			+ "--         ,\n"
			+ "    sprlpu3.name                           \"prikName\"\n"
			+ "        ,\n"
			+ "    sprlpu4.name                           \"priksName\"\n"
			+ "        ,\n"
			+ "--        sprlpu3.tpn                            PRIK_TPN   \"\"\n"
			+ "--         ,\n"
			+ "--        sprlpu4.tpns                           PRIKS_TPNS   \"\"\n"
			+ "--         ,\n"
			+ "    sprlpu5.name                           \"prikBeginPeriodName\"\n"
			+ "        ,\n"
			+ "    sprlpu6.name                           \"priksBeginPeriodName\"\n"
			+ "        ,\n"
			+ "    sprlpu5.tpn                            \"prikBeginPeriodTpn\"\n"
			+ "        ,\n"
			+ "    sprlpu6.tpns                           \"priksBeginPeriodTpns\"\n"
			+ "        ,\n"
			+ "    NVL (sprlpu7.FULL_NAME, reemorf_ord.name)    \"lpuOrdName\"\n"
			+ "        ,\n"
			+ "    NVL (services1.name, services2.name)   \"codName\"\n"
			+ "        ,\n"
			+ "    ds.name                                \"dsName\"\n"
			+ "        ,\n"
			+ "    kpresl.name                            \"tipName\"\n"
			+ "        ,\n"
			+ "    CASE\n"
			+ "    WHEN t.ORD = 1    THEN 'плановая госпитализация'\n"
			+ "    WHEN t.ORD = 2    THEN 'экстренная госпитализация'\n"
			+ "    WHEN t.ORD = 3    THEN 'пациент обратился самостоятельно'\n"
			+ "    WHEN t.ORD = 4    THEN 'направление от МО системы ОМС города Москвы'\n"
			+ "    WHEN t.ORD = 5    THEN 'плановая госпитализация'\n"
			+ "    WHEN t.ORD = 6    THEN 'допризывник направлен военкоматом'\n"
			+ "    WHEN t.ORD = 7    THEN 'наличие распределения Москомспорта на проведение углубленных медицинских осмотров'\n"
			+ "    WHEN t.ORD = 8    THEN 'наличие договора МО с дошкольным / школьным учреждением'\n"
			+ "    END      \"ordName\"\n"
			+ "        ,\n"
			+ "    rslt.name                              \"rsltName\"\n"
			+ "        ,\n"
			+ "    ishod.name                             \"ishodName\"\n"
			+ "        ,\n"
			+ "    profot.name                            \"profotName\"\n"
			+ "        ,\n"
			+ "    profil.name                            \"profilName\"\n"
			+ "        ,\n"
			+ "    vozobs.name                            \"ageCatName\"\n"
			+ "        ,\n"
			+ "    hopff_.name                            \"codhoName\"\n"
			+ "        ,\n"
			+ "    osoree.name                            \"patDTypeName\"\n"
			+ "        ,\n"
			+ "    os_s.name                              \"billDTypeName\"\n"
			+ "        ,\n"
			+ "    spec1.name                             \"prvsName\"\n"
			+ "        ,\n"
			+ "    spec2.name                             \"prvsOrdName\"\n"
			+ "        ,\n"
			+ "    spec3.name                             \"prvsOrdTargetName\"\n"
			+ "        ,\n"
			+ "    CASE\n"
			+ "    WHEN spec1.code IN (5, 186, 204) OR spec1.high IN (5, 186, 204)\n"
			+ "    THEN\n"
			+ "    0\n"
			+ "    ELSE\n"
			+ "    1\n"
			+ "    END                                    \"prvsKind\"\n"
			+ "        , t.NOVOR   \"novor\"\n"
			+ "        , t.F_TYPE   \"fType\"\n"
			+ "        , t.IS_OPERATION_MAIN   \"isOperationMain\"\n"
			+ "        , ROWNUM  \"id\"\n"
			+ "\n"
			+ "    %select_end%\n"
			+ "FROM (SELECT * FROM PMP_PROD.HORIZ_CALC_TEMP %where%) t\n"
			+ "    INNER JOIN MV_DICT_VERSIONS  v ON v.period = t.period\n"
			+ "    LEFT JOIN pmp_nsi_new.NSI_MED_INSURANCE_ORG sprsmo\n"
			+ "    ON sprsmo.OLD_QQ = t.SMO_ID AND sprsmo.version_id = v.sprsmo_ver\n"
			+ "    LEFT JOIN pmp_nsi_new.NSI_MOSCOW_OMS_DEPARTMENT sprlpu1\n"
			+ "    ON     sprlpu1.FIL_ID = t.LPU_ID\n"
			+ "    AND sprlpu1.version_id = v.sprlpu_ver\n"
			+ "    LEFT JOIN pmp_nsi_new.NSI_MOSCOW_OMS_DEPARTMENT sprlpu2\n"
			+ "    ON     sprlpu2.FIL_ID = t.FIL_ID\n"
			+ "    AND sprlpu2.version_id = v.sprlpu_ver\n"
			+ "    LEFT JOIN pmp_nsi_new.NSI_MOSCOW_OMS_DEPARTMENT sprlpu3\n"
			+ "    ON sprlpu3.FIL_ID = t.PRIK AND sprlpu3.version_id = v.sprlpu_ver\n"
			+ "    LEFT JOIN pmp_nsi_new.NSI_MOSCOW_OMS_DEPARTMENT sprlpu4\n"
			+ "    ON     sprlpu4.FIL_ID = t.PRIKS\n"
			+ "    AND sprlpu4.version_id = v.sprlpu_ver\n"
			+ "    LEFT JOIN pmp_nsi_new.NSI_MOSCOW_OMS_DEPARTMENT sprlpu5\n"
			+ "    ON     sprlpu5.FIL_ID = t.PRIK_BEGIN_PERIOD\n"
			+ "    AND sprlpu5.version_id = v.sprlpu_ver\n"
			+ "    LEFT JOIN pmp_nsi_new.NSI_MOSCOW_OMS_DEPARTMENT sprlpu6\n"
			+ "    ON     sprlpu6.FIL_ID = t.PRIKS_BEGIN_PERIOD\n"
			+ "    AND sprlpu6.version_id = v.sprlpu_ver\n"
			+ "    LEFT JOIN pmp_nsi_new.NSI_MOSCOW_OMS_DEPARTMENT sprlpu7\n"
			+ "    ON     sprlpu7.FIL_ID = t.LPU_ORD\n"
			+ "    AND sprlpu7.version_id = v.sprlpu_ver\n"
			+ "    LEFT JOIN PMP_NSI_NEW.PMP_REEMORF REEMORF_ORD\n"
			+ "    ON     REEMORF_ORD.CODE = t.LPU_ORD\n"
			+ "    AND REEMORF_ORD.VERSION_ID = V.REEMORF_VER\n"
			+ "    LEFT JOIN pmp_nsi_new.NSI_MEDICAL_REGISTRY_SER services1\n"
			+ "    ON     services1.code = LPAD(t.COD, 6, '0')\n"
			+ "    AND services1.version_id = v.reesus_ver\n"
			+ "    LEFT JOIN pmp_nsi_new.NSI_MED_STANDARDS services2\n"
			+ "    ON     services2.code = LPAD(t.COD, 6, '0')\n"
			+ "    AND services2.version_id = v.reesms_ver\n"
			+ "    LEFT JOIN pmp_nsi_new.NSI_MKB_DIAGNOSES ds\n"
			+ "    ON ds.code = t.DS AND ds.version_id = v.mkb10__ver\n"
			+ "    LEFT JOIN pmp_nsi_new.NSI_INTERRUPT_MS kpresl\n"
			+ "    ON kpresl.code = t.TIP AND kpresl.version_id = v.kpresl_ver\n"
			+ "    LEFT JOIN pmp_nsi_new.NSI_MEDICAL_CARE_RESULT rslt\n"
			+ "    ON rslt.code = t.RSLT AND rslt.version_id = v.rsv009_ver\n"
			+ "    LEFT JOIN pmp_nsi_new.NSI_OUTCOME_DISEASE ishod\n"
			+ "    ON ishod.code = t.ISHOD AND ishod.version_id = v.isv012_ver\n"
			+ "    LEFT JOIN pmp_nsi_new.NSI_BED_PROFILE_DEPARTMENT profot\n"
			+ "    ON profot.code = t.PROFOT AND profot.version_id = v.profot_ver\n"
			+ "    LEFT JOIN pmp_nsi_new.NSI_MEDICAL_CARE_PROFILE profil\n"
			+ "    ON profil.code = t.PROFIL AND profil.version_id = v.prv002_ver\n"
			+ "    LEFT JOIN pmp_nsi_new.NSI_AGE_GROUP_POPULATION vozobs\n"
			+ "    ON vozobs.code = t.AGE_CAT AND vozobs.version_id = v.vozobs_ver\n"
			+ "    LEFT JOIN pmp_nsi_new.NSI_SURGERY_OPERATION hopff_\n"
			+ "    ON hopff_.code = t.CODHO AND hopff_.version_id = v.hopff__ver\n"
			+ "    LEFT JOIN pmp_nsi_new.NSI_CASE_REGISTRY_PATIENT osoree\n"
			+ "    ON     osoree.code = t.PAT_D_TYPE\n"
			+ "    AND osoree.version_id = v.osoree_ver\n"
			+ "    LEFT JOIN pmp_nsi_new.NSI_CASE_ACCOUNT_PATIENT os_s\n"
			+ "    ON os_s.code = t.BILL_D_TYPE AND os_s.version_id = v.ososch_ver\n"
			+ "    LEFT JOIN pmp_nsi_new.NSI_MEDICAL_SPECIALITY spec1\n"
			+ "    ON spec1.code = t.PRVS AND spec1.version_id = v.spv015_ver\n"
			+ "    LEFT JOIN pmp_nsi_new.NSI_MEDICAL_SPECIALITY spec2\n"
			+ "    ON spec2.code = t.PRVS_ORD AND spec2.version_id = v.spv015_ver\n"
			+ "    LEFT JOIN pmp_nsi_new.NSI_MEDICAL_SPECIALITY spec3\n"
			+ "    ON     spec3.code = t.PRVS_ORD_TARGET\n"
			+ "    AND spec3.version_id = v.spv015_ver\n"
			+ "\n"
			+ "    %pagination_end%";

}
