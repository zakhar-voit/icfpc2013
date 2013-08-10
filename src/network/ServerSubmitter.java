package network;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Ilya Zban(izban@mail.ru)
 */
@SuppressWarnings("unchecked")
public class ServerSubmitter implements Submitter {
    String id;

    public ServerSubmitter(String myID) {
        id = myID;
    }
    //return new JSONObject();df

    public long[] eval(long[] a) {
        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();
        for (long anA : a) arr.add(Long.toHexString(anA));
        obj.put("id", id);
        obj.put("arguments", arr);
        obj = Network.Submit("eval", obj);
        String s = obj.get("outputs").toString();
        return ResponseUtils.parseResponse(s);
    }

    public boolean guess(String program) {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("program", program);
        return Network.Submit("guess", obj).get("status").toString().equals("win");
    }
}
