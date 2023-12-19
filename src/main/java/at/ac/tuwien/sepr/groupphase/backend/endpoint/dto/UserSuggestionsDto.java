package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.entity.Department;

public class UserSuggestionsDto {
    private Integer maxResults;
    private String firstInput;
    private String lastInput;
    private Integer projectId;

    private String managerEmail;


    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(Integer maxResults) {
        this.maxResults = maxResults;
    }

    public String getFirstInput() {
        return firstInput;
    }

    public void setFirstInput(String firstInput) {
        this.firstInput = firstInput;
    }

    public String getLastInput() {
        return lastInput;
    }

    public void setLastInput(String lastInput) {
        this.lastInput = lastInput;
    }

    public String getManagerEmail() {
        return managerEmail;
    }

    public void setManagerEmail(String managerEmail) {
        this.managerEmail = managerEmail;
    }

    public static final class UserSuggestionsDtoBuilder {
        private Integer maxResults;
        private String firstInput;
        private String lastInput;
        private Integer projectId;
        private String managerEmail;


        private UserSuggestionsDtoBuilder() {
        }

        public static UserSuggestionsDtoBuilder anUserSearchDto() {
            return new UserSuggestionsDtoBuilder();
        }

        public UserSuggestionsDtoBuilder withManagerEmail(String managerEmail) {
            this.managerEmail = managerEmail;
            return this;
        }

        public UserSuggestionsDtoBuilder withFirstName(String firstName) {
            this.firstInput = firstName;
            return this;
        }

        public UserSuggestionsDtoBuilder withLastName(String lastName) {
            this.lastInput = lastName;
            return this;
        }

        public UserSuggestionsDtoBuilder withEmail(Integer maxResults) {
            this.maxResults = maxResults;
            return this;
        }

        public UserSuggestionsDtoBuilder withProjectId(Integer projectId) {
            this.projectId = projectId;
            return this;
        }

        public UserSuggestionsDto build() {
            UserSuggestionsDto userSuggestionsDto = new UserSuggestionsDto();
            userSuggestionsDto.setFirstInput(firstInput);
            userSuggestionsDto.setLastInput(lastInput);
            userSuggestionsDto.setMaxResults(maxResults);
            userSuggestionsDto.setProjectId(projectId);
            userSuggestionsDto.setManagerEmail(managerEmail);
            return userSuggestionsDto;
        }
    }

}
