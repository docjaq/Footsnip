package mesh;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import renderer.glprimitives.GLTriangle;
import renderer.glprimitives.GLVertex;

public abstract class AbstractGeometryFile implements GeometryFile {

	protected ArrayList<GLVertex> vertices;
	protected ArrayList<GLTriangle> triangles;

	public AbstractGeometryFile(ArrayList<GLTriangle> triangles, ArrayList<GLVertex> vertices) {
		this.triangles = triangles;
		this.vertices = vertices;
	}

	public AbstractGeometryFile() {
	}

	public void normaliseAndCentre(ArrayList<GLVertex> vertices) {
		Vector3f ma = new Vector3f(-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE);
		Vector3f mi = new Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);

		for (GLVertex p : vertices) {
			if (p.xyzw.x < mi.x) {
				mi.x = p.xyzw.x;
			}
			if (p.xyzw.x > ma.x) {
				ma.x = p.xyzw.x;
			}
			if (p.xyzw.y < mi.y) {
				mi.y = p.xyzw.y;
			}
			if (p.xyzw.y > ma.y) {
				ma.y = p.xyzw.y;
			}
			if (p.xyzw.z < mi.z) {
				mi.z = p.xyzw.z;
			}
			if (p.xyzw.z > ma.z) {
				ma.z = p.xyzw.z;
			}
		}

		Vector3f vRange = new Vector3f(Vector3f.sub(ma, mi, null));
		float d = Math.max(vRange.x, vRange.y);
		d = 0.5f * Math.max(d, vRange.z);
		Vector3f vCentre = new Vector3f(Vector3f.add(ma, mi, null));
		vCentre.scale(0.5f);

		for (GLVertex p : vertices) {
			p.xyzw.x = (p.xyzw.x - vCentre.x) / d;
			p.xyzw.y = (p.xyzw.y - vCentre.y) / d;
			p.xyzw.z = (p.xyzw.z - vCentre.z) / d;
		}

		/** If any properties are stored in the triangle, update them now **/
	}
}
