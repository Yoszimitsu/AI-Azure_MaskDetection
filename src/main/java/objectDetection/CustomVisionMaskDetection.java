package objectDetection;

import dto.AzurePassDto;
import dto.MaskDetectionRequestDto;
import error.CredentialsFileNotFound;
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
import java.util.ArrayList;

import static openCv.ImageProcessing.mat2Img;
import static services.CustomVisionService.*;

public class CustomVisionMaskDetection {

    private final double threshold = 0.5;
    private ArrayList<JSONObject> maskDetectionJSONObjectArray = new ArrayList<>();
    private AzurePassDto azurePass;

    public CustomVisionMaskDetection() throws CredentialsNotFoundError, CredentialsFileNotFound {
        this.azurePass = getCredentials("./src/main/java/credentials/app.config", "url.customVision", "key.customVision");
    }

    public Image maskDetection(VideoCapture capture) {
        MaskDetectionRequestDto maskDetectionRequestDto = new MaskDetectionRequestDto();
        capture.read(maskDetectionRequestDto.getMat());
        try {
            MatOfByte bytes = new MatOfByte();
            Imgcodecs.imencode(".jpg", maskDetectionRequestDto.getMat(), bytes);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes.toArray());

            // SEND HTTP REQUEST
            HttpResponse response = sendRequest(
                    maskDetectionRequestDto,
                    inputStream,
                    this.azurePass.getUrl(),
                    this.azurePass.getKey());

            // CONVERT RESPONSE TO STRING AND CONVERT RESPONSE STRING TO JSON ARRAY
            if (response.getEntity() != null) {
                maskDetectionRequestDto.getHttpEntities().add(response.getEntity());
            }

            maskDetectionJSONObjectArray = jsonParser(
                    maskDetectionRequestDto.getHttpEntities(), maskDetectionJSONObjectArray, threshold);

        } catch (Exception e) {
            e.getStackTrace();
        }
        return mat2Img(rectangle(maskDetectionRequestDto.getMat()));
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
}
