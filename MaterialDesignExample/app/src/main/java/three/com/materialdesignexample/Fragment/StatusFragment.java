package three.com.materialdesignexample.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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
public class StatusFragment extends android.support.v4.app.Fragment {

    private ListView statusList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton fab;
    private StatusAdapter statusAdapter;
    private List<AVObject> data = new ArrayList<AVObject>();
    private View footerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.status_framgment, null);

        statusList = (ListView) view.findViewById(R.id.status_listView);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.status_swipe_container);
        fab = (FloatingActionButton) view.findViewById(R.id.status_add_fab);

        statusAdapter = new StatusAdapter(getActivity(), data);

        //加载更多进度条
        footerView = View.inflate(getActivity(), R.layout.footer_progress, null);
        footerView.setVisibility(View.GONE);
        statusList.addFooterView(footerView);

        statusList.setAdapter(statusAdapter);

        initFab();

        statusList.setOnScrollListener(new AbsListView.OnScrollListener() {

            /*
             * 定义一个布尔变量，
             * 用于判断滑动方向是否向下
             */
            private boolean moveToBottom = false;

            /*
             * 定义一个整型变量，
             * 用于记录ListView之前可见的第一个列表项的索引值，
             * 主要用于判断ListView的滑动方向是否向下，
             * 具体的使用方法见下方
             */
            private int previous = 0;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (previous < firstVisibleItem) {
                    moveToBottom = true;
                } else if (previous > firstVisibleItem) {
                    moveToBottom = false;
                }
                previous = firstVisibleItem;

                if (totalItemCount == firstVisibleItem + visibleItemCount && moveToBottom) {
                    if (footerView.getVisibility() == View.GONE && data.size() >= 10) {
                        Log.d("winson", "加载更多");
                        footerView.setVisibility(View.VISIBLE);
                        findMoreData();
                    }
                }
            }
        });

        swipeRefreshLayout.setColorSchemeColors(R.color.mainColor);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getStatus();
                footerView.setVisibility(View.GONE);
                skipCount=10;
            }
        });


        return view;
    }
    private int skipCount=10;
    private void findMoreData() {

        AVQuery<AVObject> avQuery = new AVQuery<AVObject>(DbOpenHelper.STATUS_TABLE);
        avQuery.setCachePolicy(AVQuery.CachePolicy.NETWORK_ONLY);
        avQuery.skip(skipCount);
        skipCount+=10;

        avQuery.setLimit(10);
        //id降序
        avQuery.orderByDescending("createdAt");
        avQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    Log.d("winson", list.size() + "个");
                    if (list.size() == 0) {
                        Toast.makeText(getActivity(), "已经没有更多帖子啦", Toast.LENGTH_SHORT).show();
                        footerView.setVisibility(View.INVISIBLE);
                    } else {
                        notifyDataSetChanged(list, false);
                        footerView.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(getActivity(), "你的网络好像有点问题，重新试试吧", Toast.LENGTH_SHORT).show();
                    Log.d("winson", "出错：" + e.getMessage());
                    footerView.setVisibility(View.GONE);
                }
            }
        });

    }

    private void getStatus() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        findNewData(getActivity(), AVQuery.CachePolicy.NETWORK_ONLY, new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        Toast.makeText(getActivity(), "没有任何帖子", Toast.LENGTH_SHORT).show();
                    }
                    Log.d("winson", list.size() + "个");
                    notifyDataSetChanged(list, true);
                    statusList.setSelection(0);
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    if (!e.getMessage().contains("Cache")) {
                        Toast.makeText(getActivity(), "你的网络好像有点问题，刷新试试吧", Toast.LENGTH_SHORT).show();
                    }

                    Log.d("winson", "出错：" + e.getMessage());
                }
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
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
        if (avQuery.hasCachedResult()) {
            avQuery.clearCachedResult();
        }
//        //取(Integer) SharedPreferencesUtil.get(context, SharedPreferencesUtil.PER_GOODS_STATUS_COUNT, 10)条
        avQuery.setLimit(10);
        //id降序
        avQuery.orderByDescending("createdAt");
        avQuery.findInBackground(callback);
    }

    @Override
    public void onStart() {
        super.onStart();
        getStatus();
    }

    @Override
    public void onDestroy() {


        super.onDestroy();
    }
}
