package aria.web.user;

import aria.domain.dao.*;
import aria.domain.ejb.Book;
import aria.domain.ejb.BorrowedBook;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Map;

@ManagedBean(name = "borrowBookController", eager = true)
@ViewScoped
public class BorrowBookController implements Serializable{

    @Inject
    BookDao bookDao;
    @Inject
    GenreToBookDao genreToBookDao;
    @Inject
    AuthorToBookDao authorToBookDao;
    @Inject
    BorrowedBookDao borrowedBookDao;
    @Inject
    AccountDao accountDao;

    @Getter
    private Long bookId;
    @Getter
    @Setter
    private Book book;
    @Getter
    @Setter
    private boolean exist = false;

    @PostConstruct
    public void init() {
        Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        bookId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("bookId"));
        book = bookDao.getForBookId(bookId);

        if(book.getAvailableItems() > 0)
            exist = true;
        book.setGenres(genreToBookDao.getGenresForBookId(bookId));
        book.setGenresString(book.getGenres().toString());
        book.setGenresString(book.getGenresString().replace("[", ""));
        book.setGenresString(book.getGenresString().replace("]", ""));

        book.setAuthors(authorToBookDao.getAuthorsForBookId(bookId));
        book.setAuthorsString(book.getAuthors().toString());
        book.setAuthorsString(book.getAuthorsString().replace("[", ""));
        book.setAuthorsString(book.getAuthorsString().replace("]", ""));
    }

    public BorrowBookController() {
    }

    public void borrowBook(){
        if(exist) {
            FacesContext context = FacesContext.getCurrentInstance();
            NavigationHandler navigationHandler = context.getApplication().getNavigationHandler();
            long accountId = Long.parseLong(context.getExternalContext().getSessionMap().get("id").toString());

            BorrowedBook borrowedBook = new BorrowedBook();
            borrowedBook.setAccount(accountDao.getForAccountId(accountId));
            borrowedBook.setBook(book);
            borrowedBook.setDateOfBorrow(LocalDate.now());
            borrowedBookDao.createBorrowedBook(borrowedBook);

            book.setAvailableItems(book.getAvailableItems()-1);
            bookDao.updateBook(book);

            navigationHandler.handleNavigation(context, null, "Book.xhtml?faces-redirect=true&includeViewParams=true");
        }
    }
}