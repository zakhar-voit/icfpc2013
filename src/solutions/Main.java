package solutions;

import network.ServerSubmitter;
import network.Submitter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * @author Ilya Zban(izban@mail.ru)
 */
public class Main {

    public void run() {
        //new Solver().getProblems();
        final int iters = 100;
        int ok = 0;
        for (int i = 0; i < iters; i++) {
            if (new Solver().run("", new JSONArray())) ok++;
            System.out.println((i + 1) + "/" + iters + " calced, " + ok + " is correct");
            //JSONObject start = Solver.randID_1(true);
            //System.out.println(Boolean.toString(new BruteforceSolution(new ServerSubmitter(start.get("id").toString(), (JSONArray)start.get("operators")), true).solve()));
            try {
                Thread.sleep(20200);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new Main().run();
    }
}
