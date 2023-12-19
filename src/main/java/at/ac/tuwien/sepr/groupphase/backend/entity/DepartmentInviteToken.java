package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
public class DepartmentInviteToken {


    @Column(nullable = false, name = "manager")
    private String manager;
    @Column(nullable = false, name = "email", unique = true)
    private String email;
    @Column(nullable = false, name = "token")
    private String token;
    @Column(nullable = false, name = "time")
    private LocalDateTime localDateTime;
    @Id
    @GeneratedValue
    private Long id;

    public DepartmentInviteToken() {

    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(Long id) {
        this.id = id;
    }

    //@Column(name = "time", columnDefinition = "TIME", insertable = false, updatable = false)
    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public String getToken() {
        return token;
    }

    public Long getId() {
        return id;
    }

    @Column(name = "email", insertable = false, updatable = false)
    public String getEmail() {
        return email;
    }


    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getManager() {
        return manager;
    }

    public DepartmentInviteToken(String manager, String email, String token, LocalDateTime time) {
        this.manager = manager;
        this.email = email;
        this.token = token;
        this.localDateTime = time;
    }
}
