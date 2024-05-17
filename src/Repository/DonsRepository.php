<?php

namespace App\Repository;

use App\Entity\Dons;

use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

class DonsRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Dons::class);
    }

    public function getDonsByUserId(int $userId): array
    {
        return $this->createQueryBuilder('d')
            ->andWhere('d.idUser = :userId')
            ->setParameter('userId', $userId)
            ->getQuery()
            ->getResult();
    }
    public function findDonsByEmail($email)
    {
        return $this->createQueryBuilder('d')
            ->join('d.idUser', 'u')
            ->andWhere('u.emailuser = :email')
            ->setParameter('email', $email)
            ->getQuery()
            ->getResult();
    }

    public function countByEtatstatutdons($etat): int
    {
        return $this->createQueryBuilder('d')
            ->select('COUNT(d)')
            ->where('d.etatstatutdons = :etat')
            ->setParameter('etat', $etat)
            ->getQuery()
            ->getSingleScalarResult();
    }

    

    public function findByEtatstatutdons($etat)
    {
        return $this->createQueryBuilder('d')
            ->andWhere('d.etatstatutdons = :etat')
            ->setParameter('etat', $etat)
            ->getQuery()
            ->getResult();
    }
}
