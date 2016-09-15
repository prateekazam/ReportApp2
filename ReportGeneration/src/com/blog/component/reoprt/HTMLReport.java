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

public class HTMLReport {
    public static void htmlReport(RichTable richTableBinding,
                                  OutputStream outputStream,
                                  Boolean serialNumber) throws DocumentException,
                                                               IOException {
        OutputStreamWriter outputStreamWrite =
            new OutputStreamWriter(outputStream);

        List<String> columnHeaderList =
            DeclarativeComponentUtil.getColumnsHeader(richTableBinding.getChildren());
        createHtmlHeader(serialNumber, columnHeaderList, outputStreamWrite);
        List<Integer> getRenderedColumnId =
            DeclarativeComponentUtil.getRenderedColumnIds(richTableBinding.getChildren());
        int i = 0;
        CollectionModel cm = (CollectionModel)richTableBinding.getValue();
        for (int k = 0; k < cm.getEstimatedRowCount(); k++) {
            outputStreamWrite.write("<tr>");
            JUCtrlHierNodeBinding ds = (JUCtrlHierNodeBinding)cm.getRowData(k);
            if (serialNumber) {
                outputStreamWrite.write("<td>");
                outputStreamWrite.write(++i + "");
                outputStreamWrite.write("</td>");
            }
            for (int h = 0; h < getRenderedColumnId.size(); h++) {

                if (ds.getAttribute(getRenderedColumnId.get(h)) != null) {
                    outputStreamWrite.write("<td>");
                    outputStreamWrite.write(ds.getAttribute(getRenderedColumnId.get(h)).toString());
                    outputStreamWrite.write("</td>");
                } else {
                    outputStreamWrite.write("<td>");
                    outputStreamWrite.write("");
                    outputStreamWrite.write("</td>");
                }
            }
            outputStreamWrite.write("</tr>");
        }
        createHtmlFooter(outputStreamWrite);
        outputStreamWrite.close();

    }

    public static void createHtmlHeader(boolean createSerialNumberHeader,
                                        List<String> headerList,
                                        OutputStreamWriter outputStreamWrite) throws IOException {
        outputStreamWrite.write("<html>\n" +
                "<body>\n" +
                "<table border=\"1\"><tr>");
        if (createSerialNumberHeader) {
            outputStreamWrite.write("<td>");
            outputStreamWrite.write(FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("serialColumnHeader").toString());
            outputStreamWrite.write("</td>");
        }
        for (String headerName : headerList) {
            outputStreamWrite.write("<td>");
            outputStreamWrite.write(headerName);
            outputStreamWrite.write("</td>");
        }
        outputStreamWrite.write("</tr>");
    }

    public static void createHtmlFooter(OutputStreamWriter outputStreamWrite) throws IOException {
        outputStreamWrite.write("</table>\n" +
                "</body>\n" +
                "</html>");
    }

}
