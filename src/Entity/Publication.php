<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;
use App\Repository\PublicationRepository;
use Symfony\Component\Validator\Constraints as Assert;

use Doctrine\DBAL\Types\Types;
use DateTime; // Import de DateTime

#[ORM\Table(name: "publication")]
#[ORM\Entity(repositoryClass: PublicationRepository::class)]
class Publication
{
    #[ORM\Column(name: "idPublication", type: "integer", nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: "IDENTITY")]
    private $idpublication;

    #[ORM\Column(name: "description", type: "string", length: 255, nullable: false)]
    #[Assert\NotBlank(message: "Veuillez entrer une description.")]
    private $description;

    #[ORM\Column(name: "datePublication", type: "datetime", nullable: false)]
    private DateTime $datePublication; // Utilisation de DateTime

    #[ORM\Column(name: "imagePublication", type: "string", length: 255, nullable: false)]

    private $imagepublication;
    
    #[ORM\Column(name: "titrePublication", type: "string", length: 255, nullable: false)]
    #[Assert\NotBlank(message: "Veuillez entrer un titre de publication")]
   
    private $titrepublication;

    public function getIdpublication(): ?int
    {
        return $this->idpublication;
    }

    public function getDescription(): ?string
    {
        return $this->description;
    }

    public function setDescription(string $description): self
    {
        $this->description = $description;

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

    public function getImagepublication(): ?string
    {
        return $this->imagepublication;
    }

    public function setImagepublication(string $imagepublication): self
    {
        $this->imagepublication = $imagepublication;

        return $this;
    }

    public function getTitrepublication(): ?string
    {
        return $this->titrepublication;
    }

    public function setTitrepublication(string $titrepublication): self
    {
        $this->titrepublication = $titrepublication;

        return $this;
    }

    


}
