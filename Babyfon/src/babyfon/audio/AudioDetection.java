package babyfon.audio;

public class AudioDetection {

	private long currentTime = 0;
	private long lastTime = 0;

	private int noiseCounter = 0;
	private int mNoiseThreshold = 50;

	// private static int maxLevel = 0;

	// TODO: Umstellen auf byte[] ? (eigtl short[])
	public static int calculateVolume(byte[] bData, int readSize) {

		boolean btMode = true;

		// TODO: readsize auslesen aus buffer? wie?
		if (readSize <= 0) {
			// hier gehts nur rein, wenn anderer Modus als BT aktiv ist
			btMode = false;
			readSize = bData.length;
		}

		double sum = 0;

		for (int i = 0; i < readSize; i++) {
			sum += bData[i] * bData[i];
		}
		if (readSize > 0) {

			if (btMode) {
				readSize *= 2;
			}

			final double amplitude = sum / readSize;

			// Für Debugzwecke
			// int level = (int) Math.sqrt(amplitude);
			// if (level > maxLevel)
			// maxLevel = level;
			// System.out.println("curVol: " + level);
			// System.out.println("maxVol: " + maxLevel);

			return (int) Math.sqrt(amplitude);
		} else
			return 0;
	}

	public boolean isBabyScreaming(int level) {
		if (level > mNoiseThreshold) {
			if (currentTime == 0 && lastTime == 0) {
				currentTime = System.currentTimeMillis();
				lastTime = System.currentTimeMillis();
			} else {
				currentTime = System.currentTimeMillis();
			}

			if ((currentTime - lastTime) > 5000) {
				noiseCounter = 0;
			} else {
				noiseCounter++;
			}
			lastTime = currentTime;
		}

		if (noiseCounter > 10) {
			noiseCounter = 0;

			return true;
		} else {
			return false;
		}
	}

	public void setThreshold(int value) {
		this.mNoiseThreshold = value;
	}

}
