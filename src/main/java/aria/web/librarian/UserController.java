package aria.web.librarian;

import aria.domain.dao.*;
import aria.domain.ejb.Account;
import aria.domain.ejb.Book;
import aria.domain.ejb.BorrowedBook;
import aria.web.HelperController;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import org.primefaces.model.FilterMeta;
import org.primefaces.util.LangUtils;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.*;

@ManagedBean(name = "userController", eager = true)
@ViewScoped
public class UserController implements Serializable {

    @Inject
    BorrowedBookDao borrowedBookDao;
    @Inject
    AccountDao accountDao;
    @Inject
    ActDao actDao;
    @Inject
    GenreToBookDao genreToBookDao;
    @Inject
    AuthorToBookDao authorToBookDao;
    @Inject
    BorrowStatusToBorrowedBookDao borrowStatusToBorrowedBookDao;
    @Inject
    HelperController helperController;

    @Getter
    @Setter
    private List<Account> users;
    @Getter
    @Setter
    private List<Account> filteredUsers;
    @Getter
    private List<FilterMeta> filterBy;
    @Getter
    @Setter
    private Account chosenAccount;
    @Getter
    @Setter
    private List<Book> chosenAccountsBooks;

    @PostConstruct
    public void init(){
        users = accountDao.getAll();
        filteredUsers = new ArrayList<>(users);
        filterBy = new ArrayList<>();
        setBorrowedBooks(users);

        FacesContext context = FacesContext.getCurrentInstance();
        if(context.getExternalContext().getSessionMap().containsKey("chosenAccountId")){
            chosenAccount = users.stream().filter(u->u.getAccountId() == context.getExternalContext().getSessionMap().get("chosenAccountId")).collect(Collectors.toList()).get(0);
            setGenresAuthors();
        }
    }

    public void setBorrowedBooks(List<Account> accounts){
        for (Account account: accounts) {
            account.setBooksNotReturnedYet(borrowedBookDao.getNotReturnedYetForAccountId(account.getAccountId()));
            account.setBooksReturned(borrowedBookDao.getReturnedForAccountId(account.getAccountId()));
        }
    }

    public void showBorrowedBooks(long accountId){
        Map<String,Object> options = new HashMap<String, Object>();
        options.put("modal", true);
        options.put("width", 800);
        options.put("height", 700);
        options.put("contentWidth", "100%");
        options.put("contentHeight", "100%");
        options.put("headerElement", "customheader");

        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getSessionMap().put("chosenAccountId", accountId);

        PrimeFaces.current().dialog().openDynamic("ViewBorrowedBooks.xhtml", options, null);

        init();
    }
    public void setGenresAuthors() {
        chosenAccountsBooks = new ArrayList<>();
        for (BorrowedBook borrowedBook : chosenAccount.getBooksNotReturnedYet()) {
            chosenAccountsBooks.add(borrowedBook.getBook());
            borrowedBook.setCurrentStatus(borrowStatusToBorrowedBookDao.getLatestStatusForBorrowedBookId(borrowedBook.getBorrowedBookId()).getBorrowStatus());
        }
        for (BorrowedBook borrowedBook : chosenAccount.getBooksReturned()){
            chosenAccountsBooks.add(borrowedBook.getBook());
            borrowedBook.setCurrentStatus(borrowStatusToBorrowedBookDao.getLatestStatusForBorrowedBookId(borrowedBook.getBorrowedBookId()).getBorrowStatus());
        }
        for (Book tmpBook: chosenAccountsBooks) {
            tmpBook.setGenres(genreToBookDao.getGenresForBookId(tmpBook.getBookId()));
            tmpBook.setGenresString(tmpBook.getGenres().toString());
            tmpBook.setGenresString(tmpBook.getGenresString().replace("[", ""));
            tmpBook.setGenresString(tmpBook.getGenresString().replace("]", ""));

            tmpBook.setAuthors(authorToBookDao.getAuthorsForBookId(tmpBook.getBookId()));
            tmpBook.setAuthorsString(tmpBook.getAuthors().toString());
            tmpBook.setAuthorsString(tmpBook.getAuthorsString().replace("[", ""));
            tmpBook.setAuthorsString(tmpBook.getAuthorsString().replace("]", ""));
        }
    }

    public void demotePromote(long accountId, int action) throws IOException {
        if (action == 2) {
            Account account = accountDao.getForAccountId(accountId);
            if(account.getAct().getActId() == 1)
                account.setAct(actDao.getAct(4));
            else if(account.getAct().getActId() == 4)
                account.setAct(actDao.getAct(2));
            else
                account.setAct(actDao.getAct(account.getAct().getActId() + 1));
            accountDao.updateUser(account);
        }
        else if(action == 1){
            Account account = accountDao.getForAccountId(accountId);
            if(account.getAct().getActId() == 2)
                account.setAct(actDao.getAct(4));
            else if(account.getAct().getActId() == 4)
                account.setAct(actDao.getAct(1));
            else
                account.setAct(actDao.getAct(account.getAct().getActId() - 1));
            accountDao.updateUser(account);
        }
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI());
    }

    public UserController(){

    }
    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (LangUtils.isValueBlank(filterText)) {
            return true;
        }

        Account account = (Account) value;
        boolean check = account.getAccountId().toString().toLowerCase().contains(filterText)
                || account.getAct().getRoleName().toLowerCase().contains(filterText)
                || account.getLoginName().toLowerCase().contains(filterText)
                || account.getPerson().getFirstName().toLowerCase().contains(filterText)
                || account.getPerson().getLastName().toLowerCase().contains(filterText)
                || account.getPerson().getFirstName().toLowerCase().concat(" ").concat(account.getPerson().getLastName().toLowerCase()).contains(filterText)
                || account.getPerson().getAddress().toLowerCase().contains(filterText)
                || account.getPerson().getEmail().toLowerCase().contains(filterText)
                || account.getPerson().getPhoneNumber().toLowerCase().contains(filterText)
                || helperController.localDateFilterCheck(account.getPerson().getDateOfBirth(), filterText);

        return check;
    }
    public List<Account> getUsers(){
        return new ArrayList<Account>(users);
    }
    public void setFilteredUsers(List<Account> filteredUsers) { this.filteredUsers = filteredUsers; }
    public List<Account> getFilteredUsers() {
        return filteredUsers;
    }
}
