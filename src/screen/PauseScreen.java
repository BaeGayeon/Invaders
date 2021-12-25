package screen;

import java.awt.event.KeyEvent;

import engine.Core;
import engine.MusicManager;
import engine.Cooldown;

public class PauseScreen extends Screen {

    /** Milliseconds between changes in user selection. */
	private static final int SELECTION_TIME = 200;
	
	/** Time between changes in user selection. */
	private Cooldown selectionCooldown;

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
    public PauseScreen(final int width, final int height, final int fps) {
        super(width, height, fps);
		this.returnCode = 7;
		this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
		this.selectionCooldown.reset();
    }

    /**
	 * Starts the action.
	 * 
	 * @return Next screen code.
	 */
    public final int run() {
		super.run();

		return this.returnCode;
	}

    /**
	 * Updates the elements on screen and checks for events.
	 */
    protected final void update() {
		super.update();
		MusicManager.runMain(MusicManager.BgmType.GameBgm);
		// 화면에서 옵션 선택 메뉴 구현
		draw();

		if (this.selectionCooldown.checkFinished()
				&& this.inputDelay.checkFinished()) {
			if (inputManager.isKeyDown(KeyEvent.VK_UP)
					|| inputManager.isKeyDown(KeyEvent.VK_W)) {
				previousMenuItem();
				this.selectionCooldown.reset();
				}
			if (inputManager.isKeyDown(KeyEvent.VK_DOWN)
					|| inputManager.isKeyDown(KeyEvent.VK_S)) {
				nextMenuItem();
				this.selectionCooldown.reset();
			}
			if (inputManager.isKeyDown(KeyEvent.VK_SPACE)) {
				this.isRunning = false;
				return;
			}
			if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE)){
				this.returnCode = 2;
				this.isRunning = false;

				return;
			}
		}
    }

	/**
	 * Shifts the focus to the next menu item.
	 */
    private void nextMenuItem() {
		 if (this.returnCode == 7) {
			this.returnCode = 1;
		}	
	}

	/**
	 * Shifts the focus to the previous menu item.
	 */
	private void previousMenuItem() {
		if (this.returnCode == 1) {
			this.returnCode = 7;
		}	
	}

    /**
	 * Draws the elements associated with the screen.
	 */
	private void draw() {
		drawManager.initDrawing(this);

		drawManager.drawTitle(this);
		drawManager.drawPause(this, this.returnCode);

		drawManager.completeDrawing(this);
	}
}
