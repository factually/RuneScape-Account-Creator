package data;

import java.util.ArrayList;

public class Account {

    private String firstPart, email, password;
    private int number;
    private boolean active = true;

    public Account(String firstPart, int number, String email, String password) {
        super();
        this.firstPart = firstPart;
        this.number = number;
        this.email = email;
        this.password = password;
    }

    public String getFirstPart() {
        return firstPart;
    }

    public void setFirstPart(String firstPart) {
        this.firstPart = firstPart;
    }

    public int getNumber() { return number; }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword(String password) { return password; }

    public void setPassword(String password) { this.password = password; }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
