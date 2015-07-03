package pullaapps.example.com.myqueue;

/**
 * Created by vankayap on 3/16/2015.
 */
public class StoreDrawerItem {
    public String imageURL;
    public String title;
    public String subtitle;
    // Constructor.

    public StoreDrawerItem()
    {

    }

    public StoreDrawerItem(String title,String subtitle,String imageURL)
    {
        this.title=title;
        this.subtitle=subtitle;
        this.imageURL=imageURL;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getsubTitle() {
        return subtitle;
    }
    public void setsubTitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getImageURL() {
        return imageURL;
    }
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
