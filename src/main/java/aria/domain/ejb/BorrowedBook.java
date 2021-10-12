package aria.domain.ejb;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private LocalDateTime dateOfBorrow;
    @Getter
    @Setter
    @Column(name = "dateOfReturn")
    private LocalDateTime dateOfReturn;
    @Getter
    @Setter
    @Column(name = "dateToBeReturned")
    private LocalDateTime dateToBeReturned;
    @Getter
    @Setter
    @Transient
    private String stringDateOfBorrow;
    @Getter
    @Setter
    @Transient
    private String stringDateOfReturn;
    @Getter
    @Setter
    @Transient
    private String stringDateToBeReturned;
    @Getter
    @Setter
    @Transient
    private int price;
    @Getter
    @Setter
    @Transient
    private BorrowStatus currentStatus;
}
