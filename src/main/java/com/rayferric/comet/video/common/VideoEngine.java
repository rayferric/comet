package com.rayferric.comet.video.common;

import com.rayferric.comet.resources.Resource;
import com.rayferric.comet.resources.Texture;
import com.rayferric.comet.video.display.Window;

import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public abstract class VideoEngine {
    // This must be called by the render thread
    public VideoEngine(String wndTitle, int wndWidth, int wndHeight) {
        (thread = new Thread(() -> {
            window = new Window(wndTitle, wndWidth, wndHeight);

            while(running.get()) {
                draw();
                // Command buffer is flushed and now we process the resources while swap interval passes
                freeEnqueuedResources();
                createEnqueuedResources();
                window.swapBuffers();
            }

            resources.forEach((resource, internalVideoResource) -> resource.free());
            while(freeingQueue.size() > 0)
                freeEnqueuedResources();
            // creationQueue is empty, because thread pool execution could not finish with non-empty creation queue
        })).start();
    }

    public void stopAndFreeResources() {
        running.set(false);
        // Wait for the render thread to free all resources and then return
        try {
            thread.join();
        } catch(InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // This method waits till the render thread has finished loading the resource
    // This allows the caller to safely delete memory allocated in the recipe like texture data
    public void waitForResource(Resource.InternalRecipe recipe) {
        // Here the thread locks itself and waits for createResorces() to unload the queue and release the semaphore
        // Condition is not usable here as createResources() can theoretically signal it before await
        recipe.semaphore.acquireUninterruptibly();
        creationQueue.add(recipe);
        recipe.semaphore.acquireUninterruptibly();
        recipe.semaphore.release();
    }

    public void freeResource(Resource resource) {
        freeingQueue.add(resources.remove(resource));
    }

    public void reloadResources() {
        // resource.reload() calls both freeResource() and (on another thread, after recipe data realloc) waitForResource()
        resources.forEach((resource, internalVideoResource) -> resource.reload());
    }

    public boolean windowShouldClose() {
        return
    }

    protected abstract void draw();

    protected abstract InternalVideoResource createTexture(Texture.InternalRecipe recipe);

    private final Thread thread;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private AtomicReference<Window> window = new AtomicReference<>();
    private final ConcurrentHashMap<Resource, InternalVideoResource> resources = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<Resource.InternalRecipe> creationQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<InternalVideoResource> freeingQueue = new ConcurrentLinkedQueue<>();

    // Is called by the render thread
    private void createEnqueuedResources() {
        Resource.InternalRecipe recipe;
        try {
            recipe = creationQueue.remove();
        } catch(NoSuchElementException e) {
            return;
        }

        InternalVideoResource internalResource;
        if(recipe instanceof Texture.InternalRecipe)
            internalResource = createTexture((Texture.InternalRecipe)recipe);
        else
            throw new IllegalArgumentException("Enqueued creation of an incompatible type of resource. (This is not a recipe of a video resource.)");

        resources.put(recipe.resource, internalResource);

        recipe.semaphore.release();
    }

    private void freeEnqueuedResources() {
        InternalVideoResource internalResource;
        try {
            internalResource = freeingQueue.remove();
        } catch(NoSuchElementException e) {
            return;
        }
        internalResource.free();
    }
}
