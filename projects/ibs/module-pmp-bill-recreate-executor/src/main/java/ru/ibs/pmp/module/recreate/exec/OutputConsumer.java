package ru.ibs.pmp.module.recreate.exec;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author NAnishhenko
 */
public class OutputConsumer extends Thread {

    private static final Logger log = LoggerFactory.getLogger(ExecuteRecreate.class);
    private final Process process;
    private final StringBuilder stringBuilder;
    List<String> responseData = new ArrayList<String>();
    private final boolean agregateResult;

    public OutputConsumer(Process process, boolean agregateResult) {
        this.process = process;
        stringBuilder = new StringBuilder();
        this.agregateResult = agregateResult;
    }

    @Override
    public void run() {
        log.debug("OutputConsumer started");
        BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
//        byte[] buff = new byte[1024];
        int r = 0;
        
        try {
            String line;
            while ((line = in.readLine()) != null) {
                responseData.add(line.replaceAll("\\s+", " ").trim());
            }
        } catch (Throwable e) {
            log.error("error consuming output", e);
        }
        responseData.stream().forEach(str -> stringBuilder.append(str));
        log.debug("OutputConsumer finished");
    }

    public String getProcessOutput() {
        return stringBuilder.toString();
    }

    public List<String> getResponseData() {
        return responseData;
    }
   
}
