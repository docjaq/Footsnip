package audio;

import java.io.BufferedInputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.WaveData;

import util.FileUtil;

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
			checkForErrors();
		} catch (LWJGLException e) {
			throw new AudioException("Error initialising AudioEngine", e);
		}

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

		loadAudioFiles();

		// Bind the buffer with the source.
		AL10.alGenSources(source);

		checkForErrors();

		setupAudioSources();

		checkForErrors();
	}

	private void loadAudioFiles() {
		for (SoundType soundType : SoundType.values()) {
			loadAudioFile(soundType);
		}
	}

	private void loadAudioFile(SoundType soundType) {
		BufferedInputStream bis = FileUtil.loadFile(soundType.fileLocation());
		WaveData waveFile = WaveData.create(bis);
		FileUtil.closeFile(bis);

		AL10.alBufferData(buffer.get(soundType.index()), waveFile.format, waveFile.data, waveFile.samplerate);
		waveFile.dispose();
	}

	private void setupAudioSources() {
		for (SoundType soundType : SoundType.values()) {
			setupAudioSource(soundType);
		}
	}

	private void setSourcePosition(SoundType soundType, FloatBuffer position) {
		AL10.alSource(source.get(soundType.index()), AL10.AL_POSITION, position);
	}

	private void setupAudioSource(SoundType soundType) {
		int index = soundType.index();
		AL10.alSourcei(source.get(index), AL10.AL_BUFFER, buffer.get(index));
		AL10.alSourcef(source.get(index), AL10.AL_PITCH, 1.0f);
		AL10.alSourcef(source.get(index), AL10.AL_GAIN, soundType.gain());
		setSourcePosition(soundType, (FloatBuffer) sourcePos.position(index * 3));
		AL10.alSource(source.get(index), AL10.AL_VELOCITY, (FloatBuffer) sourceVel.position(index * 3));
	}

	private void setListenerValues() {
		AL10.alListener(AL10.AL_POSITION, listenerPos);
		AL10.alListener(AL10.AL_VELOCITY, listenerVel);
		AL10.alListener(AL10.AL_ORIENTATION, listenerOri);
	}

	private void killALData() {
		AL10.alDeleteSources(source);
		AL10.alDeleteBuffers(buffer);
	}

	public void playPlayerSound() {
		if (AL10.alGetSourcei(source.get(SoundType.PLAYER.index()), AL10.AL_SOURCE_STATE) != AL10.AL_PLAYING) {
			// play(SoundType.PLAYER);
		}
	}

	public void playMonsterSound(FloatBuffer position) {
		setSourcePosition(SoundType.MONSTER, position);
		play(SoundType.MONSTER);
	}

	public void playProjectileSound() {
		// play(SoundType.PROJECTILE);
	}

	private void play(SoundType soundType) {
		AL10.alSourcePlay(source.get(soundType.index()));
	}

	public void close() {
		killALData();
		AL.destroy();
	}
}
