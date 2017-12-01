package com.pex.image.processor;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * Created by rhernandez on 11/30/17.
 */
public class ColorFrequency {
    private BufferedImage image;

    public ColorFrequency(InputStream is) throws IOException {
        try {
            image = ImageIO.read(is);
        } finally {
            is.close();
        }
    }

    public int[] frequencies() {
        Multiset<Integer> colorFrequencies = HashMultiset.create();
        int[] dominantColors = new int[3];

        for(int y = 0; y < image.getHeight(); y++) {
            for(int x = 0; x < image.getWidth(); x++) {
                int pixelARGB = image.getRGB(x,y);
                int[] rgb = new int[4];
                rgb[0] = pixelARGB >> 24 & 0xFF;
                rgb[1] = pixelARGB >> 16 & 0xFF;
                rgb[2] = pixelARGB >> 8 & 0xFF;
                rgb[3] = pixelARGB & 0xFF;

                int pixelRGB = rgb[1] << 16 | rgb[2] << 8 | rgb[3];

                if(rgb[1] != 255 || rgb[2] != 255 || rgb[3] != 255)
                    colorFrequencies.add(pixelRGB);
            }
        }

        Iterable<Multiset.Entry<Integer>> entriesSortedByCount =
                Multisets.copyHighestCountFirst(colorFrequencies).entrySet();
        Integer i = 0;
        Integer pixel;

        Iterator<Multiset.Entry<Integer>> iterator = entriesSortedByCount.iterator();

        while(iterator.hasNext() && i < dominantColors.length) {
            pixel = iterator.next().getElement();
            dominantColors[i++] = pixel;
        }

        return dominantColors;
    }
}
