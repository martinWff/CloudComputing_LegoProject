package cc.srv.db.dataconstructor;

import java.util.UUID;

public class LegoSetModel {
    private String id;
    private String name;

    private String description;

    private int yearOfProduction;

    public LegoSetModel() {

    }


    public LegoSetModel(String productId,String name,String description,int year) {
        this.id = productId;
        this.name = name;
        this.description = description;
        this.yearOfProduction = year;
    }

    public LegoSetModel(String name,String description,int year) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.yearOfProduction = year;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
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

    public int getYearOfProduction() {
        return yearOfProduction;
    }

    public void setYearOfProduction(int yearOfProduction) {
        this.yearOfProduction = yearOfProduction;
    }
}
