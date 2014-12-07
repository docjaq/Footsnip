package audio;

import assets.entities.Player;

public class PlayerSound extends EntitySound<Player> {

	@Override
	public void play() {
		if (!isPlaying()) {
			super.play();
		}
	}

	@Override
	protected String getFilePath() {
		return "src/main/resources/audio/Rocket_vshort.wav";
	}

	@Override
	protected float getGain() {
		return 0.6f;
	}
}
