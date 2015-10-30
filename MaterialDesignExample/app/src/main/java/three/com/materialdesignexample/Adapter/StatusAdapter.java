package three.com.materialdesignexample.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;

import java.util.List;

import three.com.materialdesignexample.Activity.StatusActivity;
import three.com.materialdesignexample.Db.DbOpenHelper;
import three.com.materialdesignexample.R;
import three.com.materialdesignexample.widget.TimeHelper;

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

        Viewholder holder=null;

        if(convertView==null){
            holder=new Viewholder();
            convertView = View.inflate(context, R.layout.status_item_list, null);
            holder.nameTv= (TextView) convertView.findViewById(R.id.status_name_tv);
            holder.dateTv= (TextView) convertView.findViewById(R.id.status_time_tv);
            holder.contentTv= (TextView) convertView.findViewById(R.id.status_content_tv);
            holder.commentBtn = (TextView) convertView.findViewById(R.id.status_comment_btn);
            holder.allLayout = convertView.findViewById(R.id.all_layout);
            convertView.setTag(holder);
        }
        else
            holder= (Viewholder) convertView.getTag();

        final AVObject avObject=data.get(position);
        //名字
        holder.nameTv.setText(avObject.getString(DbOpenHelper.STATUS_USER));
        //时间
        holder.dateTv.setText(TimeHelper.timeToFriendlyTime(avObject.getCreatedAt().toString()));
        //内容
        holder.contentTv.setText(avObject.getString(DbOpenHelper.STATUS_CONTETT));
        //评论
        holder.commentBtn.setText(avObject.getLong(DbOpenHelper.STATUS_COUNT) + "");
        //打开详细页面
        holder.allLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StatusActivity.startStatusActivity(context, avObject.getObjectId());
            }
        });
        holder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StatusActivity.startStatusActivity(context, avObject.getObjectId());
            }
        });

        return convertView;
    }

    private static class Viewholder {

        TextView nameTv;
        TextView dateTv;
        TextView contentTv;
        TextView commentBtn;
        View allLayout;
    }
}
