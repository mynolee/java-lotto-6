package camp.nextstep.edu.missionutils;

import java.util.Random;

public class Randoms {
    private static final Random RANDOM = new Random();

    public static int pickNumberInRange(int startInclusive, int endInclusive) {
        if (startInclusive > endInclusive) {
            throw new IllegalArgumentException("startInclusive must be <= endInclusive");
        }
        return RANDOM.nextInt(endInclusive - startInclusive + 1) + startInclusive;
    }
}