package PaooGame;

import java.awt.*;

public class SetBackground  extends GameDecorator{
    public SetBackground(Game game) {
        super(game);
    }
    public void decorate()
    {
        game.setBackground(Color.black);
    };
}
