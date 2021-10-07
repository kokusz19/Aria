package aria.web;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import java.io.Serializable;
import java.time.LocalDate;

@ManagedBean(name = "helperController", eager = true)
@RequestScoped
public class HelperController implements Serializable {
    public HelperController(){

    }

    public boolean localDateFilterCheck(LocalDate localDate, String filterText){
        if(localDate == null)
            return false;
        String stringDate = String.valueOf(localDate.getYear()).concat("-").concat(localDate.getMonthValue() < 10 ? "0".concat(String.valueOf(localDate.getMonthValue())) : String.valueOf(localDate.getMonthValue())).concat("-").concat(localDate.getDayOfMonth() < 10 ? "0".concat(String.valueOf(localDate.getDayOfMonth())) : String.valueOf(localDate.getDayOfMonth()));
        return stringDate.contains(filterText);
    }
}
