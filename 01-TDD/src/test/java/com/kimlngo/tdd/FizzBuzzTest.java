package com.kimlngo.tdd;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FizzBuzzTest {
    //if the number is divisible by 3 -> print Fizz
    //if the number is divisible by 5 -> print Buzz
    //if the number is divisible by 3 and 5 -> print FizzBuzz
    //if the number is NOT divisible by 3 or 5 -> print the number

    @Test
    @Order(1)
    @DisplayName("Divisible by 3")
    public void testDivisibleByThree() {
        var expected = "Fizz";
        Assertions.assertEquals(expected, FizzBuzz.compute(3), "Should print Fizz");
    }

    @Test
    @Order(2)
    @DisplayName("Divisible by 5")
    public void testDivisibleByFive() {
        var expected = "Buzz";
        Assertions.assertEquals(expected, FizzBuzz.compute(5), "Should print Buzz");
    }

    @Test
    @Order(3)
    @DisplayName("Divisible by 3 and 5")
    public void testDivisibleByThreeAndFive() {
        var expected = "FizzBuzz";
        Assertions.assertEquals(expected, FizzBuzz.compute(15), "Should print FizzBuzz");
    }

    @Test
    @Order(4)
    @DisplayName("Not Divisible by 3 or 5")
    public void testNotDivisibleByThreeOrFive() {
        var expected = "7";
        Assertions.assertEquals(expected, FizzBuzz.compute(7), "Should print FizzBuzz");
    }

    @DisplayName("Test with Small CSV file")
    @ParameterizedTest(name = "input={0},expected={1}")
    @CsvFileSource(resources = "/small-test-data.csv")
    @Order(5)
    public void testFizzBuzzWithCSVFile_Small(int input, String expected) {
        Assertions.assertEquals(expected, FizzBuzz.compute(input));
    }

    @DisplayName("Test with Medium CSV file")
    @ParameterizedTest(name = "input={0},expected={1}")
    @CsvFileSource(resources = "/medium-test-data.csv")
    @Order(5)
    public void testFizzBuzzWithCSVFile_Medium(int input, String expected) {
        Assertions.assertEquals(expected, FizzBuzz.compute(input));
    }

    @DisplayName("Test with Large CSV file")
    @ParameterizedTest(name = "input={0},expected={1}")
    @CsvFileSource(resources = "/large-test-data.csv")
    @Order(5)
    public void testFizzBuzzWithCSVFile_Large(int input, String expected) {
        Assertions.assertEquals(expected, FizzBuzz.compute(input));
    }
}
