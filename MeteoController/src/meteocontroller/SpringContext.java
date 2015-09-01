package meteocontroller;

/**
 * Trida pro udrzeni instanci singletonu
 *
 * @author Jan Novak <novano@mail.muni.cz>
 */
public class SpringContext {

    private static PhotoGetter photoGetter = new PhotoGetterImpl();

    public static PhotoGetter getPhotoGetter() {
        return photoGetter;
    }

}
