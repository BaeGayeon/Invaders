package engine;

import screen.Screen;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.*;


/** 
 * 
 * The method that plays music in general.
 * 
 */
public class MusicManager {

    /** Bgm Types. */
    public static enum BgmType {
        MainBgm,
        Shoot,
        ComeBoss,
        GameBgm,
        ShipExp,
        EnemyExp,
        GetItem
    };

    /** bgm file 인스턴스들을 모아둔 배열 */
    private final static File[] bgms = {
            new File("res/MainBGM.wav"),
            new File("res/Shoot.wav"),
            new File("res/BeginBoss.wav"),
            new File("res/GameBGM.wav"),
            new File("res/ShipExplosion.wav"),
            new File("res/EnemyShipExplosion.wav"),
            new File("res/GetItem.wav")
    };

    private static final int MAIN_BGM_LENGTH = 264000;
    private static final int GAME_BGM_LENGTH = 102000;

    public static Cooldown mainCooldown = Core.getCooldown(MAIN_BGM_LENGTH);
    public static Cooldown gameCooldown = Core.getCooldown(GAME_BGM_LENGTH);

    private static Clip mainClip = makeClip(BgmType.MainBgm);
    private static Clip gameClip = makeClip(BgmType.GameBgm);

    private static boolean isMute = false;

    /**
     * setting이 mute인지 확인
     * @return true if mute else false
     */
    public static boolean getIsMute(){
        return isMute;
    }

    /**
     * 현재 mute 상태를 반대로 변환
     */
    public static void toggleIsMute(){
        isMute = !isMute;
    }

    /**
     * bgms 배열에서 인자로 받은 enum에 해당하는 file을 return
     * @param bgm BgmType이 정의된 enum
     * @return BgmType에 맞는 file
     */
    public static File getBgm(BgmType bgm){
        return bgms[bgm.ordinal()];
    }

    /**
     * bgm을 재생
     * @param bgm BgmType이 정의된 enum
     */
    public static void runBgm(BgmType bgm) {
        if(getIsMute()) return;
        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(getBgm(bgm));
            Clip clip = AudioSystem.getClip();
            clip.open(stream);
            clip.start();
        } catch (Exception e){
            //TODO: handle exception
            e.printStackTrace();
        }
    }

    /**
     * main bgm 혹은 game bgm을 재생
     * @param bgm BgmType이 정의된 enum
     */
    public static void runMain(BgmType bgm) {
        if(getIsMute()){
            stopClip(mainClip);
            return;
        }
        Clip clipOne, clipTwo;
        Cooldown cooldownOne;

        if (bgm == BgmType.MainBgm) {
            clipOne = mainClip;
            clipTwo = gameClip;
            cooldownOne = mainCooldown;
        } else if (bgm == BgmType.GameBgm) {
            clipOne = gameClip;
            clipTwo = mainClip;
            cooldownOne = gameCooldown;
        } else {
            return ;
        }

        if (clipOne.isRunning()) {
            if (cooldownOne.checkFinished()){
                clipOne.loop(-1);
                clipOne.start();
                cooldownOne.reset();
            }
        } else {
            if (clipTwo.isRunning()){
                stopClip(clipTwo);
            }
            clipOne.loop(-1);
            clipOne.start();
            cooldownOne.reset();
        }

    }


    /**
     * 인자로 받은 clip의 재생을 정지
     * @param clip 정지할 clip
     */
    public static void stopClip(Clip clip){
        clip.stop();
        clip.setFramePosition(0);
    }

    /**
     * file을 통해 clip을 생성
     * @param bgm BgmType이 정의된 enum
     * @return 생성된 clip
     */
    private static Clip makeClip(BgmType bgm){
        Clip clip;
        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(getBgm(bgm));
            clip = AudioSystem.getClip();
            clip.open(stream);
            return clip;
        } catch (Exception e){
            //TODO: handle exception
            e.printStackTrace();
            return null;
        }
    }
}
