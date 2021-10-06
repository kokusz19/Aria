insert into borrowStatus values(9, 'Brought back');
insert into borrowStatus values(10, 'Could not be delivered');

create table carriedBooks(
                             carryingId bigint not null,
                             carrierId bigint not null,
                             borrowedbookId bigint not null,
                             PRIMARY KEY(carryingId)
);
ALTER TABLE carriedBooks ADD FOREIGN KEY (carrierId) REFERENCES account (accountId);
ALTER TABLE carriedBooks ADD FOREIGN KEY (borrowedBookId) REFERENCES borrowedBook (borrowedBookId);