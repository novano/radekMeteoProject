package meteocontroller.photo.uploader;

import java.util.List;
import meteocontroller.dto.PhotoDto;

/**
 *
 * @author Johnny
 */
public interface PhotoUploader {

    void uploadPhotos(List<PhotoDto> photos);
}
