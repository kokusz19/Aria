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
import java.time.LocalDateTime;
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
            helperController.setStringBorrowedBookDates(borrowedBook);
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
        helperController.generateStrings(books);
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
                || helperController.localDateTimeConverter(borrowedBook.getDateOfBorrow(), false).contains(filterText)
                || helperController.localDateTimeConverter(borrowedBook.getDateOfReturn(), false).contains(filterText)
                || helperController.localDateTimeConverter(borrowedBook.getDateToBeReturned(), false).contains(filterText);

        return check;
    }

    public void setFilteredBorrowedBooks(List<BorrowedBook> filteredBorrowedBooks) { this.filteredBorrowedBooks = filteredBorrowedBooks; }
    public List<BorrowedBook> getFilteredBorrowedBooks() {
        return filteredBorrowedBooks;
    }

    public void action(long borrowedBookId, int actionId){
        BorrowedBook borrowedBook = borrowedBookDao.getForBorrowedBookId(borrowedBookId);
        helperController.generateStrings(borrowedBook.getBook());

        BorrowStatusToBorrowedBook tmp = new BorrowStatusToBorrowedBook();
        tmp.setUpdateDate(LocalDateTime.now());
        tmp.setBorrowedBook(borrowedBook);

        String detail = "";
        if(actionId == 1){
            // Hand over to user
            borrowedBook.setDateToBeReturned(LocalDateTime.now().plusWeeks(2));
            borrowedBookDao.updateBorrowedBook(borrowedBook);
            tmp.setBorrowStatus(borrowStatusDao.getBorrowStatus(2));

            detail = borrowedBook.getBook().getAuthorsString() + "  " + borrowedBook.getBook().getBookTitle() + " has been handed over to " + borrowedBook.getAccount().getPerson().getFirstName() + " " + borrowedBook.getAccount().getPerson().getLastName() + ".";
            FacesContext context = FacesContext.getCurrentInstance();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Handover", detail));
        } else if(actionId == 2){
            // Remove booking
            borrowedBook.setDateToBeReturned(null);
            borrowedBook.setDateOfReturn(null);
            borrowedBookDao.updateBorrowedBook(borrowedBook);

            Book book = borrowedBook.getBook();
            book.setAvailableItems(book.getAvailableItems()+1);
            bookDao.updateBook(book);
            tmp.setBorrowStatus(borrowStatusDao.getBorrowStatus(8));

            detail = borrowedBook.getAccount().getPerson().getFirstName() + " " + borrowedBook.getAccount().getPerson().getLastName() + "'s booking on " + borrowedBook.getBook().getAuthorsString() + " - " + borrowedBook.getBook().getBookTitle() + " has been removed.";
            FacesContext context = FacesContext.getCurrentInstance();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Removed", detail));
        } else if(actionId == 3){
            // Hand over to carrier
            tmp.setBorrowStatus(borrowStatusDao.getBorrowStatus(4));

            detail = borrowedBook.getBook().getAuthorsString() + " - " + borrowedBook.getBook().getBookTitle() + " has been packed for the carrier.";
            FacesContext context = FacesContext.getCurrentInstance();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Packed", detail));
        } else if(actionId == 4){
            // Book returned
            borrowedBook.setDateOfReturn(LocalDateTime.now());
            borrowedBookDao.updateBorrowedBook(borrowedBook);
            tmp.setBorrowStatus(borrowStatusDao.getBorrowStatus(9));

            Book book = borrowedBook.getBook();
            book.setAvailableItems(book.getAvailableItems()+1);
            bookDao.updateBook(book);

            detail = borrowedBook.getBook().getAuthorsString() + " - " + borrowedBook.getBook().getBookTitle() + " has been returned by " + borrowedBook.getAccount().getPerson().getFirstName() + " " + borrowedBook.getAccount().getPerson().getLastName()+".";
            FacesContext context = FacesContext.getCurrentInstance();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Packed", detail));
        }
        borrowStatusToBorrowedBookDao.createBorrowStatusToBorrowedBook(tmp);


        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        NavigationHandler navigationHandler = context.getApplication().getNavigationHandler();
        navigationHandler
                .handleNavigation(context, null, "BorrowedBook.xhtml?faces-redirect=true&includeViewParams=true");
    }
}
