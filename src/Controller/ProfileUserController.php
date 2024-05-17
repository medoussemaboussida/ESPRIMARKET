<?php

namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\HttpFoundation\Session\SessionInterface ;
use App\Entity\Utilisateur;
use PragmaRX\Google2FAQRCode\Google2FAQRCode;
use PragmaRX\Google2FA\Google2FA;
use App\Services\QrcodeService;
use Symfony\Component\HttpFoundation\Request;


class ProfileUserController extends AbstractController
{
    #[Route('/profile-user', name: 'app_profile_user')]
    public function userSettings(SessionInterface $session)
    {
        
     
        $userId = $session->get('iduser');

        $user = $this->getDoctrine()->getRepository(Utilisateur::class)->find($userId);

        if ($user->getIs2faEnabled() === false) {
            $message = 'Vous n\'avez pas votre Google Authentication active. Cliquez ici pour l\'activer.';
        } else {
            $message = null;
        }
    
        return $this->render('fruitkha-1.0.0/userprofile.html.twig', [
            'message' => $message,
            'user' => $user,
        ]);
    }

   
    #[Route('/enable2fa', name: 'user_enable_2fa')]
    public function enable2fa(SessionInterface $session, QrcodeService $qrcodeService)
{
    $userId = $session->get('iduser');
    $user = $this->getDoctrine()->getRepository(Utilisateur::class)->find($userId);


    // Generate a secret key for the user
    $google2fa = new Google2FA();
    $secret = $google2fa->generateSecretKey();

    // Generate a QR code for the secret key
    $google2faQr = new Google2FAQRCode();
    $qrCodeUrl = $google2faQr->getQRCodeUrl(
        'Esprit Market', // Name of your application
        $user->getEmailuser(),
        $secret
    );
    $qrCodeData = $qrcodeService->qrcode($qrCodeUrl);

    // Store the secret key and set 2FA enabled in the user's record
    $user->setfaSecretKey($secret);
    $user->setIs2faEnabled(true);
    $entityManager = $this->getDoctrine()->getManager();
    $entityManager->persist($user);
    $entityManager->flush();
    
    
    // Render the 2FA setup page with the QR code
    return $this->render('fruitkha-1.0.0/2fa_setup.html.twig', [
        
        'qr_code_data' => $qrCodeData,
        
    ]);
}

#[Route('/update-image', name: 'user_update_image')]
public function updateUserImage(Request $request, SessionInterface $session)
{
    $userId = $session->get('iduser');

    $user = $this->getDoctrine()->getRepository(Utilisateur::class)->find($userId); 
       dump($userId);
    $entityManager = $this->getDoctrine()->getManager();
    $user = $entityManager->getRepository(Utilisateur::class)->find($userId);

    $imageFile = $request->files->get('image_file');

    // Handle the case when no image file is uploaded
    if (!$imageFile) {
        // Return an error response or redirect back to the settings page with an error message
        return $this->redirectToRoute('app_profile_user');
    }

    // Generate a unique filename for the uploaded file
    $newFilename = uniqid().'.'.$imageFile->getClientOriginalExtension();

    // Move the uploaded file to a location on the server
    $imageFile->move(
        $this->getParameter('images_directory'), // the target directory defined in services.yaml
        $newFilename
    );

    // Update the user's profile with the new image filename
    $user->setImage($newFilename);

    // Persist the changes to the database
    $entityManager->flush();

    // Update the user object stored in the session with the new image filename
    $session->set('user', $user);

    return $this->redirectToRoute('app_profile_user');
}

}
