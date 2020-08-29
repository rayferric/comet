package com.rayferric.spatialwalker;

import com.rayferric.comet.text.FontMetadata;
import com.rayferric.comet.math.Matrix4f;
import com.rayferric.comet.math.Vector3f;
import com.rayferric.comet.util.ResourceLoader;

public class Test {
    public static void main(String[] args) {
        Vector3f euler = new Vector3f(0, 5, 0);

        FontMetadata meta = new FontMetadata(ResourceLoader.readTextFileToString(false, "data/fonts/open-sans-bold.fnt"), "data/fonts/open-sans-bold.fnt");

        //printMatrix(transform.getMatrix());
    }

    private static void printMatrix(Matrix4f matrix) {
        System.out.println(matrix.getX());
        System.out.println(matrix.getY());
        System.out.println(matrix.getZ());
        System.out.println(matrix.getW());
        System.out.println();
    }
}
