package com.daggerstudio.password;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.daggerstudio.dao.DaoMaster;
import com.daggerstudio.dao.DaoSession;
import com.daggerstudio.dao.Rec;
import com.daggerstudio.dao.RecDao;
import com.daggerstudio.password.R;
import com.daggerstudio.password.utils.EncDecUtil;
import com.daggerstudio.password.utils.PasswordUtil;

public class EditActivity extends ActionBarActivity {
    public static final int DEFAULT_RAMDON_PWD_LENGTH = 14;
    private static final String REC_BUNDLE_TAG = "REC_BUNDLE_TAG";
    private static final String PARCEL_TAG = "PARCEL_TAG";
    private static final String SHAREDPREFERENCE_TAG = "SHAREDPREFERENCE_TAG";
    private static final String MAIN_PWD_TAG = "MAIN_PWD_TAG";

    //TODO
    private static final String MSG1 = "请先登录并设置主密码";

    TextView brief;
    TextView usrName;
    TextView pwd;
    TextView siteUrl;
    TextView note;
    Button genBtn;
    Button postBtn;
    Rec recTrans = null;
    SharedPreferences sp;
    RecDao recDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        brief = (TextView) findViewById(R.id.edit_et_brief);
        usrName = (TextView) findViewById(R.id.edit_et_usrname);
        pwd = (TextView) findViewById(R.id.edit_et_pwd);
        siteUrl = (TextView) findViewById(R.id.edit_et_site);
        note = (TextView) findViewById(R.id.edit_et_note);
        genBtn = (Button) findViewById(R.id.edit_gen_btn);
        genBtn.setOnClickListener(new GenOnClickListener());
        postBtn = (Button) findViewById(R.id.edit_post_btn);
        postBtn.setOnClickListener(new PostOnClickListener());

        //从Bundle里读取Rec信息，如果能拿到则为修改模式，否则是新建模式
        if (null != savedInstanceState) {
            Bundle bundle = savedInstanceState.getBundle(REC_BUNDLE_TAG);
            recTrans = bundle.getParcelable(PARCEL_TAG);
        }


        if (null != recTrans) {
            if (null != recTrans.getSite_url()) siteUrl.setText(recTrans.getSite_url());
            if (null != recTrans.getSite_brief()) brief.setText(recTrans.getSite_brief());
            pwd.setText("******");
            if (null != recTrans.getNote()) note.setText(recTrans.getNote());
            if (null != recTrans.getUser_name()) usrName.setText(recTrans.getSite_url());
        }

        sp = getSharedPreferences(SHAREDPREFERENCE_TAG, MODE_PRIVATE);
        recDao = (new DaoMaster(new DaoMaster.DevOpenHelper(this, "rec-db", null).getReadableDatabase())).newSession().getRecDao();
    }

    @Override
    protected void onStart(){
        super.onStart();
//        getActionBar().setDisplayHomeAsUpEnabled(true);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class GenOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            //TODO 完成生成密码的dialog
            pwd.setText(PasswordUtil.genertRandomPassword(DEFAULT_RAMDON_PWD_LENGTH, true, true));
        }
    }


    private class PostOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Rec nRec = new Rec(siteUrl.getText().toString(), brief.getText().toString(), usrName.getText().toString(), null, note.getText().toString(), null);
            String mainPwdHash = sp.getString(MAIN_PWD_TAG, "");
            if("".equals(mainPwdHash)){
                Toast.makeText(v.getContext(), MSG1, Toast.LENGTH_SHORT).show();
                return;
            }
            byte[] enc_content = EncDecUtil.encrypt(pwd.getText().toString(), mainPwdHash);
            nRec.setEncypted_content(enc_content);
            recDao.insert(nRec);
            EditActivity.this.finish();
        }
    }

}
