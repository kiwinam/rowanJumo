package smart.rowan.chatting;

public class User {
    private String name;
    private String email;
    private int count;

    public User(String name, String email, int count) {
        this.name = name;
        this.email = email;
        this.count = count;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public int getCount() {
        return this.count;
    }
}
