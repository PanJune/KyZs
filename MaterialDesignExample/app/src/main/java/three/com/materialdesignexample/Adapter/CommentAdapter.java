package three.com.materialdesignexample.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;

import java.util.List;

import three.com.materialdesignexample.Db.DbOpenHelper;
import three.com.materialdesignexample.R;
import three.com.materialdesignexample.widget.TimeHelper;

/**
 * Created by Administrator on 2015/10/30.
 */
public class CommentAdapter extends BaseAdapter {

    private Context context;
    private List<AVObject> data;

    public CommentAdapter(Context context, List<AVObject> data) {
        this.context=context;
        this.data=data;
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
        ViewHolder holder = null;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.comment_item_list, null);
            holder.nameTv = (TextView) convertView.findViewById(R.id.comment_name_tv);
            holder.timeTv = (TextView) convertView.findViewById(R.id.comment_time_tv);
            holder.contentTv = (TextView) convertView.findViewById(R.id.comment_comment_tv);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        final AVObject avComment = data.get(position);

        //名字
        holder.nameTv.setText(avComment.getString(DbOpenHelper.COMMENT_SENDUSER));
        //时间
        holder.timeTv.setText(TimeHelper.timeToFriendlyTime(avComment.getCreatedAt().toString()));
        //内容
        String content = avComment.getString(DbOpenHelper.COMMENT_CONTETT);
        String avReceive = avComment.getString(DbOpenHelper.COMMENT_RECEIVE_USER);
        if(avReceive != null) {
            content = "回复 " + avReceive + ": " + content;
        }
        holder.contentTv.setText(content);

        return convertView;
    }

    private static class ViewHolder {
        TextView nameTv;
        TextView timeTv;
        TextView contentTv;
        TextView deviceTv;
    }
}
