package models;

import java.util.Objects;

public class Customer {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String password; // stored in plain text for this simple example
    private int completedRentals = 0;
    private String badge = "";

    public Customer(String id, String name, String email, String phone, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    public Customer(String id, String name, String email, String phone, String password, int completed, String badge) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.completedRentals = completed;
        this.badge = badge == null ? "" : badge;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getPassword() { return password; }
    public int getCompletedRentals() { return completedRentals; }
    public String getBadge() { return badge; }
    public void setName(String n) { this.name = n; }
    public void setEmail(String e) { this.email = e; }
    public void setPhone(String p) { this.phone = p; }
    public void setPassword(String pw) { this.password = pw; }
    public void setCompletedRentals(int v) { this.completedRentals = v; }
    public void setBadge(String b) { this.badge = b; }

    public String toCSV() {
        return String.join(",", id, name, email, phone, password == null ? "" : password, String.valueOf(completedRentals), badge == null ? "" : badge);
    }

    public static Customer fromCSV(String line) {
        String[] parts = line.split(",", -1);
        // support older format without password (len==4) and older with password (len==5)
        if (parts.length >= 7) {
            try {
                int completed = Integer.parseInt(parts[5].trim());
                String badge = parts[6].trim();
                return new Customer(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim(), parts[4].trim(), completed, badge);
            } catch (Exception e) {
                return new Customer(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim(), parts[4].trim());
            }
        } else if (parts.length == 5) {
            return new Customer(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim(), parts[4].trim());
        } else if (parts.length == 4) {
            return new Customer(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim(), "");
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;
        Customer customer = (Customer) o;
        return Objects.equals(email, customer.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
