package aria.web.user;

import aria.domain.dao.*;
import aria.domain.ejb.*;
import aria.web.HelperController;
import lombok.Setter;
import lombok.Getter;
import org.primefaces.model.FilterMeta;
import org.primefaces.util.LangUtils;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@ManagedBean(name = "userBookController", eager = true)
@ViewScoped
public class BookController implements Serializable{

    @Inject
    BookDao bookDao ;
    @Inject
    GenreDao genreDao;
    @Inject
    AuthorDao authorDao;
    @Inject
    LanguageDao languageDao;
    @Inject
    GenreToBookDao genreToBookDao;
    @Inject
    AuthorToBookDao authorToBookDao;
    @Inject
    HelperController helperController;

    @Getter
    @Setter
    private List<Book> books;
    @Getter
    @Setter
    private List<Book> filteredBooks;
    @Getter
    private List<FilterMeta> filterBy;

    @Getter
    @Setter
    private List<Genre> allGenres;
    @Getter
    @Setter
    private List<Author> allAuthors;
    @Getter
    @Setter
    private List<Language> allLanguages;

    @PostConstruct
    public void init() {
        allGenres = genreDao.getGenres();
        allAuthors = authorDao.getAuthors();
        allLanguages = languageDao.getLanguages();

        books = bookDao.getBooks();
        for (Book book: books) {
            book.setGenres(genreToBookDao.getGenresForBookId(book.getBookId()));
            book.setGenresString(book.getGenres().toString());
            book.setGenresString(book.getGenresString().replace("[", ""));
            book.setGenresString(book.getGenresString().replace("]", ""));

            book.setAuthors(authorToBookDao.getAuthorsForBookId(book.getBookId()));
            book.setAuthorsString(book.getAuthors().toString());
            book.setAuthorsString(book.getAuthorsString().replace("[", ""));
            book.setAuthorsString(book.getAuthorsString().replace("]", ""));
        }
        filterBy = new ArrayList<>();
        filteredBooks = new ArrayList<>(books);
    }

    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (LangUtils.isValueBlank(filterText)) {
            return true;
        }

        Book book = (Book) value;
        boolean check = book.getBookTitle().toLowerCase().trim().contains(filterText)
                || book.getAuthors().toString().trim().toLowerCase().contains(filterText)
                || book.getBookId().toString().toLowerCase().trim().contains(filterText)
                || book.getGenres().toString().trim().toLowerCase().contains(filterText)
                || book.getIsbn().toString().toLowerCase().trim().contains(filterText)
                || book.getLanguage().getLanguageName().toLowerCase().trim().contains(filterText)
                || helperController.localDateTimeConverter(book.getPublishedAt(), false).contains(filterText);
        return check;
    }

    public BookController() {
    }
}