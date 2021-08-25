package io.kyberorg.yalsee.test;

import lombok.Data;

@Data
public class TestFlowController {
    private static TestFlowController instance = null;
    private static boolean welcomeExecuted = false;

    public static TestFlowController getInstance() {
        if (instance == null) {
            instance = new TestFlowController();
        }
        return instance;
    }

    public void printWelcome() {
        if (welcomeExecuted) return;

        System.out.println("Testing started");
        welcomeExecuted = true;
    }

    public void addShutdownHook(final Runnable method) {
        Runtime.getRuntime().addShutdownHook(new Thread(method));
    }


}
