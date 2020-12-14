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
import org.json.JSONObject;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.Properties;
import java.util.stream.Collectors;

import static openCv.ImageProcessing.mat2Img;

public class CustomVisionMaskDetectionTags {

    private String url;
    private String key;
    private final double threshold = 0.5;
    private ArrayList<JSONObject> maskDetectionJSONObjectArray;
    private Rect[] rectArray;

    public CustomVisionMaskDetectionTags() {
    }

    public Image maskDetection(VideoCapture capture) throws IOException {
        HttpClient httpclient = HttpClients.createDefault();
        Mat mat = new Mat();
        MatOfRect facesDetected = new MatOfRect();
        ArrayList<HttpEntity> httpEntities = new ArrayList<>();
        // GET IMAGE FROM VIDEO
        capture.read(mat);
        // SEND IMAGE TO OPENCV FACE DETECTION
        OpenCvFaceDetection openCvFaceDetection = new OpenCvFaceDetection(mat);
        // GET FACE IMAGES DETECTED ON CAPTURE
        ArrayList<Mat> faceImageArray = openCvFaceDetection.getFaceImageArray();
        rectArray = openCvFaceDetection.getRectArray();

        for (Mat faceImage : faceImageArray) {
            try {
                MatOfByte bytes = new MatOfByte();
                Imgcodecs.imencode(".jpg", faceImage, bytes);
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
                    // ADD ENTITY TO ARRAY
                    httpEntities.add(entity);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        // PARSE JSON RESPONSES
        jsonParser(httpEntities);

        return mat2Img(rectangle(rectArray, mat, maskDetectionJSONObjectArray));
    }

    private void jsonParser(ArrayList<HttpEntity> httpEntitiesArray) throws IOException {
        maskDetectionJSONObjectArray = new ArrayList<>();
        for (HttpEntity entity : httpEntitiesArray) {
            JSONObject jsonObjectResponse = new JSONObject(EntityUtils.toString(entity));
            if (jsonObjectResponse.get("predictions") != null) {
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
    }

    private Mat rectangle(Rect[] rectArray, Mat image, ArrayList<JSONObject> maskDetectionJSONObjectArray) {
        for (int i = 0; i < rectArray.length; i++) {
            try {
                Rect face = rectArray[i];
                if (maskDetectionJSONObjectArray.get(i).getString("tagName").equals("mask"))
                    Imgproc.rectangle(image, face.tl(), face.br(), new Scalar(0, 255, 0), 1);
                if (maskDetectionJSONObjectArray.get(i).getString("tagName").equals("noMask"))
                    Imgproc.rectangle(image, face.tl(), face.br(), new Scalar(0, 0, 255), 1);
            }
            catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        return image;
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
        this.url = prop.getProperty("url.customVisionTags");
        this.key = prop.getProperty("key.customVisionTags");
    }
}
