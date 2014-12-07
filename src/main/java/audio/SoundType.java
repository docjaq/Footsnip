package audio;

public enum SoundType {
	PLAYER(0, "src/main/resources/audio/Rocket_vshort.wav", 0.6f), ASTEROID(1, "src/main/resources/audio/Explode.wav", 1.0f), PROJECTILE(2,
			"src/main/resources/audio/Shot.wav", 0.6f);

	private int index;
	private String fileLocation;
	private float gain;

	private SoundType(int index, String fileLocation, float gain) {
		this.index = index;
		this.fileLocation = fileLocation;
		this.gain = gain;
	}

	public int index() {
		return index;
	}

	public String fileLocation() {
		return fileLocation;
	}

	public float gain() {
		return gain;
	}
}
