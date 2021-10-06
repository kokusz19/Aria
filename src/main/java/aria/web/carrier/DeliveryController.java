package aria.web.carrier;

import aria.domain.dao.*;
import aria.domain.ejb.*;
import aria.web.librarian.BookController;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import org.primefaces.model.FilterMeta;
import org.primefaces.util.LangUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
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
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean(name = "deliveryController", eager = true)
@ViewScoped
public class DeliveryController implements Serializable{
    @Inject
    BorrowedBookDao borrowedBookDao;
    @Inject
    BorrowStatusToBorrowedBookDao borrowStatusToBorrowedBookDao;
    @Inject
    BorrowStatusDao borrowStatusDao;
    @Inject
    CarriedBookDao carriedBookDao;
    @Inject
    AccountDao accountDao;
    @Inject
    BookController bookController;

    @Getter
    @Setter
    private List<BorrowedBook> allBorrowedBook;
    @Getter
    @Setter
    private List<BorrowedBook> filteredAllBorrowedBooks;
    @Getter
    private List<FilterMeta> filterBy;
    @Getter
    @Setter
    private List<BorrowStatus> allStatuses;
    @Getter
    @Setter
    private List<CarriedBook> carriedBooks;
    @Getter
    @Setter
    private Map<Long, Account> carriers;

    @Getter
    @Setter
    private long id;
    @Getter
    @Setter
    private String roleName;

    @PostConstruct
    public void init(){
        id = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id").toString());
        roleName = accountDao.getForAccountId(id).getAct().getRoleName();

        List<Book> books = new ArrayList<>();
        allBorrowedBook = borrowedBookDao.getBorrowedBooks();
        for (BorrowedBook borrowedBook: allBorrowedBook) {
            borrowedBook.setCurrentStatus(borrowStatusToBorrowedBookDao.getLatestStatusForBorrowedBookId(borrowedBook.getBorrowedBookId()).getBorrowStatus());
            books.add(borrowedBook.getBook());
        }
        bookController.generateStrings(books);
        allBorrowedBook = allBorrowedBook.stream().filter(b -> b.getCurrentStatus().getBorrowStatusId() == 4 || b.getCurrentStatus().getBorrowStatusId() == 5 || b.getCurrentStatus().getBorrowStatusId() == 6 || b.getCurrentStatus().getBorrowStatusId() == 10).collect(Collectors.toList());
        filteredAllBorrowedBooks = new ArrayList<>(allBorrowedBook);

        carriedBooks = carriedBookDao.getAllCarriedBooks();
        carriers = new TreeMap<Long, Account>();
        for (CarriedBook tmpCarried: carriedBooks) {
            carriers.put(tmpCarried.getBorrowedBook().getBorrowedBookId(), tmpCarried.getCarrier());
        }

        allStatuses = borrowStatusDao.getBorrowStatuses();
        allStatuses = allStatuses.stream().filter((s -> s.getBorrowStatusId() == 4 || s.getBorrowStatusId() == 5 || s.getBorrowStatusId() == 6 || s.getBorrowStatusId() == 10)).collect(Collectors.toList());
    }

    public void action(long borrowedBookId, int actionId) throws IOException {
        BorrowedBook borrowedBook = borrowedBookDao.getForBorrowedBookId(borrowedBookId);

        BorrowStatusToBorrowedBook tmp = new BorrowStatusToBorrowedBook();
        tmp.setUpdateDate(LocalDate.now());
        tmp.setBorrowedBook(borrowedBook);

        if(actionId == 1){
            // Delivering
            tmp.setBorrowStatus(borrowStatusDao.getBorrowStatus(5));
            CarriedBook tmpCarriedBook = new CarriedBook();
            tmpCarriedBook.setBorrowedBook(borrowedBook);
            tmpCarriedBook.setCarrier(accountDao.getForAccountId(id));
            carriedBookDao.createCarriedBook(tmpCarriedBook);
        } else if(actionId == 2){
            // Delivered
            tmp.setBorrowStatus(borrowStatusDao.getBorrowStatus(6));
            borrowedBook.setDateToBeReturned(LocalDate.now().plusWeeks(2));
            borrowedBookDao.updateBorrowedBook(borrowedBook);
        } else if(actionId == 3){
            // Could not be delivered
            tmp.setBorrowStatus(borrowStatusDao.getBorrowStatus(10));
        }
        borrowStatusToBorrowedBookDao.createBorrowStatusToBorrowedBook(tmp);

        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI());
    }

    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (LangUtils.isValueBlank(filterText)) {
            return true;
        }

        BorrowedBook borrowedBook = (BorrowedBook) value;
        boolean check = borrowedBook.getAccount().getPerson().getFirstName().toLowerCase().contains(filterText)
                || borrowedBook.getAccount().getPerson().getLastName().toLowerCase().contains(filterText)
                || borrowedBook.getAccount().getPerson().getEmail().toLowerCase().contains(filterText)
                || borrowedBook.getAccount().getPerson().getAddress().toLowerCase().contains(filterText)
                || borrowedBook.getAccount().getPerson().getPhoneNumber().toLowerCase().contains(filterText)
                || borrowedBook.getBook().getAuthors().toString().toLowerCase().contains(filterText)
                || borrowedBook.getBook().getBookTitle().toLowerCase().contains(filterText)
                || borrowedBook.getBook().getGenres().toString().toLowerCase().contains(filterText)
                || borrowedBook.getBook().getIsbn().toString().toLowerCase().contains(filterText)
                || borrowedBook.getBook().getLanguage().getLanguageName().toLowerCase().contains(filterText)
                || borrowedBook.getBook().getBookId().toString().toLowerCase().contains(filterText)
                || borrowedBook.getCurrentStatus().getBorrowStatusName().toLowerCase().contains(filterText);

        if(carriers.containsKey(borrowedBook.getBorrowedBookId())){
            check = check || carriers.get(borrowedBook.getBorrowedBookId()).getPerson().getFirstName().toLowerCase().contains(filterText)
                    || carriers.get(borrowedBook.getBorrowedBookId()).getPerson().getLastName().toLowerCase().contains(filterText)
                    || carriers.get(borrowedBook.getBorrowedBookId()).getPerson().getFirstName().concat(" ").concat(carriers.get(borrowedBook.getBorrowedBookId()).getPerson().getLastName()).toLowerCase().contains(filterText);
        }

        return check;
    }

    public void setFilteredAllBorrowedBooks(List<BorrowedBook> filteredAllBorrowedBooks) { this.filteredAllBorrowedBooks = filteredAllBorrowedBooks; }
    public List<BorrowedBook> getFilteredAllBorrowedBooks() {
        return filteredAllBorrowedBooks;
    }
}
