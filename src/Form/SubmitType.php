<?php
namespace App\Form;

use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\SubmitType as BaseSubmitType;
use Symfony\Component\OptionsResolver\OptionsResolver;

class SubmitType extends AbstractType
{
    public function configureOptions(OptionsResolver $resolver)
    {
        $resolver->setDefaults([
            'label' => 'Ajouter un don',
        ]);
    }

    public function getParent()
    {
        return BaseSubmitType::class;
    }
}