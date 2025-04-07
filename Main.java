package PaooGame;
import javax.swing.*;
public class Main
{
    public static void main(String[] args)
    {
        JFrame GameWindow=new JFrame();
        GameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GameWindow.setResizable(false);
        GameWindow.setTitle("Game");
        Game g=new Game();
        SetBackground sb=new SetBackground(g);
        // seteaza culoarea de fundal la negru
        // Color.black este o constanta din biblioteca Java AWT
        SetDoubleBuffered sdb=new SetDoubleBuffered(g);
        // Scopul este de a imbunatați performanța de redare desenand grafice intr-un buffer inainte de a le afișa pe ecran
        SetFocusable sf=new SetFocusable(g);
        // Daca o componenta este focusable poate raspunde la evenimentele de la tastatura
        SetPreferredSize sps=new SetPreferredSize(g);
        // Seteaza dimensiunea preferata a componentei la 1080 pixeli lațime si 720 pixeli inalțime.
        sb.decorate();
        sdb.decorate();
        sf.decorate();
        sps.decorate();
        GameWindow.add(g);
        // Aici g devine o parte a panoului de conținut al ferestrei si va fi afisat
        GameWindow.pack();
        // Acest lucru asigură că toate componentele sunt la dimensiunile lor preferate
        GameWindow.setLocationRelativeTo(null);
        // Seteaza locatia GameWindow in raport cu componenta specificata, in acest caz o sa fie centrată pe ecran
        GameWindow.setVisible(true);
        // Este ultimul pas in configurarea GUI facand totul vizibil dupa configurare
        g.StartGame();
    }
}
