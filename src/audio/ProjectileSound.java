package audio;

import assets.entities.Projectile;

public class ProjectileSound extends EntitySound<Projectile> {

	@Override
	protected String getFilePath() {
		return "resources/audio/Shot.wav";
	}

	@Override
	protected float getGain() {
		return 0.6f;
	}
}
