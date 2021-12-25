package entity;

import engine.DrawManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnemyShipTest {

    EnemyShip testEnemyShipSpcial = new EnemyShip();
    EnemyShip testEnemyShipA1 = new EnemyShip(0, 0, DrawManager.SpriteType.EnemyShipA1);

    @BeforeEach
    void setUp() {
        System.out.println("=====START======");
    }

    @AfterEach
    void tearDown() {
        System.out.println("=====FINISH=====");
    }

    @DisplayName("Check Points of Ship")
    @Test
    void getPointValue() {
        assertSame(testEnemyShipSpcial.getPointValue(), 100);
    }

    @DisplayName("Test animation update")
    @Test
    void update() {
        testEnemyShipA1.update();
        assertSame(testEnemyShipA1.spriteType, DrawManager.SpriteType.EnemyShipA2);
    }

    @DisplayName("Check EnemyShip is destroyed")
    @Test
    void destroy() {
        testEnemyShipSpcial.destroy();
        assertTrue(testEnemyShipSpcial.isDestroyed());
    }


}