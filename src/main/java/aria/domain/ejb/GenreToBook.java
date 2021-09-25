package aria.domain.ejb;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name="genreToBook")
public class GenreToBook {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long genreToBookId;
    @Getter
    @Setter
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bookId")
    private Book book;
    @Getter
    @Setter
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "genreId")
    private Genre genre;
}
