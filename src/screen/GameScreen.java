package screen;

import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import engine.Cooldown;
import engine.Core;
import engine.GameSettings;
import engine.GameState;
import engine.MusicManager;
import entity.*;

/**
 * Implements the game screen, where the action happens.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class GameScreen extends Screen {

	/** Milliseconds until the screen accepts user input. */
	private static final int INPUT_DELAY = 6000;
	/** Bonus score for each life remaining at the end of the level. */
	private static final int LIFE_SCORE = 100;
	/** Minimum time between bonus ship's appearances. */
	private static final int BONUS_SHIP_INTERVAL = 7500;
	/** Maximum variance in the time between bonus ship's appearances. */
	private static final int BONUS_SHIP_VARIANCE = 5000;
	/** Time until bonus ship explosion disappears. */
	private static final int BONUS_SHIP_EXPLOSION = 500;
	/** Time from finishing the level to screen change. */
	private static final int SCREEN_CHANGE_INTERVAL = 1500;
	/** Height of the interface separation line. */
	private static final int SEPARATION_LINE_HEIGHT = 40;
	/** Limit of Math.random()'s number (0~3: reward, 4: fail) **/
	private final int REWARD_WITHOUT_FAIL = 4;
	private final int REWARD_WITH_FAIL = 5;

	/** Difficulty settings for level 1. */
	private static final GameSettings RESTART_SETTING =
			new GameSettings(5, 4, 60, 2000);


	/** Current game difficulty settings. */
	private GameSettings gameSettings;
	/** Current difficulty level number. */
	private int level;
	/** Formation of enemy ships. */
	private EnemyShipFormation enemyShipFormation;
	/** Player's ship. */
	private Ship ship;
	/** Bonus enemy ship that appears sometimes. */
	private EnemyShip enemyShipSpecial;
	/** Minimum time between bonus ship appearances. */
	private Cooldown enemyShipSpecialCooldown;
	/** Time until bonus ship explosion disappears. */
	private Cooldown enemyShipSpecialExplosionCooldown;
	/** Time from finishing the level to screen change. */
	private Cooldown screenFinishedCooldown;
	/** Set of all bullets fired by on screen ships. */
	private Set<Bullet> bullets;
	/** Current score. */
	private int score;
	/** Player lives left. */
	private int lives;
	/** Total bullets shot by the player. */
	private int bulletsShot;
	/** Total ships destroyed by the player. */
	private int shipsDestroyed;
	/** Moment the game starts. */
	private long gameStartTime;
	/** Checks if the level is finished. */
	private boolean levelFinished;
	/** Checks if a bonus life is received. */
	private boolean bonusLife;
	/** Pause Screen */
	private Screen pausescreen;
	/** Check if game is pause */
	private boolean isPause;
	/** Check ESC Cooldown */
	private Cooldown escCooldown;
	/** Check get reward information Cooldown */
	private Cooldown itemCooldown;

	/** Check if resume is printed on log */
    private Boolean resumeLogged;

	private Boolean isGetReward;

	/** Save reward that user get **/
	private static String rewardInfo;

	/**
	 * Constructor, establishes the properties of the screen.
	 * 
	 * @param gameState
	 *            Current game state.
	 * @param gameSettings
	 *            Current game settings.
	 * @param bonusLife
	 *            Checks if a bonus life is awarded this level.
	 * @param width
	 *            Screen width.
	 * @param height
	 *            Screen height.
	 * @param fps
	 *            Frames per second, frame rate at which the game is run.
	 */
	public GameScreen(final GameState gameState,
			final GameSettings gameSettings, final boolean bonusLife,
			final int width, final int height, final int fps, Ship ship) {
		super(width, height, fps);

		this.gameSettings = gameSettings;
		this.bonusLife = bonusLife;
		this.level = gameState.getLevel();
		this.score = gameState.getScore();
		this.lives = gameState.getLivesRemaining();
		if (this.bonusLife)
			this.lives++;
		this.bulletsShot = gameState.getBulletsShot();
		this.shipsDestroyed = gameState.getShipsDestroyed();
		this.isPause = false;
		this.returnCode = 2;
		this.ship = ship;
		this.isGetReward = false;
	}

	/**
	 * Initializes basic screen properties, and adds necessary elements.
	 */
	public final void initialize() {
		super.initialize();

		enemyShipFormation = new EnemyShipFormation(this.gameSettings);
		enemyShipFormation.attach(this);
		// Appears each 10-30 seconds.
		this.enemyShipSpecialCooldown = Core.getVariableCooldown(
				BONUS_SHIP_INTERVAL, BONUS_SHIP_VARIANCE);
		this.enemyShipSpecialCooldown.reset();
		this.enemyShipSpecialExplosionCooldown = Core
				.getCooldown(BONUS_SHIP_EXPLOSION);
		this.screenFinishedCooldown = Core.getCooldown(SCREEN_CHANGE_INTERVAL);
		this.bullets = new HashSet<Bullet>();
		this.escCooldown = Core.getCooldown(500);
		this.escCooldown.reset();
		this.itemCooldown = Core.getCooldown(2000);
        this.resumeLogged = true;
		// Special input delay / countdown.
		this.gameStartTime = System.currentTimeMillis();
		this.inputDelay = Core.getCooldown(INPUT_DELAY);
		this.inputDelay.reset();
	}

	/**
	 * Starts the action.
	 * 
	 * @return Next screen code.
	 */
	public final int run() {
		super.run();

		this.score += LIFE_SCORE * (this.lives - 1);
		this.logger.info("Screen cleared with a score of " + this.score);
		return this.returnCode;
	}

	/**
	 * Updates the elements on screen and checks for events.
	 */
	protected final void update() {
		super.update();
		MusicManager.runMain(MusicManager.BgmType.GameBgm);
		if (this.inputDelay.checkFinished() && !this.levelFinished) {

			if (!this.ship.isDestroyed()) {
				boolean moveRight = inputManager.isKeyDown(KeyEvent.VK_RIGHT)
						|| inputManager.isKeyDown(KeyEvent.VK_D);
				boolean moveLeft = inputManager.isKeyDown(KeyEvent.VK_LEFT)
						|| inputManager.isKeyDown(KeyEvent.VK_A);

				boolean isRightBorder = this.ship.getPositionX()
						+ this.ship.getWidth() + this.ship.getSpeed() > this.width - 1;
				boolean isLeftBorder = this.ship.getPositionX()
						- this.ship.getSpeed() < 1;

				if (moveRight && !isRightBorder) {
					this.ship.moveRight();
				}
				if (moveLeft && !isLeftBorder) {
					this.ship.moveLeft();
				}
				if (inputManager.isKeyDown(KeyEvent.VK_SPACE))
					if (this.ship.shoot(this.bullets)) {
						MusicManager.runBgm(MusicManager.BgmType.Shoot);
						this.bulletsShot++;
					}
				
				if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE)){
					if (isPause == false) {
						if (this.escCooldown.checkFinished()){
							this.escCooldown.reset();
							this.isPause = true;
							this.returnCode = 10;
						}
					}
				}
			}

			if (this.enemyShipSpecial != null) {
				if (!this.enemyShipSpecial.isDestroyed()){
					this.enemyShipSpecial.move(2, 0);
				}
				else if (this.enemyShipSpecialExplosionCooldown.checkFinished()){
					int destroyed_x = enemyShipSpecial.getPositionX() + enemyShipSpecial.getWidth()/2;
					int destroyed_y = enemyShipSpecial.getPositionY();
					this.enemyShipSpecial = null;
					this.logger.info("A reward bullet appears");
					RewardBullet rewardBullet = new RewardBullet(destroyed_x, destroyed_y);
					rewardBullet.setPositionX(destroyed_x - rewardBullet.getWidth() / 2);
					bullets.add(rewardBullet);
				}
			}
			if (this.enemyShipSpecial == null
					&& this.enemyShipSpecialCooldown.checkFinished()) {
				this.enemyShipSpecial = new EnemyShip();
				this.enemyShipSpecialCooldown.reset();
				this.logger.info("A special ship appears");
			}
			if (this.enemyShipSpecial != null
					&& this.enemyShipSpecial.getPositionX() > this.width) {
				this.enemyShipSpecial = null;
				this.logger.info("The special ship has escaped");
			}

			this.ship.update();
			this.enemyShipFormation.update();
			this.enemyShipFormation.shoot(this.bullets);
		}

		manageCollisions();
		cleanBullets();
		draw();

		if ((this.enemyShipFormation.isEmpty() || this.lives == 0)
				&& !this.levelFinished) {
			this.levelFinished = true;
			this.screenFinishedCooldown.reset();
		}

		if (this.levelFinished && this.screenFinishedCooldown.checkFinished()) {
			this.logger.info("Your ship is approved!");
			if(this.level == 3)
				this.getStageReward();
			this.getReward(REWARD_WITHOUT_FAIL);
			this.isRunning = false;
		}
		
		if (this.returnCode == 1) {
			this.logger.info("Go to menu");
			this.isRunning = false;
		}
	}

	/**
	 * Draws the elements associated with the screen.
	 */
	private void draw() {
		if (!isPause) {

			if (!resumeLogged) {
				this.logger.info("Resumed");
				resumeLogged = true;
			}
			drawManager.initDrawing(this);

			drawManager.drawEntity(this.ship, this.ship.getPositionX(),
					this.ship.getPositionY());
			if (this.enemyShipSpecial != null)
				drawManager.drawEntity(this.enemyShipSpecial,
						this.enemyShipSpecial.getPositionX(),
						this.enemyShipSpecial.getPositionY());

			enemyShipFormation.draw();

			for (Bullet bullet : this.bullets)
				drawManager.drawEntity(bullet, bullet.getPositionX(),
						bullet.getPositionY());

			// Interface.
			drawManager.drawScore(this, this.score);
			drawManager.drawLives(this, this.lives);
			drawManager.drawHorizontalLine(this, SEPARATION_LINE_HEIGHT - 1);

			// Draw reward that user got.
			if(!this.itemCooldown.checkFinished() || (!this.inputDelay.checkFinished() && this.level > 1)){
				drawManager.drawGetItem(this, this.rewardInfo);
			}

			// Countdown to game start.
			if (!this.inputDelay.checkFinished()) {
				int countdown = (int) ((INPUT_DELAY
						- (System.currentTimeMillis()
								- this.gameStartTime)) / 1000);
				drawManager.drawCountDown(this, this.level, countdown,
						this.bonusLife);
				drawManager.drawHorizontalLine(this, this.height / 2 - this.height
						/ 12);
				drawManager.drawHorizontalLine(this, this.height / 2 + this.height
						/ 12);
			}
			drawManager.completeDrawing(this);
		}
		else {
			this.pausescreen = new PauseScreen(width, height, fps);
			drawManager.initDrawing(pausescreen);
			
			drawManager.drawTitle(this);
			drawManager.drawPause(this, this.returnCode);
			this.logger.info("Paused");
			this.resumeLogged = false;
			drawManager.completeDrawing(this);
			while(isPause) {
				try {
					Thread.sleep(80);
					this.returnCode = pausescreen.run();

					if (this.returnCode == 1) {
						return;
					}

					else if (this.returnCode == 7) {
						this.level = 1;
						this.score = 0;
						this.lives = 3;
						this.bulletsShot = 0;
						this.shipsDestroyed = 0;
						this.gameSettings = RESTART_SETTING;
						initialize();
						this.logger.info("Restart");
						this.isPause = false;
						this.update();
						this.returnCode = 2;
					}

					else if (this.returnCode == 2){
						this.isPause = false;
						this.escCooldown.reset();
					}
					Thread.sleep(80);
				} catch (InterruptedException e) { }
			}
		}
	}


	/**
	 * Cleans bullets that go off screen.
	 */
	private void cleanBullets() {
		Set<Bullet> recyclable = new HashSet<Bullet>();
		for (Bullet bullet : this.bullets) {
			bullet.update();
			if (bullet.getPositionY() < SEPARATION_LINE_HEIGHT
					|| bullet.getPositionY() > this.height)
				recyclable.add(bullet);
		}
		this.bullets.removeAll(recyclable);
		BulletPool.recycle(recyclable);
	}

	/**
	 * Manages collisions between bullets and ships.
	 */
	private void manageCollisions() {
		Set<Bullet> recyclable = new HashSet<Bullet>();
		for (Bullet bullet : this.bullets)
			if (bullet.getSpeed() > 0) {
				if (checkCollision(bullet, this.ship) && !this.levelFinished) {
					recyclable.add(bullet);
					if (!this.ship.isDestroyed()) {
						if (bullet instanceof RewardBullet){
							this.logger.info("Reward acquire.");
							this.getReward(REWARD_WITH_FAIL);
						}
						else {
							this.ship.destroy();
							MusicManager.runBgm(MusicManager.BgmType.EnemyExp);
							this.lives--;
							this.logger.info("Hit on player ship, " + this.lives
									+ " lives remaining.");
						}
					}
				}
			} else {
				for (EnemyShip enemyShip : this.enemyShipFormation)
					if (!enemyShip.isDestroyed()
							&& checkCollision(bullet, enemyShip)) {
						this.score += enemyShip.getPointValue();
						MusicManager.runBgm(MusicManager.BgmType.ShipExp);
						this.shipsDestroyed++;
						this.enemyShipFormation.destroy(enemyShip);
						recyclable.add(bullet);
					}
				if (this.enemyShipSpecial != null
						&& !this.enemyShipSpecial.isDestroyed()
						&& checkCollision(bullet, this.enemyShipSpecial)) {
					this.score += this.enemyShipSpecial.getPointValue();
					MusicManager.runBgm(MusicManager.BgmType.ShipExp);
					this.shipsDestroyed++;
					this.enemyShipSpecial.destroy();
					this.enemyShipSpecialExplosionCooldown.reset();
					recyclable.add(bullet);
				}
			}
		this.bullets.removeAll(recyclable);
		BulletPool.recycle(recyclable);
	}

	/**
	 * Checks if two entities are colliding.
	 * 
	 * @param a
	 *            First entity, the bullet.
	 * @param b
	 *            Second entity, the ship.
	 * @return Result of the collision test.
	 */
	private boolean checkCollision(final Entity a, final Entity b) {
		// Calculate center point of the entities in both axis.
		int centerAX = a.getPositionX() + a.getWidth() / 2;
		int centerAY = a.getPositionY() + a.getHeight() / 2;
		int centerBX = b.getPositionX() + b.getWidth() / 2;
		int centerBY = b.getPositionY() + b.getHeight() / 2;
		// Calculate maximum distance without collision.
		int maxDistanceX = a.getWidth() / 2 + b.getWidth() / 2;
		int maxDistanceY = a.getHeight() / 2 + b.getHeight() / 2;
		// Calculates distance.
		int distanceX = Math.abs(centerAX - centerBX);
		int distanceY = Math.abs(centerAY - centerBY);

		return distanceX < maxDistanceX && distanceY < maxDistanceY;
	}

	private void getReward(int limit) {
		if (limit == REWARD_WITHOUT_FAIL){
			rewardInfo = "Stage Reward: ";
		} else {
			rewardInfo = "";
		}
		int tmp = (int)(Math.random() * limit);
		String info = "";
		this.logger.info("Get reward...");
		MusicManager.runBgm(MusicManager.BgmType.GetItem);
		if (tmp == 0) {
			ship.increase_Numofbullets();
			info = "Shoot More!";
		} else if (tmp == 1){
			ship.decrease_Interval();
			info = "Shoot Interval Decrease!";
		} else if (tmp == 2){
			ship.increase_BulletSpeed();
			info = "Bullets are going faster!";
		} else if (tmp == 3){
			ship.increase_Speed();
			info = "Move Faster!";
		} else {
			info = "Oops! Not in here!";
		}
		this.logger.info(info);
		this.rewardInfo += info;

		itemCooldown.reset();

	}

	private void getStageReward() {
		ship.changeShipcode();
		ship.update();
	}

	/**
	 * Returns a GameState object representing the status of the game.
	 * 
	 * @return Current game state.
	 */
	public final GameState getGameState() {
		return new GameState(this.level, this.score, this.lives,
				this.bulletsShot, this.shipsDestroyed);
	}
}