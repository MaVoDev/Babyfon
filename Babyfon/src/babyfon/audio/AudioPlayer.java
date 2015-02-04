package babyfon.audio;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import babyfon.connectivity.ConnectionInterface;

import android.bluetooth.BluetoothSocket;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Environment;
import android.util.Log;

// TODO: Evtl Thread draus machen

public class AudioPlayer {

	private static final int VOL_THRESHOLD = 50;
	protected static final String TAG = AudioPlayer.class.getCanonicalName();
	private String FolderName = "RecTest";
	private AudioTrack track;

	private static final int RECORDER_SAMPLERATE = 8000;
	int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we
									// use only 1024
	int BytesPerElement = 2; // 2 bytes in 16bit format
	private BluetoothSocket mSocket;
	private boolean isPlaying;

	public AudioPlayer() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				track = new AudioTrack(AudioManager.STREAM_MUSIC, RECORDER_SAMPLERATE, AudioFormat.CHANNEL_OUT_MONO,
						AudioFormat.ENCODING_PCM_16BIT, BufferElements2Rec * BytesPerElement, AudioTrack.MODE_STREAM);

				track.play();

				Log.i(TAG, "AudioPlayer created and track is in playing mode...");
			}
		}).start();

	}

	public void playData(byte[] data) {

		// Log.i(TAG, "playData! " + data);

		track.write(data, 0, data.length);

		// byte[] bData = new byte[BufferElements2Rec];

		// isPlaying = true;
		//
		// while (isPlaying) {
		//
		// int readSize = 0;
		// calculateVolume(data, readSize);
		//
		// track.write(data, 0, readSize);
		// }

	}

	public void playData(byte[] data, int bytesRead) {
		// Log.i(TAG, "PLAY DATA! Track PlayState: " + track.getPlayState() + "; Track State: " + track.getState());

		track.write(data, 0, bytesRead);
	}

	private void calculateVolume(byte[] bData, int readSize) {
		double sum = 0;
		for (int i = 0; i < readSize; i++) {
			sum += bData[i] * bData[i];
		}

		if (readSize > 0) {
			final double amplitude = ((sum / readSize) * 1.f) - 28.f;

			final int volume = (int) Math.sqrt(amplitude);

		}
	}

	private void playSound() {

		Log.i(TAG, "playSound()");

		track = new AudioTrack(AudioManager.STREAM_MUSIC, RECORDER_SAMPLERATE, AudioFormat.CHANNEL_OUT_MONO,
				AudioFormat.ENCODING_PCM_16BIT, BufferElements2Rec * BytesPerElement, AudioTrack.MODE_STREAM);

		String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + FolderName;

		File file = new File(filePath + File.separator + "voice8K16bitmono.pcm");

		// short sData[] = new short[BufferElements2Rec]; // Data as short
		// byte[] bData = new byte[BufferElements2Rec * 2];
		byte[] bData = new byte[BufferElements2Rec];

		try {
			BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));

			int read;

			// int offset = 0;

			track.play();

			while ((read = inputStream.read(bData)) != -1) {
				track.write(bData, 0, read);
				// offset += track.write(bData, offset, read);

			}

			inputStream.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			// Clean up
			track.flush();
			track.stop();
			track.release();

			// Re-Enable the Play-Button after playing finished
			// mActivtity.runOnUiThread(new Runnable() {
			//
			// @Override
			// public void run() {
			// mActivtity.enablePlayBtn();
			// }
			// });
		}

	}

	public void stopPlaying() {

		Log.i(TAG, "Stop Audio Playing...");

		isPlaying = false;

		if (track != null) {
			if (track.getState() != AudioTrack.STATE_UNINITIALIZED) {
				track.flush();
				track.stop();
				track.release();
			}
		}
	}

}
