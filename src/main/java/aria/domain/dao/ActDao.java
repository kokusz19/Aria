package aria.domain.dao;

import com.querydsl.jpa.impl.JPAQueryFactory;
import aria.domain.ejb.Act;
import aria.domain.ejb.QAct;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.Serializable;

@Stateful
public class ActDao implements Serializable {

    @Inject
    private EntityManager em;

    public ActDao() {
    }

    public Act getAct(final String roleName) {
        JPAQueryFactory query = new JPAQueryFactory(em);
        QAct act = QAct.act;
        return query
                .from(act)
                .select(act)
                .where(act.roleName.eq(roleName))
                .fetchOne();
}

}
