package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public class UpdatePasswordRequest {
    private String resetToken;
    private String newPassword;

    public String getNewPassword() {
        return newPassword;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    @Override
    public String toString() {
        return "UpdatePasswordRequest{"
            + "resetToken='" + resetToken + '\''
            + ", newPassword='" + newPassword + '\''
            + '}';
    }
}
