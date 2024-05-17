<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Security\Core\User\UserInterface;
use Symfony\Component\Serializer\Annotation\Groups;
use Symfony\Bridge\Doctrine\Validator\Constraints\UniqueEntity;

#[ORM\Table(name: "utilisateur")]
#[ORM\Entity]
class Utilisateur implements UserInterface
{
    #[ORM\Column(name: "idUser", type: "integer", nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: "IDENTITY")]
    private $iduser;

    #[ORM\Column(name: "nomUser", type: "string", length: 255, nullable: false)]
    private $nomuser;

    #[ORM\Column(name: "prenomUser", type: "string", length: 255, nullable: false)]
    private $prenomuser;

    #[ORM\Column(name: "emailUser", type: "string", length: 255, nullable: false)]
    private $emailuser;

    #[ORM\Column(name: "mdp", type: "string", length: 255, nullable: false)]
    private $mdp;

    #[ORM\Column(name: "nbPoints", type: "integer", nullable: false)]
    private $nbpoints;

    #[ORM\Column(name: "numTel", type: "integer", nullable: false)]
    private $numtel;

    #[ORM\Column(name: "role", type: "string", length: 255, nullable: false)]
    private $role;

    #[ORM\Column(name: "activationToken", type: "string", length: 250, nullable: true)]
    private $activationToken;

    #[ORM\Column(type: "string", length: 255, nullable: true)]
    private $faSecretKey;

    #[ORM\Column(type: "boolean")]
    private $is2faEnabled = false;

    #[ORM\Column(type: "boolean")]
    private $isDisabled = false;

    #[ORM\Column(name: "reclamation", type: "string", length: 255, nullable: true)]
    public $reclamation;
    #[ORM\Column(name: "image", type: "string", length: 255, nullable: true)]
    private $image;

    public function getIduser(): ?int
    {
        return $this->iduser;
    }

    public function getNomuser(): ?string
    {
        return $this->nomuser;
    }

    public function setNomuser(string $nomuser): static
    {
        $this->nomuser = $nomuser;

        return $this;
    }

    public function getPrenomuser(): ?string
    {
        return $this->prenomuser;
    }

    public function setPrenomuser(string $prenomuser): static
    {
        $this->prenomuser = $prenomuser;

        return $this;
    }

    public function getEmailuser(): ?string
    {
        return $this->emailuser;
    }

    public function setEmailuser(string $emailuser): static
    {
        $this->emailuser = $emailuser;

        return $this;
    }

    public function getMdp(): ?string
    {
        return $this->mdp;
    }

    public function setMdp(string $mdp): static
    {
        $this->mdp = $mdp;

        return $this;
    }

    public function getNbpoints(): ?int
    {
        return $this->nbpoints;
    }

    public function setNbpoints(int $nbpoints): static
    {
        $this->nbpoints = $nbpoints;

        return $this;
    }

    public function getNumtel(): ?int
    {
        return $this->numtel;
    }

    public function setNumtel(int $numtel): static
    {
        $this->numtel = $numtel;

        return $this;
    }

    public function getRoles(): ?string
    {
        // Transformez le champ $role en un tableau pour respecter l'interface UserInterface
        return $this->role;
    }

    public function setRole(string $role): static
    {
        $this->role = $role;

        return $this;
    }
    public function getReclamation(): ?string
    {
        return $this->reclamation;
    }

    public function setReclamation(?string $reclamation): self
    {
        $this->reclamation = $reclamation;

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
    public function getActivationToken(): ?string
    {
        return $this->activationToken;
    }

    public function setActivationToken(?string $activationToken): self
    {
        $this->activationToken = $activationToken;

        return $this;
    }
  
    public function getUsername(): string
    {
        return $this->emailuser; // Assuming email is the username
    }
    public function getPassword(): string
    {
        return $this->mdp;
    }

  

    public function isIs2faEnabled(): ?bool
    {
        return $this->is2faEnabled;
    }
    public function getIsDisabled(): bool
    {
        return $this->isDisabled;
    }

    public function setIsDisabled(bool $isDisabled): void
    {
        $this->isDisabled = $isDisabled;
    }

    public function getfaSecretKey(): ?string
    {
        return $this->faSecretKey;
    }
    
    public function setfaSecretKey(?string $faSecretKey): self
    {
        $this->faSecretKey = $faSecretKey;
    
        return $this;
    }
    
    public function getIs2faEnabled(): bool
    {
        return $this->is2faEnabled;
    }
    
    public function setIs2faEnabled(bool $is2faEnabled): self
    {
        $this->is2faEnabled = $is2faEnabled;
    
        return $this;
    }
    public function getSalt()
    {
        // Salt is not needed with bcrypt
        return null;
    }

    public function eraseCredentials()
    {
       
    }
    public function setPassword(string $mdp): self
    {
        $this->mdp = $mdp;

        return $this;
    }
}
