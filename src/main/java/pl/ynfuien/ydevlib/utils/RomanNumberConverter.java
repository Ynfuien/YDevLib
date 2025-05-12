package pl.ynfuien.ydevlib.utils;

public class RomanNumberConverter {
    public static String toRoman(int number) {
        StringBuilder result = new StringBuilder();

        int[] num = {1, 4, 5, 9, 10, 40, 50, 90, 100, 400, 500, 900, 1000};
        String[] sym = {"I", "IV", "V", "IX", "X", "XL", "L", "XC", "C", "CD", "D", "CM", "M"};

        int i = 12;
        while (number > 0) {
            int div = number / num[i];
            number = number % num[i];

            while (div-- > 0) {
                result.append(sym[i]);
            }

            i--;
        }

        return result.toString();
    }
}
