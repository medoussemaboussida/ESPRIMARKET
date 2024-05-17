<?php

namespace App\Form;
use App\Entity\Dons;
use App\Entity\Utilisateur;
use App\Entity\Evenement;

use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\ChoiceType as SymfonyChoiceType;
use Symfony\Component\OptionsResolver\OptionsResolver;

class ChoiceType extends AbstractType
{
    public function configureOptions(OptionsResolver $resolver)
    {
        $resolver->setDefaults([
            'choices' => [], // Les choix possibles, à remplir lors de l'utilisation du formulaire
        ]);
    }

    public function getParent()
    {
        // Utilisez le type de formulaire ChoiceType de Symfony
        return SymfonyChoiceType::class;
    }
}
