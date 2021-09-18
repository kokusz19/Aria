package aria.domain.dao;

import aria.domain.ejb.Person;
import com.querydsl.jpa.impl.JPAQueryFactory;
import aria.domain.ejb.Genre;
import aria.domain.ejb.QGenre;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;

@Stateful
public class GenreDao implements Serializable {

    @Inject
    private EntityManager em;

    public GenreDao() {
    }

    public Genre getForGenreName(final String genreName) {
        JPAQueryFactory query = new JPAQueryFactory(em);
        QGenre genre = QGenre.genre;
        return query
                .from(genre)
                .select(genre)
                .where(genre.genreName.eq(genreName))
                .fetchOne();
    }

    public Genre getForGenreId(final long genreId) {
        JPAQueryFactory query = new JPAQueryFactory(em);
        QGenre genre = QGenre.genre;
        return query
                .from(genre)
                .select(genre)
                .where(genre.genreId.eq(genreId))
                .fetchOne();
    }

    public List<Genre> getGenres(){
        JPAQueryFactory query = new JPAQueryFactory(em);
        QGenre genre = QGenre.genre;
        return query
                .from(genre)
                .select(genre)
                .fetch();
    }

    public void createGenre(Genre genre) {
        em.persist(genre);
    }

}
