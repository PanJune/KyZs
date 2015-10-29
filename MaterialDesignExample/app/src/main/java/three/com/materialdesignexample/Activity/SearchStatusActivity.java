package three.com.materialdesignexample.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

import three.com.materialdesignexample.Adapter.PhoneAdapter;
import three.com.materialdesignexample.Db.Db;
import three.com.materialdesignexample.Models.PhoneInfo;
import three.com.materialdesignexample.R;
import three.com.materialdesignexample.Util.HandleResponseUtil;

/**
 * Created by Administrator on 2015/10/28.
 */
public class SearchStatusActivity extends AppCompatActivity {
    public static void startSearchStatusActivity(Context context) {
        Intent intent = new Intent(context, SearchStatusActivity.class);
        context.startActivity(intent);
    }

    private SearchView searchView;
    private ListView search_list;
    private PhoneAdapter phoneAdapter;
    private ArrayList<PhoneInfo> phoneInfos =new ArrayList<PhoneInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_status);

        search_list= (ListView) findViewById(R.id.search_listView);
        phoneAdapter=new PhoneAdapter(this,phoneInfos);
        search_list.setAdapter(phoneAdapter);

        //设置actionBar的标题
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2196f3")));
        actionBar.setTitle("搜索联系人");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.seach_menu, menu);
        initSearchView(menu);
        return true;
    }

    private void initSearchView(Menu menu) {
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint("请输入要搜索的姓名");
        searchView.onActionViewExpanded();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if(HandleResponseUtil.db==null){
                    HandleResponseUtil.db= Db.getInstance(SearchStatusActivity.this);
                }
                else {
                    if(HandleResponseUtil.db.loadPhoneInfoByName(query, phoneInfos)){
                        phoneAdapter.notifyDataSetChanged();
                    }
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
