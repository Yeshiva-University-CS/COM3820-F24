package edu.yu.parallel;

import java.util.Date;
import java.util.concurrent.Callable;

public class CanteenStaffProducer implements Callable {

    private String staffName;

    public CanteenStaffProducer(String prodName) {
        this.staffName = prodName;
    }

    @Override
    public FoodPlate call() throws Exception {
        // Simulate time required to prepare food plates using Thread sleeps
        System.out.println("Current Canteen Staff at work: " +
                this.staffName + " at " + new Date());
        Thread.sleep(2000L);

        FoodPlate foodPlate = new FoodPlate();
        foodPlate.setBurgerReady(true);
        foodPlate.setPizzaReady(true);
        foodPlate.setOtherJunkReady(true);
        foodPlate.setFoodPlateCreatedBy(this.staffName);

        return foodPlate;
    }
}