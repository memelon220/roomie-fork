package br.edu.ufape.roomie.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "interesse", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id_estudante", "id_imovel"})
})
@Data
@NoArgsConstructor
public class Interest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_interesse")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_estudante", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "id_imovel", nullable = false)
    private Property property;

    @Column(name = "data_interesse", nullable = false)
    private LocalDateTime interestDate;

    public Interest(Student student, Property property) {
        this.student = student;
        this.property = property;
        this.interestDate = LocalDateTime.now();
    }
}