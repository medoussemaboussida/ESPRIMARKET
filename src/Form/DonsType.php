<?php

namespace App\Form;

use App\Entity\Dons;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Component\Form\Extension\Core\Type\IntegerType;
use Symfony\Component\Form\Extension\Core\Type\DateType;
use Symfony\Component\Validator\Constraints\NotBlank;
use Symfony\Component\Validator\Constraints\Length;
use Symfony\Component\Validator\Constraints\Positive;


use App\Entity\Evenement;
use Symfony\Bridge\Doctrine\Form\Type\EntityType;

class DonsType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options)
    {
        $builder
            // Autres champs de votre formulaire...
            ->add('idEv', EntityType::class, [
                'label' => 'Choisissez un événement :',

                'class' => Evenement::class,
                'choice_label' => 'nomEv',
            ])
            ->add('nbpoints', IntegerType::class, [
                'label' => 'Saisir le nombre de points :',
                'constraints' => [
                    new NotBlank(['message' => 'Le nombre de points est obligatoire.']),
                    new Positive(['message' => 'Le nombre de points doit être positif .']),
                ], ])
            ->getForm();

            
        
        
    }

    public function configureOptions(OptionsResolver $resolver)
    {
        $resolver->setDefaults([
            'data_class' => Dons::class,
        ]);
    }}