package mesh;

import java.util.ArrayList;

import math.types.Vector3;
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
		Vector3 ma = new Vector3(-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE);
		Vector3 mi = new Vector3(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);

		for (GLVertex p : vertices) {
			if (p.xyzw.x() < mi.x()) {
				mi.x(p.xyzw.x());
			}
			if (p.xyzw.x() > ma.x()) {
				ma.x(p.xyzw.x());
			}
			if (p.xyzw.y() < mi.y()) {
				mi.y(p.xyzw.y());
			}
			if (p.xyzw.y() > ma.y()) {
				ma.y(p.xyzw.y());
			}
			if (p.xyzw.z() < mi.z()) {
				mi.z(p.xyzw.z());
			}
			if (p.xyzw.z() > ma.z()) {
				ma.z(p.xyzw.z());
			}
		}

		Vector3 vRange = Vector3.sub(ma, mi);

		float d = Math.max(vRange.x(), vRange.y());
		d = 0.5f * Math.max(d, vRange.z());
		Vector3 vCentre = Vector3.add(ma, mi);
		// vCentre.scale(0.5f);
		vCentre.mult(0.5f);

		for (GLVertex p : vertices) {
			p.xyzw.x((p.xyzw.x() - vCentre.x()) / d);
			p.xyzw.y((p.xyzw.y() - vCentre.y()) / d);
			p.xyzw.z((p.xyzw.z() - vCentre.z()) / d);
		}

		/** If any properties are stored in the triangle, update them now **/
	}
}
