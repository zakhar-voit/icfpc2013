package network;

import org.json.simple.JSONObject;

/**
 * @author Ilya Zban(izban@mail.ru)
 */
public class Main {

    public void run() {
        //new Solver().getProblems();
        new Solver().run();
    }

    public static void main(String[] args) {
        new Main().run();
    }
}
