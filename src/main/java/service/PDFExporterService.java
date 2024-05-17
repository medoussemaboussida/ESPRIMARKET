package service;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import entities.DemandeDons;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class PDFExporterService {

    public boolean exportDemandToPDF(DemandeDons demande, File file) {
        try {
            PdfWriter writer = new PdfWriter(file);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Define fonts
            PdfFont titleFont = PdfFontFactory.createFont();
            PdfFont contentFont = PdfFontFactory.createFont();

            // Define colors
            Color titleColor = new DeviceRgb(0, 0, 0); // Black
            Color contentColor = new DeviceRgb(0, 0, 0); // Black

            // Create title paragraph
            Paragraph title = new Paragraph("Demande de don").setFont(titleFont).setFontColor(titleColor).setFontSize(18).setBold();
            title.setTextAlignment(TextAlignment.CENTER);
            document.add(title);

            // Format date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateStr = dateFormat.format(demande.getDatePublication());

            // Create content paragraphs
            Paragraph user = new Paragraph("Utilisateur: " + demande.getNomUser() + " " + demande.getPrenomUser()).setFont(contentFont).setFontColor(contentColor).setFontSize(12);
            Paragraph content = new Paragraph("Contenu: " + demande.getContenu()).setFont(contentFont).setFontColor(contentColor).setFontSize(12);
            Paragraph publicationDate = new Paragraph("Date de publication: " + dateStr).setFont(contentFont).setFontColor(contentColor).setFontSize(12);
            Paragraph points = new Paragraph("Points gagnés: " + demande.getNbPoints()).setFont(contentFont).setFontColor(contentColor).setFontSize(12);

            // Add paragraphs to the document
            document.add(user);
            document.add(content);
            document.add(publicationDate);
            document.add(points);

            // Close the document
            document.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
