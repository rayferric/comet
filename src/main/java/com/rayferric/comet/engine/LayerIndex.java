package com.rayferric.comet.engine;

import com.rayferric.comet.scenegraph.node.AudioPlayer;
import com.rayferric.comet.scenegraph.node.body.PhysicsBody;
import com.rayferric.comet.scenegraph.node.model.Model;
import com.rayferric.comet.scenegraph.node.Node;
import com.rayferric.comet.scenegraph.node.camera.Camera;
import com.rayferric.comet.scenegraph.node.light.DirectionalLight;
import com.rayferric.comet.scenegraph.node.light.PointLight;
import com.rayferric.comet.scenegraph.node.light.SpotLight;

import java.util.ArrayList;
import java.util.List;

/**
 * Layer index is a read-only structure that holds lists of different types of nodes present in a layer.<br>
 * Layer indexes are regenerated by the engine every so often and supply required node tree information to the servers.<br>
 * Layer index may be forcefully regenerated by calling {@link Layer#genIndex()}
 */
public class LayerIndex {
    /**
     * Creates a read only layer index.
     */
    public LayerIndex(Node root) {
        root.indexAll(this);
    }

    public List<Model> getModels() {
        return models;
    }

    public List<Camera> getCameras() {
        return cameras;
    }

    public List<DirectionalLight> getDirectionalLights() {
        return directionalLights;
    }

    public List<PointLight> getPointLights() {
        return pointLights;
    }

    public List<SpotLight> getSpotLights() {
        return spotLights;
    }

    public List<AudioPlayer> getAudioSources() {
        return audioPlayers;
    }

    public List<PhysicsBody> getPhysicsBodies() {
        return physicsBodies;
    }

    /**
     * Adds a {@link Model} to the index.<br>
     * • Is implicitly called by the constructor when traversing the node tree.<br>
     * • Must not be called by the user, this is an internal method.<br>
     * • May be called from any thread.
     *
     * @param model the {@link Model} to be indexed
     */
    public void add(Model model) {
        models.add(model);
    }

    /**
     * Adds a {@link Camera} to the index.<br>
     * • Is implicitly called by the constructor when traversing the node tree.<br>
     * • Must not be called by the user, this is an internal method.<br>
     * • May be called from any thread.
     *
     * @param camera the {@link Camera} to be indexed
     */
    public void add(Camera camera) {
        cameras.add(camera);
    }

    /**
     * Adds a {@link DirectionalLight} to the index.<br>
     * • Is implicitly called by the constructor when traversing the node tree.<br>
     * • Must not be called by the user, this is an internal method.<br>
     * • May be called from any thread.
     *
     * @param light the {@link DirectionalLight} to be indexed
     */
    public void add(DirectionalLight light) {
        directionalLights.add(light);
    }

    /**
     * Adds a {@link PointLight} to the index.<br>
     * • Is implicitly called by the constructor when traversing the node tree.<br>
     * • Must not be called by the user, this is an internal method.<br>
     * • May be called from any thread.
     *
     * @param light the {@link PointLight} to be indexed
     */
    public void add(PointLight light) {
        pointLights.add(light);
    }

    /**
     * Adds a {@link SpotLight} to the index.<br>
     * • Is implicitly called by the constructor when traversing the node tree.<br>
     * • Must not be called by the user, this is an internal method.<br>
     * • May be called from any thread.
     *
     * @param light the {@link SpotLight} to be indexed
     */
    public void add(SpotLight light) {
        spotLights.add(light);
    }

    /**
     * Adds a {@link AudioPlayer} to the index.<br>
     * • Is implicitly called by the constructor when traversing the node tree.<br>
     * • Must not be called by the user, this is an internal method.<br>
     * • May be called from any thread.
     *
     * @param player the {@link AudioPlayer} to be indexed
     */
    public void add(AudioPlayer player) {
        audioPlayers.add(player);
    }

    /**
     * Adds a {@link PhysicsBody} to the index.<br>
     * • Is implicitly called by the constructor when traversing the node tree.<br>
     * • Must not be called by the user, this is an internal method.<br>
     * • May be called from any thread.
     *
     * @param body the {@link PhysicsBody} to be indexed
     */
    public void add(PhysicsBody body) {
        physicsBodies.add(body);
    }

    private final List<Model> models = new ArrayList<>();
    private final List<Camera> cameras = new ArrayList<>();
    private final List<DirectionalLight> directionalLights = new ArrayList<>();
    private final List<PointLight> pointLights = new ArrayList<>();
    private final List<SpotLight> spotLights = new ArrayList<>();
    private final List<AudioPlayer> audioPlayers = new ArrayList<>();
    private final List<PhysicsBody> physicsBodies = new ArrayList<>();
}
