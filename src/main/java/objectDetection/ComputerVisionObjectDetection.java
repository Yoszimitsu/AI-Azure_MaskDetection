package objectDetection;

import dto.AzurePassDto;
import error.CredentialsFileNotFound;
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
import java.net.URI;

import static services.CustomVisionService.getCredentials;

public class ComputerVisionObjectDetection {

    private HttpClient httpclient = HttpClients.createDefault();
    private AzurePassDto azurePass;
    private URIBuilder builder;
    private URI uri;
    private HttpResponse response;
    private StringEntity reqEntity;

    public ComputerVisionObjectDetection() throws CredentialsNotFoundError, CredentialsFileNotFound {
        this.azurePass = getCredentials("./src/main/java/credentials/app.config", "url.computerVision", "key.computerVision");
    }

    public void execute(ByteArrayInputStream inputStream) {
        try {
            builder = new URIBuilder(this.azurePass.getUrl());
            uri = builder.build();
            HttpPost request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/octet-stream");
            request.setHeader("Ocp-Apim-Subscription-Key", this.azurePass.getKey());

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
}
