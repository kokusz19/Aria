package aria.domain.dao;

import com.querydsl.jpa.impl.JPAQueryFactory;
import aria.domain.ejb.Language;
import aria.domain.ejb.QLanguage;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;

@Stateful
public class LanguageDao implements Serializable {

    @Inject
    private EntityManager em;

    public LanguageDao() {
    }

    public Language getForLanguageName(final String languageName) {
        JPAQueryFactory query = new JPAQueryFactory(em);
        QLanguage language = QLanguage.language;
        return query
                .from(language)
                .select(language)
                .where(language.languageName.eq(languageName))
                .fetchOne();
    }

    public Language getForLanguageId(final long languageId) {
        JPAQueryFactory query = new JPAQueryFactory(em);
        QLanguage language = QLanguage.language;
        return query
                .from(language)
                .select(language)
                .where(language.languageId.eq(languageId))
                .fetchOne();
    }

    public List<Language> getLanguages(){
        JPAQueryFactory query = new JPAQueryFactory(em);
        QLanguage language = QLanguage.language;
        return query
                .from(language)
                .select(language)
                .fetch();
    }

    public void createLanguage(Language language) {
        em.persist(language);
    }

}
