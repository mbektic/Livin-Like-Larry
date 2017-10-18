package com.mbektic.adream.states;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mbektic.adream.ADream;
import com.mbektic.adream.sprites.Bird;
import com.mbektic.adream.sprites.Tube;

public class PlayState extends State {
    private static final int TUBE_SPACING = 125;
    private static final int TUBE_COUNT = 4;
    private static final int GROUND_Y_OFFSET = -40;

    private Bird bird;
    private BitmapFont font;
    private int score;
    private int highScore;
    private Preferences pref = Gdx.app.getPreferences("My_state");
    private Texture bg;
    private Texture ground;
    private Vector2 groundPos1, groundPos2;

    private Array<Tube> tubes;
    public PlayState(GameStateManager gsm) {
        super(gsm);
        bird = new Bird(50,300);
        cam.setToOrtho(false, ADream.width/2, ADream.height/2);
        bg = new Texture("menu.png");
        ground = new Texture("ground.png");
        groundPos1 = new Vector2(cam.position.x-cam.viewportWidth/2,GROUND_Y_OFFSET);
        groundPos2 = new Vector2((cam.position.x-cam.viewportWidth/2) + ground.getWidth(), GROUND_Y_OFFSET);
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 60;
        font = generator.generateFont(parameter);
        generator.dispose();
        score = 0;
        highScore = pref.getInteger("highScore",0);

        tubes = new Array<Tube>();

        for(int i = 1; i <= TUBE_COUNT; i++){
            tubes.add(new Tube(i * (TUBE_SPACING + Tube.TUBE_WIDTH) + 125));
        }
    }

    @Override
    protected void handleInput() {
        if(Gdx.input.justTouched()){
            bird.jump();
        }
    }

    @Override
    public void update(float dt) {
        if(GameStateManager.resume) {
            if(dt == 0){
                dt = .00001f;
            }
            handleInput();
            updateGround();
            bird.update(dt);
            cam.position.x = bird.getPos().x + 80;

            for (int i = 0; i < tubes.size; i++) {
                Tube tube = tubes.get(i);
                if (cam.position.x - (cam.viewportWidth / 2) > tube.getPosTopTube().x + tube.getTopTube().getWidth()) {
                    tube.reposition(tube.getPosTopTube().x + ((Tube.TUBE_WIDTH + TUBE_SPACING) * TUBE_COUNT));
                    score++;
                }

                if (tube.collides(bird.getBounds())) {
                    if (score > highScore) {
                        highScore = score;
                        pref.putInteger("highScore", highScore);
                        pref.flush();
                    }
                    gsm.set(new LossState(gsm, bird, tubes, font, ground, groundPos1, groundPos2, score, highScore, cam));
                }
            }

            if (bird.getPos().y <= ground.getHeight() + GROUND_Y_OFFSET) {
                gsm.set(new LossState(gsm, bird, tubes, font, ground, groundPos1, groundPos2, score, highScore, cam));
                if (score > highScore) {
                    highScore = score;
                    pref.putInteger("highScore", highScore);
                    pref.flush();
                }
            }
            cam.update();
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(bg,cam.position.x - (cam.viewportWidth/2), 0 ,cam.viewportWidth,cam.viewportHeight);
        sb.draw(bird.getTexture(), bird.getPos().x,bird.getPos().y);
        for(Tube tube: tubes){
            sb.draw(tube.getTopTube(), tube.getPosTopTube().x, tube.getPosTopTube().y);
            sb.draw(tube.getBotTube(), tube.getPosBotTube().x, tube.getPosBotTube().y);
        }
        sb.draw(ground,groundPos1.x,groundPos1.y);
        sb.draw(ground,groundPos2.x,groundPos2.y);
        font.setUseIntegerPositions(false);
        font.getData().setScale(.75f);
        java.lang.String s;
        if((""+score).length() == 1)
            s = "0"+score;
        else
            s = ""+score;
        font.draw(sb, s, cam.position.x - 20,cam.viewportHeight);
        sb.end();
    }

    @Override
    public void dispose() {

    }

    private void updateGround(){
        if(cam.position.x - (cam.viewportWidth/2) > groundPos1.x + ground.getWidth()){
            groundPos1.add(ground.getWidth()*2,0);
        }
        if(cam.position.x - (cam.viewportWidth/2) > groundPos2.x + ground.getWidth()){
            groundPos2.add(ground.getWidth()*2,0);
        }
    }
}
