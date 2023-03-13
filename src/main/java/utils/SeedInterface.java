package utils;

import java.util.Scanner;

public interface SeedInterface {

    public void init();
    public void scanSeed(Scanner scanner);

    public String getPath();

    public String serialize();
}
