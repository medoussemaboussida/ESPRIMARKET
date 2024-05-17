<?php

namespace App\Controller;

use Symfony\Component\HttpFoundation\Session\SessionInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use App\Entity\Offre;
use App\Entity\Notification;
use App\Form\OffreType;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\File\UploadedFile;
use App\Entity\Produit;
use Symfony\Component\HttpFoundation\File\Exception\FileException;
use Symfony\Component\HttpFoundation\JsonResponse;
use App\Repository\OffreRepository;
use DateInterval;
use DatePeriod;
use DateTime;
use Symfony\UX\Modal\Modal;
use Knp\Component\Pager\PaginatorInterface;
use Symfony\Component\HttpFoundation\Session\Flash\FlashBagInterface;
use Symfony\Component\HttpFoundation\StreamedResponse;
use App\Service\PDFExporterService;
use SebastianBergmann\Environment\Console;
use Symfony\Component\Mercure\PublisherInterface;
use Symfony\Component\Mercure\Update;
use App\Entity\Utilisateur;
use Doctrine\ORM\EntityManagerInterface;

class OffreController extends AbstractController
{

    #[Route('/update-offre-date', name: 'update_offre_date', methods: ['POST'])]
    public function updateOffreDate(Request $request): JsonResponse
    {
        $entityManager = $this->getDoctrine()->getManager();
        $eventId = $request->request->get('id'); // Récupérer l'ID de l'offre
    
        if (empty($eventId)) {
            return new JsonResponse(['status' => 'error', 'message' => 'Offer ID is missing'], 400);
        }
    
        $offre = $entityManager->getRepository(Offre::class)->find($eventId);
    
        if (!$offre) {
            return new JsonResponse(['status' => 'error', 'message' => 'Offer not found'], 404);
        }
    
        $newStart = $request->request->get('start');
        $newEnd = $request->request->get('end');
    
        if (new \DateTime($newStart) >= new \DateTime($newEnd)) {
            return new JsonResponse(['status' => 'error', 'message' => 'Start date must be before end date']);
        }
    
        $offre->setDatedebut(new \DateTime($newStart));
        $offre->setDatefin(new \DateTime($newEnd));
    
        $entityManager->flush();
    
        return new JsonResponse(['status' => 'success', 'message' => 'Dates updated successfully']);
    }
    
    

    #[Route('/afficher-offres-calendrier', name: 'afficher_offres_calendrier')]
public function afficherOffresCalendrier(): Response
{
    // Récupérer toutes les offres depuis la base de données
    $offres = $this->getDoctrine()->getRepository(Offre::class)->findAll();

    // Transformer les données d'offres en un format compatible avec FullCalendar
    $events = [];
    foreach ($offres as $offre) {
        $events[] = [
            'title' => $offre->getNomoffre(),
            'start' => $offre->getDatedebut()->format('Y-m-d'),
            'end' => $offre->getDatefin()->format('Y-m-d'),
            // Vous pouvez ajouter d'autres propriétés ici selon vos besoins
        ];
    }

    // Convertir le tableau en JSON
    $eventsJson = json_encode($events);

    // Passer les données transformées à la vue Twig
    return $this->render('offre/calender.html.twig', [
        'events' => $eventsJson,
    ]);
}


#[Route('/ajouter-offre', name: 'ajouter_offre')]
public function ajouterOffre(Request $request, EntityManagerInterface $em, FlashBagInterface $flashBag): Response
{
    $offre = new Offre();
    $form = $this->createForm(OffreType::class, $offre);
    $form->handleRequest($request);

    if ($form->isSubmitted() && $form->isValid()) {
         // Récupérer les produits sélectionnés
         $produits = $form->get('produits')->getData();

         // Associer chaque produit à l'offre
         foreach ($produits as $produit) {
             if ($produit->getOffre() !== null) {
                 // Si le produit est déjà associé à une autre offre, ajoutez un flash message
                 $flashBag->add('error', 'Parmi les produits sélectionnés, il existe un produit déjà affecté à une autre offre.');
                 
                 // Redirigez l'utilisateur vers la page d'ajout d'offre pour lui permettre de corriger
                 return $this->redirectToRoute('ajouter_offre');
             }
         }
         // Associer chaque produit à l'offre
            foreach ($produits as $produit) {
                $produit->setOffre($offre);
                $em->persist($produit); // Persister chaque produit associé à l'offre
            }

        // Handle uploaded image if needed
        /** @var UploadedFile $image */
        $image = $form->get('imageoffre')->getData();
        if ($image) {
            $fileName = md5(uniqid()) . '.' . $image->guessExtension();
            $image->move($this->getParameter('images_directory'), $fileName);
            $offre->setImageoffre($fileName);
        }

        // Persist the new offer
        $em->persist($offre);
        $em->flush();

        // Create a new notification when a new offer is created
        $notification = new Notification();
        $notification->setMessage("A new offer has been created: " . $offre->getNomoffre());
        $notification->setCreatedAt(new \DateTime());
        $notification->setIsRead(false); // Default to unread

        $em->persist($notification);
        $em->flush();

        // Add a flash message for successful offer creation
        $this->addFlash('success', 'Offre ajoutée avec succès.');

        // Redirect after successful creation
        return $this->redirectToRoute('afficher_offres');
    }
    // Récupérer tous les produits depuis la base de données
    $produits = $this->getDoctrine()->getRepository(Produit::class)->findAll();
    
    return $this->render('offre/ajouter.html.twig', [
        'form' => $form->createView(),
        'produits' => $produits, // Passer les produits au modèle Twig

    ]);
}

#[Route('/afficher-offres', name: 'afficher_offres')]
public function afficherOffres(Request $request, OffreRepository $offreRepository, SessionInterface $session): Response
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
    // Récupération des critères de filtrage
    $searchQuery = $request->query->get('search_query');
    $sortBy = $request->query->get('sort_by', 'datedebut'); // Défaut tri par date de début
    $sortOrder = $request->query->get('sort_order', 'asc'); // Ordre de tri

    $nomOffre = null;
    $reduction = null;

    // Traitement des critères de recherche
    if ($searchQuery) {
        if (ctype_digit($searchQuery)) {
            $reduction = (int)$searchQuery;
        } else {
            $nomOffre = $searchQuery;
        }
    }

    // Appel au repository avec les paramètres de tri
    $criteria = [
        'nomOffre' => $nomOffre,
        'reduction' => $reduction
    ];
    $offres = $offreRepository->findByCriteriaAndSort($criteria, $sortBy, $sortOrder);

    return $this->render('offre/afficher.html.twig', [
        'offres' => $offres,
        'sort_order' => $sortOrder,
        'notifications' => $notifications,
        'user' => $user,

    ]);
}




    #[Route('/modifier-offre/{id}', name: 'modifier_offre')]
public function modifier(Request $request, int $id): Response {
    $entityManager = $this->getDoctrine()->getManager();
    $offre = $entityManager->getRepository(Offre::class)->find($id);

    if (!$offre) {
        throw $this->createNotFoundException("No offer found for id ".$id);
    }
   
    // Sauvegarder le nom de l'ancienne image
    $ancienneImage = $offre->getImageoffre();

    $form = $this->createForm(OffreType::class, $offre);
    $form->handleRequest($request);

    if ($form->isSubmitted() && $form->isValid()) {

     // Récupérer les produits sélectionnés
     $produitsSelectionnes = $form->get('produits')->getData();

     // Récupérer tous les produits actuellement associés à l'offre
     $produitsAssocies = $offre->getProduits();

     // Dissocier chaque produit qui n'est pas sélectionné de l'offre
     foreach ($produitsAssocies as $produit) {
        // Vérifiez si le produit actuel n'est pas sélectionné dans le formulaire
        if (!$produitsSelectionnes->contains($produit)) {
            // Dissociez le produit de l'offre
            $produit->setOffre(null);
            // Persistez le produit pour mettre à jour la base de données
            $entityManager->persist($produit);
        }
    }
    

     // Mettre à jour les nouvelles associations produit-offre avec l'offre modifiée
     foreach ($produitsSelectionnes as $produit) {
         $offre->addProduit($produit);
         $produit->setOffre($offre);
         $entityManager->persist($produit); // Persistez le produit pour mettre à jour la base de données
     }

        /** @var UploadedFile|null $image */
        $image = $form->get('imageoffre')->getData();

        if ($image) {
            $nomFichier = md5(uniqid()).'.'.$image->guessExtension();
            try {
                $image->move($this->getParameter('images_directory'), $nomFichier);
                $offre->setImageoffre($nomFichier);
            } catch (FileException $e) {
                // Gérer l'exception si quelque chose se passe mal pendant le téléchargement du fichier
            }
        } else {
            // Si aucune nouvelle image n'est téléchargée, conserver l'ancienne image
            $offre->setImageoffre($ancienneImage);
        }

        $entityManager->flush();
        $this->addFlash('success', "L'offre a été modifiée avec succès.");

        return $this->redirectToRoute('afficher_offres');
    }

    return $this->render('offre/modifier.html.twig', [
        'offre' => $offre,
        'form' => $form->createView(),
    ]);
}


#[Route('/supprimer-offre/{id}', name: 'supprimer_offre')]
    public function supprimerOffre(int $id): Response
    {
        $entityManager = $this->getDoctrine()->getManager();
    
        // Récupérer l'offre à supprimer depuis la base de données
        $offre = $entityManager->getRepository(Offre::class)->find($id);
    
        // Vérifier si l'offre existe
        if (!$offre) {
            throw $this->createNotFoundException('Aucune offre trouvée pour cet identifiant.');
        }
    
        // Récupérer tous les produits associés à cette offre
        $produits = $offre->getProduits();
    
        // Dissocier chaque produit de l'offre
        foreach ($produits as $produit) {
            $produit->setOffre(null); // Dissocier le produit de l'offre
        }
    
        // Supprimer l'offre de la base de données
        $entityManager->remove($offre);
        $entityManager->flush();
    
        // Ajouter un message flash pour indiquer la suppression réussie
        $this->addFlash('success', 'Offre supprimée avec succès.');
    
        // Rediriger vers la page d'affichage des offres
        return $this->redirectToRoute('afficher_offres');
    }


    #[Route('/consulter-offre/{id}', name: 'consulter_offre')]
    public function consulterOffre(int $id,SessionInterface $session): Response
    {
        $userId = $session->get('iduser');
            $user = $this->getDoctrine()->getRepository(Utilisateur::class)->find($userId);
        $entityManager = $this->getDoctrine()->getManager();
        $offre = $entityManager->getRepository(Offre::class)->find($id);

        // Vérifier si l'offre existe
        if (!$offre) {
            throw $this->createNotFoundException('Aucune offre trouvée pour cet identifiant.');
        }
        return $this->render('offre/consulter.html.twig', [
            'offre' => $offre,
            'user'=> $user,

        ]);
    }

     #[Route('/export/offres', name: 'export_offres')]
    public function exportOffres(PDFExporterService $pdfExporterService, OffreRepository $offreRepository): StreamedResponse
    {
        $offres = $offreRepository->findAll(); // Récupérer toutes les offres

        $filePath = tempnam(sys_get_temp_dir(), 'offres_') . '.pdf'; // Chemin du fichier temporaire

        // Utiliser le service pour exporter les offres en PDF
        $pdfExporterService->exportToPDF($offres, $filePath);

        $response = new StreamedResponse(function () use ($filePath) {
            readfile($filePath); // Envoyer le contenu du PDF au client
        });

        // Configurer la réponse pour le téléchargement
        $response->headers->set('Content-Type', 'application/pdf');
        $response->headers->set('Content-Disposition', 'attachment; filename="offres.pdf"');

        return $response; // Retourner la réponse
    }
    
    public function envoyerNotification(Offre $offre, PublisherInterface $publisher): void {
        // Créez le message de notification
        $data = [
            'title' => 'Nouvelle Offre!',
            'message' => "Une nouvelle offre a été ajoutée: {$offre->getNomoffre()}",
            'details' => "Description: {$offre->getDescriptionoffre()}",
        ];
    
        // Envoyer l'update à Mercure
        $update = new Update(
            '/notifications',  // Le sujet (endpoint) Mercure
            json_encode($data)  // Les données à envoyer
        );
    
        $publisher($update);
    }
}