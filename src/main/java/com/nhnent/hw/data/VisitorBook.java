package com.nhnent.hw.data;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

@Entity
public class VisitorBook {
    
    private static final String EMAIL_PATTERN = 
            "^(([^<>()[\\]\\.,;:\\s@\"]+(\\.[^<>()[\\]\\.,;:\\s@\"]+)*)|(\".+\"))@(([^<>()[\\]\\.,;:\\s@\"]+\\.)+[^<>()[\\]\\.,;:\\s@\"]{2,})$";
    
    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String passwd;
    
    @Column(nullable = false)
    private String content;
    
    @Column(nullable = false)
    private Date createdAt;
    
    @Column(nullable = false)
    private Date modifiedAt;
    
    public VisitorBook() {
    }

    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }
    
    @JsonIgnore
    public String getPasswd() {
        return passwd;
    }

    public String getContent() {
        return content;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    @JsonSetter
    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
    
    public boolean hasValidEmail() {
        return this.email.matches(EMAIL_PATTERN);
    }
    
}
