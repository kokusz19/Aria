CREATE TABLE author (
                        authorId  bigint NOT NULL,
                        firstName VARCHAR NOT NULL,
                        lastName  VARCHAR NOT NULL,
                        dateOfBirth date,
                        PRIMARY KEY(authorId)
);
CREATE TABLE book (
                      bookId  bigint             NOT NULL,
                      bookTitle VARCHAR  NOT NULL,
                      isbn    VARCHAR    NOT NULL unique,
                      publishedAt date,
                      genreId   VARCHAR,
                      maxItems    bigint not null,
                      availableItems  bigint not null,
                      PRIMARY KEY(bookId)
);
CREATE TABLE genre (
                       genreId bigint NOT NULL,
                       genreName VARCHAR(255) NOT NULL unique,
                       PRIMARY KEY(genreId)
);
CREATE TABLE genreToBook(
                            genreToBookId bigint not null,
                            bookId bigint not null,
                            genreId bigint not null,
                            PRIMARY KEY(genreToBookId)
);
CREATE TABLE authorToBook(
                             authorToBookId bigint not null,
                             bookId bigint not null,
                             authorId bigint not null,
                             PRIMARY KEY(authorToBookId)
);

ALTER TABLE genreToBook ADD FOREIGN KEY (bookId) REFERENCES book (bookId);
ALTER TABLE genreToBook ADD FOREIGN KEY (genreId) REFERENCES genre (genreId);

ALTER TABLE authorToBook ADD FOREIGN KEY (authorId) REFERENCES author (authorId);
ALTER TABLE authorToBook ADD FOREIGN KEY (bookId) REFERENCES book (bookId);

CREATE TABLE  person  (
                          personId bigint not null,
                          firstName varchar not null,
                          lastName varchar not null,
                          dateOfBirth date not null,
                          phoneNumber varchar not null,
                          email varchar not null,
                          address varchar not null,
                          PRIMARY KEY(personId)
);
CREATE TABLE account (
                         accountId bigint not null,
                         loginName varchar unique not null,
                         createdAd date not null,
                         password varchar not null,
                         personId bigint unique not null,
                         actId bigint not null,
                         PRIMARY KEY(accountId)
);
CREATE TABLE act (
                     actId bigint not null,
                     roleName varchar not null,
                     rolePrio bigint not null,
                     PRIMARY KEY(actId)
);
ALTER TABLE account ADD FOREIGN KEY (actId) REFERENCES act (actId);
ALTER TABLE account ADD FOREIGN KEY (personId) REFERENCES person (personId);

CREATE TABLE borrowingPrices (
                                 borrowingPricesId bigint not null,
                                 maxDaysSinceBorrowed bigint not null,
                                 price bigint not null,
                                 PRIMARY KEY(borrowingPricesId)
);

CREATE TABLE borrowedBooks (
                               borrowingId bigint not null,
                               bookId bigint not null,
                               accountId bigint not null,
                               dateOfBorrow date not null,
                               borrowingDays bigint,
                               PRIMARY KEY(borrowingId)
);

ALTER TABLE borrowedBooks ADD FOREIGN KEY (bookId) REFERENCES book (bookId);
ALTER TABLE borrowedBooks ADD FOREIGN KEY (accountId) REFERENCES account (accountId);