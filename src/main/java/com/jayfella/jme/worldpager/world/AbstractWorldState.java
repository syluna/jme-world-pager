package com.jayfella.jme.worldpager.world;

import com.jayfella.jme.worldpager.core.NoiseEvaluator;
import com.jayfella.jme.worldpager.grid.SceneGrid;
import com.jayfella.jme.worldpager.grid.collision.CollisionGrid;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractWorldState extends BaseAppState implements World {

    private final String name;
    private final int seed;

    private final ExecutorService threadPoolExecutor;

    //private final LayeredNoise layeredNoise;
    private NoiseEvaluator noiseEvaluator;

    private List<SceneGrid> sceneGrids = new ArrayList<>();
    private List<CollisionGrid> collisionGrids = new ArrayList<>();
    private final Vector3f follower = new Vector3f();

    private final Node worldNode;

    private final Map<String, Material> registeredMaterials = new HashMap<>();

    public AbstractWorldState(String worldName, int seed, int nThreads) {
        this.name = worldName;
        this.seed = seed;
        this.worldNode = new Node("World: " + worldName);

        this.threadPoolExecutor = Executors.newFixedThreadPool(nThreads);
        //this.layeredNoise = new LayeredNoise();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getSeed() {
        return seed;
    }

    @Override
    public NoiseEvaluator getWorldNoise() {
        return noiseEvaluator;
    }

    @Override
    public void setWorldNoise(NoiseEvaluator noiseEvaluator) {
        this.noiseEvaluator = noiseEvaluator;
    }

    @Override
    public ExecutorService getThreadPool() {
        return threadPoolExecutor;
    }

    @Override
    public Vector3f getFollower() {
        return follower;
    }

    @Override
    public void setFollower(Vector3f follower) {
        this.follower.set(follower);
    }

    @Override
    public void registerMaterial(String key, Material val) {
        registeredMaterials.put(key, val);
    }

    @Override
    public Material getRegisteredMaterial(String key) {
        return registeredMaterials.get(key);
    }

    @Override
    public List<SceneGrid> getSceneGrids() {
        return sceneGrids;
    }

    @Override
    public SceneGrid getSceneGrid(String name) {
        return sceneGrids.stream()
                .filter(grid -> grid.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void addSceneGrid(SceneGrid pagedGrid) {
        this.sceneGrids.add(pagedGrid);
        this.worldNode.attachChild(pagedGrid.getGridNode());
    }

    @Override
    public List<CollisionGrid> getCollisionGrids() {
        return collisionGrids;
    }

    @Override
    public CollisionGrid getCollisionGrid(String name) {
        return collisionGrids.stream()
                .filter(grid -> grid.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void addCollisionGrid(CollisionGrid collisionGrid) {
        this.collisionGrids.add(collisionGrid);
    }

    public abstract void initializeWorld(Application app);

    @Override
    protected void initialize(Application app) {
        initializeWorld(app);

        sceneGrids.forEach(child -> getStateManager().attach(child));
        collisionGrids.forEach(child -> getStateManager().attach(child));
    }

    @Override protected void cleanup(Application app) {
        threadPoolExecutor.shutdown();
    }

    @Override protected void onEnable() {
        ((SimpleApplication)getApplication()).getRootNode().attachChild(worldNode);
    }

    @Override protected void onDisable() {
        worldNode.removeFromParent();
    }

    @Override
    public void update(float tpf) {
        sceneGrids.forEach(child -> child.setLocation(follower));
        collisionGrids.forEach(child -> child.update(tpf));
    }

    @Override
    public Node getWorldNode() {
        return worldNode;
    }

}
