package aria.domain.ejb;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;


@Entity
@Table(name="book")
public class Book {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long bookId;
    @Getter
    @Setter
    @Column(name = "bookTitle")
    private String bookTitle;
    @Getter
    @Setter
    @Column(name = "isbn")
    private String isbn;
    @Getter
    @Setter
    @Column(name = "publishedAt")
    private LocalDate publishedAt;
    @Getter
    @Setter
    @Column(name = "maxItems")
    private long maxItems;
    @Getter
    @Setter
    @Column(name = "availableItems")
    private long availableItems;
    @Getter
    @Setter
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "languageId")
    private Language language;

    @Getter
    @Setter
    @Transient
    private List<Genre> genres;
    @Getter
    @Setter
    @Transient
    private String genresString;
    @Getter
    @Setter
    @Transient
    private List<Author> authors;
    @Getter
    @Setter
    @Transient
    private String authorsString;

}
