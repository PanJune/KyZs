package three.com.materialdesignexample.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import three.com.materialdesignexample.Models.PhoneInfo;
import three.com.materialdesignexample.R;

/**
 * Created by Administrator on 2015/10/27.
 */
public class PhoneAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<PhoneInfo> phoneInfos =new ArrayList<PhoneInfo>();

    public PhoneAdapter(Context context, ArrayList<PhoneInfo> phoneInfos) {
        this.context = context;
        this.phoneInfos = phoneInfos;
    }

    @Override
    public int getCount() {
        return phoneInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return phoneInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.phone_item_list, null);
            holder.head_name_tv = (TextView) convertView.findViewById(R.id.head_name_tv);
            holder.class_name_tv = (TextView) convertView.findViewById(R.id.class_name_tv);
            holder.phone_number_tv = (TextView) convertView.findViewById(R.id.phone_number_tv);
            holder.name_tv = (TextView) convertView.findViewById(R.id.name_tv);
            holder.school_number_tv= (TextView) convertView.findViewById(R.id.school_number_tv);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }



        Random random = new Random();
        int circleShape = 0;
        int count=random.nextInt(10);
        int color = 0;

        if (count>=8) {
            circleShape = R.drawable.circle_deep_orange;
            color = context.getResources().getColor(R.color.deep_orange_500);
        } else if (count>=6 &&count<8) {
            circleShape = R.drawable.circle_teal;
            color = context.getResources().getColor(R.color.indigo_500);
        } else if (count>=4 &&count<6) {
            circleShape = R.drawable.circle_cyan;
            color = context.getResources().getColor(R.color.cyan_500);
        } else if (count>=2 &&count<4) {
            circleShape = R.drawable.circle_blue;
            color = context.getResources().getColor(R.color.blue_500);
        } else  {
            circleShape = R.drawable.circle_amber;
            color = context.getResources().getColor(R.color.amber_500);
        }


        PhoneInfo phoneInfo= phoneInfos.get(position);

        holder.head_name_tv.setBackgroundResource(circleShape);

        holder.head_name_tv.setText(phoneInfo.getPeopleName().substring(0,1));
        holder.class_name_tv.setText(phoneInfo.getClassName());
        holder.phone_number_tv.setText(phoneInfo.getPhoneNumber());
        holder.name_tv.setText(phoneInfo.getPeopleName());
        holder.school_number_tv.setText(phoneInfo.getSchoolNumber());

        return convertView;
    }

    private static class ViewHolder {
        TextView head_name_tv;
        TextView class_name_tv;
        TextView phone_number_tv;
        TextView name_tv;
        TextView school_number_tv;
    }
}
