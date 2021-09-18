package aria.web.librarian;

import aria.domain.dao.GenreDao;
import aria.domain.ejb.Genre;
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


@ManagedBean(name = "genreController", eager = true)
@SessionScoped
public class GenreController implements Serializable{

    @Inject
    GenreDao genreDao;

    private List<Genre> genres;
    private List<Genre> filteredGenres;
    @Getter
    private List<FilterMeta> filterBy;

    @Getter
    @Setter
    private String newGenre;

    @PostConstruct
    public void init() {
        genres = new ArrayList<>(genreDao.getGenres());
        filterBy = new ArrayList<>();
        filteredGenres = new ArrayList<>(genres);
    }

    public void addGenre(){
        Genre genre = new Genre();
        genre.setGenreName(newGenre);
        genreDao.createGenre(genre);
        init();

        FacesContext context = FacesContext.getCurrentInstance();
        NavigationHandler navigationHandler = context.getApplication().getNavigationHandler();
        navigationHandler.handleNavigation(context, null, "Genre.xhtml?faces-redirect=true&includeViewParams=true");
    }

    public GenreController() {
    }

    public List<Genre> getGenres(){
        init();
        return new ArrayList<>(genres);
    }

    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (LangUtils.isValueBlank(filterText)) {
            return true;
        }

        Genre genre = (Genre) value;
        boolean check = genre.getGenreId().toString().contains(filterText)
                || genre.getGenreName().toLowerCase().contains(filterText);

        return check;
    }

    public void setFilteredGenres(List<Genre> filteredGenres) { this.filteredGenres = filteredGenres; }
    public List<Genre> getFilteredGenres() {
        return filteredGenres;
    }

}
