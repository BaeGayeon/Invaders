package entity;

import engine.DrawManager;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import engine.GameSettings;

class EnemyShipFormationTest {

    //SETTINGS_LEVEL_1
    GameSettings gameSettings = new GameSettings(5, 4, 60, 2000);
    EnemyShipFormation enemyShipFormation = new EnemyShipFormation(gameSettings);

    @BeforeEach
    void setUp() {
        System.out.println("=====START======");
    }

    @AfterEach
    void tearDown() {
        System.out.println("=====FINISH=====");
    }

    @Disabled
    @DisplayName("destroy a ship")
    @Test
    void destroy() {
        System.out.println("Destroy one ship from enemyShipFormation");
    }


    @DisplayName("test isEmpty")
    @Test
    void isEmpty() {
        assertTrue(!enemyShipFormation.isEmpty());
    }
}