package PaooGame;

public abstract class GameDecorator{
    Game game;
    public GameDecorator(Game game) {
        this.game = game;
    }
    public abstract void decorate();
}
