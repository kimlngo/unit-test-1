package com.kimlngo.tdd;

import java.util.stream.IntStream;

public class MainApp {
    public static void main(String[] args) {
        IntStream.range(0, 101)
                 .forEach(i -> System.out.println(FizzBuzz.compute(i)));
    }
}
