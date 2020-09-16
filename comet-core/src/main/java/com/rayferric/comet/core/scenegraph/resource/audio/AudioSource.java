package com.rayferric.comet.core.scenegraph.resource.audio;

import com.rayferric.comet.core.audio.recipe.AudioSourceRecipe;
import com.rayferric.comet.core.engine.Engine;

public class AudioSource extends AudioResource {
    public AudioSource() {
        load();
    }

    @Override
    public boolean load() {
        if(!super.load()) return false;

        serverHandle.set(Engine.getInstance().getAudioServer().scheduleResourceCreation(new AudioSourceRecipe()));
        finishLoading();

        return true;
    }
}
