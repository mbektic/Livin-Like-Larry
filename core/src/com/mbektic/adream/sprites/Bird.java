package com.mbektic.adream.sprites;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class Bird {
    private static final int GRAVITY = -15;
    private static final int MOVEMENT = 100;
    private Vector3 pos;
    private Vector3 vel;
    private Rectangle bounds;
    private Animation birdAnimation;
    private  Texture texture;
    private Sound flap;

    public Vector3 getPos() {
        return pos;
    }

    public TextureRegion getTexture() {
        return birdAnimation.getFrame();
    }

    public Bird(int x, int y){
        pos = new Vector3(x,y,0);
        vel = new Vector3(0,0,0);

        texture= new Texture("birdanimation.png");
        birdAnimation = new Animation(new TextureRegion(texture), 4, 0.75f);
        bounds = new Rectangle(x,y + 6,texture.getWidth()/4, texture.getHeight()-4);
        flap = Gdx.audio.newSound(Gdx.files.internal("munch.mp3"));
    }

    public void update(float dt){
        birdAnimation.update(dt);
        if(pos.y > 0)
            vel.add(0,GRAVITY,0);
        vel.scl(dt);
        pos.add(MOVEMENT * dt, vel.y,0);
        if(pos.y < 0 ){
            pos.y = 0;
        }

        vel.scl(1/dt);
        bounds.setPosition(pos.x,pos.y);
    }

    public void jump(){
        vel.y=250;
        flap.play(0.25f);
    }

    public Rectangle getBounds(){
        return bounds;
    }

    public void dispose(){
        texture.dispose();
        flap.dispose();
    }
}
