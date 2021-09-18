alter table book drop column genreId;
alter table book add column languageId bigint not null;
CREATE TABLE languageOfBook  (
                                 languageId bigint not null,
                                 languageName varchar not null unique,
                                 PRIMARY KEY(languageId)
);
ALTER TABLE book ADD FOREIGN KEY (languageId) REFERENCES languageOfBook (languageId);