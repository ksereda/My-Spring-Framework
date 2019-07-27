package com.myspring;

public class Main {

    public static void main(String[] args) {
        Engine engine = new Engine();
        engine.setPower(200);

        Gear gear = new Manual();

        Car car = new Car(engine, gear);
        car.setEngine(engine);
        car.setGear(gear);
    }

}
