package aria.web;


import at.favre.lib.crypto.bcrypt.BCrypt;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import aria.domain.dao.AccountDao;
import aria.domain.dao.ActDao;
import aria.domain.dao.PersonDao;
import aria.domain.ejb.*;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Named
@SessionScoped
public class CreateController implements Serializable {

    FacesMessage message;

    @Inject
    private AccountDao accountDao;

    @Inject
    private PersonDao personDao;

    @Inject
    private ActDao actDao;


    @Setter
    @Getter
    @Inject
    private Account newAccount;

    @Getter
    @Setter
    private String loginName;
    @Getter
    @Setter
    private String firstName;
    @Getter
    @Setter
    private String lastName;
    @Getter
    @Setter
    private Date dateOfBirth;
    @Getter
    @Setter
    private String phoneNumber;
    @Getter
    @Setter
    private String email;
    @Getter
    @Setter
    private String address;
    @Getter
    @Setter
    private String password;
    @Getter
    @Setter
    private String tipus = "default";

    public void create() {
        message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Message", " Sikerült létrehozni!");
        newAccount = new Account();
        newAccount.setPerson(createPerson());
        newAccount.setCreatedAt(LocalDateTime.now());
        newAccount.setPassword(BCrypt.withDefaults().hashToString(12, password.toCharArray()));
        newAccount.setAct(actDao.getAct(tipus));
        newAccount.setLoginName(loginName);
        try {
            if (accountDao.getForUsername(newAccount.getLoginName()) == null) {
                personDao.createPerson(newAccount.getPerson());
                accountDao.createUser(newAccount);

                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Registration", "Registration successful"));
                clearTextBoxes();

                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);

                NavigationHandler navigationHandler = context.getApplication().getNavigationHandler();
                navigationHandler.handleNavigation(context, null, "greet.xhtml?faces-redirect=true&includeViewParams=true");

            } else {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Message", " Registration failed, the username is already in use!");
                PrimeFaces.current().dialog().showMessageDynamic(message);
            }
        } catch (Exception e) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Message", " Registration failed due to an unknown error!");
            PrimeFaces.current().dialog().showMessageDynamic(message);
        }
    }

    private Person createPerson(){
        Person person = new Person();
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setDateOfBirth(new java.sql.Date(dateOfBirth.getTime()).toLocalDate());
        person.setPhoneNumber(phoneNumber);
        person.setEmail(email);
        person.setAddress(address);
        return person;
    }

    private void clearTextBoxes(){
        setLoginName("");
        setFirstName("");
        setLastName("");
        setPassword("");
        setAddress("");
        setDateOfBirth(null);
        setEmail("");
        setPhoneNumber("");
        getNewAccount().setLoginName("");
    }
}
