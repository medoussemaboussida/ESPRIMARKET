<?php

namespace App\Entity;

use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;




#[ORM\Table(name: "demandedons", indexes: [
    new ORM\Index(name: "idUtilisateur", columns: ["idUtilisateur"]),
    new ORM\Index(name: "idDons", columns: ["idDons"])

])]
#[ORM\Entity]
class Demandedons
{
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: "IDENTITY")]
    #[ORM\Column(name: "idDemande", type: "integer", nullable: false)]
    private ?int $idDemande;

    #[ORM\Column(name: "contenu", type: "text", length: 65535, nullable: true, options: ["default" => "NULL"])]
   

    private ?string $contenu = null;

    #[ORM\Column(name: "image", type: "string", length: 255, nullable: true, options: ["default" => "NULL"])]
    private ?string $image = null;

    #[ORM\Column(name: "datePublication", type: "datetime", nullable: false)]

    private \DateTimeInterface $datePublication;

    #[ORM\Column(name: "nbpoints", type: "integer", nullable: true)]
    private ?int $nbpoints = null;

    #[ORM\Column(name: "nomuser", type: "string", length: 255, nullable: true, options: ["default" => "NULL"])]
    private ?string $nomuser = null;

    #[ORM\Column(name: "prenomuser", type: "string", length: 255, nullable: true, options: ["default" => "NULL"])]
    private ?string $prenomuser = null;

    #[ORM\Column(name: "objectifPoints", type: "integer", nullable: true)]
 
    private ?int $objectifPoints;
    

    #[ORM\Column(name: "delai", type: "date", nullable: true)]

    private ?\DateTimeInterface $delai;

    #[ORM\ManyToOne(targetEntity: "Dons")]
    #[ORM\JoinColumn(name: "idDons", referencedColumnName: "idDons")]
    private ?Dons $idDons;

    #[ORM\ManyToOne(targetEntity: "Utilisateur")]
    #[ORM\JoinColumn(name: "idUtilisateur", referencedColumnName: "idUser")]
    private ?Utilisateur $idUtilisateur;

    public function getIdDemande(): ?int
    {
        return $this->idDemande;
    }

    public function getContenu(): ?string
    {
        return $this->contenu;
    }

    public function setContenu(?string $contenu): self
    {
        $this->contenu = $contenu;
        return $this;
    }

    public function getImage(): ?string
    {
        return $this->image;
    }

    public function setImage(?string $image): self
    {
        $this->image = $image;
        return $this;
    }

    public function getDatePublication(): ?\DateTimeInterface
    {
        return $this->datePublication;
    }

    public function setDatePublication(\DateTimeInterface $datePublication): self
    {
        $this->datePublication = $datePublication;
        return $this;
    }

    public function getNbpoints(): ?int
    {
        return $this->nbpoints;
    }

    public function setNbpoints(?int $nbpoints): self
    {
        $this->nbpoints = $nbpoints;
        return $this;
    }

    public function getNomuser(): ?string
    {
        return $this->nomuser;
    }

    public function setNomuser(?string $nomuser): self
    {
        $this->nomuser = $nomuser;
        return $this;
    }

    public function getPrenomuser(): ?string
    {
        return $this->prenomuser;
    }

    public function setPrenomuser(?string $prenomuser): self
    {
        $this->prenomuser = $prenomuser;
        return $this;
    }

    public function getIdDons(): ?Dons
    {
        return $this->idDons;
    }

    public function setIdDons(?Dons $idDons): self
    {
        $this->idDons = $idDons;
        return $this;
    }

    public function getIdUtilisateur(): ?Utilisateur
    {
        return $this->idUtilisateur;
    }

    public function setIdUtilisateur(?Utilisateur $idUtilisateur): self
    {
        $this->idUtilisateur = $idUtilisateur;
        return $this;
    }

    public function getObjectifPoints(): ?int
    {
        return $this->objectifPoints;
    }

    public function setObjectifPoints(?int $objectifPoints): self
    {
        $this->objectifPoints = $objectifPoints;
        return $this;
    }

    public function getDelai(): ?\DateTimeInterface
    {
        return $this->delai;
    }

    public function setDelai(?\DateTimeInterface $delai): self
    {
        $this->delai = $delai;
        return $this;
    }
}





