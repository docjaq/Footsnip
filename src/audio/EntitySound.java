package audio;

import java.io.BufferedInputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.WaveData;

import util.FileUtil;
import assets.entities.Entity;

public abstract class EntitySound<T extends Entity> {
	private IntBuffer source;
	private IntBuffer buffer;

	public EntitySound() {
		loadBuffer();
		setupAudioSource();
	}

	private void loadBuffer() {
		buffer = BufferUtils.createIntBuffer(1);
		AL10.alGenBuffers(buffer);
		checkForErrors();

		loadAudioFile(buffer);
		checkForErrors();
	}

	private void checkForErrors() {
		int error = AL10.alGetError();
		if (error != AL10.AL_NO_ERROR) {
			throw new ALException(error);
		}
	}

	private void loadAudioFile(IntBuffer buffer) {
		BufferedInputStream bis = FileUtil.loadFile(getFilePath());
		WaveData waveFile = WaveData.create(bis);
		FileUtil.closeFile(bis);

		AL10.alBufferData(buffer.get(0), waveFile.format, waveFile.data, waveFile.samplerate);
		waveFile.dispose();
	}

	private void setupAudioSource() {
		source = BufferUtils.createIntBuffer(1);
		AL10.alGenSources(source);
		checkForErrors();

		AL10.alSourcei(source.get(0), AL10.AL_BUFFER, buffer.get(0));
		AL10.alSourcef(source.get(0), AL10.AL_PITCH, getPitch());
		AL10.alSourcef(source.get(0), AL10.AL_GAIN, getGain());
		checkForErrors();
	}

	protected void setSourcePosition(FloatBuffer position) {
		AL10.alSource(source.get(0), AL10.AL_POSITION, position);
	}

	protected void setSourceVelocity(FloatBuffer velocity) {
		AL10.alSource(source.get(0), AL10.AL_VELOCITY, velocity);
	}

	public void play() {
		AL10.alSourcePlay(source.get(0));
	}

	protected abstract String getFilePath();

	protected float getPitch() {
		return 1.0f;
	}

	protected float getGain() {
		return 1.0f;
	}

	public void killALData() {
		AL10.alDeleteSources(source);
		AL10.alDeleteBuffers(buffer);
	}
}
