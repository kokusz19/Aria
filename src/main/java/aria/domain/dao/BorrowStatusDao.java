package aria.domain.dao;

import aria.domain.ejb.*;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;

@Stateful
public class BorrowStatusDao implements Serializable {

    @Inject
    private EntityManager em;

    public BorrowStatusDao() {
    }

    public BorrowStatus getBorrowStatus (final long borrowStatusId) {
        JPAQueryFactory query = new JPAQueryFactory(em);
        QBorrowStatus borrowStatus = QBorrowStatus.borrowStatus;
        return query
                .from(borrowStatus)
                .select(borrowStatus)
                .where(borrowStatus.borrowStatusId.eq(borrowStatusId))
                .fetchOne();
    }

    public List<BorrowStatus> getBorrowStatuses(){
        JPAQueryFactory query = new JPAQueryFactory(em);
        QBorrowStatus borrowStatus = QBorrowStatus.borrowStatus;
        return query
                .from(borrowStatus)
                .select(borrowStatus)
                .fetch();
    }
}
