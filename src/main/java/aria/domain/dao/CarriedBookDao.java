package aria.domain.dao;

import aria.domain.ejb.*;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;

@Stateful
public class CarriedBookDao implements Serializable {

    @Inject
    private EntityManager em;

    public CarriedBookDao() {
    }

    public List<CarriedBook> getAllCarriedBooks () {
        JPAQueryFactory query = new JPAQueryFactory(em);
        QCarriedBook carriedBook = QCarriedBook.carriedBook;
        return query
                .from(carriedBook)
                .select(carriedBook)
                .fetch();
    }
    public List<CarriedBook> getCarriersCarriedBooks (final long accountId) {
        JPAQueryFactory query = new JPAQueryFactory(em);
        QCarriedBook carriedBook = QCarriedBook.carriedBook;
        return query
                .from(carriedBook)
                .select(carriedBook)
                .where(carriedBook.carrier.accountId.eq(accountId))
                .fetch();
    }
    public CarriedBook getCarriedBookById (final long carryingId) {
        JPAQueryFactory query = new JPAQueryFactory(em);
        QCarriedBook carriedBook = QCarriedBook.carriedBook;
        return query
                .from(carriedBook)
                .select(carriedBook)
                .where(carriedBook.carryingId.eq(carryingId))
                .fetchOne();
    }

    public void createCarriedBook(CarriedBook carriedBook){em.persist(carriedBook);}
}
