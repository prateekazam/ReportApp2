package com.blog.component.reoprt;

import com.blog.component.utils.DeclarativeComponentUtil;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.io.OutputStream;

import java.io.OutputStreamWriter;

import java.util.List;

import oracle.adf.view.rich.component.rich.data.RichTable;

import oracle.jbo.uicli.binding.JUCtrlHierNodeBinding;

import org.apache.myfaces.trinidad.model.CollectionModel;

public class CSVReport {
    public static void csvReport(RichTable richTableBinding,
                                 OutputStream outputStream,
                                 Boolean serialNumber) throws DocumentException,
                                                              IOException {
        OutputStreamWriter outputStreamWrite =
            new OutputStreamWriter(outputStream);
        List<String> columnHeaderList =
            DeclarativeComponentUtil.getColumnsHeader(richTableBinding.getChildren());
        DeclarativeComponentUtil.createTableHeaderForCsv(serialNumber,
                                                         columnHeaderList,
                                                         outputStreamWrite);
        List<Integer> getRenderedColumnId =
            DeclarativeComponentUtil.getRenderedColumnIds(richTableBinding.getChildren());
        int i = 0;
        CollectionModel cm = (CollectionModel)richTableBinding.getValue();
        for (int k = 0; k < cm.getEstimatedRowCount(); k++) {
            JUCtrlHierNodeBinding ds = (JUCtrlHierNodeBinding)cm.getRowData(k);
            if (serialNumber) {
                outputStreamWrite.write(++i + "");
                outputStreamWrite.write(',');
            }
            for (int h = 0; h < getRenderedColumnId.size(); h++) {
                if (ds.getAttribute(getRenderedColumnId.get(h)) != null) {
                    outputStreamWrite.write(ds.getAttribute(getRenderedColumnId.get(h)).toString());
                    outputStreamWrite.write(',');
                } else {
                    outputStreamWrite.write("");
                }
            }
            outputStreamWrite.write("\n");
        }
        outputStreamWrite.close();

    }
}
