package ru.ibs.pmp.zzztestapplication.bean;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum ReportExportFileType {

	PRELIMINARY("Предварительный"), 
	FINAL("Финальный"), 
	EXAMINATION("Экспертиза"),
	OLD_FINAL("Старый финальный");//OLD_FINAL для переделки

	private static final Map<String, ReportExportFileType> userStringMap = Arrays.stream(ReportExportFileType.values()).collect(Collectors.toMap(b -> b.getUserString(), b -> b));
	private final String userString;
	
	private ReportExportFileType(String userString) {
		this.userString=userString;
	}
	
	public String getUserString() {
		return userString;
	}

	public static ReportExportFileType getValueByUserString(String userString) {
		return userStringMap.get(userString);
	}
}
