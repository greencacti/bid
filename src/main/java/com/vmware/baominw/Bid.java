package com.vmware.baominw;

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
    }

    private static void login() throws Exception {
        Runtime.getRuntime().exec("E:\\Bid\\NetBidClient.exe");

        // verify the hardware
        robot.delay(LONG_DELAY);
        robot.mouseMove(430, 330);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
        robot.mouseMove(680, 495);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);

        // capture the security code
        capture(922, 458, 110, 30, new File("E:\\Bid\\SecurityCode.bmp"));

        // analyse the security code
        execute("c:\\Program Files\\Tesseract-OCR\\tesseract.exe" +
                " E:\\Bid\\SecurityCode.bmp E:\\Bid\\SecurityCodeResult");

        // read the security code
        BufferedReader reader = new BufferedReader(new FileReader("E:\\Bid\\SecurityCodeResult.txt"));
        String securityCode = reader.readLine();

        // input the username and the password
        RobotUtil.pressKeyString(robot, "52313733");
        RobotUtil.pressKey(robot, '\t');
        RobotUtil.pressKeyString(robot, "2378");
        RobotUtil.pressKey(robot, '\t');
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
        BufferedImage monochromeImage = generateSingleColorBitMap(caputuredImage);
        ImageIO.write(monochromeImage, "bmp", file);
    }

    private static BufferedImage generateSingleColorBitMap(BufferedImage caputuredImage) {
        BufferedImage monochromeImage = new BufferedImage(caputuredImage.getWidth(),
                                                 caputuredImage.getHeight(),
                                                 BufferedImage.TYPE_BYTE_BINARY);
        monochromeImage.createGraphics().drawImage(caputuredImage, 0, 0, null);
        return monochromeImage;
    }
}
