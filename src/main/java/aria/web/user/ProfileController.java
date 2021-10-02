package aria.web.user;

import aria.domain.dao.AccountDao;
import aria.domain.dao.PersonDao;
import aria.domain.ejb.Account;
import aria.domain.ejb.Person;
import at.favre.lib.crypto.bcrypt.BCrypt;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

@ManagedBean(name = "profileController")
@ViewScoped
public class ProfileController {

    @Inject
    AccountDao accountDao;
    @Inject
    PersonDao personDao;

    @Getter
    @Setter
    private Account account;
    @Getter
    @Setter
    private boolean editable = false;
    @Getter
    @Setter
    private boolean editPassword = false;
    @Getter
    @Setter
    private String currentPassword, newPassword1, newPassword2;

    @PostConstruct
    public void init(){
        account = accountDao.getForAccountId(Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id").toString()));
    }

    public void saveValue() {
        personDao.updatePerson(account.getPerson());
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage( "Updated!", "Profile has been updated."));
    }

    public ProfileController(){

    }

    public void changeEditable() {
        editable = !editable;
    }
    public void changeEditPassword(){
        editPassword = !editPassword;
    }
    public void savePassword(){
        String detail = "";
        FacesMessage.Severity severity = null;

        BCrypt.Result result = BCrypt.verifyer().verify(currentPassword.toCharArray(), account.getPassword());
        if(!result.verified){
            severity = FacesMessage.SEVERITY_ERROR;
            detail = "Current password is not correct";
        }else{
            if(!newPassword1.equals(newPassword2)){
                severity = FacesMessage.SEVERITY_ERROR;
                detail = "New passwords does not match";
            }
            else{
                if(BCrypt.verifyer().verify(newPassword1.toCharArray(), account.getPassword()).verified){
                    severity = FacesMessage.SEVERITY_ERROR;
                    detail = "New passwords matches the current password";
                }else {
                    account.setPassword(BCrypt.withDefaults().hashToString(12, newPassword2.toCharArray()));
                    accountDao.updateUser(account);
                    severity = FacesMessage.SEVERITY_INFO;
                    detail = "New password has been set";
                    changeEditPassword();
                }
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, "Password", detail));
    }
}
