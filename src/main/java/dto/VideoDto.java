package dto;

import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class VideoDto {

    private ImageView imageView;
    private HBox hbox;
    private Scene scene;

    public VideoDto() {
        this.imageView = new ImageView();
        this.hbox = new HBox(imageView);
        this.scene = new Scene(hbox);
    }
}
