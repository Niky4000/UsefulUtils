package ru.kiokle.xlstest;

import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;

public class XlsTestStart {

	public static void main(String[] args) throws Exception {
		System.out.println("Hello World!!!");
		org.apache.commons.io.IOUtils.byteArray();
		final String leftHeader = "Российская Федерация\n"
				+ "Центральный федеральный округ \n"
				+ "  Белгородская область\n"
				+ "  Брянская область\n"
				+ "  Владимирская область\n"
				+ "  Воронежская область\n"
				+ "  Ивановская область\n"
				+ "  Калужская область\n"
				+ "  Костромская область\n"
				+ "  Курская область\n"
				+ "  Липецкая область\n"
				+ "  Московская область\n"
				+ "  Орловская область\n"
				+ "  Рязанская область\n"
				+ "  Смоленская область\n"
				+ "  Тамбовская область\n"
				+ "  Тверская область\n"
				+ "  Тульская область\n"
				+ "  Ярославская область\n"
				+ "  г. Москва\n"
				+ "Северо-Западный федеральный округ \n"
				+ "  Республика Карелия\n"
				+ "  Республика Коми\n"
				+ "  Архангельская область\n"
				+ "  Вологодская область\n"
				+ "  Калининградская область\n"
				+ "  Ленинградская область\n"
				+ "  Мурманская область\n"
				+ "  Новгородская область\n"
				+ "  Псковская область\n"
				+ "  г. Санкт-Петербург\n"
				+ "  Ненецкий автономный округ\n"
				+ "Приволжский федеральный округ \n"
				+ "  Республика Башкортостан\n"
				+ "  Республика Марий Эл\n"
				+ "  Республика Мордовия\n"
				+ "  Республика Татарстан (Татарстан)\n"
				+ "  Удмуртская Республика\n"
				+ "  Чувашская Республика - Чувашия\n"
				+ "  Кировская область\n"
				+ "  Нижегородская область\n"
				+ "  Оренбургская область\n"
				+ "  Пензенская область\n"
				+ "  Пермский край\n"
				+ "  Самарская область\n"
				+ "  Саратовская область\n"
				+ "  Ульяновская область\n"
				+ "Уральский федеральный округ \n"
				+ "  Курганская область\n"
				+ "  Свердловская область\n"
				+ "  Тюменская область\n"
				+ "  Челябинская область\n"
				+ "  Ханты-Мансийский автономный округ - Югра\n"
				+ "  Ямало-Ненецкий автономный округ\n"
				+ "Северо-Кавказский федеральный округ \n"
				+ "  Республика Дагестан\n"
				+ "  Республика Ингушетия\n"
				+ "  Кабардино-Балкарская Республика\n"
				+ "  Карачаево-Черкесская Республика\n"
				+ "  Республика Северная Осетия - Алания\n"
				+ "  Чеченская Республика\n"
				+ "  Ставропольский край\n"
				+ "Южный федеральный округ \n"
				+ "  Республика Адыгея (Адыгея)\n"
				+ "  Республика Калмыкия\n"
				+ "  Краснодарский край\n"
				+ "  Астраханская область\n"
				+ "  Волгоградская область\n"
				+ "  Ростовская область\n"
				+ "  Республика Крым\n"
				+ "  г. Севастополь\n"
				+ "Сибирский федеральный округ \n"
				+ "  Республика Алтай\n"
				+ "  Республика Тыва\n"
				+ "  Республика Хакасия\n"
				+ "  Алтайский край\n"
				+ "  Красноярский край\n"
				+ "  Иркутская область\n"
				+ "  Кемеровская область\n"
				+ "  Новосибирская область\n"
				+ "  Омская область\n"
				+ "  Томская область\n"
				+ "Дальневосточный федеральный округ \n"
				+ "  Республика Бурятия\n"
				+ "  Республика Саха (Якутия)\n"
				+ "  Приморский край\n"
				+ "  Хабаровский край\n"
				+ "  Амурская область\n"
				+ "  Камчатский край\n"
				+ "  Магаданская область\n"
				+ "  Сахалинская область\n"
				+ "  Забайкальский край\n"
				+ "  Еврейская автономная область\n"
				+ "  Чукотский автономный округ\n"
				+ "Байконур\n"
				+ "  г. Байконур\n"
				+ "Федеральный округ № 9\n"
				+ "  Донецкая Народная Республика\n"
				+ "  Луганская Народная Республика\n"
				+ "  Запорожская область\n"
				+ "  Херсонская область";
		String[] leftHeaderArray = leftHeader.split("\n");
		int i = 0;
		List<List<Object>> dataList = Arrays.asList(Arrays.asList("Head First Java", "Kathy Serria", 79, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("Effective Java", "Joshua Bloch", 36, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("Clean Code", "Robert martin", 42, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("Thinking in Java", "Bruce Eckel", 35, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"),
				Arrays.asList("ZZZZZZZZ" + (++i), "ZZZZZZZZZ", 4444, "1", "2", "3", "4", "5", "6", "7"));
		try ( FileOutputStream outputStream = new FileOutputStream("/home/me/tmp/DemoVersion.xlsx")) {
			new AttachedAndInsuredPersonsReport().createXls(dataList, leftHeaderArray, outputStream);
		}
	}
}
