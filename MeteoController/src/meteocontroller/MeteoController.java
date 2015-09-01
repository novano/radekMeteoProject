package meteocontroller;

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

        List<PhotoDto> newPhotos = photoGetter.getNewPhotos();

    }

}
