package com.rayferric.comet.server;

import com.rayferric.comet.video.common.VideoEngine;
import com.rayferric.comet.video.gl.GLVideoEngine;

public class VideoServer extends Server {
    public enum VideoAPI {
        OPENGL
    }

    public VideoServer(VideoAPI api) {
        super(() -> {

        });

        if(api == VideoAPI.OPENGL)
            videoEngine = new GLVideoEngine();

        thread.start();
    }

    public VideoEngine getVideoEngine() {
        return videoEngine;
    }

    private VideoEngine videoEngine;
}
