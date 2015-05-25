package com.daggerstudio.password;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.daggerstudio.password.R;
import com.daggerstudio.password.utils.PasswordUtil;

public class EditActivity extends ActionBarActivity {
    static final int DEFAULT_RAMDON_PWD_LENGTH = 14;

    TextView brief;
    TextView usrName;
    TextView pwd;
    TextView siteUrl;
    TextView note;
    Button genBtn;
    Button postBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        brief = (TextView)findViewById(R.id.edit_et_brief);
        usrName = (TextView)findViewById(R.id.edit_et_usrname);
        pwd = (TextView)findViewById(R.id.edit_et_pwd);
        siteUrl = (TextView)findViewById(R.id.edit_et_site);
        note = (TextView)findViewById(R.id.edit_et_note);
        genBtn = (Button)findViewById(R.id.edit_gen_btn);
        postBtn = (Button)findViewById(R.id.edit_post_btn);

        //从Bundle里读取Rec信息，如果能拿到则为修改模式，否则是新建模式
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

        }
    }

}
