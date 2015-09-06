package meteocontroller;

/**
 * Trida pro udrzeni instanci singletonu
 *
 * @author Jan Novak <novano@mail.muni.cz>
 */
public class SpringContext {

    private static PhotoGetter photoGetter = new PhotoGetterImpl();
    private static PhotoUploader photoUploader = new PhotoUploaderImpl();

    public static PhotoGetter getPhotoGetter() {
        return photoGetter;
    }

    public static PhotoUploader getPhotoUploader() {
        return photoUploader;
    }

}
