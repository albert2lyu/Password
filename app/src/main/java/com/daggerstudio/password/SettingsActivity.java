package com.daggerstudio.password;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;

import com.daggerstudio.password.assets.GlobalConstants;
import com.daggerstudio.password.assets.ProgressDialogAsyncTaskForBackup;
import com.daggerstudio.password.assets.ProgressDialogAsyncTaskForRecovery;
import com.daggerstudio.password.assets.PwdPickerDialog;
import com.daggerstudio.password.utils.FileUtils;

import java.io.File;
import java.util.Calendar;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceClickListener{
    /**
     * Determines whether to always show the simplified settings UI, where
     * settings are presented in a single list. When false, settings are shown
     * as a master/detail two-pane view on tablets. When true, a single pane is
     * shown on tablets.
     */
    private static final boolean ALWAYS_SIMPLE_PREFS = false;

    private ProgressDialog pd = null;




    Handler handler;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setupSimplePreferencesScreen();

        //TODO 为什么建议静态？
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case GlobalConstants.ASYNCTASK_FROM_BACKUP_TAG:
                        if(null != pd) pd.dismiss();
                        if(msg.arg1 == GlobalConstants.ASYNCTASK_FROM_BACKUP_STATUS_SUCCESS){
                            String filename = (String)msg.obj;
                            Toast.makeText(SettingsActivity.this, GlobalConstants.SUCCESS_BACKUP_MSG_HEAD+filename, Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(SettingsActivity.this, GlobalConstants.FAIL_BACKUP_MSG, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case GlobalConstants.ASYNCTASK_FROM_RECOVER_TAG:
                        if(null != pd) pd.dismiss();
                        if(msg.arg1 == GlobalConstants.ASYNCTASK_FROM_RECOVER_STATUS_SUCCESS){
                            Toast.makeText(SettingsActivity.this, GlobalConstants.RECOVER_SUCCESS_MSG, Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(SettingsActivity.this, GlobalConstants.RECOVER_SUCCESS_MSG, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                }
            }
        };
    }

    /**
     * Shows the simplified settings UI if the device configuration if the
     * device configuration dictates that a simplified, single-pane UI should be
     * shown.
     */
    private void setupSimplePreferencesScreen() {
        if (!isSimplePreferences(this)) {
            return;
        }

        Preference tmpPref;
        addPreferencesFromResource(R.xml.pref_secure);
        addPreferencesFromResource(R.xml.pref_data_sync);

        tmpPref = findPreference(getString(R.string.pref_title_login_type));
        if (null != tmpPref) {
            tmpPref.setOnPreferenceChangeListener(new MyOnPreferenceChangeListener());
            ListPreference listPreference = (ListPreference) tmpPref;
             String stringValue = ((ListPreference) tmpPref).getValue().toString();
            int index = listPreference.findIndexOfValue(stringValue);

            tmpPref.setSummary(
                    index >= 0
                            ? listPreference.getEntries()[index]
                            : null);
        }

        tmpPref = findPreference("login_pwd_change");
        if(null != tmpPref){
            tmpPref.setOnPreferenceClickListener(this);
        }

        tmpPref = findPreference("backup_to_sdcard");
        if(null != tmpPref){
            tmpPref.setOnPreferenceClickListener(this);
        }

        tmpPref = findPreference("recovery_from_sdcard");
        if(null != tmpPref){
            tmpPref.setOnPreferenceClickListener(this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)  {
        switch (requestCode){
            case GlobalConstants.FILE_SELECT_CODE:
                if(resultCode == RESULT_OK){
                    Uri uri = data.getData();
                    String path = FileUtils.getPath(SettingsActivity.this, uri);
                    if (null != path){
                        File file = new File(path);
                        if(!file.exists()){
                            Toast.makeText(getApplicationContext(), GlobalConstants.FAIL_ACCESS_FILE_ERR_MSG+"@"+path, Toast.LENGTH_SHORT).show();
                            return;
                        }else{
                            if(null == pd){
                                pd = ProgressDialog.show(this, GlobalConstants.RECOVERING_MSG, GlobalConstants.PLEASE_WAIT_MSG, false);
                            }else{
                                pd.setTitle(GlobalConstants.RECOVERING_MSG);
                                pd.setMessage(GlobalConstants.PLEASE_WAIT_MSG);
                                pd.show();
                            }

                            new ProgressDialogAsyncTaskForRecovery().execute(SettingsActivity.this, file, pd, handler);
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), GlobalConstants.FAIL_FILE_IO_ERR_MSG, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                break;
        }
    }

    @Override
    protected void onDestroy(){
        if (null != pd && pd.isShowing()){
            pd.dismiss();
        }
        super.onDestroy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this) && !isSimplePreferences(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Determines whether the simplified settings UI should be shown. This is
     * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
     * doesn't have newer APIs like {@link PreferenceFragment}, or the device
     * doesn't have an extra-large screen. In these cases, a single-pane
     * "simplified" settings UI should be shown.
     */
    private static boolean isSimplePreferences(Context context) {
        return ALWAYS_SIMPLE_PREFS
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
                || !isXLargeTablet(context);
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */


    //处理事件

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String title = preference.getKey();
        if("login_pwd_change".equals(title)){
            new PwdPickerDialog(SettingsActivity.this, getSharedPreferences(GlobalConstants.SHAREDPREFERENCE_TAG, MODE_PRIVATE)).showDialog();
        }else if ("backup_to_sdcard".equals(title)){
            //备份文件
            String sdCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
            if(null == sdCardRoot || "".equals(sdCardRoot)){
                Toast.makeText(getApplicationContext(), GlobalConstants.NO_SDCARD_ROOT_ERR_MSG, Toast.LENGTH_SHORT).show();
                return true;
            }

            String dirPath = sdCardRoot + File.separator + "Password";
            File rootDirFile = new File(dirPath);
            if (null != rootDirFile){
                if(!rootDirFile.isDirectory() && !rootDirFile.mkdirs()){
                    Toast.makeText(getApplicationContext(), GlobalConstants.FAIL_ACCESS_DIR_ERR_MSG, Toast.LENGTH_SHORT).show();
                    return true;
                }
            }else{
                Toast.makeText(getApplicationContext(), GlobalConstants.FAIL_ACCESS_DIR_ERR_MSG, Toast.LENGTH_SHORT).show();
                return true;
            }

            Calendar cal = Calendar.getInstance();
            String fileName = dirPath + File.separator + "Password_backup_"+ cal.get(Calendar.YEAR)+"_"+(cal.get(Calendar.MONTH)+1)+"_"+cal.get(Calendar.DATE)+
                    "_"+cal.get(Calendar.HOUR_OF_DAY)+cal.get(Calendar.MINUTE)+cal.get(Calendar.SECOND);
            Log.d("fileAbsName", fileName);
            File backupFile = new File(fileName);
            if(null == backupFile){
                Toast.makeText(getApplicationContext(), GlobalConstants.FAIL_ACCESS_DIR_ERR_MSG, Toast.LENGTH_SHORT).show();
                return true;
            }

            if(null == pd){
                pd = ProgressDialog.show(this, GlobalConstants.BACKUPING_MSG, GlobalConstants.PLEASE_WAIT_MSG, false);
            }else{
                pd.setTitle(GlobalConstants.BACKUPING_MSG);
                pd.setMessage(GlobalConstants.PLEASE_WAIT_MSG);
                pd.show();
            }
            new ProgressDialogAsyncTaskForBackup().execute(SettingsActivity.this, backupFile, pd, handler);
        }else if ("recovery_from_sdcard".equals(title)){
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "选择还原文件"), GlobalConstants.FILE_SELECT_CODE);
        }else if ("sync_with_server".equals(title)){

        }
        return true;
    }

    class MyOnPreferenceChangeListener implements Preference.OnPreferenceChangeListener{
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if(getString(R.string.pref_title_login_type).equals(preference.getKey())){
                ListPreference listPreference = (ListPreference) preference;
                String stringValue = newValue.toString();
                int index = listPreference.findIndexOfValue(stringValue);

                new PwdPickerDialog(SettingsActivity.this, getSharedPreferences(GlobalConstants.SHAREDPREFERENCE_TAG, MODE_PRIVATE)).showDialog();

                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            }
            return true;
        }
    }

}
