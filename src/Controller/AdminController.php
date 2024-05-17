<?php

namespace App\Controller;

use App\Entity\Utilisateur;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Session\SessionInterface ;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\Security\Core\Security;


class AdminController extends AbstractController
{
    #[Route('/admin-dashboard', name: 'admin_dashboard')]
    public function adminDashboard(SessionInterface $session): Response
    {
       
        return $this->render('BackWeb/index.html.twig');
    }
    #[Route('/users', name: 'app_admin')]
    public function index(SessionInterface $session): Response
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

        // Fetch all users from the database
        $userRepository = $this->getDoctrine()->getRepository(Utilisateur::class);
        $users = $userRepository->findAll();
        
        return $this->render('BackWeb/users.html.twig', [
            
            'users' => $users,
            'user' => $user,
        ]);
    }
    }
    #[Route('/user/{id}/details', name: 'user_details')]
public function getUserDetails(Request $request, $id): JsonResponse
{
    // Get the user from the database
    $entityManager = $this->getDoctrine()->getManager();
    $user = $entityManager->getRepository(Utilisateur::class)->find($id);

    if (!$user) {
        return $this->json(['error' => 'User not found'], Response::HTTP_NOT_FOUND);
    }

    // Return user details as JSON response
    return $this->json([
        'nomuser' => $user->getNomuser(),
        'prenomuser' => $user->getPrenomuser(),
        'emailuser' => $user->getEmailuser(),
    ]);
}

    #[Route('/user/{id}/update', name: 'user_update')]
    public function updateUser(Request $request, $id): Response
    {
        // Get the user from the database
        $entityManager = $this->getDoctrine()->getManager();
        $user = $entityManager->getRepository(Utilisateur::class)->find($id);
    
        // Check if user exists
        if (!$user) {
            throw $this->createNotFoundException('User not found');
        }
    
        // Update user details
        // Example: Update user's first name
        $user->setNomuser($request->request->get('nomuser'));
        // Example: Update user's last name
        $user->setPrenomuser($request->request->get('prenomuser'));
        // Example: Update user's email
        $user->setEmailuser($request->request->get('emailuser'));
    
        // Save changes to the database
        $entityManager->flush();
    
        // Redirect back to the users page
        return $this->redirectToRoute('app_admin', ['id' => $user->getIduser()]);
    }
    
    

    #[Route('/user/{id}/delete', name: 'user_delete')]
    public function deleteUser($id): Response
    {
        // Get the user from the database
        $entityManager = $this->getDoctrine()->getManager();
        $user = $entityManager->getRepository(Utilisateur::class)->find($id);

        // Check if user exists
        if (!$user) {
            throw $this->createNotFoundException('User not found');
        }

        // Remove the user
        $entityManager->remove($user);
        $entityManager->flush();

        // Redirect back to the users page
        return $this->redirectToRoute('app_admin');
    }

    #[Route('/user/{id}/enable', name: 'user_enable')]
    public function enableUser($id): Response
    {
        // Get the user from the database
        $entityManager = $this->getDoctrine()->getManager();
        $user = $entityManager->getRepository(Utilisateur::class)->find($id);

        // Check if user exists
        if (!$user) {
            throw $this->createNotFoundException('User not found');
        }

        // Enable the user
        $user->setIsDisabled(false);
        $entityManager->flush();

        // Redirect back to the users page
        return $this->redirectToRoute('app_admin');
    }

    #[Route('/user/{id}/disable', name: 'user_disable')]
    public function disableUser($id): Response
    {
        // Get the user from the database
        $entityManager = $this->getDoctrine()->getManager();
        $user = $entityManager->getRepository(Utilisateur::class)->find($id);

        // Check if user exists
        if (!$user) {
            throw $this->createNotFoundException('User not found');
        }

        // Disable the user
        $user->setIsDisabled(true);
        $entityManager->flush();

        // Redirect back to the users page
        return $this->redirectToRoute('app_admin');
    }

}
