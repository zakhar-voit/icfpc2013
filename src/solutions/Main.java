package solutions;

import network.Submitter;
import org.json.simple.JSONArray;

/**
 * @author Ilya Zban(izban@mail.ru)
 */
public class Main {

    public void run() {
        new Solver().getProblems();
        /*for (int i = 0; i < 20; i++) {
            new Solver().run("", new JSONArray());
            try {
                Thread.sleep(25000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } */
    }

    public static void main(String[] args) {
        new Main().run();
    }
}
