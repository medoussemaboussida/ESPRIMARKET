<?php

namespace App\Repository;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;
use App\Entity\Codepromo;

class CodeRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Codepromo::class);
    }


    public function findByCriteriaAndSort(array $criteria, string $sortBy = 'datedebut', string $sortOrder = 'asc')
    {
        $qb = $this->createQueryBuilder('c');
    
        if (!empty($criteria['code'])) {
            $qb->andWhere('c.code LIKE :code')
               ->setParameter('code', '%' . $criteria['code'] . '%');
        }

        if (!empty($criteria['reductionassocie'])) {
            $qb->andWhere('c.reductionassocie = :reductionassocie')
               ->setParameter('reductionassocie', (int)$criteria['reductionassocie']);
        }
    
        // Ajouter des conditions de tri
        $allowedSortFields = ['datedebut', 'datefin', 'reductionassocie'];
        if (in_array($sortBy, $allowedSortFields)) {
            $qb->orderBy('c.' . $sortBy, $sortOrder);
        }
    
        return $qb->getQuery()->getResult();
    }
    

}
