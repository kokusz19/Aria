package aria.domain.ejb;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;


@Entity
@Table(name="borrowedBook")
public class BorrowedBook {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long borrowedBookId;
    @Getter
    @Setter
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bookId")
    private Book book;
    @Getter
    @Setter
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "accountId")
    private Account account;
    @Getter
    @Setter
    @Column(name = "dateOfBorrow")
    private LocalDate dateOfBorrow;
    @Getter
    @Setter
    @Column(name = "dateOfReturn")
    private LocalDate dateOfReturn;
    @Getter
    @Setter
    @Column(name = "dateToBeReturned")
    private LocalDate dateToBeReturned;
    @Getter
    @Setter
    @Transient
    private int price;
    @Getter
    @Setter
    @Transient
    private BorrowStatus currentStatus;
}
