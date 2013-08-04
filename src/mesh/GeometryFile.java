package mesh;

import java.io.File;
import java.util.ArrayList;

import renderer.glprimitives.GLTriangle;
import renderer.glprimitives.GLVertex;

public interface GeometryFile {

	public void read(File file);

	public ArrayList<GLTriangle> getTriangles();

	public ArrayList<GLVertex> getVertices();
}
