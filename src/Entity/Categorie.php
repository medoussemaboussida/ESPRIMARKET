<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;
use App\Repository\CategorieRepository;
use Symfony\Component\Validator\Constraints as Assert;

#[ORM\Table(name: "categorie")]
#[ORM\Entity(repositoryClass: CategorieRepository::class)]
class Categorie
{
    #[ORM\Column(name: "idCategorie", type: "integer", nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: "IDENTITY")]
    private $idcategorie;

    #[ORM\Column(name: "nomCategorie", type: "string", length: 255, nullable: false)]
    #[Assert\NotBlank(message: "Veuillez entrer un nom de catégorie.")]
    #[Assert\Length(
        min: 5,
        max: 20,
        minMessage: "Le nom de categorie doit comporter {{ limit }} caractères minimum.",
        maxMessage: "Le nom de categorie doit comporter {{ limit }} caractères maximum."
    )]
    private $nomcategorie;

    #[ORM\Column(name: "imageCategorie", type: "string", length: 255, nullable: false)]
    private $imagecategorie;

    public function getIdcategorie(): ?int
    {
        return $this->idcategorie;
    }

    public function getNomcategorie(): ?string
    {
        return $this->nomcategorie;
    }

    public function setNomcategorie(string $nomcategorie): static
    {
        $this->nomcategorie = $nomcategorie;

        return $this;
    }

    public function getImagecategorie(): ?string
    {
        return $this->imagecategorie;
    }

    public function setImagecategorie(string $imagecategorie): static
    {
        $this->imagecategorie = $imagecategorie;

        return $this;
    }


}
