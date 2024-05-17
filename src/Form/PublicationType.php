<?php

namespace App\Form;

use App\Entity\Publication;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Form\Extension\Core\Type\TextareaType;
use Symfony\Component\Form\Extension\Core\Type\HiddenType;
use Symfony\Component\Form\Extension\Core\Type\FileType;
use Trsteel\CkeditorBundle\Form\Type\CkeditorType;


class PublicationType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options)
    {
        $builder
            ->add('titrePublication', CKEditorType::class, [
                'label' => 'Titre de la Publication',
                'attr' => [
                    'class' => 'form-control',
                    'placeholder' => 'Titre de la publication',
                ],
            ])
            ->add('description', CKEditorType::class, [
                'label' => 'Description',
                'attr' => [
                    'class' => 'form-control',
                    'placeholder' => 'Description de la publication',
                ],
            ])
            ->add('imagePublication', FileType::class, [
                'label' => 'Image de la Publication',
                'mapped' => false,
                'required' => false,
            ])
            ->add('datePublication', HiddenType::class, [
                'mapped' => false,
            ]);
    }

    public function configureOptions(OptionsResolver $resolver)
    {
        $resolver->setDefaults([
            'data_class' => Publication::class,
            'csrf_protection' => false, // Désactiver la protection CSRF
            'validation_groups' => false, // Désactiver les groupes de validation
        ]);
    }
}

