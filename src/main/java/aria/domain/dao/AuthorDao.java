package aria.domain.dao;

import aria.domain.ejb.Author;
import aria.domain.ejb.QAuthor;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;

@Stateful
public class AuthorDao implements Serializable {

    @Inject
    private EntityManager em;

    public AuthorDao() {
    }

    public Author getForAuthorId(final long authorId) {
        JPAQueryFactory query = new JPAQueryFactory(em);
        QAuthor author = QAuthor.author;
        return query
                .from(author)
                .select(author)
                .where(author.authorId.eq(authorId))
                .fetchOne();
    }
    public List<Author> getForAuthorName(final String firstName, final String lastName) {
        JPAQueryFactory query = new JPAQueryFactory(em);
        QAuthor author = QAuthor.author;
        return query
                .from(author)
                .select(author)
                .where(author.firstName.toLowerCase().eq(firstName.toLowerCase()))
                .where(author.lastName.toLowerCase().eq(lastName.toLowerCase()))
                .fetch();
    }
    public List<Author> getAuthors(){
        JPAQueryFactory query = new JPAQueryFactory(em);
        QAuthor author = QAuthor.author;
        return query
                .from(author)
                .select(author)
                .fetch();
    }

    public void createAuthor(Author author) {
        em.persist(author);
    }

}
