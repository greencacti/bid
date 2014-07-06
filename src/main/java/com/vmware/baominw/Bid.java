package com.vmware.baominw;

import com.vmware.baominw.utils.ImageUtil;
import com.vmware.baominw.utils.RobotUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by wanghuaiyu on 2014/7/6.
 */
public class Bid {
    private static int LONG_DELAY = 1000;

    private static int SHORT_DELAY = 100;

    private static Robot robot;

    public static void main(String[] args) throws Exception {
        // create a global robot;
        robot = new Robot();

        // login the Bid system
        login();

        // first round bid
        firstRoundBid();
    }

    private static void login() throws Exception {
        Runtime.getRuntime().exec("D:\\投标拍卖模拟\\Ge201401\\Demo.exe");

        // verify the hardware
        robot.delay(LONG_DELAY);
        robot.mouseMove(430, 330);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
        robot.mouseMove(680, 495);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);

        // get the security code
        String securityCode = getSecurityCode(922, 458, 110, 30);
        while(securityCode == null || securityCode.equals("") || !isValid(securityCode)) {
            securityCode = getSecurityCode(922, 458, 110, 30);
        }

        // input the username and the password
        RobotUtil.pressKeyString(robot, "52313733");
        RobotUtil.pressKey(robot, '\t');
        RobotUtil.pressKeyString(robot, "2378");
        RobotUtil.pressKey(robot, '\t');
        RobotUtil.pressKeyString(robot, securityCode);

        // join the bid
        robot.mouseMove(860, 545);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
    }

    private static void firstRoundBid() throws Exception {
        // enter the price
        robot.delay(LONG_DELAY);
        RobotUtil.pressKeyString(robot, "100");
        robot.delay(SHORT_DELAY);
        RobotUtil.pressKey(robot, '\t');
        robot.delay(SHORT_DELAY);
        RobotUtil.pressKeyString(robot, "100");

        // issue the price
        robot.mouseMove(1040, 410);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);

        // get the security code
        String securityCode = getSecurityCode(730, 395, 110, 30);
        while(securityCode == null || securityCode.equals("") || !isValid(securityCode)) {
            // cancel the input
            robot.mouseMove(785, 495);
            robot.mousePress(InputEvent.BUTTON1_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_MASK);

            // reissue the price
            robot.mouseMove(1040, 410);
            robot.mousePress(InputEvent.BUTTON1_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_MASK);

            // get th security code
            securityCode = getSecurityCode(730, 395, 110, 30);
        }

        // set the security code
        RobotUtil.pressKeyString(robot, securityCode);
    }

    private static void execute(String command) throws Exception {
        Process process = java.lang.Runtime.getRuntime().exec(command);
        try {
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void capture(int x, int y, int width, int height, File file) throws Exception  {
        BufferedImage caputuredImage = robot.createScreenCapture(new Rectangle(x, y, width, height));
        BufferedImage monochromeImage = ImageUtil.handleImage(caputuredImage);
        ImageIO.write(monochromeImage, "bmp", file);
    }

    private static String getSecurityCode(int x, int y, int width, int height) throws Exception {
        // delete the old image file and result file
        File oldSecurityCodeFile = new File("E:\\Bid\\SecurityCode.bmp");
        if(oldSecurityCodeFile.exists()) {
            oldSecurityCodeFile.delete();
        }

        File oldSecurityCodeResultFile = new File("E:\\Bid\\SecurityCodeResult.txt");
        if(oldSecurityCodeResultFile.exists()) {
            oldSecurityCodeResultFile.delete();
        }

        // capture the security code
        capture(x, y, width, height, new File("E:\\Bid\\SecurityCode.bmp"));

        // analyse the security code
        execute("c:\\Program Files\\Tesseract-OCR\\tesseract.exe" +
                " E:\\Bid\\SecurityCode.bmp E:\\Bid\\SecurityCodeResult");

        // read the security code
        robot.delay(SHORT_DELAY);
        BufferedReader reader = new BufferedReader(new FileReader("E:\\Bid\\SecurityCodeResult.txt"));
        String securityCode = reader.readLine();

        return  securityCode;
    }

    private static boolean isValid(String securityCode) {
        if(securityCode.length() != 6) {
            return false;
        }

        for(int i=0; i< securityCode.length(); i++) {
            char ch = securityCode.charAt(i);
            if(ch < '0' || ch > '9') {
                return false;
            }
        }

        return true;
    }
}
