package aria.domain.ejb;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name="account")
public class Account implements Serializable {
    @Getter
    @Id
    @GeneratedValue
    private Long accountId;
    @Getter
    @Setter
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "personId")
    private Person person;
    @Getter
    @Setter
    @Column(name = "loginName", unique = true)
    private String loginName;
    @Getter
    @Setter
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "actid")
    private Act act;
    @Getter
    @Setter
    @Column(name = "jelszo")
    private String jelszo;
    @Getter
    @Setter
    @Column(name = "accountCreatedAt")
    private LocalDate accountCreatedAt;
    //TODO átgondolni itt ezeket ujra
    /*
    @Getter
    @Setter
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "doctor")
    private List<Patient> patients;
    @Getter
    @Setter
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "prescribedBy")
    private List<Prescription> prescriptions;

    @Getter
    @Setter
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "recordedBy")
    private List<Visit> visitsRecorded;
    @Getter
    @Setter
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "diagnosedBy")
    private List<Visit> visitsDiagnosed;*/
}
