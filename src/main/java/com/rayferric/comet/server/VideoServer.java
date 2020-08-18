package com.rayferric.comet.server;

import com.rayferric.comet.resources.Resource;
import com.rayferric.comet.resources.video.Texture;
import com.rayferric.comet.video.VideoEngine;
import com.rayferric.comet.video.display.Window;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class VideoServer extends Server {
    public VideoServer(Window window, VideoEngine videoEngine) {
        this.videoEngine = videoEngine;

        (thread = new Thread(() -> {
            Window.makeCurrent(window);

            videoEngine.init();

            while(running.get()) {
                videoEngine.draw();
                // Command buffer is flushed and now we process the resources while swap interval passes
                freeEnqueuedResources();
                createEnqueuedResources();
                window.swapBuffers();
            }

            resources.forEach((resource, serverResource) -> resource.free());
            while(freeingQueue.size() > 0)
                freeEnqueuedResources();
            // creationQueue is empty, because thread pool execution could not finish with non-empty creation queue
        })).start();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        if(!super.equals(o)) return false;
        VideoServer other = (VideoServer)o;
        return Objects.equals(videoEngine, other.videoEngine) &&
                Objects.equals(resources, other.resources) &&
                Objects.equals(creationQueue, other.creationQueue) &&
                Objects.equals(freeingQueue, other.freeingQueue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), videoEngine, resources, creationQueue, freeingQueue);
    }

    @Override
    public String toString() {
        return String
                .format("VideoServer{videoEngine=%s, resources=%s, creationQueue=%s, freeingQueue=%s}", videoEngine,
                        resources, creationQueue, freeingQueue);
    }

    // This method waits till the render thread has finished loading the resource
    // This allows the caller to safely delete memory allocated in the recipe like texture data
    public void waitForResource(Resource.InternalRecipe recipe) {
        // Here the thread locks itself and waits for createResorces() to unload the queue and release the semaphore
        // Condition is not usable here as createResources() can theoretically signal it before await
        recipe.getSemaphore().acquireUninterruptibly();
        creationQueue.add(recipe);
        recipe.getSemaphore().acquireUninterruptibly();
        recipe.getSemaphore().release();
    }

    public void freeResource(Resource resource) {
        freeingQueue.add(resources.remove(resource));
    }

    public void reloadResources() {
        // resource.reload() calls both freeResource() and (on another thread, after recipe data realloc) waitForResource()
        resources.forEach((resource, serverResource) -> resource.reload());
    }

    private final VideoEngine videoEngine;
    private final ConcurrentHashMap<Resource, ServerResource> resources = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<Resource.InternalRecipe> creationQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<ServerResource> freeingQueue = new ConcurrentLinkedQueue<>();

    // Is called by the render thread
    private void createEnqueuedResources() {
        Resource.InternalRecipe recipe;
        try {
            recipe = creationQueue.remove();
        } catch(NoSuchElementException e) {
            return;
        }

        ServerResource serverResource;
        if(recipe instanceof Texture.InternalRecipe)
            serverResource = videoEngine.createTexture((Texture.InternalRecipe)recipe);
        else
            throw new IllegalArgumentException(
                    "Enqueued creation of an incompatible type of resource. (This is not a recipe of a video resource.)");

        resources.put(recipe.getResource(), serverResource);

        recipe.getSemaphore().release();
    }

    private void freeEnqueuedResources() {
        ServerResource serverResource;
        try {
            serverResource = freeingQueue.remove();
        } catch(NoSuchElementException e) {
            return;
        }
        serverResource.free();
    }
}
