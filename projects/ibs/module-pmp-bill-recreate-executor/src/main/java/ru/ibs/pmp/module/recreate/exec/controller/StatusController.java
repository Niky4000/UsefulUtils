package ru.ibs.pmp.module.recreate.exec.controller;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.ibs.pmp.auth.PermissionsSecured;
import ru.ibs.pmp.module.recreate.exec.ExecuteRecreate;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Parfenov on 02.03.2017.
 */
@Controller
@RequestMapping(value = "/status", produces = MediaType.APPLICATION_JSON_VALUE)
public class StatusController {

    private static final Map<Boolean, String> statuses = new HashMap<>();

    static {
        statuses.put(Boolean.TRUE, "WORK");
        statuses.put(Boolean.FALSE, "NO WORK");
    }

    @Autowired
    private ExecuteRecreate executeRecreate;


    @RequestMapping("/start")
    @PermissionsSecured("RECREATE_CREATE")
    public ResponseEntity start() {
        executeRecreate.setCanStartExecutor(Boolean.TRUE.toString());
        ResponseEntity entity = new ResponseEntity<>(HttpStatus.OK);
        return entity;
    }

    @RequestMapping("/stop")
    @PermissionsSecured("RECREATE_DELETE")
    public ResponseEntity stop() {
        executeRecreate.setCanStartExecutor(Boolean.FALSE.toString());
        ResponseEntity entity = new ResponseEntity<>(HttpStatus.OK);
        return entity;
    }

    @RequestMapping("/getStatus")
    @PermissionsSecured("RECREATE_READ")
    public ResponseEntity getStatus() {
        String result = !Strings.isNullOrEmpty(executeRecreate.getCanStartExecutor())
                && executeRecreate.getCanStartExecutor().equals(Boolean.TRUE.toString()) ?
                statuses.get(Boolean.TRUE) :
                statuses.get(Boolean.FALSE);
        ResponseEntity entity = new ResponseEntity<>(result, HttpStatus.OK);
        return entity;

    }
}
