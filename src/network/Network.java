package network;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
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

    static private JSONObject Submit(String x, JSONObject request) {
        URL myUrl;
        HttpURLConnection myConnect = null;

        try {
            myUrl = new URL(getURL(x));
            myConnect = (HttpURLConnection) myUrl.openConnection();
            myConnect.setRequestMethod("POST");

            myConnect.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(myConnect.getOutputStream());
            wr.writeBytes(request.toString());
            wr.flush();
            wr.close();

            InputStream is = myConnect.getInputStream();
            Scanner in = new Scanner(is);
            String s = "{}";
            if (in.hasNextLine()) s = in.nextLine();

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

    static public void run() {
        JSONObject fromTrain = new Network().Submit("train", new JSONObject());
        System.out.println(fromTrain.toString());

        JSONObject toEval = new JSONObject();
        toEval.put("id", fromTrain.get("id"));
        JSONArray arr = new JSONArray();
        Random rnd = new Random();
        for (int i = 0; i < 10; i++) arr.add(rnd.nextInt(1000000000));
        toEval.put("arguments", arr);
        System.out.println(Submit("eval", toEval));

        JSONObject toSubmit = new JSONObject();
        toSubmit.put("id", fromTrain.get("id"));
        toSubmit.put("program", fromTrain.get("challenge"));
        JSONObject fromGuess = Submit("guess", toSubmit);
        System.out.println(fromGuess.toString());
        System.out.println(arr.toString());
    }
}

