package aria.web.librarian;

import aria.domain.dao.*;
import aria.domain.ejb.Account;
import aria.domain.ejb.Book;
import aria.domain.ejb.BorrowedBook;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import org.primefaces.context.PrimeFacesContext;
import org.primefaces.model.FilterMeta;
import org.primefaces.util.LangUtils;

import javax.annotation.PostConstruct;
import javax.faces.application.NavigationHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import javax.servlet.http.*;

@ManagedBean(name = "userController", eager = true)
@RequestScoped
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

    @Getter
    @Setter
    private List<Account> defaults;
    @Getter
    @Setter
    private List<Account> carriers;
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
    private List<Account> filteredCarriers;
    @Getter
    @Setter
    private List<Account> filteredLibrarians;
    @Getter
    @Setter
    private List<Account> filteredAdmins;
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
        defaults = accountDao.getForRoleName("default");
        carriers = accountDao.getForRoleName("carrier");
        librarians = accountDao.getForRoleName("konyvtaros");
        admins = accountDao.getForRoleName("admin");

        setBorrowedBooks(defaults);
        setBorrowedBooks(librarians);
        setBorrowedBooks(admins);

        filteredDefaults = new ArrayList<>(defaults);
        filteredCarriers = new ArrayList<>(carriers);
        filteredLibrarians = new ArrayList<>(librarians);
        filteredAdmins = new ArrayList<>(admins);

        filterBy = new ArrayList<>();

        FacesContext context = FacesContext.getCurrentInstance();
        if(context.getExternalContext().getSessionMap().containsKey("chosenAccountId")){
            getBooksForAccount(Long.parseLong(context.getExternalContext().getSessionMap().get("chosenAccountId").toString()));
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
        options.put("width", 640);
        options.put("height", 700);
        options.put("contentWidth", "100%");
        options.put("contentHeight", "100%");
        options.put("headerElement", "customheader");

        FacesContext context = FacesContext.getCurrentInstance();
        NavigationHandler navigationHandler = context.getApplication().getNavigationHandler();
        context.getExternalContext().getSessionMap().put("chosenAccountId", accountId);

        PrimeFaces.current().dialog().openDynamic("ViewBorrowedBooks.xhtml", options, null);

        init();
    }

    public void getBooksForAccount(long accountId){
        switch(accountDao.getForAccountId(accountId).getAct().getRoleName()){
            case "default":
                for (Account user: defaults)
                    if(user.getAccountId() == accountId) chosenAccount = user;
                break;
            case "carrier":
                for (Account carrier: carriers)
                    if(carrier.getAccountId() == accountId) chosenAccount = carrier;
                break;
            case "konyvtaros":
                for (Account librarian: librarians)
                    if(librarian.getAccountId() == accountId) chosenAccount = librarian;
                break;
            case "admin":
                for (Account admin: admins)
                    if(admin.getAccountId() == accountId) chosenAccount = admin;
                break;
        }
        setGenresAuthors();
    }
    public void setGenresAuthors(){
        chosenAccountsBooks = new ArrayList<>();
        for (BorrowedBook borrowedBook: chosenAccount.getBooksNotReturnedYet())
            chosenAccountsBooks.add(borrowedBook.getBook());
        for (BorrowedBook borrowedBook: chosenAccount.getBooksReturned())
            chosenAccountsBooks.add(borrowedBook.getBook());

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
