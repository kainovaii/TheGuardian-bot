package fr.kainovaii.guardian.domain.user;

import org.javalite.activejdbc.Model;

public class User extends Model
{
    public String getUsername() {
        return getString("username");
    }

    public void setUsername(String username) {
        set("username", username);
    }

    public String getPassword() {
        return getString("password");
    }

    public void setPassword(String password) {
        set("password", password);
    }

    public String getRole() {
        return getString("role");
    }

    public void setRole(String role) {
        set("role", role);
    }
}
