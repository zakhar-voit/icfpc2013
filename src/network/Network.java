package network;

import java.net.*;
import java.io.*;
import java.lang.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: izban
 * Date: 09.08.13
 * Time: 17:50
 * To change this template use File | Settings | File Templates.
 */

public class Network {
    public void Run() {
        URL myUrl;
        HttpURLConnection myConnect = null;

        try {
            String request = "request body TrainRequest";
            myUrl = new URL("http://icfpc2013.cloudapp.net/train?auth=03666BdvADBV1jujD9NNrr9pD3NJENbYB2r41s2WvpsH1H");
            myConnect = (HttpURLConnection)myUrl.openConnection();

            /*myConnect.setRequestMethod("POST");
            myConnect.setRequestProperty("Content-Length", "" + Integer.toString(request.getBytes().length));

            myConnect.setUseCaches (false);
            myConnect.setDoInput(true);
            myConnect.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(myConnect.getOutputStream());
            wr.writeBytes(request);
            wr.flush();
            wr.close();      */

            InputStream is = myConnect.getInputStream();
            Scanner in = new Scanner(is);
            while (in.hasNextLine()) {
                System.out.println(in.nextLine());
            }


            in.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (myConnect != null) myConnect.disconnect();
        }
    }
}
