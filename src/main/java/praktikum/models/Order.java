package models;

public class Order {
    private String[] ingredients;
    private String accessToken;

    public String[] getIngredients() {
        return ingredients;
    }

    public Order setIngredients(String[] ingredients) {
        this.ingredients = ingredients;
        return this;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public Order setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }
}