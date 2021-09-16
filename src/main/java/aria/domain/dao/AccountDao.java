package aria.domain.dao;

import com.querydsl.jpa.impl.JPAQueryFactory;
import aria.domain.ejb.Account;
import aria.domain.ejb.QAccount;

import javax.ejb.Stateful;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.Serializable;

@Stateful
@Alternative
public class AccountDao implements Serializable {

    @Inject
    private EntityManager entityManager;

    public AccountDao() {
    }

    public Account getForUsername(String username) {
        JPAQueryFactory query = new JPAQueryFactory(entityManager);
        QAccount account = QAccount.account;
        return query
                .from(account)
                .select(account)
                .where(account.loginName.eq(username))
                .fetchOne();
    }

    public Account getForAccountId(long accountId) {
        JPAQueryFactory query = new JPAQueryFactory(entityManager);
        QAccount account = QAccount.account;
        return query
                .from(account)
                .select(account)
                .where(account.accountId.eq(accountId))
                .fetchOne();
    }

    public void createUser(Account account) {
        entityManager.persist(account);
    }

}
