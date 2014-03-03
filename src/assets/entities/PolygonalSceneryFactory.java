package assets.entities;

import java.util.ArrayList;
import java.util.List;

import maths.types.Vector3;
import maths.types.Vector4;
import renderer.GLPosition;
import renderer.glmodels.GLMesh;
import renderer.glmodels.GLModel;
import renderer.glprimitives.GLTriangle;
import renderer.glprimitives.GLVertex;
import renderer.glshaders.GLShader;

public class PolygonalSceneryFactory {

	public static PolygonalScenery create(GLShader shader, Vector3 sceneryPos) {

		Vector3 sceneryAngle = new Vector3(0, 0, 0);
		float sceneryScale = 1;
		Vector4 sceneryColor = new Vector4(0.2f, 0.2f, 0.2f, 1.0f);
		Vector4 normal = new Vector4(0, 0, 1, 1);

		List<GLVertex> vertexList = new ArrayList<GLVertex>();
		vertexList.add(new GLVertex(0, 0, 0, 0, sceneryColor, normal));
		vertexList.add(new GLVertex(1, 0.5f, 0, 0, sceneryColor, normal));
		vertexList.add(new GLVertex(2, 0, 0.5f, 0, sceneryColor, normal));

		List<GLTriangle> triangleList = new ArrayList<GLTriangle>();
		triangleList.add(new GLTriangle(vertexList.get(0), vertexList.get(1), vertexList.get(2)));

		GLModel polygonalSceneryModel = new GLMesh(triangleList, vertexList);
		GLPosition position = new GLPosition(sceneryPos, sceneryAngle, sceneryScale, polygonalSceneryModel.getModelRadius());

		PolygonalScenery polygon = new PolygonalScenery(polygonalSceneryModel, position, "Scenery");

		return polygon;
	}
}
