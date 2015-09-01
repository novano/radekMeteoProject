package meteocontroller.dto;

import java.io.File;

/**
 *
 * @author Jan Novak <novano@mail.muni.cz>
 */
public class PhotoDto {

    private File file;

    public PhotoDto(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

}
