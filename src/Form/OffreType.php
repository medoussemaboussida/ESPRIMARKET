<?php

namespace App\Form;

use App\Entity\Offre;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Component\Form\Extension\Core\Type\FileType;
use App\Entity\Produit;
use Symfony\Bridge\Doctrine\Form\Type\EntityType;


class OffreType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            ->add('descriptionoffre')
            ->add('nomoffre')
            ->add('datedebut')
            ->add('datefin')
            ->add('imageoffre', FileType::class, [
                'label' => 'Image',
                'mapped' => false,
                'required' => false, // Le champ n'est pas obligatoire
            ])
            ->add('reduction')
            ->add('produits', EntityType::class, [
                'class' => Produit::class,
                'choice_label' => 'nomProduit',
                'multiple' => true, // Permet la sélection de plusieurs produits
                'expanded' => false, // Affiche les produits comme cases à cocher
                'required' => true, // Le champ n'est pas obligatoire
                // Vous pouvez ajouter d'autres options de personnalisation ici si nécessaire
            ]);
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Offre::class,
        ]);
    }
}
