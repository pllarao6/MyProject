package pullaapps.example.com.myqueue;

import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DisplayProducts extends BaseActivity implements TabFragment.Communicator{

    ActionBar actionBar;
    Bundle bundle;
    String lat;
    String lon;
    public View inflatedView;
    SessionManager sessionManager;
    private MealAdapter mealAdapter;
    private SearchView searchView;
    private MenuItem searchMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflatedView=getLayoutInflater().inflate(R.layout.activity_display_products,frameLayout);
        actionBar=getActionBar();
        sessionManager=new SessionManager(this);
        HashMap<String, String> location;
        location=sessionManager.getLocationInfo();
        lat=location.get("latitude");
        lon=location.get("longitude");
        if (actionBar != null) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            actionBar.addTab(actionBar.newTab().setText("Veg").setTabListener(new TabListener("Veg")));
            actionBar.addTab(actionBar.newTab().setText("NonVeg").setTabListener(new TabListener("NonVeg")));
            actionBar.addTab(actionBar.newTab().setText("Beverages").setTabListener(new TabListener("Beverages")));
        } else {
            Toast.makeText(getApplicationContext(), "Error in ActionBar", Toast.LENGTH_SHORT).show();
        }
    }

    public void Message(Bundle args)
    {
        Intent intent=new Intent(getApplicationContext(),DisplayItem.class);
        intent.putExtras(args);
        startActivity(intent);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        /*SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        //searchView.setSubmitButtonEnabled(true);
        searchView.setIconified(false);
        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextChange(String newText)
            {
                // this is your adapter that will be filtered
                mealAdapter.getFilter().filter(newText);
                System.out.println("on text chnge text: "+newText);
                return true;
            }
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                // this is your adapter that will be filtered
                mealAdapter.getFilter().filter(query);
                System.out.println("on query submit: "+query);
                return true;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);*/
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.action_search:
                // search action
                return true;
            case R.id.cart:
                // location found
                showCart();
                return true;
            case R.id.action_refresh:
                // refresh
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showCart() {
        Intent i = new Intent(getApplicationContext(), MyCart.class);
        startActivity(i);
    }

    public void setMealAdapter(MealAdapter adapter)
   {
       this.mealAdapter=adapter;
   }
}
