package aria.domain.ejb;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name="act")
public class Act {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long actId;
    @Getter
    @Setter
    @Column(name = "roleName")
    private String roleName;
    @Getter
    @Setter
    @Column(name = "rolePrio")
    private Long rolePrio;

}
