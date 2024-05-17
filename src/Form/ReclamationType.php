<?php

namespace App\Form;

use App\Entity\Reclamation;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Component\Validator\Constraints\NotBlank;
use Symfony\Component\Validator\Constraints\Email;
use Symfony\Component\Validator\Constraints\DateTime;
class ReclamationType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
        ->add('message', null, [
            'constraints' => [
                new NotBlank(),
            ],
        ])
        ->add('type', null, [
            'constraints' => [
                new NotBlank(),
            ],
        ])
        ->add('date', null, [
            'constraints' => [
                new NotBlank(),
                new DateTime(),
            ],
        ])
        ->add('email', null, [
            'constraints' => [
                new NotBlank(),
                new Email(),
            ],
        ])
        ->add('nom', null, [
            'constraints' => [
                new NotBlank(),
            ],
        ])
    ;
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Reclamation::class,
        ]);
    }
}
