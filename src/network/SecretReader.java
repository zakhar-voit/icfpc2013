package network;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Zakhar Voit(zakharvoit@gmail.com)
 */
public class SecretReader {
    static final String DEFAULT_FILE = "secret.txt";

    static public String readSecret() {
        return readSecret(DEFAULT_FILE);
    }

    static public String readSecret(String fileName) {
        try {
            return new BufferedReader(new FileReader(fileName)).readLine().trim();
        } catch (IOException e) {
            return "";
        }
    }
}
