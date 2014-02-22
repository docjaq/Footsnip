package audio;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Scanner;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.WaveData;

public class AudioEngine {

	private static AudioEngine instance;

	IntBuffer buffer = BufferUtils.createIntBuffer(1);

	IntBuffer source = BufferUtils.createIntBuffer(1);

	FloatBuffer sourcePos = (FloatBuffer) BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f }).rewind();

	FloatBuffer sourceVel = (FloatBuffer) BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f }).rewind();

	FloatBuffer listenerPos = (FloatBuffer) BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f }).rewind();

	FloatBuffer listenerVel = (FloatBuffer) BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f }).rewind();

	FloatBuffer listenerOri = (FloatBuffer) BufferUtils.createFloatBuffer(6).put(new float[] { 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f })
			.rewind();

	private AudioEngine() {
		// No-op
	}

	public synchronized static AudioEngine getInstance() {
		if (instance == null) {
			instance = new AudioEngine();
		}

		return instance;
	}

	int loadALData() {
		// Load wav data into a buffer.
		AL10.alGenBuffers(buffer);

		if (AL10.alGetError() != AL10.AL_NO_ERROR)
			return AL10.AL_FALSE;

		// Loads the wave file from this class's package in your classpath
		// WaveData waveFile = WaveData.create("fancypants.wav");

		java.io.FileInputStream fin = null;
		try {
			fin = new java.io.FileInputStream("resources/audio/fancypants.wav");
		} catch (java.io.FileNotFoundException ex) {
			ex.printStackTrace();
			return AL10.AL_FALSE;
		}
		WaveData waveFile = WaveData.create(fin);
		try {
			fin.close();
		} catch (java.io.IOException ex) {
		}

		AL10.alBufferData(buffer.get(0), waveFile.format, waveFile.data, waveFile.samplerate);
		waveFile.dispose();

		// Bind the buffer with the source.
		AL10.alGenSources(source);

		if (AL10.alGetError() != AL10.AL_NO_ERROR)
			return AL10.AL_FALSE;

		AL10.alSourcei(source.get(0), AL10.AL_BUFFER, buffer.get(0));
		AL10.alSourcef(source.get(0), AL10.AL_PITCH, 1.0f);
		AL10.alSourcef(source.get(0), AL10.AL_GAIN, 1.0f);
		AL10.alSource(source.get(0), AL10.AL_POSITION, sourcePos);
		AL10.alSource(source.get(0), AL10.AL_VELOCITY, sourceVel);

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

	public static void main(String[] args) {
		AudioEngine.getInstance().execute();
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
}
