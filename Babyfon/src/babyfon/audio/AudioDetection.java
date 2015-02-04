package babyfon.audio;

public class AudioDetection {

	private static int maxLevel = 0;

	// TODO: Umstellen auf byte[] ? (eigtl short[])
	public static int calculateVolume(byte[] bData, int readSize) {

		// TODO: readsize auslesen aus buffer? wie?
		if (readSize <= 0)
			readSize = bData.length;

		double sum = 0;

		for (int i = 0; i < readSize; i++) {
			sum += bData[i] * bData[i];
		}
		if (readSize > 0) {
			final double amplitude = sum / readSize;

			int level = (int) Math.sqrt(amplitude);
			// int level = (int) amplitude;

			if (level > maxLevel)
				maxLevel = level;

//			System.out.println("curVol: " + level);
//			System.out.println("maxVol: " + maxLevel);

			return (int) Math.sqrt(amplitude);
		} else
			return 0;
	}

}
