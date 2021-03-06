package audio;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;

import renderer.GLPosition;
import assets.entities.Asteroid;
import assets.entities.Player;
import assets.entities.Projectile;

public class AudioEngine {

	private static AudioEngine instance;

	private EntitySound<Asteroid> asteroidSound;
	private EntitySound<Projectile> projectileSound;
	private EntitySound<Player> playerSound;

	private FloatBuffer listenerPos = (FloatBuffer) BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f }).rewind();
	private FloatBuffer listenerVel = (FloatBuffer) BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f }).rewind();
	private FloatBuffer listenerOri = (FloatBuffer) BufferUtils.createFloatBuffer(6)
			.put(new float[] { 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f }).rewind();

	public synchronized static AudioEngine getInstance() {
		if (instance == null) {
			instance = new AudioEngine();
		}

		return instance;
	}

	private AudioEngine() {
		init();
		setupSounds();
		setListenerValues();
	}

	private void init() {
		try {
			AL.create();
			checkForErrors();
		} catch (LWJGLException e) {
			throw new RuntimeException("Error initialising AudioEngine", e);
		}
	}

	private void setupSounds() {
		asteroidSound = new AsteroidSound();
		projectileSound = new ProjectileSound();
		playerSound = new PlayerSound();
	}

	private void checkForErrors() {
		if (AL10.alGetError() != AL10.AL_NO_ERROR) {
			throw new ALException(AL10.alGetError());
		}
	}

	private void setListenerValues() {
		AL10.alListener(AL10.AL_POSITION, listenerPos);
		AL10.alListener(AL10.AL_VELOCITY, listenerVel);
		AL10.alListener(AL10.AL_ORIENTATION, listenerOri);
	}

	public void playPlayerSound() {
		playerSound.play();
	}

	public void playAsteroidSound(GLPosition glPosition) {
		FloatBuffer positionBuffer = (FloatBuffer) BufferUtils.createFloatBuffer(3)
				.put(new float[] { glPosition.modelPos.x(), glPosition.modelPos.y(), glPosition.modelPos.z() }).rewind();
		asteroidSound.setSourcePosition(positionBuffer);
		asteroidSound.setSourceVelocity(BufferUtils.createFloatBuffer(3));
		asteroidSound.play();
	}

	public void playProjectileSound() {
		projectileSound.play();
	}

	public void close() {
		// TODO: Do we need to do this for all the sounds?
		// asteroidSound.killALData();
		AL.destroy();
	}
}
