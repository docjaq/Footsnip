package assets.entities;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import renderer.glmodels.GLMesh;
import renderer.glmodels.GLModel;
import renderer.glprimitives.GLTriangle;
import renderer.glprimitives.GLVertex;
import renderer.glshaders.GLShader;

public class PolygonalSceneryFactory {

	public static PolygonalScenery create(GLShader shader, Vector3f sceneryPos) {

		Vector3f sceneryAngle = new Vector3f(0, 0, 0);
		float sceneryScale = 1;
		Vector4f sceneryColor = new Vector4f(0.2f, 0.2f, 0.2f, 1.0f);
		Vector4f normal = new Vector4f(0, 0, 1, 1);

		List<GLVertex> vertexList = new ArrayList<GLVertex>();
		vertexList.add(new GLVertex(0, 0, 0, 0, sceneryColor, normal));
		vertexList.add(new GLVertex(1, 0.5f, 0, 0, sceneryColor, normal));
		vertexList.add(new GLVertex(2, 0, 0.5f, 0, sceneryColor, normal));

		List<GLTriangle> triangleList = new ArrayList<GLTriangle>();
		triangleList.add(new GLTriangle(vertexList.get(0), vertexList.get(1), vertexList.get(2)));

		GLModel polygonalSceneryModel = new GLMesh(triangleList, vertexList, sceneryPos, sceneryAngle, sceneryScale);
		PolygonalScenery polygon = new PolygonalScenery(polygonalSceneryModel, "Scenery");

		return polygon;
	}
}