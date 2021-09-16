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
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.time.LocalDate;
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
    private String tipus;

    public void create() {
        message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Message", " Sikerült létrehozni!");
        newAccount.setPerson(createPerson());
        newAccount.setAccountCreatedAt(LocalDate.now());
        String bcryptHashString = createPassword();
        newAccount.setJelszo(bcryptHashString);
        newAccount.setAct(actDao.getAct(tipus));

        try {
            if (accountDao.getForUsername(newAccount.getLoginName()) == null) {
                personDao.createPerson(newAccount.getPerson());
                accountDao.createUser(newAccount);
                if (tipus.equals("Beteg")) {
                    // TODO
                }
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Message", " Sikerült létrehozni!");
                PrimeFaces.current().dialog().showMessageDynamic(message);
            } else {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Message", " Nem sikerült létrehozni, a megadott felhasználónév már foglalt!");
                PrimeFaces.current().dialog().showMessageDynamic(message);
            }
        } catch (Exception e) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Message", " Nem sikerült létrehozni!");
            PrimeFaces.current().dialog().showMessageDynamic(message);
        }
    }

    private Person createPerson(){
        Person person = new Person();
        person.setAddress(address);
        person.setEmail(email);
        person.setDateOfBirth(new java.sql.Date(dateOfBirth.getTime()).toLocalDate());
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setPhoneNumber(phoneNumber);
        return person;
    }

    public String createPassword() {
        String hash = null;
        switch (tipus) {
            case "Orvos":
                hash = BCrypt.withDefaults().hashToString(12, password.toCharArray());
                break;
            case "Asszisztens":
                hash = BCrypt.withDefaults().hashToString(10, password.toCharArray());
                break;
            case "Beteg":
                hash = BCrypt.withDefaults().hashToString(8, password.toCharArray());
                break;
        }
        return hash;
    }
}
