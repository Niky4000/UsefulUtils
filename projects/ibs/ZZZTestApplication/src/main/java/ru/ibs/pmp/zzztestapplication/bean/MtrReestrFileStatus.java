/**
 * 
 */
package ru.ibs.pmp.zzztestapplication.bean;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.base.Strings;
import ru.mgfoms.pump.mtr.ref.FileType;

/**
 * состояние файла реестра счетов
 * 
 * @author Ю.Артёмов
 *
 * Мы вынуждены применять тип Date для полей, снабженных аннотацией JsonFormat, 
 * так как в Java8 эта аннотация поддерживает только этот тип, но не LocalDate и т.п. 
 */
public class MtrReestrFileStatus {
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	public final Date period;
	public final long id;
	public final String nschet;
	public final String cOkato1;
	public final String okatoOms;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	public final Date dschet;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	public final Date whenUnloaded;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	public final Date whenSent;
	public final FileType ft;
	public int numZap;
	public int numFlkErrs;
	public final int numMekErrs;
	public final String curFname;
	public final BigDecimal sumV;
	public final BigDecimal sumP;
	public final BigDecimal sumMek;
	public final String status;
	public final int rev;
	public final BigDecimal sumPaid;		// оплачено

	public MtrReestrFileStatus(Date period, long id, String nschet, String cOkato1, String okatoOms, Date dschet, Date whenUnloaded, Date whenSent,
			FileType ft, int numZap, int numFlkErrs, int numMekErrs, String curFname, BigDecimal sumV, BigDecimal sumP,
			BigDecimal sumMek, int rev, BigDecimal sumPaid) {
		this.period = period;
		this.id = id;
		this.nschet = nschet;
		this.cOkato1 = cOkato1;
		this.okatoOms = okatoOms;
		this.dschet = dschet;
		this.whenUnloaded = whenUnloaded;
		this.whenSent = whenSent;
		this.ft = ft;
		this.numZap = numZap;
		this.numFlkErrs = numFlkErrs;
		this.numMekErrs = numMekErrs;
		this.curFname = curFname;
		this.sumV = sumV;
		this.sumP = sumP;
		this.sumMek = sumMek;
		this.status = (null != whenSent) ? "отослан"
				: (null != whenUnloaded) ? "выгружен"
						: (!Strings.isNullOrEmpty(curFname)
								&& curFname.toUpperCase().endsWith(".OMS")
								&& (0 == numFlkErrs)) ? "сформирован" : "есть ошибки";
		this.rev = rev;
		this.sumPaid = sumPaid;
	}
}
