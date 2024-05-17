<?php

namespace App\Repository;

use App\Entity\Produit;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Produit>
 *
 * @method Produit|null find($id, $lockMode = null, $lockVersion = null)
 * @method Produit|null findOneBy(array $criteria, array $orderBy = null)
 * @method Produit[]    findAll()
 * @method Produit[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class ProduitRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Produit::class);
    }

//    /**
//     * @return Produit[] Returns an array of Produit objects
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

//    public function findOneBySomeField($value): ?Produit
//    {
//        return $this->createQueryBuilder('p')
//            ->andWhere('p.exampleField = :val')
//            ->setParameter('val', $value)
//            ->getQuery()
//            ->getOneOrNullResult()
//        ;
//    }

public function findByKeyword(string $keyword)
    {
        return $this->createQueryBuilder('p')
            ->where('p.nomproduit LIKE :keyword')
            ->setParameter('keyword', '%' . $keyword . '%')
            ->getQuery()
            ->getResult();
    }
    public function findByCategoryName(string $keyword): array
    {
        return $this->createQueryBuilder('p')
            ->join('p.categorie', 'c') // Assurez-vous que "categorie" est le nom de la relation dans l'entité Produit
            ->where('c.nomcategorie LIKE :keyword')
            ->setParameter('keyword', '%'.$keyword.'%')
            ->getQuery()
            ->getResult();
    }
    public function getCategoryStatistics()
    {
        return $this->createQueryBuilder('p')
            ->select('c.nomcategorie as category', 'COUNT(p.idproduit) as productCount')
            ->join('p.categorie', 'c')
            ->groupBy('c.idcategorie')
            ->orderBy('productCount', 'DESC')
            ->getQuery()
            ->getResult();
    }
    public function searchByKeywordOrPriceOrQuantity($searchTerm)
    {
        return $this->createQueryBuilder('p')
            ->andWhere('p.nomproduit LIKE :searchTerm')
            ->orWhere('p.prix = :searchTerm2')
            ->orWhere('p.quantite = :searchTerm3')
            ->setParameter('searchTerm', '%'.$searchTerm.'%')
            ->setParameter('searchTerm2', $searchTerm)
            ->setParameter('searchTerm3', $searchTerm)
            ->getQuery()
            ->getResult();
    }

}
