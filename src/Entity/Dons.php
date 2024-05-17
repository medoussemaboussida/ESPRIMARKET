<?php

namespace App\Entity;
use Doctrine\DBAL\Types\Types;

use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;


#[ORM\Table(name: "dons")]
#[ORM\Entity]
class Dons
{
    #[ORM\Column(name: "idDons", type: "integer", nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: "IDENTITY")]
    private $iddons;

    #[ORM\Column(name: "nbpoints", type: "integer", nullable: true)]
    #[Assert\NotBlank(message:"L'objectif de points est obligatoire.")]
    #[Assert\PositiveOrZero(message:"L'objectif de points doit être positif ou zéro.")]


    private $nbpoints;

    #[ORM\Column(name: "date_ajout", type: "datetime", nullable: false)]
    private $dateAjout;

    #[ORM\Column(name: "etatStatutDons", type: "string", length: 255, nullable: true)]
    private $etatstatutdons;

    #[ORM\ManyToOne(targetEntity: "Utilisateur")]
    #[ORM\JoinColumn(name: "idUser", referencedColumnName: "idUser")]
    private $idUser;

    #[ORM\ManyToOne(targetEntity: "Evenement")]
    #[ORM\JoinColumn(name: "idEv", referencedColumnName: "id_ev")]
    private $idEv;

    public function getIdDons(): ?int
    {
        return $this->iddons;
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

    public function getDateAjout(): ?\DateTimeInterface
    {
        return $this->dateAjout;
    }

    public function setDateAjout(\DateTimeInterface $dateAjout): self
    {
        $this->dateAjout = $dateAjout;

        return $this;
    }

    public function getEtatstatutdons(): ?string
    {
        return $this->etatstatutdons;
    }

    public function setEtatstatutdons(?string $etatstatutdons): self
    {
        $this->etatstatutdons = $etatstatutdons;

        return $this;
    }

    public function getIdUser(): ?Utilisateur
    {
        return $this->idUser;
    }

    public function setIdUser(?Utilisateur $idUser): self
    {
        $this->idUser = $idUser;

        return $this;
    }
    public function getIdEv(): ?Evenement
    {
        return $this->idEv;
    }

    public function setIdEv(?Evenement $idEv): self
    {
        $this->idEv = $idEv;

        return $this;
    }
}
