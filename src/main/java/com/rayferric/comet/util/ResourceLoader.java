package com.rayferric.comet.util;

import org.lwjgl.system.MemoryUtil;

import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * Utility class used to read data from external files and JAR resources.
 */
public class ResourceLoader {
    /**
     * Reads text data from resource.
     *
     * @param fromJar whether to search in a JAR file
     * @param path    path to resource
     *
     * @return text data
     */
    public static String readTextFileToString(boolean fromJar, String path) {
        byte[] bytes = readFile(fromJar, path);
        return new String(bytes);
    }

    /**
     * Reads binary data from resource to native buffer.<br>
     * • Native buffer must be manually freed using {@link MemoryUtil#memFree(Buffer)}.
     *
     * @param fromJar whether to search in a JAR file
     * @param path    path to resource
     *
     * @return binary data
     */
    public static ByteBuffer readBinaryFileToNativeBuffer(boolean fromJar, String path) {
        byte[] bytes = readFile(fromJar, path);

        ByteBuffer nativeBuffer = MemoryUtil.memAlloc(bytes.length);
        nativeBuffer.put(bytes);
        nativeBuffer.flip();

        return nativeBuffer;
    }

    /**
     * Extracts parent directory from file path.
     *
     * @param path path to extract from
     *
     * @return parent directory path with ending slash
     */
    public static String getDir(String path) {
        int slashIndex = path.replaceAll("\\\\", "/").lastIndexOf('/') + 1;
        return path.substring(0, slashIndex);
    }

    /**
     * Reads data from resource to byte array.
     *
     * @param fromJar whether to search in a JAR file
     * @param path    path to resource
     *
     * @return byte array
     */
    private static byte[] readFile(boolean fromJar, String path) {
        InputStream stream;
        if(fromJar) {
            stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
            if(stream == null)
                throw new RuntimeException(formatErrorMessage(true, path));
        } else {
            File file = new File(path);
            try {
                stream = new FileInputStream(file);
            } catch(FileNotFoundException e) {
                throw new RuntimeException(formatErrorMessage(false, path));
            }

        }

        byte[] bytes;
        try {
            bytes = stream.readAllBytes();
            stream.close();
        } catch(IOException e) {
            throw new RuntimeException(formatErrorMessage(fromJar, path));
        }

        return bytes;
    }

    /**
     * Formats an exception message based on resource path and its origin.
     *
     * @param fromJar whether it's a JAR resource
     * @param path    path to resource
     *
     * @return failure message
     */
    private static String formatErrorMessage(boolean fromJar, String path) throws RuntimeException {
        if(fromJar)
            return "Failed to read JAR resource.\n" + path;
        else
            return "Failed to read file.\n" + path;
    }
}
