package entity;

import engine.DrawManager.SpriteType;

import java.awt.*;

/**
 * Implements a bullet that moves vertically up or down.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class Bullet extends Entity {

	/**
	 * Speed of the bullet, positive or negative depending on direction -
	 * positive is down.
	 */
	private int speed;
	private int initialPositionX;
	private int initialPositionY;
	private int shootPosition;
	/**
	 * Constructor, establishes the bullet's properties.
	 * 
	 * @param positionX
	 *            Initial position of the bullet in the X axis.
	 * @param positionY
	 *            Initial position of the bullet in the Y axis.
	 * @param speed
	 *            Speed of the bullet, positive or negative depending on
	 *            direction - positive is down.
	 */
	public Bullet(final int positionX, final int positionY, final int speed,final int shooterPositionX) {
		super(positionX, positionY, 3 * 2, 5 * 2, Color.WHITE);
		initialPositionX = positionX;
		initialPositionY = positionY;
		shootPosition = shooterPositionX;
		this.speed = speed;
		setSprite();
	}
	public final void setShooterPositionX(int num) {
		shootPosition = num;
	}
	/**
	 * Sets correct sprite for the bullet, based on speed.
	 */
	public final void setSprite() {
		if (speed < 0)
			this.spriteType = SpriteType.Bullet;
		else
			this.spriteType = SpriteType.EnemyBullet;
	}

	/**
	 * Updates the bullet's position.
	 */
	public final void update() {
		this.positionY += this.speed;
		if(this.speed > 0) {
				if (shootPosition==2) {
					this.positionX -= this.speed / 3;
				}
				else if (shootPosition==3) {
					this.positionX += this.speed / 3;
				}
		}
	}

	/**
	 * Setter of the speed of the bullet.
	 * 
	 * @param speed
	 *            New speed of the bullet.
	 */
	public final void setSpeed(final int speed) {
		this.speed = speed;
	}

	/**
	 * Getter for the speed of the bullet.
	 * 
	 * @return Speed of the bullet.
	 */
	public final int getSpeed() {
		return this.speed;
	}
}
