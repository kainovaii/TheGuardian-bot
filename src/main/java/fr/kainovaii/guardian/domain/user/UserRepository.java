package fr.kainovaii.guardian.domain.user;

import org.javalite.activejdbc.LazyList;

public class UserRepository
{
    public void create(String username, String password, String role)
    {
        User user = new User();
        user.set("username", username,  "password", password,"role", role);
        user.saveIt();
    }

    public boolean updateByUsername(String username, String newUsername, String newPassword)
    {
        User user = this.findByUsername(username);
        user.set("username", newUsername, "password", newPassword);
        return user.saveIt();
    }

    public static boolean userExist(String username)
    {
        return User.findFirst("username = ?", username) != null;
    }

    public LazyList<User> getAll() {

        return User.findAll();
    }

    public User findByUsername(String username)
    {
        return User.findFirst("username = ?", username);
    }
}
