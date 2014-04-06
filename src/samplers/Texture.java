package samplers;

import org.lwjgl.opengl.GL11;

public abstract class Texture {

	protected int texId;

	public abstract void bind();

	public void destroy() {
		GL11.glDeleteTextures(texId);
	}

	public int getTexId() {
		return texId;
	}
}
