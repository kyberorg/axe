package io.kyberorg.yalsee.test;

import lombok.Data;

@Data
public class YalseeTest {
    private static YalseeTest instance = null;
    private static boolean welcomeExecuted = false;

    public String testString;

    public static YalseeTest getInstance() {
        if (instance == null) {
            instance = new YalseeTest();
        }
        return instance;
    }

    public void printWelcome() {
        if (welcomeExecuted) return;

        System.out.println("Testing started");
        welcomeExecuted = true;
    }
}
