package com.example.easj.canteen.model;

import java.io.Serializable;

public class Dish implements Serializable {
    private int id, energy;
    private double alcohol, carbohydrates, protein, fat, weight, price;
    private String title, description, pictureUrl;

    public Dish() {
        // No-args constructor, needed for serialization
    }

    public Dish(int id, String title, String description, int energy, double alcohol, double carbohydrates, double protein, double fat, double weight, double price, String pictureUrl) {
        this.id = id;
        this.energy = energy;
        this.alcohol = alcohol;
        this.carbohydrates = carbohydrates;
        this.protein = protein;
        this.fat = fat;
        this.weight = weight;
        this.price = price;
        this.title = title;
        this.description = description;
        this.pictureUrl = pictureUrl;
    }

    @Override
    public String toString() {
        return title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public double getAlcohol() {
        return alcohol;
    }

    public void setAlcohol(double alcohol) {
        this.alcohol = alcohol;
    }

    public double getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(double carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }
}
