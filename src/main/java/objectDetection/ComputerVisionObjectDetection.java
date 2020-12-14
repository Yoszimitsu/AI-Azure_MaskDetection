package objectDetection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.io.*;
import java.net.URI;
import java.util.Properties;

public class ComputerVisionObjectDetection {

    HttpClient httpclient = HttpClients.createDefault();
    private String url;
    private String key;
    private URIBuilder builder;
    private URI uri;
    private HttpResponse response;
    private StringEntity reqEntity;

    public ComputerVisionObjectDetection() {
        getCredentials();
    }

    public void sendRequest(ByteArrayInputStream inputStream) {
        try {
            builder = new URIBuilder(url);
            uri = builder.build();
            HttpPost request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/octet-stream");
            request.setHeader("Ocp-Apim-Subscription-Key", key);

            // Request body
            byte[] data = inputStream.readAllBytes();
            HttpEntity requestEntity = new ByteArrayEntity(data);

            request.setEntity(requestEntity);

            response = httpclient.execute(request);

            HttpEntity entity = response.getEntity();
            String result = "";
            if (entity != null) {
                // CONVERT RESPONSE TO STRING
                result = EntityUtils.toString(entity);
                result = "[" + result + "]";
                System.out.println(result);
            }

            // CONVERT RESPONSE STRING TO JSON ARRAY
            JSONArray ja = new JSONArray(result);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void getCredentials() {
        Properties prop = new Properties();
        String fileName = "app.config";
        InputStream is = null;
        try {
            is = new FileInputStream(fileName);
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
        try {
            prop.load(is);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        this.url = prop.getProperty("url.computerVision");
        this.key = prop.getProperty("key.computerVision");
    }
}
