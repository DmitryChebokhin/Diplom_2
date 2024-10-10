import org.apache.commons.lang3.RandomStringUtils;

public class UserSetGet {
    private String email;
    private String password;
    private String name;

    public  UserSetGet(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public static  UserSetGet generateUser() {
        String email = RandomStringUtils.randomAlphabetic(3,12) + "@gmail.com";
        String password = RandomStringUtils.randomAlphabetic(12);
        String name = RandomStringUtils.randomAlphabetic(3,12);

        return new  UserSetGet(email, password, name);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}