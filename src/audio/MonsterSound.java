package audio;

import assets.entities.Monster;

public class MonsterSound extends EntitySound<Monster> {

	@Override
	protected String getFilePath() {
		return "resources/audio/Explode.wav";
	}

}
