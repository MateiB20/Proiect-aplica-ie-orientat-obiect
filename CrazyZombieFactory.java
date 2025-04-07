package PaooGame;
public class CrazyZombieFactory implements ZombieInterface{
    private ZombieFactory z;
    public CrazyZombieFactory(int hp, int speed)
    {
        this.z=new ZombieFactory(hp, speed);
    }
    @Override
    public ZombieFactory desc() {
        return z;
    }
}
