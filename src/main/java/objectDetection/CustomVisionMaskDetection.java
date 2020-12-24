package objectDetection;

import dto.MaskDetectionRequestObject;
import error.CredentialsNotFoundError;
import javafx.scene.image.Image;
import org.apache.http.HttpResponse;
import org.json.JSONObject;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import static openCv.ImageProcessing.mat2Img;
import static services.CustomVisionService.jsonParser;
import static services.CustomVisionService.sendRequest;

public class CustomVisionMaskDetection {

    private final double threshold = 0.5;
    private ArrayList<JSONObject> maskDetectionJSONObjectArray = new ArrayList<>();
    private String url;
    private String key;

    public CustomVisionMaskDetection() throws CredentialsNotFoundError {
        getCredentials();
    }

    public Image maskDetection(VideoCapture capture) {
        MaskDetectionRequestObject maskDetectionRequestObject = new MaskDetectionRequestObject();
        capture.read(maskDetectionRequestObject.getMat());
        try {
            MatOfByte bytes = new MatOfByte();
            Imgcodecs.imencode(".jpg", maskDetectionRequestObject.getMat(), bytes);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes.toArray());

            // SEND HTTP REQUEST
            HttpResponse response = sendRequest(maskDetectionRequestObject, inputStream, url, key);

            // CONVERT RESPONSE TO STRING AND CONVERT RESPONSE STRING TO JSON ARRAY
            if (response.getEntity() != null) {
                maskDetectionRequestObject.getHttpEntities().add(response.getEntity());
            }

            maskDetectionJSONObjectArray = jsonParser(maskDetectionRequestObject.getHttpEntities(), maskDetectionJSONObjectArray, threshold);

        } catch (Exception e) {
            e.getStackTrace();
        }
        return mat2Img(rectangle(maskDetectionRequestObject.getMat()));
    }

    private Mat rectangle(Mat image) {
        if (!maskDetectionJSONObjectArray.isEmpty()) {
            for (JSONObject jsonObject : maskDetectionJSONObjectArray) {
                int x = (int) (jsonObject.getJSONObject("boundingBox").getDouble("left") * image.width());
                int y = (int) (jsonObject.getJSONObject("boundingBox").getDouble("top") * image.height());
                int width = (int) (jsonObject.getJSONObject("boundingBox").getDouble("width") * image.width());
                int height = (int) (jsonObject.getJSONObject("boundingBox").getDouble("height") * image.height());

                if (jsonObject.getString("tagName").equals("noMask"))
                    Imgproc.rectangle(image, new Point(x, y), new Point(x + width, y + height), new Scalar(0, 0, 255), 3);
                if (jsonObject.getString("tagName").equals("mask"))
                    Imgproc.rectangle(image, new Point(x, y), new Point(x + width, y + height), new Scalar(0, 255, 0), 3);
            }
        }
        return image;
    }

    private void getCredentials() throws CredentialsNotFoundError {
        Properties prop = new Properties();
        String fileName = "./src/main/java/credentials/app.config";
        try {
            InputStream is = new FileInputStream(fileName);
            prop.load(is);
            this.url = prop.getProperty("url.customVision");
            this.key = prop.getProperty("key.customVision");
        } catch (Exception e) {
            throw new CredentialsNotFoundError();
        }
    }
}
