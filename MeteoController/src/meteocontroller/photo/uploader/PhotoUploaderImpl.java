package meteocontroller.photo.uploader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import meteocontroller.MeteoController;
import meteocontroller.MeteoLogger;
import meteocontroller.SpringContext;
import meteocontroller.Timer;

import meteocontroller.dto.PhotoDto;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

/**
 *
 * @author Jan Novak <novano@mail.muni.cz>
 */
public class PhotoUploaderImpl implements PhotoUploader {

    private Timer timer;

    private static String ftpServerUrl;
    private static int ftpServerPort;
    private static String ftpServerLogin;
    private static String ftpServerPass;

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
        MeteoLogger.log("upload method start with " + photos.size() + " files");
        if (photos != null && photos.size() > 0) {
            bufferedPhotos.addAll(photos);

            if (timer.activate()) {
                PhotoDto photo = selectMostRecentPhoto(photos);
                if (photo != null) {
                    uploadPhotosToServer(photo);
                    bufferedPhotos.clear();
                }
            }
        }
        MeteoLogger.log("upload method end");
    }

    private void uploadPhotosToServer(PhotoDto photo) {
        MeteoLogger.log("upload timer activated");

        try {
            String remoteFile = "radekMeteo/actual.jpg";
            try (InputStream inputStream = new FileInputStream(photo.getFile())) {
                MeteoLogger.log("storing to FTP server");
                client.storeFile(remoteFile, inputStream);
                MeteoLogger.log("storing to FTP server completed");
                MeteoLogger.log("Uploading file '" + photo.getFile().getName() + "' as remote file '" + remoteFile + "'.");
            }
        } catch (SocketException ex) {
            closeConnection(client);
            client = getConnection();
            //TODO pockej treba 10s a pak az se znova pripoj .. sak vis 
            uploadPhotosToServer(photo);
        } catch (IOException ex) {
            MeteoLogger.log(ex);
        }

    }

    public FTPClient client = null;

    private FTPClient getConnection() {
        if (client == null) {
            try {
                client = new FTPClient();
                MeteoLogger.log("connecting FTP server");
                client.connect(ftpServerUrl, ftpServerPort);
                MeteoLogger.log("logging FTP server");
                client.login(ftpServerLogin, ftpServerPass);
                client.enterLocalPassiveMode();
                client.setFileType(FTP.BINARY_FILE_TYPE);
            } catch (IOException ex) {
                MeteoLogger.log(ex);
                return null;
            }
        }

        return client;
    }

    private void closeConnection(FTPClient client) {
        if (client != null) {
            try {
                client.logout();
                client.disconnect();
            } catch (IOException ex) {
                MeteoLogger.log(ex);
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
