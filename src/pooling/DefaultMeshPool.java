package pooling;

public class DefaultMeshPool extends MeshPool<T> {

    public DefaultMeshPool(int minPoolSize, int maxPoolSize, long updateInterval){
        super(minPoolSize, maxPoolSize, updateInterval);
    }

    @Override
    protected float[][] generateHeightmap() {
        return new float[0][];
    }

    @Override
    protected void transformMeshFromHeightmap() {

    }

    @Override
    protected T initaliseObject() {
        return null;
    }
}
