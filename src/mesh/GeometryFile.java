package mesh;

import java.io.File;
import java.util.ArrayList;

import org.lwjgl.util.vector.Vector4f;

import renderer.glprimitives.GLTriangle;
import renderer.glprimitives.GLVertex;

public interface GeometryFile {

	public void read(File file, Vector4f color);

	public ArrayList<GLTriangle> getTriangles();

	public ArrayList<GLVertex> getVertices();
}
