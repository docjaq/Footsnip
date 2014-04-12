package samplers;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

public class Texture2D extends Texture {

	public Texture2D(String filename, int numColorChannels) {
		this.numColorChannels = numColorChannels;
		texId = loadPNGTextureAsDataAndBind(filename);
		bind();
	}

	@Override
	public void bind() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
	}

	private int loadPNGTextureAsDataAndBind(String filename) {
		ByteBuffer buf = null;
		int width = 0;
		int height = 0;

		try {
			// Open the PNG file as an InputStream
			InputStream in = new FileInputStream(filename);
			// Link the PNG decoder to this stream
			PNGDecoder decoder = new PNGDecoder(in);

			// Get the width and height of the texture
			width = decoder.getWidth();
			height = decoder.getHeight();

			// Decode the PNG file in a ByteBuffer
			buf = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
			decoder.decodeFlipped(buf, decoder.getWidth() * 4, numColorChannels == 3 ? Format.RGB : Format.RGBA);
			buf.flip();

			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		// Create a new texture object in memory and bind it
		int texId = GL11.glGenTextures();

		bind();

		// All RGB bytes are aligned to each other and each component is 1 byte
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		// MAY NOT BE NECESSARY

		// Upload the texture data and generate mip maps (for scaling)
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, numColorChannels == 3 ? GL11.GL_RGB : GL11.GL_RGBA, width, height, 0,
				numColorChannels == 3 ? GL11.GL_RGB : GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, 0);

		return texId;
	}

}
