package aria.domain.ejb;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name="notification")
public class Notification {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long notificationId;
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

}


