package solutions;

import network.ServerSubmitter;
import network.Submitter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * @author Ilya Zban(izban@mail.ru)
 */
public class Main {

    public void run() {
        new Solver().getProblems();

        /*JSONArray arr = new JSONArray();
        arr.add("if0"); arr.add("shl1"); arr.add("tfold");
        new Solver().run("WwgRkpvGeHQCMMjWva2qfwkY", arr);*/

        /*final int iters = 100;
        int ok = 0;
        for (int i = 0; i < iters; i++) {
            if (new Solver().run("", new JSONArray())) ok++;
            System.out.println((i + 1) + "/" + iters + " calced, " + ok + " is correct");
            try {
                Thread.sleep(20200);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    }

    public static void main(String[] args) {
        new Main().run();
    }
}
