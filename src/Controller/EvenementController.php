<?php

namespace App\Controller;
use App\Entity\Evenement;
use App\Form\EvenementType;
use App\Entity\Utilisateur;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use App\Repository\EvenementRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Sensio\Bundle\FrameworkExtraBundle\Configuration\ParamConverter;
use Symfony\Component\HttpFoundation\File\UploadedFile;
use Symfony\Component\HttpFoundation\Session\SessionInterface;
use Symfony\Component\HttpFoundation\File\File;

class EvenementController extends AbstractController
{
    #[Route('/evenement/front', name: 'app_evenement_front')]
    public function index(EvenementRepository $evenementRepository,SessionInterface $session): Response
    {
        $userId = $session->get('iduser');
        $user = $this->getDoctrine()->getRepository(Utilisateur::class)->find($userId);
        return $this->render('evenement/frontEvenement.html.twig', [
            'evenements' => $evenementRepository->findAll(),
            'user'=> $user,

        ]);
    }

    //AJOUT ET AFFICHE DE EVENT DANS LA PAGE 

    #[Route('/evenement/back/event', name: 'app_evenement_new')]
    public function event(Request $request,EntityManagerInterface $entityManager,EvenementRepository $evenementRepository,SessionInterface $session): Response
    {
        $userId = $session->get('iduser');
        // Vérifier si l'ID de l'utilisateur existe dans la session
        // Récupérer l'utilisateur à partir de session
        if (!$userId)
        {
            return $this->redirectToRoute('app_utilisateur');
    
        }
        else {
            $user = $this->getDoctrine()->getRepository(Utilisateur::class)->find($userId);

        $evenement = new Evenement();
     //   $evenement->setMaxParticipants(10);
       // $evenement->setParticipants(0);
        $form = $this->createForm(EvenementType::class, $evenement);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $image = $form->get('imageEv')->getData();

            // Vérifiez si une image a été téléchargée
            if ($image) {
                // Générez un nom de fichier unique
                $nomFichier = md5(uniqid()).'.'.$image->guessExtension();
    
                // Déplacez le fichier vers le répertoire public/images
                $image->move(
                    $this->getParameter('images_directory'), // Le chemin vers votre répertoire Images dans le dossier public
                    $nomFichier
                );
    
                // Définir le nom du fichier de l'image de catégorie dans l'entité
                $evenement->setImageEv($nomFichier);
            }
            $entityManager->persist($evenement);
            $entityManager->flush();
            return $this->redirectToRoute('app_evenement_new');

        }

        return $this->renderForm('evenement/ajouterEvenement.html.twig', [
            'evenement' => $evenement,
            'form' => $form,
            'evenements' => $evenementRepository->findAll(),
            'user'=> $user,

        ]);
    }
    }
    //EDIT EVENT
    #[Route('/evenement/edit/{idEv}', name: 'app_evenement_edit')]
    public function edit(Request $request,$idEv,EntityManagerInterface $entityManager,EvenementRepository $evenementRepository): Response
    {
        $evenement = $evenementRepository->find($idEv);
        $form = $this->createForm(EvenementType::class, $evenement);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $image = $form->get('imageEv')->getData();

            // Vérifiez si une image a été téléchargée
            if ($image) {
                // Générez un nom de fichier unique
                $nomFichier = md5(uniqid()).'.'.$image->guessExtension();
    
                // Déplacez le fichier vers le répertoire public/images
                $image->move(
                    $this->getParameter('images_directory'), // Le chemin vers votre répertoire Images dans le dossier public
                    $nomFichier
                );
    
                // Définir le nom du fichier de l'image de catégorie dans l'entité
                $evenement->setImageEv($nomFichier);
            }
            $entityManager->flush();

            return $this->redirectToRoute('app_evenement_new');
        }

        return $this->renderForm("evenement/editEvenement.html.twig", ["form" => $form]);

    }
    #[Route('/evenement/delete/{idEv}', name: 'app_evenement_delete')]
    public function delete(Request $request,$idEv,EvenementRepository $evenementRepository, EntityManagerInterface $entityManager): Response
    {
        $list = $evenementRepository->find($idEv);
        $em = $this->getDoctrine()->getManager();
        $em->remove($list);
        $em->flush();

        return $this->redirectToRoute('app_evenement_new');
    }
    #[Route('/evenement/show/{idEv}', name: 'app_evenement_show')]
    public function show($idEv,EvenementRepository $evenementRepository): Response
    {
        return $this->render('evenement/show.html.twig', [
            'evenement' => $evenementRepository->find($idEv),
        ]);
    }
}
