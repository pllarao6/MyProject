package pullaapps.example.com.myqueue;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter extends BaseAdapter{
    Context mContext;
    ArrayList<StoreDrawerItem> arrayitems;
    private LayoutInflater layoutInflater;
    public ImageLoader imageLoader;
    public MyAdapter(Context mContext, ArrayList<StoreDrawerItem> listarray) {
        this.mContext = mContext;
        this.arrayitems=listarray;
        layoutInflater = LayoutInflater.from(mContext);
        imageLoader=new ImageLoader(mContext);
    }

    public Object getItem(int position) {
        return arrayitems.get(position);
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return arrayitems.size();
    }


    public long getItemId(int position) {
        return position;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null)
        {
            Log.e("position",position+"");
            holder=new ViewHolder();
            /*LayoutInflater mInflater = (LayoutInflater) mContext
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);*/
            convertView = layoutInflater.inflate(R.layout.list_row_layout, null);

            //convertView = mInflater.inflate(R.layout.custom_spinner, null);

            holder.title_item = (TextView) convertView.findViewById(R.id.title);
            holder.subtitle_item = (TextView) convertView.findViewById(R.id.address);
            holder.imageView = (ImageView) convertView.findViewById(R.id.thumbImage);
            convertView.setTag(holder);
        }
        else
        {
            holder=(ViewHolder)convertView.getTag();
        }

        StoreDrawerItem itm=arrayitems.get(position);
        holder.title_item.setText(itm.getTitle());
        holder.subtitle_item.setText(itm.getsubTitle());
        imageLoader.DisplayImage(itm.getImageURL(), holder.imageView);
        return convertView;
    }

    static class ViewHolder
    {
        TextView title_item;
        TextView subtitle_item;
        ImageView imageView;
    }
}

