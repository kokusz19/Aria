package aria.domain.ejb;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name="account")
public class Account implements Serializable {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long accountId;
    @Getter
    @Setter
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "personId")
    private Person person;
    @Getter
    @Setter
    @Column(name = "loginName", unique = true)
    private String loginName;
    @Getter
    @Setter
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "actid")
    private Act act;
    @Getter
    @Setter
    @Column(name = "password")
    private String password;
    @Getter
    @Setter
    @Column(name = "createdAt")
    private LocalDate createdAt;
    @Getter
    @Setter
    @Transient
    private List<BorrowedBook> booksNotReturnedYet;
    @Getter
    @Setter
    @Transient
    private List<BorrowedBook> booksReturned;
}
