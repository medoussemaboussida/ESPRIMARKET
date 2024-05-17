<?php

namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Doctrine\ORM\EntityManagerInterface;
use App\Entity\Notification;

class NotificationController extends AbstractController
{
    #[Route('/notifications', name: 'notifications')]
    public function getAllNotifications(EntityManagerInterface $em): Response
    {
        $notifications = $em->getRepository(Notification::class)->findAll();

        return $this->render('notification/list.html.twig', [
            'notifications' => $notifications,
        ]);
    }
}
