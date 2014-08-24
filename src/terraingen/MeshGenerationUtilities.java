package terraingen;

import assets.world.AbstractTile;
import math.types.Vector4;
import renderer.glprimitives.GLTriangle;
import renderer.glprimitives.GLVertex;

import java.util.List;

public class MeshGenerationUtilities {

    private static final float Z_OFFSET = 0;

    public static void generatePlanarMesh(List<GLVertex> vertices, List<GLTriangle> triangles) {

        int localTileComplexity = 150;

        float xInc = AbstractTile.SIZE / (float) (localTileComplexity - 1);
        float yInc = AbstractTile.SIZE / (float) (localTileComplexity - 1);

        float xyOffset = AbstractTile.SIZE / 2f;

        int index = 0;

        for (int i = 0; i < localTileComplexity * localTileComplexity; i++) {
            GLVertex vertex = new GLVertex(i);
            vertex.rgba = new Vector4(0.4f, 0.4f, 0.9f, 1f);
            vertex.nxnynznw = new Vector4(0, 0, 1, 1);
            vertices.add(vertex);
        }

        for (int i = 0; i < localTileComplexity; i++) {
            vertices.get(index).setXYZ((float) (i * xInc - xyOffset), -xyOffset, Z_OFFSET);
            index++;
        }

        int numItems = index;
        System.out.println("NumItems: " + numItems);

        for (int i = 1; i < localTileComplexity; i++) {
            int count = 0;
            for (int j = 0; j < localTileComplexity; j++) {
                vertices.get(index).setXYZ((float) (j * xInc - xyOffset), (float) (i * yInc - xyOffset), Z_OFFSET);
                addTriangles(vertices, triangles, index, count, numItems);
                index++;
                count++;
            }
        }
    }


    private static GLTriangle generateTriangle(GLVertex v0, GLVertex v1, GLVertex v2) {
        GLTriangle triangle = new GLTriangle(v0, v1, v2);
        v0.addParentTriangle(triangle);
        v1.addParentTriangle(triangle);
        v2.addParentTriangle(triangle);

        return triangle;
    }


    private static void addTriangles(List<GLVertex> vertices, List<GLTriangle> triangles, int index, int count, int numItems) {
        if (count == 0) {
        } else {
            triangles.add(generateTriangle(vertices.get(index), vertices.get(index - 1), vertices.get(index - numItems - 1)));
            triangles.add(generateTriangle(vertices.get(index), vertices.get(index - numItems - 1), vertices.get(index - numItems)));
        }
    }
}
