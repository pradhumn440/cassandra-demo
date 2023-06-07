package com.springcassandra.cassandrademo.designPatterns;

abstract class Plan {
    protected double rate;
    abstract void getRate();

    public double calculateBill(int units) {
        return units*rate;
    }
}
