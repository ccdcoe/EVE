package eve.wuti.jpa;

import javax.persistence.Column;
import javax.persistence.OneToOne;
import java.lang.reflect.Field;

/**
 * Creado por Jesus el 24/3/15.
 */
public class JPAField {

    public static boolean isRequired(Class aClass, String fieldName) {
        Field field = getField(aClass, fieldName);

        Column column = field.getAnnotation(Column.class);
        OneToOne oneToOne = field.getAnnotation(OneToOne.class);

        if (column != null)
            return !column.nullable();

        //noinspection SimplifiableIfStatement
        if (oneToOne != null)
            return !oneToOne.optional();

        return false;
    }

    public static boolean isString(Class aClass, String fieldName) {
        Field field = getField(aClass, fieldName);
        return field.getType().isAssignableFrom(String.class);
    }


    public static int getLength(Class aClass, String fieldName) {
        if (!isString(aClass, fieldName)) return -1;

        Field field = getField(aClass, fieldName);
        Column fieldAnnotation = field.getAnnotation(Column.class);

        if (fieldAnnotation == null)
            throw new RuntimeException(aClass + " does not seem to contain the field: " + fieldName) ;
        else
            return fieldAnnotation.length();
    }

    private static Field getField(Class aClass, String nameField) {
        try {
            return aClass.getDeclaredField(nameField);
        } catch (Exception e) {
            throw new RuntimeException(aClass + "' does not contain any '" + nameField + "' (or does not have a @Column annotation)");
        }
    }
}
