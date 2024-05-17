<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;
use App\Repository\CommentaireRepository;
use Symfony\Component\Validator\Constraints as Assert;


#[ORM\Table(name: "commentaire", indexes: [
    new ORM\Index(name: "idUser", columns: ["idUser"]),
    new ORM\Index(name: "idPublication", columns: ["idPublication"])
])]
#[ORM\Entity(repositoryClass: CommentaireRepository::class)]
class Commentaire
{
    #[ORM\Column(name: "idCommentaire", type: "integer", nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: "IDENTITY")]
    private $idcommentaire;

    #[ORM\Column(name: "descriptionCommentaire", type: "string", length: 255, nullable: false)]
    #[Assert\NotBlank(message: "Veuillez entrer un commentaire.")]
    
    private $descriptioncommentaire;

    

    #[ORM\ManyToOne(targetEntity: Utilisateur::class)]
    #[ORM\JoinColumn(name: "idUser", referencedColumnName: "idUser")]
    private $iduser;
    
    #[ORM\ManyToOne(targetEntity: Publication::class)]
    #[ORM\JoinColumn(name: "idPublication", referencedColumnName: "idPublication")]
    private $idpublication;

    public function getIdcommentaire(): ?int
    {
        return $this->idcommentaire;
    }

    public function getDescriptioncommentaire(): ?string
    {
        return $this->descriptioncommentaire;
    }

    public function setDescriptioncommentaire(string $descriptioncommentaire): self
    {
        $this->descriptioncommentaire = $descriptioncommentaire;

        return $this;
    }

    public function getIduser(): ?Utilisateur
    {
        return $this->iduser;
    }

    public function setIduser(?Utilisateur $iduser): self
    {
        $this->iduser = $iduser;

        return $this;
    }

    public function getIdpublication(): ?Publication
    {
        return $this->idpublication;
    }

    public function setIdpublication(?Publication $idpublication): self
    {
        $this->idpublication = $idpublication;

        return $this;
    }


}
