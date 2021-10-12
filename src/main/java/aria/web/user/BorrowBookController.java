package aria.web.user;

import aria.domain.dao.*;
import aria.domain.ejb.*;
import aria.web.HelperController;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;

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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    @Inject
    NotificationDao notificationDao;
    @Inject
    BorrowStatusToBorrowedBookDao borrowStatusToBorrowedBookDao;
    @Inject
    BorrowStatusDao borrowStatusDao;
    @Inject
    HelperController helperController;

    @Getter
    private Long bookId;
    @Getter
    @Setter
    private Book book;
    @Getter
    @Setter
    private boolean exist = false;
    @Getter
    @Setter
    private String pickUpOrDeliver = "";

    @PostConstruct
    public void init() {
        Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        bookId = Long.parseLong(map.get("bookId"));
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
            borrowedBook.setDateOfBorrow(LocalDateTime.now());

            book.setAvailableItems(book.getAvailableItems()-1);

            BorrowStatusToBorrowedBook borrowStatusToBorrowedBook = new BorrowStatusToBorrowedBook();
            borrowStatusToBorrowedBook.setBorrowedBook(borrowedBook);
            if(pickUpOrDeliver.equals("1"))
                borrowStatusToBorrowedBook.setBorrowStatus(borrowStatusDao.getBorrowStatus(1));
            else if(pickUpOrDeliver.equals("2"))
                borrowStatusToBorrowedBook.setBorrowStatus(borrowStatusDao.getBorrowStatus(3));
            borrowStatusToBorrowedBook.setUpdateDate(LocalDateTime.now());

            List<BorrowedBook> forAccount = borrowedBookDao.getForAccountId(accountId).stream().filter(e -> e.getBook().getBookId() == bookId).collect(Collectors.toList());
            for (BorrowedBook bb: forAccount) {
                BorrowStatus tmpStat = borrowStatusToBorrowedBookDao.getLatestStatusForBorrowedBookId(bb.getBorrowedBookId()).getBorrowStatus();
                bb.setCurrentStatus(tmpStat);
            }
            long allClosed = forAccount.stream().filter(x->x.getCurrentStatus().getBorrowStatusId() == 7 || x.getCurrentStatus().getBorrowStatusId() == 8 || x.getCurrentStatus().getBorrowStatusId() == 9).count();
            if(forAccount.size() == 0 || forAccount.size() == allClosed){
                borrowedBookDao.createBorrowedBook(borrowedBook);
                bookDao.updateBook(book);
                if(!notificationDao.getForAccountId(accountId).isEmpty())
                    for (Notification notification: notificationDao.getForAccountId(accountId))
                        if (notification.getBook().getBookId() == bookId)
                            notificationDao.removeNotification(notification);
                borrowStatusToBorrowedBookDao.createBorrowStatusToBorrowedBook(borrowStatusToBorrowedBook);

                String detail = borrowedBook.getBook().getAuthorsString() + " - " + borrowedBook.getBook().getBookTitle() + " has been borrowed.";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Borrowing", detail));

                context.getExternalContext().getFlash().setKeepMessages(true);

                navigationHandler.handleNavigation(context, null, "Book.xhtml?faces-redirect=true&includeViewParams=true");
            } else{
                String detail = "You already have " + borrowedBook.getBook().getAuthorsString() + " - " + borrowedBook.getBook().getBookTitle();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Borrowing", detail));
            }

        }
    }

    public void notify(long bookId){
        Book tmpBook = bookDao.getForBookId(bookId);
        List<Book> tmpListBook = new ArrayList<>();
        tmpListBook.add(tmpBook);
        helperController.generateStrings(tmpListBook);

        long accountId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id").toString());
        List<Notification> ownNotifications = notificationDao.getForAccountId(accountId);

        if (ownNotifications.stream().filter(n -> n.getBook().getBookId() == bookId).count() == 0) {

            Notification notification = new Notification();
            notification.setBook(bookDao.getForBookId(bookId));
            notification.setAccount(accountDao.getForAccountId(accountId));

            List<Book> bookList = new ArrayList<>();
            bookList.add(notification.getBook());
            helperController.generateStrings(bookList);

            notificationDao.createNotification(notification);


            String detail = "Notification has been set for " + notification.getBook().getAuthorsString() + " - " + notification.getBook().getBookTitle() + ".";
            FacesContext context = FacesContext.getCurrentInstance();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Notification", detail));

            context.getExternalContext().getFlash().setKeepMessages(true);

            NavigationHandler navigationHandler = context.getApplication().getNavigationHandler();
            navigationHandler.handleNavigation(context, null, "Book.xhtml?faces-redirect=true&includeViewParams=true");
        } else{
            String detail = "A notification has already been set for " + tmpBook.getAuthorsString() + " - " + tmpBook.getBookTitle() + ".";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Notification", detail));
        }
    }
}