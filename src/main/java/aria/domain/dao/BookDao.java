package aria.domain.dao;

import aria.domain.ejb.Author;
import aria.domain.ejb.Book;
import aria.domain.ejb.Genre;
import aria.domain.ejb.QBook;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;

@Stateful
public class BookDao implements Serializable {

    @Inject
    private EntityManager em;

    public BookDao() {
    }

    public Book getForBookId(final long bookId) {
        JPAQueryFactory query = new JPAQueryFactory(em);
        QBook book = QBook.book;
        return query
                .from(book)
                .select(book)
                .where(book.bookId.eq(bookId))
                .fetchOne();
    }

    public List<Book> getBooks(){
        JPAQueryFactory query = new JPAQueryFactory(em);
        QBook book = QBook.book;
        return query
                .from(book)
                .select(book)
                .fetch();
    }

    public List<Author> getAuthorsForBookId(final long bookId){
        JPAQueryFactory query = new JPAQueryFactory(em);
        QBook book = QBook.book;
        return query
                .from(book)
                .select(book)
                .where(book.bookId.eq(bookId))
                .fetchOne().getAuthors();
    }

    public List<Genre> getGenresForBookId(final long bookId){
        JPAQueryFactory query = new JPAQueryFactory(em);
        QBook book = QBook.book;
        return query
                .from(book)
                .select(book)
                .where(book.bookId.eq(bookId))
                .fetchOne().getGenres();
    }

    public void createBook(Book book) {
        em.persist(book);
    }

}
