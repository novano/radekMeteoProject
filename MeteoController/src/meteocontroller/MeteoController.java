package meteocontroller;

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
