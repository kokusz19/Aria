package aria.web;

import at.favre.lib.crypto.bcrypt.BCrypt;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import aria.domain.dao.AccountDao;
import aria.domain.ejb.Account;
import aria.domain.ejb.Act;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.io.Serializable;

@ManagedBean(name = "loginController", eager = true)
@SessionScoped
public class LoginController implements Serializable {
    FacesMessage message;
    @Inject
    AccountDao accountDao;

    @Getter
    @Setter
    @Size(min=3)
    private String username;
    @Getter
    @Setter
    @Size(min=2)
    private String password;
    @Getter
    private Account account;

    public void login() throws IOException {
        String retVal = "";
        try {
            account = accountDao.getForUsername(username);
            BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), account.getPassword());
            if(result.verified){
                Act role = account.getAct();
                String roleName = role.getRoleName();
                String url = null;
                switch (roleName) {
                    case "admin":
                        url = "/admin/HomePage.xhtml";
                        break;
                    case "konyvtaros":
                        url = "/librarian/HomePage.xhtml";
                        break;
                    case "carrier":
                        url = "/carrier/HomePage.xhtml";
                        break;
                    case "default":
                        url = "/user/HomePage.xhtml";
                        break;
                }

                FacesContext context = FacesContext.getCurrentInstance();
                NavigationHandler navigationHandler = context.getApplication().getNavigationHandler();
                context.getExternalContext().getSessionMap().put("id", account.getAccountId());
                navigationHandler
                        .handleNavigation(context, null, url+"?faces-redirect=true&includeViewParams=true");
            }
            else{
                message = new FacesMessage(FacesMessage.SEVERITY_INFO,"wrongPasswd", "Wrong password");
                PrimeFaces.current().dialog().showMessageDynamic(message);
                FacesContext context = FacesContext.getCurrentInstance();
                NavigationHandler navigationHandler = context.getApplication().getNavigationHandler();
                navigationHandler
                        .handleNavigation(context, null, "/failure.xhml?faces-redirect=true&includeViewParams=true");
            }
        } catch (Exception e){
            FacesContext context = FacesContext.getCurrentInstance();
            NavigationHandler navigationHandler = context.getApplication().getNavigationHandler();
            navigationHandler
                    .handleNavigation(context, null, "/failure.xhml?faces-redirect=true&includeViewParams=true");

        }
    }
}
