package network;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import solutions.Solver;

import java.util.ArrayList;

/**
 * Ilya Zban(izban@mail.ru)
 */
@SuppressWarnings("unchecked")
public class ServerSubmitter implements Submitter {
    JSONObject start = new JSONObject();
    ArrayList<Long> v = new ArrayList<Long>();

    public boolean isAllowed(String operand) {
        boolean lol = true;
        for (int i = 4; i < Solver.choice2.length; i++) if (operand.equals(Solver.choice2[i])) lol = false;
        if (lol) return true;
        if (start.containsKey("id")) {
            JSONArray arr = (JSONArray) start.get("operands");
            for (Object anArr : arr)
                if (anArr.toString().equals(operand))
                    return true;
            return false;
        } else {
            return start.get("program").toString().contains(operand);
        }
    }

    public ServerSubmitter(String id, JSONArray arr) {
        start.put("id", id);
        start.put("operands", arr);
        v.add(System.currentTimeMillis());
    }

    public ServerSubmitter(String s) {
        if (s.charAt(0) == '(') {
            start.put("program", s);
        } else {
            start.put("id", s);
        }
        v.add(System.currentTimeMillis());
    }

    public boolean timeExpired() {
        long ctime = System.currentTimeMillis();
        if (v.size() >= 1 && ctime - v.get(0) > 302000) return true;
        return false;
    }

    private void not429() {
        try {
            long ctime = System.currentTimeMillis();
            if (v.size() >= 5 && ctime - v.get(v.size() - 5) < 21000) {
                Thread.sleep(21000 - (ctime - v.get(v.size() - 5)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long[] eval(long[] a) {
        not429();

        JSONObject obj = new JSONObject(start);
        JSONArray arr = new JSONArray();
        for (long anA : a) arr.add(Long.toHexString(anA));
        obj.put("arguments", arr);
        obj = Network.Submit("eval", obj);
        String s = obj.get("outputs").toString();
        v.add(System.currentTimeMillis());
        return ResponseUtils.parseResponse(s);
    }

    public boolean guess(String program) {
        not429();

        JSONObject obj = new JSONObject(start);
        obj.put("program", program);
        boolean ans = !start.containsKey("id") || Network.Submit("guess", obj).get("status").toString().equals("win");
        v.add(System.currentTimeMillis());
        return ans;
    }

    public JSONObject guessFull(String program) {
        not429();

        JSONObject obj = new JSONObject(start);
        obj.put("program", program);
        JSONObject ans = new JSONObject();
        if (start.containsKey("id")) {
            ans = Network.Submit("guess", obj);
        } else {
            ans.put("status", "win");
        }
        v.add(System.currentTimeMillis());
        return ans;
    }
}
