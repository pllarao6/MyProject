package pullaapps.example.com.myqueue;

/**
 * Created by vankayap on 3/16/2015.
 */
public class MealDrawerItem {
	
    public String title;
	public int price;   
	
    // Constructor.
    public MealDrawerItem(String title,int price)
    {
        this.title = title;
        this.price = price;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }
}