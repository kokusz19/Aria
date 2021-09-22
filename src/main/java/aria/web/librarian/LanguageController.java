package aria.web.librarian;

import aria.domain.dao.LanguageDao;
import aria.domain.ejb.Language;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.FilterMeta;
import org.primefaces.util.LangUtils;

import javax.annotation.PostConstruct;
import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


@ManagedBean(name = "languageController", eager = true)
@SessionScoped
public class LanguageController implements Serializable{

    @Inject
    LanguageDao languageDao;

    private List<Language> languages;
    private List<Language> filteredLanguages;
    @Getter
    private List<FilterMeta> filterBy;

    @Getter
    @Setter
    private String newLanguage;

    @PostConstruct
    public void init() {
        languages = new ArrayList<>(languageDao.getLanguages());
        filterBy = new ArrayList<>();
        filteredLanguages = new ArrayList<>(languages);
    }

    public void addLanguage(){
        Language language = new Language();
        language.setLanguageName(newLanguage);
        languageDao.createLanguage(language);
        init();

        FacesContext context = FacesContext.getCurrentInstance();
        NavigationHandler navigationHandler = context.getApplication().getNavigationHandler();
        navigationHandler.handleNavigation(context, null, "Language.xhtml?faces-redirect=true&includeViewParams=true");
    }

    public LanguageController() {
    }

    public List<Language> getLanguages(){
        init();
        return new ArrayList<>(languages);
    }

    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (LangUtils.isValueBlank(filterText)) {
            return true;
        }

        Language language = (Language) value;
        boolean check = language.getLanguageId().toString().contains(filterText)
                || language.getLanguageName().toLowerCase().contains(filterText);

        return check;
    }

    public void setFilteredLanguages(List<Language> filteredLanguages) { this.filteredLanguages = filteredLanguages; }
    public List<Language> getFilteredLanguages() {
        return filteredLanguages;
    }

}
