package service;
import entities.Offre;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
public class PDFExporterOffreService {
    private static final float START_OFFSET_X = 50f;
    private static final float START_OFFSET_Y = 700f;
    private static final float LINE_HEIGHT = 20f;
    private static final float PAGE_BOTTOM_MARGIN = 50f;

    private void drawTableHeader(PDPageContentStream contentStream, float x, float y) throws IOException {
        contentStream.addRect(x, y, 550f, -LINE_HEIGHT); // Rectangle pour l'en-tête

        contentStream.setLineWidth(1);
        contentStream.moveTo(x, y);
        contentStream.lineTo(x + 550f, y); // Bordure supérieure
        contentStream.stroke();
    }

    private void addTableText(PDPageContentStream contentStream, String text, float x, float y) throws IOException {
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 10);
        contentStream.newLineAtOffset(x, y - 15);
        contentStream.showText(text);
        contentStream.endText();
    }

    public boolean exportToPDF(List<Offre> offres, String filePath) throws IOException {
        PDDocument document = new PDDocument(); // Déclaration du document
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Format de date

        try {
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page); // Déclaration du stream

            float y = START_OFFSET_Y; // Position initiale
            drawTableHeader(contentStream, START_OFFSET_X, y); // Dessiner l'en-tête

            addTableText(contentStream, "Nom de l'offre", START_OFFSET_X, y);
            addTableText(contentStream, "Description", START_OFFSET_X + 100, y);
            addTableText(contentStream, "Date de début", START_OFFSET_X + 200, y);
            addTableText(contentStream, "Date de fin", START_OFFSET_X + 300, y);
            addTableText(contentStream, "Réduction", START_OFFSET_X + 400, y);

            y -= LINE_HEIGHT; // Position pour les offres

            for (Offre offre : offres) {
                if (y <= PAGE_BOTTOM_MARGIN) {
                    // Nouvelle page requise
                    page = new PDPage(); // Nouvelle page
                    document.addPage(page); // Ajout de la nouvelle page

                    contentStream.close(); // Fermer le stream précédent
                    contentStream = new PDPageContentStream(document, page); // Nouveau stream
                    y = START_OFFSET_Y; // Réinitialiser la position Y
                    drawTableHeader(contentStream, START_OFFSET_X, y); // Redessiner l'en-tête
                }

                addTableText(contentStream, offre.getNomOffre(), START_OFFSET_X, y); // Ajouter l'offre
                addTableText(contentStream, offre.getDescriptionOffre(), START_OFFSET_X + 100, y);
                addTableText(contentStream, dateFormat.format(offre.getDateDebut()), START_OFFSET_X + 200, y);
                addTableText(contentStream, dateFormat.format(offre.getDateFin()), START_OFFSET_X + 300, y);
                addTableText(contentStream, offre.getReduction() + "%", START_OFFSET_X + 400, y);

                y -= LINE_HEIGHT; // Réduction pour la prochaine entrée
            }

            contentStream.close(); // Fermer le stream une fois terminé
        } catch (IOException e) {
            e.printStackTrace(); // Gestion des exceptions
        } finally {
            document.save(new File(filePath)); // Sauvegarder le document
            document.close(); // Fermer le document
        }

        return true; // Signal que l'exportation a réussi
    }
}
