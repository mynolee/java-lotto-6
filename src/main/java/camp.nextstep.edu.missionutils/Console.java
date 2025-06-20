package camp.nextstep.edu.missionutils;

import java.util.Scanner;

public class Console {
    private static final Scanner SCANNER = new Scanner(System.in);

    public static String readLine() {
        return SCANNER.nextLine();
    }
}