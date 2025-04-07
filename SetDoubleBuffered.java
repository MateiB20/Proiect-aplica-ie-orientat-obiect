package PaooGame;

import java.awt.*;

public class SetDoubleBuffered  extends GameDecorator{
    public SetDoubleBuffered(Game game) {
        super(game);
    }
    public void decorate()
    {
        game.setDoubleBuffered(true);
    };
}
