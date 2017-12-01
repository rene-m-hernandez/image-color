package com.pex.image;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by rhernandez on 11/29/17.
 */
public class Util {
    public static int countLines(File filename) throws IOException {
        try (InputStream is = new BufferedInputStream(new FileInputStream(filename))){
            byte[] buf = new byte[1024];
            int c;
            int lineCount = 0;
            while ((c = is.read(buf)) > 0) {
                for (int i = 0; i < c; i++) {
                    if (buf[i] == '\n') lineCount++;
                }
            }
            return lineCount;
        }
    }
}
