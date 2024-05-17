<?php

namespace App\Repository;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;
use App\Entity\Offre;

class OffreRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Offre::class);
    }

    public function findByCriteriaAndSort(array $criteria, string $sortBy, string $sortOrder = 'asc')
{
    $qb = $this->createQueryBuilder('o');

    if (!empty($criteria['nomOffre'])) {
        $qb->andWhere('o.nomoffre LIKE :nomOffre')
           ->setParameter('nomOffre', '%'.$criteria['nomOffre'].'%');
    }

    if (!empty($criteria['reduction'])) {
        $qb->andWhere('o.reduction = :reduction')
           ->setParameter('reduction', $criteria['reduction']);
    }

    // Gérer le tri
    $allowedSortFields = ['datedebut', 'datefin', 'reduction', 'nomOffre'];
    if (in_array($sortBy, $allowedSortFields)) {
        $qb->orderBy('o.' . $sortBy, $sortOrder);
    }

    return $qb->getQuery()->getResult();
}

}