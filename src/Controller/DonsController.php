<?php

namespace App\Controller;

use App\Entity\Dons;
use App\Entity\Utilisateur;
use App\Form\DonsType;
use App\Form\ChoiceType;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

use App\Repository\DonsRepository;
use App\Entity\Evenement;
use Symfony\Component\Form\Extension\Core\Type\IntegerType;
use Symfony\Component\Form\Extension\Core\Type\SubmitType;
use App\Repository\EvenementRepository;
use Symfony\Component\HttpFoundation\RequestStack;
use Symfony\Bridge\Doctrine\Form\Type\EntityType;
use App\Form\ModifierPointsFormType;
use Flashy\Flashy;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use App\Repository\UtilisateurRepository;
use Symfony\Component\Security\Core\User\UserInterface;
use Symfony\Component\Security\Core\Encoder\UserPasswordEncoderInterface;
use Symfony\Component\HttpFoundation\Session\SessionInterface;












class DonsController extends AbstractController
{
    
    

    private $requestStack;

    public function __construct(RequestStack $requestStack)
    {
        $this->requestStack = $requestStack;
    }
    #[Route('/dons', name: 'dons_page')]
    public function index(Request $request, DonsRepository $donsRepository, EvenementRepository $evenementRepository, SessionInterface $session): Response
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
    
            // Récupérer le nombre de points de l'utilisateur
            $nbPointsUtilisateur = $utilisateur->getNbpoints();
        } else {
            // Si aucun ID utilisateur n'est fourni dans l'URL, utiliser l'utilisateur connecté
            $utilisateur = $this->getUser();
    
            // Vérifier si l'utilisateur est connecté
            if (!$utilisateur instanceof Utilisateur) {
                throw $this->createNotFoundException('Utilisateur non trouvé.');
            }
    
            // Récupérer le nombre de points de l'utilisateur
            $nbPointsUtilisateur = $utilisateur->getNbpoints();
        }
    
        // Récupérer les dons de l'utilisateur
        $dons = $donsRepository->getDonsByUserId($userId);
        $evenementsDons = $evenementRepository->findByTypeEv('dons');
    
        $maximumPoints = 200; // Par exemple, vous pouvez définir le maximum à 200
    
        // Créer un nouvel objet Dons
        $don = new Dons();
    
        // Créer le formulaire en utilisant le formulaire DonsType
        $form = $this->createForm(DonsType::class, $don)
        ->add('idEv', EntityType::class, [
            'class' => Evenement::class,
            'choices' => $evenementsDons, // Utilisez les événements de type "dons" récupérés précédemment
            'choice_label' => 'nomEv', // Assurez-vous que 'nomEv' correspond au champ que vous souhaitez afficher
            'label' => 'Choisir un événement',
            'attr' => ['class' => 'form-control'],
            ]);
    
        // Gérer la soumission du formulaire
        $form->handleRequest($request);
    
        // Vérifier si le formulaire a été soumis et est valide
        if ($form->isSubmitted() && $form->isValid()) {
            // Récupérer la valeur de nbpoints depuis le formulaire
            $donPoints = $form->get('nbpoints')->getData();
    
            // Vérifier si l'utilisateur a suffisamment de points
            if ($nbPointsUtilisateur < $donPoints) {
                // Ajouter un message flash d'erreur
                $this->addFlash('error', 'Vous n\'avez pas suffisamment de points pour effectuer ce don.');
            } else {
                // Récupérer l'événement sélectionné
                $evenementId = $form->get('idEv')->getData();
                $evenement = $evenementRepository->find($evenementId);
    
                // Soustraire les points du don aux points de l'événement
                $evenement->setNbPoints($evenement->getNbPoints() + $donPoints);

                // Mettre à jour les points de l'utilisateur en soustrayant les points du don
                $nbPointsUtilisateur -= $donPoints;
                $session->set('nbPointsUtilisateur', $nbPointsUtilisateur); // Mettre à jour les points de l'utilisateur en session

                // Définir l'utilisateur pour le don
                $don->setIdUser($utilisateur);

                // Définir l'état du don comme "en attente"
                $don->setEtatstatutdons('en attente');

                // Enregistrer les modifications dans la base de données
                $entityManager = $this->getDoctrine()->getManager();
                $entityManager->persist($don);
                $entityManager->flush();
                $this->addFlash('success', 'Don ajouté avec succès.');
            }
        }
    
        // Rendre la vue Twig avec le formulaire, les dons et les événements de type "dons"
        return $this->render('dons/Dons.html.twig', [
            'form' => $form->createView(),
            'dons' => $dons,
            'nbPointsUtilisateur' => $nbPointsUtilisateur,
            'evenementsDons' => $evenementsDons,
            'maximumPoints' => $maximumPoints, // Passer le maximum de points à la vue Twig
            'user' => $utilisateur, // Utilisateur connecté
        ]);
    }
    




    public function addPointsToEventForm(Request $request, Evenement $evenement, SessionInterface $session): Response
    {
        // Récupérer le nombre de points de l'utilisateur à partir de la session
        $nbPointsUtilisateur = $session->get('nbPointsUtilisateur');
    
        // Créer le formulaire d'ajout de points
        $form = $this->createFormBuilder()
            ->add('nbPoints', IntegerType::class, [
                'label' => 'Nombre de points à ajouter',
                'data' => $nbPointsUtilisateur, // Utiliser le nombre de points de l'utilisateur comme valeur par défaut
            ])
            ->add('submit', SubmitType::class, ['label' => 'Ajouter des points'])
            ->getForm();
    
        // Gérer la soumission du formulaire
        $form->handleRequest($request);
    
        // Vérifier si le formulaire a été soumis et est valide
        if ($form->isSubmitted() && $form->isValid()) {
            // Récupérer les données du formulaire
            $data = $form->getData();
    
            // Ajouter les points à l'événement
            $evenement->setNbPoints($evenement->getNbPoints() + $data['nbPoints']);
    
            // Mettre à jour le nombre de points de l'utilisateur en soustrayant les points ajoutés à l'événement
            $nbPointsUtilisateur -= $data['nbPoints'];
            $session->set('nbPointsUtilisateur', $nbPointsUtilisateur);
    
            // Enregistrer les modifications dans la base de données
            $entityManager = $this->getDoctrine()->getManager();
            $entityManager->flush();
            $this->addFlash('success', 'Points ajoutés avec succès.');
    
            // Rediriger l'utilisateur vers une autre page ou afficher un message de confirmation
            // Ici, je redirige l'utilisateur vers la page de détails de l'événement
            return $this->redirect($this->generateUrl('evenement_details', ['id' => $evenement->getId()]));
        }
    
        // Afficher le formulaire dans le template
        return $this->render('dons/add_points_to_event_form.html.twig', [
            'form' => $form->createView(),
            'evenement' => $evenement,
        ]);
    }
    



      /**
     * @Route("/user/{userId}/dons", name="user_dons")
     */
    public function getDonsByUserId(DonsRepository $donsRepository, int $userId): Response
    {
        $dons = $donsRepository->getDonsByUserId($userId);

        return $this->render('dons/Dons.html.twig', [
            'dons' => $dons,
        ]);
    }

/**
 * @Route("/dons/{id}/delete", name="don_delete", methods={"POST"})
 */
public function delete(Request $request, DonsRepository $donsRepository, int $id): Response
{
    // Récupérer le don à supprimer depuis la base de données
    $entityManager = $this->getDoctrine()->getManager();
    $don = $donsRepository->find($id);

    // Vérifier si le don existe
    if (!$don) {
        throw $this->createNotFoundException('Le don avec l\'ID '.$id.' n\'existe pas.');
    }

    // Supprimer le don
    $entityManager->remove($don);
    $entityManager->flush();

    // Rediriger vers la page des dons ou une autre page après la suppression
    return $this->redirectToRoute('dons_page', ['userId' => $don->getIdUser()->getIdUser()]);
}

    /**
 * @Route("/edit/{id}", name="edit_don")
 */
public function editDon(Request $request, int $id): Response
{
 
    // Récupérer le don correspondant à l'identifiant
    $don = $this->getDoctrine()->getRepository(Dons::class)->find($id);

    // Vérifier si le don existe
    if (!$don) {
        throw $this->createNotFoundException('Le don avec l\'identifiant '.$id.' n\'existe pas.');
    }
    $nomEv = $don->getIdEv()->getNomEv();


    // Récupérer l'utilisateur associé au don
    $utilisateur = $don->getIdUser();

    // Récupérer le nombre de points avant la modification du don
    $ancienPoints = $don->getNbpoints();

    // Créer le formulaire avec le type de formulaire DonsType
    $form = $this->createForm(DonsType::class, $don);

    // Traiter la soumission du formulaire
    $form->handleRequest($request);

    // Vérifier si le formulaire est soumis et valide
    if ($form->isSubmitted() && $form->isValid()) {
        // Récupérer le nouveau nombre de points saisi dans le formulaire
        $nouveauPoints = $don->getNbpoints();

        // Calculer les points mis à jour
        $pointsMisAJour = $utilisateur->getNbPoints() + $ancienPoints - $nouveauPoints;

        // Mettre à jour le nombre de points de l'utilisateur
        $utilisateur->setNbPoints($pointsMisAJour);

        // Enregistrer les modifications dans la base de données
        $entityManager = $this->getDoctrine()->getManager();
        $entityManager->persist($don);
        $entityManager->flush();
        $this->addFlash('success', 'Don modifié avec succès.');


        // Rediriger l'utilisateur vers une autre page après l'édition
        return $this->redirectToRoute('dons_page', ['userId' => $don->getIdUser()->getIdUser()]);
    }

    // Afficher le formulaire dans le template
    return $this->render('dons/edit_modal.html.twig', [
        'form' => $form->createView(),
        'don' => $don, // Passer la variable "don" au template
        'nbPointsUtilisateur' => $utilisateur->getNbPoints(),
        'nomEv' => $nomEv,



    ]);
}



  
    
    /**
 * @Route("/admin/dons/{id}/delete", name="admin_dons_delete", methods={"GET", "POST"})
 */

public function deleteDon(Request $request, DonsRepository $donsRepository, $id): Response
{
    // Récupérer le don à supprimer en fonction de son ID
    $don = $donsRepository->find($id);

    // Vérifier si le don existe
    if (!$don) {
        throw $this->createNotFoundException('Le don avec l\'ID '.$id.' n\'existe pas.');
    }

    // Supprimer le don
    $entityManager = $this->getDoctrine()->getManager();
    $entityManager->remove($don);
    $entityManager->flush();

    // Rediriger vers la page des dons après la suppression
    return $this->redirectToRoute('admin_dons');
}

      


/**
 * @Route("/update_etat_dons/{id}", name="update_etat_dons", methods={"POST"})
 */
public function updateEtatDons(Request $request, int $id): Response
{
    // Récupérer le don à mettre à jour
    $entityManager = $this->getDoctrine()->getManager();
    $don = $entityManager->getRepository(Dons::class)->find($id);

    if (!$don) {
        throw $this->createNotFoundException('Le don avec l\'ID '.$id.' n\'existe pas.');
    }

    $etatstatutdons = $request->request->get('etatstatutdons');

    // Vérifier que l'état est valide (reçu ou en attente)
    if ($etatstatutdons === 'reçu' || $etatstatutdons === 'en attente') {
        // Vérifier si l'état actuel est "reçu"
        if ($don->getEtatstatutdons() !== 'reçu') {
            $don->setEtatstatutdons($etatstatutdons);
            $entityManager->flush();

            $this->addFlash('success', 'L\'état du don a été mis à jour avec succès.');
        } else {
            $this->addFlash('error', 'Impossible de modifier un don déjà reçu.');
        }
    } else {
        $this->addFlash('error', 'L\'état du don n\'a pas pu être mis à jour. État invalide.');
    }

    return $this->redirectToRoute('admin_dons');
}


/**
 * @Route("/modifier-points/{id}", name="modifier_points_don", methods={"GET", "POST"})
 */
public function modifierPointsDon(Request $request, int $id): Response
{
    // Récupérer le don correspondant à l'identifiant
    $don = $this->getDoctrine()->getRepository(Dons::class)->find($id);

    // Vérifier si le don existe
    if (!$don) {
        throw $this->createNotFoundException('Le don avec l\'identifiant '.$id.' n\'existe pas.');
    }

    // Récupérer l'utilisateur associé au don
    $utilisateur = $don->getIdUser();

    // Récupérer le nombre de points avant la modification du don
    $ancienPoints = $don->getNbpoints();

    // Créer le formulaire avec le type de formulaire ModifierPointsFormType
    $form = $this->createForm(ModifierPointsFormType::class, $don);

    // Traiter la soumission du formulaire
    $form->handleRequest($request);

    // Vérifier si le formulaire est soumis et valide
    if ($form->isSubmitted() && $form->isValid()) {
        // Récupérer le nouveau nombre de points saisi dans le formulaire
        $nouveauPoints = $don->getNbpoints();

        // Calculer les points mis à jour
        $pointsMisAJour = $utilisateur->getNbPoints() + $ancienPoints - $nouveauPoints;

        // Mettre à jour le nombre de points de l'utilisateur
        $utilisateur->setNbPoints($pointsMisAJour);

        // Enregistrer les modifications dans la base de données
        $entityManager = $this->getDoctrine()->getManager();
        $entityManager->flush();

        // Rediriger vers la route admin_dons
        return $this->redirectToRoute('admin_dons');
    }

    // Afficher le formulaire dans le template
    return $this->render('dons/afficherFormulaireModificationPoints.html.twig', [
        'form' => $form->createView(),
        'don' => $don, // Passer la variable "don" au template
        'utilisateur' => $utilisateur, // Passer la variable "utilisateur" au template
    ]);
}

/**
 * @Route("/admin/dons", name="admin_dons")
 */
public function backDons(DonsRepository $donsRepository, Request $request, SessionInterface $session): Response
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
        // Récupérer l'utilisateur ayant effectué le plus de dons
    
        // Récupérer l'e-mail de la requête
        $nomUtilisateur = $utilisateur->getNomuser();
        $prenomUtilisateur = $utilisateur->getPrenomuser();
        $email = $request->query->get('email');


        // Si un e-mail est fourni, récupérer les dons par cet e-mail
        if ($email) {
            $dons = $donsRepository->findDonsByEmail($email);
        } else {
            // Sinon, récupérer tous les dons
            $dons = $donsRepository->findAll();
        }

      // Récupérer l'état sélectionné dans le formulaire de filtrage
      $etat = $request->query->get('etat');

      // Récupérer les dons en fonction de l'état sélectionné
      if ($etat) {
          $dons = $donsRepository->findBy(['etatstatutdons' => $etat]);
      } else {
          // Si aucun état n'est sélectionné, récupérer tous les dons
          $dons = $donsRepository->findAll();
      }
        // Calculer le nombre de demandes reçues et en attente
        $demandesRecues = $donsRepository->countByEtatstatutdons('reçu');
        $demandesEnAttente = $donsRepository->countByEtatstatutdons('en attente');

        // Rendre la vue Twig avec la liste des dons et les informations sur les demandes
        return $this->render('dons/backDons.html.twig', [
            'dons' => $dons,
            'demandesRecues' => $demandesRecues,
            'demandesEnAttente' => $demandesEnAttente,
            'nomUtilisateur' => $nomUtilisateur,
            'prenomUtilisateur' => $prenomUtilisateur,

        ]);
    }

}

}