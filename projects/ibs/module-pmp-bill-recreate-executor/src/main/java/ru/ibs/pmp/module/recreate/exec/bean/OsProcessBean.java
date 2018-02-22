package ru.ibs.pmp.module.recreate.exec.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.ibs.pmp.api.interfaces.bill.RecreateBillsFeature;

/**
 * @author NAnishhenko
 */
public class OsProcessBean {

//    private Pattern patternToDefineLpuAndPeriod = Pattern.compile("^.+?(-.+?)\\s([\\d])+?\\s(\\d\\d\\d\\d-\\d\\d).+$");
    private Pattern patternToDefineLpuAndPeriod = Pattern.compile("^.+?(-.+?)\\s(\\d+?)\\s(\\d\\d\\d\\d-\\d\\d).*$");

    private final String processName;
    private final Integer processId;
    private final String processCmd;
    private final String type;
    private final Integer lpuId;
    private final Date period;

    public OsProcessBean(String processName, Integer processId, String processCmd) {
        this.processName = processName;
        this.processId = processId;
        this.processCmd = processCmd;
        Date period_ = null;
        Integer lpuId_ = null;
        String type_ = null;
        if (processCmd != null) {
            Matcher matcher = patternToDefineLpuAndPeriod.matcher(processCmd.substring(processCmd.lastIndexOf("-jar") + "-jar".length()));
            if (matcher.find()) {
                String type__ = matcher.group(1);
                if (type__.startsWith("-m")) {
                    type_ = RecreateBillsFeature.NAME;
                } else if (type__.startsWith("-s")) {
                    type_ = RecreateBillsFeature.SEND;
                } else if (type__.startsWith("-p")) {
                    type_ = RecreateBillsFeature.CALC_POSOBR;
                }
                lpuId_ = Integer.valueOf(matcher.group(2));
                String periodStr = matcher.group(3);
                try {
                    period_ = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss_SSS").parse(periodStr + "-01 00:00:00_000");
                } catch (ParseException ex) {
                    Logger.getLogger(OsProcessBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        lpuId = lpuId_;
        period = period_;
        type = type_;
    }

    public String getProcessName() {
        return processName;
    }

    public Integer getProcessId() {
        return processId;
    }

    public String getProcessCmd() {
        return processCmd;
    }

    public String getType() {
        return type;
    }

    public Integer getLpuId() {
        return lpuId;
    }

    public Date getPeriod() {
        return period;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.type);
        hash = 29 * hash + Objects.hashCode(this.lpuId);
        hash = 29 * hash + Objects.hashCode(this.period);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OsProcessBean other = (OsProcessBean) obj;
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }
        if (!Objects.equals(this.lpuId, other.lpuId)) {
            return false;
        }
        if (!Objects.equals(this.period, other.period)) {
            return false;
        }
        return true;
    }

}
