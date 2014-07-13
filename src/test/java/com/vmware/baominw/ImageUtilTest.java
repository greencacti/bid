package com.vmware.baominw;

import com.vmware.baominw.utils.ImageUtil;
import org.testng.annotations.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by wanghuaiyu on 2014/7/8.
 */
public class ImageUtilTest {
    @Test
    public void testCleanImage() throws Exception {
        BufferedImage image = ImageIO.read(new File("E:\\Bid\\leftslash.bmp"));

        BufferedImage cleanImage = ImageUtil.cleanImage(image);
        ImageIO.write(cleanImage, "bmp", new File("E:\\Bid\\new.bmp"));
    }
}
