package com.example.puppiesapp.models;

public class ModelUser {
    String name, email,phone, image, cover,uidemail,onlineStatus;

    public ModelUser() {
    }

    public ModelUser(String name, String email, String phone, String image, String cover, String uidemail, String onlineStatus) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.image = image;
        this.cover = cover;
        this.uidemail = uidemail;
        this.onlineStatus = onlineStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getUidemail() {
        return uidemail;
    }

    public void setUidemail(String uidemail) {
        this.uidemail = uidemail;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }
}
