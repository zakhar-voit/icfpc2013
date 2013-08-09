package network;

import eval.Interpreter;
import eval.InterpreterTest;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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
    final String choice[] = {"x", "0", "1", "(not e)", "(or e e)", "(and e e)", "(xor e e)", "(plus e e)"};
    long[] a, a0;


    boolean tryCheck(String s) {
        long[] b = Interpreter.eval(s, a0);
        boolean ok = true;

        for (int i = 0; i < b.length; i++) ok &= b[i] == a[i];

        return ok;
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

                for (int j = 0; j < 8; j++) {
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

    public void run() {
        JSONObject tr = Network.Submit("train", new JSONObject());
        id = tr.get("id").toString();
        System.out.println(tr.get("challenge").toString());

        a0 = new long[2]; a0[0] = 0; a0[1] = 1; for (int i = 0; i < 62; i++) a0[1] = a0[1] + a0[1] + 1;
        JSONArray arr = new JSONArray();
        arr.add(Long.toHexString(0)); arr.add(Long.toHexString(a0[1]));
        JSONObject query = new JSONObject();
        query.put("id", id);
        //query.put("program", "(lambda (x) (xor (or x 1) (and x x)))");
        query.put("arguments", arr);
        String res = Network.Submit("eval", query).get("outputs").toString();
        a = ResponceUtils.parseResponse(res);

        cur = "(lambda (x) e)";
        if (rec()) {
            System.out.println(cur);
            JSONObject obj = new JSONObject();
            obj.put("id", id);
            obj.put("program", cur);
            JSONObject result = Network.Submit("guess", obj);
            if (result.get("status").toString().equals("win")) System.out.print("YYEEEAAAH");
            else System.out.println(result.get("values").toString());
        } else System.out.println("Nothing found");
    }
}
