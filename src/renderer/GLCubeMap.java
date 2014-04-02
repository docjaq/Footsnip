package renderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL42;

public class GLCubeMap {

	private static final int NUM_MIPMAPS = 3;

	// private int width;
	// private int height;
	// private int numColorChannels;
	private int texId;

	public GLCubeMap(String[] textures, int width, int height, int numColorChannels) {
		// this.height = height;
		// this.width = width;
		// this.numColorChannels = numColorChannels;
		texId = GL11.glGenTextures();

		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texId);

		GL11.glEnable(GL32.GL_TEXTURE_CUBE_MAP_SEAMLESS);

		GL42.glTexStorage2D(GL13.GL_TEXTURE_CUBE_MAP, NUM_MIPMAPS, numColorChannels == 3 ? GL11.GL_RGB8 : GL11.GL_RGBA8, width, height);
		for (int i = 0; i < 6; i++) {
			GL11.glTexSubImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, 0, 0, width, height, numColorChannels == 3 ? GL11.GL_RGB
					: GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, GLUtilityMethods.loadPNG(textures[i], numColorChannels));
		}
		GL30.glGenerateMipmap(GL13.GL_TEXTURE_CUBE_MAP);

		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
	}

	public void destroy() {
		GL11.glDeleteTextures(texId);
	}

	public void bind() {
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texId);
	}

	public int getTexId() {
		return texId;
	}
}
