<?php

namespace App\Service;

use Dompdf\Dompdf;
use Dompdf\Options;
use App\Entity\Offre;

class PDFExporterService
{
    public function exportToPDF(array $offres, string $filePath): void
    {
        $options = new Options();
        $options->set('isRemoteEnabled', true); // Permet d'utiliser des ressources externes
        $dompdf = new Dompdf($options);

        // Logo (assurez-vous que le chemin est accessible)
        $logoUrl = "https://i.pinimg.com/originals/75/17/bd/7517bd131e58c55e014e8c501d6251e3.png";

        // Début du contenu HTML
        $html = '<html><head><style>';
        $html .= 'body { font-family: Arial, sans-serif; }';
        $html .= 'table { width: 100%; border-collapse: collapse; }';
        $html .= 'th, td { border: 1px solid #ddd; padding: 8px; }';
        $html .= 'th { background-color: #f2f2f2; }';
        $html .= 'h1 { color: #800080; text-align: center; }'; // Couleur mauve pour le titre et centré
        $html .= '</style></head><body>';

        // Ajouter le logo
        $html .= "<img src='{$logoUrl}' alt='Logo' style='height: 50px; display: block; margin: 0 auto;'>"; // Centré également

        // Titre
        $html .= '<h1>Liste des offres</h1>';

        // Tableau pour les offres
        $html .= '<table>';
        $html .= '<tr><th>Nom de l\'offre</th><th>Description</th><th>Date de début</th><th>Date de fin</th><th>Réduction</th></tr>';
        foreach ($offres as $offre) {
            $html .= "<tr>";
            $html .= "<td>{$offre->getNomOffre()}</td>";
            $html .= "<td>{$offre->getDescriptionOffre()}</td>";
            $html .= "<td>{$offre->getDateDebut()->format('Y-m-d')}</td>";
            $html .= "<td>{$offre->getDateFin()->format('Y-m-d')}</td>";
            $html .= "<td>{$offre->getReduction()}%</td>";
            $html .= "</tr>";
        }
        $html .= '</table>';

        // Fin du contenu HTML
        $html .= '</body></html>';

        $dompdf->loadHtml($html);
        $dompdf->setPaper('A4', 'portrait'); // Configurer le papier
        $dompdf->render(); // Générer le PDF
        $output = $dompdf->output(); // Obtenir le contenu du PDF

        file_put_contents($filePath, $output); // Enregistrer le fichier PDF
    }
}
