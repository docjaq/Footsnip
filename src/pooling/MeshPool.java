package pooling;

public abstract class MeshPool<T> extends ObjectPool<T> {

    public MeshPool(int minPoolSize, int maxPoolSize, long updateInterval){
        super(minPoolSize, maxPoolSize, updateInterval);
    }

    @Override
    protected T borrowObject(){
        T object = super.borrowObject();

        //Create heightmap
        generateHeightmap();

        //Modify mesh to heightmap
        transformMeshFromHeightmap();

        return object;
    }

    protected abstract float[][] generateHeightmap();
    protected abstract void transformMeshFromHeightmap();
}
