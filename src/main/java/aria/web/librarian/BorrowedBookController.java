package aria.web.librarian;

import aria.domain.dao.*;
import aria.domain.ejb.*;
import aria.web.HelperController;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import org.primefaces.model.FilterMeta;
import org.primefaces.util.LangUtils;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean(name = "borrowedBookController", eager = true)
@ViewScoped
public class BorrowedBookController implements Serializable {

    @Inject
    BorrowedBookDao borrowedBookDao;
    @Inject
    BookController bookController;
    @Inject
    BookDao bookDao;
    @Inject
    BorrowStatusToBorrowedBookDao borrowStatusToBorrowedBookDao;
    @Inject
    BorrowStatusDao borrowStatusDao;
    @Inject
    ActDao actDao;
    @Inject
    HelperController helperController;

    @Getter
    @Setter
    private List<BorrowedBook> allBorrowedBook;
    @Getter
    @Setter
    private List<List<BorrowedBook>> statuses;
    @Getter
    @Setter
    private List<BorrowStatus> allStatuses;
    @Getter
    @Setter
    private List<Act> allRoles;
    @Getter
    @Setter
    private List<BorrowedBook> filteredBorrowedBooks;
    @Getter
    private List<FilterMeta> filterBy;

    @PostConstruct
    public void init(){
        allBorrowedBook = borrowedBookDao.getBorrowedBooks();
        for (BorrowedBook borrowedBook: allBorrowedBook) {
            borrowedBook.setCurrentStatus(borrowStatusToBorrowedBookDao.getLatestStatusForBorrowedBookId(borrowedBook.getBorrowedBookId()).getBorrowStatus());
        }

        statuses = new ArrayList<>();
        for(int i = 1; i <= borrowStatusDao.getBorrowStatuses().size(); i++){
            int finalI = i;
            statuses.add(i-1, allBorrowedBook.stream().filter(b -> b.getCurrentStatus().getBorrowStatusId() == finalI).collect(Collectors.toList()));
        }

        allStatuses = new ArrayList<>(borrowStatusDao.getBorrowStatuses());
        allRoles = new ArrayList<>(actDao.getAllActs());

        filterBy = new ArrayList<>();

        filteredBorrowedBooks = new ArrayList<>(allBorrowedBook);

        List<Book> books = new ArrayList<>();
        for (BorrowedBook borrowedBook: allBorrowedBook) {
            books.add(borrowedBook.getBook());
        }
        bookController.generateStrings(books);
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
                || borrowedBook.getAccount().getPerson().getFirstName().toLowerCase().concat(" ").concat(borrowedBook.getAccount().getPerson().getLastName().toLowerCase()).contains(filterText)
                || borrowedBook.getAccount().getPerson().getEmail().toLowerCase().contains(filterText)
                || borrowedBook.getAccount().getPerson().getPhoneNumber().toLowerCase().contains(filterText)
                || borrowedBook.getAccount().getPerson().getAddress().toLowerCase().contains(filterText)
                || borrowedBook.getBook().getAuthors().toString().toLowerCase().contains(filterText)
                || borrowedBook.getBook().getBookTitle().toLowerCase().contains(filterText)
                || borrowedBook.getBook().getGenres().toString().toLowerCase().contains(filterText)
                || borrowedBook.getBook().getIsbn().toString().toLowerCase().contains(filterText)
                || borrowedBook.getBook().getLanguage().getLanguageName().toLowerCase().contains(filterText)
                || borrowedBook.getBook().getBookId().toString().toLowerCase().contains(filterText)
                || borrowedBook.getCurrentStatus().getBorrowStatusName().toLowerCase().contains(filterText)
                || helperController.localDateFilterCheck(borrowedBook.getDateOfBorrow(), filterText)
                || helperController.localDateFilterCheck(borrowedBook.getDateOfReturn(), filterText)
                || helperController.localDateFilterCheck(borrowedBook.getDateToBeReturned(), filterText);

        return check;
    }

    public void setFilteredBorrowedBooks(List<BorrowedBook> filteredBorrowedBooks) { this.filteredBorrowedBooks = filteredBorrowedBooks; }
    public List<BorrowedBook> getFilteredBorrowedBooks() {
        return filteredBorrowedBooks;
    }

    public void action(long borrowedBookId, int actionId){
        BorrowedBook borrowedBook = borrowedBookDao.getForBorrowedBookId(borrowedBookId);

        BorrowStatusToBorrowedBook tmp = new BorrowStatusToBorrowedBook();
        tmp.setUpdateDate(LocalDate.now());
        tmp.setBorrowedBook(borrowedBook);

        if(actionId == 1){
            // Hand over to user
            borrowedBook.setDateToBeReturned(LocalDate.now().plusWeeks(2));
            borrowedBookDao.updateBorrowedBook(borrowedBook);
            tmp.setBorrowStatus(borrowStatusDao.getBorrowStatus(2));
        } else if(actionId == 2){
            // Remove booking
            borrowedBook.setDateToBeReturned(null);
            borrowedBook.setDateOfReturn(null);
            borrowedBookDao.updateBorrowedBook(borrowedBook);

            Book book = borrowedBook.getBook();
            book.setAvailableItems(book.getAvailableItems()+1);
            bookDao.updateBook(book);
            tmp.setBorrowStatus(borrowStatusDao.getBorrowStatus(8));
        } else if(actionId == 3){
            // Hand over to carrier
            tmp.setBorrowStatus(borrowStatusDao.getBorrowStatus(4));
        } else if(actionId == 4){
            // Book returned
            borrowedBook.setDateOfReturn(LocalDate.now());
            borrowedBookDao.updateBorrowedBook(borrowedBook);
            tmp.setBorrowStatus(borrowStatusDao.getBorrowStatus(9));

            Book book = borrowedBook.getBook();
            book.setAvailableItems(book.getAvailableItems()+1);
            bookDao.updateBook(book);
        }
        borrowStatusToBorrowedBookDao.createBorrowStatusToBorrowedBook(tmp);
        FacesContext context = FacesContext.getCurrentInstance();
        NavigationHandler navigationHandler = context.getApplication().getNavigationHandler();
        navigationHandler
                .handleNavigation(context, null, "BorrowedBook.xhtml?faces-redirect=true&includeViewParams=true");
    }
}
