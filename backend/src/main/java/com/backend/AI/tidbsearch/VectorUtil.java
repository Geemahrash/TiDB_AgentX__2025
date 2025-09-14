package com.backend.AI.tidbsearch;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Arrays;

/**
 * Utility class for vector operations used in TiDB vector search
 */
public class VectorUtil {

    /**
     * Convert a float array to byte array for storage in TiDB
     * 
     * @param floatArray The float array to convert
     * @return The byte array representation
     */
    public static byte[] floatArrayToByteArray(float[] floatArray) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(floatArray.length * 4);
        for (float value : floatArray) {
            byteBuffer.putFloat(value);
        }
        return byteBuffer.array();
    }

    /**
     * Convert a byte array back to float array
     * 
     * @param byteArray The byte array to convert
     * @return The float array representation
     */
    public static float[] byteArrayToFloatArray(byte[] byteArray) {
        FloatBuffer floatBuffer = ByteBuffer.wrap(byteArray).asFloatBuffer();
        float[] floatArray = new float[floatBuffer.limit()];
        floatBuffer.get(floatArray);
        return floatArray;
    }

    /**
     * Calculate cosine similarity between two float vectors
     * 
     * @param vector1 First vector
     * @param vector2 Second vector
     * @return Cosine similarity value between -1 and 1
     */
    public static float cosineSimilarity(float[] vector1, float[] vector2) {
        if (vector1.length != vector2.length) {
            throw new IllegalArgumentException("Vectors must have the same dimension");
        }

        float dotProduct = 0.0f;
        float norm1 = 0.0f;
        float norm2 = 0.0f;

        for (int i = 0; i < vector1.length; i++) {
            dotProduct += vector1[i] * vector2[i];
            norm1 += vector1[i] * vector1[i];
            norm2 += vector2[i] * vector2[i];
        }

        if (norm1 <= 0.0f || norm2 <= 0.0f) {
            return 0.0f;
        }

        return dotProduct / (float) (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}