package meteocontroller.photo.getter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import meteocontroller.MeteoController;
import meteocontroller.MeteoLogger;
import meteocontroller.SpringContext;

import meteocontroller.dto.PhotoDto;

/**
 *
 * @author Jan Novak <novano@mail.muni.cz>
 */
public class PhotoGetterImpl implements PhotoGetter {

    private final String SUPPORTED_SUFIXES;
    private final String SCAN_FOLDER_PATH;
    private final String TMP_FOLDER_PATH;
    private final String DATA_FOLDER_PATH;

    public PhotoGetterImpl() {
        SpringContext context = MeteoController.getContext();
        SUPPORTED_SUFIXES = context.getProperty("supported.suffixes");
        SCAN_FOLDER_PATH = context.getProperty("folder.path.scan");
        TMP_FOLDER_PATH = context.getProperty("folder.path.tmp");
        DATA_FOLDER_PATH = context.getProperty("folder.path.data");
    }

    @Override
    public List<PhotoDto> getNewPhotos() {
        List<PhotoDto> newPhotos = scanFolder();
        newPhotos = filterPhotos(newPhotos);
        proceedPhotos(newPhotos);
        clearTmpFolder();
        MeteoLogger.log("Scaned " + newPhotos.size() + " files.");
        return newPhotos;
    }

    private List<PhotoDto> scanFolder() {
        MeteoLogger.log("Scan folder.");
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
                MeteoLogger.log("Loading file '" + photo.getFile().getName() + "'.");
            }
        }
        return filteredPhotos;
    }

    private void proceedPhotos(List<PhotoDto> newPhotos) {
        for (PhotoDto newPhoto : newPhotos) {
            File file = newPhoto.getFile();

            newPhoto.setFile(moveFile(TMP_FOLDER_PATH + file.getName(), getFileDataFolderPath(newPhoto)));
        }
    }

    private boolean validateSuffix(PhotoDto filteredPhoto) {
        //TODO splitnout posledni cast '.' - aktual zpusob muze selhat .XXXjpg
        String[] suffixes = SUPPORTED_SUFIXES.toLowerCase().split(",");
        for (String suffix : suffixes) {
            if (filteredPhoto != null && filteredPhoto.getFile() != null && filteredPhoto.getFile().getName() != null) {
                if (filteredPhoto.getFile().getName().toLowerCase().endsWith(suffix)) {
                    return true;
                }
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

    private String getFileDataFolderPath(PhotoDto newPhoto) {
        try {
            File file = newPhoto.getFile();
            BasicFileAttributes attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            long millis = attributes.lastModifiedTime().toMillis();
            Date created = new Date(millis);
            int year = created.getYear() + 1900;
            int month = created.getMonth() + 1;
            int date = created.getDate();

            String folderPath = DATA_FOLDER_PATH + year + "/" + month + "/" + date + "/" + file.getName();

            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            return folderPath;
        } catch (IOException ex) {
            return "";
        }
    }

}
