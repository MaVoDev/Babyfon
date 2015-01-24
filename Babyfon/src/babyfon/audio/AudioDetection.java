package babyfon.audio;

public class AudioDetection {

	// TODO: Umstellen auf byte[] ? (eigtl short[])
	public static int calculateVolume(byte[] bData, int readSize) {

		// TODO: readsize auslesen aus buffer? wie?
		readSize = bData.length;

		double sum = 0;

		for (int i = 0; i < readSize; i++) {
			sum += bData[i] * bData[i];
		}
		if (readSize > 0) {
			final double amplitude = sum / readSize;
			// pb.setProgress((int) Math.sqrt(amplitude));

			return (int) Math.sqrt(amplitude);
		} else
			return 0;
	}

}
