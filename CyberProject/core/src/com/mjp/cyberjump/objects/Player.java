package com.mjp.cyberjump.objects;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.math.*;
import com.mjp.cyberjump.screens.GameScreen;
import com.mjp.cyberjump.utils.Utils;

import java.util.*;

public class Player
{
	private Vector2 position, velocity;
	public Animation<TextureRegion> anim;
	public Animation<TextureRegion> anim1;
	private float mass;
	public float time, timeTmp,  scale;
	public boolean roof;
	public Rectangle collider;
	public int score;
	public static boolean collide;
	Sprite sprite;
	public Player(Texture texture, Texture texture1, int column, int row, int c1, int r1, float animSpeed, float mass)
	{
		score = 0;
		scale = 100;
		TextureRegion[][] temp = new TextureRegion(texture).split(texture.getWidth() / column, texture.getHeight() / row);
		TextureRegion[] frames = new TextureRegion[row * column];
		int index = 0;
		for(int i = 0; i < row; ++i)
			for(int j = 0; j < column; ++j)
				frames[index++] = temp[i][j];
		anim = new Animation<TextureRegion>(animSpeed, frames);
		
		temp = new TextureRegion(texture1).split(texture1.getWidth() / c1, texture1.getHeight() / r1);
		this.sprite = new Sprite(anim.getKeyFrame(0));
		this.sprite.setSize(sprite.getWidth() + (scale - sprite.getWidth()), sprite.getHeight() + (scale - sprite.getHeight()));
		
		frames = new TextureRegion[r1 * c1];
		index = 0;
		for(int i = 0; i < r1; ++i)
			for(int j = 0; j < c1; ++j)
				frames[index++] = temp[i][j];
		anim1 = new Animation<TextureRegion>(animSpeed, frames);
		
		this.mass = mass;
		velocity = Vector2.Zero;
		position = new Vector2(Utils.screenBounds.x / 2, Utils.screenBounds.y / 2);
		collider = new Rectangle(position.x + (sprite.getWidth() / 2), position.y + (sprite.getHeight() / 2), sprite.getWidth() / 1.75f, sprite.getHeight() / 1.75f);
		time = 0f;
		timeTmp = 0f;
	}

	public Sprite getSprite()
	{
		return sprite;
	}
	
	public boolean collidedWith(ArrayList<Obstacle> list)
	{
		for(int i = 0; i < list.size(); ++i)
		{
			for(int i1 = 0; i1 < list.get(i).getAll().size(); ++i1)
			{
				if(collider.overlaps(list.get(i).getAll().get(i1).getBoundingRectangle()))
				{
					collide = true;
					return true;
				}
			}
		}
		
		if(position.y < 0)
		{
			collide = true;
			return true;
		}
			
		return false;
	}

	public Vector2 getPosition()
	{
		return position;
	}
	
	public void setPosition(Vector2 position)
	{
		this.position = position;
	}

	public Vector2 getVelocity()
	{
		return velocity;
	}
	
	public void setVelocity(Vector2 velocity)
	{
		this.velocity = velocity;
	}
	
	public float getX()
	{
		return position.x;
	}
	
	public float getY()
	{
		return position.y;
	}
	
	public void transform(Vector2 position)
	{
		if(!roof) 
		{
			this.position.add(new Vector2(velocity).scl(Gdx.graphics.getDeltaTime()));
			velocity.y -= (100f * mass) * Gdx.graphics.getDeltaTime();
			velocity.scl(1 - (0.1f * Gdx.graphics.getDeltaTime()));
		}
		
		if(getY() > 0 && getY() + getSprite().getHeight() < Utils.screenBounds.y)
		{
			this.position.y = position.y;
			roof = false;
		}
		else
		{
			if(getY() + getSprite().getHeight() > Utils.screenBounds.y)
			{
				this.position.y = Utils.screenBounds.y - (getSprite().getHeight() + 1);
				velocity.y = 0.5f * -velocity.y;
				roof = true;
			}
		}
		
		collider.setPosition(new Vector2(this.position.x + (sprite.getWidth() / 2 )/ 2, this.position.y + ((sprite.getHeight() / 2) / 2)));
	}
	
	public void addForce(Vector2 velocity)
	{
		this.velocity = velocity;
	}
	
	public void debug(ShapeRenderer sr)
	{
		sr.begin(ShapeRenderer.ShapeType.Line);
		sr.setColor(Color.RED);
		sr.rect(collider.getX(), collider.getY(), collider.getWidth(), collider.getHeight());
		sr.end();
	}
	
	public void draw(SpriteBatch batch)
	{
		timeTmp += Gdx.graphics.getDeltaTime();
		if(GameScreen.dead)
		{
			sprite = new Sprite(anim.getKeyFrame(0.1f));
			sprite.setPosition(getX(), getY());
			sprite.setSize(sprite.getWidth() + (scale - sprite.getWidth()), sprite.getHeight() + (scale - sprite.getHeight()));
			
		}
		else if(GameScreen.gameStart && !collide)
		{
			sprite = new Sprite(anim.getKeyFrame(time));
			sprite.setPosition(getX(), getY());
			sprite.setSize(sprite.getWidth() + (scale - sprite.getWidth()), sprite.getHeight() + (scale - sprite.getHeight()));
		}
		else if(!collide)
		{
			sprite = new Sprite(anim1.getKeyFrame(timeTmp, true));
			sprite.setPosition(getX(), getY());
			sprite.setSize(sprite.getWidth() + (scale - sprite.getWidth()), sprite.getHeight() + (scale - sprite.getHeight()));
		}
		sprite.draw(batch);
	}
}
