package pullaapps.example.com.myqueue;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

/**
 * Created by Prathyusha on 2/7/2015.
 */
public class TabListener implements ActionBar.TabListener {

    String diet_name;
    public TabListener(String diet_name)
    {
        this.diet_name=diet_name;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        Fragment tabFragment=new TabFragment();
        Bundle bundle=new Bundle();
        bundle.putString("KEY_DIET",diet_name);
        tabFragment.setArguments(bundle);
        ft.replace(R.id.fragment_container, tabFragment);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }
}
