package com.dignaj.otcs;

import java.awt.*;
import java.io.IOException;
import java.util.*;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class InvoiceGenerator {

    public static void main(String[] args) {
        System.out.println("Hello World!");
        try {
            InvoiceGenerator igen = new InvoiceGenerator();
            igen.Generate(null);
        } catch (InvoiceException e) {
            e.printStackTrace();
        }
    }

    final PDDocument document;
    final PDPage page;
    final PDPageContentStream contents;
    final Color ACCENT_COLOR = new Color(136, 144, 192);

    public InvoiceGenerator() throws InvoiceException {
        document = new PDDocument();
        page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        try {
            contents = new PDPageContentStream(document, page);
        } catch (IOException e) {
            throw new InvoiceException("Failed to initialize PDF Content Stream", e);
        }
    }

    public void Generate(HashMap<String, String> data) throws InvoiceException {
        try {

            drawHeader();
            LinkedHashMap<String, String> invoiceInfoData = new LinkedHashMap<>();
            invoiceInfoData.put("RECEIVED DATE", "");
            invoiceInfoData.put("INVOICE NO", "12345");
            invoiceInfoData.put("INVOICE DATE", "11/12/2020");
            drawSingleRowTable(invoiceInfoData, 230, 680, 110, 20, 5, 10);

            LinkedHashMap<String, String> siteData = new LinkedHashMap<>();
            siteData.put("SITE GLN", "9377778794884");
            siteData.put("SITE NAME", "BP Ashfield COM - Sample");
            siteData.put("PO NUMBER", "100500");
            siteData.put("PARTNER VAT NUMBER", "");
            drawSingleRowTable(siteData, 40, 620, 130, 20, 5, 10);

            LinkedHashMap<String, String> deliveryData = new LinkedHashMap<>();
            deliveryData.put("DELIVERY DATE", "2018-10-23");
            drawSingleRowTable(deliveryData, 40, 80, 130, 20, 5, 10);

            HashMap<String,String> descData = new HashMap<>();
            descData.put("Net","110.72");
            descData.put("VAT","1.71");
            descData.put("TOTAL","112.43");
            drawDescriptionTable(descData);

        } catch (IOException e) {
            throw new InvoiceException(e);
        }

        saveInvoice("Invoice.pdf");
    }

    private void saveInvoice(String fileName) throws InvoiceException {
        try {
            //Close the Content Stream before closing
            contents.close();
            document.save(fileName);
        } catch (IOException e) {
            throw new InvoiceException("Unable to save Invoice", e);
        }
    }

    private void drawDescriptionTable(Map<String,String> data) throws IOException {
        //Description column
        contents.setNonStrokingColor(ACCENT_COLOR);
        contents.addRect(40, 550, 360, 27);
        contents.fill();
        drawTableCellText("DESCRIPTION", 45, 560, Color.BLACK, 10);
        contents.addRect(40, 550, 360, 27);
        contents.stroke();
        contents.addRect(40, 180, 360, 370);
        contents.stroke();

        drawTableCellText("Net", 50, 530, Color.BLACK, PDType1Font.HELVETICA, 10);
        drawTableCellText("VAT", 50, 515, Color.BLACK, PDType1Font.HELVETICA, 10);

        //Amount column
        contents.setNonStrokingColor(ACCENT_COLOR);
        contents.addRect(400, 550, 160, 27);
        contents.fill();
        drawTableCellText("AMOUNT", 510, 560, Color.BLACK, 10);
        contents.addRect(400, 550, 160, 27);
        contents.stroke();
        contents.addRect(400, 180, 160, 370);
        contents.stroke();

        drawTableCellText(data.get("Net"), 480, 530, Color.BLACK, PDType1Font.HELVETICA, 10);
        drawTableCellText(data.get("VAT"), 480, 515, Color.BLACK, PDType1Font.HELVETICA, 10);

        //Invoice Currency AUD column cell
        contents.addRect(40, 130, 300, 50);
        contents.stroke();
        drawTableCellText("Invoice Currency AUD", 120, 150, Color.BLACK, PDType1Font.HELVETICA, 12);
        //TOTAL column cell
        contents.addRect(340, 130, 220, 50);
        contents.stroke();
        drawTableCellText("TOTAL", 350, 150, Color.BLACK, PDType1Font.HELVETICA_BOLD, 14);
        drawTableCellText(data.get("TOTAL"), 480, 150, Color.BLACK, PDType1Font.HELVETICA_BOLD, 14);
    }

    private void drawSingleRowTable(Map<String, String> data, float left, float top, float col_width, float row_height, float padding, float font_size) throws IOException {


        Iterator<Map.Entry<String, String>> columns = data.entrySet().iterator();

        int i = 0;
        while (columns.hasNext()) {
            Map.Entry<String, String> entry = columns.next();

            //Header background
            contents.setNonStrokingColor(ACCENT_COLOR);
            contents.addRect(left + (col_width * i), top, col_width, row_height);
            contents.fill();

            drawTableCellText(entry.getKey(), left + (col_width * i) + padding, top + padding, Color.BLACK, font_size);
            //Header border
            contents.addRect(left + (col_width * i), top, col_width, row_height);
            contents.stroke();
            //First row
            contents.addRect(left + (col_width * i), top - row_height, col_width, row_height);
            contents.stroke();

            drawTableCellText(entry.getValue(), left + (col_width * i) + padding, top - row_height + padding, Color.BLACK, font_size);

            i++;
        }

    }

    private void drawTableCellText(String text, float tx, float ty, Color color, float font_size) throws IOException {
        drawTableCellText(text,tx,ty,color,PDType1Font.HELVETICA,font_size);
    }

    private void drawTableCellText(String text, float tx, float ty, Color color, PDFont font,float font_size) throws IOException {
        contents.beginText();
        contents.setFont(font, font_size);
        contents.setNonStrokingColor(color);
        contents.newLineAtOffset(tx, ty);
        contents.showText(text);
        contents.endText();
    }

    private void drawHeader() throws IOException {

        contents.beginText();
        contents.setFont(PDType1Font.HELVETICA_BOLD, 30);
        contents.setNonStrokingColor(ACCENT_COLOR);
        contents.newLineAtOffset(370, 770);
        contents.showText("TAX INVOICE");
        contents.endText();

        contents.beginText();
        contents.setNonStrokingColor(Color.BLACK);
        contents.setFont(PDType1Font.HELVETICA, 18);
        contents.newLineAtOffset(40, 770);
        contents.showText("LD&D Australia Pty Limited");
        contents.endText();

        contents.beginText();
        contents.setFont(PDType1Font.HELVETICA, 16);
        contents.newLineAtOffset(40, 750);
        contents.showText("9310113000004");
        contents.endText();

    }

    public void finalize() throws InvoiceException {
        try {
            document.close();
        } catch (IOException e) {
            throw new InvoiceException(e);
        }
    }
}