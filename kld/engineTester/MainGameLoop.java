package engineTester;

import	java.util.ArrayList;
import java.util.List;
import java.util.Random;


import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.ModelData;
import models.RawModel;
import models.TexturedModel;
import normalMappingObjConverter.NormalMappedObjLoader;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJFileLoader;
import renderEngine.OBJLoader;


import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.MousePicker;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

public class MainGameLoop {

	public static void main(String[] args) {
		
		DisplayManager.createDisplay();
		
		
		Loader loader = new Loader();
		
		ModelData data = OBJFileLoader.loadOBJ("tree");
		
		//Terrain textures
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("flower"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
		
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
		
		// no more textures
		
	


		RawModel rocks = OBJLoader.loadObjModel("rocks", loader);
		TexturedModel rockTexture = new TexturedModel(rocks, new ModelTexture(loader.loadTexture("rocks")));
		RawModel playerModel = OBJLoader.loadObjModel("player", loader);
		TexturedModel playerTexture = new TexturedModel(playerModel, new ModelTexture(loader.loadTexture("playerTexture")));
		List<Terrain> terrains = new ArrayList<Terrain>();
		
		Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap, "heightmap");
		Terrain terrain2 = new Terrain(-1, -1, loader, texturePack, blendMap, "heightMap");
		terrains.add(terrain);
		terrains.add(terrain2);
		Player player = new Player(playerTexture, new Vector3f(100,5,-150),0,150,0,0.6f);
		Camera camera = new Camera(player);
		
		List<GuiTexture> guiTextures = new ArrayList<GuiTexture>();
		
		
		GuiRenderer guiRenderer = new GuiRenderer(loader);

		TexturedModel staticModel = new TexturedModel(OBJLoader.loadObjModel("tree", loader), new ModelTexture(loader.loadTexture("tree")));
		
		TexturedModel grass = new TexturedModel(OBJLoader.loadObjModel("grassModel", loader),
				new ModelTexture(loader.loadTexture("grassTexture")));
		TexturedModel flower = new TexturedModel(OBJLoader.loadObjModel("grassModel", loader), new ModelTexture(loader.loadTexture("flower")));
		grass.getTexture().setHasTransparency(true);
		ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern"));
		fernTextureAtlas.setNumberOfRows(2);
		grass.getTexture().setUseFakeLighting(true);
		TexturedModel fern = new TexturedModel(OBJLoader.loadObjModel("fern", loader), fernTextureAtlas);
		TexturedModel barrelModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("barrel", loader), new ModelTexture(loader.loadTexture("barrel")));
		barrelModel.getTexture().setShineDamper(10);
		barrelModel.getTexture().setReflectivity(0.5f);
		
				
		fern.getTexture().setHasTransparency(true);
		flower.getTexture().setHasTransparency(true);
		flower.getTexture().setUseFakeLighting(true);
		TexturedModel bobble = new TexturedModel(OBJLoader.loadObjModel("lowPolyTree",loader), new  ModelTexture(loader.loadTexture("lowPolyTree")));
		TexturedModel lamp = new TexturedModel(OBJLoader.loadObjModel("lamp", loader), new ModelTexture(loader.loadTexture("lamp")));
		
		
		List<Entity> entities = new ArrayList<Entity>();
		List<Entity> normalMapEntities = new ArrayList<Entity>();
		normalMapEntities.add(new Entity(barrelModel, new Vector3f(75, 10, -75), 0, 0, 0, 1f));
		
		Random random = new Random(676452);
		for(int i = 0; i < 400; i++){
			if(i % 7 == 0){
				float x = random.nextFloat() * 800 - 400;
				float z = random.nextFloat()* -600;
				float y = terrain.getHeightOfTerrain(x, z);
				entities.add(new Entity(grass, new Vector3f(random.nextFloat()* 400 - 200 ,0,
						random.nextFloat() * -400),0,0,0,1.8f));
				entities.add(new Entity(flower, new Vector3f(random.nextFloat()* 400 - 200 ,0,
						random.nextFloat() * -400),0,0,0,2.3f));
				entities.add(new Entity(fern, random.nextInt(4), new Vector3f(x,y,z),0, random.nextFloat(),
						0,0.9f));
				entities.add(player);
			}
			if(i % 3 == 0){
				entities.add(new Entity(fern, new Vector3f(random.nextFloat()* 400 - 200,0,
						random.nextFloat()* -400),0, random.nextFloat() * 360, 0, 0.3f));
				entities.add(new Entity(bobble, new Vector3f(random.nextFloat()* 800 - 400,0, random.nextFloat() * -600),0,random.nextFloat()*360,0,
						random.nextFloat() * 0.1f + 0.6f));
				entities.add(new Entity(rockTexture, new Vector3f(75,4.6f,-75), 0, 0, 0, 75));
				entities.add(new Entity(staticModel,new Vector3f(random.nextFloat() * 800 - 400,0,
					random.nextFloat() * -600), 0,0,0, random.nextFloat() * 1 + 4));	
			}
		}
		
		
		
		
		List<Light> lights = new ArrayList<Light>();
		lights.add(new Light(new Vector3f(0,1000, -7000),new Vector3f(0.4f,0.4f,0.4f)));
		lights.add(new Light(new Vector3f(185,10,-293), new Vector3f(2,0,0), new Vector3f(1,0.01f,0.002f)));
		lights.add(new Light(new Vector3f(370,17,-300), new Vector3f(0,2,2), new Vector3f(1,0.01f,0.002f)));
		lights.add(new Light(new Vector3f(293,7,-305), new Vector3f(2,2,0), new Vector3f(1,0.01f,0.002f)));
		
		entities.add(new Entity(lamp, new Vector3f(185, -4.7f,-293), 0, 0, 0, 1));
		entities.add(new Entity(lamp, new Vector3f(370,4.2f,-300), 0, 0, 0, 1));
		entities.add(new Entity(lamp, new Vector3f(293,-6.8f, -305), 0, 0, 0, 1));
		
		
	
		
		MasterRenderer renderer = new MasterRenderer(loader);
		
		
		
		
		WaterFrameBuffers buffers = new WaterFrameBuffers();
		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), buffers);
		List<WaterTile> waters = new ArrayList<WaterTile>();
		WaterTile watar = new WaterTile(75, -75, 0);
		waters.add(watar);

		MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrain);
		Entity lampEntity = new Entity(lamp, new Vector3f(293, -6.8f, -305), 0, 0, 0, 1);
		entities.add(lampEntity);
		Light light = new Light(new Vector3f(293,7,-305), new Vector3f(0,2,2), new Vector3f(1,0.01f,0.002f));
		lights.add(light);
		
		while(!Display.isCloseRequested()){
		
			player.move(terrain);
			camera.move();
			picker.update();
			
			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
			
			buffers.bindReflectionFrameBuffer();
			float distance = 2*(camera.getPosition().y -watar.getHeight());
			camera.getPosition().y -= distance;
			camera.invertPitch();
			renderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0, 1, 0, -watar.getHeight()+1f));
			camera.getPosition().y += distance;
			camera.invertPitch();
			
			// render refraction texture
			buffers.bindRefractionFrameBuffer();
			renderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0,-1,0,watar.getHeight()));
			
			//render to screen
			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			buffers.unbindCurrentFrameBuffer();
			
			renderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0, -1, 0, 1000000000));
			waterRenderer.render(waters, camera, light);
			guiRenderer.render(guiTextures);
			DisplayManager.updateDisplay();
		}
		buffers.cleanUp();
		waterShader.cleanUP();
		guiRenderer.cleanUP();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();

	}

}
