package aria.domain.ejb;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name="languageOfBook")
public class Language {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long languageId;
    @Getter
    @Setter
    @Column(name = "languageName")
    private String languageName;

    @Override
    public String toString() {
        return languageName;
    }
}
