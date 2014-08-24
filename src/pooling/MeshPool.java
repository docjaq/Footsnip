package pooling;

import renderer.glmodels.GLMesh;

public abstract class MeshPool<T extends GLMesh> extends ObjectPool<T> {

    protected int tileComplexity;

    public MeshPool(int minPoolSize, int maxPoolSize, long updateInterval, int tileComplexity){
        super(minPoolSize, maxPoolSize, updateInterval);

        this.tileComplexity = tileComplexity;
    }

    public GLMesh borrowObject(float[][] heightmap){
        GLMesh object = borrowObject();

        transformMeshFromHeightmap(object, heightmap);

        return object;
    }

    //protected abstract float[][] generateHeightmap();
    protected abstract void transformMeshFromHeightmap(GLMesh mesh, float[][] heightmap);
}
