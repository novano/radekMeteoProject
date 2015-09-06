package meteocontroller;

import java.util.ArrayList;
import java.util.List;
import meteocontroller.dto.PhotoDto;

/**
 *
 * @author Johnny
 */
public class MeteoController {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        PhotoGetter photoGetter = SpringContext.getPhotoGetter();
        PhotoUploader photoUploader = SpringContext.getPhotoUploader();

        List<PhotoDto> newPhotos = new ArrayList<>();

        if (photoGetter != null) {
            newPhotos.addAll(photoGetter.getNewPhotos());
        }

        if (photoUploader != null) {
            photoUploader.uploadPhotos(newPhotos);
        }

    }

}
