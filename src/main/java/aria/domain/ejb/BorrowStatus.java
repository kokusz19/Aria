package aria.domain.ejb;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name="borrowStatus")
public class BorrowStatus {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long borrowStatusId;
    @Getter
    @Setter
    @Column(name = "borrowStatusName")
    private String borrowStatusName;
}


