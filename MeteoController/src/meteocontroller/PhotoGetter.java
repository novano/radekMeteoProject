package meteocontroller;

import java.util.List;
import meteocontroller.dto.PhotoDto;

/**
 *
 * @author Johnny
 */
public interface PhotoGetter {

    List<PhotoDto> getNewPhotos();
}
