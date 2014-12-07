package mesh;

import java.io.File;
import java.util.ArrayList;

import math.types.Vector4;
import renderer.glprimitives.GLTriangle;
import renderer.glprimitives.GLVertex;

public interface GeometryFile {

	public void read(File file, Vector4 color);

	public void write(File file);

	public ArrayList<GLTriangle> getTriangles();

	public ArrayList<GLVertex> getVertices();

	public void setTriangles(ArrayList<GLTriangle> triangles);

	public void setVertices(ArrayList<GLVertex> vertices);
}
