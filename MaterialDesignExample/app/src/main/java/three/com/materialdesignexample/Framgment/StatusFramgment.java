package three.com.materialdesignexample.Framgment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import three.com.materialdesignexample.Activity.AddActivity;
import three.com.materialdesignexample.Adapter.StatusAdapter;
import three.com.materialdesignexample.Db.DbOpenHelper;
import three.com.materialdesignexample.R;

/**
 * Created by Administrator on 2015/10/26.
 */
public class StatusFramgment extends android.support.v4.app.Fragment {

    private ListView statusList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton fab;
    private StatusAdapter statusAdapter;
    private List<AVObject> data = new ArrayList<AVObject>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.status_framgment,null);
        statusList= (ListView) view.findViewById(R.id.status_listView);
        swipeRefreshLayout= (SwipeRefreshLayout) view.findViewById(R.id.status_swipe_container);
        fab= (FloatingActionButton) view.findViewById(R.id.status_add_fab);



        statusAdapter=new StatusAdapter(getActivity(),data);
        statusList.setAdapter(statusAdapter);

        initFab();

        findNewData(getActivity(),AVQuery.CachePolicy.NETWORK_ONLY,new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        Toast.makeText(getActivity(),"没有任何帖子",Toast.LENGTH_SHORT);
                    }
                    Log.d("winson", list.size() + "个");
                    notifyDataSetChanged(list, true);
                    statusList.setSelection(0);
                } else {
                    if (!e.getMessage().contains("Cache")) {
                        Toast.makeText(getActivity(),"你的网络好像有点问题，刷新试试吧",Toast.LENGTH_SHORT);
                    }

                    Log.d("winson", "出错：" + e.getMessage());
                }
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        return view;
    }

    private void notifyDataSetChanged(List<AVObject> list, boolean isRefresh) {
        if (isRefresh) {
            data.clear();
        }
        data.addAll(list);
        statusAdapter.notifyDataSetChanged();

    }

    private void initFab() {

        fab.attachToListView(statusList);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(getActivity(), AddActivity.class);
                startActivity(addIntent);
            }
        });

    }

    public void findNewData(Context context, AVQuery.CachePolicy cachePolicy, FindCallback callback) {
        AVQuery<AVObject> avQuery = new AVQuery<AVObject>(DbOpenHelper.STATUS_TABLE);
        avQuery.setCachePolicy(cachePolicy);

        //若有缓存则清空
        if(avQuery.hasCachedResult()) {
            avQuery.clearCachedResult();
        }
        //取(Integer) SharedPreferencesUtil.get(context, SharedPreferencesUtil.PER_GOODS_STATUS_COUNT, 10)条
        avQuery.setLimit(10);
        //id降序
        avQuery.orderByDescending("createdAt");
        avQuery.findInBackground(callback);
    }
}
