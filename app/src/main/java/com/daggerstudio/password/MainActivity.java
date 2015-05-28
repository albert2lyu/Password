package com.daggerstudio.password;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.LruCache;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.daggerstudio.password.dao.DaoMaster;
import com.daggerstudio.password.dao.DaoSession;
import com.daggerstudio.password.dao.Rec;
import com.daggerstudio.password.dao.RecDao;

import java.util.List;


public class MainActivity extends ActionBarActivity {
    private static final String REC_BUNDLE_TAG = "REC_BUNDLE_TAG";
    private static final String SHAREDPREFERENCE_TAG = "SHAREDPREFERENCE_TAG";
    private static final String MAIN_PWD_TAG = "MAIN_PWD_TAG";
    private static final String CONFIRM_DELETE_TAG = "确定删除吗？";


    private ListView mainListView;
    private Button mainNewBtn;
    private MainListViewAdapter mainAdapter;
    private SharedPreferences sharedPreferences;
    private DaoMaster daoMaster;
    private SQLiteDatabase db;
    private DaoSession daoSession;
    private RecDao recDao;
    private List<Rec> allRecs;
    private LruCache listViewCache;
    private boolean listViewBusy = false;
    private InfoPack focusedInfoPack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainListView = (ListView)findViewById(R.id.main_listView);
        mainListView.setOnScrollListener(new MainLVOnScrollListener());
        listViewCache = new LruCache(40);//主界面ListView的内存缓存
        mainNewBtn = (Button)findViewById(R.id.main_new_btn);
        mainNewBtn.setOnClickListener(new MainBtnOnClickListener());

        //加载SharedPreference和数据库对象
        if (sharedPreferences == null){
            sharedPreferences = getSharedPreferences(SHAREDPREFERENCE_TAG, MODE_PRIVATE);
        }
        if(daoMaster == null || db == null || recDao == null || daoSession == null){
            db = new DaoMaster.DevOpenHelper(this, "rec-db", null).getReadableDatabase();
            daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
            recDao = daoSession.getRecDao();
        }

        //TODO 事成之后，删掉这段测试数据注入部分
        if(sharedPreferences.getBoolean("firsttime", true)){
            //        public Rec(String site_url, String site_brief, String user_name, byte[] encypted_content, String note, Long id) {
            Rec rec;
            for(int i = 0;i<15;i++){
                rec = new Rec("site_url"+i, "brief"+i, "usr_name_"+i, ("password"+i).getBytes(), null, null);
                recDao.insert(rec);
            }
            sharedPreferences.edit().putBoolean("firsttime", false).commit();
            sharedPreferences.edit().putString(MAIN_PWD_TAG, "123123").commit();
        }


        //下面把数据放入内存进行缓存
        //TODO 从数据库读数据部分放入异步线程里！现在先懒得弄
        allRecs = (List<Rec>)recDao.loadAll();

        mainAdapter = new MainListViewAdapter(this);
        mainListView.setAdapter(mainAdapter);
    }


    private void updateData(){
        allRecs = (List<Rec>)recDao.loadAll();
        mainAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onStart(){
        super.onStart();
        updateData();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class MainListViewAdapter extends BaseAdapter{

        Context context;

        public MainListViewAdapter(Context context){
            this.context = context;
        }

        @Override
        public int getCount() {
            return allRecs.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
//            TODO 如果正在滚动，先不加载。这里有些危险，估计会出现问题，先看看吧
//            if(listViewBusy){
//                return convertView;
//            }

            InfoPack ip = new InfoPack(allRecs.get(position).getId(), position);
            if(convertView == null){
                //TODO 先从缓存里向外读一个LinearLayout   这里为什么不对？还需要研究研究
//                LinearLayout ll = (LinearLayout)listViewCache.get(position);
//                if(ll == null){
//                    ll = (LinearLayout) LinearLayout.inflate(context, R.layout.card_view_main, null);
//                    listViewCache.put(position, ll);
//                    listViewCache.put(position, (LinearLayout) LinearLayout.inflate(context, R.layout.card_view_main, null));
//                    ll = (LinearLayout)listViewCache.get(position);
//                }
                LinearLayout ll = (LinearLayout) LinearLayout.inflate(context, R.layout.card_view_main, null);
                ll.setTag(ip);

                TextView tv = (TextView)ll.findViewById(R.id.main_textview);
                tv.setText(allRecs.get(position).getSite_brief());
                tv.setTag(ll.getTag());
                tv.setOnClickListener(new MainLVTagOnClickListener());

                Button btn = (Button)ll.findViewById(R.id.main_delbtn);
                btn.setOnClickListener(new MainBtnDelOnClickListener());
                btn.setTag(ll.getTag());
                return ll;
            }else{
                convertView.setTag(ip);
                TextView tv = (TextView)convertView.findViewById(R.id.main_textview);
                tv.setText(allRecs.get(position).getSite_brief());
                tv.setTag(convertView.getTag());
                Button btn = (Button)convertView.findViewById(R.id.main_delbtn);
                btn.setTag(convertView.getTag());
                return convertView;
            }
        }
    }


    private class MainLVTagOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
//            Toast.makeText(v.getContext(), "click on "+((InfoPack)v.getTag()).getRecId().toString(), Toast.LENGTH_SHORT).show();
            InfoPack ip = (InfoPack)v.getTag();
            Bundle bundle = new Bundle();
            bundle.putParcelable(REC_BUNDLE_TAG, allRecs.get(ip.getPosition()));
            Intent i = new Intent(MainActivity.this, DetailActivity.class);
            i.putExtras(bundle);
            startActivity(i);
        }
    }


    private class MainBtnOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent i = new Intent(MainActivity.this, EditActivity.class);
            startActivity(i);
        }
    }



    private class MainBtnDelOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            focusedInfoPack = (InfoPack)v.getTag();
            new AlertDialog.Builder(v.getContext()).setTitle(CONFIRM_DELETE_TAG)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            long id = focusedInfoPack.getId().longValue();
                            int p = focusedInfoPack.getPosition();
                            allRecs.remove(p);
                            recDao.deleteByKey(id);
                            mainAdapter.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    }).show();
        }
    }


    private class MainLVOnScrollListener implements AbsListView.OnScrollListener {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState){
                case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                    listViewBusy = true;
                    break;
                default:
                    listViewBusy = false;
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }
    }

    //TODO 非常不喜欢这个设计但先这么凑活着用吧
    private class InfoPack{
        private Long id;

        private InfoPack(Long id, int position) {
            this.id = id;
            this.position = position;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        private int position;
    }
}
