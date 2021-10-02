/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the 
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package aria.web;

import aria.domain.dao.*;
import aria.domain.ejb.Notification;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "greetController", eager = true)
@RequestScoped
public class GreetController {

    @Inject
    AccountDao accountDao;
    @Inject
    BookDao bookDao;
    @Inject
    NotificationDao notificationDao;
    @Inject
    AuthorToBookDao authorToBookDao;

    @Getter
    @Setter
    private long accountId;
    @Getter
    @Setter
    private List<Notification> notifications;
    @Getter
    @Setter
    private String userName;
    @Getter
    @Setter
    private List<Notification> allNotifications;

    @PostConstruct
    public void init(){
        accountId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("id").toString());
        userName = accountDao.getForAccountId(accountId).getLoginName();
        notifications = new ArrayList<>();
        getAllNotifications(accountId);
        getNotifications(accountId);
    }

    public void greet() {

    }

    public void getAllNotifications(long accountId) {
        allNotifications = notificationDao.getForAccountId(accountId);
        for (Notification tempNotification : allNotifications) {
            tempNotification.getBook().setAuthors(authorToBookDao.getAuthorsForBookId(tempNotification.getBook().getBookId()));
            tempNotification.getBook().setAuthorsString(tempNotification.getBook().getAuthors().toString().replace("[", "").replace("]", "").trim());
        }
    }

    public void getNotifications(long accountId) {
        for (Notification tempNotification : allNotifications)
            if (bookDao.getForBookId(tempNotification.getBook().getBookId()).getAvailableItems() > 0) {
                tempNotification.getBook().setAuthors(authorToBookDao.getAuthorsForBookId(tempNotification.getBook().getBookId()));
                tempNotification.getBook().setAuthorsString(tempNotification.getBook().getAuthors().toString().replace("[", "").replace("]", "").trim());
                notifications.add(tempNotification);
            }
    }

    public void dismissNotification(long notificationId) throws IOException {
        notificationDao.removeNotification(notificationDao.getForNotificationId(notificationId));
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI());
    }



}
