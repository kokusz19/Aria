alter table account alter column createdAt type timestamp;

alter table borrowedbook alter column dateOfBorrow TYPE timestamp;
alter table borrowedbook alter column dateOfReturn TYPE timestamp;
alter table borrowedbook alter column dateToBeReturned type timestamp;

alter table borrowstatustoborrowedbook alter column updateDate type timestamp;