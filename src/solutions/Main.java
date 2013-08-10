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
        for (int i = 0; i < 20; i++) {
            //new Solver().run("", new JSONArray());
            JSONObject start = Solver.randID_1(true);
            System.out.println(Boolean.toString(new BruteforceSolution(new ServerSubmitter(start.get("id").toString(), (JSONArray)start.get("operators")), true).solve()));
            try {
                Thread.sleep(25000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new Main().run();
    }
}
