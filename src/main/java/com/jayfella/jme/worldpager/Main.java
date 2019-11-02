package com.jayfella.jme.worldpager;

import com.jayfella.jme.worldpager.world.AbstractWorldState;
import com.jayfella.jme.worldpager.world.WorldSettings;
import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.water.WaterFilter;

public class Main extends SimpleApplication {

    public static void main(String... args) {
        Main main = new Main();
        main.start();
    }

    private AbstractWorldState world;

    @Override
    public void simpleInitApp() {

        // move the camera above the ground
        cam.setLocation(new Vector3f(0, 15, 0));

        // set the sky to a nice blue
        viewPort.setBackgroundColor(new ColorRGBA(0.5f, 0.6f, 0.7f, 1.0f));

        // move about a bit quicker.
        flyCam.setMoveSpeed(100);

        // add some light
        DirectionalLight directionalLight = new DirectionalLight(new Vector3f(-1, -1, -1).normalizeLocal());
        rootNode.addLight(directionalLight);

        rootNode.addLight(new AmbientLight(ColorRGBA.White.mult(0.2f)));

        // create our world.

        WorldSettings worldSettings = new WorldSettings();
        worldSettings.setWorldName("Test World");
        worldSettings.setSeed(123);
        worldSettings.setNumThreads(3);

        world = new DemoWorldState(worldSettings);
        stateManager.attach(world);


        // add an ocean.
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        WaterFilter waterFilter = new WaterFilter(rootNode, directionalLight.getDirection());
        waterFilter.setWaterHeight(8);
        fpp.addFilter(waterFilter);
        viewPort.addProcessor(fpp);
    }

    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);

        world.setFollower(cam.getLocation());
    }

}
