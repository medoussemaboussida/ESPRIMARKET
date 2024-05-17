<?php

namespace App\Controller;
use App\Entity\Reclamation;
use App\Form\ReclamationType;
use App\Repository\ReclamationRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Sensio\Bundle\FrameworkExtraBundle\Configuration\ParamConverter;
use Symfony\Component\HttpFoundation\Session\SessionInterface;
use App\Entity\Utilisateur;
class ReclamationController extends AbstractController
{
    #[Route('/reclamation/back', name: 'app_reclamation')]
    public function index(ReclamationRepository $reclamationRepository,SessionInterface $session): Response
    {
        $userId = $session->get('iduser');

        if (!$userId)
        {
            return $this->redirectToRoute('app_utilisateur');
    
        }
        else {
            $user = $this->getDoctrine()->getRepository(Utilisateur::class)->find($userId);
        return $this->render('reclamation/backReclamation.html.twig', [
            'reclamations' => $reclamationRepository->findAll(),
            'user'=> $user,

        ]);
    }
    }
    #[Route('/reclamation/new', name: 'app_reclamation_new')]
    public function new(Request $request, EntityManagerInterface $entityManager,SessionInterface $session): Response
    {
        $userId = $session->get('iduser');
        $user = $this->getDoctrine()->getRepository(Utilisateur::class)->find($userId);
        $reclamation = new Reclamation();
        $form = $this->createForm(ReclamationType::class, $reclamation);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->persist($reclamation);
            $entityManager->flush();

            return $this->redirectToRoute('app_reclamation_new');
        }

        return $this->renderForm('reclamation/ajouterReclamation.html.twig', [
            'reclamation' => $reclamation,
            'form' => $form,
            'user'=> $user,
        ]);
    }

    #[Route('/reclamation/delete/{id}', name: 'app_reclamation_delete')]
    public function delete(Request $request,$id,ReclamationRepository $reclamationRepository, EntityManagerInterface $entityManager): Response
    {
        $list = $reclamationRepository->find($id);
        $em = $this->getDoctrine()->getManager();
        $em->remove($list);
        $em->flush();

        return $this->redirectToRoute('app_reclamation_new');
    }

    #[Route('/reclamation/show/{id}', name: 'app_reclamation_show')]
    public function show($id,ReclamationRepository $reclamationRepository): Response
    {
        return $this->render('reclamation/show.html.twig', [
            'reclamation' => $reclamationRepository->find($id),
        ]);
    }
}
