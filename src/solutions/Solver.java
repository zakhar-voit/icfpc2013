package solutions;

import eval.Interpreter;
import network.Network;
import network.ResponseUtils;
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
    static public final String choice[] = {"x", "y", "0", "1", "(not e)", "(shl1 e)", "(shr1 e)", "(shr4 e)", "(shr16 e)", "(or e e)", "(and e e)", "(xor e e)", "(plus e e)", "(if0 e e e)", "(fold x 0 (lambda (x y) e))", "(fold e e (lambda (x y) e))"};
    static public final String choice2[]= {"x", "y", "0", "1", "not",     "shl1",     "shr1",     "shr4",     "shr16",     "or",       "and",       "xor",       "plus",       "if0",         "tfold",                        "fold"};
    long[] a, a0;
    ServerSubmitter submitter;
    JSONArray perm;
    int maxSize;

    boolean tryCheck(String s) {
        long[] b;
        try {
            b = Interpreter.eval(s, a0);
        } catch (Exception e) {
            System.out.println(s);
            e.printStackTrace();
            return true;
        }
        boolean ok = true;

        for (int i = 0; i < b.length; i++) ok &= b[i] == a[i];
        if (!ok) return false;
        JSONObject res = submitter.guessFull(s);
        if (res.get("status").equals("win")) return true;

        System.out.println("wrong try");
        long[] x = ResponseUtils.parseResponse(res.get("values").toString());
        long a1[] = new long[a0.length + 1];
        for (int i = 0; i < a0.length; i++) a1[i] = a0[i];
        a1[a0.length] = x[0];
        a0 = a1;

        long a2[] = new long[a.length + 1];
        for (int i = 0; i < a.length; i++) a2[i] = a[i];
        a2[a.length] = x[1];
        a = a2;
        return false;
    }

    int csize;
    boolean rec() {
        if (submitter.timeExpired()) return false;

        int balance = 0;
        boolean ok = true;

        int wasfold = -1;
        boolean needy = false;
        for (int i = 0; i < cur.length(); i++) {
            if (cur.charAt(i) == '(') balance++;
            if (cur.charAt(i) == ')') balance--;
            if (i < cur.length() - 1 && cur.charAt(i) == 'f' && cur.charAt(i + 1) == 'o') {
                wasfold = balance;
            }
            if (wasfold != -1 && balance <= wasfold) needy = false;
            if (wasfold != -1 && i < cur.length() - 1 && cur.charAt(i) == 'l' && cur.charAt(i + 1) == 'a') needy = true;

            if (cur.charAt(i) == 'e') {
                String s1 = "", s2 = "";
                for (int j = 0; j < cur.length(); j++) {
                    if (j < i) s1 += cur.charAt(j);
                    if (j > i) s2 += cur.charAt(j);
                }

                int nsize = csize;
                for (int j = 0; j < choice.length; j++) {
                    if (j >= 4 && balance == 6) break;
                    if (j == 4 || j == 9 || j == 13 || j == 14) nsize++;
                    if (j == 1 && (!needy || balance <= wasfold)) continue;
                    if (j >= 14 && wasfold != -1) continue;

                    if (nsize > maxSize) continue;
                    if (!submitter.isAllowed(choice2[j])) continue;
                    cur = s1 + choice[j] + s2;
                    csize = nsize;
                    if (rec())
                        return true;
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
            int ok = 0, cnt = 0;
            maxSize = 10;
            for (int cSize = 1; cSize <= 10; cSize++) {
                System.out.println(cSize);
                for (Object anArr : arr) {
                    JSONObject cur = (JSONObject) anArr;
                    if (maxSize == 1) Out.println(cur.toString());
                    if (cur.get("size").toString().equals(Integer.toString(cSize))) {
                        if (cur.containsKey("solved") && (cur.get("solved").toString().equals("true") || cur.get("timeLeft").toString().equals("0"))) {
                            cnt++;
                            if (cur.get("solved").toString().equals("true")) ok++;
                            continue;
                        }
                        System.out.println(cur.toString());
                        if (run(cur.get("id").toString(), (JSONArray)cur.get("operators"))) ok++;
                        cnt++;
                        System.out.println(cnt + "/40 is solved, " + ok + " is correct");
                        try {
                            Thread.sleep(20200);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            System.out.println(arr.size());
            Out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String randID(boolean f) {
        maxSize = 10;
        JSONObject sbmt = new JSONObject();
        sbmt.put("size", 11);
        JSONArray arr = new JSONArray(); arr.add("tfold");
        //sbmt.put("operators", arr);
        JSONObject lol = Network.Submit("train", sbmt);
        if (f) System.out.println(lol.toString());
        perm = (JSONArray)lol.get("operators");
        return lol.get("id").toString();
    }

    public static JSONObject randID_1(boolean f) {
        JSONObject sbmt = new JSONObject();
        sbmt.put("size", 10);
        JSONObject lol = Network.Submit("train", sbmt);
        if (f) System.out.println(lol.get("challenge").toString());
        return lol;
    }

    public boolean run(String ID, JSONArray arr) {
        Random rnd = new Random();
        if (ID.equals(""))
            id = randID(true);
        else {
            id = ID;
            perm = arr;
            maxSize = 10;
        }

        submitter = new ServerSubmitter(id, perm);
        //submitter = new ServerSubmitter("(lambda (x_5415) (fold x_5415 0 (lambda (x_5415 x_5416) (xor (shl1 x_5415) x_5415))))");

        a0 = new long[15];
        for (int i = 2; i < 15; i++) a0[i] = Math.abs(rnd.nextLong());
        a0[0] = 0;
        a0[1] = 1;
        for (int i = 0; i < 62; i++) a0[1] = a0[1] + a0[1] + 1;

        a = submitter.eval(a0);

        cur = "(lambda (x) e)";
        csize = 1;
        if (rec()) {
            System.out.println(cur);
            System.out.println("YYYEEEEAAAHH");
            return true;
        } else {
            System.out.println("Nothing found");
            return false;
        }
    }
}