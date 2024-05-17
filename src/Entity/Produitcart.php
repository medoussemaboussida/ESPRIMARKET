<?php

namespace App\Entity;

use App\Repository\ProduitcartRepository;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Table(name: "produitcart", indexes: [
    new ORM\Index(name: "idPanier", columns: ["idPanier"]),
    new ORM\Index(name: "idProduit", columns: ["idProduit"])
])]
#[ORM\Entity(repositoryClass: ProduitcartRepository::class)]
class Produitcart
{
    #[ORM\Column(name: "idPanierProduit", type: "integer", nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: "IDENTITY")]
    private $idpanierproduit;

    #[ORM\ManyToOne(targetEntity: Panier::class)]
    #[ORM\JoinColumn(name: "idPanier", referencedColumnName: "idPanier")]
    private $idpanier;

    #[ORM\ManyToOne(targetEntity: Produit::class)]
    #[ORM\JoinColumn(name: "idProduit", referencedColumnName: "idProduit")]
    private $idproduit;

    public function getIdpanierproduit(): ?int
    {
        return $this->idpanierproduit;
    }

    public function getIdpanier(): ?Panier
    {
        return $this->idpanier;
    }

    public function setIdpanier(?Panier $idpanier): static
    {
        $this->idpanier = $idpanier;

        return $this;
    }

    public function getIdproduit(): ?Produit
    {
        return $this->idproduit;
    }

    public function setIdproduit(?Produit $idproduit): static
    {
        $this->idproduit = $idproduit;

        return $this;
    }
}