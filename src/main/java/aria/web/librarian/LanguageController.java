package aria.web.librarian;

import aria.domain.dao.LanguageDao;
import aria.domain.ejb.Language;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.FilterMeta;
import org.primefaces.util.LangUtils;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


@ManagedBean(name = "languageController", eager = true)
@SessionScoped
public class LanguageController implements Serializable{

    @Inject
    LanguageDao languageDao;

    @Getter
    @Setter
    private List<Language> languages;
    @Getter
    @Setter
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

    public void addLanguage() throws IOException {
        Language language = new Language();
        language.setLanguageName(newLanguage);
        if(languageDao.getForLanguageName(newLanguage) == null) {
            languageDao.createLanguage(language);

            String detail = "Language " + newLanguage + " has been added.";
            FacesContext context = FacesContext.getCurrentInstance();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Language", detail));

            context.getExternalContext().getFlash().setKeepMessages(true);

            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI());
        } else{
            String detail = "Language " + newLanguage  + " is already in the database";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Language", detail));
        }
    }

    public LanguageController() {
    }

    public List<Language> getLanguages(){
        return new ArrayList<>(languageDao.getLanguages());
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
