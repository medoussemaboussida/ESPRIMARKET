<?php

namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

use App\Entity\Demandedons;
use App\Entity\Utilisateur; // Importez l'entité Utilisateur
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\RedirectResponse;
use Symfony\Component\HttpFoundation\JsonResponse;
use App\Repository\DemandedonsRepository; 
use App\Form\ModifierPointsFormType;
use Knp\Component\Pager\PaginatorInterface;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Form\Extension\Core\Type\IntegerType;
use Symfony\Component\Form\Extension\Core\Type\DateType;
use Symfony\Component\Validator\Constraints\NotBlank;
use Symfony\Component\Validator\Constraints\Length;
use Symfony\Component\Validator\Constraints\Positive;
use Symfony\Component\Validator\Constraints\Date;
use Symfony\Component\Validator\Constraints\GreaterThan;
use Symfony\Bridge\Doctrine\Form\Type\EntityType;
use App\Form\ChoiceType;
use Symfony\Component\Validator\Constraints\PositiveOrZero;
use Symfony\Component\HttpFoundation\Session\SessionInterface;
use Endroid\QrCode\QrCode;

use Endroid\QrCode\Builder\Builder;
use Endroid\QrCode\Encoding\Encoding;
use Endroid\QrCode\ErrorCorrectionLevel\ErrorCorrectionLevelLow;
use Endroid\QrCode\Label\Label;
use Endroid\QrCode\ErrorCorrectionLevel;
use Endroid\QrCode\Writer\PngWriter;




use App\Form\DemandedonsType; // Assurez-vous d'avoir ce fichier de formulaire



class DemandeDonsController extends AbstractController
{
    private $paginator;

    public function __construct(PaginatorInterface $paginator)
    {
        $this->paginator = $paginator;
    }
    

    
    #[Route('/demander_dons', name: 'demander_dons', methods: ['GET', 'POST'])]
    public function demanderDons(Request $request, EntityManagerInterface $entityManager, SessionInterface $session, PaginatorInterface $paginator, DemandedonsRepository $demandedonsRepository): Response
    {
        // Récupérer l'ID de l'utilisateur à partir de la session
        
        $userId = $session->get('iduser');
    
        // Si aucun ID utilisateur n'est stocké en session, rediriger vers la page de connexion
        if (!$userId) {
            // Redirection vers la page de connexion
            return $this->redirectToRoute('login'); // Remplacez 'login' par le nom de votre route de connexion
        }
    
        // Récupérer l'utilisateur à partir de l'ID
        $utilisateur = $this->getDoctrine()->getRepository(Utilisateur::class)->find($userId);
    
        // Vérifier si l'utilisateur existe
        if (!$utilisateur) {
            throw $this->createNotFoundException('Utilisateur non trouvé.');
        } 
    
        $nomUtilisateur = $utilisateur->getNomuser();
        $prenomUtilisateur = $utilisateur->getPrenomuser();
    
        $demande = new Demandedons();
        $form = $this->createForm(DemandedonsType::class, $demande);
    
        // Gérer la soumission du formulaire
        $form->handleRequest($request);
        if ($form->isSubmitted() && $form->isValid()) {
            $demande->setIdUtilisateur($utilisateur);
            $demande->setNomuser($utilisateur->getNomuser());
            $demande->setPrenomuser($utilisateur->getPrenomuser());
            $demande->setNbpoints(0);

    
            // Persistez la demande dans la base de données
            $entityManager->persist($demande);
            $entityManager->flush();
    
            // Rediriger vers la même page pour éviter la soumission répétée du formulaire
            return $this->redirectToRoute('demander_dons');
        }
    
        $filterSort = $request->query->get('filter_sort');
        $filter = null;
        $sort = null;
        
        // Analyser les paramètres de filtre et de tri
        if ($filterSort) {
            if ($filterSort === 'alphabetical') {
                $filter = 'alphabetical';
            } elseif ($filterSort === 'date') {
                $filter = 'date';
            } elseif ($filterSort === 'asc') {
                $sort = 'asc';
            } elseif ($filterSort === 'desc') {
                $sort = 'desc';
            }
        }
        
        // Récupérer les demandes de dons en fonction du filtre et du tri
        $demandes = $demandedonsRepository->findFilteredAndSorted($filter, $sort);
        
    
        // Récupérer les demandes de dons en fonction du filtre et du tri
    
        // Paginer les résultats
        $demandesPaginated = $paginator->paginate(
            $demandes,
            $request->query->getInt('page', 1),
            3
        );
    /*
        $qrCodes = [];
        foreach ($demandesPaginated as $demande) {
            $nbPoints = $demande->getNbpoints();
            if ($nbPoints !== null) {
                // Créer un nouveau code QR pour chaque demande
                $qrCode = QrCode::create($nbPoints)
                    ->setEncoding(new Encoding('UTF-8'))
                    ->setSize(100)
                    ->setMargin(10);
        
                // Générer l'URI de données pour le code QR
                $qrCodeUri = (new PngWriter())->write($qrCode)->getDataUri();
                
                // Ajouter l'URI de données au tableau
                $qrCodes[] = $qrCodeUri;
            }
        }
$pngWriter = new PngWriter();
$pngResult = $pngWriter->write($qrCode);
$qrCodeUri = $pngResult->getDataUri(); // Obtenez l'URI de l'image PNG
        */
        
    
        // Rendre la vue avec les données
        return $this->render('demande_dons/demanderdons.html.twig', [
            'utilisateur' => $utilisateur,
            'form' => $form->createView(),
            'demandes' => $demandesPaginated,
            'nomUtilisateur' => $nomUtilisateur,
            'prenomUtilisateur' => $prenomUtilisateur,
           // 'qrCodes' => $qrCodes, 
            'user' => $utilisateur, // Utilisateur connecté


        ]);
    }
    
    
    

/**
 * @Route("/transfer_points", name="transfer_points", methods={"POST"})
 */
public function transferPoints(Request $request, EntityManagerInterface $entityManager, SessionInterface $session): JsonResponse
{
    // Récupérer l'ID de l'utilisateur à partir de la session
    $userId = $session->get('iduser');

    // Utiliser $userId qui est l'ID de l'utilisateur à partir de la session
    
    $donPoints = $request->request->get('donPoints');
    $idDemande = $request->request->get('idDemande');

    // Récupérer l'utilisateur expéditeur
    $sender = $entityManager->getRepository(Utilisateur::class)->find($userId);
    if (!$sender) {
        return new JsonResponse(['error' => 'Utilisateur expéditeur non trouvé'], Response::HTTP_NOT_FOUND);
    }

    // Récupérer la demande de don associée à l'ID
    $demande = $entityManager->getRepository(Demandedons::class)->find($idDemande);
    if (!$demande) {
        return new JsonResponse(['error' => 'Demande de don non trouvée'], Response::HTTP_NOT_FOUND);
    }

    // Vérifier que les données sont valides
    if (!is_numeric($donPoints) || $donPoints <= 0) {
        return new JsonResponse(['error' => 'Nombre de points invalide'], Response::HTTP_BAD_REQUEST);
    }

    // Vérifier si l'utilisateur expéditeur a suffisamment de points
    if ($sender->getNbPoints() < $donPoints) {
        return new JsonResponse(['error' => 'Points insuffisants pour effectuer le transfert'], Response::HTTP_BAD_REQUEST);
    }

    // Mettre à jour les points de l'utilisateur expéditeur
    $sender->setNbPoints($sender->getNbPoints() - $donPoints);
    
    // Mettre à jour les points de la demande en remplaçant les points existants par les nouveaux points
    $newPoints = $demande->getNbPoints() + $donPoints ;
    $demande->setNbPoints($newPoints);
    
    // Enregistrer les changements dans la base de données
    $entityManager->flush();
    $this->addFlash('success', 'Les points ont été transférés avec succès.');

    // Retourner la réponse JSON avec les nouveaux points
    return new JsonResponse(['success' => true, 'newPoints' => $newPoints]);
}






/**
 * @Route("/admin/demandedons", name="admin_demandedons")
 */
public function backDemandesDons(DemandedonsRepository $demandedonsRepository, Request $request, SessionInterface $session): Response
{
    // Récupérer l'ID de l'utilisateur à partir de la requête
    $userId = $session->get('iduser');

    // Si l'ID de l'utilisateur est fourni dans l'URL, récupérer l'utilisateur correspondant
    if ($userId) {
        // Récupérer l'utilisateur à partir de l'ID
        $utilisateur = $this->getDoctrine()->getRepository(Utilisateur::class)->find($userId);

        // Vérifier si l'utilisateur existe
        if (!$utilisateur) {
            throw $this->createNotFoundException('Utilisateur non trouvé.');
        }
    }
     // Récupérer l'adresse e-mail saisie dans le formulaire

    // Récupérer les 10 demandes de dons les plus récentes
    $demandesRecentes = $demandedonsRepository->findMostRecent(10);

    // Récupérer les 10 demandes de dons les plus anciennes
    $demandesAnciennes = $demandedonsRepository->findOldest(10);


    // Récupérer toutes les demandes de dons

    // Récupérer toutes les demandes de dons
    $demandesDons = $demandedonsRepository->findAll();

    // Initialiser les compteurs
    $demandesAtteintesCount = 0;
    $demandesNonAtteintesCount = 0;
    $email = $request->query->get('email');
    $nomUtilisateur = $utilisateur->getNomuser();
    $prenomUtilisateur = $utilisateur->getPrenomuser();
    // Si une adresse e-mail est saisie, filtrer les demandes de dons correspondantes
    if ($email) {
        $demandesDons = $demandedonsRepository->findByEmail($email);
    } else {
        // Sinon, récupérer toutes les demandes de dons
        $demandesDons = $demandedonsRepository->findAll();
    }

    // Parcourir toutes les demandes de dons
    foreach ($demandesDons as $demande) {
        // Récupérer l'objectif de points de la demande
        $objectifPoints = $demande->getObjectifPoints();

        // Vérifier si la demande a atteint l'objectif
        if ($demande->getNbPoints() >= $objectifPoints) {
            // Incrémenter le compteur des demandes atteintes
            $demandesAtteintesCount++;
        } else {
            // Incrémenter le compteur des demandes non atteintes
            $demandesNonAtteintesCount++;
        }
    }

    

    // Rendre la vue Twig avec les données
    return $this->render('demande_dons/backDemandeDons.html.twig', [
        'demandesAtteintesCount' => $demandesAtteintesCount,
        'demandesNonAtteintesCount' => $demandesNonAtteintesCount,
        'nomUtilisateur' => $nomUtilisateur,
        'prenomUtilisateur' => $prenomUtilisateur,
        'Demandedons' => $demandesDons,
        'demandesRecentes' => $demandesRecentes,
        'demandesAnciennes' => $demandesAnciennes,

    ]);
}


/**
     * @Route("/admin/demandedons/{id}/delete", name="admin_demandedons_delete", methods={"GET", "POST"})
     */
    public function deleteDemande(Request $request, DemandedonsRepository $demandedonsRepository, $id): Response


    {
        $demande = $demandedonsRepository->find($id);

        // Récupérer la demande de don à supprimer
        if (!$demande) {
            throw $this->createNotFoundException('La demande de don n\'existe pas.');
        }

        // Supprimer la demande de don de la base de données
        $entityManager = $this->getDoctrine()->getManager();
        $entityManager->remove($demande);
        $entityManager->flush();

        // Rediriger vers la page des demandes de dons après la suppression
        return $this->redirectToRoute('admin_demandedons');
    }



    /**
 * @Route("/modifier-points-demande/{id}", name="modifier_points_demande_don", methods={"GET", "POST"})
 */
public function modifierPointsDemandeDon(Request $request, int $id): Response
{
    // Récupérer la demande de don correspondant à l'identifiant
    $demandeDon = $this->getDoctrine()->getRepository(Demandedons::class)->find($id);

    // Vérifier si la demande de don existe
    if (!$demandeDon) {
        throw $this->createNotFoundException('La demande de don avec l\'identifiant '.$id.' n\'existe pas.');
    }

    // Récupérer l'utilisateur associé à la demande de don
    $utilisateur = $demandeDon->getIdUtilisateur();

    // Récupérer le nombre de points avant la modification de la demande de don
    $ancienPoints = $demandeDon->getNbpoints();

    // Créer le formulaire avec le type de formulaire ModifierPointsFormType
    $form = $this->createForm(ModifierPointsFormType::class, $demandeDon);

    // Traiter la soumission du formulaire
    $form->handleRequest($request);

    // Vérifier si le formulaire est soumis et valide
    if ($form->isSubmitted() && $form->isValid()) {
        // Récupérer le nouveau nombre de points saisi dans le formulaire
        $nouveauPoints = $demandeDon->getNbpoints();

        // Calculer les points mis à jour
        $pointsMisAJour = $utilisateur->getNbPoints() + $ancienPoints - $nouveauPoints;

        // Mettre à jour le nombre de points de l'utilisateur
        $utilisateur->setNbPoints($pointsMisAJour);

        // Enregistrer les modifications dans la base de données
        $entityManager = $this->getDoctrine()->getManager();
        $entityManager->flush();

        // Rafraîchir la page actuelle pour afficher les modifications
        return $this->redirectToRoute('admin_demandedons');
    }

    // Afficher le formulaire dans le template
    return $this->render('demande_dons/updatenbpointsdemande.html.twig', [
        'form' => $form->createView(),
        'demandeDon' => $demandeDon, // Passer la variable "demandeDon" au template
        'utilisateur' => $utilisateur, // Passer la variable "utilisateur" au template

    ]);
}



#[Route('/demander_dons_action', name: 'demander_dons_action', methods: ['GET', 'POST'])]
public function demanderDonsAction(Request $request): Response
{
    $entityManager = $this->getDoctrine()->getManager();

    // Récupérer tous les utilisateurs
    $utilisateurs = $entityManager->getRepository(Utilisateur::class)->findAll();

    $form = $this->createFormBuilder()
    ->add('utilisateur', EntityType::class, [
        'class' => Utilisateur::class,
        'choice_label' => function ($utilisateur) {
            return $utilisateur->getNomUser() . ' ' . $utilisateur->getPrenomUser();
        },
    ])
        ->add('contenu', TextType::class, [
            'constraints' => [
                new NotBlank(['message' => 'Le contenu est obligatoire.']),
                new Length([
                    'min' => 4,
                    'max' => 50,
                    'minMessage' => 'Le contenu doit contenir au moins {{ limit }} mots.',
                    'maxMessage' => 'Le contenu ne peut pas contenir plus de {{ limit }} mots.'
                ]),
            ],
        ])
        ->add('objectifPoints', IntegerType::class, [
            'constraints' => [
                new NotBlank(['message' => "L'objectif de points est obligatoire."]),
                new Positive(['message' => "L'objectif de points doit être positif."]),
            ],
        ])
        ->add('delai', DateType::class, [
            'constraints' => [
                new NotBlank(['message' => 'Le délai est obligatoire.']),
                new GreaterThan([
                    'value' => new \DateTime(),
                    'message' => 'Le délai doit être postérieur à la date d\'aujourd\'hui.'
                ]),
            ],
        ])
        ->getForm();

    // Gérer la soumission du formulaire
    $form->handleRequest($request);
    if ($form->isSubmitted() && $form->isValid()) {
        // Récupérer les données du formulaire
        $data = $form->getData();

        // Récupérer l'utilisateur sélectionné
        $utilisateur = $data['utilisateur'];
        $nomuser = $utilisateur->getNomUser();
$prenomuser = $utilisateur->getPrenomUser();

        // Créer une nouvelle demande
        $demande = new Demandedons();
        $demande->setIdUtilisateur($utilisateur);
        $demande->setNomuser($nomuser); // Définir le nom complet de l'utilisateur sur la demande
$demande->setprenomuser($prenomuser); // Définir le nom complet de l'utilisateur sur la demande
   

        
        $demande->setContenu($data['contenu']);
        $demande->setObjectifPoints($data['objectifPoints']);
        $demande->setDelai($data['delai']);
        $demande->setNbpoints(0);


        // Récupérer le prénom de l'utilisateur

    

        // Persistez la demande dans la base de données
        $entityManager->persist($demande);
        $entityManager->flush();

        // Rediriger vers la même page pour éviter la soumission répétée du formulaire
        return $this->redirectToRoute('admin_demandedons');
    }

    return $this->render('demande_dons/ajouterdemandedons.html.twig', [
        'utilisateurs' => $utilisateurs,
        'form' => $form->createView(),
        
    ]);
}

#[Route('/demande/{id}/supprimer', name: 'supprimer_demande')]
public function SupprimerDemande (Request $request, DemandedonsRepository $demandedonsRepository, $id): Response


    {
        $demande = $demandedonsRepository->find($id);

        // Récupérer la demande de don à supprimer
        if (!$demande) {
            throw $this->createNotFoundException('La demande de don n\'existe pas.');
        }

        // Supprimer la demande de don de la base de données
        $entityManager = $this->getDoctrine()->getManager();
        $entityManager->remove($demande);
        $entityManager->flush();

        // Rediriger vers la page des demandes de dons après la suppression
        return $this->redirectToRoute('demander_dons');
    }





}