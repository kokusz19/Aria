package aria.web.librarian;

import aria.domain.dao.AccountDao;
import aria.domain.dao.BorrowedBookDao;
import aria.domain.ejb.Account;
import aria.domain.ejb.BorrowedBook;
import aria.domain.ejb.Genre;
import lombok.Getter;
import lombok.Setter;
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

@ManagedBean(name = "userController", eager = true)
@ViewScoped
public class UserController implements Serializable {

    @Inject
    BorrowedBookDao borrowedBookDao;
    @Inject
    AccountDao accountDao;

    @Getter
    @Setter
    private List<Account> defaults;
    @Getter
    @Setter
    private List<Account> librarians;
    @Getter
    @Setter
    private List<Account> admins;
    @Getter
    @Setter
    private List<Account> filteredDefaults;
    @Getter
    @Setter
    private List<Account> filteredLibrarians;
    @Getter
    @Setter
    private List<Account> filteredAdmins;
    @Getter
    private List<FilterMeta> filterBy;

    @PostConstruct
    public void init(){
        defaults = accountDao.getForRoleName("default");
        librarians = accountDao.getForRoleName("konyvtaros");
        admins = accountDao.getForRoleName("admin");

        setBorrowedBooks(defaults);
        setBorrowedBooks(librarians);
        setBorrowedBooks(admins);

        filteredDefaults = new ArrayList<>(defaults);
        filteredLibrarians = new ArrayList<>(librarians);
        filteredAdmins = new ArrayList<>(admins);

        filterBy = new ArrayList<>();
    }

    public void setBorrowedBooks(List<Account> accounts){
        for (Account account: accounts) {
            account.setBooksNotReturnedYet(borrowedBookDao.getNotReturnedYetForAccountId(account.getAccountId()).size());
            account.setBooksReturned(borrowedBookDao.getReturnedForAccountId(account.getAccountId()).size());
            account.setTotalBooksBorrowed(account.getBooksNotReturnedYet() + account.getBooksReturned());
        }
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
                || account.getLoginName().toLowerCase().contains(filterText)
                || account.getPerson().getFirstName().toLowerCase().contains(filterText)
                || account.getPerson().getLastName().toLowerCase().contains(filterText)
                || account.getPerson().getPhoneNumber().toLowerCase().contains(filterText)
                || account.getPerson().getEmail().toLowerCase().contains(filterText)
                || account.getPerson().getAddress().toLowerCase().contains(filterText);

        return check;
    }

    public List<Account> getDefaults(){
        return new ArrayList<Account>(defaults);
    }
    public void setFilteredDefaults(List<Account> filteredDefaults) { this.filteredDefaults = filteredDefaults; }
    public List<Account> getFilteredDefaults() {
        return filteredDefaults;
    }

    public List<Account> getLibrarians(){
        return new ArrayList<Account>(librarians);
    }
    public void setFilteredLibrarians(List<Account> filteredLibrarians) { this.filteredLibrarians = filteredLibrarians; }
    public List<Account> getFilteredLibrarians() {
        return filteredLibrarians;
    }

    public List<Account> getAdmins(){
        return new ArrayList<Account>(admins);
    }
    public void setFilteredAdmins(List<Account> filteredAdmins) { this.filteredAdmins = filteredAdmins; }
    public List<Account> getFilteredAdmins() {
        return filteredAdmins;
    }
}
