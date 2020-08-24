package com.rayferric.comet.video.api.gl.texture;

import com.rayferric.comet.engine.Engine;
import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.video.VideoServer;
import com.rayferric.comet.video.util.texture.TextureFormat;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.EXTTextureCompressionS3TC.GL_COMPRESSED_RGBA_S3TC_DXT5_EXT;
import static org.lwjgl.opengl.EXTTextureCompressionS3TC.GL_COMPRESSED_RGB_S3TC_DXT1_EXT;
import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.*;

public class GLTexture2D extends GLTexture {
    public GLTexture2D(Buffer data, Vector2i size, TextureFormat format, boolean filter) {
        VideoServer videoServer = Engine.getInstance().getVideoServer();
        int minFilter = switch(videoServer.getTextureFilter()) {
            case NEAREST -> GL_NEAREST;
            case BILINEAR -> GL_LINEAR;
            case TRILINEAR -> GL_LINEAR_MIPMAP_LINEAR;
        };

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filter ? GL_LINEAR : GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minFilter);

        float maxAnisotropy = glGetFloat(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT);
        float anisotropy = Math.min(videoServer.getTextureAnisotropy(), maxAnisotropy);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, anisotropy);

        boolean compressed, hdr;
        int internalFormat, baseFormat, type;
        switch(format) {
            default:
            case R8:
                compressed = false;
                hdr = false;
                internalFormat = GL_R8;
                baseFormat = GL_RED;
                type = GL_UNSIGNED_BYTE;
                break;
            case R16F:
                compressed = false;
                hdr = true;
                internalFormat = GL_R16F;
                baseFormat = GL_RED;
                type = GL_HALF_FLOAT;
                break;
            case R32F:
                compressed = false;
                hdr = true;
                internalFormat = GL_R32F;
                baseFormat = GL_RED;
                type = GL_FLOAT;
                break;
            case RG8:
                compressed = false;
                hdr = false;
                internalFormat = GL_RG8;
                baseFormat = GL_RG;
                type = GL_UNSIGNED_BYTE;
                break;
            case RG16F:
                compressed = false;
                hdr = true;
                internalFormat = GL_RG16F;
                baseFormat = GL_RG;
                type = GL_HALF_FLOAT;
                break;
            case RG32F:
                compressed = false;
                hdr = true;
                internalFormat = GL_RG32F;
                baseFormat = GL_RG;
                type = GL_FLOAT;
                break;
            case RGB8:
                compressed = false;
                hdr = false;
                internalFormat = GL_RGB8;
                baseFormat = GL_RGB;
                type = GL_UNSIGNED_BYTE;
                break;
            case RGB16F:
                compressed = false;
                hdr = true;
                internalFormat = GL_RGB16F;
                baseFormat = GL_RGB;
                type = GL_HALF_FLOAT;
                break;
            case RGB32F:
                compressed = false;
                hdr = true;
                internalFormat = GL_RGB32F;
                baseFormat = GL_RGB;
                type = GL_FLOAT;
                break;
            case RGBA8:
                compressed = false;
                hdr = false;
                internalFormat = GL_RGBA8;
                baseFormat = GL_RGBA;
                type = GL_UNSIGNED_BYTE;
                break;
            case RGBA16F:
                compressed = false;
                hdr = true;
                internalFormat = GL_RGBA16F;
                baseFormat = GL_RGBA;
                type = GL_HALF_FLOAT;
                break;
            case RGBA32F:
                compressed = false;
                hdr = true;
                internalFormat = GL_RGBA32F;
                baseFormat = GL_RGBA;
                type = GL_FLOAT;
                break;
            case SRGB8:
                compressed = false;
                hdr = false;
                internalFormat = GL_SRGB8;
                baseFormat = GL_RGB;
                type = GL_UNSIGNED_BYTE;
                break;
            case SRGBA8:
                compressed = false;
                hdr = false;
                internalFormat = GL_SRGB8_ALPHA8;
                baseFormat = GL_RGBA;
                type = GL_UNSIGNED_BYTE;
                break;
            case BC1:
                compressed = true;
                hdr = false;
                internalFormat = GL_COMPRESSED_RGB_S3TC_DXT1_EXT;
                baseFormat = GL_RGB;
                type = GL_UNSIGNED_BYTE;
                break;
            case BC3:
                compressed = true;
                hdr = false;
                internalFormat = GL_COMPRESSED_RGBA_S3TC_DXT5_EXT;
                baseFormat = GL_RGBA;
                type = GL_UNSIGNED_BYTE;
                break;
            case BC4:
                compressed = true;
                hdr = false;
                internalFormat = GL_COMPRESSED_RED_RGTC1;
                baseFormat = GL_RED;
                type = GL_UNSIGNED_BYTE;
                break;
            case BC5:
                compressed = true;
                hdr = false;
                internalFormat = GL_COMPRESSED_RG_RGTC2;
                baseFormat = GL_RG;
                type = GL_UNSIGNED_BYTE;
                break;
            case BC6:
                compressed = true;
                hdr = true;
                internalFormat = GL_COMPRESSED_RGB_BPTC_UNSIGNED_FLOAT;
                baseFormat = GL_RGB;
                type = GL_UNSIGNED_BYTE;
                break;
            case BC7:
                compressed = true;
                hdr = false;
                internalFormat = GL_COMPRESSED_RGBA_BPTC_UNORM;
                baseFormat = GL_RGBA;
                type = GL_UNSIGNED_BYTE;
                break;
        }

        if(compressed)
            glCompressedTexImage2D(GL_TEXTURE_2D, 0, internalFormat, size.getX(), size.getY(), 0, (ByteBuffer)data);
        else
            if(hdr)
                glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, size.getX(), size.getY(), 0, baseFormat, type, (FloatBuffer)data);
            else
                glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, size.getX(), size.getY(), 0, baseFormat, type, (ByteBuffer)data);

        if(data != null)
            glGenerateMipmap(GL_TEXTURE_2D);
    }

    @Override
    public void bind() {
        glBindTexture(GL_TEXTURE_2D, handle);
    }
}
