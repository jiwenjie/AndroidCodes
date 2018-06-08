package com.kuky.restframework;

import java.util.List;

/**
 * @author kuky.
 */

public class User {

    /**
     * id : 4
     * last_login : null
     * is_superuser : false
     * username : smile_xg
     * first_name :
     * last_name :
     * email :
     * is_staff : false
     * is_active : true
     * date_joined : 2018-04-12T21:59:48.630435+08:00
     * phone_num : 18796006988
     * qq : null
     * we_chat : null
     * avatar : http://192.168.0.103:8080/media/C%3A/ProjectsFloder/PythonProjects/expert/expert_project/media/avatar/smile_xg/1523628340.jpg
     * groups : []
     * user_permissions : []
     */

    private int id;
    private Object last_login;
    private boolean is_superuser;
    private String username;
    private String first_name;
    private String last_name;
    private String email;
    private boolean is_staff;
    private boolean is_active;
    private String date_joined;
    private String phone_num;
    private Object qq;
    private Object we_chat;
    private String avatar;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Object getLast_login() {
        return last_login;
    }

    public void setLast_login(Object last_login) {
        this.last_login = last_login;
    }

    public boolean isIs_superuser() {
        return is_superuser;
    }

    public void setIs_superuser(boolean is_superuser) {
        this.is_superuser = is_superuser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isIs_staff() {
        return is_staff;
    }

    public void setIs_staff(boolean is_staff) {
        this.is_staff = is_staff;
    }

    public boolean isIs_active() {
        return is_active;
    }

    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }

    public String getDate_joined() {
        return date_joined;
    }

    public void setDate_joined(String date_joined) {
        this.date_joined = date_joined;
    }

    public String getPhone_num() {
        return phone_num;
    }

    public void setPhone_num(String phone_num) {
        this.phone_num = phone_num;
    }

    public Object getQq() {
        return qq;
    }

    public void setQq(Object qq) {
        this.qq = qq;
    }

    public Object getWe_chat() {
        return we_chat;
    }

    public void setWe_chat(Object we_chat) {
        this.we_chat = we_chat;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
