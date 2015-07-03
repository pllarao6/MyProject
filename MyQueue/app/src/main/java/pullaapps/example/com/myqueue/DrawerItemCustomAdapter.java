package pullaapps.example.com.myqueue;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class DrawerItemCustomAdapter extends BaseAdapter {
    Context mContext;
    int layoutResourceId;
    ArrayList<ObjectDrawerItem> arrayitems;

    public DrawerItemCustomAdapter(Context mContext, ArrayList<ObjectDrawerItem> listarray) {
        super();
        this.mContext = mContext;
        this.arrayitems=listarray;
    }

    @Override
    public Object getItem(int position) {
        return arrayitems.get(position);
    }
    public int getCount() {
        // TODO Auto-generated method stub
        return arrayitems.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ImageText
    {
        TextView title_item;
        ImageView icon;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageText view;

        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();

        if(convertView==null)
        {
            view=new ImageText();

            ObjectDrawerItem itm=arrayitems.get(position);

            convertView = inflater.inflate(R.layout.listview_item_row, null);

            view.icon = (ImageView) convertView.findViewById(R.id.imageViewIcon);
            view.title_item = (TextView) convertView.findViewById(R.id.textViewName);

            view.title_item.setText(itm.getTitle());

            view.icon.setImageResource(itm.getIcon());

            convertView.setTag(view);
        }
        else
        {
            view=(ImageText)convertView.getTag();
        }
        return convertView;
    }
}
