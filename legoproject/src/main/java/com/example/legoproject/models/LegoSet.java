package com.example.legoproject.models;

import com.example.legoproject.TimestampSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.Instant;

public class LegoSet {

    private String id;
    private String name;
    private String description;
    private String[] photos;

    private int yearOfProduction;

    public LegoSet() {

    }

    public LegoSet(String id,String name,String description,int yearOfProduction,String[] photos) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.yearOfProduction = yearOfProduction;
        this.photos = photos;
    }

    public LegoSet(String id,String name,String description,int yearOfProduction,String photo) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.yearOfProduction = yearOfProduction;
        this.photos = new String[] {photo};
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getPhotos() {
        return photos;
    }

    public void setPhotos(String[] photos) {
        this.photos = photos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getYearOfProduction() {
        return yearOfProduction;
    }

    public void setYearOfProduction(int yearOfProduction) {
        this.yearOfProduction = yearOfProduction;
    }
}
