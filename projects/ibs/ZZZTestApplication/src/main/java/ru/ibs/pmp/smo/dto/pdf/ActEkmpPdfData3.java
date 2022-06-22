package ru.ibs.pmp.smo.dto.pdf;

import java.util.List;
import ru.ibs.pmp.api.service.export.msk.pdf.bean.PdfData;
import ru.ibs.pmp.smo.report.model.ActEkmpPdfDataBody2;
import ru.ibs.pmp.smo.report.model.ActEkmpPdfDataBody3;
import ru.ibs.pmp.smo.report.model.ActMeePdfDataTable3;
import ru.ibs.pmp.smo.report.model.ActEkmpPdfDataTable4;

public class ActEkmpPdfData3 extends PdfData {

    private ActEkmpPdfDataBody2 b;
    private List<ActMeePdfDataTable3> t;
    private List<ActEkmpPdfDataTable4> t2;
    private List<ActEkmpPdfDataBody3> b2;

    public ActEkmpPdfDataBody2 getB() {
        return b;
    }

    public void setB(ActEkmpPdfDataBody2 b) {
        this.b = b;
    }

    public List<ActMeePdfDataTable3> getT() {
        return t;
    }

    public void setT(List<ActMeePdfDataTable3> t) {
        this.t = t;
    }

    public List<ActEkmpPdfDataTable4> getT2() {
        return t2;
    }

    public void setT2(List<ActEkmpPdfDataTable4> t2) {
        this.t2 = t2;
    }

    public List<ActEkmpPdfDataBody3> getB2() {
        return b2;
    }

    public void setB2(List<ActEkmpPdfDataBody3> b2) {
        this.b2 = b2;
    }
}
