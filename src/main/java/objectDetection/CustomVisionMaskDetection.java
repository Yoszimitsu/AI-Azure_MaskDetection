package objectDetection;

import javafx.scene.image.Image;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.io.*;
import java.net.URI;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Properties;
import java.util.stream.Collectors;

import static openCv.ImageProcessing.mat2Img;

public class CustomVisionMaskDetection {

    private final double threshold = 0.5;
    private ArrayList<JSONObject> maskDetectionJSONObjectArray = new ArrayList<>();
    private String url;
    private String key;

    public CustomVisionMaskDetection() {
        getCredentials();
    }

    public Image maskDetection(VideoCapture capture) {
        HttpClient httpclient = HttpClients.createDefault();
        Mat mat = new Mat();
        capture.read(mat);

        try {
            MatOfByte bytes = new MatOfByte();
            Imgcodecs.imencode(".jpg", mat, bytes);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes.toArray());

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
            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                // CONVERT RESPONSE TO STRING
                // CONVERT RESPONSE STRING TO JSON ARRAY
                this.jsonParser(EntityUtils.toString(entity));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mat2Img(rectangle(mat));
    }

    public void jsonParser(String result) {
        JSONObject jsonObjectResponse = new JSONObject(result);
        maskDetectionJSONObjectArray.clear();
        try {
            if (jsonObjectResponse.has("predictions")) {
                JSONArray predictionsObjectArray = new JSONArray(jsonObjectResponse.get("predictions").toString());

                for (int i = 0; i < predictionsObjectArray.length(); i++) {
                    maskDetectionJSONObjectArray.add(new JSONObject(predictionsObjectArray.get(i).toString()));
                }
                maskDetectionJSONObjectArray = (ArrayList<JSONObject>) maskDetectionJSONObjectArray
                        .stream()
                        .filter(jsonObject -> jsonObject.getDouble("probability") > threshold)
                        .collect(Collectors.toList());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Mat rectangle(Mat image) {
        if (!maskDetectionJSONObjectArray.isEmpty()) {

            for (JSONObject jsonObject : maskDetectionJSONObjectArray) {
                int x = (int) (jsonObject.getJSONObject("boundingBox").getDouble("left") * image.width());
                int y = (int) (jsonObject.getJSONObject("boundingBox").getDouble("top") * image.height());
                int width = (int) (jsonObject.getJSONObject("boundingBox").getDouble("width") * image.width());
                int height = (int) (jsonObject.getJSONObject("boundingBox").getDouble("height") * image.height());

                String probability = new DecimalFormat("#.##").format(jsonObject.getDouble("probability"));

                if (jsonObject.getString("tagName").equals("noMask")) {
                    Imgproc.rectangle(image, new Point(x, y), new Point(x + width, y + height), new Scalar(0, 0, 255), 3);
                    Imgproc.putText(image, "noMask pr:" + probability, new Point(x, y), 1, 1, new Scalar(0, 0, 255), 1);
                }
                if (jsonObject.getString("tagName").equals("mask")) {
                    Imgproc.rectangle(image, new Point(x, y), new Point(x + width, y + height), new Scalar(0, 255, 0), 3);
                    Imgproc.putText(image, "mask pr:" + probability, new Point(x, y), 1, 1, new Scalar(0, 255, 0), 1);
                }
            }
        }
        return image;
    }

    private void getCredentials() {
        Properties prop = new Properties();
        String fileName = "./src/main/resources/app.config";
        InputStream is = null;
        try {
            is = new FileInputStream(fileName);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        try {
            prop.load(is);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        this.url = prop.getProperty("url.customVision");
        this.key = prop.getProperty("key.customVision");
    }
}
