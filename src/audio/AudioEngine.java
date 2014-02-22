package audio;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Scanner;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.WaveData;

public class AudioEngine {

	private static final int NUM_BUFFERS = 3;
	private static final int NUM_SOURCES = 3;

	private static final int SOUND_PLAYER = 0;
	private static final int SOUND_MONSTER = 1;
	private static final int SOUND_PROJECTILE = 2;

	private static AudioEngine instance;

	IntBuffer buffer = BufferUtils.createIntBuffer(NUM_BUFFERS);
	IntBuffer source = BufferUtils.createIntBuffer(NUM_SOURCES);

	FloatBuffer sourcePos = (FloatBuffer) BufferUtils.createFloatBuffer(3 * NUM_SOURCES).put(new float[] { 0.0f, 0.0f, 0.0f }).rewind();
	FloatBuffer sourceVel = (FloatBuffer) BufferUtils.createFloatBuffer(3 * NUM_SOURCES).put(new float[] { 0.0f, 0.0f, 0.0f }).rewind();

	FloatBuffer listenerPos = (FloatBuffer) BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f }).rewind();
	FloatBuffer listenerVel = (FloatBuffer) BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f }).rewind();
	FloatBuffer listenerOri = (FloatBuffer) BufferUtils.createFloatBuffer(6).put(new float[] { 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f })
			.rewind();

	private AudioEngine() {

		try {
			AL.create();
		} catch (LWJGLException le) {
			le.printStackTrace();
			return;
		}
		AL10.alGetError();

		// Load the wav data.
		if (loadALData() == AL10.AL_FALSE) {
			System.out.println("Error loading data.");
			return;
		}

		setListenerValues();
	}

	public synchronized static AudioEngine getInstance() {
		if (instance == null) {
			instance = new AudioEngine();
		}

		return instance;
	}

	private void loadAudioFile(String fileLocation, int bufferIndex) {
		BufferedInputStream bis;
		try {
			bis = new BufferedInputStream(new FileInputStream(fileLocation));
		} catch (java.io.FileNotFoundException ex) {
			ex.printStackTrace();
			return;
		}
		WaveData waveFile = WaveData.create(bis);
		try {
			bis.close();
		} catch (java.io.IOException ex) {
		}

		AL10.alBufferData(buffer.get(bufferIndex), waveFile.format, waveFile.data, waveFile.samplerate);
		waveFile.dispose();
	}

	private void setupAudioSource(int bufferIndex, float gain) {
		AL10.alSourcei(source.get(bufferIndex), AL10.AL_BUFFER, buffer.get(bufferIndex));
		AL10.alSourcef(source.get(bufferIndex), AL10.AL_PITCH, 1.0f);
		AL10.alSourcef(source.get(bufferIndex), AL10.AL_GAIN, gain);
		AL10.alSource(source.get(bufferIndex), AL10.AL_POSITION, (FloatBuffer) sourcePos.position(bufferIndex * 3));
		AL10.alSource(source.get(bufferIndex), AL10.AL_VELOCITY, (FloatBuffer) sourceVel.position(bufferIndex * 3));
	}

	private int loadALData() {
		// Load wav data into a buffer.
		AL10.alGenBuffers(buffer);

		if (AL10.alGetError() != AL10.AL_NO_ERROR)
			return AL10.AL_FALSE;

		loadAudioFile("resources/audio/Rocket.wav", SOUND_PLAYER);
		loadAudioFile("resources/audio/Explode.wav", SOUND_MONSTER);
		loadAudioFile("resources/audio/Shot.wav", SOUND_PROJECTILE);

		// Bind the buffer with the source.
		AL10.alGenSources(source);

		if (AL10.alGetError() != AL10.AL_NO_ERROR)
			return AL10.AL_FALSE;

		setupAudioSource(SOUND_PLAYER, 0.8f);
		setupAudioSource(SOUND_MONSTER, 1.0f);
		setupAudioSource(SOUND_PROJECTILE, 0.2f);

		// Do another error check and return.
		if (AL10.alGetError() == AL10.AL_NO_ERROR)
			return AL10.AL_TRUE;

		return AL10.AL_FALSE;
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
		AL10.alSourcePlay(source.get(SOUND_PLAYER));
	}

	public void playMonsterSound() {
		System.out.println("Fuck off");
		AL10.alSourcePlay(source.get(SOUND_MONSTER));
	}

	public void playProjectileSound() {
		AL10.alSourcePlay(source.get(SOUND_PROJECTILE));
	}

	public void execute() {
		// Initialize OpenAL and clear the error bit.
		try {
			AL.create();
		} catch (LWJGLException le) {
			le.printStackTrace();
			return;
		}
		AL10.alGetError();

		// Load the wav data.
		if (loadALData() == AL10.AL_FALSE) {
			System.out.println("Error loading data.");
			return;
		}

		setListenerValues();

		// Loop.
		System.out.println("OpenAL Tutorial 1 - Single Static Source");
		System.out.println("[Menu]");
		System.out.println("p - Play the sample.");
		System.out.println("s - Stop the sample.");
		System.out.println("h - Pause the sample.");
		System.out.println("q - Quit the program.");
		char c = ' ';
		Scanner stdin = new Scanner(System.in);
		while (c != 'q') {
			try {
				System.out.print("Input: ");
				c = (char) stdin.nextLine().charAt(0);
			} catch (Exception ex) {
				c = 'q';
			}

			switch (c) {
			// Pressing 'p' will begin playing the sample.
			case 'p':
				AL10.alSourcePlay(source.get(0));
				break;

			// Pressing 's' will stop the sample from playing.
			case 's':
				AL10.alSourceStop(source.get(0));
				break;

			// Pressing 'h' will pause the sample.
			case 'h':
				AL10.alSourcePause(source.get(0));
				break;
			}
			;
		}
		killALData();
		AL.destroy();
	}

	public void close() {
		killALData();
		AL.destroy();
	}
}
