package com.mbektic.adream.states;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mbektic.adream.ADream;

public class MenuState extends State {
    private Texture background;
    private Texture playBtn;

    private boolean ts, tf;
    float alpha;

    public MenuState(GameStateManager gsm) {
        super(gsm);
        stateName = "menu";
        cam.setToOrtho(false, ADream.width/2, ADream.height/2);
        background = new Texture("menu.png");
        playBtn = new Texture("playbtn.png");

        ts = false;
        tf = false;
        alpha = 0f;
    }

    @Override
    public void handleInput() {
        if(Gdx.input.justTouched()){
            if(!ts){
                ts = true;
            }
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
        if(tf){
            gsm.set(new PlayState(gsm));
        }

        if(ts) {
            alpha += 0.04f;
            if(alpha > 1.0f){
                tf = true;
            }
        }

    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(background,0,0,cam.viewportWidth,cam.viewportHeight);
        sb.draw(playBtn,cam.position.x - cam.viewportWidth/4,cam.position.y,cam.viewportWidth/2,playBtn.getHeight());


        if (ts){
            ShapeRenderer shapeRenderer = new ShapeRenderer();
            shapeRenderer.setProjectionMatrix(cam.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(new Color(0,0,0,alpha));
            shapeRenderer.rect(0, 0, cam.viewportWidth,cam.viewportHeight);
            shapeRenderer.end();
        }

        sb.end();
    }

    @Override
    public void dispose() {
        background.dispose();
        playBtn.dispose();
    }

}
