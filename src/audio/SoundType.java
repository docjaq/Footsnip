package audio;

public enum SoundType {
	PLAYER(0), MONSTER(1), PROJECTILE(2);

	private int index;

	private SoundType(int index) {
		this.index = index;
	}

	public int index() {
		return index;
	}
}
