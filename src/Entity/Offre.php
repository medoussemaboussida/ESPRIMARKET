<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;
use Symfony\Component\Validator\Context\ExecutionContextInterface;
use App\Repository\OffreRepository;

#[ORM\Table(name: "offre")]
#[ORM\Entity(repositoryClass: OffreRepository::class)]
class Offre
{
    #[ORM\Column(name: "idOffre", type: "integer", nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: "IDENTITY")]
    private $idoffre;

    #[ORM\Column(name: "descriptionOffre", type: "string", length: 255, nullable: false)]
    private $descriptionoffre;

    #[ORM\Column(name: "nomOffre", type: "string", length: 255, nullable: false)]
    private $nomoffre;

    #[ORM\Column(name: "dateDebut", type: "date", nullable: false)]
    private $datedebut;

    #[ORM\Column(name: "dateFin", type: "date", nullable: false)]
    private $datefin;

    #[ORM\Column(name: "imageOffre", type: "string", length: 255, nullable: false)]
    private $imageoffre;

    #[ORM\Column(name: "reduction", type: "integer", nullable: false)]
    private $reduction;

    #[ORM\OneToMany(targetEntity: Produit::class, mappedBy: "offre")]

    private $produits;

    public function __construct()
    {
        $this->produits = new ArrayCollection();
    }

    public function getIdoffre(): ?int
    {
        return $this->idoffre;
    }

    public function getDescriptionoffre(): ?string
    {
        return $this->descriptionoffre;
    }

    public function setDescriptionoffre(string $descriptionoffre): static
    {
        $this->descriptionoffre = $descriptionoffre;
        return $this;
    }

    public function getNomoffre(): ?string
    {
        return $this->nomoffre;
    }

    public function setNomoffre(string $nomoffre): static
    {
        $this->nomoffre = $nomoffre;
        return $this;
    }

    public function getDatedebut(): ?\DateTimeInterface
    {
        return $this->datedebut;
    }

    public function setDatedebut(\DateTimeInterface $datedebut): static
    {
        $this->datedebut = $datedebut;
        return $this;
    }

    public function getDatefin(): ?\DateTimeInterface
    {
        return $this->datefin;
    }

    public function setDatefin(\DateTimeInterface $datefin): static
    {
        $this->datefin = $datefin;
        return $this;
    }

    public function getImageoffre(): ?string
    {
        return $this->imageoffre;
    }

    public function setImageoffre(string $imageoffre): static
    {
        $this->imageoffre = $imageoffre;
        return $this;
    }

    public function getReduction(): ?int
    {
        return $this->reduction;
    }

    public function setReduction(int $reduction): static
    {
        $this->reduction = $reduction;
        return $this;
    }

    /**
 * @return \Doctrine\Common\Collections\Collection|\App\Entity\Produit[]
     */
    public function getProduits(): Collection
    {
        return $this->produits;
    }

    public function addProduit(Produit $produit): self
    {
        if (!$this->produits->contains($produit)) {
            $this->produits[] = $produit;
            $produit->setOffre($this);
        }

        return $this;
    }

    public function removeProduit(Produit $produit): self
    {
        if ($this->produits->removeElement($produit)) {
            // Définir l'offre du produit à null
            $produit->setOffre(null);
        }

        return $this;
    }
}
