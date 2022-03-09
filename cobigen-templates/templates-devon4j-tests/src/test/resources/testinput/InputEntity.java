package testinput.testapp.testcomponent.dataaccess.api;

import java.util.Collection;

@SuppressWarnings("javadoc")
public class InputEntity {

    private String stringField;

    private Collection<String> collectionOfStrings;

    private int nativeInt;

    public String getStringField() {
        return stringField;
    }

    public void setStringField(String stringField) {
        this.stringField = stringField;
    }

    public Collection<String> getCollectionOfStrings() {
        return collectionOfStrings;
    }

    public void setCollectionOfStrings(Collection<String> collectionOfStrings) {
        this.collectionOfStrings = collectionOfStrings;
    }

    public int getNativeInt() {
        return nativeInt;
    }

    public void setNativeInt(int nativeInt) {
        this.nativeInt = nativeInt;
    }

}
