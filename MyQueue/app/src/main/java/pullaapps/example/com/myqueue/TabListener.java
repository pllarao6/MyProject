package pullaapps.example.com.myqueue;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

/**
 * Created by Prathyusha on 2/7/2015.
 */
public class TabListener implements ActionBar.TabListener {

    Fragment fragment;
    String name;
    public TabListener(String name)
    {
        this.name=name;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        Fragment tabFragment=new TabFragment();
        Bundle bundle=new Bundle();
        bundle.putString("name",name);
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
