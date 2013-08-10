package network;

import eval.Interpreter;
import eval.InterpreterTest;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Random;
/**
 * Created with IntelliJ IDEA.
 * User: izban
 * Date: 10.08.13
 * Time: 10:22
 * To change this template use File | Settings | File Templates.
 */
public class Solver {

    String cur;
    String id;
    final String choice[] = {"x", "0", "1", "(not e)", "(or e e)", "(and e e)", "(xor e e)", "(plus e e)", "(shl1 e)", "(shr1 e)", "(shr4 e)", "(shr16 e)", "(if0 e e e)"};
    long[] a, a0;


    boolean tryCheck(String s) {
        long[] b = Interpreter.eval(s, a0);
        boolean ok = true;

        for (int i = 0; i < b.length; i++) ok &= b[i] == a[i];
        if (!ok) return false;

        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("program", s);
        JSONObject result = Network.Submit("guess", obj);
        if (result.get("status").toString().equals("win")) return true;
        return false;
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
        if (ok) {
            return tryCheck(cur);
        }
        return false;
    }

    public void getProblems() {
        try {
            JSONObject tr = Network.Submit("myproblems", new JSONObject());
            JSONArray arr = (JSONArray)tr.get("lol");
            PrintWriter Out = new PrintWriter(new BufferedWriter(new FileWriter("tasks.txt")));
            for (int i = 0; i < arr.size(); i++) Out.println(arr.get(i));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        Random rnd = new Random();
        JSONObject tr = Network.Submit("train", new JSONObject());
        id = tr.get("id").toString();
        System.out.println(tr.get("challenge").toString());

        a0 = new long[7];
        for (int i = 2; i < 7; i++) a0[i] = rnd.nextInt(1000000000);
        a0[0] = 0; a0[1] = 1; for (int i = 0; i < 62; i++) a0[1] = a0[1] + a0[1] + 1;

        JSONArray arr = new JSONArray();
        for (int i = 0; i < a0.length; i++) arr.add(Long.toHexString(a0[i]));
        JSONObject query = new JSONObject();
        query.put("id", id);
        query.put("arguments", arr);
        String res = Network.Submit("eval", query).get("outputs").toString();
        a = ResponceUtils.parseResponse(res);

        cur = "(lambda (x) e)";
        if (rec()) {
            System.out.println(cur);
            System.out.println("YYYEEEEAAAHH");
        } else System.out.println("Nothing found");
    }
}
