package br.edu.ufape.roomie.service;

import br.edu.ufape.roomie.model.Interest;
import br.edu.ufape.roomie.model.Property;
import br.edu.ufape.roomie.model.Student;
import br.edu.ufape.roomie.repository.InterestRepository;
import br.edu.ufape.roomie.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InterestService {

    private final InterestRepository interestRepository;
    private final PropertyRepository propertyRepository;
    private final NotificationService notificationService;

    @Transactional
    public void registerInterest(Long propertyId, Student student) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Imóvel não encontrado."));

        if (interestRepository.existsByStudentAndProperty(student, property)) {
            throw new IllegalStateException("Você já demonstrou interesse neste imóvel.");
        }

        Interest interest = new Interest(student, property);
        interestRepository.save(interest);

        notificationService.notifyOwnerAboutInterest(property.getOwner(), student, property);
    }
}