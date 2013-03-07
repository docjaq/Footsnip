package renderer.glmodels;

import static renderer.GLUtilityMethods.exitOnGLError;
import static renderer.GLUtilityMethods.loadPNGTexture;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;

public class GLTexturedModel extends GLModel {

	// Textures
	private List<Integer> texIds;

	public GLTexturedModel(Vector3f modelPos, Vector3f modelAngle, Vector3f modelScale, float[] color) {
		super(modelPos, modelAngle, modelScale, color);

		texIds = new ArrayList<Integer>();
	}

	public void draw() {

		// Bind the texture
		// This is the texture unit
		GL13.glActiveTexture(GL13.GL_TEXTURE0);

		// This is the index to the specific texture to use
		// Currently, hard coded to the first texture id
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texIds.get(0));

		// Bind to the VAO that has all the information about the vertices
		GL30.glBindVertexArray(vaoId);

		// Currently, our GLModel, therefore, only supports 3 arrays; xyz, rgb,
		// and st. If we load them in the current manner, they MUST all be
		// present
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);

		// Bind to the index VBO that has all the information about the order of
		// the vertices
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId);

		// Enables transparency in Texture (things have to be drawn in the right
		// order. Just doing this for some debugging)
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		// Draw the vertices
		// Currently, our GLModel, therefore, can only consist of triangles
		GL11.glDrawElements(GL11.GL_TRIANGLES, indicesCount, GL11.GL_UNSIGNED_BYTE, 0);

		// Put everything back to default (deselect)
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);

	}

	// TODO: This method should accept a string, the
	// texture name, and then it should read it into a list of texture ids (not
	// a fixed length array)
	public void setupTextures(String textureLocation) {
		// Reads a PNG, creates an texture, binds it to memory within OPENGL,
		// sets it up (parameterisation-wise) and returns an int 'pointer' which
		// can be used to reference it in OpenGL

		texIds.add(loadPNGTexture(textureLocation, GL13.GL_TEXTURE0));
		// texIds[1] = loadPNGTexture("resources/images/stGrid2.png",
		// GL13.GL_TEXTURE0); modelMatrix = new Matrix4f();

		exitOnGLError("setupTexture");
	}

	// TODO: Check if there is a texture, as with draw()
	public void cleanUp() {
		cleanUpTextures();
		cleanUpGeometry();
	}

	public void cleanUpTextures() {
		// Delete textures
		for (int i : texIds) {
			GL11.glDeleteTextures(i);
		}
	}

}
