package cat.app.gmap.model;

public class RowItem {
    int imageId;

    String title;

    public RowItem(int imageId, String title) {
        this.imageId = imageId;
        this.title = title;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getTitle() {
        return title;
    }

    public void setStringTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}