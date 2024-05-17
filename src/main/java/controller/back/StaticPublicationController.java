package controller.back;

import entities.Publication;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import service.CommentaireService;
import service.PublicationService;

import java.util.List;

public class StaticPublicationController {

    @FXML
    private BarChart<String, Integer> barChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    private CommentaireService commentaireService = new CommentaireService();
    private PublicationService publicationService = new PublicationService();

    @FXML
    private void initialize() {
        // Chargement des données et configuration du graphique
        loadDataAndSetupChart();
    }

    private void loadDataAndSetupChart() {
        // Récupérer la liste des publications
        List<Publication> allPublications = publicationService.readAll();

        // Nettoyer le graphique
        barChart.getData().clear();
        xAxis.getCategories().clear();

        // Créer une série pour les données du graphique
        XYChart.Series<String, Integer> series = new XYChart.Series<>();
        series.setName("Nombre de Commentaires"); // Nom de la série

        for (Publication publication : allPublications) {
            int nombreCommentaires = commentaireService.countCommentairesParPublication(publication.getIdPublication());
            String publicationTitle = publication.getTitrePublication(); // Utilisez le titre de la publication
            XYChart.Data<String, Integer> data = new XYChart.Data<>(publicationTitle, nombreCommentaires);
            series.getData().add(data);
        }

        // Ajouter la série au graphique
        barChart.getData().add(series);

        // Ajuster l'axe Y pour afficher uniquement des valeurs entières
        yAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                if (object.intValue() == object.doubleValue()) {
                    // Afficher l'étiquette seulement si c'est une valeur entière
                    return String.valueOf(object.intValue());
                } else {
                    // Sinon, ne pas afficher l'étiquette
                    return "";
                }
            }

            @Override
            public Number fromString(String string) {
                return null;
            }
        });
    }

    @FXML
    private void close(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
