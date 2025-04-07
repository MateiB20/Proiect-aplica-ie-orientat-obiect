package PaooGame;
import java.awt.*;
public class Zombie implements ZombieInterface{
    public ZombieFactory z;
    public Zombie(int x, int y)
    {
        NormalZombieFactory nz=new NormalZombieFactory();
        z=nz.desc();
        this.z.x=x;
        this.z.y=y;
    }
    public Zombie(int x, int y, int hp, int speed)
    {
        CrazyZombieFactory cz=new CrazyZombieFactory(hp, speed);
        z=cz.desc();
        this.z.x=x;
        this.z.y=y;
    }
    public Zombie(int x, int y, int hp, int speed, int i)
    {
        FlyingZombieFactory cz=new FlyingZombieFactory(hp, speed, i);
        z=cz.desc();
        this.z.x=x;
        this.z.y=y;
    }
    public void getImage() {}
    public void draw(int sprite, Graphics2D g2d)
    {
        this.z.g2d=g2d;
        if(sprite%3==0)
        {
            z.image=z.tileImage1;
        }
        else if(sprite%3==1)
        {
            z.image=z.tileImage2;
        }
        else if(sprite%3==2)
        {
            z.image=z.tileImage3;
        }
        if(z.hp>0){TexturePaint tileTexture = new TexturePaint(z.image, new Rectangle(0, 0, z.image.getWidth(), z.image.getHeight()));
        g2d.setPaint(tileTexture);
        if(sprite%z.speed==0)z.x-=130;
        if(z.x<=0){z.gameOver=true;}
            int colIndex = z.x / 130;
            int rowIndex = (z.y - 150) / 130;
            int index = rowIndex * 8 + colIndex;
        z.collision=index;
        g2d.fillRect(z.x, z.y, 130, 130);}
        else{z.dead=true;}
    }
    @Override
    public ZombieFactory desc() {
        return z;
    }
}