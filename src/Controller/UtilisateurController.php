<?php

namespace App\Controller;
use App\Entity\Utilisateur;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Security\Core\Exception\BadCredentialsException;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\Security\Http\Authentication\AuthenticationUtils;
use Symfony\Component\HttpFoundation\Session\SessionInterface;
use Symfony\Component\Security\Http\Logout\LogoutUrlGenerator;
use Symfony\Component\Mailer\MailerInterface;
use Symfony\Component\Mime\Email;
use Symfony\Component\Routing\Generator\UrlGeneratorInterface;
use PragmaRX\Google2FAQRCode\Google2FAQRCode;
use PragmaRX\Google2FA\Google2FA;
use Symfony\Component\HttpFoundation\JsonResponse;
use Twilio\Rest\Client;
use Symfony\Component\Form\FormError;
use Symfony\Component\Security\Core\Encoder\UserPasswordEncoderInterface;
use Swift_SmtpTransport;
use Swift_Mailer;
use App\Form\UtilisateurType;

class UtilisateurController extends AbstractController
{
    #[Route('/utilisateur', name: 'app_utilisateur')]
    public function index(): Response
    {
        return $this->render('utilisateur/login.html.twig', [
            
        ]);
    }
    #[Route('/login', name: 'app_login')]
public function login(Request $request, AuthenticationUtils $authenticationUtils, SessionInterface $session,UserPasswordEncoderInterface $passwordEncoder): Response
{
   // Récupérer les informations du formulaire
   $email = $request->request->get('email');
   $password = $request->request->get('password');

   // Récupérer l'entité Utilisateur à partir de l'email
   $user = $this->getDoctrine()->getRepository(Utilisateur::class)->findOneBy(['emailuser' => $email]);
   $user2 = $this->getDoctrine()->getRepository(Utilisateur::class)->findOneBy(['mdp' => $password]);

   // Vérifier si l'utilisateur existe et si le mot de passe est correct
   if ($user && (($passwordEncoder->isPasswordValid($user, $password)||$user2))) {
    if ($user->getRoles() === 'Client')
    {
    $iduser = $user->getIduser();
       $session->set('iduser', $iduser);
       return $this->redirectToRoute('app_produit');

    }
    else
    {
        $iduser = $user->getIduser();
       $session->set('iduser', $iduser);
        return $this->redirectToRoute('app_produit_ajouter');

    }
   } else {
       // Si les informations de connexion sont incorrectes, afficher un message d'erreur
       return $this->redirectToRoute('app_utilisateur');   }
}

private $logoutUrlGenerator;

    public function __construct(LogoutUrlGenerator $logoutUrlGenerator)
    {
        $this->logoutUrlGenerator = $logoutUrlGenerator;
    }

    #[Route('/logout', name: 'app_logout')]
    public function logout(): void
    {
        
    }

    #[Route('/change-password', name: 'change_password')]
public function changePassword(Request $request, UserPasswordEncoderInterface $passwordEncoder, SessionInterface $session): Response
{
    // Get the user from the session
    $userId = $session->get('iduser');

    $user = $this->getDoctrine()->getRepository(Utilisateur::class)->find($userId);
    $oldPassword = $request->request->get('old_password');
    $newPassword = $request->request->get('new_password');
    $confirmPassword = $request->request->get('confirm_password');

    // Check if the old password matches
    if (!$passwordEncoder->isPasswordValid($user, $oldPassword)) {
        // Handle invalid old password
    }

    // Check if new password and confirm password match
    if ($newPassword !== $confirmPassword) {
        // Handle password mismatch
    }

    // Encode the new password
    $encodedPassword = $passwordEncoder->encodePassword($user, $newPassword);
    $user->setPassword($encodedPassword);

    // Save the updated user entity
    $entityManager = $this->getDoctrine()->getManager();
    $entityManager->persist($user);
    $entityManager->flush();

    // Redirect to user profile page after password change
    return $this->redirectToRoute('app_profile_user');
}
#[Route('/forgot-password', name: 'app_forgot_password')]
public function forgotPassword(Request $request, \Swift_Mailer $mailer, UserPasswordEncoderInterface $passwordEncoder)
{
    
    $transport = (new Swift_SmtpTransport('smtp.gmail.com', 587, 'tls'))
        ->setUsername('fbenrabii1@gmail.com')
        ->setPassword('aufqfyumqefgkpel');
    $mailer = new Swift_Mailer($transport);

    // Handle the form submission
    if ($request->isMethod('POST')) {
        // Get the email address from the form
        $email = $request->request->get('email');

        // Find the user with the given email
        $utilisateur = $this->getDoctrine()->getRepository(Utilisateur::class)->findOneBy(['emailuser' => $email]);

        if (!$utilisateur) {
            // Redirect the user to an error page
            
             return $this->redirectToRoute('app_forgot_password');
        }

        // Generate a new password
        $newPassword = substr(md5(rand()), 0, 8);
        
        // Encode the new password
        $encodedPassword = $passwordEncoder->encodePassword($utilisateur, $newPassword);
        $utilisateur->setPassword($encodedPassword);

        // Update the user in the database
        $entityManager = $this->getDoctrine()->getManager();
        $entityManager->persist($utilisateur);
        $entityManager->flush();

        // Create the message to send
        $message = (new \Swift_Message('Mot de passe oublié'))
            ->setFrom('noreply@example.com')
            ->setTo($email);

        // Add the message body with a design and user's name
        $message->setBody(
            $this->renderView(
                'fruitkha-1.0.0/email_template.html.twig',
                ['nom' => $utilisateur->getNomuser(), 'prenom' => $utilisateur->getPrenomuser(), 'new_password' => $newPassword]
            ),
            'text/html'
        );

        // Send the message
        $mailer->send($message);

        // Redirect the user to a confirmation page
        return $this->redirectToRoute('app_forgot_password_confirm');
    }

    // Render the form
    return $this->render('fruitkha-1.0.0/forgot_password.html.twig');
}


#[Route('/forgot-password-confirm', name: 'app_forgot_password_confirm')]
public function forgotPasswordConfirm()
{
return $this->render('fruitkha-1.0.0/password_reset_confirmation.html.twig');
}

////////////////sign up/////////////////////////////////////////
#[Route('/signup', name: 'app_signup')]
public function signup(Request $request, UserPasswordEncoderInterface $passwordEncoder, SessionInterface $session): Response
{
    // replace with your own Twilio credentials
    $sid = 'AC2c5bcf5da51392b4ecbdb94e067d69cd';
    $token = '875adf76743f232f00a2889eecdea213';

    // A Twilio phone number you purchased at twilio.com/console
    $twilioPhoneNumber = '+12514511090';
    $twilioClient = new Client($sid, $token);
    
    $utilisateur = new Utilisateur();
    $form = $this->createForm(UtilisateurType::class, $utilisateur);
    $form->handleRequest($request);

    /*// Check if the form is submitted and valid
    if ($form->isSubmitted() && $form->isValid()) {
        // Check if the "Send Code" button is clicked
        if ($form->get('sendCode')->isClicked()) {
            $tel = $form->get('numtel')->getData();
            $tel= '+216' . $tel;
            $verificationCode = rand(1000, 9999);
            
            try {
                // Send the verification code via Twilio
                $message = $twilioClient->messages->create($tel, [
                    'from' => $twilioPhoneNumber,
                    'body' => sprintf('Votre code de vérification est %d', $verificationCode),
                ]);
            } catch (RestException $e) {
                // Handle the exception if the message failed to send
                $this->addFlash('error', 'Failed to send verification code');
                return $this->redirectToRoute('user_registration');
            }
            
            // Store the verification code in session
            $session->set('verification_code', $verificationCode);
            $session->set('tel', $tel);
        }*/
        
        // Handle verification code submission
      //  $submittedVerificationCode = $form->get('verificationCode')->getData();
        if ($form->isSubmitted()) {
         //   $sessionVerificationCode = $session->get('verification_code');
         
                // Verification code is correct, proceed with user creation
                // Encrypt the password before persisting
                $encodedPassword = $passwordEncoder->encodePassword($utilisateur, $utilisateur->getPassword());
                $utilisateur->setPassword($encodedPassword);
                $utilisateur->setRole('Client');
                $utilisateur->setNbpoints('0');
                
                // handle image upload
               /* $image = $form['image']->getData();
                $imagePath = $request->request->get('imagePath');
                if ($image && is_uploaded_file($image)) {
                    $imageFileName = uniqid().'.'.$image->guessExtension();
                    move_uploaded_file($image, $this->getParameter('images_directory').'/'.$imageFileName);
                    $utilisateur->setImage($imageFileName);
                }*/
                
                // Get the EntityManager
                $entityManager = $this->getDoctrine()->getManager();

                // Persist the user object
                $entityManager->persist($utilisateur);

                // Flush the changes to the database
                $entityManager->flush();

                // Redirect the user to the login page after successful signup
                return $this->redirectToRoute('app_login');
            }
    
    return $this->render('utilisateur/inscrire.html.twig', [
        'form' => $form->createView(),
    ]);
}


}

