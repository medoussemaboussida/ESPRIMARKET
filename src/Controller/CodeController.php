<?php

namespace App\Controller;

use App\Entity\Codepromo;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\HttpFoundation\Request;
use App\Form\CodeType;
use App\Repository\CodeRepository;
use App\Entity\Utilisateur;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Component\Mime\Email;
use Symfony\Component\Mailer\MailerInterface;
use Knp\Component\Pager\PaginatorInterface;
use App\Entity\Notification;
use Symfony\Component\HttpFoundation\Session\SessionInterface;


class CodeController extends AbstractController
{


#[Route('/ajouter-code', name: 'ajouter_code')]
    public function ajouterCode(Request $request): Response
{
    $code = new Codepromo();
    $form = $this->createForm(CodeType::class, $code);
    $form->handleRequest($request);

    if ($form->isSubmitted() && $form->isValid()) {

        // Enregistrez la catégorie dans la base de données
        $entityManager = $this->getDoctrine()->getManager();
        $entityManager->persist($code);
        $entityManager->flush();

         // Create a new notification when a new offer is created
         $notification = new Notification();
         $notification->setMessage("A new code promo has been created: " . $code->getCode());
         $notification->setCreatedAt(new \DateTime());
         $notification->setIsRead(false); // Default to unread

         $entityManager->persist($notification);
         $entityManager->flush();

        $this->addFlash('success', 'Code promo ajoutée avec succès.');

        // Redirigez l'utilisateur après l'ajout réussi
        return $this->redirectToRoute('afficher_codes');
    }
    //affichage
    $codes = $this->getDoctrine()->getRepository(Codepromo::class)->findAll();
 
    return $this->render('code/ajouter.html.twig', [
        'form' => $form->createView(),
        'codes' => $codes,

    ]);
}

    #[Route('/afficher-codes', name: 'afficher_codes')]
    public function afficherCodes(Request $request, CodeRepository $codeRepository,SessionInterface $session): Response
    {
        // Récupérer l'ID de l'utilisateur à partir de la session
        $userId = $session->get('iduser');
    
        // Si aucun ID utilisateur n'est stocké en session, rediriger vers la page de connexion
        if (!$userId) {
            // Redirection vers la page de connexion
            return $this->redirectToRoute('app_login'); // Remplacez 'login' par le nom de votre route de connexion
        }
    
        // Récupérer l'utilisateur à partir de l'ID
        $utilisateur = $this->getDoctrine()->getRepository(Utilisateur::class)->find($userId);
    
        // Vérifier si l'utilisateur existe
        if (!$utilisateur) {
            throw $this->createNotFoundException('Utilisateur non trouvé.');
        } 
        $user = $this->getDoctrine()->getRepository(Utilisateur::class)->find($userId);

        $notifications = $this->getDoctrine()->getRepository(Notification::class)->findAll();

        $searchQuery = $request->query->get('search_query', '');  // Rechercher par nom de code
        $sortBy = $request->query->get('sort_by', 'datedebut');    // Champ de tri par défaut
        $sortOrder = $request->query->get('sort_order', 'asc');    // Ordre de tri par défaut
        $reductionAssocie = $request->query->get('reduction_associe', null);

    
        $criteria = [
            'code' => $searchQuery,
            'reductionassocie' => $reductionAssocie

        ];
    
        // Utilisation du repository pour obtenir les codes promo filtrés et triés
        $codes = $codeRepository->findByCriteriaAndSort($criteria, $sortBy, $sortOrder);
    
        return $this->render('code/afficher.html.twig', [
            'codes' => $codes,
            'sort_by' => $sortBy,
            'sort_order' => $sortOrder,
            'notifications' => $notifications,
            'user' => $user,

        ]);
    }
    

    #[Route('/modifier-code/{id}', name: 'modifier_code')]
    public function modifierCode(int $id, Request $request): Response
    {
        $entityManager = $this->getDoctrine()->getManager();
        $code = $entityManager->getRepository(Codepromo::class)->find($id);
    
        // Vérifier si le code existe
        if (!$code) {
            throw $this->createNotFoundException('Aucun code trouvé pour cet identifiant.');
        }
    
        $form = $this->createForm(CodeType::class, $code); // Use CodeType form class here
        $form->handleRequest($request);
    
        if ($form->isSubmitted() && $form->isValid()) {
            // Mettre à jour le code dans la base de données
            $entityManager->flush();
    
            // Ajouter un message flash pour indiquer la modification réussie
            $this->addFlash('success', 'Code modifié avec succès.');
    
            // Rediriger vers la page d'affichage des codes
            return $this->redirectToRoute('afficher_codes');
        }
    
        return $this->render('code/modifier.html.twig', [
            'form' => $form->createView(),
        ]);
    }

    #[Route('/consulter-code/{id}', name: 'consulter_code')]
    public function consulterCode(int $id): Response
    {
        $entityManager = $this->getDoctrine()->getManager();
        $code = $entityManager->getRepository(Codepromo::class)->find($id);

        // Vérifier si le code existe
        if (!$code) {
            throw $this->createNotFoundException('Aucun code trouvée pour cet identifiant.');
        }
        return $this->render('code/consulter.html.twig', [
            'code' => $code,
        ]);
    }

    #[Route('/supprimer-code/{id}', name: 'supprimer_code')]
    public function supprimerCode(int $id): Response
    {
        // Récupérer l'offre à supprimer depuis la base de données
        $entityManager = $this->getDoctrine()->getManager();
        $code = $entityManager->getRepository(Codepromo::class)->find($id);

        // Vérifier si l'offre existe
        if (!$code) {
            throw $this->createNotFoundException('Aucun code promo trouvée pour cet identifiant.');
        }

        // Supprimer l'offre de la base de données
        $entityManager->remove($code);
        $entityManager->flush();

        // Ajouter un message flash pour indiquer la suppression réussie
        $this->addFlash('success', 'Code promo supprimée avec succès.');

        // Rediriger vers la page d'affichage des codes
        return $this->redirectToRoute('afficher_codes');
    }
    




}