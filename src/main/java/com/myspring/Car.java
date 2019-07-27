package com.myspring;

public class Car {
    private Engine engine;
    private Gear gear;

    public Car(Engine engine, Gear gear) {
        this.engine = engine;
        this.gear = gear;
    }

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public Gear getGear() {
        return gear;
    }

    public void setGear(Gear gear) {
        this.gear = gear;
    }
}
