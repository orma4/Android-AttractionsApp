package com.project.attractionsapp;

import java.io.Serializable;

public class Attraction implements Serializable {
    private String name;
    private String address;
    private String url;
    private String id;
    private String price;
    private String availableTickets;

    public Attraction(String name,String address,String url,String price,String availableTickets,String id){
        this.name = name;
        this.address = address;
        this.url = url;
        this.price = price;
        this.availableTickets = availableTickets;
        this.id = id;
    }


    public String getUrl() {
        return url;
    }
    public String getName() {
        return name;
    }
    public String getId() {
        return id;
    }
    public String getAddress() {
        return address;
    }
    public String getPrice() {
        return price;
    }
    public String getAvailableTickets() {
        return availableTickets;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setPrice(String price) {
        this.price = price;
    }
    public void setAvailableTickets(String availableTickets) { this.availableTickets = availableTickets; }
    public void setId(String id) { this.id = id; }

    @Override
    public String toString() {
        return " "+name+"\n" +
                " "+address +"\n" +
                " "+price+ "\n" +
                " "+availableTickets+"\n"+
                " "+url;
    }

    public Attraction(String name, String address, String price, String availableTickets, String url){
        this.name = name;
        this.address = address;
        this.price = price;
        this.availableTickets = availableTickets;
        this.url = url;
    }
}
