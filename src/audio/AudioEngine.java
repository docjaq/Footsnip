package audio;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.WaveData;

public class AudioEngine {

	private static final int NUM_BUFFERS = 3;
	private static final int NUM_SOURCES = 3;

	private static AudioEngine instance;

	private IntBuffer buffer = BufferUtils.createIntBuffer(NUM_BUFFERS);
	private IntBuffer source = BufferUtils.createIntBuffer(NUM_SOURCES);

	private FloatBuffer sourcePos = (FloatBuffer) BufferUtils.createFloatBuffer(3 * NUM_SOURCES).put(new float[] { 0.0f, 0.0f, 0.0f })
			.rewind();
	private FloatBuffer sourceVel = (FloatBuffer) BufferUtils.createFloatBuffer(3 * NUM_SOURCES).put(new float[] { 0.0f, 0.0f, 0.0f })
			.rewind();

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
		loadWavData();
		setListenerValues();
	}

	private void init() {
		try {
			AL.create();
		} catch (LWJGLException e) {
			throw new AudioException("Error initialising AudioEngine", e);
		}

		checkForErrors();
	}

	private void checkForErrors() {
		if (AL10.alGetError() != AL10.AL_NO_ERROR) {
			throw new AudioException("Error initialising AudioEngine; error code from OpenAL: " + AL10.alGetError());
		}
	}

	private void loadWavData() {
		// Load wav data into a buffer.
		AL10.alGenBuffers(buffer);

		checkForErrors();

		loadAudioFile("resources/audio/Rocket_vshort.wav", SoundType.PLAYER);
		loadAudioFile("resources/audio/Explode.wav", SoundType.MONSTER);
		loadAudioFile("resources/audio/Shot.wav", SoundType.PROJECTILE);

		// Bind the buffer with the source.
		AL10.alGenSources(source);

		checkForErrors();

		setupAudioSource(SoundType.PLAYER, 0.8f);
		setupAudioSource(SoundType.MONSTER, 1.0f);
		setupAudioSource(SoundType.PROJECTILE, 0.2f);

		checkForErrors();
	}

	private void loadAudioFile(String fileLocation, SoundType soundType) {
		BufferedInputStream bis = loadFile(fileLocation);
		WaveData waveFile = WaveData.create(bis);
		closeFile(bis);

		AL10.alBufferData(buffer.get(soundType.index()), waveFile.format, waveFile.data, waveFile.samplerate);
		waveFile.dispose();
	}

	private void closeFile(BufferedInputStream bis) {
		try {
			bis.close();
		} catch (java.io.IOException ex) {
			throw new AudioException("Error closing wav file.", ex);
		}
	}

	private BufferedInputStream loadFile(String fileLocation) {
		try {
			return new BufferedInputStream(new FileInputStream(fileLocation));
		} catch (java.io.FileNotFoundException ex) {
			throw new AudioException("Error reading wav file.", ex);
		}
	}

	private void setupAudioSource(SoundType soundType, float gain) {
		int index = soundType.index();
		AL10.alSourcei(source.get(index), AL10.AL_BUFFER, buffer.get(index));
		AL10.alSourcef(source.get(index), AL10.AL_PITCH, 1.0f);
		AL10.alSourcef(source.get(index), AL10.AL_GAIN, gain);
		AL10.alSource(source.get(index), AL10.AL_POSITION, (FloatBuffer) sourcePos.position(index * 3));
		AL10.alSource(source.get(index), AL10.AL_VELOCITY, (FloatBuffer) sourceVel.position(index * 3));
	}

	void setListenerValues() {
		AL10.alListener(AL10.AL_POSITION, listenerPos);
		AL10.alListener(AL10.AL_VELOCITY, listenerVel);
		AL10.alListener(AL10.AL_ORIENTATION, listenerOri);
	}

	void killALData() {
		AL10.alDeleteSources(source);
		AL10.alDeleteBuffers(buffer);
	}

	public void playPlayerSound() {
		if (AL10.alGetSourcei(source.get(SoundType.PLAYER.index()), AL10.AL_SOURCE_STATE) != AL10.AL_PLAYING) {
			AL10.alSourcePlay(source.get(SoundType.PLAYER.index()));
		}
	}

	public void playMonsterSound() {
		AL10.alSourcePlay(source.get(SoundType.MONSTER.index()));
	}

	public void playProjectileSound() {
		AL10.alSourcePlay(source.get(SoundType.PROJECTILE.index()));
	}

	public void close() {
		killALData();
		AL.destroy();
	}
}
