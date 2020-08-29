package com.rayferric.comet.text;

import com.rayferric.comet.math.Vector2f;
import com.rayferric.comet.util.ResourceLoader;

import java.util.HashMap;
import java.util.Scanner;

public class FontMetadata {
    public FontMetadata(String data, String rootPath) {
        try {
            Vector2f atlasSizePxInv = new Vector2f(0);
            int lineHeightPx = 0;

            Scanner scanner = new Scanner(data);
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String header = line.substring(0, line.indexOf(' '));
                switch(header) {
                    case "common" -> {
                        atlasSizePxInv.setX(1F / Integer.parseInt(getProperty(line, "scaleW")));
                        atlasSizePxInv.setY(1F / Integer.parseInt(getProperty(line, "scaleH")));
                        lineHeightPx = Integer.parseInt(getProperty(line, "lineHeight"));
                        lineHeight = lineHeightPx * atlasSizePxInv.getY();
                        int pages = Integer.parseInt(getProperty(line, "pages"));
                        if(pages != 1)
                            throw new RuntimeException("Multi-page fonts are not supported.");
                    }
                    case "page" -> {
                        int id = Integer.parseInt(getProperty(line, "id"));
                        if(id == 0) atlasPath = ResourceLoader.getDir(rootPath) + getProperty(line, "file");
                    }
                    case "char" -> {
                        int page = Integer.parseInt(getProperty(line, "page"));
                        if(page != 0) continue;
                        char code = (char)Integer.parseInt(getProperty(line, "id"));
                        int x = Integer.parseInt(getProperty(line, "x"));
                        int y = Integer.parseInt(getProperty(line, "y"));
                        int width = Integer.parseInt(getProperty(line, "width"));
                        int height = Integer.parseInt(getProperty(line, "height"));
                        int offsetX = Integer.parseInt(getProperty(line, "xoffset"));
                        int offsetY = Integer.parseInt(getProperty(line, "yoffset"));
                        int advanceX = Integer.parseInt(getProperty(line, "xadvance"));
                        Vector2f pos = new Vector2f(x, y + height).mul(new Vector2f(atlasSizePxInv.getX(), -atlasSizePxInv.getY()));
                        Vector2f size = new Vector2f(width, height).mul(atlasSizePxInv);
                        Vector2f offset = new Vector2f(offsetX,  lineHeightPx - (offsetY + height)).div(lineHeightPx);
                        float advance = (float)advanceX / lineHeightPx;
                        FontCharacter character = new FontCharacter(pos, size, offset, advance);
                        chars.put(code, character);
                    }
                }
            }
            scanner.close();

            if(atlasPath.isEmpty())
                throw new RuntimeException("Could not read atlas location.");
        } catch(Throwable e) {
            throw new RuntimeException("Failed to parse BMFont file.\n" + e.getMessage());
        }
    }

    public float getLineHeight() {
        return lineHeight;
    }

    public String getAtlasPath() {
        return atlasPath;
    }

    public FontCharacter getCharacter(char code) {
        return chars.get(code);
    }

    // Line height in atlas scale units:
    private float lineHeight = 0;
    private String atlasPath = "";
    private final HashMap<Character, FontCharacter> chars = new HashMap<>();

    private String getProperty(String line, String property) {
        int startIdx = line.indexOf(property) + property.length() + 1;
        int endIdx;
        if(line.charAt(startIdx) == '"') {
            startIdx++;
            endIdx = line.indexOf('"', startIdx + 1);
        } else endIdx = line.indexOf(' ', startIdx);
        return line.substring(startIdx, endIdx);
    }
}
