package aria.domain.ejb;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;


@Entity
@Table(name="author")
public class Author {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long authorId;
    @Getter
    @Setter
    @Column(name = "firstName")
    private String firstName;
    @Getter
    @Setter
    @Column(name = "lastName")
    private String lastName;
    @Getter
    @Setter
    @Column(name = "dateOfBirth")
    private LocalDate dateOfBirth;

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
