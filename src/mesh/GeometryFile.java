package mesh;

import java.io.File;
import java.util.ArrayList;

import org.lwjgl.util.vector.Vector4f;

import renderer.glprimitives.GLTriangle;
import renderer.glprimitives.GLVertex;

public interface GeometryFile {

	public void read(File file, Vector4f color);

	public void write(File file);

	public ArrayList<GLTriangle> getTriangles();

	public ArrayList<GLVertex> getVertices();

	public void setTriangles(ArrayList<GLTriangle> triangles);

	public void setVertices(ArrayList<GLVertex> vertices);
}
