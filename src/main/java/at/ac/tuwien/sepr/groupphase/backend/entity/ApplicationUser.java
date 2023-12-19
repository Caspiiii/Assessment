package at.ac.tuwien.sepr.groupphase.backend.entity;

import at.ac.tuwien.sepr.groupphase.backend.entity.type.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.util.List;
import java.util.Objects;


@Entity
public class ApplicationUser {

    @Column(nullable = false, name = "first_name")
    private String firstName;
    @Column(nullable = false, name = "last_name")
    private String lastName;
    @Column(nullable = false, name = "email", unique = true)
    private String email;
    @Column(nullable = false, name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "role")
    private Role role;
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToMany(mappedBy = "applicationUser")
    private List<Answer> answers;

    @JsonIgnore
    @ManyToMany(mappedBy = "members")
    private List<Project> projects;

    public ApplicationUser() {
    }

    public ApplicationUser(String firstName, String lastName, String email, String password, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ApplicationUser that = (ApplicationUser) o;
        return Objects.equals(firstName, that.firstName)
            && Objects.equals(lastName, that.lastName)
            && Objects.equals(email, that.email)
            && Objects.equals(password, that.password)
            && Objects.equals(role, that.role)
            && Objects.equals(id, that.id)
            && Objects.equals(answers, that.answers)
            && Objects.equals(projects, that.projects);
    }

    @Override
    public String toString() {
        return "ApplicationUser{"
            +
            "firstName='" + firstName + '\''
            +
            ", lastName='" + lastName + '\''
            +
            ", email='" + email + '\''
            +
            ", password='" + password + '\''
            +
            ", role=" + role
            +
            ", id=" + id
            +
            ", department=" + department
            +
            '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, email, password, role, id, answers);
    }


    public static final class ApplicationUserBuilder {

        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String password;
        private Role role;
        private List<Answer> answers;
        private List<Project> projects;
        private Department department;

        private ApplicationUserBuilder() {
        }

        public static ApplicationUserBuilder aApplicationUser() {
            return new ApplicationUserBuilder();
        }


        public ApplicationUserBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public ApplicationUserBuilder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public ApplicationUserBuilder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public ApplicationUserBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public ApplicationUserBuilder withPassword(String password) {
            this.password = password;
            return this;
        }


        public ApplicationUserBuilder withRole(Role role) {
            this.role = role;
            return this;
        }

        public ApplicationUserBuilder withAnswers(List<Answer> answers) {
            this.answers = answers;
            return this;
        }

        public ApplicationUserBuilder withDepartment(Department department) {
            this.department = department;
            return this;
        }

        public ApplicationUserBuilder withProjects(List<Project> projects) {
            this.projects = projects;
            return this;
        }

        public ApplicationUser build() {
            ApplicationUser user = new ApplicationUser();
            user.setId(id);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setPassword(password);
            user.setProjects(projects);
            user.setDepartment(department);
            user.setAnswers(answers);
            user.setRole(role);
            return user;

        }
    }
}