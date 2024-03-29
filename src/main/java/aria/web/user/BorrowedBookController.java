package aria.web.user;

import aria.domain.dao.BookDao;
import aria.domain.dao.BorrowStatusDao;
import aria.domain.dao.BorrowStatusToBorrowedBookDao;
import aria.domain.dao.BorrowedBookDao;
import aria.domain.ejb.Book;
import aria.domain.ejb.BorrowStatus;
import aria.domain.ejb.BorrowStatusToBorrowedBook;
import aria.domain.ejb.BorrowedBook;
import aria.web.HelperController;
import aria.web.librarian.BookController;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.FilterMeta;
import org.primefaces.util.LangUtils;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@ManagedBean(name = "userBorrowedBookController", eager = true)
@ViewScoped
public class BorrowedBookController implements Serializable {

    @Inject
    BorrowedBookDao borrowedBookDao;
    @Inject
    BookController bookController;
    @Inject
    BorrowStatusToBorrowedBookDao borrowStatusToBorrowedBookDao;
    @Inject
    BookDao bookDao;
    @Inject
    BorrowStatusDao borrowStatusDao;
    @Inject
    HelperController helperController;

    @Getter
    @Setter
    private List<BorrowedBook> allBorrowedBook;
    @Getter
    @Setter
    private List<BorrowedBook> filteredBorrowedBooks;
    @Getter
    @Setter
    private List<BorrowStatus> allStatus;
    @Getter
    private List<FilterMeta> filterBy;

    @PostConstruct
    public void init(){
        long userId;
        FacesContext context = FacesContext.getCurrentInstance();
        NavigationHandler navigationHandler = context.getApplication().getNavigationHandler();
        userId = Long.parseLong(context.getExternalContext().getSessionMap().get("id").toString());

        allBorrowedBook = borrowedBookDao.getForAccountId(userId);
        filterBy = new ArrayList<>();
        filteredBorrowedBooks = new ArrayList<>(allBorrowedBook);
        allStatus = borrowStatusDao.getBorrowStatuses();

        List<Book> books = new ArrayList<>();
        for (BorrowedBook borrowedBook: allBorrowedBook) {
            books.add(borrowedBook.getBook());
            helperController.setStringBorrowedBookDates(borrowedBook);
            borrowedBook.setCurrentStatus(borrowStatusToBorrowedBookDao.getLatestStatusForBorrowedBookId(borrowedBook.getBorrowedBookId()).getBorrowStatus());
        }
        helperController.generateStrings(books);
    }

    public void action(long borrowedBookId, int actionId) throws IOException {
        if(actionId == 1){ // Cancelled by user
            BorrowedBook borrowedBook = borrowedBookDao.getForBorrowedBookId(borrowedBookId);
            borrowedBook.setDateOfReturn(LocalDateTime.now());
            borrowedBookDao.updateBorrowedBook(borrowedBook);

            Book book = borrowedBook.getBook();
            helperController.generateStrings(book);
            book.setAvailableItems(book.getAvailableItems()+1);
            bookDao.updateBook(book);

            BorrowStatusToBorrowedBook borrowStatusToBorrowedBook = new BorrowStatusToBorrowedBook();
            borrowStatusToBorrowedBook.setBorrowedBook(borrowedBook);
            borrowStatusToBorrowedBook.setBorrowStatus(borrowStatusDao.getBorrowStatus(7));
            borrowStatusToBorrowedBook.setUpdateDate(LocalDateTime.now());
            borrowStatusToBorrowedBookDao.createBorrowStatusToBorrowedBook(borrowStatusToBorrowedBook);

            String detail = "Borrowing of " + book.getAuthorsString() + " - " + book.getBookTitle() + " has been cancelled.";
            FacesContext context = FacesContext.getCurrentInstance();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Cancelled", detail));
            context.getExternalContext().getFlash().setKeepMessages(true);
        }
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI());
    }

    public BorrowedBookController(){

    }
    public List<BorrowedBook> getBorrowedBooks(){
        List<BorrowedBook> localBorrowedBooks = borrowedBookDao.getBorrowedBooks();
        return new ArrayList<>(localBorrowedBooks);
    }

    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (LangUtils.isValueBlank(filterText)) {
            return true;
        }

        BorrowedBook borrowedBook = (BorrowedBook) value;
        boolean check = borrowedBook.getAccount().getLoginName().toLowerCase().contains(filterText)
                || borrowedBook.getAccount().getPerson().getFirstName().toLowerCase().contains(filterText)
                || borrowedBook.getAccount().getPerson().getLastName().toLowerCase().contains(filterText)
                || borrowedBook.getBook().getAuthors().toString().toLowerCase().contains(filterText)
                || borrowedBook.getBook().getBookTitle().toLowerCase().contains(filterText)
                || borrowedBook.getBook().getGenres().toString().toLowerCase().contains(filterText)
                || borrowedBook.getBook().getIsbn().toString().toLowerCase().contains(filterText)
                || borrowedBook.getBook().getLanguage().getLanguageName().toLowerCase().contains(filterText)
                || borrowedBook.getCurrentStatus().getBorrowStatusName().toLowerCase().contains(filterText)
                || helperController.localDateTimeConverter(borrowedBook.getDateOfBorrow(), false).contains(filterText)
                || helperController.localDateTimeConverter(borrowedBook.getDateOfReturn(), false).contains(filterText)
                || helperController.localDateTimeConverter(borrowedBook.getDateToBeReturned(), false).contains(filterText);

        return check;
    }

    public void setFilteredBooks(List<BorrowedBook> filteredBorrowedBooks) { this.filteredBorrowedBooks = filteredBorrowedBooks; }
    public List<BorrowedBook> getFilteredBorrowedBooks() {
        return filteredBorrowedBooks;
    }
}
