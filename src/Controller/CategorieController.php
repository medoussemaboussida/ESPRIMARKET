<?php

namespace App\Controller;

use App\Entity\Categorie;
use App\Form\CategorieType;
use App\Repository\CategorieRepository;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\File\UploadedFile;
use Symfony\Component\HttpFoundation\RedirectResponse;
use Doctrine\ORM\EntityManagerInterface;
use Doctrine\Persistence\ManagerRegistry;
use Symfony\Component\Security\Core\Security;
use App\Entity\Utilisateur;
use Symfony\Component\HttpFoundation\Session\SessionInterface;

class CategorieController extends AbstractController
{
    #[Route('/categorie', name: 'app_categorie')]
    public function index(): Response
    {
        return $this->render('categorie/index.html.twig', [
            'controller_name' => 'CategorieController',
        ]);
    }
    

    #[Route('/categorie/ajouter', name: 'app_categorie_ajouter')]
    public function ajouter(Request $request,SessionInterface $session): Response
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

    $categorie = new Categorie();
    $form = $this->createForm(CategorieType::class, $categorie);
    $form->handleRequest($request);

    if ($form->isSubmitted() && $form->isValid()) {
        /** @var UploadedFile $image */
        $image = $form->get('imagecategorie')->getData();

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
            $categorie->setImagecategorie($nomFichier);
        }

        // Enregistrez la catégorie dans la base de données
        $entityManager = $this->getDoctrine()->getManager();
        $entityManager->persist($categorie);
        $entityManager->flush();

        // Redirigez l'utilisateur après l'ajout réussi
        return $this->redirectToRoute('app_categorie_ajouter');
    }
    //affichage
    $categories = $this->getDoctrine()->getRepository(Categorie::class)->findAll();
 
    
    return $this->render('categorie/ajouterCategorie.html.twig', [
        'form' => $form->createView(),
        'categories' => $categories,
        'user' => $user,

    ]);}
}


#[Route('/categorie/supprimer/{id}', name: 'app_categorie_supprimer')]
public function supprimer($id, CategorieRepository $repository): Response
{
    $list = $repository->find($id);
    $em = $this->getDoctrine()->getManager();
    $em->remove($list);
    $em->flush();
    return $this->redirectToRoute('app_categorie_ajouter');
}


#[Route('/categorie/edit/{id}', name: 'app_categorie_edit')]
public function edit(CategorieRepository $repository, $id, Request $request)
{
    $categorie = $repository->find($id);
    $form = $this->createForm(CategorieType::class, $categorie);
    $form->handleRequest($request);

    if ($form->isSubmitted()&& $form->isValid()) {
        // Si le formulaire est soumis et valide, procédez à la sauvegarde des modifications
        $image = $form->get('imagecategorie')->getData();

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
            $categorie->setImagecategorie($nomFichier);
        }
        $em = $this->getDoctrine()->getManager();
            $em->flush();
        return $this->redirectToRoute('app_categorie_ajouter');
    }

    return $this->renderForm("categorie/editCategorie.html.twig", ["form" => $form]);

}
/*
#[Route('/categorie/afficher', name: 'app_categorie_afficher')]
public function afficher(): Response
{
    // Récupérer toutes les catégories de la base de données
    $categories = $this->getDoctrine()->getRepository(Categorie::class)->findAll();

    return $this->render('categorie/afficherCategorie.html.twig', [
        'categories' => $categories,
    ]);
}*/

}