package aria.domain.dao;

import aria.domain.ejb.Book;
import aria.domain.ejb.Author;
import aria.domain.ejb.AuthorToBook;
import aria.domain.ejb.QAuthorToBook;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;

@Stateful
public class AuthorToBookDao implements Serializable {

    @Inject
    private EntityManager em;

    public AuthorToBookDao() {
    }

    public List<AuthorToBook> getForBookId(final long bookId) {
        JPAQueryFactory query = new JPAQueryFactory(em);
        QAuthorToBook authorToBook = QAuthorToBook.authorToBook;
        return query
                .from(authorToBook)
                .select(authorToBook)
                .where(authorToBook.book.bookId.eq(bookId))
                .fetch();
    }

    // List the authors of a Book
    public List<Author> getAuthorsForBookId(final long bookId){
        JPAQueryFactory query = new JPAQueryFactory(em);
        QAuthorToBook authorToBook = QAuthorToBook.authorToBook;
        return query
                .from(authorToBook)
                .select(authorToBook.author)
                .where(authorToBook.book.bookId.eq(bookId))
                .fetch();
    }
    // List the books of a specific genre
    public List<Book> getBooksForAuthorId(final long authorId){
        JPAQueryFactory query = new JPAQueryFactory(em);
        QAuthorToBook authorToBook = QAuthorToBook.authorToBook;
        return query
                .from(authorToBook)
                .select(authorToBook.book)
                .where(authorToBook.author.authorId.eq(authorId))
                .fetch();
    }

    public void createAuthorToBook(AuthorToBook authorToBook) {
        em.persist(authorToBook);
    }

}
