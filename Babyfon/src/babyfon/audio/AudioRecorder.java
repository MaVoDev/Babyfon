package babyfon.audio;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;
import babyfon.connectivity.ConnectionInterface;
import babyfon.connectivity.wifi.UDPReceiver;
import babyfon.connectivity.wifi.UDPSender;
import babyfon.settings.SharedPrefs;

public class AudioRecorder {

	private static String TAG = AudioRecorder.class.getCanonicalName();

	private static final int RECORDER_SAMPLERATE = 8000;
	private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
	private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

	private static final double VOLUME_THRESHOLD = 0;
	private boolean isRecording;
	private Thread recordingThread;

	private String FolderName = "RecTest";

	public static int BufferElements2Rec = 1024; // want to play 2048 (2K) since
													// 2 bytes we
	// use only 1024

	int BytesPerElement = 2; // 2 bytes in 16bit format
	private AudioRecord recorder;

	private int threshold = 9999999;

	private ConnectionInterface mConnection;

	private SharedPrefs mSharedPrefs;
	
	private UDPSender mUdpSender;
	
	public AudioRecorder(Context mContext, ConnectionInterface connection) {
		this.mConnection = connection;
		mSharedPrefs = new SharedPrefs(mContext);
		recorder = initRecorder();
		mUdpSender = new UDPSender(mContext);
	}

	public boolean isRecording() {
		return isRecording;
	}

	public void startRecording() {

		Log.i(TAG, "Starting Audio Recording...");

		recorder.startRecording();

		isRecording = true;

		recordingThread = new Thread(new Runnable() {
			public void run() {
				streamAudio();
			}
		}, "AudioRecorder Thread");
		recordingThread.start();

	}

	private AudioRecord initRecorder() {

		Log.i(TAG, "Initializing Recorder...");

		int minBufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, RECORDER_CHANNELS,
				RECORDER_AUDIO_ENCODING);

		Log.i(TAG, "BufferSize: " + minBufferSize);

		if (minBufferSize != AudioRecord.ERROR_BAD_VALUE) {

			// AudioRecord recorder = new
			// AudioRecord(MediaRecorder.AudioSource.MIC, RECORDER_SAMPLERATE,
			// RECORDER_CHANNELS,
			// RECORDER_AUDIO_ENCODING, BufferElements2Rec * BytesPerElement);

			AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, RECORDER_SAMPLERATE,
					RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING, minBufferSize * 10);

			BufferElements2Rec = minBufferSize * 2;

			if (recorder.getState() == AudioRecord.STATE_INITIALIZED)
				return recorder;

		}

		Log.e(TAG, "Recorder was not initialized correctly. Cancel recording...");

		return null;
	}

	private void streamAudio() {

		short sData[] = new short[BufferElements2Rec]; // Data as short

		while (isRecording) {
			// gets the voice output from microphone to byte format

			// Schreibe Byte-Daten vom Recorder ins Array sData
			int readSize = recorder.read(sData, 0, BufferElements2Rec);

			// Error beim Lesen aufgetreten, also Schleifendurchlauf abbrechen
			if (readSize < 0)
				return;

			// AUDIO DETECTION

			double sum = 0;

			for (int i = 0; i < readSize; i++) {
				sum += sData[i] * sData[i];
			}
			if (readSize > 0) {
				// amplitude == Lautstärke
				double amplitude = sum / readSize;

				if (amplitude >= VOLUME_THRESHOLD)
					// TODO: GERÄUSCHERKENNUNG IMPLEMENTIEREN
					// Führe Aktion durch
					;
			}

			// /AUDIO DETECTION

			// // writes the data to file from buffer
			// // stores the voice buffer
			byte bData[] = short2byte(sData);

//			Log.i(TAG, "Writing data to file: " + sData.toString());

			if(mSharedPrefs.getConnectivityType() == 1) {
				mConnection.sendData(bData, (byte) 1);
			}
			
			if(mSharedPrefs.getConnectivityType() == 2) {
				mUdpSender.sendUDPMessage(bData);
			}
			

			// Send Audio Data to connected client
			// mmOutStream.write(bData, 0, BufferElements2Rec
			// * BytesPerElement);

		}

	}

	// convert short to byte
	private byte[] short2byte(short[] sData) {
		int shortArrsize = sData.length;
		byte[] bytes = new byte[shortArrsize * 2];
		for (int i = 0; i < shortArrsize; i++) {
			bytes[i * 2] = (byte) (sData[i] & 0x00FF);
			bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
			sData[i] = 0;
		}
		return bytes;

	}

	public void stopRecording() {
		// stops the recording activity
		if (null != recorder) {
			isRecording = false;
			recorder.stop();

			Log.i(TAG, "Recording stopped...");
		}
	}

	public void cleanUp() {
		if (null != recorder) {

			stopRecording();

			recorder.release();
			recorder = null;
			recordingThread = null;

		}
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}

}
