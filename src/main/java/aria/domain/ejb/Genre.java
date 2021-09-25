package aria.domain.ejb;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name="genre")
public class Genre {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long genreId;
    @Getter
    @Setter
    @Column(name = "genreName")
    private String genreName;

    @Override
    public String toString() {
        return genreName;
    }
}


