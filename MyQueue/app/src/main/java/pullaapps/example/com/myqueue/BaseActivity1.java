package pullaapps.example.com.myqueue;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class BaseActivity1 extends FragmentActivity {

    protected String[] titles;
    private DrawerLayout mDrawerLayout;
    protected ListView mDrawerList;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    protected FrameLayout frameLayout;
    protected static boolean isLaunch=true;
    private ArrayList<ObjectDrawerItem> NavItms;
    private TypedArray NavIcons;
    SessionManager session;
    protected int flag1,flag2,flag3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base1);
        session = new SessionManager(getApplicationContext());
        titles = getResources().getStringArray(R.array.nav_options);
        NavIcons = getResources().obtainTypedArray(R.array.navigation_icons);
        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout1);
        frameLayout = (FrameLayout)findViewById(R.id.content_frame1);
        View header = getLayoutInflater().inflate(R.layout.header, null);
        header.setClickable(false);
        header.setActivated(false);
        mDrawerList = (ListView) findViewById(R.id.left_drawer1);
        mDrawerList.addHeaderView(header);
        mDrawerList.setItemsCanFocus(false);
        NavItms = new ArrayList<ObjectDrawerItem>();
        NavItms.add(new ObjectDrawerItem(titles[0], NavIcons.getResourceId(0, -1)));
        //Favoritos
        NavItms.add(new ObjectDrawerItem(titles[1], NavIcons.getResourceId(1, -1)));

        NavItms.add(new ObjectDrawerItem(titles[2], NavIcons.getResourceId(2, -1)));

        NavItms.add(new ObjectDrawerItem(titles[3], NavIcons.getResourceId(3, -1)));

        DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this, NavItms);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
            Toast.makeText(getApplicationContext(), "Selected item AT " + pos, Toast.LENGTH_LONG).show();            ;
            selectItem(pos);
        }

    }

    private void selectItem(int position) {

        Fragment fragment = null;
        switch (position) {
            case 1:
                if(flag1==0) {
                    startActivity(new Intent(this, Profile.class));
                    finish();
                    mDrawerList.setItemChecked(position, true);
                }
                mDrawerLayout.closeDrawer(mDrawerList);
                break;
            case 2:
                if(flag2==0) {
                    startActivity(new Intent(this, Store.class));
                    finish();
                    mDrawerList.setItemChecked(position, true);
                }
                mDrawerLayout.closeDrawer(mDrawerList);
                break;
            case 3:
                if(flag3==0)
                {
                    startActivity(new Intent(this, MyOrders.class));
                    finish();
                    mDrawerList.setItemChecked(position, true);
                }
                mDrawerLayout.closeDrawer(mDrawerList);
                break;
            case 4:
                session.logoutUser();
                finish();
                break;
            default:
                position=1;
                break;
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            mDrawerList.setItemChecked(position, true);
            setTitle(titles[position-1]);
            mDrawerLayout.closeDrawer(mDrawerList);
            // Actualizamos el contenido segun la opcion elegida
        }
    }

    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }
}
