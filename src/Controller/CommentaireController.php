<?php

namespace App\Controller;

use App\Entity\Commentaire;
use App\Entity\Publication;
use App\Entity\Utilisateur; // Ajoutez ceci
use Symfony\Component\HttpFoundation\Request; // Importer la classe Request correcte
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Session\SessionInterface;

class CommentaireController extends AbstractController
{
    /**
 * @Route("/ajouter-commentaire", name="ajouter_commentaire", methods={"POST"})
 */
public function ajouterCommentaire(Request $request, SessionInterface $session): Response
{
    $contenu = $request->request->get('comment');
    $publicationId = $request->request->get('publication_id');
    
    // Récupérer l'ID de l'utilisateur à partir de la session
    $userId = $session->get('iduser');

    // Vérifier si l'utilisateur est connecté
    if (!$userId) {
        // Rediriger vers la page de connexion si l'utilisateur n'est pas connecté
        return $this->redirectToRoute('app_utilisateur');
    }

    // Récupérer la publication en fonction de son ID
    $publication = $this->getDoctrine()->getRepository(Publication::class)->find($publicationId);

    if (!$publication) {
        throw $this->createNotFoundException('La publication avec l\'ID '.$publicationId.' n\'existe pas.');
    }

    // Récupérer l'utilisateur en fonction de son ID
    $utilisateur = $this->getDoctrine()->getRepository(Utilisateur::class)->find($userId);

    if (!$utilisateur) {
        throw $this->createNotFoundException('Aucun utilisateur trouvé avec l\'ID '.$userId.'.');
    }

    // Créer un nouveau commentaire
    $commentaire = new Commentaire();

    // Vérifier si le contenu du commentaire est valide
    if ($contenu !== null) {
        $commentaire->setDescriptioncommentaire($contenu);
    } else {
        $commentaire->setDescriptioncommentaire(''); // Ou une autre valeur par défaut
    }

    // Définir la publication pour ce commentaire
    $commentaire->setIdpublication($publication);

    // Définir l'utilisateur pour ce commentaire
    $commentaire->setIduser($utilisateur);

    $entityManager = $this->getDoctrine()->getManager();
    $entityManager->persist($commentaire);
    $entityManager->flush();

    return $this->redirectToRoute('front_publication_detail', [
        'id' => $publicationId,
        'iduser' => $userId,
    ]);
}

    /**
     * @Route("/publication/{id}/commentaire", name="detail_commentaire")
     */
    public function detailCommentaire(Publication $publication,$id): Response
    {
        // Récupérer les commentaires associés à cette publication
        $commentaires = $this->getDoctrine()->getRepository(Commentaire::class)->findBy(['idpublication' => $id]);

        return $this->render('publication/detailCommentaire.html.twig', [
            'publication' => $publication,
            'commentaires' => $commentaires,
        ]);
    }


    /**
 * @Route("/commentaire/{id}/delete", name="delete_commentaire", methods={"POST"})
 */
public function deleteCommentaire(Commentaire $commentaire,$id,Request $request): Response
{
    $entityManager = $this->getDoctrine()->getManager();
    $commentaire = $entityManager->getRepository(Commentaire::class)->find($id);
    $entityManager->remove($commentaire);
    $entityManager->flush();


    $this->addFlash('success', 'Le commentaire a été supprimé avec succès.');

    // Rediriger vers la page où vous affichez les commentaires
    return $this->redirectToRoute('detail_commentaire', ['id' => $commentaire->getIdpublication()->getIdPublication()]);
}
}
