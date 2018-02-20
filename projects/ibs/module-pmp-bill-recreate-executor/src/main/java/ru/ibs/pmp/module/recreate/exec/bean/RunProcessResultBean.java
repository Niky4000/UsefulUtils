package ru.ibs.pmp.module.recreate.exec.bean;

import java.util.List;
import ru.ibs.pmp.module.recreate.exec.OutputConsumer;

/**
 * @author NAnishhenko
 */
public class RunProcessResultBean {

    private final boolean result;
    private final String processOutput;
    private final List<String> responseData;

    public RunProcessResultBean(boolean result, String processOutput,List<String> responseData) {
        this.result = result;
        this.processOutput = processOutput;
        this.responseData=responseData;
    }

    public boolean isResult() {
        return result;
    }

    public String getProcessOutput() {
        return processOutput;
    }

    public List<String> getResponseData() {
        return responseData;
    }

}
