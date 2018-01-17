package com.melayer.grapper.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * A User.
 */
@Document(collection = "user")
@JsonInclude(JsonInclude.Include.NON_NULL)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("name")
    private String name;

    @Field("email_id")
    private String emailId;


    @Field("mobile_no")
    private String mobileNo;

    @Field("password")
    private String password;

    @Field("date")
    private String date;

    @Field("installedApps")
    private List<String> installedApps;

    @Field("uninstalledApps")
    private List<String> uninstalledApps;

    @Field("skippedApps")
    private List<String> skippedApps;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public User name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailId() {
        return emailId;
    }

    public User emailId(String emailId) {
        this.emailId = emailId;
        return this;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public User mobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
        return this;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getPassword() {
        return password;
    }

    public User password(String password) {
        this.password = password;
        return this;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDate() {
        return date;
    }

    public User date(String date) {
        this.date = date;
        return this;
    }

    public void setDate(String date) {
        this.date = date;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove


    public List<String> getInstalledApps() {
        return installedApps;
    }

    public void setInstalledApps(List<String> installedApps) {
        this.installedApps = installedApps;
    }

    public List<String> getUninstalledApps() {
        return uninstalledApps;
    }

    public void setUninstalledApps(List<String> uninstalledApps) {
        this.uninstalledApps = uninstalledApps;
    }

    public List<String> getSkippedApps() {
        return skippedApps;
    }

    public void setSkippedApps(List<String> skippedApps) {
        this.skippedApps = skippedApps;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        if (user.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "User{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", emailId='" + getEmailId() + "'" +
            ", mobileNo='" + getMobileNo() + "'" +
            ", password='" + getPassword() + "'" +
            ", date='" + getDate() + "'" +
            "}";
    }
}
