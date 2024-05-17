<?php


namespace App\Repository;

use App\Entity\Demandedons;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

class DemandedonsRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Demandedons::class);
    }

    public function getDemandesAvecUtilisateurs(): array
    {
        $entityManager = $this->getEntityManager();

        $query = $entityManager->createQuery(
            'SELECT d, u.nomuser, u.prenomuser, d.nbpoints' .
            'FROM App\Entity\Demandedons d ' .
            'JOIN d.idUtilisateur u'
        );
    

        return $query->getResult();
    }
    public function countByEtat($etat)
{
    return $this->createQueryBuilder('d')
        ->select('COUNT(d)')
        ->andWhere('d.etatstatutdons = :etat')
        ->setParameter('etat', $etat)
        ->getQuery()
        ->getSingleScalarResult();
}

public function findByEmail($email)
{
    return $this->createQueryBuilder('d')
        ->join('d.idUtilisateur', 'u')
        ->andWhere('u.emailuser = :email')
        ->setParameter('email', $email)
        ->getQuery()
        ->getResult();
}
public function findAllOrderedByDate($direction)
{
    return $this->createQueryBuilder('d')
        ->orderBy('d.datePublication', $direction)
        ->getQuery()
        ->getResult();
}
public function findAllOrderedByPoints($sortDirection)
    {
        $qb = $this->createQueryBuilder('d');

        // Vérifiez la direction du tri
        if ($sortDirection === 'asc') {
            $qb->orderBy('d.nbpoints', 'ASC');
        } else {
            $qb->orderBy('d.nbpoints', 'DESC');
        }

        // Exécutez la requête et retournez les résultats
        return $qb->getQuery()->getResult();
    }
    public function findFilteredAndSorted($filter, $sort)
    {
        $queryBuilder = $this->createQueryBuilder('d');
    
        // Ajouter les conditions de filtrage
        if ($filter === 'alphabetical') {
            $queryBuilder->orderBy('d.contenu', 'ASC');
        } elseif ($filter === 'date') {
            $queryBuilder->orderBy('d.datePublication', 'ASC');
        }
    
        // Ajouter les conditions de tri uniquement si aucun filtre n'est appliqué
        if (!$filter) {
            if ($sort === 'asc') {
                $queryBuilder->orderBy('d.nbpoints', 'ASC');
            } elseif ($sort === 'desc') {
                $queryBuilder->orderBy('d.nbpoints', 'DESC');
            }
        }
    
        return $queryBuilder->getQuery()->getResult();
    }
    

    // DemandedonsRepository.php

public function findMostRecent($limit = 10)
{
    return $this->createQueryBuilder('d')
        ->orderBy('d.datePublication', 'DESC')
        ->setMaxResults($limit)
        ->getQuery()
        ->getResult();
}

public function findOldest($limit = 10)
{
    return $this->createQueryBuilder('d')
        ->orderBy('d.datePublication', 'ASC')
        ->setMaxResults($limit)
        ->getQuery()
        ->getResult();
}

public function findFilteredAndSort($filter, $sort)
{
    $queryBuilder = $this->createQueryBuilder('d');

 if ($filter === 'date') {
        $queryBuilder->orderBy('d.datePublication', 'ASC');
    }

    // Ajouter les conditions de tri
    if ($sort === 'asc') {
        $queryBuilder->orderBy('d.nbpoints', 'ASC');
    } elseif ($sort === 'desc') {
        $queryBuilder->orderBy('d.nbpoints', 'DESC');
    }

    return $queryBuilder->getQuery()->getResult();
}




}
