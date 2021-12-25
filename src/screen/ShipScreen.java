package screen;

import java.awt.event.KeyEvent;

import engine.Core;
import engine.MusicManager;
import engine.Cooldown;

public class ShipScreen extends Screen {

    /** Milliseconds between changes in user selection. */
	private static final int SELECTION_TIME = 500;
	
	/** Time between changes in user selection. */
	private Cooldown selectionCooldown;

    private static int selectionNum = 1;


    public ShipScreen(final int width, final int height, final int fps) {
        super(width, height, fps);

        this.returnCode = 4;
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

    protected final void update() {
		super.update();
		MusicManager.runMain(MusicManager.BgmType.MainBgm);
        draw();
		if (this.selectionCooldown.checkFinished()
				&& this.inputDelay.checkFinished()) {
			if (inputManager.isKeyDown(KeyEvent.VK_LEFT)
					|| inputManager.isKeyDown(KeyEvent.VK_A)) {
				previousMenuItem();
				this.selectionCooldown.reset();
			}
			if (inputManager.isKeyDown(KeyEvent.VK_RIGHT)
					|| inputManager.isKeyDown(KeyEvent.VK_D)) {
				nextMenuItem();
				this.selectionCooldown.reset();
			}
			if (inputManager.isKeyDown(KeyEvent.VK_SPACE)){
				this.selectionCooldown.reset();
				this.isRunning = false;
			}
		}
    }

    /**
	 * Shifts the focus to the next menu item.
	 */
    private void nextMenuItem() {
		if (this.selectionNum == 2) {
			this.selectionNum = 1;
		}	
		else {
			this.selectionNum++;
		}
	}

	/**
	 * Shifts the focus to the previous menu item.
	 */
	private void previousMenuItem() {
		if (this.selectionNum == 1) {
			this.selectionNum = 2;
		}	
		else {
			this.selectionNum--;
		}
	}

    private void draw() {
		drawManager.initDrawing(this);

		drawManager.drawTitle2(this);
		drawManager.drawshipSelecting(this, this.returnCode);
        drawManager.drawShips(this, this.selectionNum);

		drawManager.completeDrawing(this);
	}

    public int getShipCode() {
        return this.selectionNum;
    }
}
