package ru.ibs.pmp.module.recreate.exec.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.ibs.pmp.service.ListPermissionsService;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Parfenov on 02.05.2017.
 */
@Controller
@RequestMapping(value = "/permissions", produces = MediaType.APPLICATION_JSON_VALUE)
public class PermissionsController {

    @Autowired
    private ListPermissionsService listPermissionsService;

    @RequestMapping(value = { "/", "" }, method = RequestMethod.GET)
    @ResponseBody
    public Map<String, String[]> listPermissions() throws IOException {
        return listPermissionsService.listPermissions();
    }
}
