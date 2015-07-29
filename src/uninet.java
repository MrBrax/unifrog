import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JOptionPane;


public class uninet {

	static String sendGetRequest(String endpoint, String requestParameters) {
        String result = null;
        if (endpoint.startsWith("http://")) {
            try {

                String urlStr = endpoint;
                if (requestParameters != null && requestParameters.length() > 0) {
                    urlStr += "?" + requestParameters;
                }
                URL url = new URL(urlStr);
                URLConnection conn = url.openConnection();

                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuffer sb = new StringBuffer();
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                rd.close();
                result = sb.toString();
            } catch (Exception e) {
            	JOptionPane.showMessageDialog(MainGame.f, "Network error, please retry.");
            	if( MainGame.uniSend != null) MainGame.uniSend.stop();
                //e.printStackTrace();
            }
        }
        return result;
    }

}
