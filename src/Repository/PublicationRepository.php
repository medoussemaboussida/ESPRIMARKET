<?php

namespace App\Repository;

use App\Entity\Publication;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

class PublicationRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Publication::class);
    }

    public function addPublication(Publication $publication)
    {
        // Récupérer l'EntityManager
        $entityManager = $this->getEntityManager();

        // Ajouter la publication à l'EntityManager pour la persistance
        $entityManager->persist($publication);

        // Exécuter la requête pour sauvegarder la nouvelle publication
        $entityManager->flush();
    }

    public function searchByTerm(string $searchTerm): array
    {
        return $this->createQueryBuilder('p')
            ->andWhere('p.titrePublication LIKE :term OR p.description LIKE :term')
            ->setParameter('term', '%' . $searchTerm . '%')
            ->getQuery()
            ->getResult();
    }

    /**
     * Recherche les publications par titre ou description
     * en fonction du terme de recherche fourni.
     *
     * @param string $searchTerm Le terme de recherche
     * @return Publication[] Les publications correspondantes
     */
    public function findBySearchTerm(string $searchTerm): array
    {
        return $this->createQueryBuilder('p')
            ->where('p.titrepublication LIKE :searchTerm')
            ->orWhere('p.description LIKE :searchTerm')
            ->orWhere('p.datePublication LIKE :searchTerm')
            ->setParameter('searchTerm', '%'.$searchTerm.'%')
            ->getQuery()
            ->getResult();
    }
}