package utils;

import Network.NetworkSeed;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public abstract class ClassSeed implements SeedInterface {

    public ClassSeed() {
        this.init();
    }

    public ClassSeed(String filename) {
        this.init();
        File seed = new File(getPath() + filename + ".txt");
        Scanner scanner;
        try {
            scanner = new Scanner(seed).useDelimiter("([ \n\r])+");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        this.scanSeed(scanner);
    }

    public void saveSeed() {
        String filename = getStandardFilename();
        this.saveSeed(filename);
    }

    public void saveSeed(String filename) {
        this.saveSeed(this.getPath(), filename + ".txt");
    }

    public void saveSeed(String relativePath, String filename) {

        String filepath = relativePath + filename;
        PrintWriter writer;
        File file = new File(filepath);
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            writer = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        writer.print(this.serialize());
        writer.close();
    }

    public static String getStandardFilename() {
        return ClassSeed.getDate() + "_seed";
    }

    private static String getDate() {
        return GetDate.getDate();
    }

}
