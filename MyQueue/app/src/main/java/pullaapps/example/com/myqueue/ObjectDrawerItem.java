package pullaapps.example.com.myqueue;

import android.graphics.Bitmap;

/**
 * Created by vankayap on 3/16/2015.
 */
public class ObjectDrawerItem {
    public int icon;
    public String title;
    // Constructor.
    public ObjectDrawerItem(String title,int icon)
    {
        this.title = title;
        this.icon = icon;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public int getIcon() {
        return icon;
    }
    public void setIcon(int icon) {
        this.icon = icon;
    }

}
