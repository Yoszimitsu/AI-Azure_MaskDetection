package dto;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;

import java.util.ArrayList;

public class MaskDetectionRequestObject {

    private HttpClient httpclient;
    private Mat mat;
    private MatOfRect facesDetected;
    private ArrayList<HttpEntity> httpEntities;


    public MaskDetectionRequestObject() {
        this.httpclient = HttpClients.createDefault();
        this.mat = new Mat();
        this.facesDetected = new MatOfRect();
        this.httpEntities = new ArrayList<>();
    }

    public HttpClient getHttpclient() {
        return httpclient;
    }

    public void setHttpclient(HttpClient httpclient) {
        this.httpclient = httpclient;
    }

    public Mat getMat() {
        return mat;
    }

    public void setMat(Mat mat) {
        this.mat = mat;
    }

    public MatOfRect getFacesDetected() {
        return facesDetected;
    }

    public void setFacesDetected(MatOfRect facesDetected) {
        this.facesDetected = facesDetected;
    }

    public ArrayList<HttpEntity> getHttpEntities() {
        return httpEntities;
    }

    public void setHttpEntities(ArrayList<HttpEntity> httpEntities) {
        this.httpEntities = httpEntities;
    }

}
