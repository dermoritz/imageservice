package de.ml.statistic;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Created by moritz on 08.12.2015.
 */
public class DistributionChart {

    private final Map<Integer, Long> distribution;
    private final int countRange;
    private static final int MAX_IMAGE_WIDTH = 2000;
    private int max;
    private int min;

    public DistributionChart(Map<Integer, Long> distribution, int countRange) {
        this.distribution = distribution;
        this.countRange = countRange;
        max = 0;
        min = Integer.MAX_VALUE;
        for (Long hitCount : distribution.values()) {
            Integer hitCountInt = Math.toIntExact(hitCount);
            if (hitCountInt > max) {
                max = hitCountInt;
            }
            if (hitCountInt < min) {
                min = hitCountInt;
            }
        }
    }

    public byte[] getDistributionChart() {
        // create picture
        int width = countRange, height = 200;
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D chart = bi.createGraphics();


        chart.setPaint(Color.BLUE);

        for (int i = 0; i < countRange; i++) {
            int value = Math.toIntExact(distribution.get(i) == null ? 0 : distribution.get(i));
            int y = height * value / max;
            chart.drawLine(i, 0, i, y);
        }

        for (Map.Entry<Integer, Long> entry : distribution.entrySet()) {
            chart.drawLine(entry.getKey(), 0, entry.getKey(), Math.toIntExact(entry.getValue()));
        }
        //scale in width if necessary
        if (countRange > MAX_IMAGE_WIDTH) {
            float yScale = (float) MAX_IMAGE_WIDTH / countRange;
            chart.scale(1,yScale);
            bi = resizeImage(bi, BufferedImage.TYPE_INT_ARGB,MAX_IMAGE_WIDTH,height);
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(bi, "PNG", os);
        } catch (IOException e) {
            throw new IllegalStateException("Problem writing image: ", e);
        }
        return os.toByteArray();
    }

    private static BufferedImage resizeImage(BufferedImage originalImage, int type, int IMG_WIDTH, int IMG_HEIGHT) {
        BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
        g.dispose();

        return resizedImage;
    }

}
