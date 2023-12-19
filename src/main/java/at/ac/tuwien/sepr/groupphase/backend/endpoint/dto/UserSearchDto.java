package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public class UserSearchDto {
    private Integer maxResults;
    private String firstInput;
    private String lastInput;

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

    public static final class UserSearchDtoBuilder {
        private Integer maxResults;
        private String firstInput;
        private String lastInput;

        private UserSearchDtoBuilder() {
        }

        public static UserSearchDto.UserSearchDtoBuilder anUserSearchDto() {
            return new UserSearchDto.UserSearchDtoBuilder();
        }

        public UserSearchDto.UserSearchDtoBuilder withFirstName(String firstName) {
            this.firstInput = firstName;
            return this;
        }

        public UserSearchDto.UserSearchDtoBuilder withLastName(String lastName) {
            this.lastInput = lastName;
            return this;
        }

        public UserSearchDto.UserSearchDtoBuilder withEmail(Integer maxResults) {
            this.maxResults = maxResults;
            return this;
        }

        public UserSearchDto build() {
            UserSearchDto userSearchDto = new UserSearchDto();
            userSearchDto.setFirstInput(firstInput);
            userSearchDto.setLastInput(lastInput);
            userSearchDto.setMaxResults(maxResults);
            return userSearchDto;
        }
    }

}
