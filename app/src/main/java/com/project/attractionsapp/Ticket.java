package com.project.attractionsapp;

import java.io.Serializable;

public class Ticket  implements Serializable {
    private String name;
    private String date;
    private String attraction;
    private String totalPrice;
    private String numberOfPeople;


    public String getAttraction() {
        return attraction;
    }
    public String getNumberOfPeople() {
        return numberOfPeople;
    }
    public String getDate() { return date; }
    public String getName() {
        return name;
    }
    public String getTotalPrice() {
        return totalPrice;
    }

    public void setAttraction(String attraction) {
        this.attraction = attraction;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }
    public void setNumberOfPeople(String numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    @Override
    public String toString() {
        return " "+attraction+"\n" +
                " "+name+"\n" +
                " "+date +"\n" +
                " "+totalPrice+ "\n" +
                " "+numberOfPeople+"\n";
    }

    public Ticket( String name,String attraction, String date, String totalPrice, String numberOfPeople){
        this.attraction = attraction;
        this.name = name;
        this.date = date;
        this.totalPrice = totalPrice;
        this.numberOfPeople = numberOfPeople;
    }
}
