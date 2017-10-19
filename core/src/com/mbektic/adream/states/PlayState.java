package com.mbektic.adream.states;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mbektic.adream.ADream;
import com.mbektic.adream.sprites.Animation;
import com.mbektic.adream.sprites.Bird;
import com.mbektic.adream.sprites.Tube;

public class PlayState extends State {
    private static final int TUBE_SPACING = 125;
    private static final int TUBE_COUNT = 4;
    private static final int GROUND_Y_OFFSET = -40;

    private Bird bird;
    private BitmapFont font, font2;
    private int score;
    private int highScore;
    private Preferences pref = Gdx.app.getPreferences("My_state");
    private Texture bg;
    private Texture ground;
    private Texture lossTex;
    Texture texture;
    private Animation lllAni;
    private Vector2 groundPos1, groundPos2;
    private boolean loss, tapped;
    private double birdChangeIntro;

    private boolean tf, ts1, ts2;
    private float alpha;
    private double lossOffset;
    private Sound gameOver, scoreSound;

    private Array<Tube> tubes;
    PlayState(GameStateManager gsm) {
        super(gsm);
        stateName = "play";
        bird = new Bird(15,300);
        cam.setToOrtho(false, ADream.width/2, ADream.height/2);
        bg = new Texture("menu.png");
        ground = new Texture("ground.png");
        lossTex = new Texture("lossmenu..png");
        loss = false;
        tapped = false;
        birdChangeIntro = 0.5;
        groundPos1 = new Vector2(cam.position.x-cam.viewportWidth/2,GROUND_Y_OFFSET);
        groundPos2 = new Vector2((cam.position.x-cam.viewportWidth/2) + ground.getWidth(), GROUND_Y_OFFSET);
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 60;
        font = generator.generateFont(parameter);
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 30;
        font2 = generator.generateFont(parameter);
        generator.dispose();
        score = 0;
        highScore = pref.getInteger("highScore",0);

        texture= new Texture("lll.png");
        lllAni = new Animation(new TextureRegion(texture), 5, 0.35f);

        tf = false;
        ts1 = false;
        ts2 = false;
        alpha = 1f;
        lossOffset = -cam.position.y - 75;
        gameOver = Gdx.audio.newSound(Gdx.files.internal("loss.mp3"));
        scoreSound = Gdx.audio.newSound(Gdx.files.internal("score.wav"));

        tubes = new Array<Tube>();

        for(int i = 1; i <= TUBE_COUNT; i++){
            tubes.add(new Tube(i * (TUBE_SPACING + Tube.TUBE_WIDTH) + 125));
        }
    }

    @Override
    protected void handleInput() {
        if(Gdx.input.justTouched() && tf){
            if(loss && !ts1)
                ts2 = true;
            else if(!tapped && !loss){
                tapped = true;
                bird.jump();
                texture.dispose();
            } else
                bird.jump();
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
        if(!loss && tapped) {
            if (GameStateManager.resume) {
                if (dt == 0) {
                    dt = .00001f;
                }
                updateGround();
                bird.update(dt);
                cam.position.x = bird.getPos().x + 105;

                for (int i = 0; i < tubes.size; i++) {
                    Tube tube = tubes.get(i);
                    if (cam.position.x - (cam.viewportWidth / 2) > tube.getPosTopTube().x + tube.getTopTube().getWidth()) {
                        tube.reposition(tube.getPosTopTube().x + ((Tube.TUBE_WIDTH + TUBE_SPACING) * TUBE_COUNT));
                        score++;
                        scoreSound.play(0.25f);
                    }

                    if (tube.collides(bird.getBounds())) {
                        if (score > highScore) {
                            highScore = score;
                            pref.putInteger("highScore", highScore);
                            pref.flush();
                        }
                        loss = true;
                        ts1 = true;
                        gameOver.play(0.5f);
                    }
                }

                if (bird.getPos().y <= ground.getHeight() + GROUND_Y_OFFSET) {
                    if (score > highScore) {
                        highScore = score;
                        pref.putInteger("highScore", highScore);
                        pref.flush();
                    }
                    loss = true;
                    ts1 = true;
                    gameOver.play(0.5f);
                }
                cam.update();
            }
        }
        else if(!tapped){
            lllAni.update(dt);
            bird.updateAnimation(dt);
            bird.updatePosY(birdChangeIntro);
            if(bird.getPos().y > 305 || bird.getPos().y < 295){
                birdChangeIntro *= -1;
            }

            if(!tf) {
                lllAni.update(dt);
                alpha -= 0.04f;
                if(alpha < 0.0f){
                    tf = true;
                }
            }
        }
        if(ts2){
            alpha += 0.04f;
            if(alpha > 1.0f){
                gsm.set(new PlayState(gsm));
            }
        }

        if(ts1){
            lossOffset += 10f;
            if(lossOffset > 0){
                ts1 = false;
            }
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(cam.combined);
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

        if(loss){
            sb.draw(lossTex,cam.position.x - 100 ,cam.position.y - 75 + (float)lossOffset,200,150);
            font2.setColor(Color.BLACK);
            if((""+highScore).length() == 1)
                s = "0"+highScore;
            else
                s = ""+highScore;
            font2.draw(sb,""+s,cam.position.x + 45, cam.position.y +5 + (float)lossOffset);
        }

        if(!tapped){
            sb.draw(lllAni.getFrame(), cam.position.x - 103, 100, 200,80);
        }


        if(!tf || ts2){
            sb.draw(texture,0,0,0,0);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(new Color(0,0,0,alpha));
            shapeRenderer.rect(cam.position.x - cam.viewportWidth/2, cam.position.y - cam.viewportHeight/2, cam.viewportWidth,cam.viewportHeight);

            shapeRenderer.end();
        }
        sb.end();
    }

    @Override
    public void dispose() {
        bg.dispose();
        bird.dispose();
        ground.dispose();
        font.dispose();
        font2.dispose();
        lossTex.dispose();
        gameOver.dispose();
        scoreSound.dispose();
        for(Tube tube: tubes)
            tube.dispose();
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
