package aria.web;

import aria.domain.dao.AuthorToBookDao;
import aria.domain.dao.GenreToBookDao;
import aria.domain.ejb.Book;
import aria.domain.ejb.BorrowedBook;
import aria.domain.ejb.Genre;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@ManagedBean(name = "helperController", eager = true)
@RequestScoped
public class HelperController implements Serializable {
    @Inject
    GenreToBookDao genreToBookDao;
    @Inject
    AuthorToBookDao authorToBookDao;

    public HelperController(){

    }

    public String localDateTimeConverter(Object date, boolean needSeconds){
        if(date == null)
            return "";

        String output = "";
        if(date instanceof LocalDate){
            LocalDate tmpDate = (LocalDate) date;
            output = String.valueOf(tmpDate.getYear())
                    .concat("-")
                    .concat(tmpDate.getMonthValue() < 10 ? "0".concat(String.valueOf(tmpDate.getMonthValue())) : String.valueOf(tmpDate.getMonthValue()))
                    .concat("-")
                    .concat(tmpDate.getDayOfMonth() < 10 ? "0".concat(String.valueOf(tmpDate.getDayOfMonth())) : String.valueOf(tmpDate.getDayOfMonth()));
        }
        if(date instanceof LocalDateTime) {
            LocalDateTime tmpDate = (LocalDateTime) date;
            output = String.valueOf(tmpDate.getYear())
                    .concat("-")
                    .concat(tmpDate.getMonthValue() < 10 ? "0".concat(String.valueOf(tmpDate.getMonthValue())) : String.valueOf(tmpDate.getMonthValue()))
                    .concat("-")
                    .concat(tmpDate.getDayOfMonth() < 10 ? "0".concat(String.valueOf(tmpDate.getDayOfMonth())) : String.valueOf(tmpDate.getDayOfMonth()))
                    .concat(" ")
                    .concat(tmpDate.getHour() < 10 ? "0".concat(String.valueOf(tmpDate.getHour())) : String.valueOf(tmpDate.getHour()))
                    .concat(":")
                    .concat(tmpDate.getMinute() < 10 ? "0".concat(String.valueOf(tmpDate.getMinute())) : String.valueOf(tmpDate.getMinute()));
            if (needSeconds) {
                output.concat(":")
                        .concat(tmpDate.getSecond() < 10 ? "0".concat(String.valueOf(tmpDate.getSecond())) : String.valueOf(tmpDate.getSecond()));
            }
        }
        return output;
    }

    public void setStringBorrowedBookDates(BorrowedBook borrowedBook){
        borrowedBook.setStringDateOfBorrow(localDateTimeConverter(borrowedBook.getDateOfBorrow(), false));
        borrowedBook.setStringDateOfReturn(localDateTimeConverter(borrowedBook.getDateOfReturn(), false));
        borrowedBook.setStringDateToBeReturned(localDateTimeConverter(borrowedBook.getDateToBeReturned(), false));
    }

    public void generateStrings(List<Book> tmpBooks){
        for (Book tmpBook: tmpBooks) {
            tmpBook.setGenres(genreToBookDao.getGenresForBookId(tmpBook.getBookId()));
            tmpBook.setGenresString(tmpBook.getGenres().toString());
            tmpBook.setGenresString(tmpBook.getGenresString().replace("[", ""));
            tmpBook.setGenresString(tmpBook.getGenresString().replace("]", ""));

            tmpBook.setAuthors(authorToBookDao.getAuthorsForBookId(tmpBook.getBookId()));
            tmpBook.setAuthorsString(tmpBook.getAuthors().toString());
            tmpBook.setAuthorsString(tmpBook.getAuthorsString().replace("[", ""));
            tmpBook.setAuthorsString(tmpBook.getAuthorsString().replace("]", ""));
        }
    } public void generateStrings(Book tmpBook){
            tmpBook.setGenres(genreToBookDao.getGenresForBookId(tmpBook.getBookId()));
            tmpBook.setGenresString(tmpBook.getGenres().toString());
            tmpBook.setGenresString(tmpBook.getGenresString().replace("[", ""));
            tmpBook.setGenresString(tmpBook.getGenresString().replace("]", ""));

            tmpBook.setAuthors(authorToBookDao.getAuthorsForBookId(tmpBook.getBookId()));
            tmpBook.setAuthorsString(tmpBook.getAuthors().toString());
            tmpBook.setAuthorsString(tmpBook.getAuthorsString().replace("[", ""));
            tmpBook.setAuthorsString(tmpBook.getAuthorsString().replace("]", ""));
    }
}
