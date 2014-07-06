package com.vmware.baominw.utils;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Created by baominw on 3/7/2014.
 */
public class RobotUtil {
    public static void pressKey(Robot robot, int keyvalue) {
        robot.keyPress(keyvalue);
        robot.keyRelease(keyvalue);
    }

    public static void pressKeyWithShift(Robot robot, int keyvalue) {
        robot.keyPress(KeyEvent.VK_SHIFT);
        robot.keyPress(keyvalue);
        robot.keyRelease(keyvalue);
        robot.keyRelease(KeyEvent.VK_SHIFT);
    }

    public static void pressKeyString(Robot robot, String value) {
        for (int i = 0; i < value.length(); i++) {
            pressKey(robot, value.charAt(i));
        }
    }

    public static void closeApplication(Robot robot) {
        robot.keyPress(KeyEvent.VK_ALT);
        robot.keyPress(KeyEvent.VK_F4);
        robot.keyRelease(KeyEvent.VK_ALT);
        robot.keyRelease(KeyEvent.VK_F4);
        robot.keyPress(KeyEvent.VK_N);
        robot.keyRelease(KeyEvent.VK_N);
    }
}
