package lotto;

import camp.nextstep.edu.missionutils.Console;
import camp.nextstep.edu.missionutils.Randoms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.LinkedHashMap;

public class Application {
    private static final int TICKET_PRICE = 1000;

    public static void main(String[] args) {
        try {
            startLottoGame();
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private static void startLottoGame() {
        int purchaseAmount = readPurchaseAmount();
        List<Lotto> lottos = purchaseLottos(purchaseAmount);
        List<Integer> winningNumbers = readWinningNumbers();
        int bonusNumber = readBonusNumber(winningNumbers);
        Map<String, Integer> results = matchLottos(lottos, winningNumbers, bonusNumber);

        Map<String, Integer> prizeTable = new LinkedHashMap<>();
        prizeTable.put("3", 5_000);
        prizeTable.put("4", 50_000);
        prizeTable.put("5", 1_500_000);
        prizeTable.put("5+bonus", 30_000_000);
        prizeTable.put("6", 2_000_000_000);

        printResult(results, purchaseAmount, prizeTable);
    }

    private static int readPurchaseAmount() {
        System.out.println("구입금액을 입력해 주세요.");
        String input = Console.readLine();
        int amount = parseInteger(input);
        if (amount % TICKET_PRICE != 0) {
            throw new IllegalArgumentException("로또 구입 금액은 1,000원 단위여야 합니다.");
        }
        return amount;
    }

    private static int parseInteger(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("숫자를 입력해야 합니다.");
        }
    }

    private static List<Lotto> purchaseLottos(int amount) {
        int count = amount / TICKET_PRICE;
        printPurchaseCount(count);
        List<Lotto> lottos = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            List<Integer> numbers = Randoms.pickUniqueNumbersInRange(1, 45, 6);
            Lotto lotto = new Lotto(numbers);
            lottos.add(lotto);
            System.out.println(lotto);
        }
        return lottos;
    }

    private static void printPurchaseCount(int count) {
        System.out.println(count + "개를 구매했습니다.");
    }

    private static List<Integer> readWinningNumbers() {
        System.out.println("\n당첨 번호를 입력해 주세요.");
        String input = Console.readLine();
        String[] numberStrs = input.split(",");
        if (numberStrs.length != 6) {
            throw new IllegalArgumentException("당첨 번호는 6개여야 합니다.");
        }
        Set<Integer> numbers = new HashSet<>();
        for (String numberStr : numberStrs) {
            int num = parseInteger(numberStr.trim());
            validateNumber(num);
            numbers.add(num);
        }
        if (numbers.size() != 6) {
            throw new IllegalArgumentException("당첨 번호는 중복되지 않아야 합니다.");
        }
        List<Integer> list = new ArrayList<>(numbers);
        Collections.sort(list);
        return list;
    }

    private static int readBonusNumber(List<Integer> winningNumbers) {
        System.out.println("\n보너스 번호를 입력해 주세요.");
        int bonus = parseInteger(Console.readLine());
        validateNumber(bonus);
        if (winningNumbers.contains(bonus)) {
            throw new IllegalArgumentException("보너스 번호는 당첨 번호와 중복되지 않아야 합니다.");
        }
        return bonus;
    }

    private static void validateNumber(int number) {
        if (number < 1 || number > 45) {
            throw new IllegalArgumentException("로또 번호는 1부터 45 사이의 숫자여야 합니다.");
        }
    }

    private static Map<String, Integer> matchLottos(List<Lotto> lottos, List<Integer> winningNumbers, int bonusNumber) {
        Map<String, Integer> result = new LinkedHashMap<>();
        result.put("6", 0);
        result.put("5+bonus", 0);
        result.put("5", 0);
        result.put("4", 0);
        result.put("3", 0);

        for (Lotto lotto : lottos) {
            List<Integer> numbers = lotto.getNumbers();
            int matchCount = calculateMatchCount(numbers, winningNumbers);
            boolean hasBonus = numbers.contains(bonusNumber);

            switch (matchCount) {
                case 6:
                    result.put("6", result.get("6") + 1);
                    break;
                case 5:
                    if (hasBonus) {
                        result.put("5+bonus", result.get("5+bonus") + 1);
                    } else {
                        result.put("5", result.get("5") + 1);
                    }
                    break;
                case 4:
                    result.put("4", result.get("4") + 1);
                    break;
                case 3:
                    result.put("3", result.get("3") + 1);
                    break;
                default:
                    break;
            }
        }
        return result;
    }

    private static int calculateMatchCount(List<Integer> numbers, List<Integer> winningNumbers) {
        int matchCount = 0;
        for (int num : numbers) {
            if (winningNumbers.contains(num)) {
                matchCount++;
            }
        }
        return matchCount;
    }

    private static void printResult(Map<String, Integer> result, int purchaseAmount, Map<String, Integer> prizeTable) {
        System.out.println("\n당첨 통계\n---");

        printStatistics(result, prizeTable);

        int total = calculateTotalPrize(result, prizeTable);
        double rate = (double) total / purchaseAmount * 100;
        System.out.printf("총 수익률은 %.1f%%입니다.%n", rate);
    }

    private static void printStatistics(Map<String, Integer> result, Map<String, Integer> prizeTable) {
        for (Map.Entry<String, Integer> entry : prizeTable.entrySet()) {
            String key = entry.getKey();
            int prize = entry.getValue();
            String message = getMatchMessage(key, prize);
            System.out.printf("%s - %d개%n", message, result.getOrDefault(key, 0));
        }
    }

    private static int calculateTotalPrize(Map<String, Integer> result, Map<String, Integer> prizeTable) {
        int total = 0;
        for (String key : prizeTable.keySet()) {
            total += result.getOrDefault(key, 0) * prizeTable.get(key);
        }
        return total;
    }

    private static String getMatchMessage(String key, int prize) {
        return switch (key) {
            case "3" -> "3개 일치 (" + formatMoney(prize) + "원)";
            case "4" -> "4개 일치 (" + formatMoney(prize) + "원)";
            case "5" -> "5개 일치 (" + formatMoney(prize) + "원)";
            case "5+bonus" -> "5개 일치, 보너스 볼 일치 (" + formatMoney(prize) + "원)";
            case "6" -> "6개 일치 (" + formatMoney(prize) + "원)";
            default -> "";
        };
    }

    private static String formatMoney(int amount) {
        return String.format("%,d", amount);
    }

}
