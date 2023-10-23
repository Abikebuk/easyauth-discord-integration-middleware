package com.abikebuk;

public class Util {

    public static String getRandomNumericPassword(int size){
        if (size < 1) throw new IllegalArgumentException("Password size must be higher than 1");
        return String.format(
                "%0" + size + "d",
                (int) Math.round(Math.random() * Math.pow(10, size))
        );
    }
}
