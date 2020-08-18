package com.rayferric.comet.server;

import com.rayferric.comet.math.Vector2i;
import com.rayferric.comet.resources.Resource;
import com.rayferric.comet.resources.video.Texture;
import com.rayferric.comet.video.VideoEngine;
import com.rayferric.comet.video.Window;

import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class VideoServer extends Server {
    public VideoServer(Window window, VideoEngine videoEngine) {
        this.videoEngine = videoEngine;

        (thread = new Thread(() -> {
            Window.makeCurrent(window);

            videoEngine.init();

            while(running.get()) {
                Vector2i fbSize = window.getFramebufferSize();
                if(!fbSize.equals(videoEngine.getSize()))
                    videoEngine.resize(fbSize);

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
    public String toString() {
        return String
                .format("VideoServer{videoEngine=%s, resources=%s, creationQueue=%s, freeingQueue=%s}", videoEngine,
                        resources, creationQueue, freeingQueue);
    }

    // This method waits till the render thread has finished loading the resource
    // This allows the caller to safely delete memory allocated in the recipe like texture data
    public void waitForResource(Resource.ServerRecipe recipe) {
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
    private final ConcurrentLinkedQueue<Resource.ServerRecipe> creationQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<ServerResource> freeingQueue = new ConcurrentLinkedQueue<>();

    // Is called by the render thread
    private void createEnqueuedResources() {
        Resource.ServerRecipe recipe;
        try {
            recipe = creationQueue.remove();
        } catch(NoSuchElementException e) {
            return;
        }

        ServerResource serverResource;
        if(recipe instanceof Texture.ServerRecipe)
            serverResource = videoEngine.createTexture((Texture.ServerRecipe)recipe);
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
