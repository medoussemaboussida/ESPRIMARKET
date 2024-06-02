<?php

namespace App\Controller;

use App\Entity\Produitcart;
use App\Entity\Panier;
use App\Entity\Utilisateur;
use App\Entity\Produit;
use App\Form\ProduitType;
use App\Repository\ProduitRepository;
use App\Repository\ProduitcartRepository;
use App\Repository\PanierRepository;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\JsonResponse;
use Hexaequo\CurrencyConverterBundle\Converter;

class ProduitcartController extends AbstractController
{
    #[Route('/produitcart', name: 'app_produitcart')]
    public function index(): Response
    {
        return $this->render('produitcart/index.html.twig', [
            'controller_name' => 'ProduitcartController',
        ]);
    }

  
 

//ajouter meme produit avec bouton +
#[Route('/produitcart/ajouterPlus/{id}/{idp}', name: 'app_produitcart_ajouter')]
public function ajouterP($id, $idp, ProduitcartRepository $repository, PanierRepository $panierRepository, Converter $converter): Response
{
    $entityManager = $this->getDoctrine()->getManager();
    $panier = $panierRepository->find($idp);
    $produit = $entityManager->getRepository(Produit::class)->find($id);

    // Vérifier si le produit existe déjà dans le panier
    $produitCart = $entityManager->getRepository(Produitcart::class)->findBy(['idpanier' => $panier, 'idproduit' => $produit]);

    // Compter le nombre de fois que ce produit est dans le panier
    $quantiteDansPanier = count($produitCart);

    // Vérifier si la quantité maximale a été atteinte
    if ($quantiteDansPanier >= $produit->getQuantite()) {
        return new JsonResponse([
            'success' => false,
            'message' => 'La quantité maximale pour ce produit a été atteinte.',
        ]);
    }

    // Ajouter le produit au panier
    $nouveauProduit = new Produitcart();
    $nouveauProduit->setIdproduit($produit);
    $nouveauProduit->setIdpanier($panier);

    // Enregistrer le nouveau produit dans la base de données
    $entityManager->persist($nouveauProduit);
    $entityManager->flush();

    // Recalculer les données du panier
    $produitCart = $entityManager->getRepository(Produitcart::class)->findBy(['idpanier' => $panier]);
    $quantite = [];
    $produitsUniques = [];

    // Calcul montant facture
    $totalPrix = 0;
    foreach ($produitCart as $produitItem) {
        $totalPrix += $produitItem->getIdproduit()->getPrix();
    }

    $prixEnEuros = $converter->convert($totalPrix, 'TND', 'EUR');
    $prixEnDollars = $converter->convert($totalPrix, 'TND', 'USD');
    $prixEnYuan = $converter->convert($totalPrix, 'TND', 'CNY');

    // Parcourir chaque produit dans le produitcart et compter les quantités
    foreach ($produitCart as $produitItem) {
        $produitId = $produitItem->getIdproduit()->getIdproduit();
        if (array_key_exists($produitId, $quantite)) {
            $quantite[$produitId]++;
        } else {
            $quantite[$produitId] = 1;
            $produitsUniques[$produitId] = $produitItem->getIdproduit();
        }
    }

    // Renvoyer une réponse JSON avec les données mises à jour
    $response = new JsonResponse([
        'success' => true,
        'message' => 'Produit ajouté au panier avec succès',
        'quantite' => $quantite,
        'totalPrixFormatted' => number_format($totalPrix, 3, '.', ' ') . ' DT',
        'totalPrixEnEurosFormatted' => number_format($prixEnEuros, 3, '.', ' ') . ' EUR',
        'totalPrixEnDollarsFormatted' => number_format($prixEnDollars, 3, '.', ' ') . ' USD',
        'totalPrixEnYuanFormatted' => number_format($prixEnYuan, 3, '.', ' ') . ' CNY',
    ]);

    return $response;
}


 
   //supprimer un produit de votre panier avec bouton -
   #[Route('/produitcart/supprimer/{id}/{idp}', name: 'app_produitcart_supprimer')]
   public function supprimer($id,$idp,ProduitcartRepository $repository,PanierRepository $panierRepository,Converter $converter): Response
   {
     //recherche et suppression aleatoire de produit se repete plusieurs fois
     $panier = $panierRepository->find($idp);
     $produits = $repository->findBy(['idproduit' => $id, 'idpanier' => $panier]);
     $produit = $produits[array_rand($produits)];

       $em = $this->getDoctrine()->getManager();
       $em->remove($produit);
       $em->flush();
       $produitCart = $this->getDoctrine()->getRepository(Produitcart::class)->findBy(['idpanier' => $panier]);
       $quantite = [];
       $produitsUniques = [];
    //calcul montant facture
    $totalPrix = 0;
    // Parcourez chaque produit et ajoutez son prix au total
    foreach ($produitCart as $produit) {
        $totalPrix += $produit->getIdproduit()->getPrix(); 
    }
    $prixEnEuros = $converter->convert($totalPrix, 'TND', 'EUR');
$prixEnDollars = $converter->convert($totalPrix, 'TND', 'USD');
$prixEnYuan = $converter->convert($totalPrix, 'TND', 'CNY');
       // Parcourir chaque produit dans le produitcart et si un produit se repete plusieurs fois , on stocke dans variable quantite , et affiche ce produit une seule fois dans le tableau
       foreach ($produitCart as $produit) {
       
           if (array_key_exists($produit->getIdproduit()->getIdproduit(), $quantite)) {
               $quantite[$produit->getIdproduit()->getIdproduit()]++;
           } else {
               $quantite[$produit->getIdproduit()->getIdproduit()] = 1;
               $produitsUniques[$produit->getIdproduit()->getIdproduit()] = $produit->getIdproduit();
           }
       }
       
       // Renvoyer une réponse JSON avec les données mises à jour
    $response = new JsonResponse([
        'success' => true,
        'message' => 'Produit supprimé au panier avec succès',
        'quantite' => $quantite,
        'totalPrixFormatted' => number_format($totalPrix, 3, '.', ' ') . ' DT',
        'totalPrixEnEurosFormatted' => number_format($prixEnEuros, 3, '.', ' ') . ' EUR',
    'totalPrixEnDollarsFormatted' => number_format($prixEnDollars, 3, '.', ' ') . ' USD',
    'totalPrixEnYuanFormatted' => number_format($prixEnYuan, 3, '.', ' ') . ' CNY',
    
    ]);

    return $response;
   }
   
 //ajouter un produit a votre panier
 #[Route('/produitcart/ajouter/{idProduit}/{idUser}', name: 'app_produit_cart')]

 public function ajouter(Request $request, $idProduit, $idUser): Response
 {


    //chercher le panier de user passé en parametre
     $panier = $this->getDoctrine()->getRepository(Panier::class)->findOneBy(['iduser' => $idUser]);
    
     //chercher le produit passe en parametre
     $produit = $this->getDoctrine()->getRepository(Produit::class)->find($idProduit);

    if($produit->getQuantite()==0)
    {
        $this->addFlash('error', 'Ce produit est indisponible pour le moment.');

    }
    else
    {
     // Créer une nouvelle instance de Produitcart
     $produitCart = new Produitcart();

     // Ajouter le produit et panier dans produitcart
     $produitCart->setIdproduit($produit);
     $produitCart->setIdpanier($panier);

     // Enregistrer l'entité Produitcart
     $entityManager = $this->getDoctrine()->getManager();
     $entityManager->persist($produitCart);
     $entityManager->flush(); 
    }
     return $this->redirectToRoute('app_produit_front');
   
 }



    //afficher la panier avec les produits choisis
    #[Route('/produitcart/afficher-panier/{idUser}', name: 'afficher_produit_panier')]
    public function afficherPanier($idUser,Converter $converter): Response
    {
        //user connecté 
        $user = $this->getDoctrine()->getRepository(Utilisateur::class)->findOneBy(['iduser' => $idUser]);

        // Récupérer le panier de l'utilisateur spécifié en parametre 
        $panier = $this->getDoctrine()->getRepository(Panier::class)->findOneBy(['iduser' => $idUser]);
        $produitCartRepository = $this->getDoctrine()->getRepository(Produitcart::class);
        $produitsDansPanier = $produitCartRepository->findBy(['idpanier' => $panier]);

        //selectionner produitcart selon panier specifique
        $produitCart = $this->getDoctrine()->getRepository(Produitcart::class)->findBy(['idpanier' => $panier]);

        //calcul montant facture
        $totalPrix = 0;
        // Parcourez chaque produit et ajoutez son prix au total
        foreach ($produitCart as $produit) {
            $totalPrix += $produit->getIdproduit()->getPrix(); 
        }
        
        foreach ($produitCart as $produit) {
$prixEnEuros = $converter->convert($produit->getIdproduit()->getPrix(), 'TND', 'EUR');
$prixEnDollars = $converter->convert($produit->getIdproduit()->getPrix(), 'TND', 'USD');
$prixEnYuan = $converter->convert($produit->getIdproduit()->getPrix(), 'TND', 'CNY');
}


        $quantite = [];
        $produitsUniques = [];

        // Parcourir chaque produit dans le produitcart et si un produit se repete plusieurs fois , on stocke dans variable quantite , et affiche ce produit une seule fois dans le tableau
        foreach ($produitCart as $produit) {
        
            if (array_key_exists($produit->getIdproduit()->getIdproduit(), $quantite)) {
                $quantite[$produit->getIdproduit()->getIdproduit()]++;
            } else {
                $quantite[$produit->getIdproduit()->getIdproduit()] = 1;
                $produitsUniques[$produit->getIdproduit()->getIdproduit()] = $produit->getIdproduit();
            }
        }

        return $this->render('produitcart/panier.html.twig', [
            'produitsUniques' => $produitsUniques,
            'produitsDansPanier' => $produitsDansPanier,
            'panier' => $panier,
            'totalPrix' => $totalPrix,
            'quantite' => $quantite, // Passer le tableau de quantité à la vue
            'user'=> $user,
            'prixEnEuros' => $prixEnEuros,
            'prixEnDollars' => $prixEnDollars,
            'prixEnYuan' => $prixEnYuan,
        ]);
    }



}
