package dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;

import java.util.ArrayList;

@AllArgsConstructor
@Data
@Builder
public class MaskDetectionRequestDto {

    private HttpClient httpclient;
    private Mat mat;
    private MatOfRect facesDetected;
    private ArrayList<HttpEntity> httpEntities;


    public MaskDetectionRequestDto() {
        this.httpclient = HttpClients.createDefault();
        this.mat = new Mat();
        this.facesDetected = new MatOfRect();
        this.httpEntities = new ArrayList<>();
    }
}
