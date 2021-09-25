package aria.web.librarian;

import aria.domain.dao.AuthorToBookDao;
import aria.domain.dao.BookDao;
import aria.domain.dao.GenreDao;
import aria.domain.dao.GenreToBookDao;
import aria.domain.ejb.Author;
import aria.domain.ejb.Book;
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


@ManagedBean(name = "bookController", eager = true)
@SessionScoped
public class BookController implements Serializable{

    @Inject
    BookDao bookDao ;
    @Inject
    GenreToBookDao genreToBookDao;
    @Inject
    AuthorToBookDao authorToBookDao;

    private List<Book> books;
    private List<Book> filteredBooks;
    @Getter
    private List<FilterMeta> filterBy;

    @Getter
    @Setter
    private List<Genre> genres;
    @Getter
    @Setter
    private List<Author> authors;

    @PostConstruct
    public void init() {
        books = new ArrayList<>(bookDao.getBooks());
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

    public void addBook(){
        Book book = new Book();
        book.setGenres(genres);
        book.setAuthors(authors);
        bookDao.createBook(book);
        init();

        FacesContext context = FacesContext.getCurrentInstance();
        NavigationHandler navigationHandler = context.getApplication().getNavigationHandler();
        navigationHandler.handleNavigation(context, null, "Book.xhtml?faces-redirect=true&includeViewParams=true");
    }

    public BookController() {
    }

    public List<Book> getBooks(){
        init();
        return new ArrayList<>(books);
    }

    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (LangUtils.isValueBlank(filterText)) {
            return true;
        }

        Book book = (Book) value;
        boolean check = book.getAuthors().toString().trim().toLowerCase().contains(filterText)
                || book.getBookId().toString().toLowerCase().trim().contains(filterText)
                || book.getBookTitle().toLowerCase().trim().contains(filterText)
                || book.getGenres().toString().trim().toLowerCase().contains(filterText)
                || book.getIsbn().toString().toLowerCase().trim().contains(filterText)
                || book.getLanguage().getLanguageName().toLowerCase().trim().contains(filterText);

        return check;
    }

    public void setFilteredBooks(List<Book> filteredBooks) { this.filteredBooks = filteredBooks; }
    public List<Book> getFilteredBooks() {
        return filteredBooks;
    }

}
