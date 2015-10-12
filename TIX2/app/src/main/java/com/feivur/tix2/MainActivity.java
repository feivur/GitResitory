package com.feivur.tix2;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {

	ImageView imageView;
	Button btnMain;
	private SoundPool mSoundPool;
	private List<Integer> mSounds = new ArrayList<>(0);
	Animation animationShake;

	int soundFileIndex;
	String buttonLabel;


	SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		InitSettings();
		initInterface();
		loadSounds();
	}

	private void InitSettings() {
		preferences = (SharedPreferences) PreferenceManager.getDefaultSharedPreferences(this);
		preferences.registerOnSharedPreferenceChangeListener(
			new SharedPreferences.OnSharedPreferenceChangeListener() {
				public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
					reloadSettings();
				}
			});
		reloadSettings();
	}

	private void reloadSettings(){
		soundFileIndex = Integer.valueOf(preferences.getString(getString(R.string.pref_sound), "0"));
		buttonLabel = preferences.getString(getString(R.string.pref_label), getString(R.string.label_1));
		if (btnMain != null)
			btnMain.setText(buttonLabel);
	}

	private void initInterface() {
		imageView = (ImageView) findViewById(R.id.imageView);

		btnMain = (Button) findViewById(R.id.button);
		if (btnMain != null) {
			btnMain.setText(buttonLabel);
			btnMain.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					imageView.startAnimation(animationShake);
					int id = mSounds.get(soundFileIndex);

					playSound(id);
				}
			});
		}

		// Подгружаем анимации
		animationShake = AnimationUtils.loadAnimation(this, R.anim.shake);
	}

	// получим идентификаторы
	private void loadSounds() {
		if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			// Для устройств до Android 5
			createOldSoundPool();
		} else {
			// Для новых устройств
			createNewSoundPool();
		}
		mSounds.add(mSoundPool.load(this, R.raw.tix, 0));
		mSounds.add(mSoundPool.load(this, R.raw.tinn, 0));
		mSounds.add(mSoundPool.load(this, R.raw.badadum, 0));
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private void createNewSoundPool() {
		AudioAttributes attributes = new AudioAttributes.Builder()
		                              .setUsage(AudioAttributes.USAGE_MEDIA)
		                              .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
		                              .build();
		mSoundPool = new SoundPool.Builder()
		              .setAudioAttributes(attributes)
		              .build();
	}

	@SuppressWarnings("deprecation")
	private void createOldSoundPool() {
		mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
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
			startActivity( new Intent(this, PreferencesActivity.class));
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void playSound(int sound) {
		if (sound > 0) {
			mSoundPool.play(sound, 1, 1, 1, 0, 1);
		}
	}
}
