package services;

import dto.AzurePassDto;
import dto.MaskDetectionRequestDto;
import error.CredentialsNotFoundError;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.stream.Collectors;

public class CustomVisionService {

    public static HttpResponse sendRequest(MaskDetectionRequestDto maskDetectionRequestDto, ByteArrayInputStream inputStream, String url, String key) {
        HttpResponse response = null;
        try {
            URIBuilder builder = new URIBuilder(url);
            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);
            request.setHeader("Prediction-Key", key);
            request.setHeader("Content-Type", "application/octet-stream");

            // Request body
            byte[] data = inputStream.readAllBytes();
            HttpEntity requestEntity = new ByteArrayEntity(data);
            request.setEntity(requestEntity);

            // Send HTTP request
            response = maskDetectionRequestDto.getHttpclient().execute(request);

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static ArrayList<JSONObject> jsonParser(ArrayList<HttpEntity> httpEntitiesArray, ArrayList<JSONObject> maskDetectionJSONObjectArray, double threshold) throws IOException {
        maskDetectionJSONObjectArray.clear();
        for (HttpEntity entity : httpEntitiesArray) {
            JSONObject jsonObjectResponse = new JSONObject(EntityUtils.toString(entity));
            if (jsonObjectResponse.has("predictions")) {
                JSONArray predictionsObjectArray = new JSONArray(jsonObjectResponse.get("predictions").toString());
                for (int i = 0; i < predictionsObjectArray.length(); i++) {
                    maskDetectionJSONObjectArray.add(new JSONObject(predictionsObjectArray.get(i).toString()));
                }
                // FILTER PROBABILITY > THRESHOLD
                maskDetectionJSONObjectArray = (ArrayList<JSONObject>) maskDetectionJSONObjectArray
                        .stream()
                        .filter(jsonObject -> jsonObject.getDouble("probability") > threshold)
                        .collect(Collectors.toList());
            }
        }
        return maskDetectionJSONObjectArray;
    }

    public static AzurePassDto getCredentials(String urlVariable, String keyVariable) throws CredentialsNotFoundError {
        AzurePassDto azurePassDto = new AzurePassDto();
        Properties prop = new Properties();
        String fileName = "./src/main/java/credentials/app.config";
        try {
            InputStream is = new FileInputStream(fileName);
            prop.load(is);
            azurePassDto.setUrl(prop.getProperty(urlVariable));
            azurePassDto.setKey(prop.getProperty(keyVariable));
        } catch (Exception e) {
            throw new CredentialsNotFoundError();
        }
        return azurePassDto;
    }

}
