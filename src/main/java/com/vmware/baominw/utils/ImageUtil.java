package com.vmware.baominw.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by wanghuaiyu on 2014/7/6.
 */
public class ImageUtil {
    public static BufferedImage handleImage(BufferedImage image) throws Exception {
        ImageIO.write(image, "bmp", new File("E:\\Bid\\old.bmp"));
        // clean the image
        image = cleanImage(image);

        // transform to monochrome image
        BufferedImage monochromeImage = generateSingleColorBitMap(image);

        return monochromeImage;
    }

    public static BufferedImage generateSingleColorBitMap(BufferedImage caputuredImage) {
        BufferedImage monochromeImage = new BufferedImage(caputuredImage.getWidth(),
                caputuredImage.getHeight(),
                BufferedImage.TYPE_BYTE_BINARY);
        monochromeImage.createGraphics().drawImage(caputuredImage, 0, 0, null);
        return monochromeImage;
    }

    public static BufferedImage cleanImage(BufferedImage image) {
        int weight = image.getWidth();
        int height = image.getHeight();
        int x;
        int y;
        int k;
        int disturbColor = 0;
        int matchedX1 = 0;
        int matchedX2 = 0;
        int matchedY1 = 0;
        int matchedY2 = 0;
        int delta = 0;
        int count = 0;

        // remove the vertical disturb line
        count = 0;
        boolean verticalLineFound = false;
        for (x = 0; x < weight/4; x++) {
            int PointX1 = image.getRGB(x, 1);
            int PointX2 = image.getRGB(x, 2);
            int PointX3 = image.getRGB(x, 3);
            if ((PointX1 != 0xFFFFFFFF) && ((PointX1 == PointX2) || (PointX1 == PointX3)) ||
                    (PointX2 != 0xFFFFFFFF) && ((PointX2 == PointX1) || (PointX2 == PointX3))) {
                count++;
                if (count == 1) {
                    matchedX1 = x;
                } else {
                    matchedX2 = x;
                }
            }

            if (count == 2) {
                if (PointX1 != 0xFFFFFFFF) {
                    disturbColor = PointX1;
                } else {
                    disturbColor = PointX2;
                }
                verticalLineFound = true;
                break;
            }
        }

        if(verticalLineFound) {
            delta = matchedX2 - matchedX1;
            for(x = matchedX1; x < weight; x += delta) {
                for (y = 0; y < height; y++) {
                    if (image.getRGB(x, y) == disturbColor) {
                        image.setRGB(x, y, 0xFFFFFFFF);
                    }
                }
            }
        }

        // remove the horizontal disturb line
        count = 0;
        boolean horizontalLineFound = false;
        for (y = 0; y < height; y++) {
            int Point1Y = image.getRGB(1, y);
            int Point2Y = image.getRGB(2, y);
            int Point3Y = image.getRGB(3, y);
            if ((Point1Y != 0xFFFFFFFF) && ((Point1Y == Point2Y) || (Point1Y == Point3Y)) ||
                    (Point2Y != 0xFFFFFFFF) && ((Point2Y == Point1Y) || (Point2Y == Point3Y))) {
                count++;
                if (count == 1) {
                    matchedY1 = y;
                } else {
                    matchedY2 = y;
                }
            }

            if (count == 2) {
                if (Point1Y != 0xFFFFFFFF) {
                    disturbColor = Point1Y;
                } else {
                    disturbColor = Point2Y;
                }

                horizontalLineFound = true;
                break;
            }
        }

        if(horizontalLineFound) {
            delta = matchedY2 - matchedY1;
            for(y = matchedY1; y < height; y += delta) {
                for (x = 0; x < weight; x++) {
                    if (image.getRGB(x, y) == disturbColor) {
                        image.setRGB(x, y, 0xFFFFFFFF);
                    }
                }
            }
        }

        // remove the right disturb line
        count = 0;
        boolean rightDisturbLineFound = false;
        for (x = 1; x < weight/4; x++) {
            int PointX0 = image.getRGB(x, 0);
            int PointXminus1Yplus1 = image.getRGB(x - 1, 1);
            if ((PointX0 != 0xFFFFFFFF) && (PointX0 == PointXminus1Yplus1)) {
                count++;
                if (count == 1) {
                    matchedX1 = x;
                } else {
                    matchedX2 = x;
                }
            }

            if (count == 2) {
                disturbColor = PointX0;
                rightDisturbLineFound = true;
                break;
            }
        }

        if (rightDisturbLineFound) {
            delta = matchedX2 - matchedX1;
            for (x = matchedX1; x < (weight + height); x += delta) {
                for (k = x; (k >= 0) && (x - k < height); k--) {
                    if ((k < weight) && (image.getRGB(k, x - k) == disturbColor)) {
                        image.setRGB(k, x - k, 0xFFFFFFFF);
                    }
                }
            }
        }

        // remove the left disturb line
        count = 0;
        boolean leftDisturbLineFound = false;
        for (x = 1; x < weight/4; x++) {
            int PointXY = image.getRGB(x, height - 1);
            int PointXminus1Yminus1 = image.getRGB(x - 1, height - 2);
            if ((PointXY != 0xFFFFFFFF) && (PointXY == PointXminus1Yminus1)) {
                count++;
                if (count == 1) {
                    matchedX1 = x;
                } else {
                    matchedX2 = x;
                }
            }

            if (count == 2) {
                disturbColor = PointXY;
                leftDisturbLineFound = true;
                break;
            }
        }

        if (leftDisturbLineFound) {
            delta = matchedX2 - matchedX1;
            for (x = matchedX1; x < (weight + height); x += delta) {
                for (k = x; (k >= 0) && (x - k < height); k--) {
                    if ((k < weight) && (image.getRGB(k, height - 1 - (x - k)) == disturbColor)) {
                        image.setRGB(k, height - 1 - (x - k), 0xFFFFFFFF);
                    }
                }
            }
        }

        return image;
    }

    private static boolean isBlack(int colorInt) {
        Color color = new Color(colorInt);
        if (color.getRed() + color.getGreen() + color.getBlue() <= 300) {
            return true;
        }
        return false;
    }

    private static boolean isWhite(int colorInt) {
        Color color = new Color(colorInt);
        if (color.getRed() + color.getGreen() + color.getBlue() > 300) {
            return true;
        }
        return false;
    }

    private static int isBlackOrWhite(int colorInt) {
        if (getColorBright(colorInt) < 30 || getColorBright(colorInt) > 730) {
            return 1;
        }
        return 0;
    }

    private static int getColorBright(int colorInt) {
        Color color = new Color(colorInt);
        return color.getRed() + color.getGreen() + color.getBlue();
    }
}
