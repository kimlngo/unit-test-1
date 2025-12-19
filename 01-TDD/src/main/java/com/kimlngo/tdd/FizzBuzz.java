package com.kimlngo.tdd;

public class FizzBuzz {

    /**
     * if the number is divisible by 3 -> print Fizz
     * if the number is divisible by 5 -> print Buzz
     * if the number is divisible by 3 and 5 -> print FizzBuzz
     * if the number is NOT divisible by 3 or 5 -> print the number
     *
     * @param number
     * @return: Fizz / Buzz or the number
     */
    public static String compute(int number) {
        //refactored
        StringBuilder sb = new StringBuilder();

        if (number % 3 == 0)
            sb.append("Fizz");

        if (number % 5 == 0)
            sb.append("Buzz");

        if (sb.isEmpty()) {
            sb.append(number);
        }

        return sb.toString();

        /*        if (number % 3 == 0 && number % 5 == 0)
            return "FizzBuzz";

        else if (number % 3 == 0)
            return "Fizz";

        else if (number % 5 == 0)
            return "Buzz";

        return String.valueOf(number);*/
    }
}
