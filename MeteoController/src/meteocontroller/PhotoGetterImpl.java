package meteocontroller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import meteocontroller.dto.PhotoDto;

/**
 *
 * @author Jan Novak <novano@mail.muni.cz>
 */
public class PhotoGetterImpl implements PhotoGetter {

    private static String SUPPORTED_SUFIXES = "jpg,png,gif,jpeg";
    private static String SCAN_FOLDER_PATH = "./photosUpload/";
    private static String TMP_FOLDER_PATH = "./temp/";
    private static String DATA_FOLDER_PATH = "./data/";

    @Override
    public List<PhotoDto> getNewPhotos() {
        List<PhotoDto> newPhotos = scanFolder();
        newPhotos = filterPhotos(newPhotos);
        proceedPhotos(newPhotos);
        clearTmpFolder();
        return newPhotos;
    }

    private List<PhotoDto> scanFolder() {
        List<PhotoDto> result = new ArrayList<>();
        File scanFolder = new File(SCAN_FOLDER_PATH);
        if (scanFolder.isDirectory()) {

            for (File file : scanFolder.listFiles()) {
                File moveFile = moveFile(SCAN_FOLDER_PATH + file.getName(), TMP_FOLDER_PATH + file.getName());
                result.add(new PhotoDto(moveFile));
            }
        }
        return result;
    }

    private List<PhotoDto> filterPhotos(List<PhotoDto> newPhotos) {
        List<PhotoDto> filteredPhotos = new ArrayList<>(newPhotos.size());
        for (PhotoDto photo : newPhotos) {
            if (validateSuffix(photo)) {
                filteredPhotos.add(photo);
                System.err.println("Loading file '" + photo.getFile().getName() + "'");
            }
        }
        return filteredPhotos;
    }

    private void proceedPhotos(List<PhotoDto> newPhotos) {
        for (PhotoDto newPhoto : newPhotos) {
            File file = newPhoto.getFile();
            newPhoto.setFile(moveFile(TMP_FOLDER_PATH + file.getName(), DATA_FOLDER_PATH + file.getName()));
        }
    }

    private boolean validateSuffix(PhotoDto filteredPhoto) {
        //TODO splitnout posledni cast '.' - aktual zpusob muze selhat .XXXjpg
        String[] suffixes = SUPPORTED_SUFIXES.toLowerCase().split(",");
        for (String suffix : suffixes) {
            if (filteredPhoto.getFile().getName().toLowerCase().endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    private File moveFile(String from, String to) {
        try {
            Path source = Paths.get(from);
            Path target = Paths.get(to);
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
            return target.toFile();
        } catch (IOException ex) {
            //TODO
            return null;
        }
    }

    private void clearTmpFolder() {
        for (File file : new File(TMP_FOLDER_PATH).listFiles()) {
            file.delete();
        }
    }

}
