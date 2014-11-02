package pooling;

import java.nio.ByteBuffer;

import renderer.glmodels.GLMesh;

public abstract class MeshPool<T extends GLMesh> extends ObjectPool<T> {

	protected int tileComplexity;

	public MeshPool(int minPoolSize, int maxPoolSize, long updateInterval, int tileComplexity) {
		super(minPoolSize, maxPoolSize, updateInterval);

		this.tileComplexity = tileComplexity;
	}

	public GLMesh borrowObject(ByteBuffer heightmapBuff) {
		GLMesh object = borrowObject();

		transformMeshFromHeightmap(object, heightmapBuff);

		return object;
	}

	// Deprecated from current Footsnip
	public GLMesh borrowObject(float[][] heightmap) {
		GLMesh object = borrowObject();

		transformMeshFromHeightmap(object, heightmap);

		return object;
	}

	// //Deprecated from current Footsnip
	protected abstract void transformMeshFromHeightmap(GLMesh mesh, float[][] heightmap);

	protected abstract void transformMeshFromHeightmap(GLMesh mesh, ByteBuffer heightmap);
}
