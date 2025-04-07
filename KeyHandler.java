package PaooGame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
// pentru a gestiona evenimentele de intrare de la tastatura
// implementeaza interfața KeyListener
public class KeyHandler implements KeyListener
{
    public static KeyHandler instance=null;
    public boolean anyKey;
    public boolean Pressed1;
    public boolean Pressed2;
    public boolean select;
    public boolean Pressed3;
    public boolean Pressed4;
    public boolean Pressed5;
    public boolean escape;
    public boolean skip;
    public static KeyHandler getInstance()
    {
        if(instance==null)
        {
            instance=new KeyHandler();
        }
        return instance;
    }
    public void reset() {instance=null;}
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyPressed(KeyEvent e)
    {
        anyKey=true;
        int code=e.getKeyCode();
        if(code==KeyEvent.VK_Q)
        {
            Pressed1=true;
        }
        if(code==KeyEvent.VK_W)
        {
            Pressed2=true;
        }
        if(code==KeyEvent.VK_E)
        {
            Pressed3=true;
        }
        if(code==KeyEvent.VK_R)
        {
            Pressed4=true;
        }
        if(code==KeyEvent.VK_T)
        {
            Pressed5=true;
        }
        if(code==KeyEvent.VK_ENTER)
        {
            select=true;
        }
        if(code==KeyEvent.VK_SPACE)
        {
            skip=true;
        }
        if(code==KeyEvent.VK_ESCAPE)
        {
            escape=true;
        }
    }
    @Override
    public void keyReleased(KeyEvent e)
    {
        int code=e.getKeyCode();
        if(code==KeyEvent.VK_Q)
        {
            Pressed1=false;
        }
        if(code==KeyEvent.VK_W)
        {
            Pressed2=false;
        }
        if(code==KeyEvent.VK_E)
        {
            Pressed3=false;
        }
        if(code==KeyEvent.VK_R)
        {
            Pressed4=false;
        }
        if(code==KeyEvent.VK_T)
        {
            Pressed5=false;
        }
        if(code==KeyEvent.VK_ENTER)
        {
            select=false;
        }
        if(code==KeyEvent.VK_SPACE)
        {
            skip=false;
        }
        if(code==KeyEvent.VK_ESCAPE)
        {
            escape=false;
        }
    }
}

