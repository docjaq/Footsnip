package audio;

import assets.entities.Asteroid;

public class AsteroidSound extends EntitySound<Asteroid> {

	@Override
	protected String getFilePath() {
		return "src/main/resources/audio/Explode.wav";
	}

}
