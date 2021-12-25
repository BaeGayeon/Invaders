package screen;

import java.awt.event.KeyEvent;

import engine.MusicManager;


public class HelpScreen extends Screen{

    /**
     * Constructor, establishes the properties of the screen.
     *
     * @param width
     *            Screen width.
     * @param height
     *            Screen height.
     * @param fps
     *            Frames per second, frame rate at which the game is run.
     */
    public HelpScreen (final int width, final int height, final int fps){
        super(width, height, fps);

        this.returnCode = 1;
    }

    /**
     * Starts the action.
     *
     * @return Next screen code.
     */
    public final int run() {
        super.run();

        draw();
        return this.returnCode;
    }

    /**
     * Updates the elements on screen and checks for events.
     */
    protected final void update() {
        super.update();
        MusicManager.runMain(MusicManager.BgmType.MainBgm);
        draw();
        if (inputManager.isKeyDown(KeyEvent.VK_SPACE)
                && this.inputDelay.checkFinished())
            this.isRunning = false;
    }

    /**
     * Draws the elements associated with the screen.
     */
    private void draw(){
        drawManager.initDrawing(this);

        drawManager.drawHelp(this);

        drawManager.completeDrawing(this);
    }

}
