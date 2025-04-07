package PaooGame;

public class FlyingZombieFactory implements ZombieInterface{
    private ZombieFactory z;
    public FlyingZombieFactory(int hp, int speed, int x)
    {
        this.z=new ZombieFactory(hp, speed, x);
    }
    @Override
    public ZombieFactory desc() {
        return z;
    }
}
