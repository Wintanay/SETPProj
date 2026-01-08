package models;

public class Admin {
    private String id;
    private String username;
    private String password;

    public Admin(String id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }

    public String toCSV() {
        return String.join(",", id, username, password == null ? "" : password);
    }

    public static Admin fromCSV(String line) {
        String[] p = line.split(",");
        if (p.length >= 3) {
            return new Admin(p[0].trim(), p[1].trim(), p[2].trim());
        }
        return null;
    }
}
