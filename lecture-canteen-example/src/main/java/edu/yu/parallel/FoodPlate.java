package edu.yu.parallel;

public class FoodPlate {

    private boolean isPizzaReady;
    private boolean isBurgerReady;
    private boolean isOtherJunkReady;
    private String foodPlateCreatedBy;

    public String getFoodPlateCreatedBy() {
        return foodPlateCreatedBy;
    }

    public void setFoodPlateCreatedBy(String foodPlateCreatedBy) {
        this.foodPlateCreatedBy = foodPlateCreatedBy;
    }

    public boolean isPizzaReady() {
        return isPizzaReady;
    }

    public void setPizzaReady(boolean isPizzaReady) {
        this.isPizzaReady = isPizzaReady;
    }

    public boolean isBurgerReady() {
        return isBurgerReady;
    }

    public void setBurgerReady(boolean isBurgerReady) {
        this.isBurgerReady = isBurgerReady;
    }

    public boolean isOtherJunkReady() {
        return isOtherJunkReady;
    }

    public void setOtherJunkReady(boolean isOtherJunkReady) {
        this.isOtherJunkReady = isOtherJunkReady;
    }

}
