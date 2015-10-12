package meteocontroller;

import meteocontroller.photo.getter.PhotoGetter;
import meteocontroller.photo.uploader.PhotoUploaderImpl;
import meteocontroller.photo.getter.PhotoGetterImpl;
import meteocontroller.photo.uploader.PhotoUploader;

/**
 *
 * @author Johnny
 */
public class MeteoController {
    
    private static SpringContext context;
    
    public static SpringContext getContext() {
        return context;
    }
    
    public static void main(String[] args) {
        MeteoLogger.log("Author Johnny Novak > jan.novak@ibacz.eu");
        
        context = SpringContext.getInstance();
        MeteoLogger.log("Scanning folder " + context.getProperty("folder.path.scan"));
        
        PhotoGetter photoGetter = new PhotoGetterImpl();
        PhotoUploader photoUploader = new PhotoUploaderImpl();
        
        while (true) {
            photoUploader.uploadPhotos(photoGetter.getNewPhotos());
            
            try {
                Thread.sleep(500L);
            } catch (InterruptedException ex) {
                MeteoLogger.log(ex);
            }
        }
        
    }
    
}
