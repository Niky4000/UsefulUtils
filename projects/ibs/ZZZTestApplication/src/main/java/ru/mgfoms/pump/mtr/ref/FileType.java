package ru.mgfoms.pump.mtr.ref;

/**
 * типы файлов, применяемые в МТР
 * @author jartemov
 *
 */
public enum FileType {
	R("R"),
	D("D"),
	A("A"),
	PL("P"),
	Y("Y");
	
	FileType(String code) {
		this.code = code;
	}
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	private String code;
	
}
