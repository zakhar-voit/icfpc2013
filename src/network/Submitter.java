package network;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.lang.model.element.ElementKind;

/**
 * Created with IntelliJ IDEA.
 * User: izban
 * Date: 10.08.13
 * Time: 17:12
 * To change this template use File | Settings | File Templates.
 */
public class Submitter {
    String id;

    Submitter(String myID) {
        id = myID;
    }
                //return new JSONObject();df

    long[] eval(long[] a) {
        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();
        for (int i = 0; i < a.length; i++) arr.add(Long.toHexString(a[i]));
        obj.put("id", id);
        obj.put("arguments", arr);
        obj = Network.Submit("eval", obj);
        String s = obj.get("outputs").toString();
        return ResponceUtils.parseResponse(s);
    }

    boolean guess(String program) {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("program", program);
        return Network.Submit("guess", obj).get("status").toString().equals("win");
    }
}
