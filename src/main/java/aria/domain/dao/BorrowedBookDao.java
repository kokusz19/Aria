package aria.domain.dao;

import aria.domain.ejb.*;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.Serializable;

@Stateful
public class BorrowedBookDao implements Serializable {

    @Inject
    private EntityManager em;

    public BorrowedBookDao() {
    }

    public BorrowedBook getForBorrowedBookId(final long borrowedBookId) {
        JPAQueryFactory query = new JPAQueryFactory(em);
        QBorrowedBook borrowedBook = QBorrowedBook.borrowedBook;
        return query
                .from(borrowedBook)
                .select(borrowedBook)
                .where(borrowedBook.borrowedBookId.eq(borrowedBookId))
                .fetchOne();
    }

    public void createBorrowedBook(BorrowedBook borrowedBook) {
        em.persist(borrowedBook);
    }

}