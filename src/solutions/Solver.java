package solutions;

import eval.Interpreter;
import network.Network;
import network.ServerSubmitter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Random;

/**
 * @author Ilya Zban(izban@mail.ru)
 */
@SuppressWarnings("unchecked")
public class Solver {

    String cur;
    String id;
    final String choice[] = {"x", "0", "1", "(not e)", "(or e e)", "(and e e)", "(xor e e)", "(plus e e)", "(shl1 e)", "(shr1 e)", "(shr4 e)", "(shr16 e)", "(if0 e e e)"};
    long[] a, a0;
    ServerSubmitter submitter;

    boolean tryCheck(String s) {
        long[] b = Interpreter.eval(s, a0);
        boolean ok = true;

        for (int i = 0; i < b.length; i++) ok &= b[i] == a[i];
        return ok && submitter.guess(s);

    }

    boolean rec() {
        int balance = 0;
        boolean ok = true;

        for (int i = 0; i < cur.length(); i++) {
            if (cur.charAt(i) == '(') balance++;
            if (cur.charAt(i) == ')') balance--;
            if (cur.charAt(i) == 'e') {
                String s1 = "", s2 = "";
                for (int j = 0; j < cur.length(); j++) {
                    if (j < i) s1 += cur.charAt(j);
                    if (j > i) s2 += cur.charAt(j);
                }

                for (int j = 0; j < choice.length; j++) {
                    if (j >= 3 && balance == 3) break;
                    cur = s1 + choice[j] + s2;
                    if (rec()) return true;
                }

                ok = false;
                break;
            }
        }
        return ok && tryCheck(cur);
    }

    public void getProblems() {
        try {
            JSONObject tr = Network.Submit("myproblems", new JSONObject());
            JSONArray arr = (JSONArray) tr.get("lol");
            PrintWriter Out = new PrintWriter(new BufferedWriter(new FileWriter("tasks.txt")));
            for (Object anArr : arr) {
                JSONObject cur = (JSONObject) anArr;
                Out.println(cur.toString());
                if (false && cur.get("size").toString().equals("4") && !cur.containsKey("solved")) {
                    System.out.println(cur.toString());
                    run(cur.get("id").toString());
                    try {
                        Thread.sleep(25000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            Out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String randID(boolean f) {
        JSONObject sbmt = new JSONObject();
        sbmt.put("size", 4);
        JSONObject lol = Network.Submit("train", sbmt);
        if (f) System.out.println(lol.get("challenge").toString());
        return lol.get("id").toString();
    }

    public void run(String ID) {
        Random rnd = new Random();
        if (ID.equals(""))
            id = randID(true);
        else id = ID;

        submitter = new ServerSubmitter(id);

        a0 = new long[7];
        for (int i = 2; i < 7; i++) a0[i] = rnd.nextInt(1000000000);
        a0[0] = 0;
        a0[1] = 1;
        for (int i = 0; i < 62; i++) a0[1] = a0[1] + a0[1] + 1;

        a = submitter.eval(a0);

        cur = "(lambda (x) e)";
        if (rec()) {
            System.out.println(cur);
            System.out.println("YYYEEEEAAAHH");
        } else
            System.out.println("Nothing found");
    }
}