<?php

namespace App\Form;

use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;
use App\Entity\Dons;
use App\Entity\Utilisateur;
use App\Entity\Evenement;
use App\Entity\Demandedons;


use Symfony\Component\Form\Extension\Core\Type\IntegerType;

class ModifierPointsFormType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
        ->add('nbpoints', IntegerType::class, [
            'label' => 'Nouveau nombre de points',
        ]);
    
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            // Configure your form options here
        ]);
    }
}
