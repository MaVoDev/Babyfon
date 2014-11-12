package babyfon.detection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

public class Noise {

	private static String TAG = Noise.class.getCanonicalName();

	private static final int RECORDER_SAMPLERATE = 8000;
	private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
	private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	private boolean isRecording;
	private Thread recordingThread;

	private String FolderName = "RecTest";

	int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
	int BytesPerElement = 2; // 2 bytes in 16bit format
	private AudioRecord recorder;

	public Noise() {

	}

	public boolean isRecording() {
		return isRecording;
	}

	public void startListening() {

		recorder = initRecorder();

		if (recorder == null) {
			Log.e(TAG, "Recorder was not initialized correctly. Cancel recording...");
			return;
		}

		recorder.startRecording();

		recordingThread = new Thread(new Runnable() {
			public void run() {
				writeAudioDataToFile();
			}
		}, "AudioRecorder Thread");
		recordingThread.start();

	}

	private AudioRecord initRecorder() {

		Log.i(TAG, "Initializing Recorder...");

		int bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);

		Log.i(TAG, "BufferSize: " + bufferSize);

		if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {

			AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, RECORDER_SAMPLERATE, RECORDER_CHANNELS,
					RECORDER_AUDIO_ENCODING, BufferElements2Rec * BytesPerElement);
			// AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, RECORDER_SAMPLERATE, RECORDER_CHANNELS,
			// RECORDER_AUDIO_ENCODING, bufferSize);

			if (recorder.getState() == AudioRecord.STATE_INITIALIZED)
				return recorder;

		}

		return null;
	}

	private void writeAudioDataToFile() {
		// Write the output audio in byte

		String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + FolderName;

		File dir = new File(filePath);
		Log.i(TAG, "Create path: \"" + filePath + "\" -> " + dir.mkdirs());

		File file = new File(dir, "voice8K16bitmono.pcm");

		// short sData[] = new short[BufferElements2Rec]; // Data as short
		byte bData[] = new byte[BufferElements2Rec * 2];

		FileOutputStream os = null;
		try {
			os = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		isRecording = true;

		Log.i(TAG, "Start recording to file: " + file.getAbsolutePath());

		while (isRecording) {
			// gets the voice output from microphone to byte format

			// recorder.read(sData, 0, BufferElements2Rec);
			recorder.read(bData, 0, BufferElements2Rec*2);
			Log.i(TAG, "Short wirting to file" + bData.toString());
			try {
				// // writes the data to file from buffer
				// // stores the voice buffer
				// byte bData[] = short2byte(sData);
				os.write(bData, 0, BufferElements2Rec * BytesPerElement);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
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
			Log.i(TAG, "Stopping recording...");

			isRecording = false;
			recorder.stop();
			recorder.release();
			recorder = null;
			recordingThread = null;
		}
	}

}
