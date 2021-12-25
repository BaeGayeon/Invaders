package entity;

import java.awt.Color;
import java.util.Set;

import engine.Cooldown;
import engine.Core;
import engine.DrawManager.SpriteType;
import screen.ShipScreen;

/**
 * Implements a ship, to be controlled by the player.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class Ship extends Entity {

	/** Time between shots. */
	private int shooting_interval = 750;
	/** Speed of the bullets shot by the ship. */
	private int bullet_speed = -6;
	/** Movement of the ship for each unit of time. */
	private int ship_speed = 2;
	/** The number of bullets ship shoots at once. */
	private int num_of_bullets = 1;

	private int shipcode = 1;

	private static final int UNIT_INTERVAL = 150;
	private static final int UNIT_SHIP_SPEED = 1;
	private static final int UNIT_BULLET_SPEED = 2;
	
	/** Minimum time between shots. */
	private Cooldown shootingCooldown;
	/** Time spent inactive between hits. */
	private Cooldown destructionCooldown;

	/**
	 * Constructor, establishes the ship's properties.
	 * 
	 * @param positionX
	 *            Initial position of the ship in the X axis.
	 * @param positionY
	 *            Initial position of the ship in the Y axis.
	 */
	public Ship(final int positionX, final int positionY) {
		super(positionX, positionY, 13 * 2, 8 * 2, Color.GREEN);
		ShipScreen shipscreen = new ShipScreen(width, height, 60);
		this.shipcode = shipscreen.getShipCode();
		if(this.shipcode == 1) {
			this.spriteType = SpriteType.Ship;
		} else if (this.shipcode == 2) {
			this.spriteType = SpriteType.Ship2;
		} else if (this.shipcode == 3) {
			this.spriteType = SpriteType.Ship3;
		}
		
		this.shootingCooldown = Core.getCooldown(shooting_interval);
		this.destructionCooldown = Core.getCooldown(1000);
	}

	/**
	 * Moves the ship speed uni ts right, or until the right screen border is
	 * reached.
	 */
	public final void moveRight() {
		this.positionX += ship_speed;
	}

	/**
	 * Moves the ship speed units left, or until the left screen border is
	 * reached.
	 */
	public final void moveLeft() {
		this.positionX -= ship_speed;
	}

	/**
	 * Shoots a bullet upwards.
	 * 
	 * @param bullets
	 *            List of bullets on screen, to add the new bullet.
	 * @return Checks if the bullet was shot correctly.
	 */
	public final boolean shoot(final Set<Bullet> bullets) {
		if (this.shootingCooldown.checkFinished() && this.num_of_bullets == 1) {
			this.shootingCooldown.reset();
			bullets.add(BulletPool.getBullet(positionX + this.width / 2,
					positionY, bullet_speed,positionX));
			return true;
		}
		else if (this.shootingCooldown.checkFinished() && this.num_of_bullets == 2) {
			this.shootingCooldown.reset();
			bullets.add(BulletPool.getBullet(positionX,
					positionY, bullet_speed,positionX));
			bullets.add(BulletPool.getBullet(positionX + this.width,
					positionY, bullet_speed,positionX));
			return true;
		}
		else if (this.shootingCooldown.checkFinished() && this.num_of_bullets == 3) {
			this.shootingCooldown.reset();
			bullets.add(BulletPool.getBullet(positionX - 10,
					positionY, bullet_speed,positionX));
			bullets.add(BulletPool.getBullet(positionX + this.width / 2,
					positionY, bullet_speed,positionX));
			bullets.add(BulletPool.getBullet(positionX + this.width + 10,
					positionY, bullet_speed,positionX));
			return true;
		}
		return false;
	}

	/**
	 * Updates status of the ship.
	 */
	public final void update() {
		if (!this.destructionCooldown.checkFinished())
			this.spriteType = SpriteType.ShipDestroyed;
		else {
			if(this.shipcode == 1) {
				this.spriteType = SpriteType.Ship;
			} else if (this.shipcode == 2) {
				this.spriteType = SpriteType.Ship2;
			} else if (this.shipcode == 3) {
				this.spriteType = SpriteType.Ship3;
			}
		}
	}

	/**
	 * Switches the ship to its destroyed state.
	 */
	public final void destroy() {
		this.destructionCooldown.reset();
	}

	/**
	 * Checks if the ship is destroyed.
	 * 
	 * @return True if the ship is currently destroyed.
	 */
	public final boolean isDestroyed() {
		return !this.destructionCooldown.checkFinished();
	}

	/**
	 * Getter for the ship's speed.
	 * 
	 * @return Speed of the ship.
	 */
	public final int getSpeed() {
		return ship_speed;
	}

	/**
	 * Getter for the bullets ship shoots at once.
	 * 
	 * @return Bullets ship shoots at once.
	 */
	public final int getShoot() {
		return num_of_bullets;
	}

	/**
	 * Increase ship's speed by unit speed(1).
	 * 
	 */
	public final void increase_Speed() {
		this.ship_speed += UNIT_SHIP_SPEED;
	}

	/**
	 * Decrease ship's speed by unit speed(1).
	 * 
	 */
	public final void decrease_Speed() {
		if(ship_speed > UNIT_SHIP_SPEED) {
			this.ship_speed -= UNIT_SHIP_SPEED;
		}
	}

	/**
	 * Increase ship's bullet speed by unit speed(2).
	 * 
	 */
	public final void increase_BulletSpeed() {
		this.bullet_speed -= UNIT_BULLET_SPEED;
	}

	/**
	 * Decrease ship's bullet speed by unit speed(2).
	 * 
	 */
	public final void decrease_BulletSpeed() {
		if(bullet_speed < -UNIT_BULLET_SPEED) {
			this.bullet_speed += UNIT_BULLET_SPEED;
		}
	}

	/**
	 * Decrease ship's shooting interval by unit time(150)
	 * as a result, ship shoot faster.
	 */
	public final void decrease_Interval() {
		if(shooting_interval > UNIT_INTERVAL) {
			this.shooting_interval -= UNIT_INTERVAL;
		}
		this.shootingCooldown = Core.getCooldown(shooting_interval);
	}

	/**
	 * Increase ship's shooting interval by unit time(150)
	 * as a result, ship shoot slower.
	 * 
	 */
	public final void increase_Interval() {
		this.shooting_interval += UNIT_INTERVAL;
		this.shootingCooldown = Core.getCooldown(shooting_interval);
	}

	/**
	 * Increase the bullets that ship shoots at once.
	 * 
	 */
	public final void increase_Numofbullets() {
		if (num_of_bullets < 3)
			this.num_of_bullets += 1;
	}

	/**
	 * Decrease the bullets that ship shoots at once.
	 * 
	 */
	public final void decrease_Numofbullets() {
		if(num_of_bullets > 1)
			this.num_of_bullets -= 1;
	}

	public final void changeShipcode() {
		this.shipcode = 3;
	}
}
