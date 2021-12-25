package entity;

import engine.Core;
import engine.DrawManager.SpriteType;

import java.util.logging.Logger;

public class RewardBullet extends Bullet{
    /** Speed of Reward Bullet. */
    private static final int REWARD_BULLET_SPEED = 4;

    private Logger logger;

    public RewardBullet(final int positionX, final int positionY) {
        super(positionX, positionY, REWARD_BULLET_SPEED,positionX);
        this.width = 5*2;
        this.height = 5*2;
        this.spriteType = SpriteType.RewardBullet;
        this.logger = Core.getLogger();
    }

}
