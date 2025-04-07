package PaooGame;
public class NormalZombieFactory implements ZombieInterface{
    private ZombieFactory z;
    public NormalZombieFactory()
    {
        this.z=new ZombieFactory();
    }
    @Override
    public ZombieFactory desc() {
        return z;
    }
}
