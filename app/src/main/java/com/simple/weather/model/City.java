package com.simple.weather.model;


public class City {
    private int id;
    private String name;

    private String code;

    public City(int id,String name,String code){
        this.name = name;
        this.id = id;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
