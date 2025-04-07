package PaooGame;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
public class ZombieFactory {
    boolean gameOver;
    BufferedImage image;
    BufferedImage tileImage1, tileImage2, tileImage3;
    Graphics2D g2d;
    int sprite=0;
    boolean dead=false;
    int x=135*7, y=150+135*0;
    int hp;
    int speed;
    int collision;
    public ZombieFactory()
    {
        hp=1500;
        speed=10;
        try {
            tileImage1 = ImageIO.read(getClass().getResourceAsStream("/textures/zombie1 1.png"));
            tileImage2 = ImageIO.read(getClass().getResourceAsStream("/textures/zombie1 2.png"));
            tileImage3 = ImageIO.read(getClass().getResourceAsStream("/textures/zombie1 3.png"));} catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public ZombieFactory(int hp, int speed)
    {
        this.speed=speed;
        this.hp=hp;
        try {
        tileImage1 = ImageIO.read(getClass().getResourceAsStream("/textures/zombie2 1.png"));
        tileImage2 = ImageIO.read(getClass().getResourceAsStream("/textures/zombie2 2.png"));
        tileImage3 = ImageIO.read(getClass().getResourceAsStream("/textures/zombie2 3.png"));} catch (IOException e) {
        throw new RuntimeException(e);
    }
    }
    public ZombieFactory(int hp, int speed, int x) {
        this.speed = speed;
        this.hp = hp;
        try {
            tileImage1 = ImageIO.read(getClass().getResourceAsStream("/textures/zombie3 1.png"));
            tileImage2 = ImageIO.read(getClass().getResourceAsStream("/textures/zombie3 2.png"));
            tileImage3 = ImageIO.read(getClass().getResourceAsStream("/textures/zombie3 3.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
