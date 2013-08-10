package network;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Ilya Zban(izban@mail.ru)
 */
@SuppressWarnings("unchecked")
public class ServerSubmitter implements Submitter {
    JSONObject start = new JSONObject();

    public ServerSubmitter(String s) {
        if (s.charAt(0) == '(') {
            start.put("program", s);
        } else {
            start.put("id", s);
        }
    }
    //return new JSONObject();df

    public long[] eval(long[] a) {
        JSONObject obj = new JSONObject(start);
        JSONArray arr = new JSONArray();
        for (long anA : a) arr.add(Long.toHexString(anA));
        obj.put("arguments", arr);
        obj = Network.Submit("eval", obj);
        String s = obj.get("outputs").toString();
        return ResponseUtils.parseResponse(s);
    }

    public boolean guess(String program) {
        JSONObject obj = new JSONObject(start);
        obj.put("program", program);
        if (!start.containsKey("id")) return true;
        return Network.Submit("guess", obj).get("status").toString().equals("win");
    }
}
