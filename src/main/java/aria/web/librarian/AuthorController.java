package aria.web.librarian;

import aria.domain.dao.AuthorDao;
import aria.domain.ejb.Author;
import aria.web.HelperController;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;


@ManagedBean(name = "authorController", eager = true)
@SessionScoped
public class AuthorController implements Serializable{

    @Inject
    AuthorDao authorDao;
    @Inject
    HelperController helperController;

    private List<Author> authors;
    private List<Author> filteredAuthors;
    @Getter
    private List<FilterMeta> filterBy;

    @Getter
    @Setter
    private String firstName;
    @Getter
    @Setter
    private String lastName;
    @Getter
    @Setter
    private LocalDate dateOfBirth;

    @PostConstruct
    public void init() {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, Object> test = context.getExternalContext().getSessionMap();

        authors = new ArrayList<>(authorDao.getAuthors());
        filterBy = new ArrayList<>();
        filteredAuthors = new ArrayList<>(authors);

    }

    public void addAuthor(){
        Author author = new Author();
        author.setFirstName(firstName);
        author.setLastName(lastName);
        author.setDateOfBirth(dateOfBirth);
        authorDao.createAuthor(author);
        init();

        FacesContext context = FacesContext.getCurrentInstance();
        NavigationHandler navigationHandler = context.getApplication().getNavigationHandler();
        navigationHandler.handleNavigation(context, null, "Author.xhtml?faces-redirect=true&includeViewParams=true");
    }

    public AuthorController() {
    }

    public List<Author> getAuthors(){
        init();
        return new ArrayList<>(authors);
    }

    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (LangUtils.isValueBlank(filterText)) {
            return true;
        }

        Author author = (Author) value;
        boolean check = author.getAuthorId().toString().contains(filterText)
                || author.getFirstName().toLowerCase().concat(' ' + author.getLastName().toLowerCase()).contains(filterText)
                || helperController.localDateTimeConverter(author.getDateOfBirth(), false).contains(filterText);

        return check;
    }

    public void setFilteredAuthors(List<Author> filteredAuthors) { this.filteredAuthors = filteredAuthors; }
    public List<Author> getFilteredAuthors() {
        return filteredAuthors;
    }

}
