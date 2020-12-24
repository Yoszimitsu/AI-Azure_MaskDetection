package objectDetection;

import error.CredentialsNotFoundError;
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

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
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

    public ComputerVisionObjectDetection() throws CredentialsNotFoundError {
        getCredentials();
    }

    public void execute(ByteArrayInputStream inputStream) {
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

            if (response.getEntity() != null) {
                // CONVERT RESPONSE TO STRING
                String result = "[" + EntityUtils.toString(response.getEntity()) + "]";
                // CONVERT RESPONSE STRING TO JSON ARRAY
                JSONArray ja = new JSONArray(result);
            }

        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    private void getCredentials() throws CredentialsNotFoundError {
        Properties prop = new Properties();
        String fileName = "./src/main/java/credentials/app.config";
        try {
            InputStream is = new FileInputStream(fileName);
            prop.load(is);
            this.url = prop.getProperty("url.computerVision");
            this.key = prop.getProperty("key.computerVision");
        } catch (Exception e) {
            throw new CredentialsNotFoundError();
        }
    }
}
