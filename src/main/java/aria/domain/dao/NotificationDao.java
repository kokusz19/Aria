package aria.domain.dao;

import aria.domain.ejb.*;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;

@Stateful
public class NotificationDao implements Serializable {

    @Inject
    private EntityManager em;

    public NotificationDao() {
    }

    public List<Notification> getAll() {
        JPAQueryFactory query = new JPAQueryFactory(em);
        QNotification notification = QNotification.notification;
        return query
                .from(notification)
                .select(notification)
                .fetch();
    }
    public List<Notification> getForAccountId(final long accountId) {
        JPAQueryFactory query = new JPAQueryFactory(em);
        QNotification notification = QNotification.notification;
        return query
                .from(notification)
                .select(notification)
                .where(notification.account.accountId.eq(accountId))
                .fetch();
    }
    public Notification getForNotificationId(final long notificationId) {
        JPAQueryFactory query = new JPAQueryFactory(em);
        QNotification notification = QNotification.notification;
        return query
                .from(notification)
                .select(notification)
                .where(notification.notificationId.eq(notificationId))
                .fetchOne();
    }
    public Notification getForAccountIdBookId(final long accountId, final long bookId) {
        JPAQueryFactory query = new JPAQueryFactory(em);
        QNotification notification = QNotification.notification;
        return query
                .from(notification)
                .select(notification)
                .where(notification.account.accountId.eq(accountId))
                .where(notification.book.bookId.eq(bookId))
                .fetchOne();
    }

    public void createNotification(Notification notification) {
        em.persist(notification);
    }
    public void removeNotification(Notification notification) {
        em.remove(em.contains(notification) ? notification : em.merge(notification));
    }
}
