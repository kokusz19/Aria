CREATE TABLE notification (
                               notificationId bigint not null,
                               bookId bigint not null,
                               accountId bigint not null,
                               PRIMARY KEY(notificationId)
);
ALTER TABLE notification ADD FOREIGN KEY (bookId) REFERENCES book (bookId);
ALTER TABLE notification ADD FOREIGN KEY (accountId) REFERENCES account (accountId);