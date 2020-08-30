package com.rayferric.comet.geometry;

import com.rayferric.comet.math.Vector2f;
import com.rayferric.comet.math.Vector3f;
import com.rayferric.comet.text.FontCharacter;
import com.rayferric.comet.text.FontMetadata;
import com.rayferric.comet.text.HorizontalAlignment;
import com.rayferric.comet.text.VerticalAlignment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GeometryGenerator {
    public static GeometryData genText(String text, FontMetadata meta, HorizontalAlignment hAlign,
                                       VerticalAlignment vAlign, boolean autoWrap, float wrapSize, float charSpacing,
                                       float lineSpacing) {
        // Splits by spaces, removes just one.
        String[] words = text.split("(?<! ) |(?<= {2})");
        float[] wordWidths = new float[words.length];
        List<String[]> lines = new ArrayList<>(text.length());
        List<Float> lineWidths = new ArrayList<>(text.length());

        float spaceWidth = meta.getCharacter(' ').getAdvance() * charSpacing;
        {
            List<String> lineWords = new ArrayList<>();
            float lineWidth = 0;
            for(int i = 0; i < words.length; i++) {
                String word = words[i];
                float wordWidth = 0;
                for(int j = 0; j < word.length(); j++) {
                    FontCharacter character = meta.getCharacter(word.charAt(j));
                    if(character == null) continue;
                    wordWidth += character.getAdvance() * charSpacing;
                }
                wordWidths[i] = wordWidth;

                if(autoWrap && !lineWords.isEmpty() && lineWidth + wordWidth > wrapSize) {
                    lines.add(lineWords.toArray(new String[0]));
                    lineWidths.add(lineWidth);
                    lineWords.clear();
                    lineWidth = 0;
                }

                lineWords.add(word);
                lineWidth += wordWidth + spaceWidth;
            }
            lines.add(lineWords.toArray(new String[0]));
            lineWidths.add(lineWidth);
        }

        List<Face> faces = new ArrayList<>(text.length());

        for(int lineIdx = 0; lineIdx < lines.size(); lineIdx++) {
            String[] line = lines.get(lineIdx);
            float charOffset = 0;
            for(int wordIdx = 0; wordIdx < line.length; wordIdx++) {
                String word = line[wordIdx];
                if(wordIdx != line.length - 1) word += " ";
                for(int charIdx = 0; charIdx < word.length(); charIdx++) {
                    FontCharacter character = meta.getCharacter(word.charAt(charIdx));
                    if(character == null) continue;
                    Vector2f size = character.getSize();
                    float lineHeight = meta.getLineHeight();

                    Vector2f lowerLeftPos = new Vector2f(charOffset, -lineIdx * lineSpacing - 1).add(character.getOffset());

                    switch(hAlign) {
                        case CENTER:
                            lowerLeftPos.setX(lowerLeftPos.getX() - lineWidths.get(lineIdx) * 0.5F);
                            break;
                        case RIGHT:
                            lowerLeftPos.setX(lowerLeftPos.getX() - lineWidths.get(lineIdx));
                            break;
                    }

                    switch(vAlign) {
                        case CENTER:
                            lowerLeftPos.setY(lowerLeftPos.getY() + ((lines.size() - 1) * lineSpacing + 1) * 0.5F);
                            break;
                        case BOTTOM:
                            lowerLeftPos.setY(lowerLeftPos.getY() + ((lines.size() - 1) * lineSpacing + 1));
                            break;
                    }

                    Vector2f upperRightPos =
                            lowerLeftPos.add(new Vector2f(size.getX() / lineHeight, size.getY() / lineHeight));

                    Vector2f lowerLeftTex = character.getPos();
                    Vector2f upperRightTex = lowerLeftTex.add(size);

                    charOffset += character.getAdvance() * charSpacing;
                    faces.add(buildRect(lowerLeftPos, upperRightPos, lowerLeftTex, upperRightTex));
                }
            }
        }

        return index(triangulate(faces.toArray(new Face[0])), true);
    }

    public static GeometryData genPlane(Vector2f size) {
        Face face = buildRect(size.mul(-0.5F), size.mul(0.5F), new Vector2f(0), new Vector2f(1));
        return index(triangulate(new Face[] { face }), true);
    }

    private static Face buildRect(Vector2f lowerLeftPos, Vector2f upperRightPos, Vector2f lowerLeftTex,
                                  Vector2f upperRightTex) {
        Vertex[] vertices = {
                new Vertex(lowerLeftPos.getX(), lowerLeftPos.getY(), 0, lowerLeftTex.getX(), lowerLeftTex.getY()),
                new Vertex(upperRightPos.getX(), lowerLeftPos.getY(), 0, upperRightTex.getX(), lowerLeftTex.getY()),
                new Vertex(upperRightPos.getX(), upperRightPos.getY(), 0, upperRightTex.getX(), upperRightTex.getY()),
                new Vertex(lowerLeftPos.getX(), upperRightPos.getY(), 0, lowerLeftTex.getX(), upperRightTex.getY())
        };
        return new Face(vertices);
    }

    private static Triangle[] triangulate(Face[] faces) {
        int triangleCount = 0;

        for(Face face : faces)
            triangleCount += face.getTriangleCount();

        List<Triangle> triangles = new ArrayList<>(triangleCount);

        for(Face face : faces)
            triangles.addAll(Arrays.asList(face.triangulate()));

        return triangles.toArray(new Triangle[0]);
    }

    private static GeometryData index(Triangle[] triangles, boolean shadeSmooth) {
        // Create unpacked data by simply concatenating triangles' vertices:

        Vertex[] unpackedVertices = new Vertex[triangles.length * 3];
        Vector3f[] unpackedNormals = new Vector3f[triangles.length * 3];
        Vector3f[] unpackedTangents = new Vector3f[triangles.length * 3];
        for(int i = 0; i < triangles.length; i++) {
            Triangle triangle = triangles[i];

            Vertex[] vertices = triangle.getVertices();
            unpackedVertices[i * 3] = vertices[0];
            unpackedVertices[i * 3 + 1] = vertices[1];
            unpackedVertices[i * 3 + 2] = vertices[2];

            Vector3f normal = triangle.getNormal();
            unpackedNormals[i * 3] = normal;
            unpackedNormals[i * 3 + 1] = normal;
            unpackedNormals[i * 3 + 2] = normal;

            Vector3f tangent = triangle.getTangent();
            unpackedTangents[i * 3] = tangent;
            unpackedTangents[i * 3 + 1] = tangent;
            unpackedTangents[i * 3 + 2] = tangent;
        }

        // Generate indices and packed data from unpacked data:

        int[] indices = new int[unpackedVertices.length];
        List<Vertex> packedVertices = new ArrayList<>(unpackedVertices.length);
        List<Vector3f> packedNormals = new ArrayList<>(unpackedVertices.length);
        List<Vector3f> packedTangents = new ArrayList<>(unpackedVertices.length);

        for(int i = 0; i < unpackedVertices.length; i++) {
            Vertex vertex = unpackedVertices[i];
            Vector3f normal = unpackedNormals[i];
            Vector3f tangent = unpackedTangents[i];

            int foundIndex = -1;
            for(int j = 0; j < packedVertices.size(); j++) {
                if(!vertex.equals(packedVertices.get(j))) continue;
                if(!shadeSmooth && !normal.equals(packedNormals.get(j))) continue;
                foundIndex = j;
                break;
            }

            if(foundIndex == -1) {
                indices[i] = packedVertices.size();

                packedVertices.add(vertex);
                packedNormals.add(normal);
                packedTangents.add(tangent);
            } else {
                indices[i] = foundIndex;

                // Only sum when smooth shading is enabled, otherwise it's pointless:
                if(shadeSmooth) packedNormals.set(foundIndex, packedNormals.get(foundIndex).add(normal));
                packedTangents.set(foundIndex, packedTangents.get(foundIndex).add(tangent));
            }
        }

        // Normalize vectors and store vertices in a float array:

        final int vertexFloats = 3 + 2 + 3 + 3;
        float[] vertices = new float[packedVertices.size() * vertexFloats];

        for(int i = 0; i < packedVertices.size(); i++) {
            Vertex vertex = packedVertices.get(i);
            Vector3f normal = packedNormals.get(i).normalize();
            Vector3f tangent = packedTangents.get(i).normalize();

            System.arraycopy(vertex.toArray(), 0, vertices, i * vertexFloats, 5);
            System.arraycopy(normal.toArray(), 0, vertices, i * vertexFloats + 5, 3);
            System.arraycopy(tangent.toArray(), 0, vertices, i * vertexFloats + 8, 3);
        }

        return new GeometryData(vertices, indices);
    }
}
