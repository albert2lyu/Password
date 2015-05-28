package com.daggerstudio.password;

import android.content.Context;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import com.daggerstudio.password.assets.PwdPickerDialog;
import com.daggerstudio.password.dao.DaoMaster;
import com.daggerstudio.password.dao.DaoSession;
import com.daggerstudio.password.dao.Rec;
import com.daggerstudio.password.dao.RecDao;

import java.io.File;
import java.util.Calendar;
import java.util.List;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceClickListener{
    /**
     * Determines whether to always show the simplified settings UI, where
     * settings are presented in a single list. When false, settings are shown
     * as a master/detail two-pane view on tablets. When true, a single pane is
     * shown on tablets.
     */
    private static final boolean ALWAYS_SIMPLE_PREFS = false;
    private static final String SHAREDPREFERENCE_TAG = "SHAREDPREFERENCE_TAG";

    private static final String NO_SDCARD_ROOT_ERR_MSG = "找不到外部存储路径";
    private static final String FAIL_ACCESS_DIR_ERR_MSG = "无法访问路径";

    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private RecDao recDao;
    protected List<Rec> allRec;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

//        sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
//            @Override
//            public boolean onPreferenceChange(Preference preference, Object value) {
//                String stringValue = value.toString();
//
//                if (preference instanceof ListPreference) {
//                    ListPreference listPreference = (ListPreference) preference;
//                    int index = listPreference.findIndexOfValue(stringValue);
//
//                    preference.setSummary(
//                            index >= 0
//                                    ? listPreference.getEntries()[index]
//                                    : null);
//
//                } else {
//                    preference.setSummary(stringValue);
//                }
//                return false;
//            }
//        };

        setupSimplePreferencesScreen();
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
            new PwdPickerDialog(SettingsActivity.this, getSharedPreferences(SHAREDPREFERENCE_TAG, MODE_PRIVATE)).showDialog();
        }else if ("backup_to_sdcard".equals(title)){
            if(daoMaster == null || db == null || recDao == null || daoSession == null){
                db = new DaoMaster.DevOpenHelper(this, "rec-db", null).getReadableDatabase();
                daoMaster = new DaoMaster(db);
                daoSession = daoMaster.newSession();
                recDao = daoSession.getRecDao();
            }
            allRec = recDao.loadAll();
            String sdCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
            if(null == sdCardRoot || "".equals(sdCardRoot)){
                Toast.makeText(getApplicationContext(), NO_SDCARD_ROOT_ERR_MSG, Toast.LENGTH_SHORT).show();
                return true;
            }

            String dirPath = sdCardRoot + File.separator + "Password";
            File rootDirFile = new File(dirPath);
            if (null == rootDirFile || !rootDirFile.isDirectory()){
                if(!rootDirFile.mkdirs()){
                    Toast.makeText(getApplicationContext(), FAIL_ACCESS_DIR_ERR_MSG, Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
            String fileName = 
            File backupFile = new File(dirPath + File.separator + "Password_backup"+ Calendar.getInstance().get(Calendar.YEAR)+"_"+Calendar.getInstance().get(Calendar.MONTH)+"_"+Calendar.getInstance().get(Calendar.DATE));
        }else if ("recovery_from_sdcard".equals(title)){

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

                new PwdPickerDialog(SettingsActivity.this, getSharedPreferences(SHAREDPREFERENCE_TAG, MODE_PRIVATE)).showDialog();

                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            }
            return true;
        }
    }

}
