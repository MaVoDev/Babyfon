package babyfon.performance;

import android.content.Context;
import android.media.AudioManager;

public class Sound {

	private AudioManager mAudioManager;

	public Sound(Context mContext) {
		mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
	}

	/**
	 * Mute all sounds and notifications
	 */
	public void mute() {
//		mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
//
//		mAudioManager.setStreamMute(AudioManager.STREAM_ALARM, true);
//		mAudioManager.setStreamMute(AudioManager.STREAM_DTMF, true);
//		mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
//		mAudioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
//		mAudioManager.setStreamMute(AudioManager.STREAM_RING, true);
//		mAudioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
//		mAudioManager.setStreamMute(AudioManager.STREAM_VOICE_CALL, true);

//		mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
	}
	
	public void soundOn() {
//		mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
//
//		mAudioManager.setStreamMute(AudioManager.STREAM_ALARM, false);
//		mAudioManager.setStreamMute(AudioManager.STREAM_DTMF, false);
//		mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
//		mAudioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
//		mAudioManager.setStreamMute(AudioManager.STREAM_RING, false);
//		mAudioManager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
//		mAudioManager.setStreamMute(AudioManager.STREAM_VOICE_CALL, false);

//		mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, 2, AudioManager.FLAG_PLAY_SOUND);
	}
}
