
jme-world-pager
--

An infinite procedural world generator for jmonkeyengine.
https://hub.jmonkeyengine.org/t/jme-world-pager/41881

``` java
package com.jayfella.jme.worldpager;

import com.jayfella.jme.worldpager.world.AbstractWorldState;
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

        // set the sky to a nice blue
        viewPort.setBackgroundColor(new ColorRGBA(0.5f, 0.6f, 0.7f, 1.0f));

        // move about a bit quicker.
        flyCam.setMoveSpeed(100);

        // add some light
        DirectionalLight directionalLight = new DirectionalLight(new Vector3f(-1, -1, -1).normalizeLocal());
        rootNode.addLight(directionalLight);

        rootNode.addLight(new AmbientLight(ColorRGBA.White.mult(0.2f)));

        // create our world.
        String worldName = "TestWorld";
        int seed = 123;
        int nThreads = 3;

        world = new BasicWorldState(worldName, seed, nThreads);
        stateManager.attach(world);


        // an an ocean.
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

```

Youtube Video
-
https://youtu.be/oVbc5aOITmk