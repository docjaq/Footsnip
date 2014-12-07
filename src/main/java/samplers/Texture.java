package samplers;

import org.lwjgl.opengl.GL11;

public abstract class Texture {

	protected int texId;
	protected int numColorChannels;

	// Maybe shouldn't store this with the texture, as it makes it makes it
	// essentially impossible to bind to a different shader. It's neater though.
	// Ho hum. Leave for now.
	protected int uniformLocation;

	public abstract void bind();

	public void destroy() {
		GL11.glDeleteTextures(texId);
	}

	public int getTexId() {
		return texId;
	}

	public int getNumColorChannels() {
		return numColorChannels;
	}

	public void setNumColorChannels(int numColorChannels) {
		this.numColorChannels = numColorChannels;
	}

	public int getUniformLocation() {
		return uniformLocation;
	}

	public void setUniformLocation(int uniformLocation) {
		this.uniformLocation = uniformLocation;
	}
}
