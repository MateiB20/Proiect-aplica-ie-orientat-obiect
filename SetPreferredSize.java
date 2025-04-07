package PaooGame;

import java.awt.*;

public class SetPreferredSize  extends GameDecorator{
    public SetPreferredSize(Game game) {
        super(game);
    }
    public void decorate()
    {
        game.setPreferredSize(new Dimension(1080, 720));
    };
}