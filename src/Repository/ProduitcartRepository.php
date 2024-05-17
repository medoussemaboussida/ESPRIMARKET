<?php

namespace App\Repository;

use App\Entity\Produitcart;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Produitcart>
 *
 * @method Produitcart|null find($id, $lockMode = null, $lockVersion = null)
 * @method Produitcart|null findOneBy(array $criteria, array $orderBy = null)
 * @method Produitcart[]    findAll()
 * @method Produitcart[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class ProduitcartRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Produitcart::class);
    }

//    /**
//     * @return Produitcart[] Returns an array of Produitcart objects
//     */
//    public function findByExampleField($value): array
//    {
//        return $this->createQueryBuilder('p')
//            ->andWhere('p.exampleField = :val')
//            ->setParameter('val', $value)
//            ->orderBy('p.id', 'ASC')
//            ->setMaxResults(10)
//            ->getQuery()
//            ->getResult()
//        ;
//    }

//    public function findOneBySomeField($value): ?Produitcart
//    {
//        return $this->createQueryBuilder('p')
//            ->andWhere('p.exampleField = :val')
//            ->setParameter('val', $value)
//            ->getQuery()
//            ->getOneOrNullResult()
//        ;
//    }
}
