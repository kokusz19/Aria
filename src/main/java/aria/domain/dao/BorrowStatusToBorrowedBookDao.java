package aria.domain.dao;

import aria.domain.ejb.*;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;

@Stateful
public class BorrowStatusToBorrowedBookDao implements Serializable {

    @Inject
    private EntityManager em;

    public BorrowStatusToBorrowedBookDao() {
    }

    public List<BorrowStatusToBorrowedBook> getStatusesForBorrowedBookId (final long borrowedBookId) {
        JPAQueryFactory query = new JPAQueryFactory(em);
        QBorrowStatusToBorrowedBook borrowStatusToBorrowedBook = QBorrowStatusToBorrowedBook.borrowStatusToBorrowedBook;
        return query
                .from(borrowStatusToBorrowedBook)
                .select(borrowStatusToBorrowedBook)
                .where(borrowStatusToBorrowedBook.borrowedBook.borrowedBookId.eq(borrowedBookId))
                .fetch();
    }

    public void createBorrowStatusToBorrowedBook(BorrowStatusToBorrowedBook borrowStatusToBorrowedBook){em.persist(borrowStatusToBorrowedBook);}
}
