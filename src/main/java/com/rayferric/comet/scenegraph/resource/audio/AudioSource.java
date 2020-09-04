package com.rayferric.comet.scenegraph.resource.audio;

import com.rayferric.comet.audio.recipe.AudioSourceRecipe;
import com.rayferric.comet.engine.Engine;

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
