package aria.web;

import aria.domain.dao.AccountDao;
import aria.domain.dao.ActDao;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.ejb.Init;
import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;
import javax.inject.Inject;
import java.io.Serializable;



@ManagedBean(name = "pageHeaderController", eager = true)
@SessionScoped
public class PageHeaderController implements Serializable{

    @Inject
    AccountDao accountDao;

    @Getter
    @Setter
    private Long actId;

    @PostConstruct
    public void init(){
        setActId(accountDao.getForAccountId(Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id").toString())).getAct().getActId());
    }

    public void signOut(){

        String url = "/greet.xhtml";

        FacesContext context = FacesContext.getCurrentInstance();
        NavigationHandler navigationHandler = context.getApplication().getNavigationHandler();
        context.getExternalContext().getSessionMap().remove("id");
        navigationHandler
                .handleNavigation(context, null, url+"?faces-redirect=true&includeViewParams=true");

    }
}
