package br.edu.ufape.roomie.service;

import br.edu.ufape.roomie.model.Interest;
import br.edu.ufape.roomie.model.Property;
import br.edu.ufape.roomie.model.Student;
import br.edu.ufape.roomie.model.User;
import br.edu.ufape.roomie.repository.InterestRepository;
import br.edu.ufape.roomie.repository.PropertyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterestServiceTest {

    @Mock
    private InterestRepository interestRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private InterestService interestService;

    private Student student;
    private User owner;
    private Property property;

    @BeforeEach
    void setUp() {
        student = new Student();
        student.setId(1L);
        student.setName("Estudante Teste");
        student.setMajor("Ciência da Computação");

        owner = new User();
        owner.setId(2L);
        owner.setName("Dono do Imóvel");

        property = new Property();
        property.setId(100L);
        property.setOwner(owner);
        property.setTitle("Quarto perto da UFAPE");
    }

    @Test
    @DisplayName("Deve registrar o interesse com sucesso e notificar o dono")
    void testaRegistrarInteresseComSucesso() {
        when(propertyRepository.findById(100L)).thenReturn(Optional.of(property));
        when(interestRepository.existsByStudentAndProperty(student, property)).thenReturn(false);

        interestService.registerInterest(100L, student);

        verify(interestRepository, times(1)).save(any(Interest.class));
        verify(notificationService, times(1)).notifyOwnerAboutInterest(owner, student, property);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o estudante já demonstrou interesse no imóvel")
    void testaLancarExcecaoQuandoInteresseDuplicado() {
        when(propertyRepository.findById(100L)).thenReturn(Optional.of(property));
        when(interestRepository.existsByStudentAndProperty(student, property)).thenReturn(true);

        assertThatThrownBy(() -> interestService.registerInterest(100L, student))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Você já demonstrou interesse neste imóvel.");

        verify(interestRepository, never()).save(any(Interest.class));
        verify(notificationService, never()).notifyOwnerAboutInterest(any(), any(), any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o imóvel não for encontrado")
    void testaLancarExcecaoQuandoImovelNaoEncontrado() {
        when(propertyRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> interestService.registerInterest(999L, student))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Imóvel não encontrado.");

        verify(interestRepository, never()).save(any(Interest.class));
        verify(notificationService, never()).notifyOwnerAboutInterest(any(), any(), any());
    }
}