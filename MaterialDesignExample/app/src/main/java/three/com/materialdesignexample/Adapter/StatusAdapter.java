package three.com.materialdesignexample.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;

import java.util.List;

/**
 * Created by Administrator on 2015/10/26.
 */
public class StatusAdapter extends BaseAdapter {
    private Context context;
    private List<AVObject> data;

    public StatusAdapter(Context context, List<AVObject> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    private static class Viewholder {

        TextView nameTv;
        TextView dateTv;
        TextView categoryTv;
        TextView titleTv;
        TextView contentTv;
        LinearLayout imageLayout;
        TextView commentBtn;
        TextView deviceTv;
        TextView locationTv;
        View allLayout;
    }
}
