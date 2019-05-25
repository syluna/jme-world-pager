package com.jayfella.jme.worldpager;

import com.jayfella.fastnoise.*;
import com.jayfella.jme.worldpager.core.CellSize;
import com.jayfella.jme.worldpager.core.GridSettings;
import com.jayfella.jme.worldpager.core.NoiseEvaluator;
import com.jayfella.jme.worldpager.grid.SceneGrid;
import com.jayfella.jme.worldpager.grid.TerrainGrid;
import com.jayfella.jme.worldpager.world.AbstractWorldState;
import com.jme3.app.Application;
import com.jme3.material.Material;
import com.jme3.material.Materials;
import com.jme3.math.Vector2f;

import java.util.Random;

/**
 * An example of a super-simple world that generates terrain around the camera position.
 */
public class BasicWorldState extends AbstractWorldState {

    public BasicWorldState(String worldName, int seed, int nThreads) {
        super(worldName, seed, nThreads);

        // Create a noise generator that the world will use to generate terrain.
        initWorldNoise();
    }

    private void initWorldNoise() {

        // Create layers of noise decreasing in size.
        // We start with huge continents and work our way down to small details.

        // Note: You don't *need* to use this noise generator.

        LayeredNoise layeredNoise = new LayeredNoise();

        layeredNoise.setHardFloor(true);
        layeredNoise.setHardFloorHeight(20);
        layeredNoise.setHardFloorStrength(0.6f);

        Random random = new Random(getSeed());

        NoiseLayer continents = new NoiseLayer("Continents");
        continents.setSeed(random.nextInt());
        continents.setStrength(200);
        continents.setScale(0.03f, 0.03f);
        continents.setFractalOctaves(1);
        layeredNoise.addLayer(continents);

        NoiseLayer mountains = new NoiseLayer("Mountains", random.nextInt());
        mountains.setNoiseType(FastNoise.NoiseType.PerlinFractal);
        mountains.setStrength(512);
        mountains.setScale(0.25f, 0.25f);
        layeredNoise.addLayer(mountains);
        layeredNoise.addLayerMask(new LayerMask(mountains, continents));

        NoiseLayer hills = new NoiseLayer("Hills", random.nextInt());
        hills.setNoiseType(FastNoise.NoiseType.PerlinFractal);
        hills.setStrength(96);
        hills.setScale(0.07f, 0.07f);
        hills.setFrequency(0.005f);
        hills.setFractalOctaves(1);
        hills.setGradientPerturb(GradientPerturb.Fractal);
        hills.setGradientPerturbAmp(30);
        layeredNoise.addLayer(hills);
        layeredNoise.addLayerMask(new LayerMask(hills, continents));

        NoiseLayer details = new NoiseLayer("Details", random.nextInt());
        details.setNoiseType(FastNoise.NoiseType.PerlinFractal);
        details.setStrength(15);
        details.setScale(1f, 1f);
        details.setFractalOctaves(8);

        layeredNoise.addLayer(details);

        NoiseEvaluator noiseEvaluator = new NoiseEvaluator() {
            @Override
            public float evaluate(Vector2f loc) {
                return layeredNoise.evaluate(loc);
            }
        };

        setWorldNoise(noiseEvaluator);
    }

    @Override
    public void initializeWorld(Application app) {

        // Register our materials.
        // We do it this way so that we can "get" them whenever we like and adjust any settings in a centralized manner.
        // For example we might want to increase fog or some other shader effect on all of our materials that support it.
        registerMaterial("terrain", new Material(app.getAssetManager(), Materials.LIGHTING));

        // the TerrainGrid will call getRegisteredMaterial("terrain") to obtain the material.
        SceneGrid terrainGrid = createTerrainGrid();
        addSceneGrid(terrainGrid);
    }

    private SceneGrid createTerrainGrid() {

        GridSettings gridSettings = new GridSettings();
        gridSettings.setCellSize(CellSize.Size_64); // the size of our grid cells.
        gridSettings.setViewDistance(10);

        return new TerrainGrid(this, gridSettings);
    }

}
