<?php

namespace App\Form;
use App\Entity\Utilisateur;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Form\Extension\Core\Type\EmailType;
use Symfony\Component\Form\Extension\Core\Type\PasswordType;
use Symfony\Component\Form\Extension\Core\Type\IntegerType;
use Symfony\Component\Form\Extension\Core\Type\TelType;
use Symfony\Component\Form\Extension\Core\Type\ChoiceType;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Bridge\Doctrine\Validator\Constraints\UniqueEntity;
use Symfony\Component\Validator\Constraints as Assert;
use Symfony\Component\Validator\Context\ExecutionContextInterface;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Component\Form\Extension\Core\Type\FileType;
use Symfony\Component\Form\Extension\Core\Type\SubmitType;
class UtilisateurType extends AbstractType
{
    private $entityManager;


    public function __construct(EntityManagerInterface $entityManager)
    {
        $this->entityManager = $entityManager;
    }
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $builder
            ->add('nomuser', TextType::class, [
                'label' => 'Username'
            ])
            ->add('prenomuser', TextType::class, [
                'label' => 'Last Name'
            ])
            ->add('emailuser', EmailType::class, [
                'label' => 'Email Address',
                'constraints' => [
                    new Assert\NotBlank(),
                    new Assert\Email(),
                    new Assert\Callback([$this, 'validateUniqueEmail']),
                ],
            ])
            ->add('mdp', PasswordType::class, [
                'label' => 'Password'
            ])
            ->add('numtel', TelType::class, [
                'label' => 'Phone Number'
            ])
            ->add('verificationCode', null, [
                'mapped' => false,
                'required' => true,
                'label' => 'Code de vérification',
            ])
            ->add('sendCode', SubmitType::class, [
                'label' => 'Envoyer le code',
                'attr' => ['class' => 'btn btn-primary'],
            ])
            ->add('image', FileType::class, [
                'label' => 'Image',
                'mapped' => false,
                'required' => false,
            ]);
            
    }
    public function validateUniqueEmail($email, ExecutionContextInterface $context): void
    {
        // Check if the email already exists in the database
        $existingUser = $this->entityManager->getRepository(Utilisateur::class)->findOneBy(['emailuser' => $email]);

        if ($existingUser) {
            $context->buildViolation('This email is already in use. Please choose a different email address.')
                ->atPath('emailuser')
                ->addViolation();
        }
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Utilisateur::class,
        ]);
    }
}