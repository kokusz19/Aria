package aria.domain.ejb;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name="person")
public class Person {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long personId;
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
    @Getter
    @Setter
    @Column(name = "phoneNumber")
    private String phoneNumber;
    @Getter
    @Setter
    @Column(name = "email")
    private String email;
    @Getter
    @Setter
    @Column(name = "address")
    private String address;
}
