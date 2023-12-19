package at.ac.tuwien.sepr.groupphase.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import com.fasterxml.jackson.annotation.JsonBackReference;


import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "name")
    private String name;

    @JsonIgnore
    @ManyToMany()
    @JoinTable(
        name = "project_members",
        joinColumns = @JoinColumn(name = "project_id"),
        inverseJoinColumns = @JoinColumn(name = "member_id"))
    private List<ApplicationUser> members;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ApplicationUser> getMembers() {
        return members;
    }

    public void setMembers(List<ApplicationUser> members) {
        this.members = members;
    }

    public static class ProjectBuilder {

        private Long id;

        private String name;

        private List<ApplicationUser> members;

        private Department department;

        public static Project.ProjectBuilder aDefaultProject() {
            return new Project.ProjectBuilder();
        }

        public ProjectBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public ProjectBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public ProjectBuilder withMembers(List<ApplicationUser> members) {
            this.members = members;
            return this;
        }

        public ProjectBuilder withDepartment(Department department) {
            this.department = department;
            return this;
        }

        public Project build() {
            Project project = new Project();
            project.setId(this.id);
            project.setName(this.name);
            project.setMembers(this.members);
            project.setDepartment(this.department);
            return project;
        }
    }
}
