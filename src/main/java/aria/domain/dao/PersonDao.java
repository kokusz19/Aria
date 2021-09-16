package aria.domain.dao;

import com.querydsl.jpa.impl.JPAQueryFactory;
import aria.domain.ejb.Person;
import aria.domain.ejb.QPerson;

import javax.ejb.Stateful;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.Serializable;

@Stateful
@Alternative
public class PersonDao implements Serializable {

    @Inject
    private EntityManager entityManager;

    public PersonDao() {
    }

    public Person getPerson(long personId){
        JPAQueryFactory query = new JPAQueryFactory(entityManager);
        QPerson person = QPerson.person;
        return query
                .from(person)
                .select(person)
                .where(person.personId.eq(personId))
                .fetchOne();
    }

    public void createPerson(Person person) {
        entityManager.persist(person);
    }

}
