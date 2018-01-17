package com.melayer.grapper.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * A App.
 */
@Document(collection = "app")
@JsonInclude(JsonInclude.Include.NON_NULL)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "app")
public class App implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @TextIndexed
    @Field("app_name")
    private String appName;

    @NotNull
    @Field("url")
    private String url;

    @NotNull
    @Field("offer")
    private Integer offer;

    @Field("category")
    private String category;

    @Field("date")
    private String date;

    @Field("image")
    private String image;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public App appName(String appName) {
        this.appName = appName;
        return this;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getUrl() {
        return url;
    }

    public App url(String url) {
        this.url = url;
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getOffer() {
        return offer;
    }

    public App offer(Integer offer) {
        this.offer = offer;
        return this;
    }

    public void setOffer(Integer offer) {
        this.offer = offer;
    }

    public String getCategory() {
        return category;
    }

    public App category(String category) {
        this.category = category;
        return this;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public App date(String date) {
        this.date = date;
        return this;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public App image(String image) {
        this.image = image;
        return this;
    }

    public void setImage(String image) {
        this.image = image;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        App app = (App) o;
        if (app.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), app.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "App{" +
            "id=" + getId() +
            ", appName='" + getAppName() + "'" +
            ", url='" + getUrl() + "'" +
            ", offer=" + getOffer() +
            ", category='" + getCategory() + "'" +
            ", date='" + getDate() + "'" +
            ", image='" + getImage() + "'" +
            "}";
    }
}
