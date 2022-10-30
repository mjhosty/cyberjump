package com.mjp.cyberjump.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mjp.cyberjump.objects.JumpParticle;
import com.mjp.cyberjump.objects.Obstacle;
import com.mjp.cyberjump.objects.Player;
import com.mjp.cyberjump.utils.Utils;

import java.util.*;

public class GameScreen implements Screen
{
	public static boolean gameStart, dead;
	
	public float timer, gTimer = 0;
	
	private SpriteBatch batch;
	private ShapeRenderer sr;
	private OrthographicCamera cam;
	
	private Player player;
	private boolean pressed;
	
	private Sound jSound, gSound, score;
	private Music high_score;
	
	private Vector2 camOr, titlePos, startPos;
	private BitmapFont font, font1, font2;
	private Music music;

	private Preferences pref;
	private boolean passedInRun;
	private GlyphLayout layout;
	private FreeTypeFontGenerator gen;

	private Sprite bg1, bg2, start;

	private Texture title, tube, background,
	jumpAtlas, playerJump, plat;

	private Animation<TextureRegion> titleAnim;

	private ArrayList<Texture> splash, top, bottom;
	private ArrayList<Obstacle> obstacle;
	private ArrayList<Sprite> bg1s, bg2s, platform;
	private ArrayList<JumpParticle> jumps;
	private ArrayList<Sound> jumpingSound;

	@Override
	public void show()
	{
		jumpingSound = new ArrayList<Sound>();
		obstacle = new ArrayList<Obstacle>();
		platform = new ArrayList<Sprite>();
		bg2s = new ArrayList<Sprite>();
		bg1s = new ArrayList<Sprite>();
		top = new ArrayList<Texture>();
		bottom = new ArrayList<Texture>();
		splash = new ArrayList<Texture>();
		jumps = new ArrayList<JumpParticle>();
		batch = new SpriteBatch();
		sr = new ShapeRenderer();
		cam = new OrthographicCamera();
		port = new FitViewport(Utils.screenBounds.x, Utils.screenBounds.y, cam);

		//Textures
		splash.add(new Texture(Utils.texture + "jump-splash.png"));
		splash.add(new Texture(Utils.texture + "jump-splash-1.png"));
		bg1 = new Sprite(new Texture(Utils.texture + "buildings-bg.png"));
		plat = new Texture(Utils.texture + "platform.png");
		start = new Sprite(new Texture(Utils.texture + "start.png"));
		jumpAtlas = new Texture(Utils.texture + "jump.png");
		playerJump = new Texture(Utils.texture + "run.png");
		tube = new Texture(Utils.texture + "tube.png");
		top.add(new Texture(Utils.texture + "top.png"));
		bottom.add(new Texture(Utils.texture + "bottom.png"));
		bottom.add(new Texture(Utils.texture + "bottom-1.png"));
		bg2 = new Sprite(new Texture(Utils.texture + "near-buildings-bg.png"));
		title = new Texture(Utils.texture + "title.png");
		background = new Texture(Utils.texture + "skyline-a.png");
		//Sounds
		jumpingSound.add(Gdx.audio.newSound(Gdx.files.internal(Utils.sound +"jumping-1.mp3")));
		jumpingSound.add(Gdx.audio.newSound(Gdx.files.internal(Utils.sound +"jumping-2.mp3")));
		gSound = Gdx.audio.newSound(Gdx.files.internal(Utils.sound +"game_over.mp3"));
		score = Gdx.audio.newSound(Gdx.files.internal(Utils.sound +"score.mp3"));
		jSound = Gdx.audio.newSound(Gdx.files.internal(Utils.sound +"jump.mp3"));
		//Musics
		music = Gdx.audio.newMusic(Gdx.files.internal(Utils.music +"music.mp3"));
		high_score = Gdx.audio.newMusic(Gdx.files.internal(Utils.music +"high_score.mp3"));
		//Fonts
		gen = new FreeTypeFontGenerator(Gdx.files.internal(Utils.font +"gamefont.ttf"));

		layout = new GlyphLayout();

		pref = Gdx.app.getPreferences("game preferences");
		try
		{
			pref.putInteger("high", pref.getInteger("high"));
			//pref.putInteger("high", 3);
			pref.flush();
		} catch(Exception e)
		{
			pref.putInteger("high", 0);
			pref.flush();
		}

		//AnimationTitle
		titleAnim = Utils.getAnimation(title, 1, 9, 0.1f);

		high_score.setVolume(high_score.getVolume() * .5f);
		float dif = Utils.screenBounds.x - bg1.getWidth();
		bg1.setSize(bg1.getWidth() + dif, bg1.getHeight() + dif);

		dif = Utils.screenBounds.x - bg2.getWidth();
		bg2.setSize(bg2.getWidth() + dif, bg2.getHeight() + dif);
		

		FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
		param.size = (int)((Utils.screenBounds.x + Utils.screenBounds.y) / 20);

		start.setSize(start.getWidth() + dif, start.getHeight() + dif / 4);

		font = gen.generateFont(param);
		font.setColor(Color.WHITE);
		font1 = gen.generateFont(param);
		font1.setColor(Color.WHITE);
		param.size = (int)((Utils.screenBounds.x + Utils.screenBounds.y) / 10);
		font2 = gen.generateFont(param);
		font.setColor(Color.WHITE);
		gen.dispose();

		init();
	}
	Viewport port;
	public void init()
	{
		passedInRun = false;
		write = false;
		platform.removeAll(platform);
		obstacle.removeAll(obstacle);
		bg1s.removeAll(bg1s);
		bg2s.removeAll(bg2s);
		shakeCount = 0;
		dead = false;
		bg2s.add(new Sprite(bg2));
		bg1s.add(new Sprite(bg1));
		gameStart = false;
		player = new Player(jumpAtlas, playerJump, 6, 1, 9, 1, 0.1f, 50);
		Player.collide = false;
		first = true;
		timer = 0;
		gTimer = 0;
		turn = false;
		time = 0;
		int top = MathUtils.random(0, this.top.size() - 1);
		int bottom = MathUtils.random(0, this.bottom.size() - 1);
		obstacle.add(new Obstacle(new Sprite(this.top.get(top)), new Sprite(tube), new Sprite(this.bottom.get(bottom)), Utils.screenBounds.y / 2, player));
		
		float dif = (Utils.screenBounds.x - (Utils.screenBounds.x * 0.20f)) - titleAnim.getKeyFrame(time).getRegionWidth();
		titlePos = new Vector2((Utils.screenBounds.x / 2) - ((titleAnim.getKeyFrame(time).getRegionWidth() + dif) / 2), ((Utils.screenBounds.y / 2) + ((Utils.screenBounds.y / 2) / 2)) - ((titleAnim.getKeyFrame(time).getRegionHeight() + dif / 4) / 2));
		startPos = new Vector2(Utils.screenBounds.x / 2 - start.getWidth() / 2, titlePos.y - start.getHeight());
		start.setPosition(startPos.x ,startPos.y);
		
		player.getSprite().setSize(player.getSprite().getWidth() * 2f, player.getSprite().getHeight() * 2f);
		player.transform(new Vector2(Utils.screenBounds.x / 2, Utils.screenBounds.y / 2));
	}
	
	
	float shakeCount;
	boolean write;
	public void update()
	{
		if(!gameStart)
		{
			platform.add(new Sprite(plat));
			platform.get(platform.size() - 1).setSize(100, 100);
			platform.get(platform.size() - 1).setPosition(player.getX(), player.getY() - (platform.get(platform.size() - 1).getHeight() / 12));
			float tmp1 = player.getSprite().getWidth();
			platform.get(platform.size() - 1).setSize(tmp1, tmp1);
			if(platform.size() > 0 && !player.collide)
			{
				for(int i = 0; i < platform.size(); ++i)
				{
					Vector2 tmp = new Vector2(platform.get(i).getX() - (100f * 5) * Gdx.graphics.getDeltaTime(), platform.get(i).getY());
					platform.get(i).setPosition(tmp.x, tmp.y);
					platform.get(i).setAlpha(platform.get(i).getColor().a - 1f * Gdx.graphics.getDeltaTime());
				}
			}
		}
		
		if(!music.isPlaying())
		{
			music.play();
		}
		
		if(player.score > pref.getInteger("high"))
		{
			if(!passedInRun)
			{
				passedInRun = true;
				high_score.play();
			}
			pref.putInteger("high", player.score);
			pref.flush();
		}
		
		if(((Gdx.input.isTouched() || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) && !pressed) && !Player.collide)
		{
			pressed = true;
			if(!gameStart)
			{
				gameStart = true;
			}
			
			
			jumps.add(new JumpParticle(splash.get(MathUtils.random(0, splash.size() - 1)), 1, 9, 100, 100, 0.025f));
			jumps.get(jumps.size() - 1).setPosition(player.getX(), player.getY() - jumps.get(jumps.size() - 1).cFrame.getHeight());
			player.time = 0;
			
			int r = MathUtils.random(0, jumpingSound.size() - 1);
			jumpingSound.get(r).play();
			player.addForce(new Vector2(0, 1000));
			if(0 - player.getSprite().getRotation() > 0) player.getSprite().setRotation(0);
		}
		
		if(!Player.collide)
		{
			fakeParallax(bg1s, 75, bg1);
			fakeParallax(bg2s, 150, bg2);
		}
		
		if(gameStart)
		{
			player.time += Gdx.graphics.getDeltaTime();
			
			if(jumps.size() > 0 && !player.collide)
			{
				for(int i = 0; i < jumps.size(); ++i)
				{
					Vector2 tmp = new Vector2(jumps.get(i).getPosition().x - (100f * 2) * Gdx.graphics.getDeltaTime(), jumps.get(i).getPosition().y);
					jumps.get(i).setPosition(tmp.x, tmp.y);
					//jumps.get(i).setAlpha(platform.get(i).getColor().a - 1f * Gdx.graphics.getDeltaTime());
				}
			}
			
			if(platform.size() > 0 && !player.collide)
			{
				for(int i = 0; i < platform.size(); ++i)
				{
					Vector2 tmp = new Vector2(platform.get(i).getX() - (100f * 2) * Gdx.graphics.getDeltaTime(), platform.get(i).getY());
					platform.get(i).setPosition(tmp.x, tmp.y);
					platform.get(i).setAlpha(platform.get(i).getColor().a - 1f * Gdx.graphics.getDeltaTime());
				}
			}
			
			if(timer < 4f)
			{
				timer += Gdx.graphics.getDeltaTime();
			}
			else
			{
				float randY = MathUtils.random(0 + 250, Utils.screenBounds.y - 500);
				timer = 0;
				int top = MathUtils.random(0, this.top.size() - 1);
				int bottom = MathUtils.random(0, this.bottom.size() - 1);
				obstacle.add(new Obstacle(new Sprite(this.top.get(top)), new Sprite(tube), new Sprite(this.bottom.get(bottom)), randY, player));
			}


			for(int i = 0; i < obstacle.size(); ++i)
			{
				obstacle.get(i).transform(player, score, high_score);
			}

			if(!Gdx.input.isTouched() && pressed)
			{
				pressed = false;
			}

			player.transform(player.getPosition());

			if(obstacle.get(0).getX() < 0 - obstacle.get(0).getWidth() / 2)
			{
				obstacle.remove(0);
			}

			if(player.collidedWith(obstacle) || Player.collide)
			{
				if(cam.position.x == camOr.x && !dead && shakeCount < 3)
					cam.position.set(cam.position.x + 20 , cam.position.y, 0);
				else if(cam.position.x > camOr.x)
				{
					cam.position.set(cam.position.x - 20 , cam.position.y, 0);
					shakeCount++;
				}

				
				if(gTimer <= 0)
				{
					gSound.play();
				}
				
				if(gTimer < 0.2)
				{
					Gdx.input.vibrate(200);
				}
				gTimer += Gdx.graphics.getDeltaTime();

				if(gTimer < 0.5f)
					player.roof = true;
				if(gTimer > 0.5f && gTimer < 0.6f)
				{
					dead = true;
					player.roof = false;
					player.addForce(new Vector2(0, 1000));
				}
				if(gTimer > 2f)
				{
					init();
				}
			}
		} 

		if(platform.size() > 0)
		{
			for(int i = 0; i < platform.size(); ++i)
			{
				if(platform.get(i).getX() < 0 - platform.get(i).getWidth() || platform.get(i).getColor().a < 0.05f)
					platform.remove(i);
			}
		}
		
		for(int i = 0; i < jumps.size(); ++i)
		{
			jumps.get(i).update(jumps);
		}
	}
	
	public void fakeParallax(ArrayList<Sprite> array, float speed, Sprite texture)
	{
		
		if(array.get(0).getX() < 0 && array.size() < 2)
		{
			array.add(new Sprite(texture));
			array.get(array.size() - 1).setPosition(array.get(0).getX() + array.get(0).getWidth(), 0);
		}
		
		for(Sprite s : array)
		{
			s.setPosition(s.getX() - speed * Gdx.graphics.getDeltaTime(), 0);
		}

		if(array.get(0).getWidth() + array.get(0).getX() < 0)
		{
			array.remove(0);
		}
	}
	
	public void paint()
	{
		Gdx.gl.glClearColor(0, 0, 0, 1);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(background, 0, 0, Utils.screenBounds.x, Utils.screenBounds.y);
		batch.draw(background, cam.viewportWidth, 0, Utils.screenBounds.x, Utils.screenBounds.y);
		
		cam.update();
		batch.setProjectionMatrix(cam.combined);
		for(Sprite s : bg1s)
		{
			s.draw(batch);
		}
		
		for(Sprite s : bg2s)
		{
			s.draw(batch);
		}
		
		for(Sprite sprite : platform)
		{
			sprite.draw(batch);
		}
		
		for(int i = 0; i < jumps.size(); i++)
		{
			jumps.get(i).draw(batch);
		}
		
		if(!gameStart)
		{
			time += Gdx.graphics.getDeltaTime();
			float dif = (Utils.screenBounds.x - (Utils.screenBounds.x * 0.20f)) - titleAnim.getKeyFrame(time).getRegionWidth();
			//Vector2 tmp = new Vector2((Utils.screenBounds.x / 2) - ((titleAnim.getKeyFrame(time).getRegionWidth() + dif) / 2), ((Utils.screenBounds.y / 2) + ((Utils.screenBounds.y / 2) / 2)) - ((titleAnim.getKeyFrame(time).getRegionHeight() + dif / 4) / 2));
			batch.draw(titleAnim.getKeyFrame(time, true), titlePos.x, titlePos.y, titleAnim.getKeyFrame(time).getRegionWidth() + dif, titleAnim.getKeyFrame(time).getRegionHeight() + dif / 4);

			if(!turn)
				start.setAlpha(start.getColor().a - Gdx.graphics.getDeltaTime());
			else
				start.setAlpha(start.getColor().a + Gdx.graphics.getDeltaTime());

			if(start.getColor().a < 0.1f)
				turn = true;
			else if(start.getColor().a > 0.9f)
				turn = false;
			
			start.draw(batch);
		}
		else if(gameStart && start.getX() > 0 - start.getWidth())
		{
			time += Gdx.graphics.getDeltaTime();
			float dif = (Utils.screenBounds.x - (Utils.screenBounds.x * 0.20f)) - titleAnim.getKeyFrame(time).getRegionWidth();
			Vector2 speed = new Vector2(-100 * 2, 0);

			if(!Player.collide)
			{
				titlePos.add(new Vector2(speed).scl(Gdx.graphics.getDeltaTime()));
				startPos.add(new Vector2(speed).scl(Gdx.graphics.getDeltaTime()));
			}

			batch.draw(titleAnim.getKeyFrame(time, true), titlePos.x, titlePos.y, titleAnim.getKeyFrame(time).getRegionWidth() + dif, titleAnim.getKeyFrame(time).getRegionHeight() + dif / 4);

			if(!turn)
				start.setAlpha(start.getColor().a - Gdx.graphics.getDeltaTime());
			else
				start.setAlpha(start.getColor().a + Gdx.graphics.getDeltaTime());

			if(start.getColor().a < 0.1f)
				turn = true;
			else if(start.getColor().a > 0.9f)
				turn = false;

			start.setPosition(startPos.x, startPos.y);
			start.draw(batch);
		}
		
		if(dead)
		{
			for(Obstacle o : obstacle)
			{
				o.draw(batch);
			}
			player.draw(batch);
		}
		else
		{
			
			player.draw(batch);
			for(int i = 0; i < obstacle.size(); ++i)
			{
				obstacle.get(i).draw(batch);
			}
		}
		
		
		if(gameStart)
		{
			layout.setText(font, String.format("%d", player.score));
			font.draw(batch, String.format("%d", player.score), 0 + (Utils.screenBounds.x / 12), Utils.screenBounds.y - layout.height);
			layout.setText(font, String.format("%d", pref.getInteger("high")));
			font1.draw(batch, String.format("%d", pref.getInteger("high")), Utils.screenBounds.x - (layout.width + (Utils.screenBounds.x / 12)), Utils.screenBounds.y - layout.height);
		}
		else
		{
			layout.setText(font, String.format("High Score: %d", pref.getInteger("high")));
			font1.draw(batch, String.format("High Score: %d", pref.getInteger("high")), Utils.screenBounds.x - (layout.width + (Utils.screenBounds.x / 12)), Utils.screenBounds.y - layout.height);
		}
		batch.end();

	}
	
	float time;
	boolean turn, first;
	@Override
	public void render(float delta)
	{        
	    update();
		paint();
	}

	@Override
	public void dispose()
	{
		batch.dispose();
		sr.dispose();
		bg1.getTexture().dispose();
		bg2.getTexture().dispose();
		start.getTexture().dispose();
		title.dispose();
		tube.dispose();
		background.dispose();
		jumpAtlas.dispose();
		playerJump.dispose();
		plat.dispose();
		for(Texture t : splash)
			t.dispose();
		for(Texture t : top)
			t.dispose();
		for(Texture t : bottom)
			t.dispose();
		for(Sprite s : bg1s)
			s.getTexture().dispose();
		for(Sprite s : bg2s)
			s.getTexture().dispose();
	}

	@Override
	public void resize(int width, int height) {
		port.update(width, height);
		cam.position.set(Utils.screenBounds.x / 2, Utils.screenBounds.y / 2, cam.position.z);
		camOr = new Vector2(cam.position.x, cam.position.y);
		cam.update();
	}

	@Override
	public void pause()
	{
		
	}

	@Override
	public void resume()
	{
		
	}
	
	@Override
	public void hide()
	{
		dispose();
	}
}
