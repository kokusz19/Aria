package aria.web.librarian;

import aria.domain.dao.*;
import aria.domain.ejb.*;
import aria.web.HelperController;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.FilterMeta;
import org.primefaces.util.LangUtils;

import javax.annotation.PostConstruct;
import javax.faces.application.NavigationHandler;
import javax.faces.bean.*;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@ManagedBean(name = "bookController", eager = true)
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

    private List<Book> books;
    private List<Book> filteredBooks;
    @Getter
    private List<FilterMeta> filterBy;

    @Getter
    @Setter
    private String title;
    @Getter
    @Setter
    private String ISBN;
    @Getter
    @Setter
    private LocalDate publishedAt;
    @Getter
    @Setter
    private int maxItem;
    @Getter
    @Setter
    private Language language;
    @Getter
    @Setter
    private List<Language> allLanguages;
    @Getter
    @Setter
    private List<Genre> genres;
    @Getter
    @Setter
    private List<Genre> allGenres;
    @Getter
    @Setter
    private List<Author> authors;
    @Getter
    @Setter
    private List<Author> allAuthors;

    @PostConstruct
    public void init() {
        allGenres = genreDao.getGenres();
        allAuthors = authorDao.getAuthors();
        allLanguages = languageDao.getLanguages();

        books = new ArrayList<>(bookDao.getBooks());

        helperController.generateStrings(books);

        filterBy = new ArrayList<>();
        filteredBooks = new ArrayList<>(books);
    }

    public void addBook(){
        Book book = new Book();
        book.setAvailableItems(maxItem);
        book.setMaxItems(maxItem);
        book.setBookTitle(title);
        book.setIsbn(ISBN);
        book.setPublishedAt(publishedAt);

        book.setGenres(genres);
        book.setAuthors(authors);
        book.setLanguage(language);
        bookDao.createBook(book);

        for (Genre genre: genres) {
            GenreToBook genreToBook = new GenreToBook();
            genreToBook.setBook(bookDao.getForBookId(book.getBookId()));
            genreToBook.setGenre(genre);
            genreToBookDao.createGenreToBook(genreToBook);
        }

        for (Author author: authors) {
            AuthorToBook authorToBook = new AuthorToBook();
            authorToBook.setBook(bookDao.getForBookId(book.getBookId()));
            authorToBook.setAuthor(author);
            authorToBookDao.createAuthorToBook(authorToBook);
        }

        clearSpaces();

        FacesContext context = FacesContext.getCurrentInstance();
        NavigationHandler navigationHandler = context.getApplication().getNavigationHandler();
        navigationHandler.handleNavigation(context, null, "Book.xhtml?faces-redirect=true&includeViewParams=true");
    }

    public BookController() {
    }

    public List<Book> getBooks(){
        List<Book> localBooks = bookDao.getBooks();
        helperController.generateStrings(localBooks);
        return new ArrayList<>(localBooks);
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
                || book.getLanguage().getLanguageName().toLowerCase().trim().contains(filterText)
                || helperController.localDateTimeConverter(book.getPublishedAt(), false).contains(filterText);

        return check;
    }


    public void setFilteredBooks(List<Book> filteredBooks) { this.filteredBooks = filteredBooks; }
    public List<Book> getFilteredBooks() {
        return filteredBooks;
    }

    public void clearSpaces(){
        title = "";
        ISBN = "";
        publishedAt = null;
        maxItem = 0;
        language = null;
        genres = null;
        authors = null;
        books = bookDao.getBooks();
    }
}
