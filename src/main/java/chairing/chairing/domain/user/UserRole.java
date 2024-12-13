package chairing.chairing.domain.user;

import lombok.Getter;

@Getter
public enum UserRole {
    NORMAL("ROLE_NORMAL"),
    ADMIN("ROLE_ADMIN"),
    GUARDIAN("ROLE_GUARDIAN");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return this.role;
    }
}
