package aria.domain.ejb;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name="Act")
public class Act {
    @Getter
    @Id
    @GeneratedValue
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
