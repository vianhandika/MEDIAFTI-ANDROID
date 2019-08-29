package com.PBPProject.mediafti;

public class UserDAO {
    String username,email,password,role;

    public UserDAO(){

    }

    public UserDAO(String username, String email, String password,String role) {
        this.role=role;
        this.username = username;
        this.email = email;
        this.password = password;
    }


    public String getRole() {
        return role;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
