/*
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.vanir;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.SettingsPreferenceFragment;

public class LockscreenInterface extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "LockscreenInterface";
    
    private static final String KEY_SEE_TRHOUGH = "see_through";
    private static final String KEY_LOCKSCREEN_MAXIMIZE_WIDGETS = "lockscreen_maximize_widgets";
    private static final String PREF_LOCKSCREEN_HIDE_INITIAL_PAGE_HINTS = "lockscreen_hide_initial_page_hints";
    private static final String KEY_ALLOW_ROTATION = "allow_rotation";
    private static final String PREF_LOCKSCREEN_USE_CAROUSEL = "lockscreen_use_widget_container_carousel";
    private static final String KEY_LOCKSCREEN_CAMERA_WIDGET = "lockscreen_camera_widget";
    private static final String PREF_QUICK_UNLOCK = "lockscreen_quick_unlock_control";

    private CheckBoxPreference mSeeThrough;
    private CheckBoxPreference mMaximizeWidgets;
    private CheckBoxPreference mLockscreenHideInitialPageHints;
    private CheckBoxPreference mAllowRotation;
    private CheckBoxPreference mLockscreenUseCarousel;
    private CheckBoxPreference mCameraWidget;
    private CheckBoxPreference mQuickUnlock;
    private Context mContext;

    public boolean hasButtons() {
        return !getResources().getBoolean(com.android.internal.R.bool.config_showNavigationBar);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.prefs_lockscreen);
        
        PreferenceScreen prefSet = getPreferenceScreen();
        mContext = getActivity();
        
        mSeeThrough = (CheckBoxPreference) prefSet.findPreference(KEY_SEE_TRHOUGH);
        
        mLockscreenHideInitialPageHints = (CheckBoxPreference)findPreference(PREF_LOCKSCREEN_HIDE_INITIAL_PAGE_HINTS);
        mLockscreenHideInitialPageHints.setChecked(Settings.System.getBoolean(getActivity().getContentResolver(),
              Settings.System.LOCKSCREEN_HIDE_INITIAL_PAGE_HINTS, false)); 

        mQuickUnlock = (CheckBoxPreference)findPreference(PREF_QUICK_UNLOCK);
        mQuickUnlock.setChecked(Settings.System.getBoolean(getActivity().getContentResolver(),
                Settings.System.LOCKSCREEN_QUICK_UNLOCK_CONTROL, false));       
      
        mAllowRotation = (CheckBoxPreference) prefSet.findPreference(KEY_ALLOW_ROTATION);
        mAllowRotation.setChecked(Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.LOCKSCREEN_ALLOW_ROTATION, 0) == 1);   

         mLockscreenUseCarousel = (CheckBoxPreference)findPreference(PREF_LOCKSCREEN_USE_CAROUSEL);
		 mLockscreenUseCarousel.setChecked(Settings.System.getBoolean(getActivity().getContentResolver(),
			                Settings.System.LOCKSCREEN_USE_WIDGET_CONTAINER_CAROUSEL, false));

		mCameraWidget = (CheckBoxPreference) prefSet.findPreference(KEY_LOCKSCREEN_CAMERA_WIDGET);
        mCameraWidget.setChecked(Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.KG_CAMERA_WIDGET, 0) == 1);

        mMaximizeWidgets = (CheckBoxPreference)findPreference(KEY_LOCKSCREEN_MAXIMIZE_WIDGETS);
        if (Utils.isTablet(getActivity()) || Utils.isHybrid(getActivity())) {
	        getPreferenceScreen().removePreference(mMaximizeWidgets);
	        mMaximizeWidgets = null;
        } else {
            mMaximizeWidgets.setOnPreferenceChangeListener(this);
        }
        adjustYourAttitude();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
		if (preference == mSeeThrough) {
			Settings.System.putInt(getActivity().getContentResolver(),
			        Settings.System.LOCKSCREEN_SEE_THROUGH, mSeeThrough.isChecked()
			        ? 1 : 0);
		return true;
		} else if (preference == mCameraWidget) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.KG_CAMERA_WIDGET, mCameraWidget.isChecked() ? 1 : 0);
            return true;
		} else if (preference == mLockscreenHideInitialPageHints) {
            Settings.System.putInt(getActivity().getContentResolver(),
                  Settings.System.LOCKSCREEN_HIDE_INITIAL_PAGE_HINTS,
            ((CheckBoxPreference)preference).isChecked() ? 1 : 0);
            adjustYourAttitude();
        return true;
        } else if (preference == mAllowRotation) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.LOCKSCREEN_ALLOW_ROTATION, mAllowRotation.isChecked()
                    ? 1 : 0);
        } else if (preference == mQuickUnlock) {
            Settings.System.putBoolean(getActivity().getContentResolver(),
                    Settings.System.LOCKSCREEN_QUICK_UNLOCK_CONTROL,
                    ((CheckBoxPreference) preference).isChecked());
            return true;
         } else if (preference == mLockscreenUseCarousel) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.LOCKSCREEN_USE_WIDGET_CONTAINER_CAROUSEL,
                    ((CheckBoxPreference)preference).isChecked() ? 1 : 0);
            adjustYourAttitude();
           return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
    
    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
		ContentResolver cr = getActivity().getContentResolver();
		
		if (preference == mMaximizeWidgets) {
			boolean value = (Boolean) objValue;
			Settings.System.putInt(cr, Settings.System.LOCKSCREEN_MAXIMIZE_WIDGETS, value ? 1 :0);
			return true;
	    }
	    return false;
    }

    private void adjustYourAttitude() {
        boolean mode = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.LOCKSCREEN_USE_WIDGET_CONTAINER_CAROUSEL, 0) == 1;
        if (mode) {
            mLockscreenHideInitialPageHints.setEnabled(false);
        } else {
            mLockscreenHideInitialPageHints.setEnabled(true);
        }
    }
}
