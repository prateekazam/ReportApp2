package com.blog.component.reoprt;

import com.blog.component.utils.DeclarativeComponentUtil;

import com.itextpdf.text.DocumentException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.util.List;

import javax.faces.context.FacesContext;

import oracle.adf.view.rich.component.rich.data.RichTable;

import oracle.jbo.uicli.binding.JUCtrlHierNodeBinding;

import org.apache.myfaces.trinidad.model.CollectionModel;

public class XMLReport {
    public static void xmlReport(RichTable richTableBinding,
                                 OutputStream outputStream,
                                 Boolean serialNumber, String parentTag,
                                 String rowTag) throws DocumentException,
                                                       IOException {
        OutputStreamWriter outputStreamWrite =
            new OutputStreamWriter(outputStream);
        List<String> columnHeaderList =
            DeclarativeComponentUtil.getColumnsHeader(richTableBinding.getChildren());
        List<Integer> getRenderedColumnId =
            DeclarativeComponentUtil.getRenderedColumnIds(richTableBinding.getChildren());
        int i = 0;
        CollectionModel cm = (CollectionModel)richTableBinding.getValue();
        outputStreamWrite.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
        outputStreamWrite.write("<" + parentTag + ">");
        for (int k = 0; k < cm.getEstimatedRowCount(); k++) {
            outputStreamWrite.write("<" + rowTag + ">");
            JUCtrlHierNodeBinding ds = (JUCtrlHierNodeBinding)cm.getRowData(k);
            if (serialNumber) {
                outputStreamWrite.write("<" +
                                        FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("serialColumnHeader").toString() +
                                        ">");
                outputStreamWrite.write(++i + "");
                outputStreamWrite.write("</" +
                                        FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("serialColumnHeader").toString() +
                                        ">");
            }
            for (int h = 0; h < getRenderedColumnId.size(); h++) {

                if (ds.getAttribute(getRenderedColumnId.get(h)) != null) {
                    outputStreamWrite.write("<" + columnHeaderList.get(h) +
                                            ">");
                    outputStreamWrite.write(ds.getAttribute(getRenderedColumnId.get(h)).toString());
                    outputStreamWrite.write("</" + columnHeaderList.get(h) +
                                            ">");
                } else {
                    outputStreamWrite.write("<" + columnHeaderList.get(h) +
                                            ">");
                    outputStreamWrite.write("");
                    outputStreamWrite.write("</" + columnHeaderList.get(h) +
                                            ">");
                }
            }
            outputStreamWrite.write("</" + rowTag + ">");
        }
        outputStreamWrite.write("</" + parentTag + ">");
        outputStreamWrite.close();

    }


}
