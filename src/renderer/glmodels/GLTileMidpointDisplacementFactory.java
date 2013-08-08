package renderer.glmodels;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import renderer.glprimitives.GLTriangle;
import renderer.glprimitives.GLVertex;
import renderer.glshaders.GLShader;

public class GLTileMidpointDisplacementFactory implements GLTileFactory {
	/*
	 * TODO: We don't want to keep generating the basic grid, so if we create
	 * that here in the constructor. Then every time we want to create a tile,
	 * we clone the existing grid, generate a new heightmap image, then deform
	 * the default grid according to the heightmap
	 */

	private float[][] heightMap;
	private List<GLVertex> factoryVertices;
	private List<GLTriangle> factoryTriangles;
	private int tileComplexity;
	private Vector4f tileColor;
	final static float zOffset = -0.1f;
	final static float zAdjust = 0.005f;

	public GLTileMidpointDisplacementFactory(int tileComplexity) {
		this.tileComplexity = tileComplexity;
		this.heightMap = new float[tileComplexity][tileComplexity];
		this.factoryVertices = new ArrayList<GLVertex>(tileComplexity * tileComplexity);
		this.factoryTriangles = new ArrayList<GLTriangle>((tileComplexity - 1) * (tileComplexity - 1) * 2);

		this.tileColor = new Vector4f((float) Math.random(), (float) Math.random(), (float) Math.random(), 1);

		generatePlanarMesh();
	}

	public GLMesh create(Vector3f position, Vector3f rotation, float scale, GLShader shader, float[] color, float size) {
		/*
		 * TODO: Create copy of factoryVertices, and a copy of factoryTriangles.
		 * Currently not doing this, as it will screw up the references between
		 * the triangles and vertices. Needs to be clever. For now, just
		 * creating mesh, and reusing mesh
		 */
		// List<GLVertex> localVertices = new
		// ArrayList<GLVertex>(factoryVertices.size());
		// for (GLVertex vertex : factoryVertices) {
		// localVertices.add((GLVertex) vertex.clone());
		// }

		// Create heightmap
		// resetHeights(this.factoryVertices);
		computeHeightmap(this.factoryVertices);

		// Compute normals for new vertexList
		computeNormalsForAllTriangles(this.factoryTriangles, this.factoryVertices);

		// Create mesh from vertexList and TriangleList
		return new GLMesh(this.factoryTriangles, this.factoryVertices, position, rotation, scale, shader, color);
	}

	private void generatePlanarMesh() {
		/*
		 * TODO: the size is currently hardcoded. Whilst 1f is likely, currently
		 * we pass the size parameter in the create method above, and it does
		 * nothing
		 */
		float xInc = 1f / (float) (tileComplexity - 1);
		float yInc = 1f / (float) (tileComplexity - 1);

		int index = 0;

		for (int i = 0; i < tileComplexity * tileComplexity; i++) {
			factoryVertices.add(new GLVertex(i));
		}

		for (int i = 0; i < tileComplexity; i++) {
			// GLVertex point1 = new GLVertex();
			factoryVertices.get(index).setXYZ((float) (i * xInc - 0.5f), -0.5f, zOffset);
			factoryVertices.get(index).setRGBA(tileColor);
			// factoryVertices.add(point1);
			index++;
		}

		int numItems = index;
		System.out.println("NumItems: " + numItems);

		for (int i = 1; i < tileComplexity; i++) {
			int count = 0;
			for (int j = 0; j < tileComplexity; j++) {
				// GLVertex point = new GLVertex();
				factoryVertices.get(index).setXYZ((float) (j * xInc - 0.5f), (float) (i * yInc - 0.5f), zOffset);
				factoryVertices.get(index).setRGBA(tileColor);
				// factoryVertices.add(point);
				// System.out.println(index + "," + count + "," + numItems);
				addTriangles(index, count, numItems);
				index++;
				count++;
			}
		}
	}

	private void computeHeightmap(List<GLVertex> vertices) {
		for (GLVertex v : vertices) {
			// if (v.xyzw.x < -0.45f || v.xyzw.x > 0.45f || v.xyzw.y < -0.45f ||
			// v.xyzw.y > 0.45f) {
			// } else {
			v.xyzw.z = (float) (Math.random() * (zAdjust * 2) - zAdjust + zOffset);
			// }
		}
	}

	/* TODO: remove this when a copy of the mesh is made each time */
	private void resetHeights(List<GLVertex> vertices) {
		for (GLVertex v : vertices) {
			v.xyzw.z = zOffset;
		}
	}

	private void addTriangles(int index, int count, int numItems) {
		if (count == 0) {
		} else {
			factoryTriangles.add(new GLTriangle(factoryVertices.get(index), factoryVertices.get(index - 1), factoryVertices.get(index
					- numItems - 1)));
			factoryTriangles.add(new GLTriangle(factoryVertices.get(index), factoryVertices.get(index - numItems - 1), factoryVertices
					.get(index - numItems)));
		}
	}

	private static void computeNormalsForAllTriangles(List<GLTriangle> triangles, List<GLVertex> vertices) {
		for (GLTriangle triangle : triangles) {
			addNormalToTriangle(triangle.v0, triangle.v1, triangle.v2);
		}
	}

	public static void addNormalToTriangle(GLVertex v0, GLVertex v1, GLVertex v2) {
		Vector3f vn0 = Vector3f.cross(Vector3f.sub(v1.getXYZ(), v0.getXYZ(), null), Vector3f.sub(v2.getXYZ(), v0.getXYZ(), null), null);
		vn0.normalise();
		v0.setNXNYNZ(vn0);

		Vector3f vn1 = Vector3f.cross(Vector3f.sub(v2.getXYZ(), v1.getXYZ(), null), Vector3f.sub(v0.getXYZ(), v1.getXYZ(), null), null);
		vn1.normalise();
		v1.setNXNYNZ(vn1);

		Vector3f vn2 = Vector3f.cross(Vector3f.sub(v0.getXYZ(), v2.getXYZ(), null), Vector3f.sub(v1.getXYZ(), v2.getXYZ(), null), null);
		vn2.normalise();
		v2.setNXNYNZ(vn2);
	}
}
