package aria.domain.dao;

import aria.domain.ejb.*;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

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
    public List<BorrowedBook> getForAccountId(final long accountId) {
        JPAQueryFactory query = new JPAQueryFactory(em);
        QBorrowedBook borrowedBook = QBorrowedBook.borrowedBook;
        return query
                .from(borrowedBook)
                .select(borrowedBook)
                .where(borrowedBook.account.accountId.eq(accountId))
                .fetch();
    }
    public List<BorrowedBook> getReturnedForAccountId(final long accountId) {
        JPAQueryFactory query = new JPAQueryFactory(em);
        QBorrowedBook borrowedBook = QBorrowedBook.borrowedBook;
        return query
                .from(borrowedBook)
                .select(borrowedBook)
                .where(borrowedBook.account.accountId.eq(accountId))
                .where(borrowedBook.dateOfReturn.isNotNull())
                .fetch();
    }
    public List<BorrowedBook> getNotReturnedYetForAccountId(final long accountId) {
        JPAQueryFactory query = new JPAQueryFactory(em);
        QBorrowedBook borrowedBook = QBorrowedBook.borrowedBook;
        return query
                .from(borrowedBook)
                .select(borrowedBook)
                .where(borrowedBook.account.accountId.eq(accountId))
                .where(borrowedBook.dateOfReturn.isNull())
                .fetch();
    }
    public List<BorrowedBook> getBorrowedBooks(){
        JPAQueryFactory query = new JPAQueryFactory(em);
        QBorrowedBook borrowedBook = QBorrowedBook.borrowedBook;
        return query
                .from(borrowedBook)
                .select(borrowedBook)
                .fetch();
    }

    public void createBorrowedBook(BorrowedBook borrowedBook) {
        em.persist(borrowedBook);
    }

    public void updateBorrowedBook(BorrowedBook borrowedBook) {
        em.merge(borrowedBook);
    }
}
