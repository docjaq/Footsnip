package renderer.glprimitives;

import java.util.ArrayList;
import java.util.List;

import math.types.Vector3;
import math.types.Vector4;

public class GLVertex {

	private List<GLTriangle> parentTriangles;

	public void addParentTriangle(GLTriangle t) {
		if (parentTriangles == null) {
			parentTriangles = new ArrayList<GLTriangle>();
		}
		parentTriangles.add(t);
	}

	public List<GLTriangle> getParentTriangles() {
		return parentTriangles;
	}

	// The amount of bytes an element has
	public static final int elementBytes = 4;

	// Elements per parameter
	public static final int positionElementCount = 4;
	public static final int colorElementCount = 4;
	public static final int normalElementCount = 4;
	// public static final int textureElementCount = 2;

	// Bytes per parameter
	public static final int positionBytesCount = positionElementCount * elementBytes;
	public static final int colorByteCount = colorElementCount * elementBytes;
	public static final int normalByteCount = normalElementCount * elementBytes;
	// public static final int textureByteCount = textureElementCount *
	// elementBytes;

	// Byte offsets per parameter
	public static final int positionByteOffset = 0;
	public static final int colorByteOffset = positionByteOffset + positionBytesCount;
	public static final int normalByteOffset = colorByteOffset + colorByteCount;
	// public static final int textureByteOffset = colorByteOffset +
	// colorByteCount;

	// The number of elements that a vertex has
	public static final int elementCount = positionElementCount + colorElementCount + normalElementCount;
	// The size of a vertex in bytes, like in C/C++: sizeof(Vertex)
	public static final int stride = positionBytesCount + colorByteCount + normalByteCount;

	/**
	 * Not entirely sure that storing a colour as a vector is a good idea.
	 * Probably not. Have also made this public to allow for quicker access. BAD
	 * JON.
	 **/
	public Vector4 rgba;
	public Vector4 nxnynznw;
	public Vector4 xyzw;
	public int index;

	/**
	 * TODO: A lot of different constructors here. Probably need to decide
	 * whether a vertex should be allowed to have less than all of the
	 * components. Either that or access to find out what it has, or just to
	 * create blank elements
	 **/

	public GLVertex() {
	}

	public GLVertex(int index) {
		this.index = index;
	}

	public GLVertex(int index, Vector4 xyzw, Vector4 rgba, Vector4 nxnynznw) {
		this.index = index;
		this.xyzw = xyzw;
		this.rgba = rgba;
		this.nxnynznw = nxnynznw;
	}

	/*
	 * public GLVertex(float x, float y, float z, float w, float r, float g,
	 * float b, float a, float nx, float ny, float nz, float nw) { this.xyzw =
	 * new Vector4f(x, y, z, w); this.rgba = new Vector4f(r, g, b, a);
	 * this.nxnynznw = new Vector4f(nx, ny, nz, nw); }
	 * 
	 * public GLVertex(float x, float y, float z, float w, Vector4f rgba, float
	 * nx, float ny, float nz, float nw) { this.xyzw = new Vector4f(x, y, z, w);
	 * this.rgba = rgba; this.nxnynznw = new Vector4f(nx, ny, nz, nw); }
	 * 
	 * public GLVertex(float x, float y, float z, Vector4f rgba, float nx, float
	 * ny, float nz) { this.xyzw = new Vector4f(x, y, z, 1f); this.rgba = rgba;
	 * this.nxnynznw = new Vector4f(nx, ny, nz, 1f); }
	 * 
	 * public GLVertex(float x, float y, float z, Vector4f rgba) { this.xyzw =
	 * new Vector4f(x, y, z, 1f); this.rgba = rgba; }
	 */

	public GLVertex(int index, float x, float y, float z, Vector4 rgba) {
		this.index = index;
		this.xyzw = new Vector4(x, y, z, 1f);
		this.rgba = rgba;// new Vector4f(0, 0, 0, 1);
	}

	public GLVertex(int index, float x, float y, float z, Vector4 rgba, Vector4 nxnynznw) {
		this.index = index;
		this.xyzw = new Vector4(x, y, z, 1f);
		this.rgba = rgba;
		this.nxnynznw = nxnynznw;
	}

	// Setters
	public void setXYZ(float x, float y, float z) {
		this.setXYZW(x, y, z, 1f);
	}

	public void setRGB(float r, float g, float b) {
		this.setRGBA(r, g, b, 1f);
	}

	public void setNXNYNZ(float nx, float ny, float nz) {
		this.setNXNYNZNW(nx, ny, nz, 1f);
	}

	public void setNXNYNZ(Vector3 nxnynznw) {
		this.setNXNYNZNW(nxnynznw.x(), nxnynznw.y(), nxnynznw.z(), 1f);
	}

	public void setXYZW(float x, float y, float z, float w) {
		this.xyzw = new Vector4(x, y, z, w);
	}

	public void setRGBA(float r, float g, float b, float a) {
		this.rgba = new Vector4(r, g, b, 1f);
	}

	public void setRGBA(Vector4 rgba) {
		this.rgba = rgba;
	}

	public void setNXNYNZNW(float nx, float ny, float nz, float nw) {
		this.nxnynznw = new Vector4(nx, ny, nz, nw);
	}

	// Getters
	public float[] getElements() {
		float[] out = new float[GLVertex.elementCount];
		int i = 0;

		// Insert XYZW elements
		out[i++] = this.xyzw.x();
		out[i++] = this.xyzw.y();
		out[i++] = this.xyzw.z();
		out[i++] = this.xyzw.w();
		// Insert RGBA elements
		out[i++] = this.rgba.x();
		out[i++] = this.rgba.y();
		out[i++] = this.rgba.z();
		out[i++] = this.rgba.w();
		// Insert ST elements
		out[i++] = this.nxnynznw.x();
		out[i++] = this.nxnynznw.y();
		out[i++] = this.nxnynznw.z();
		out[i++] = this.nxnynznw.w();

		return out;
	}

	/**
	 * JAQ I'm not entirely sure why all of these need to be copies. I guess as
	 * some of them are different sizes, to be consistent, it makes sense. Ho
	 * hum.
	 **/
	public Vector4 getXYZW() {
		return new Vector4(this.xyzw);
	}

	public Vector3 getXYZ() {
		return new Vector3(this.xyzw.x(), this.xyzw.y(), this.xyzw.z());
	}

	public Vector4 getRGBA() {
		return new Vector4(this.rgba);
	}

	public Vector3 getRGB() {
		return new Vector3(this.rgba.x(), this.rgba.y(), this.rgba.z());
	}

	public Vector4 getNXNYNZNW() {
		return new Vector4(this.nxnynznw);
	}

	public Vector3 getNXNYNZ() {
		return new Vector3(this.nxnynznw.x(), this.nxnynznw.y(), this.nxnynznw.z());
	}

	@Override
	public Object clone() {
		GLVertex cloned = new GLVertex();
		cloned.setXYZW(this.xyzw.x(), this.xyzw.y(), this.xyzw.z(), this.xyzw.w());
		cloned.setRGBA(this.rgba.x(), this.rgba.y(), this.rgba.z(), this.rgba.w());
		cloned.setNXNYNZNW(this.nxnynznw.x(), this.nxnynznw.y(), this.nxnynznw.z(), this.nxnynznw.w());
		return cloned;
	}
}
