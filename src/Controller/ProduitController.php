<?php

namespace App\Controller;
use App\Entity\Categorie;
use App\Entity\Panier;
use App\Form\CategorieType;
use App\Repository\CategorieRepository;
use App\Entity\Produit;
use App\Form\ProduitType;
use App\Repository\ProduitRepository;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\HttpFoundation\File\UploadedFile;
use Symfony\Component\HttpFoundation\RedirectResponse;
use Doctrine\ORM\EntityManagerInterface;
use Doctrine\Persistence\ManagerRegistry;
use Symfony\Component\HttpFoundation\Request;
use App\Entity\Utilisateur;
use Knp\Component\Pager\PaginatorInterface;
use Dompdf\Dompdf;
use Dompdf\Options;
use Symfony\Component\HttpFoundation\File\File;
use Hexaequo\CurrencyConverterBundle\Converter;
use Symfony\Component\Security\Core\Security;
use Symfony\Component\HttpFoundation\Session\SessionInterface;
use App\Entity\Notification;

class ProduitController extends AbstractController
{
    private $paginator;

    public function __construct(PaginatorInterface $paginator)
    {
        $this->paginator = $paginator;
    }

    ///////////////////////pour page home de partie front
    #[Route('/produit', name: 'app_produit')]
    public function index(SessionInterface $session): Response
    {
  // Récupérer l'ID de l'utilisateur à partir de la session
  $userId = $session->get('iduser');

  // Vérifier si l'ID de l'utilisateur existe dans la session
  if ($userId) {
      // Récupérer l'utilisateur à partir de l'ID
      $user = $this->getDoctrine()->getRepository(Utilisateur::class)->find($userId);

      return $this->render('base.html.twig', [
          'controller_name' => 'ProduitController',
          'user' => $user,
      ]);
  } else {
      // Rediriger vers la page de connexion si l'ID de l'utilisateur n'est pas trouvé dans la session
      return $this->redirectToRoute('app_login');
  }
    }



    //ajouter et afficher
    #[Route('/produit/ajouter', name: 'app_produit_ajouter')]
    public function ajouter(Request $request,SessionInterface $session): Response
{
    $userId = $session->get('iduser');
    // Vérifier si l'ID de l'utilisateur existe dans la session
    // Récupérer l'utilisateur à partir de session
    if (!$userId)
    {
        return $this->redirectToRoute('app_utilisateur');

    }
    else
    {
        $user = $this->getDoctrine()->getRepository(Utilisateur::class)->find($userId);
    $produit = new Produit();
    $form = $this->createForm(ProduitType::class, $produit,);
    $form->handleRequest($request);
    $entityManager = $this->getDoctrine()->getManager();

    if ($form->isSubmitted() && $form->isValid()) {

        // Récupérer le nom du produit à partir du formulaire
        $nomProduit = $form->get('nomproduit')->getData();
        $prixProduit = $form->get('prix')->getData();
        // Vérifier si un produit avec le même nom et prix existe déjà dans la base de données
        $existingProduit = $this->getDoctrine()->getRepository(Produit::class)->findOneBy([
            'nomproduit' => $nomProduit,
            'prix' => $prixProduit
        ]);
        if ($existingProduit) {
            // Si le produit existe déjà, augmenter sa quantité
            $existingProduit->setQuantite($existingProduit->getQuantite() + 1);
        }
        else {
        /** @var UploadedFile $image */
        $image = $form->get('imageproduit')->getData();

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
            $produit->setImageproduit($nomFichier);
        }
    
        // Enregistrez la catégorie dans la base de données
        $entityManager->persist($produit);
    }
        $entityManager->flush();

        // Redirigez l'utilisateur après l'ajout réussi
        return $this->redirectToRoute('app_produit_ajouter');
    
}
    //affichage
    $produits = $this->getDoctrine()->getRepository(Produit::class)->findAll();
   
   
 
    $pagination = $this->paginator->paginate(
        $produits,
        $request->query->getInt('page', 1),
        4
    );
    $categories = $this->getDoctrine()
    ->getRepository(Categorie::class)
    ->createQueryBuilder('c')
    ->select('c, COUNT(p.categorie) as count')
    ->leftJoin(Produit::class, 'p', 'WITH', 'p.categorie = c.idcategorie')
    ->groupBy('c.idcategorie')
    ->orderBy('count', 'DESC')
    ->getQuery()
    ->getResult();
    
    return $this->render('produit/ajouterProduit.html.twig', [
        'form' => $form->createView(),
        'produits' => $pagination,
        'categories' => $categories,
        'pagination' => $pagination,
        'user'=> $user,

    ]);
}
}



//chercher un produit
#[Route('/produit/search', name: 'app_produit_search')]

public function searchAjax(Request $request,SessionInterface $session): Response
{
    $userId = $session->get('iduser');
    // Vérifier si l'ID de l'utilisateur existe dans la session
    // Récupérer l'utilisateur à partir de session
        $user = $this->getDoctrine()->getRepository(Utilisateur::class)->find($userId);
    // Rendre la vue partielle pour les résultats de la recherche
    $produit = new Produit();
    $form = $this->createForm(ProduitType::class, $produit,);
    $form->handleRequest($request);
    $entityManager = $this->getDoctrine()->getManager();

    if ($form->isSubmitted() && $form->isValid()) {

        // Récupérer le nom du produit à partir du formulaire
        $nomProduit = $form->get('nomproduit')->getData();
        $prixProduit = $form->get('prix')->getData();
        // Vérifier si un produit avec le même nom et prix existe déjà dans la base de données
        $existingProduit = $this->getDoctrine()->getRepository(Produit::class)->findOneBy([
            'nomproduit' => $nomProduit,
            'prix' => $prixProduit
        ]);
        if ($existingProduit) {
            // Si le produit existe déjà, augmenter sa quantité
            $existingProduit->setQuantite($existingProduit->getQuantite() + 1);
        }
        else {
       
        $image = $form->get('imageproduit')->getData();

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
            $produit->setImageproduit($nomFichier);
        }
    
        // Enregistrez la catégorie dans la base de données
        $entityManager->persist($produit);
    }
        $entityManager->flush();

        // Redirigez l'utilisateur après l'ajout réussi
        return $this->redirectToRoute('app_produit_ajouter');
    
}

$searchTerm = $request->query->get('search');

// Effectuer la recherche en fonction du contenu du champ de recherche
$produits = $this->getDoctrine()->getRepository(Produit::class)->searchByKeywordOrPriceOrQuantity($searchTerm);
$pagination = $this->paginator->paginate(
    $produits,
    $request->query->getInt('page', 1),
    4
);
    $categories = $this->getDoctrine()
    ->getRepository(Categorie::class)
    ->createQueryBuilder('c')
    ->select('c, COUNT(p.categorie) as count')
    ->leftJoin(Produit::class, 'p', 'WITH', 'p.categorie = c.idcategorie')
    ->groupBy('c.idcategorie')
    ->orderBy('count', 'DESC')
    ->getQuery()
    ->getResult();

    return $this->render('produit/ajouterProduit.html.twig', [
        'form' => $form->createView(),
        'produits' => $pagination,
        'categories' => $categories,
        'searchTerm' => $searchTerm,
        'user'=> $user,
        'pagination' => $pagination,

    ]);
    
}


//afficher produits dans front + connecté user 1
#[Route('/produit/frontProduit', name: 'app_produit_front')]
    public function afficherFront(Request $request,Converter $converter,SessionInterface $session): Response
{
    ///////////////SESSION///////////////
    $userId = $session->get('iduser');
    // Vérifier si l'ID de l'utilisateur existe dans la session
    if ($userId) {
        // Récupérer l'utilisateur à partir de session
        $user = $this->getDoctrine()->getRepository(Utilisateur::class)->find($userId);
        //recuperer panier
    $panier = $this->getDoctrine()->getRepository(Panier::class)->findOneBy(['iduser' => $user]);
    if($panier==null)
{
    $panier = new Panier();
        $panier->setIduser($user);
        $entityManager = $this->getDoctrine()->getManager();
        $entityManager->persist($panier);
        $entityManager->flush();
}
    $produits = $this->getDoctrine()->getRepository(Produit::class)->findAll();
    $notifications = $this->getDoctrine()->getRepository(Notification::class)->findAll();
    $prixEnEuros = []; // Tableau pour stocker les prix convertis en euros
    $prixEnDollars =[];
    $prixEnYuan=[];
        // Conversion des prix en euros
        foreach ($produits as $produit) {
            $prixEnEuros[$produit->getIdproduit()] = $converter->convert($produit->getPrix(), 'TND', 'EUR');
            $prixEnDollars[$produit->getIdproduit()] = $converter->convert($produit->getPrix(), 'TND', 'USD');
            $prixEnYuan[$produit->getIdproduit()] = $converter->convert($produit->getPrix(), 'TND', 'CNY');

        }
    $categories = $this->getDoctrine()->getRepository(Categorie::class)->findAll();
    return $this->render('produit/frontProduit.html.twig', [
        'produits' => $produits,
         'user'=> $user,
         'categories' => $categories,
         'prixEnEuros' => $prixEnEuros,
         'prixEnDollars' => $prixEnDollars,
         'prixEnYuan' => $prixEnYuan,
         'notifications' => $notifications,


    ]);
    }
    else
   {
    return $this->redirectToRoute('app_utilisateur');   

   }
}




//filtrer products selon categorie choisi dans la partie front
#[Route('/produit/frontProduit/{id}', name: 'app_categorie_front')]
public function produitsParCategorie(Request $request,$id,Converter $converter,SessionInterface $session): Response
{
    $userId = $session->get('iduser');
     // Récupérer l'utilisateur à partir de session
        $user = $this->getDoctrine()->getRepository(Utilisateur::class)->find($userId);
    $produits = $this->getDoctrine()->getRepository(Produit::class)->findBy(['categorie' => ['idcategoire' => $id]]);
    $categories = $this->getDoctrine()->getRepository(Categorie::class)->findAll();
    $prixEnEuros = []; // Tableau pour stocker les prix convertis en euros
    $prixEnDollars =[];
    $prixEnYuan=[];
        // Conversion des prix en euros
        foreach ($produits as $produit) {
            $prixEnEuros[$produit->getIdproduit()] = $converter->convert($produit->getPrix(), 'TND', 'EUR');
            $prixEnDollars[$produit->getIdproduit()] = $converter->convert($produit->getPrix(), 'TND', 'USD');
            $prixEnYuan[$produit->getIdproduit()] = $converter->convert($produit->getPrix(), 'TND', 'CNY');
        }
    return $this->render('produit/frontProduit.html.twig', [
        'produits' => $produits,
        'user'=> $user,
         'categories' => $categories,
         'prixEnEuros' => $prixEnEuros,
         'prixEnDollars' => $prixEnDollars,
         'prixEnYuan' => $prixEnYuan,

    ]);
}



//supprimer un produit
#[Route('/produit/supprimer/{id}', name: 'app_produit_supprimer')]
public function supprimer($id, ProduitRepository $repository): Response
{
    $list = $repository->find($id);
    $em = $this->getDoctrine()->getManager();
    $em->remove($list);
    $em->flush();
    return $this->redirectToRoute('app_produit_ajouter');
}



 
//modifier un produit
#[Route('/produit/edit/{id}', name: 'app_produit_edit')]
public function edit(ProduitRepository $repository, $id, Request $request)
{
    $produit = $repository->find($id);
    $form = $this->createForm(ProduitType::class, $produit);
    $form->handleRequest($request);

    if ($form->isSubmitted()&& $form->isValid()) {
        // Si le formulaire est soumis et valide, procédez à la sauvegarde des modifications
        $image = $form->get('imageproduit')->getData();

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
            $produit->setImageproduit($nomFichier);
        }
        $em = $this->getDoctrine()->getManager();
            $em->flush();
        return $this->redirectToRoute('app_produit_ajouter');
    }

    return $this->renderForm("produit/editProduit.html.twig", ["form" => $form]);

}



//trie prix asc
#[Route('/produit/prixAsc', name: 'app_prix_asc')]
    public function triePrixAsc(Request $request,SessionInterface $session): Response
    {
        $userId = $session->get('iduser');
     // Récupérer l'utilisateur à partir de session
        $user = $this->getDoctrine()->getRepository(Utilisateur::class)->find($userId);
        $produit = new Produit();
        $form = $this->createForm(ProduitType::class, $produit,);
        $form->handleRequest($request);
        $entityManager = $this->getDoctrine()->getManager();
    
        if ($form->isSubmitted() && $form->isValid()) {
    
            // Récupérer le nom du produit à partir du formulaire
            $nomProduit = $form->get('nomproduit')->getData();
            $prixProduit = $form->get('prix')->getData();
            // Vérifier si un produit avec le même nom et prix existe déjà dans la base de données
            $existingProduit = $this->getDoctrine()->getRepository(Produit::class)->findOneBy([
                'nomproduit' => $nomProduit,
                'prix' => $prixProduit
            ]);
            if ($existingProduit) {
                // Si le produit existe déjà, augmenter sa quantité
                $existingProduit->setQuantite($existingProduit->getQuantite() + 1);
            }
            else {
            /** @var UploadedFile $image */
            $image = $form->get('imageproduit')->getData();
    
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
                $produit->setImageproduit($nomFichier);
            }
        
            // Enregistrez la catégorie dans la base de données
            $entityManager->persist($produit);
        }
            $entityManager->flush();
    
            // Redirigez l'utilisateur après l'ajout réussi
            return $this->redirectToRoute('app_produit_ajouter');
        
    }
        //affichage
        $produits = $this->getDoctrine()->getRepository(Produit::class)->findBy([], ['prix' => 'ASC']);
       
       
     
        $pagination = $this->paginator->paginate(
            $produits,
            $request->query->getInt('page', 1),
            4
        );
        $categories = $this->getDoctrine()
        ->getRepository(Categorie::class)
        ->createQueryBuilder('c')
        ->select('c, COUNT(p.categorie) as count')
        ->leftJoin(Produit::class, 'p', 'WITH', 'p.categorie = c.idcategorie')
        ->groupBy('c.idcategorie')
        ->orderBy('count', 'DESC')
        ->getQuery()
        ->getResult();
        
        return $this->render('produit/ajouterProduit.html.twig', [
            'form' => $form->createView(),
            'produits' => $pagination,
            'categories' => $categories,
            'pagination' => $pagination,
            'user'=> $user,
        ]);
    }


//trie prix desc
#[Route('/produit/prixDesc', name: 'app_prix_desc')]
public function triePrixDesc(Request $request,SessionInterface $session): Response
{
    $userId = $session->get('iduser');
     // Récupérer l'utilisateur à partir de session
        $user = $this->getDoctrine()->getRepository(Utilisateur::class)->find($userId);

    $produit = new Produit();
    $form = $this->createForm(ProduitType::class, $produit,);
    $form->handleRequest($request);
    $entityManager = $this->getDoctrine()->getManager();

    if ($form->isSubmitted() && $form->isValid()) {

        // Récupérer le nom du produit à partir du formulaire
        $nomProduit = $form->get('nomproduit')->getData();
        $prixProduit = $form->get('prix')->getData();
        // Vérifier si un produit avec le même nom et prix existe déjà dans la base de données
        $existingProduit = $this->getDoctrine()->getRepository(Produit::class)->findOneBy([
            'nomproduit' => $nomProduit,
            'prix' => $prixProduit
        ]);
        if ($existingProduit) {
            // Si le produit existe déjà, augmenter sa quantité
            $existingProduit->setQuantite($existingProduit->getQuantite() + 1);
        }
        else {
        /** @var UploadedFile $image */
        $image = $form->get('imageproduit')->getData();

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
            $produit->setImageproduit($nomFichier);
        }
    
        // Enregistrez la catégorie dans la base de données
        $entityManager->persist($produit);
    }
        $entityManager->flush();

        // Redirigez l'utilisateur après l'ajout réussi
        return $this->redirectToRoute('app_produit_ajouter');
    
}
    //affichage
    $produits = $this->getDoctrine()->getRepository(Produit::class)->findBy([], ['prix' => 'DESC']);
   
   
 
    $pagination = $this->paginator->paginate(
        $produits,
        $request->query->getInt('page', 1),
        4
    );
    $categories = $this->getDoctrine()
    ->getRepository(Categorie::class)
    ->createQueryBuilder('c')
    ->select('c, COUNT(p.categorie) as count')
    ->leftJoin(Produit::class, 'p', 'WITH', 'p.categorie = c.idcategorie')
    ->groupBy('c.idcategorie')
    ->orderBy('count', 'DESC')
    ->getQuery()
    ->getResult();
    
    return $this->render('produit/ajouterProduit.html.twig', [
        'form' => $form->createView(),
        'produits' => $pagination,
        'categories' => $categories,
        'pagination' => $pagination,
        'user'=> $user,
    ]);
}

//trie nomProduit desc
#[Route('/produit/nomAsc', name: 'app_nomproduit_asc')]
public function trieNomAsc(Request $request,SessionInterface $session): Response
{
    $userId = $session->get('iduser');
     // Récupérer l'utilisateur à partir de session
        $user = $this->getDoctrine()->getRepository(Utilisateur::class)->find($userId);
    $produit = new Produit();
    $form = $this->createForm(ProduitType::class, $produit,);
    $form->handleRequest($request);
    $entityManager = $this->getDoctrine()->getManager();

    if ($form->isSubmitted() && $form->isValid()) {

        // Récupérer le nom du produit à partir du formulaire
        $nomProduit = $form->get('nomproduit')->getData();
        $prixProduit = $form->get('prix')->getData();
        // Vérifier si un produit avec le même nom et prix existe déjà dans la base de données
        $existingProduit = $this->getDoctrine()->getRepository(Produit::class)->findOneBy([
            'nomproduit' => $nomProduit,
            'prix' => $prixProduit
        ]);
        if ($existingProduit) {
            // Si le produit existe déjà, augmenter sa quantité
            $existingProduit->setQuantite($existingProduit->getQuantite() + 1);
        }
        else {
        /** @var UploadedFile $image */
        $image = $form->get('imageproduit')->getData();

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
            $produit->setImageproduit($nomFichier);
        }
    
        // Enregistrez la catégorie dans la base de données
        $entityManager->persist($produit);
    }
        $entityManager->flush();

        // Redirigez l'utilisateur après l'ajout réussi
        return $this->redirectToRoute('app_produit_ajouter');
    
}
    //affichage
    $produits = $this->getDoctrine()->getRepository(Produit::class)->findBy([], ['nomproduit' => 'ASC']);
   
   
 
    $pagination = $this->paginator->paginate(
        $produits,
        $request->query->getInt('page', 1),
        4
    );
    $categories = $this->getDoctrine()
    ->getRepository(Categorie::class)
    ->createQueryBuilder('c')
    ->select('c, COUNT(p.categorie) as count')
    ->leftJoin(Produit::class, 'p', 'WITH', 'p.categorie = c.idcategorie')
    ->groupBy('c.idcategorie')
    ->orderBy('count', 'DESC')
    ->getQuery()
    ->getResult();
    
    return $this->render('produit/ajouterProduit.html.twig', [
        'form' => $form->createView(),
        'produits' => $pagination,
        'categories' => $categories,
        'pagination' => $pagination,
        'user'=> $user,
    ]);
}


//trie nomProduit desc
#[Route('/produit/nomDesc', name: 'app_nomproduit_desc')]
public function trieNomDesc(Request $request,SessionInterface $session): Response
{
    $userId = $session->get('iduser');
     // Récupérer l'utilisateur à partir de session
        $user = $this->getDoctrine()->getRepository(Utilisateur::class)->find($userId);

    $produit = new Produit();
    $form = $this->createForm(ProduitType::class, $produit,);
    $form->handleRequest($request);
    $entityManager = $this->getDoctrine()->getManager();

    if ($form->isSubmitted() && $form->isValid()) {

        // Récupérer le nom du produit à partir du formulaire
        $nomProduit = $form->get('nomproduit')->getData();
        $prixProduit = $form->get('prix')->getData();
        // Vérifier si un produit avec le même nom et prix existe déjà dans la base de données
        $existingProduit = $this->getDoctrine()->getRepository(Produit::class)->findOneBy([
            'nomproduit' => $nomProduit,
            'prix' => $prixProduit
        ]);
        if ($existingProduit) {
            // Si le produit existe déjà, augmenter sa quantité
            $existingProduit->setQuantite($existingProduit->getQuantite() + 1);
        }
        else {
        /** @var UploadedFile $image */
        $image = $form->get('imageproduit')->getData();

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
            $produit->setImageproduit($nomFichier);
        }
    
        // Enregistrez la catégorie dans la base de données
        $entityManager->persist($produit);
    }
        $entityManager->flush();

        // Redirigez l'utilisateur après l'ajout réussi
        return $this->redirectToRoute('app_produit_ajouter');
    
}
    //affichage
    $produits = $this->getDoctrine()->getRepository(Produit::class)->findBy([], ['nomproduit' => 'DESC']);
   
   
 
    $pagination = $this->paginator->paginate(
        $produits,
        $request->query->getInt('page', 1),
        4
    );
    $categories = $this->getDoctrine()
    ->getRepository(Categorie::class)
    ->createQueryBuilder('c')
    ->select('c, COUNT(p.categorie) as count')
    ->leftJoin(Produit::class, 'p', 'WITH', 'p.categorie = c.idcategorie')
    ->groupBy('c.idcategorie')
    ->orderBy('count', 'DESC')
    ->getQuery()
    ->getResult();
    
    return $this->render('produit/ajouterProduit.html.twig', [
        'form' => $form->createView(),
        'produits' => $pagination,
        'categories' => $categories,
        'pagination' => $pagination,
        'user'=> $user,
    ]);
}


//generate pdf
#[Route('/produit/pdf', name: 'app_produit_pdf')]
public function generatePdf(): Response
    {
            // Créer une nouvelle instance de Dompdf avec des options
            $options = new Options();
            $options->set('isHtml5ParserEnabled', true);
            $options->set('isRemoteEnabled', true); // Activer l'utilisation de ressources distantes
            $dompdf = new Dompdf($options);
            // Récupérer les produits depuis la base de données
            $entityManager = $this->getDoctrine()->getManager();
           // $produits = $entityManager->getRepository(Produit::class)->findAll();
           $categories = $entityManager->getRepository(Categorie::class)->findAll();

    // Générer le contenu HTML pour le PDF
   // $html = '<img src="/public/front/assets/img/logo.png">';
   $html = '';
    foreach ($categories as $categorie) {
        // Ajouter le nom de la catégorie
        $html .= '<h1 style="text-align: center; color: #011528;">' . $categorie->getNomCategorie() . '</h1>';

        // Récupérer les produits de cette catégorie
        $produits = $entityManager->getRepository(Produit::class)->findBy(['categorie' => $categorie->getIdCategorie()]);

        // Générer le tableau des produits
        $html .= '<table style="border-collapse: collapse; width: 100%;">';
        $html .= '<tr>';
        $html .= '<th style="background-color: #011528; color: #EE8B60;">Nom</th>';
        $html .= '<th style="background-color: #011528; color: #EE8B60;">Quantite</th>';
        $html .= '<th style="background-color: #011528; color: #EE8B60;">Prix</th>';
        $html .= '</tr>';
        foreach ($produits as $produit) {
            $html .= '<tr>';
            $html .= '<td style="border: 1px solid black;">' . $produit->getNomProduit() . '</td>';
            $html .= '<td style="border: 1px solid black;">' . $produit->getQuantite() . '</td>';
            $html .= '<td style="border: 1px solid black;">'. number_format($produit->getPrix(), 3, ',', '.') . '</td>';
            $html .= '</tr>';
        }
        $html .= '</table>';
    }

            // Charger le contenu HTML dans Dompdf
            $dompdf->loadHtml($html);
    
            // Rendre le PDF
            $dompdf->render();
    
            // Enregistrer le fichier PDF dans le répertoire public
            $pdfFilePath = $this->getParameter('kernel.project_dir') . '/public/products.pdf';
            file_put_contents($pdfFilePath, $dompdf->output());
    
            // Retourner une réponse indiquant le succès du téléchargement
            $this->addFlash('success', 'Le fichier PDF a été généré et téléchargé avec succès.');
    
            // Rediriger l'utilisateur vers une autre page
            return $this->redirectToRoute('app_produit_ajouter');    
    }

    #[Route('/produit/like/{id}', name: 'app_produit_like', methods: ['POST'])]
    public function likeProduct($id, EntityManagerInterface $em, Request $request): Response
    {
        // Find the product by ID
        $produit = $em->getRepository(Produit::class)->find($id);

        if (!$produit) {
            // If product is not found, return an error response
            return new Response("Product not found", 404);
        }

        // Increment the product's rating (simplified example, adjust as needed)
        $currentRating = $produit->getRating() ?? 0;
        $produit->setRating($currentRating + 1);

        // Persist and flush changes to the database
        $em->persist($produit);
        $em->flush();

        return new Response("Product liked", 200);
    }

    // Route to handle "dislike" action for a product
    #[Route('/produit/dislike/{id}', name: 'app_produit_dislike', methods: ['POST'])]
    public function dislikeProduct($id, EntityManagerInterface $em, Request $request): Response
    {
        // Find the product by ID
        $produit = $em->getRepository(Produit::class)->find($id);

        if (!$produit) {
            // If product is not found, return an error response
            return new Response("Product not found", 404);
        }

        // Decrement the product's rating (simplified example, adjust as needed)
        $currentRating = $produit->getRating() ?? 0;
        if ($currentRating > 0) {
            $produit->setRating($currentRating - 1);
        }

        // Persist and flush changes to the database
        $em->persist($produit);
        $em->flush();

        return new Response("Product disliked", 200);
    }
}
