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
import java.util.Calendar;
import java.util.Date;

/**
 * Created by wanghuaiyu on 2014/7/6.
 */
public class Bid {
    private static int LONG_DELAY = 2000;

    private static int SHORT_DELAY = 300;

    private static String PRICE_ISSUE_TIME = "54:55";

    private static int priceIssueMinute;

    private static int priceIssueSecond;

    static {
        String[] segments= PRICE_ISSUE_TIME.split(":");
        priceIssueMinute = Integer.parseInt(segments[0]);
        priceIssueSecond = Integer.parseInt(segments[1]);
    }

    private static Robot robot;

    public static void main(String[] args) throws Exception {
        // create a global robot;
        robot = new Robot();

        // login the Bid system
        login();

        // first round bid
        firstRoundBid();

        // second round bid
        secondRoundBid();
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
        robot.delay(SHORT_DELAY);

        // get the security code
        String securityCode = getScreenText(919, 460, 110, 30);
        while(securityCode == null || securityCode.equals("") || !isValidSecurityCode(securityCode)) {
            securityCode = getScreenText(919, 460, 110, 30);
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
        robot.delay(SHORT_DELAY);
        sleep(120);
        RobotUtil.pressKeyString(robot, "100");
        RobotUtil.pressKey(robot, '\t');
        RobotUtil.pressKeyString(robot, "100");

        // issue the price
        robot.mouseMove(1040, 410);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);

        // get the security code
        robot.delay(SHORT_DELAY);
        String securityCode = getScreenText(728, 393, 110, 30);
        while(securityCode == null || securityCode.equals("") || !isValidSecurityCode(securityCode)) {
            // cancel the input
            robot.mouseMove(785, 495);
            robot.mousePress(InputEvent.BUTTON1_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_MASK);

            // reissue the price
            robot.mouseMove(1040, 410);
            robot.mousePress(InputEvent.BUTTON1_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_MASK);
            robot.delay(SHORT_DELAY);

            // get th security code
            securityCode = getScreenText(728, 393, 110, 30);
        }

        // set the security code
        RobotUtil.pressKeyString(robot, securityCode);

        // confirm the price issue
        robot.mouseMove(610, 495);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);

        // confirm the successful prompt
        robot.delay(SHORT_DELAY);
        robot.mouseMove(697, 495);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
    }

    private static void secondRoundBid() throws Exception {
        // check the minute
        int minute = 0;
        while(true) {
            Calendar now = Calendar.getInstance();
            minute = now.get(Calendar.MINUTE);
            if(minute >= priceIssueMinute) {
                break;
            }

            sleep(30);
        }
        System.out.print(minute + ":");

        // check the second
        int second = 0;
        while(true) {
            Calendar now = Calendar.getInstance();
            second = now.get(Calendar.SECOND);
            if(second >= priceIssueSecond) {
                break;
            }

            robot.delay(SHORT_DELAY);
        }
        System.out.print(second);

        // enter the price
        robot.mouseMove(875, 390);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);

        // issue the price
        robot.delay(SHORT_DELAY);
        robot.mouseMove(1035, 450);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);

        // get the security code
        robot.delay(SHORT_DELAY);
        String securityCode = getScreenText(728, 393, 110, 30);
        while(securityCode == null || securityCode.equals("") || !isValidSecurityCode(securityCode)) {
            // cancel the input
            robot.mouseMove(785, 495);
            robot.mousePress(InputEvent.BUTTON1_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_MASK);

            // reissue the price
            robot.mouseMove(1035, 450);
            robot.mousePress(InputEvent.BUTTON1_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_MASK);

            // get th security code
            robot.delay(SHORT_DELAY);
            securityCode = getScreenText(728, 393, 110, 30);
        }

        // set the security code
        RobotUtil.pressKeyString(robot, securityCode);

        // confirm the price issue
        robot.mouseMove(610, 495);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);

        // confirm the successful prompt
        robot.delay(SHORT_DELAY);
        robot.mouseMove(697, 495);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
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

    private static String getScreenText(int x, int y, int width, int height) throws Exception {
        // delete the old image file and result file
        File oldScreenTextFile = new File("E:\\Bid\\ScreenText.bmp");
        if(oldScreenTextFile.exists()) {
            oldScreenTextFile.delete();
        }

        File oldScreenTextResultFile = new File("E:\\Bid\\ScreenTextResult.txt");
        if(oldScreenTextResultFile.exists()) {
            oldScreenTextResultFile.delete();
        }

        // capture the security code
        capture(x, y, width, height, new File("E:\\Bid\\ScreenText.bmp"));

        // analyse the security code
        execute("c:\\Program Files\\Tesseract-OCR\\tesseract.exe" +
                " E:\\Bid\\ScreenText.bmp E:\\Bid\\ScreenTextResult -psm 6 digits");

        // read the security code
        robot.delay(SHORT_DELAY);
        BufferedReader reader = new BufferedReader(new FileReader("E:\\Bid\\ScreenTextResult.txt"));
        String screenText = reader.readLine();

        return  screenText;
    }

    private static boolean isValidSecurityCode(String securityCode) {
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

    private static void sleep(int seconds) {
        try{
            Thread.sleep(seconds * 1000);
        } catch(Exception e)
        {

        }
    }
}
