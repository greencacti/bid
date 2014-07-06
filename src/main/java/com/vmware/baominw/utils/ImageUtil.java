package com.vmware.baominw.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by wanghuaiyu on 2014/7/6.
 */
public class ImageUtil {
    public static void main(String[] args) throws Exception {
        File testDataDir = new File("E:\\Bid\\SecurityCode1.bmp");
        final String destDir = testDataDir.getAbsolutePath()+".tmp";
        //cleanImage(testDataDir, destDir);
    }

    public static BufferedImage handleImage(BufferedImage caputuredImage) {
        // transform to monochrome image
        BufferedImage monochromeImage = generateSingleColorBitMap(caputuredImage);

        // clean the image
        BufferedImage cleanImage = cleanImage(monochromeImage);

        return cleanImage;
    }

    private static BufferedImage generateSingleColorBitMap(BufferedImage caputuredImage) {
        BufferedImage monochromeImage = new BufferedImage(caputuredImage.getWidth(),
                caputuredImage.getHeight(),
                BufferedImage.TYPE_BYTE_BINARY);
        monochromeImage.createGraphics().drawImage(caputuredImage, 0, 0, null);
        return monochromeImage;
    }

    private static BufferedImage cleanImage(BufferedImage image) {
        int weight = image.getWidth();
        int height = image.getHeight();

        // remove the vertical disturb line
        for (int x=0; x < weight; x++) {
            if(isBlack(image.getRGB(x,1)) &&isBlack(image.getRGB(x,2)) ) {
                for (int y = 0; y < height; y++) {
                    image.setRGB(x, y, 0xFFFF);
                }
            }
        }

        // remove the horizontal disturb line
        for (int y=0; y < height; y++) {
            if(isBlack(image.getRGB(1,y)) &&isBlack(image.getRGB(2,y)) ) {
                for (int x = 0; x < weight; x++) {
                    image.setRGB(x, y, 0xFFFF);
                }
            }
        }

        return image;
    }

    public static void cleanImage1(File sfile, String destDir)  throws Exception {
        File destF = new File(destDir);
        if (!destF.exists()) {
            destF.mkdirs();
        }

        BufferedImage bufferedImage = ImageIO.read(sfile);
        int h = bufferedImage.getHeight();
        int w = bufferedImage.getWidth();

        // 灰度化
        int[][] gray = new int[w][h];
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int argb = bufferedImage.getRGB(x, y);
                // 图像加亮（调整亮度识别率非常高）
                int r = (int) (((argb >> 16) & 0xFF) * 1.1 + 30);
                int g = (int) (((argb >> 8) & 0xFF) * 1.1 + 30);
                int b = (int) (((argb >> 0) & 0xFF) * 1.1 + 30);
                if (r >= 255) {
                    r = 255;
                }
                if (g >= 255) {
                    g = 255;
                }
                if (b >= 255) {
                    b = 255;
                }
                gray[x][y] = (int) Math
                        .pow((Math.pow(r, 2.2) * 0.2973 + Math.pow(g, 2.2)
                                * 0.6274 + Math.pow(b, 2.2) * 0.0753), 1 / 2.2);
            }
        }

        // 二值化
        int threshold = ostu(gray, w, h);
        BufferedImage binaryBufferedImage = new BufferedImage(w, h,
                BufferedImage.TYPE_BYTE_BINARY);
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (gray[x][y] > threshold) {
                    gray[x][y] |= 0x00FFFF;
                } else {
                    gray[x][y] &= 0xFF0000;
                }
                binaryBufferedImage.setRGB(x, y, gray[x][y]);
            }
        }

        // remove the disturb line
        for (int x=0; x < w; x++) {
            if(isBlack(binaryBufferedImage.getRGB(x,1)) &&isBlack(binaryBufferedImage.getRGB(x,2)) ) {
                for (int y = 0; y < h; y++) {
                    binaryBufferedImage.setRGB(x, y, 0xFFFF);
                }
            }
        }

        // 矩阵打印
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (isBlack(binaryBufferedImage.getRGB(x, y))) {
                    System.out.print("*");
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }

        ImageIO.write(binaryBufferedImage, "jpg", new File(destDir, sfile
                .getName()));
    }

    public static boolean isBlack(int colorInt) {
        Color color = new Color(colorInt);
        if (color.getRed() + color.getGreen() + color.getBlue() <= 300) {
            return true;
        }
        return false;
    }

    public static boolean isWhite(int colorInt) {
        Color color = new Color(colorInt);
        if (color.getRed() + color.getGreen() + color.getBlue() > 300) {
            return true;
        }
        return false;
    }

    public static int isBlackOrWhite(int colorInt) {
        if (getColorBright(colorInt) < 30 || getColorBright(colorInt) > 730) {
            return 1;
        }
        return 0;
    }

    public static int getColorBright(int colorInt) {
        Color color = new Color(colorInt);
        return color.getRed() + color.getGreen() + color.getBlue();
    }

    public static int ostu(int[][] gray, int w, int h) {
        int[] histData = new int[w * h];
        // Calculate histogram
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int red = 0xFF & gray[x][y];
                histData[red]++;
            }
        }

        // Total number of pixels
        int total = w * h;

        float sum = 0;
        for (int t = 0; t < 256; t++)
            sum += t * histData[t];

        float sumB = 0;
        int wB = 0;
        int wF = 0;

        float varMax = 0;
        int threshold = 0;

        for (int t = 0; t < 256; t++) {
            wB += histData[t]; // Weight Background
            if (wB == 0)
                continue;

            wF = total - wB; // Weight Foreground
            if (wF == 0)
                break;

            sumB += (float) (t * histData[t]);

            float mB = sumB / wB; // Mean Background
            float mF = (sum - sumB) / wF; // Mean Foreground

            // Calculate Between Class Variance
            float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);

            // Check if new maximum found
            if (varBetween > varMax) {
                varMax = varBetween;
                threshold = t;
            }
        }

        return threshold;
    }
}
