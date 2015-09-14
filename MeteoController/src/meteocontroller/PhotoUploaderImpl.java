package meteocontroller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import meteocontroller.dto.PhotoDto;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

/**
 *
 * @author Jan Novak <novano@mail.muni.cz>
 */
public class PhotoUploaderImpl implements PhotoUploader {

    private Timer timer = new Timer(500);

    private String ftpServerUrl;
    private int ftpServerPort;
    private String ftpServerLogin;
    private String ftpServerPass;

    public PhotoUploaderImpl() {
        SpringContext context = MeteoController.getContext();

        ftpServerUrl = context.getProperty("ftp.server.url");
        ftpServerPort = Integer.parseInt(context.getProperty("ftp.server.port"));
        ftpServerLogin = context.getProperty("ftp.server.login");
        ftpServerPass = context.getProperty("ftp.server.pass");

        long period = Long.parseLong(context.getProperty("upload.timer.period"));
        timer = new Timer(period);
    }

    List<PhotoDto> bufferedPhotos = new ArrayList<>();

    @Override
    public void uploadPhotos(List<PhotoDto> photos) {
        if (timer.activate()) {
            uploadPhotosToServer(photos);
            bufferedPhotos.clear();
        } else {
            bufferedPhotos.addAll(photos);
        }

    }

    private void uploadPhotosToServer(List<PhotoDto> photos) {
        PhotoDto mostRecent = selectMostRecentPhoto(photos);
        if (mostRecent == null) {
            return;
        }

        FTPClient client = new FTPClient();
        try {
            client.connect(ftpServerUrl, ftpServerPort);
            client.login(ftpServerLogin, ftpServerPass);
            client.enterLocalPassiveMode();
            client.setFileType(FTP.BINARY_FILE_TYPE);

            String remoteFile = "radekMeteo/actual.jpg";

            try (InputStream inputStream = new FileInputStream(mostRecent.getFile())) {
                client.storeFile(remoteFile, inputStream);
                MeteoLogger.log("Uploading file '" + mostRecent.getFile().getName() + "' as remote file '" + ftpServerUrl + ":" + ftpServerPort + "/(...)/" + remoteFile + "'.");
            }
        } catch (IOException ex) {
            MeteoLogger.log(ex);
        } finally {
            if (client.isConnected()) {
                try {
                    client.logout();
                    client.disconnect();
                } catch (IOException ex) {
                    MeteoLogger.log(ex);
                }
            }
        }

    }

    private PhotoDto selectMostRecentPhoto(List<PhotoDto> photos) {
        Date recent = new Date(0L);
        PhotoDto recentPhoto = null;
        for (PhotoDto photo : photos) {
            try {
                BasicFileAttributes attributes = Files.readAttributes(photo.getFile().toPath(), BasicFileAttributes.class);
                Date lastModification = new Date(attributes.lastModifiedTime().toMillis());
                if (lastModification.after(recent)) {
                    recent = lastModification;
                    recentPhoto = photo;
                }
            } catch (IOException ex) {
                MeteoLogger.log(ex);
            }
        }
        return recentPhoto;
    }

}
