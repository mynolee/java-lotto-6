package camp.nextstep.edu.missionutils;

import java.util.*;

public class Randoms {

    public static List<Integer> pickUniqueNumbersInRange(int startInclusive, int endInclusive, int count) {
        if (endInclusive - startInclusive + 1 < count) {
            throw new IllegalArgumentException("[ERROR] 범위보다 많은 수를 뽑을 수 없습니다.");
        }

        List<Integer> candidates = new ArrayList<>();
        for (int i = startInclusive; i <= endInclusive; i++) {
            candidates.add(i);
        }

        Collections.shuffle(candidates);
        return new ArrayList<>(candidates.subList(0, count));
    }
}