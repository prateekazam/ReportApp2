package com.blog.component.reoprt;


import com.blog.component.utils.DeclarativeComponentUtil;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import oracle.adf.view.rich.component.rich.data.RichTable;

import oracle.jbo.uicli.binding.JUCtrlHierNodeBinding;

import org.apache.myfaces.trinidad.model.CollectionModel;


public class PDFReport {
    static UIComponent uiComponent = null;

    public static byte[] pdfReport(RichTable richTableBinding,
                                   Boolean serialNumber) throws DocumentException,
                                                                IOException {
        Document document = new Document();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, buffer);
        document.setPageSize(PageSize.A4);
        document.setMargins(30.5f, 30.5f, 30.5f, 30.5f);
        CreateFooter footerEvent = new CreateFooter();
        Boolean pagination =
            (Boolean)FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("pagination");
        if (pagination) {
            writer.setPageEvent(footerEvent);
        }
        document.open();
        document.newPage();
        document.add(Chunk.NEWLINE);
        List<String> columnHeaderList =
            DeclarativeComponentUtil.getColumnsHeader(richTableBinding.getChildren());
        PdfPTable table = null;
        if (serialNumber) {
            table = new PdfPTable(columnHeaderList.size() + 1);
            table.setWidths(DeclarativeComponentUtil.createDynamicWidth(columnHeaderList.size() +
                                                                        1));
        } else {
            table = new PdfPTable(columnHeaderList.size());
            table.setWidths(DeclarativeComponentUtil.createDynamicWidth(columnHeaderList.size()));
        }
        table.setWidthPercentage(100);

        DeclarativeComponentUtil.createTableHeaderForPdf(serialNumber,
                                                         columnHeaderList,
                                                         table);
        List<Integer> getRenderedColumnId =
            DeclarativeComponentUtil.getRenderedColumnIds(richTableBinding.getChildren());
        int i = 0;
        CollectionModel cm = (CollectionModel)richTableBinding.getValue();
        for (int k = 0; k < cm.getEstimatedRowCount(); k++) {
            JUCtrlHierNodeBinding ds = (JUCtrlHierNodeBinding)cm.getRowData(k);
            if (serialNumber) {
                table.addCell(++i + "");
            }
            for (int h = 0; h < getRenderedColumnId.size(); h++) {
                if (ds.getAttribute(getRenderedColumnId.get(h)) != null) {
                    table.addCell(ds.getAttribute(getRenderedColumnId.get(h)).toString());
                } else {
                    table.addCell("");
                }
            }
        }
        try {
            document.add(table);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);
        // step 5: we close the document
        document.close();
        return buffer.toByteArray();
    }


}

