package ua.curriculum.fx;

public class ComboBoxItem {
    private String objectId;
    private String objectDisplayName;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getObjectDisplayName() {
        return objectDisplayName;
    }

    public void setObjectDisplayName(String objectDisplayName) {
        this.objectDisplayName = objectDisplayName;
    }

    public ComboBoxItem() {
    }

    public ComboBoxItem(String objectId, String objectDisplayName){
        this.objectId = objectId;
        this.objectDisplayName = objectDisplayName;
    }

    public String toString(){
        return objectDisplayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ComboBoxItem)) return false;

        ComboBoxItem that = (ComboBoxItem) o;

        if (!objectId.equals(that.objectId)) return false;
        return objectDisplayName.equals(that.objectDisplayName);
    }

    @Override
    public int hashCode() {
        int result = objectId.hashCode();
        result = 31 * result + objectDisplayName.hashCode();
        return result;
    }
}
