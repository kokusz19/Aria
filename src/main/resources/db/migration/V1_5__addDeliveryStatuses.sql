alter table borrowedBook add column dateToBeReturned date;

create table borrowStatus (
                              borrowStatusId bigint not null,
                              borrowStatusName varchar not null,
                              PRIMARY KEY(borrowstatusid)
);
create table borrowStatusToBorrowedBook (
                                            borrowStatusToBorrowedBookId bigint not null unique,
                                            borrowStatusId bigint not null,
                                            borrowedBookId bigint not null,
                                            updateDate date not null,
                                            PRIMARY KEY(borrowStatusToBorrowedBookId)
);

ALTER TABLE borrowStatusToBorrowedBook ADD FOREIGN KEY (borrowedBookId) REFERENCES borrowedBook (borrowedBookId);
ALTER TABLE borrowStatusToBorrowedBook ADD FOREIGN KEY (borrowStatusId) REFERENCES borrowStatus (borrowStatusId);

insert into borrowStatus values(1, 'I will pick it up');
insert into borrowStatus values(2, 'Picked up by user');
insert into borrowStatus values(3, 'Ready for packing');
insert into borrowStatus values(4, 'Ready for delivery');
insert into borrowStatus values(5, 'Delivery in progress');
insert into borrowStatus values(6, 'Delivered');
insert into borrowStatus values(7, 'Cancelled');
insert into borrowStatus values(8, 'Removed by librarian');