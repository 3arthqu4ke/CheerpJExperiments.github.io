package me.earth.cheerpjexperiments;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

public class Base64ToFile {
    public static void main(String[] args) throws IOException {
        String cheerpJClassBytes = "";
        byte[] bytes = Base64.getDecoder().decode(cheerpJClassBytes);
        try (FileOutputStream fos = new FileOutputStream("CheerpJ.class")) {
            fos.write(bytes);
        }
    }

}
