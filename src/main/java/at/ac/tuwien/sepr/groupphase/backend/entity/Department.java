package at.ac.tuwien.sepr.groupphase.backend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class Department {

    public Department(String name, ApplicationUser manager, List<ApplicationUser> members, List<Project> projects) {
        this.name = name;
        this.manager = manager;
        this.members = members;
        this.projects = projects;
    }

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToOne()
    @JoinColumn(name = "manager_id")
    private ApplicationUser manager;

    @OneToMany(mappedBy = "department", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private List<ApplicationUser> members;

    @JsonManagedReference
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    private List<Project> projects;

    public Department() {

    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setManager(ApplicationUser manager) {
        this.manager = manager;
    }

    public void setMembers(List<ApplicationUser> members) {
        this.members = members;
    }

    public ApplicationUser getManager() {
        return manager;
    }

    public List<ApplicationUser> getMembers() {
        return members;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public Department(Long id, String name, ApplicationUser manager, List<ApplicationUser> members) {
        this.id = id;
        this.name = name;
        this.manager = manager;
        this.members = members;
    }

}

