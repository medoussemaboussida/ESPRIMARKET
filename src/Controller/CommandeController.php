<?php

namespace App\Controller;
use App\Entity\Panier;
use App\Entity\Commande;
use App\Entity\Produitcart;
use App\Repository\PanierRepository;
use App\Repository\CommandeRepository;
use Symfony\Component\Translation\TranslatorInterface;
use Symfony\Component\HttpFoundation\Session\SessionInterface;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\HttpFoundation\Request;
use DateTimeImmutable;
use Symfony\Component\Mailer\MailerInterface;
use Symfony\Component\Mime\Email;
use Twilio\Rest\Client;
use App\Entity\Codepromo;

class CommandeController extends AbstractController
{
    #[Route('/commande', name: 'app_commande')]
    public function index(): Response
    {
        return $this->render('commande/index.html.twig', [
            'controller_name' => 'CommandeController',
        ]);
    }

//passer une commande avec une reduction dans partie front
#[Route('/commande/{idp}', name: 'app_commande_add_widh_widh_reduction')]
public function ajouterAvecCode(Request $request,$idp): Response
{
  
    $panier = $this->getDoctrine()->getRepository(Panier::class)->find($idp);
    $produitCart = $this->getDoctrine()->getRepository(Produitcart::class)->findBy(['idpanier' => $idp]);
    if (empty($produitCart)) {
        // Si la table produitcart est vide, rediriger l'utilisateur ou afficher un message d'erreur
        $this->addFlash('error', 'Votre panier est vide.');
        return $this->redirectToRoute('app_produit_front');
    }
    else {
    //calcul montant facture pour l'envoyer sur sms
    $totalPrix = 0;
    foreach ($produitCart as $produit) {
        $totalPrix += $produit->getIdproduit()->getPrix(); 
    }
    
    $dateCommande = new DateTimeImmutable();
    // Créer une nouvelle instance de Produitcart
    $commande = new Commande();

    // Ajouter le produit au panier
    $commande->setIdpanier($panier);
    $commande->setDatecommande($dateCommande);
    if (!$commande->getIdpanier()) {
        throw new \ErrorException('idpanier field cannot be null.');
    }
     // Ajouter 20 points à l'utilisateur
    $utilisateur = $panier->getIduser();
    $utilisateur->setNbpoints($utilisateur->getNbpoints() + 20);
    // Enregistrer l'entité Produitcart
    $entityManager = $this->getDoctrine()->getManager();
    $entityManager->persist($commande);
    $entityManager->flush();
     // Mettre à jour la quantité des produits dans le panier
     foreach ($produitCart as $produitCartItem) {
        $produitEntity = $produitCartItem->getIdproduit();
        $nouvelleQuantite = $produitEntity->getQuantite() - 1;

        $produitEntity->setQuantite($nouvelleQuantite);
        $entityManager->persist($produitEntity);
    }

    // Enregistrer les changements de quantité
    $entityManager->flush();
    $this->addFlash('success', 'Votre commande a bien été passée.');
       ///////////////////////sms//////////////////////////////////////
    /*  $utilisateur = $panier->getIduser();
        $numtel = $utilisateur->getNumtel();
        $numtel = '+216' . $numtel;
        $totalPrixFormate = number_format($totalPrix, 3, ',', ' ');

        // Récupérer le SID et le token depuis les variables d'environnement
         $sid = "AC2c5bcf5da51392b4ecbdb94e067d69cd";
         $token = "875adf76743f232f00a2889eecdea213";
        // Créer une nouvelle instance du client Twilio
        $twilio = new Client($sid, $token);

         // Envoi du SMS
    try {
        $twilio->messages->create(
            $numtel, // Numéro de téléphone du destinataire
            [
                'from' => '+12514511090', // Votre numéro Twilio
                'body' => 'Votre commande a été passée avec succès. Le total de votre facture est de ' . $totalPrixFormate . ' DT. Le livreur vous contactera lorsqu\'il arrivera. Merci!'
                ]
        );
    } catch (\Exception $e) {
        // Gérer les erreurs d'envoi de SMS
        $this->addFlash('error', 'Erreur lors de l\'envoi du SMS : ' . $e->getMessage());
        return $this->redirectToRoute('app_produit_front');
    }*/
    return $this->redirectToRoute('app_produit_front');
}
}


   //afficher tous les commandes pour admin
    #[Route('/commande/commande-back', name: 'app_commande_back')]
    public function afficherBack(Request $request,SessionInterface $session): Response
   {
    $userId = $session->get('iduser');
    // Vérifier si l'ID de l'utilisateur existe dans la session
    // Récupérer l'utilisateur à partir de session
        $user = $this->getDoctrine()->getRepository(Utilisateur::class)->find($userId);
     
    $commande = $this->getDoctrine()->getRepository(Commande::class)->findAll();
    
    return $this->render('commande/backCommande.html.twig', [
        'commande' => $commande,
        'user'=> $user,


    ]);
    }



   //passer une commande dans partie front
#[Route('/commande/{idp}', name: 'app_commande_add')]
public function ajouter(Request $request, int $idp, EntityManagerInterface $entityManager): Response
{
    $panier = $entityManager->getRepository(Panier::class)->find($idp);
    $produitCart = $entityManager->getRepository(Produitcart::class)->findBy(['idpanier' => $idp]);

    if (empty($produitCart)) {
        // Si la table produitcart est vide, rediriger l'utilisateur ou afficher un message d'erreur
        $this->addFlash('error', 'Votre panier est vide.');
        return $this->redirectToRoute('app_produit_front');
    } else {
        // Calcul du montant de la facture pour l'envoyer par SMS
        $totalPrix = 0;
        foreach ($produitCart as $produitCartItem) {
            $totalPrix += $produitCartItem->getIdproduit()->getPrix(); 
        }

        $dateCommande = new \DateTimeImmutable();
        // Créer une nouvelle instance de Commande
        $commande = new Commande();

        // Ajouter le panier à la commande
        $commande->setIdpanier($panier);
        $commande->setDatecommande($dateCommande);

        if (!$commande->getIdpanier()) {
            throw new \ErrorException('idpanier field cannot be null.');
        }

        // Enregistrer l'entité Commande
        $entityManager->persist($commande);
        $entityManager->flush();

        // Mettre à jour la quantité des produits dans le panier
        foreach ($produitCart as $produitCartItem) {
            $produitEntity = $produitCartItem->getIdproduit();
            $nouvelleQuantite = $produitEntity->getQuantite() - 1;

            $produitEntity->setQuantite($nouvelleQuantite);
            $entityManager->persist($produitEntity);
        }

        // Enregistrer les changements de quantité
        $entityManager->flush();

        $this->addFlash('success', 'Votre commande a bien été passée.');
        return $this->redirectToRoute('app_produit_front');
    

        ///////////////////////sms//////////////////////////////////////
    /*  $utilisateur = $panier->getIduser();
        $numtel = $utilisateur->getNumtel();
        $numtel = '+216' . $numtel;
        $totalPrixFormate = number_format($totalPrix, 3, ',', ' ');

        // Récupérer le SID et le token depuis les variables d'environnement
         $sid = "AC2c5bcf5da51392b4ecbdb94e067d69cd";
         $token = "875adf76743f232f00a2889eecdea213";
        // Créer une nouvelle instance du client Twilio
        $twilio = new Client($sid, $token);

         // Envoi du SMS
    try {
        $twilio->messages->create(
            $numtel, // Numéro de téléphone du destinataire
            [
                'from' => '+12514511090', // Votre numéro Twilio
                'body' => 'Votre commande a été passée avec succès. Le total de votre facture est de ' . $totalPrixFormate . ' DT. Le livreur vous contactera lorsqu\'il arrivera. Merci!'
                ]
        );
    } catch (\Exception $e) {
        // Gérer les erreurs d'envoi de SMS
        $this->addFlash('error', 'Erreur lors de l\'envoi du SMS : ' . $e->getMessage());
        return $this->redirectToRoute('app_produit_front');
    }*/



        return $this->redirectToRoute('app_produit_front');
    }
    }




    //annuler commande depuis front
    #[Route('/commande/delete/{idp}', name: 'app_commande_delete')]
    public function delete(Request $request,$idp): Response
    {
        // Récupérer toutes les commandes liées au panier avec l'ID donné
    $commandes = $this->getDoctrine()->getRepository(Commande::class)->findBy(['idpanier' => $idp]);
    $produitCart = $this->getDoctrine()->getRepository(Produitcart::class)->findBy(['idpanier' => $idp]);

    if (empty($produitCart)) {
        // Si la table produitcart est vide, rediriger l'utilisateur ou afficher un message d'erreur
        $this->addFlash('error', 'Votre panier est vide.');
        return $this->redirectToRoute('app_produit_front');
    }
    else {
    $dateActuelle = new DateTimeImmutable();

    // Supprimer chaque commande individuellement
    $entityManager = $this->getDoctrine()->getManager();
    foreach ($commandes as $commande) {
        $commandeDateFormatted = $commande->getDatecommande()->format('Y-m-d');
        // Convertir la date actuelle en une chaîne de caractères au format 'Y-m-d'
        $dateActuelleFormatted = $dateActuelle->format('Y-m-d');

        if ($commandeDateFormatted === $dateActuelleFormatted)
        {
        $entityManager->remove($commande);
        // Rediriger l'utilisateur vers une page appropriée après la suppression des commandes
    $this->addFlash('success', 'Votre Commande est annulée avec succès.');
    $entityManager->flush();

        }
        else
        {
            $this->addFlash('error', 'vous n avez pas une commande');
            
        }
    }

    return $this->redirectToRoute('app_produit_front');
}
    }
    

    //supprimer une commande depuis back
#[Route('/commande/supprimer/{id}', name: 'app_commande_supprimer')]
public function supprimer($id, CommandeRepository $repository): Response
{
    $list = $repository->find($id);
    $em = $this->getDoctrine()->getManager();
    $em->remove($list);
    $em->flush();
    return $this->redirectToRoute('app_commande_back');
}

#[Route('/apply-coupon/{idp}', name: 'apply_coupon')]
public function applyCoupon(Request $request,$idp): Response
{
    $couponCode = $request->request->get('coupon');  // Récupérer le code promo saisi

    // Rechercher le code promo dans la base de données
    $codePromo = $this->getDoctrine()->getRepository(Codepromo::class)->findOneBy(['code' => $couponCode]);

    
    $panier = $this->getDoctrine()->getRepository(Panier::class)->find($idp);
    $produitCart = $this->getDoctrine()->getRepository(Produitcart::class)->findBy(['idpanier' => $idp]);

    //calcul montant facture pour l'envoyer sur sms
    $totalPrix = 0;
    foreach ($produitCart as $produit) {
        $totalPrix += $produit->getIdproduit()->getPrix(); 
    }
    if ($codePromo) {
        // Vérifiez si le code est valide en termes de dates
        $today = new \DateTime();
        if ($codePromo->getDatedebut() <= $today && $codePromo->getDatefin() >= $today) {
            $reduction = $codePromo->getReductionassocie();  // Pourcentage de réduction
            
            // Calculer le nouveau total avec la réduction
       
              
                 $nouveauTotal = $totalPrix - ($totalPrix * ($reduction / 100));
            
            $this->addFlash('success', 'Coupon applied successfully! New total: ' . number_format($nouveauTotal, 3, '.', ' ') . ' DT');
            
            return $this->redirectToRoute('app_produit_front');  // Redirigez vers la page de paiement ou panier
        } else {
            $this->addFlash('error', 'Coupon is not valid at this time.');
        }
    } else {
        $this->addFlash('error', 'Invalid coupon code.');
    }

    return $this->redirectToRoute('app_produit_front');  // Redirigez vers la page de paiement ou panier
}
    }


