package com.blog.component.backingbean;


import com.blog.component.ReportDeclarative;
import com.blog.component.constraints.DeclarativeComponentConstraint;
import com.blog.component.reoprt.CSVReport;
import com.blog.component.reoprt.HTMLReport;
import com.blog.component.reoprt.PDFReport;
import com.blog.component.reoprt.XMLReport;
import com.blog.component.utils.DeclarativeComponentUtil;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.OutputStream;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import javax.servlet.http.HttpServletResponse;

import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.component.rich.fragment.RichRegion;
import oracle.adf.view.rich.component.rich.nav.RichCommandButton;
import oracle.adf.view.rich.context.AdfFacesContext;

import org.apache.myfaces.trinidad.render.ExtendedRenderKitService;
import org.apache.myfaces.trinidad.util.Service;


public class DeclarativeComponentBean {
    public UIComponent uIComponent;

    public void fileDownloadMethod(FacesContext facesContext,
                                   OutputStream outputStream) {
        try {
            ExternalContext externalContext =
                facesContext.getExternalContext();
            HttpServletResponse response =
                (HttpServletResponse)externalContext.getResponse();
            ReportDeclarative reportDeclarativeComponent =
                (ReportDeclarative)DeclarativeComponentUtil.getComponentValue();
            String reportType =
                (String)reportDeclarativeComponent.getAttributes().get(DeclarativeComponentConstraint.REPORT_TYPE);
            String tableId =
                (String)reportDeclarativeComponent.getAttributes().get(DeclarativeComponentConstraint.TABLE_ID);
            Boolean serialNumber =
                (Boolean)reportDeclarativeComponent.getAttributes().get(DeclarativeComponentConstraint.SERIAL_NUMBER);
            String rowTag =
                (String)reportDeclarativeComponent.getAttributes().get(DeclarativeComponentConstraint.ROW_TAG_XML);
            String parentTag =
                (String)reportDeclarativeComponent.getAttributes().get(DeclarativeComponentConstraint.PARENT_TAG_XML);
            Boolean pagination =
                (Boolean)reportDeclarativeComponent.getAttributes().get(DeclarativeComponentConstraint.PAGINATION);
            FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("pagination",
                                                                                       pagination);
            if (serialNumber == null) {
                serialNumber = false;
            } else {
                String serialColumnHeader =
                    (String)reportDeclarativeComponent.getAttributes().get(DeclarativeComponentConstraint.SERIAL_HEADER);
                FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("serialColumnHeader",
                                                                                           serialColumnHeader);
            }
            RichCommandButton declarativeCommandButton =
                (RichCommandButton)AdfFacesContext.getCurrentInstance().getPageFlowScope().get(DeclarativeComponentConstraint.BUTTON_REFERENCE);
            UIComponent region = getParent(declarativeCommandButton);
            UIComponent richTable = null;
            if (region != null) {
                richTable = region.findComponent(tableId);
                System.out.println("--------------" + richTable.getFamily());
            } else {
                richTable =
                        FacesContext.getCurrentInstance().getViewRoot().findComponent(tableId);
            }
            if (reportType.equalsIgnoreCase(DeclarativeComponentConstraint.PDF)) {
                String fileName =
                    reportDeclarativeComponent.getAttributes().get(DeclarativeComponentConstraint.FILE_NAME) +
                    DeclarativeComponentConstraint.PDF_EXTENSION;
                response.setContentType(DeclarativeComponentConstraint.CONTENT_TYPE_PDF);
                response.addHeader(DeclarativeComponentConstraint.CONTENT_TYPE,
                                   DeclarativeComponentConstraint.CONTENT_TYPE_PDF);
                response.addHeader(DeclarativeComponentConstraint.CONTENT_DISPOSITION,
                                   DeclarativeComponentConstraint.ATTACGMENT_FILENAME +
                                   fileName);
                byte[] bytes =
                    PDFReport.pdfReport((RichTable)richTable, serialNumber);
                DataOutput output =
                    new DataOutputStream(response.getOutputStream());
                response.setContentLength(bytes.length);
                for (int i = 0; i < bytes.length; i++) {
                    output.writeByte(bytes[i]);
                }
                outputStream.close();
            } else if (reportType.equalsIgnoreCase(DeclarativeComponentConstraint.CSV)) {
                String fileName =
                    reportDeclarativeComponent.getAttributes().get(DeclarativeComponentConstraint.FILE_NAME) +
                    DeclarativeComponentConstraint.CSV_EXTENSION;
                response.setContentType(DeclarativeComponentConstraint.CONTENT_TYPE_CSV);
                response.addHeader(DeclarativeComponentConstraint.CONTENT_TYPE,
                                   DeclarativeComponentConstraint.CONTENT_TYPE_CSV);
                response.addHeader(DeclarativeComponentConstraint.CONTENT_DISPOSITION,
                                   DeclarativeComponentConstraint.ATTACGMENT_FILENAME +
                                   fileName);
                CSVReport.csvReport((RichTable)richTable, outputStream,
                                    serialNumber);
            } else if (reportType.equalsIgnoreCase(DeclarativeComponentConstraint.HTML)) {
                String fileName =
                    reportDeclarativeComponent.getAttributes().get(DeclarativeComponentConstraint.FILE_NAME) +
                    DeclarativeComponentConstraint.HTML_EXTENSION;
                response.setContentType(DeclarativeComponentConstraint.CONTENT_TYPE_HTML);
                response.addHeader(DeclarativeComponentConstraint.CONTENT_TYPE,
                                   DeclarativeComponentConstraint.CONTENT_TYPE_HTML);
                response.addHeader(DeclarativeComponentConstraint.CONTENT_DISPOSITION,
                                   DeclarativeComponentConstraint.ATTACGMENT_FILENAME +
                                   fileName);
                HTMLReport.htmlReport((RichTable)richTable, outputStream,
                                      serialNumber);
            } else if (reportType.equalsIgnoreCase(DeclarativeComponentConstraint.XML)) {
                String fileName =
                    reportDeclarativeComponent.getAttributes().get(DeclarativeComponentConstraint.FILE_NAME) +
                    DeclarativeComponentConstraint.XML_EXTENSION;
                response.setContentType(DeclarativeComponentConstraint.CONTENT_TYPE_XML);
                response.addHeader(DeclarativeComponentConstraint.CONTENT_TYPE,
                                   DeclarativeComponentConstraint.CONTENT_TYPE_HTML);
                response.addHeader(DeclarativeComponentConstraint.CONTENT_DISPOSITION,
                                   DeclarativeComponentConstraint.ATTACGMENT_FILENAME +
                                   fileName);
                XMLReport.xmlReport((RichTable)richTable, outputStream,
                                    serialNumber, parentTag, rowTag);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        facesContext.responseComplete();
    }

    public UIComponent getParent(UIComponent uicom) {

        if (uicom instanceof RichRegion) {
            uIComponent = uicom;
            return uIComponent;
        } else {
            if (uicom != null) {
                getParent(uicom.getParent());
            }
        }
        return uIComponent;
    }


    public void buttonActionListener(ActionEvent actionEvent) {
        RichCommandButton richCommandButton =
            (RichCommandButton)actionEvent.getComponent();
        AdfFacesContext.getCurrentInstance().getPageFlowScope().put(DeclarativeComponentConstraint.BUTTON_REFERENCE,
                                                                    richCommandButton);
        RichCommandButton declarativeCommandButton =
            (RichCommandButton)AdfFacesContext.getCurrentInstance().getPageFlowScope().get(DeclarativeComponentConstraint.BUTTON_REFERENCE);
        ReportDeclarative reportDeclarativeComponent =
            (ReportDeclarative)DeclarativeComponentUtil.getComponentValue();
        String tableId =
            (String)reportDeclarativeComponent.getAttributes().get(DeclarativeComponentConstraint.TABLE_ID);
        String reportType =
            (String)reportDeclarativeComponent.getAttributes().get(DeclarativeComponentConstraint.REPORT_TYPE);
        String rowTag =
            (String)reportDeclarativeComponent.getAttributes().get(DeclarativeComponentConstraint.ROW_TAG_XML);
        String parentTag =
            (String)reportDeclarativeComponent.getAttributes().get(DeclarativeComponentConstraint.PARENT_TAG_XML);
        String serialColumnHeader =
            (String)reportDeclarativeComponent.getAttributes().get(DeclarativeComponentConstraint.SERIAL_HEADER);
        Boolean serialNumber =
            (Boolean)reportDeclarativeComponent.getAttributes().get(DeclarativeComponentConstraint.SERIAL_NUMBER);
        UIComponent region = getParent(declarativeCommandButton);
        UIComponent richTable = null;
        if (region != null) {
            richTable = region.findComponent(tableId);
        } else {
            richTable =
                    FacesContext.getCurrentInstance().getViewRoot().findComponent(tableId);
        }
        String id =
            richCommandButton.getClientId(FacesContext.getCurrentInstance());
        if (richTable != null) {
            if (serialNumber) {
                if (serialColumnHeader == null) {
                    DeclarativeComponentUtil.showErrorMessage("If you are generation Report with serial number then you should have to provide Serial Column Header value.");
                    return;
                }
            }
            if (reportType.equalsIgnoreCase(DeclarativeComponentConstraint.PDF)) {
                DeclarativeComponentUtil.callJavaScript(id);
            } else if (reportType.equalsIgnoreCase(DeclarativeComponentConstraint.CSV)) {
                DeclarativeComponentUtil.callJavaScript(id);

            } else if (reportType.equalsIgnoreCase(DeclarativeComponentConstraint.HTML)) {
                DeclarativeComponentUtil.callJavaScript(id);
            } else if (reportType.equalsIgnoreCase(DeclarativeComponentConstraint.XML)) {
                if (rowTag == null || parentTag == null) {
                    DeclarativeComponentUtil.showErrorMessage(" Row tag or Parent Tag value not be null when you are generating XML report");
                } else {
                    DeclarativeComponentUtil.callJavaScript(id);
                }
            } else {
                DeclarativeComponentUtil.showErrorMessage("Report Type Should be XML/HTML/PDF/CSV");
            }

        } else {
            DeclarativeComponentUtil.showErrorMessage("This [" + tableId +
                                                      "] id is not present in the current view port ");
        }


    }
}
