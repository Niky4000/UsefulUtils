/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.testpumputils;

import ru.ibs.pmp.api.nsi.interfaces.FindNsiEntries;
import ru.ibs.pmp.nsi.features.impl.FindNsiEntriesFeature;
import ru.ibs.pmp.service.check.msk.CheckPmpDir11;
import ru.ibs.testpumputils.utils.FieldUtil;

/**
 *
 * @author Me
 */
public class CheckPmpDir11Test {

    public static void test() throws Exception {
        CheckPmpDir11 checkPmpDir11 = new CheckPmpDir11("");

        FindNsiEntries findNsiEntries = new FindNsiEntriesFeature();
        ru.ibs.pmp.nsi.service.NsiServiceImpl nsiServiceImpl = new ru.ibs.pmp.nsi.service.NsiServiceImpl();
        FieldUtil.setField(findNsiEntries, nsiServiceImpl, "nsiService");
    }
}
