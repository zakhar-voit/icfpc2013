package network;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * @author Ilya Zban(izban@mail.ru)
 */

@SuppressWarnings("unchecked")
public class Network {

    static String getSecret() {
        return SecretReader.readSecret();
    }

    static String getURL(String x) {
        return "http://icfpc2013.cloudapp.net/" + x + "?auth=" + getSecret();
    }

    static public JSONObject Submit(String x, JSONObject request) {
        URL myUrl;
        HttpURLConnection myConnect = null;

        try {
            myUrl = new URL(getURL(x));
            myConnect = (HttpURLConnection) myUrl.openConnection();

            if (!x.equals("myproblems")) {
                myConnect.setRequestMethod("POST");

                myConnect.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(myConnect.getOutputStream());
                wr.writeBytes(request.toString());
                wr.flush();
                wr.close();
            }

            InputStream is = myConnect.getInputStream();
            Scanner in = new Scanner(is);
            String s = "{}";
            if (in.hasNextLine()) s = in.nextLine();
            if (x.equals("myproblems")) {
                JSONObject res = new JSONObject();
                JSONParser parser = new JSONParser();
                JSONArray arr;
                arr = (JSONArray) parser.parse(s);
                res.put("lol", arr);
                return res;
            }
            JSONParser parser = new JSONParser();
            JSONObject res = (JSONObject) parser.parse(s);

            in.close();
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (myConnect != null) myConnect.disconnect();
        }
        return new JSONObject();
    }

    static public String eval(String program, long[] args) {
        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();
        for (Long arg : args)
            arr.add(Long.toHexString(arg));
        obj.put("program", program);
        obj.put("arguments", arr);
        JSONObject res = Submit("eval", obj);

        String s = res.get("status").toString();
        if (s.equals("ok")) {
            return res.get("outputs").toString();
        } else return "-1";
    }
}

