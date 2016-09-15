package com.blog.component.utils;

import com.blog.component.constraints.DeclarativeComponentConstraint;

import com.itextpdf.text.pdf.PdfPTable;

import java.io.IOException;
import java.io.OutputStreamWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.el.ELContext;

import javax.el.ExpressionFactory;

import javax.el.ValueExpression;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.view.rich.component.fragment.UIXDeclarativeComponent;


import oracle.jbo.RowSetIterator;

import org.apache.myfaces.trinidad.render.ExtendedRenderKitService;
import org.apache.myfaces.trinidad.util.Service;

public class DeclarativeComponentUtil {

    public static RowSetIterator getIterator(String iteratorName) {
        DCBindingContainer bindings =
            (DCBindingContainer)BindingContext.getCurrent().getCurrentBindingsEntry();
        DCIteratorBinding dciter = bindings.findIteratorBinding(iteratorName);
        dciter.getViewObject().getApplicationModule().getSession().getLocaleContext();
        RowSetIterator rsi = dciter.getViewObject().createRowSetIterator(null);
        rsi.reset();
        return rsi;
    }

    public static Object getAttributeValue(Map map, String key) {
        return map.get(key);
    }

    public static Object getComponentValue() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ELContext elContext = facesContext.getELContext();
        ExpressionFactory expressionFactory =
            facesContext.getApplication().getExpressionFactory();
        ValueExpression valueExpression =
            expressionFactory.createValueExpression(elContext, "#{component}",
                                                    UIXDeclarativeComponent.class);
        return valueExpression.getValue(elContext);
    }


    public static void createTableHeaderForPdf(boolean createSerialNumberHeader,
                                         List<String> headerList,
                                         PdfPTable pdfTable) {
        if (createSerialNumberHeader) {
            pdfTable.addCell(FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("serialColumnHeader").toString());
        }
        for (String headerName : headerList) {
            pdfTable.addCell(headerName);
        }
    }

    public static void createTableHeaderForCsv(boolean createSerialNumberHeader,
                                         List<String> headerList,
                                         OutputStreamWriter outputStreamWrite) throws IOException {
        if (createSerialNumberHeader) {
            outputStreamWrite.write(FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("serialColumnHeader").toString());
            outputStreamWrite.write(',');
        }
        for (String headerName : headerList) {
            outputStreamWrite.write(headerName);
            outputStreamWrite.write(',');
        }
        outputStreamWrite.write("\n");
    }

    public static List<Integer> getRenderedColumnIds(List<UIComponent> coloumnList) {
        List<Integer> renderedColumnId = new ArrayList<Integer>();
        for (int i = 0; i < coloumnList.size(); i++) {
            if (coloumnList.get(i).isRendered()) {
                renderedColumnId.add(i);
            }
        }
        return renderedColumnId;
    }

    public static List<String> getColumnsHeader(List<UIComponent> coloumnList) {
        List<String> richColumn = new ArrayList<String>();
        for (int i = 0; i < coloumnList.size(); i++) {
            if (coloumnList.get(i).isRendered()) {
                richColumn.add((String)coloumnList.get(i).getAttributes().get("headerText"));
            }
        }
        return richColumn;
    }

    public static float[] createDynamicWidth(Integer numberOfWidth) {
        float widthParcentage = (float)100.0;
        float columnwidth = widthParcentage / numberOfWidth;
        float column[] = new float[numberOfWidth];
        for (int i = 0; i < numberOfWidth; i++) {
            column[i] = columnwidth;
        }
        return column;
    }

    public static void createSerialNumber() {

    }

    public static void showErrorMessage(String message) {
        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage facesMessage =
            new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message);
        context.addMessage(null, facesMessage);
        context.renderResponse();
        return;
    }

    public static void callJavaScript(String id) {
        FacesContext context = FacesContext.getCurrentInstance();
        ExtendedRenderKitService erks =
            Service.getService(context.getRenderKit(),
                               ExtendedRenderKitService.class);
        FacesContext.getCurrentInstance();
        erks.addScript(context,
                       "customHandler('" + id + DeclarativeComponentConstraint.HIDDEN_ID +
                       "');");

    }

}
