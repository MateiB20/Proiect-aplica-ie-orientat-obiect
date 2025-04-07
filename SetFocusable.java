package PaooGame;

import java.awt.*;

public class SetFocusable  extends GameDecorator{
    public SetFocusable(Game game) {
        super(game);
    }
    public void decorate()
    {
        game.setFocusable(true);
    };
}
