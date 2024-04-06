package Objects;

import java.io.Serial;
import java.io.Serializable;

public class User implements Serializable {

    private static User instance = null;

    public static void setUser(User user){
        instance = user;
    }
    public static User getInstance(){
        return instance;
    }
    @Serial
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String last_name;
    private String username;
    private String password;
    private String phone;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}