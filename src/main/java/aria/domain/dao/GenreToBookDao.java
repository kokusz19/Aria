package aria.domain.dao;

import aria.domain.ejb.Book;
import aria.domain.ejb.Genre;
import aria.domain.ejb.GenreToBook;
import aria.domain.ejb.QGenreToBook;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;

@Stateful
public class GenreToBookDao implements Serializable {

    @Inject
    private EntityManager em;

    public GenreToBookDao() {
    }

    public List<GenreToBook> getForBookId(final long bookId) {
        JPAQueryFactory query = new JPAQueryFactory(em);
        QGenreToBook genreToBook = QGenreToBook.genreToBook;
        return query
                .from(genreToBook)
                .select(genreToBook)
                .where(genreToBook.book.bookId.eq(bookId))
                .fetch();
    }

    // List the genres of a Book
    public List<Genre> getGenresForBookId(final long bookId){
        JPAQueryFactory query = new JPAQueryFactory(em);
        QGenreToBook genreToBook = QGenreToBook.genreToBook;
        return query
                .from(genreToBook)
                .select(genreToBook.genre)
                .where(genreToBook.book.bookId.eq(bookId))
                .fetch();
    }
    // List the books of a specific genre
    public List<Book> getBooksForGenreId(final long genreId){
        JPAQueryFactory query = new JPAQueryFactory(em);
        QGenreToBook genreToBook = QGenreToBook.genreToBook;
        return query
                .from(genreToBook)
                .select(genreToBook.book)
                .where(genreToBook.genre.genreId.eq(genreId))
                .fetch();
    }

    public void createGenreToBook(GenreToBook genreToBook) {
        em.persist(genreToBook);
    }

}
