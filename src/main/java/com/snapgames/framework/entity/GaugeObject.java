package com.snapgames.framework.entity;

public class GaugeObject extends Entity<GaugeObject> {

    private double maxValue = 100;
    private double minValue = 0;
    private double value = 0;


    public GaugeObject(String name) {
        super(name);
    }

    public GaugeObject setMaxValue(double maxV) {
        this.maxValue = maxV;
        return this;
    }

    public GaugeObject setMinValue(double minV) {
        this.minValue = minV;
        return this;
    }

    public double getMinValue() {
        return this.minValue;
    }

    public double getMaxValue() {
        return this.maxValue;
    }

    public GaugeObject setValue(double v) {
        this.value = v;
        return this;
    }

    public double getValue() {
        return this.value;
    }
}
