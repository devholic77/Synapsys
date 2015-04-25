package org.gbssm.synapsys.global;

import org.gbssm.synapsys.R;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

/**
 * 
 * @author Yeonho.Kim
 * @since 2015.03.30
 *
 */
public class SettingActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {
	
	@Override
	protected void onCreate(Bundle savedInstanceBundle) {
		super.onCreate(savedInstanceBundle);
		addPreferencesFromResource(R.xml.setting_activity);
		
	}

	@Override
	public boolean onPreferenceChange(Preference pref, Object newValue) {
		
		
		return false;
	}

}
