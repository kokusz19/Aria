package aria.domain.ejb;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name="borrowStatusToBorrowedBook")
public class BorrowStatusToBorrowedBook {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long borrowStatusToBorrowedBookId;
    @Getter
    @Setter
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "borrowStatusId")
    private BorrowStatus borrowStatus;
    @Getter
    @Setter
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "borrowedBookId")
    private BorrowedBook borrowedBook;
    @Getter
    @Setter
    @Column(name = "updateDate")
    private LocalDateTime updateDate;
}


