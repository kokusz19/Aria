package aria.domain.ejb;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name="authorToBook")
public class AuthorToBook {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long authorToBookId;
    @Getter
    @Setter
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bookId")
    private Book book;
    @Getter
    @Setter
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "authorId")
    private Author author;
}
