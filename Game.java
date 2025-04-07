package PaooGame;
import java.awt.event.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Scanner;
import java.sql.*;
public class Game extends JPanel implements Runnable, MouseListener {
    private int gameState=0;
    private Thread gameThread;
    KeyHandler kh=KeyHandler.getInstance();
    Sound sound=new Sound();
    int[] fr=new int[24];
    int[] placed=new int[24];
    int[] placable=new int[24];
    int[] zombie=new int[50];
    int[] dmg=new int[3];
    boolean fr1;
    boolean fr2;
    boolean fr3;
    boolean fr4;
    boolean fog=true;
    boolean paused=false;
    boolean toggleImage;
    int zombiecount;
    int toggleText;
    int icon1x=0;
    int icon1y=50;
    int command=0;
    int[][] tutorial;
    int[][] altmap;
    int sprite=0;
    int points=0;
    int currency=0;
    boolean completatTutorial=false;
    boolean completat1=false;
    boolean completat2=false;
    boolean music=false;
    Zombie[] zombies = new Zombie[50];
    int id;
    int id2;
    boolean database=true;
    boolean database2=false;
    float alpha=0;
    boolean initial=true;
    int dead=0;
    public Game()
    {
        this.addKeyListener(kh);
        this.addMouseListener( this);
        int m = 4;
        int n = 8;
        tutorial= new int[m][n];
        altmap= new int[m][n];
        try {
            Scanner scanner1 = new Scanner(new File("Tutorial.txt"));
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    if (scanner1.hasNextInt()) {
                        tutorial[i][j] = scanner1.nextInt();
                    }
                }
            }
            Scanner scanner2 = new Scanner(new File("Map.txt"));
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    if (scanner2.hasNextInt()) {
                        tutorial[i][j] = scanner2.nextInt();
                    }
                }
            }
            scanner1.close();
            scanner2.close();
        } catch (FileNotFoundException e) {}


        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:db.db");
            stmt = c.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS GAME " +
                    "(ID INT PRIMARY KEY NOT NULL," +
                    " COMPLETATTUTORIAL TEXT, " +
                    " COMPLETAT1 TEXT NOT NULL, " +
                    " COMPLETAT2 TEXT NOT NULL, " +
                    " ZOMBIECOUNT INT)";
            String sqlCreateTable = "CREATE TABLE IF NOT EXISTS MatrixTable" +
                    "(ID INT PRIMARY KEY NOT NULL," +
                    " Col1 INT, Col2 INT, Col3 INT, Col4 INT, Col5 INT, Col6 INT," +
                    " Col7 INT, Col8 INT, Col9 INT, Col10 INT, Col11 INT, Col12 INT," +
                    " Col13 INT, Col14 INT, Col15 INT, Col16 INT, Col17 INT, Col18 INT," +
                    " Col19 INT, Col20 INT, Col21 INT, Col22 INT, Col23 INT, Col24 INT, level INT, points INT, currency INT, zombiecount INT)";
            stmt.execute(sqlCreateTable);
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        playMenu();
    }

    public synchronized void StartGame() {
        gameThread = new Thread(this);
        //thread se refera la o unitate de procesare/o cale de executie
        gameThread.start();
        //porneste thread-ul nou creat si executa metoda run()
    }
    @Override
    public void run() {
        final double timeFrame      = 100000 / 60.0;
        //timpul pentru a realiza un anumit numar de actualizari pe secunda (60)
        double delta=0;
        double oldTime = System.nanoTime();
        double draw=0;
        double timer=0;
        while (gameThread != null) {
            double currentTime = System.nanoTime();
            delta+=(currentTime-oldTime)/timeFrame;
            timer+=currentTime-oldTime;
            oldTime=currentTime;
            if(delta>=1)
            {
                update();
                repaint();
                delta--;
                draw++;
            }
            if(timer>=100000)
            {
                draw=0;
                timer=0;
            }
        }
    }
    private void update() {
        // metoda conține logica jocului pentru a actualiza starea (input de la tastatură etc.)
        if(gameState==11 && alpha<0.99)alpha+=0.000001;
        if (kh.Pressed1) {
            command=0;
            fr1= true;
            fr2= false;
            fr3=false;
            fr4=false;
        }
        if(kh.Pressed2)
        {
            command=1;
            fr2= true;
            fr1= false;
            fr3=false;
            fr4=false;
        }
        if (kh.Pressed3) {
            command=2;
            fr3= true;
            fr2= false;
            fr1=false;
            fr4=false;
        }
        if(kh.Pressed4)
        {
            command=3;
            fr4= true;
            fr2= false;
            fr3=false;
            fr1=false;
        }
        if(kh.Pressed5)
        {
            command=4;
            fr1=false;
            fr2= false;
            fr3=false;
            fr4=false;
        }
        if(kh.escape)
        {
            paused=!paused;
        }
    }

    public void blocat()throws NivelBlocat
    // NivelBlocat este o clasa de excepție personalizata care extinde clasa Exception
    {
        if(command==1)
        {
            if(completatTutorial==false)
                throw new NivelBlocat("Nivelul 1 este blocat");
        }
        if(command==2)
        {
            if(completat1==false)
                throw new NivelBlocat("Nivelul 2 este blocat");
        }
        if(command==3)
        {
            if(completat2==false)
                throw new NivelBlocat("Nivelul 3 este blocat");
        }
    }
    public void paintComponent(Graphics g) {
        //suprascrie metoda paintComponent pentru a efectua display costum
        if (!paused) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            database2=true;
            if (gameState == 0) {
                BufferedImage tileImage = null;
                try {
                    tileImage = ImageIO.read(getClass().getResourceAsStream("/textures/japanese pattern2.png"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                TexturePaint tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, tileImage.getWidth(), tileImage.getHeight()));
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25F));
                g2d.setPaint(tileTexture);
                g2d.fillRect(0, 0, 1080, 720);
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 50F));
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                String text = "Zombie & Bounty Hunters";
                g2d.setPaint(Color.black);
                g2d.drawString(text, 270 + 8, 360 + 8);
                g2d.setPaint(Color.white);
                g2d.drawString(text, 270, 360);
                animatepress();
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 25F));
                text = (toggleText % 100 >= 0 && toggleText % 100 <= 85) ? "Apasa o tasta..." : "";
                g2d.setPaint(Color.red);
                g2d.drawString(text, 405 + 5, 630 + 5);
                g2d.setPaint(Color.white);
                g2d.drawString(text, 405, 630);
                g2d.dispose();
                if (kh.anyKey) {
                    toggleText = 0;
                    gameState = 1;
                }
            } else if (gameState == 1) {
                BufferedImage tileImage = null;
                try {
                    tileImage = ImageIO.read(getClass().getResourceAsStream("/textures/japanese pattern1.png"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                TexturePaint tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, tileImage.getWidth(), tileImage.getHeight()));
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25F));
                g2d.setPaint(tileTexture);
                g2d.fillRect(0, 0, 1080, 720);
                animatepress();
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 25F));
                String text = (toggleText % 100 >= 0 && toggleText % 100 <= 25) ? "Se incarca" : "";
                g2d.setPaint(Color.white);
                g2d.drawString(text, 405, 630);
                if (toggleText > 115) {
                    gameState = 2;
                }
            } else if (gameState == 2) {
                sound.clip.stop();
                music=false;
                stopMusic();
                if (database) {
                    Connection c = null;
                    Statement stmt = null;
                    try {
                        //incarca driverul SQLite JDBC
                        Class.forName("org.sqlite.JDBC");
                        //stabiliti o conexiune la baza de date
                        c = DriverManager.getConnection("jdbc:sqlite:db.db");
                        //asteapta comanda explicita pentru a "comite" operații
                        c.setAutoCommit(false);
                        // creeaza un obiect pentru a executa queries (interogari)
                        stmt = c.createStatement();
                        ResultSet rs = stmt.executeQuery("SELECT * FROM GAME WHERE id = (SELECT MAX(id) FROM game);");
                        while (rs.next()) {
                            int ID = rs.getInt("ID");
                            System.out.println(id);
                            String ct = rs.getString("COMPLETATTUTORIAL");
                            System.out.println(ct);
                            String c1 = rs.getString("COMPLETAT1");
                            System.out.println(c1);
                            String c2 = rs.getString("COMPLETAT2");
                            System.out.println(c2);
                            int zc = rs.getInt("ZOMBIECOUNT");
                            System.out.println(zc);
                            id = ID;
                            boolean b1, b2, b3;
                            if (ct.equals("true")) b1 = true;
                            else b1 = false;
                            if (c1.equals("true")) b2 = true;
                            else b2 = false;
                            if (c2.equals("true")) b3 = true;
                            else b3 = false;
                            completatTutorial = b1;
                            completat1 = b2;
                            completat2 = b3;
                            zombiecount = zc;
                        }
                        rs.close();
                        stmt.close();
                        c.close();
                    } catch (Exception e) {
                        System.err.println(e.getClass().getName() + ": " + e.getMessage());
                        System.exit(0);
                    }
                    database = false;
                }
                currency = 0;
                for (int i = 0; i < 24; ++i) {
                    fr[i] = placed[i] = 0;
                }
                dmg[0]=dmg[1]=dmg[2]=0;
                dead=0;
                points=0;
                zombies[0] = new Zombie(135 * 7, 150 + 135 * 1);
                zombies[1] = new Zombie(135 * 7, 150 + 135 * 0);
                zombies[2] = new Zombie(135 * 7, 150 + 135 * 2, 3000, 5);

                zombies[3] = new Zombie(135 * 7, 150 + 135 * 1);
                zombies[4] = new Zombie(135 * 7, 150 + 135 * 1);
                zombies[5] = new Zombie(135 * 7, 150 + 135 * 0);
                zombies[6] = new Zombie(135 * 7, 150 + 135 * 2);
                zombies[7] = new Zombie(135 * 7, 150 + 135 * 2);
                zombies[24] = new Zombie(135 * 7, 150 + 135 * 0, 3000, 5);
                zombies[25] = new Zombie(135 * 7, 150 + 135 * 0);

                zombies[8] = new Zombie(135 * 7, 150 + 135 * 2);
                zombies[9] = new Zombie(135 * 7, 150 + 135 * 1, 3000, 5, 1);
                zombies[10] = new Zombie(135 * 7, 150 + 135 * 1, 3000, 5, 1);
                zombies[11] = new Zombie(135 * 7, 150 + 135 * 0, 3000, 5);
                zombies[21] = new Zombie(135 * 7, 150 + 135 * 1, 3000, 5, 1);
                zombies[22] = new Zombie(135 * 7, 150 + 135 * 2, 3000, 5);
                zombies[23] = new Zombie(135 * 7, 150 + 135 * 0, 3000, 5);

                zombies[12] = new Zombie(135 * 7, 150 + 135 * 2);
                zombies[13] = new Zombie(135 * 7, 150 + 135 * 1, 3000, 5, 1);
                zombies[14] = new Zombie(135 * 7, 150 + 135 * 1, 3000, 5, 1);
                zombies[15] = new Zombie(135 * 7, 150 + 135 * 0, 3000, 5);
                zombies[16] = new Zombie(135 * 7, 150 + 135 * 2);
                zombies[17] = new Zombie(135 * 7, 150 + 135 * 2);
                zombies[18] = new Zombie(135 * 7, 150 + 135 * 1, 3000, 5, 1);
                zombies[19] = new Zombie(135 * 7, 150 + 135 * 2, 3000, 5);
                zombies[20] = new Zombie(135 * 7, 150 + 135 * 0, 3000, 5);
                fog = true;
                BufferedImage tileImage = null;
                try {
                    tileImage = ImageIO.read(getClass().getResourceAsStream("/textures/japanese pattern3.png"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                TexturePaint tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, tileImage.getWidth(), tileImage.getHeight()));
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25F));
                g2d.setPaint(tileTexture);
                g2d.fillRect(0, 0, 1080, 720);
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 25F));
                String text = "Prolog";
                g2d.setPaint(Color.black);
                g2d.drawString(text, 275, 365);
                g2d.setPaint(Color.white);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                g2d.drawString(text, 270, 360);

                text = "Misiunea lui Faye";
                g2d.setPaint(Color.black);
                g2d.drawString(text, 275, 395);
                if (completatTutorial == false) g2d.setPaint(Color.gray);
                else g2d.setPaint(Color.white);
                g2d.drawString(text, 270, 390);

                text = "Misiunea lui Jet";
                g2d.setPaint(Color.black);
                g2d.drawString(text, 275, 425);
                if (completat1 == false) g2d.setPaint(Color.gray);
                else g2d.setPaint(Color.white);
                g2d.drawString(text, 270, 420);

                text = "Final";
                g2d.setPaint(Color.black);
                g2d.drawString(text, 275, 455);
                if (completat2 == false) g2d.setPaint(Color.gray);
                else g2d.setPaint(Color.white);
                g2d.drawString(text, 270, 450);

                text = "Iesire";
                g2d.setPaint(Color.black);
                g2d.drawString(text, 275, 485);
                g2d.setPaint(Color.red);
                g2d.drawString(text, 270, 480);
                if(initial==true)
                {
                    command=0;
                    initial=false;
                }
                if (command == 0) {
                    g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 25F));
                    text = ">";
                    g2d.setPaint(Color.white);
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                    g2d.drawString(text, 255, 360);
                }

                if (command == 1) {
                    g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 25F));
                    text = ">";
                    g2d.setPaint(Color.white);
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                    g2d.drawString(text, 255, 390);
                }
                if (command == 2) {
                    g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 25F));
                    text = ">";
                    g2d.setPaint(Color.white);
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                    g2d.drawString(text, 255, 420);
                }
                if (command == 3) {
                    g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 25F));
                    text = ">";
                    g2d.setPaint(Color.white);
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                    g2d.drawString(text, 255, 450);
                }
                if (command == 4) {
                    g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 25F));
                    text = ">";
                    g2d.setPaint(Color.white);
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                    g2d.drawString(text, 255, 480);
                }
                if (zombiecount > 0) {
                    g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 12F));
                    text = "Apasa ESC pentru a sterge progresul";
                    g2d.setPaint(Color.white);
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                    g2d.drawString(text, 255, 255);
                    if (kh.escape) {
                        Connection c = null;
                        Statement stmt = null;
                        try {
                            Class.forName("org.sqlite.JDBC");
                            c = DriverManager.getConnection("jdbc:sqlite:db.db");
                            c.setAutoCommit(false);
                            stmt = c.createStatement();
                            stmt.executeUpdate("DELETE FROM GAME");
                            c.commit();
                            completatTutorial = false;
                            completat1 = false;
                            completat2 = false;
                            zombiecount = 0;
                        } catch (Exception e) {
                            System.err.println(e.getClass().getName() + ": " + e.getMessage());
                            System.exit(0);
                        }
                    }
                }
                try {
                    if (kh.select && command == 0) {
                        blocat();
                        gameState = 3;
                    }
                    if (kh.select && command == 1) {
                        blocat();
                        gameState = 5;
                    }
                    if (kh.select && command == 2) {
                        blocat();
                        gameState = 7;
                    }
                    if (kh.select && command == 3) {
                        blocat();
                        gameState = 9;
                    }
                    if (kh.select && command == 4) {
                        System.exit(0);
                    }
                    g2d.dispose();
                } catch (NivelBlocat e) {
                }
            } else if (gameState == 3) {
                if (music == false) {
                    playTutorial();
                    music = true;
                }
                if (fr1 == true) {
                    g2d.setColor(Color.white);
                } else {
                    g2d.setColor(Color.gray);
                }
                g2d.fillRect(icon1x, icon1y, 50, 50);
                BufferedImage tileImage = null;
                try {
                    tileImage = ImageIO.read(getClass().getResourceAsStream("/textures/Shot_1.png"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                g2d.fillRect(icon1x, icon1y, 50, 50);
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 25F));
                String text = "Puncte:" + points;
                g2d.setPaint(Color.white);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                g2d.drawString(text, 700, 25);
                TexturePaint tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, 50, 50));
                g2d.setPaint(tileTexture);
                g2d.fillRect(icon1x, icon1y, 50, 50);
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 15F));
                text = "250";
                g2d.setPaint(Color.white);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                g2d.drawString(text, 0, 50);
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 25F));
                text = "Arme:" + currency;
                g2d.setPaint(Color.white);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                g2d.drawString(text, 700, 50);
                try {
                    BufferedImage TileImage = ImageIO.read(getClass().getResourceAsStream("/textures/carpet.png"));
                    BufferedImage AltTileImage = ImageIO.read(getClass().getResourceAsStream("/textures/DecorCarpet.png"));
                    for (int x = 0; x < 8; x++) {
                        for (int y = 0; y < 3; y++) {
                            if ((fr[x + y * 8] == 1 && fr1) || placed[x + y * 8] == 1) {
                                if (tutorial[y][x] == 0) {
                                    tileTexture = new TexturePaint(TileImage, new Rectangle(0, 0, TileImage.getWidth(), TileImage.getHeight()));
                                    g2d.setPaint(tileTexture);
                                    g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                    timer.start();
                                    placed[x + y * 8] = 1;
                                    BufferedImage tileImage1 = ImageIO.read(getClass().getResourceAsStream("/textures/Shot_1.png"));
                                    BufferedImage tileImage2 = ImageIO.read(getClass().getResourceAsStream("/textures/Shot_2.png"));
                                    animateshot1();
                                    tileImage = toggleImage ? tileImage1 : tileImage2;
                                    tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, tileImage.getWidth(), tileImage.getHeight()));
                                    g2d.setPaint(tileTexture);
                                    g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                } else {
                                    tileTexture = new TexturePaint(AltTileImage, new Rectangle(0, 0, AltTileImage.getWidth(), AltTileImage.getHeight()));
                                    g2d.setPaint(tileTexture);
                                    g2d.fillRect(x * 135, 150 + y * 135, 130, 100);
                                    timer.start();
                                    placed[x + y * 8] = 1;
                                    BufferedImage tileImage1 = ImageIO.read(getClass().getResourceAsStream("/textures/Shot_1.png"));
                                    BufferedImage tileImage2 = ImageIO.read(getClass().getResourceAsStream("/textures/Shot_2.png"));
                                    animateshot1();
                                    tileImage = toggleImage ? tileImage1 : tileImage2;
                                    tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, tileImage.getWidth(), tileImage.getHeight()));
                                    g2d.setPaint(tileTexture);
                                    g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                }
                            } else {
                                tileImage = ImageIO.read(getClass().getResourceAsStream("/textures/carpet.png"));
                                tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, tileImage.getWidth(), tileImage.getHeight()));
                                g2d.setPaint(tileTexture);
                                g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                            }
                            currency += 1;
                        }
                    }
                    tileImage = ImageIO.read(getClass().getResourceAsStream("/textures/carpet.png"));
                    for (int x = 0; x < 8; ++x) {
                        if (tutorial[3][x] == 1) {
                            tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, tileImage.getWidth(), tileImage.getHeight()));
                            g2d.setPaint(tileTexture);
                            g2d.fillRect(x * 135, 150 + 3 * 135, 130, 130);
                        } else {
                            tileTexture = new TexturePaint(AltTileImage, new Rectangle(0, 0, tileImage.getWidth(), tileImage.getHeight()));
                            g2d.setPaint(tileTexture);
                            g2d.fillRect(x * 135, 150 + 3 * 135, 130, 100);
                        }
                    }
                    if (points == 0) {
                        zombies[0].getImage();
                        animatewalk();
                        zombies[0].z.hp -= dmg[1];
                        zombie[1] = 1;
                        zombies[0].draw(sprite, g2d);
                        if (zombies[0].z.dead == true) {
                            dmg[1] = 0;
                            zombie[1] = 0;
                            ++points;
                            ++zombiecount;
                        }
                        if (zombies[0].z.gameOver == true) {
                            gameState = 12;
                            initial=true;
                        }
                        placed[zombies[0].z.collision] = 0;
                        fr[zombies[0].z.collision] = 0;
                        BufferedImage Image = ImageIO.read(getClass().getResourceAsStream("/textures/Spike.png"));
                        TexturePaint Texture = new TexturePaint(Image, new Rectangle(5, 675, 50, 50));
                        g2d.setPaint(Texture);
                        g2d.fillRect(5, 675, 50, 50);
                        g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 10F));
                        text = "Spike:Apasa pe patratul de sus si apoi pe tile-ul pe care vrei sa pui trupele si omoara zombie-ul. Ai grija te va costa 250 de arme!";
                        g2d.setPaint(Color.white);
                        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                        g2d.drawString(text, 50, 700);
                    } else if (points == 1) {
                        zombies[1].getImage();
                        animatewalk();
                        zombies[1].z.hp -= dmg[0];
                        zombie[0] = 1;
                        zombies[1].draw(sprite, g2d);
                        if (zombies[1].z.dead == true) {
                            dmg[0] = 0;
                            zombie[0] = 0;
                            ++points;
                            ++zombiecount;
                        }
                        if (zombies[1].z.gameOver == true) {
                            gameState = 12;
                            initial=true;
                        }
                        placed[zombies[1].z.collision] = 0;
                        fr[zombies[1].z.collision] = 0;
                        BufferedImage Image = ImageIO.read(getClass().getResourceAsStream("/textures/Jet.png"));
                        TexturePaint Texture = new TexturePaint(Image, new Rectangle(5, 675, 50, 50));
                        g2d.setPaint(Texture);
                        g2d.fillRect(5, 675, 50, 50);
                        g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 10F));
                        text = "Jet:Tine-o tot asa!";
                        g2d.setPaint(Color.white);
                        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                        g2d.drawString(text, 50, 700);
                    } else if (points == 2) {
                        zombies[2].getImage();
                        animatewalk();
                        zombies[2].z.hp -= dmg[2];
                        zombie[2] = 1;
                        zombies[2].draw(sprite, g2d);
                        if (zombies[2].z.dead == true) {
                            dmg[2] = 0;
                            zombie[2] = 0;
                            ++points;
                            ++zombiecount;
                        }
                        if (zombies[2].z.gameOver == true) {
                            gameState = 12;
                            initial=true;
                        }
                        placed[zombies[2].z.collision] = 0;
                        fr[zombies[2].z.collision] = 0;
                        BufferedImage Image = ImageIO.read(getClass().getResourceAsStream("/textures/Faye.png"));
                        TexturePaint Texture = new TexturePaint(Image, new Rectangle(5, 675, 50, 50));
                        g2d.setPaint(Texture);
                        g2d.fillRect(5, 675, 50, 50);
                        g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 10F));
                        text = "???:Aveti grija pe ultimul rand baieti.";
                        g2d.setPaint(Color.white);
                        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                        g2d.drawString(text, 50, 700);
                    } else {
                        completatTutorial = true;

                        Connection c = null;
                        Statement stmt = null;
                        try {
                            Class.forName("org.sqlite.JDBC");
                            c = DriverManager.getConnection("jdbc:sqlite:db.db");
                            c.setAutoCommit(false);
                            stmt = c.createStatement();
                            String str1, str2;
                            if (completat1) str1 = "true";
                            else str1 = "false";
                            if (completat2) str2 = "true";
                            else str2 = "false";
                            String sql = "INSERT INTO GAME (ID, COMPLETATTUTORIAL, COMPLETAT1, COMPLETAT2, ZOMBIECOUNT) " +
                                    "VALUES (" + (++id) + ", 'true', '" + str1 + "', '" + str2 + "', " + zombiecount + " );";
                            stmt.executeUpdate(sql);
                            stmt.close();
                            c.commit();
                            c.close();
                        } catch (Exception e) {
                            System.err.println(e.getClass().getName() + ": " + e.getMessage());
                            System.exit(0);
                        }

                        points = 0;
                        gameState = 4;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (gameState == 4) {
                BufferedImage tileImage = null;
                try {
                    tileImage = ImageIO.read(getClass().getResourceAsStream("/textures/victory.png"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                TexturePaint tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, tileImage.getWidth(), tileImage.getHeight()));
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.06F));
                g2d.setPaint(tileTexture);
                g2d.fillRect(0, 0, 1080, 720);
                BufferedImage Image = null;
                try {
                    Image = ImageIO.read(getClass().getResourceAsStream("/textures/Faye.png"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                TexturePaint Texture = new TexturePaint(Image, new Rectangle(20, 250, 100, 100));
                g2d.setPaint(Texture);
                g2d.fillRect(20, 150, 100, 100);
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 15F));
                String text = "Faye:Cu placere! Eu sunt Faye. Am o idee care v-ar ajuta in urmatoarea voastra lupta.";
                g2d.setPaint(Color.white);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                g2d.drawString(text, 125, 250);
                text = "Ai deblocat fabrica de arme pentru Misiunea lui Faye.";
                g2d.setPaint(Color.white);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                g2d.drawString(text, 125, 400);
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 25F));
                text = "Apasa Space";
                g2d.setPaint(Color.white);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                g2d.drawString(text, 405, 630);
                try {
                    Image = ImageIO.read(getClass().getResourceAsStream("/textures/Spike.png"));
                    Texture = new TexturePaint(Image, new Rectangle(5, 675, 50, 50));
                    g2d.setPaint(Texture);
                    g2d.fillRect(5, 675, 50, 50);
                    g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 10F));
                    text = "Spike:Tine minte sa apesi pe celalalt patrat de sus si apoi pe tile-ul pe care vrei sa o pui.";
                    g2d.setPaint(Color.white);
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                    g2d.drawString(text, 50, 700);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (kh.skip) {
                    gameState = 2;
                }
            } else if (gameState == 5) {
                if (music == false) {
                    playTutorial();
                    music = true;
                }
                if (fr1 == true) {
                    g2d.setColor(Color.white);
                } else {
                    g2d.setColor(Color.gray);
                }
                g2d.fillRect(icon1x, icon1y, 50, 50);
                BufferedImage tileImage = null;
                try {
                    tileImage = ImageIO.read(getClass().getResourceAsStream("/textures/Shot_1.png"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                g2d.fillRect(icon1x, icon1y, 50, 50);
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 25F));
                String text = "Puncte:" + points;
                g2d.setPaint(Color.white);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                g2d.drawString(text, 700, 25);
                TexturePaint tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, 50, 50));
                g2d.setPaint(tileTexture);
                g2d.fillRect(icon1x, icon1y, 50, 50);
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 15F));
                text = "250";
                g2d.setPaint(Color.white);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                g2d.drawString(text, 0, 50);
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 25F));
                text = "Arme:" + currency;
                g2d.setPaint(Color.white);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                g2d.drawString(text, 700, 50);
                if (fr2 == true) {
                    g2d.setColor(Color.white);
                } else {
                    g2d.setColor(Color.gray);
                }
                g2d.fillRect(icon1x + 55, icon1y, 50, 50);
                tileImage = null;
                try {
                    tileImage = ImageIO.read(getClass().getResourceAsStream("/textures/factory.png"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, 50, 50));
                g2d.setPaint(tileTexture);
                g2d.fillRect(icon1x + 55, icon1y, 50, 50);
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 15F));
                text = "100";
                g2d.setPaint(Color.white);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                g2d.drawString(text, 55, 50);
                try {
                    BufferedImage TileImage = ImageIO.read(getClass().getResourceAsStream("/textures/floor.png"));
                    BufferedImage AltTileImage = ImageIO.read(getClass().getResourceAsStream("/textures/dfloor.png"));
                    for (int x = 0; x < 8; x++) {
                        for (int y = 0; y < 3; y++) {
                            if (tutorial[y][x] == 0) {
                                if ((fr[x + y * 8] == 1 && fr1 && placed[x + y * 8] != 2) || placed[x + y * 8] == 1) {
                                    tileTexture = new TexturePaint(TileImage, new Rectangle(0, 0, TileImage.getWidth(), TileImage.getHeight()));
                                    g2d.setPaint(tileTexture);
                                    g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                    timer.start();
                                    placed[x + y * 8] = 1;
                                    BufferedImage tileImage1 = ImageIO.read(getClass().getResourceAsStream("/textures/Shot_1.png"));
                                    BufferedImage tileImage2 = ImageIO.read(getClass().getResourceAsStream("/textures/Shot_2.png"));
                                    animateshot1();
                                    tileImage = toggleImage ? tileImage1 : tileImage2;
                                    tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, tileImage.getWidth(), tileImage.getHeight()));
                                    g2d.setPaint(tileTexture);
                                    g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                }
                                if ((fr[x + y * 8] == 2 && fr2) || placed[x + y * 8] == 2) {
                                    tileTexture = new TexturePaint(TileImage, new Rectangle(0, 0, TileImage.getWidth(), TileImage.getHeight()));
                                    g2d.setPaint(tileTexture);
                                    g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                    currency += 1;
                                    placed[x + y * 8] = 2;
                                    BufferedImage tileImage1 = ImageIO.read(getClass().getResourceAsStream("/textures/factory.png"));
                                    tileTexture = new TexturePaint(tileImage1, new Rectangle(0, 0, tileImage1.getWidth(), tileImage1.getHeight()));
                                    g2d.setPaint(tileTexture);
                                    g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                }
                                if (!(((fr[x + y * 8] == 1 && fr1 && placed[x + y * 8] != 2) || placed[x + y * 8] == 1) || ((fr[x + y * 8] == 2 && fr2) || placed[x + y * 8] == 2))) {
                                    tileImage = ImageIO.read(getClass().getResourceAsStream("/textures/floor.png"));
                                    tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, tileImage.getWidth(), tileImage.getHeight()));
                                    g2d.setPaint(tileTexture);
                                    g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                }
                            } else {
                                if ((fr[x + y * 8] == 1 && fr1 && placed[x + y * 8] != 2) || placed[x + y * 8] == 1) {
                                    tileTexture = new TexturePaint(AltTileImage, new Rectangle(0, 0, AltTileImage.getWidth(), AltTileImage.getHeight()));
                                    g2d.setPaint(tileTexture);
                                    g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                    timer.start();
                                    placed[x + y * 8] = 1;
                                    BufferedImage tileImage1 = ImageIO.read(getClass().getResourceAsStream("/textures/Shot_1.png"));
                                    BufferedImage tileImage2 = ImageIO.read(getClass().getResourceAsStream("/textures/Shot_2.png"));
                                    animateshot1();
                                    tileImage = toggleImage ? tileImage1 : tileImage2;
                                    tileTexture = new TexturePaint(AltTileImage, new Rectangle(0, 0, AltTileImage.getWidth(), AltTileImage.getHeight()));
                                    g2d.setPaint(tileTexture);
                                    g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                }
                                if ((fr[x + y * 8] == 2 && fr2) || placed[x + y * 8] == 2) {
                                    tileTexture = new TexturePaint(AltTileImage, new Rectangle(0, 0, AltTileImage.getWidth(), AltTileImage.getHeight()));
                                    g2d.setPaint(tileTexture);
                                    g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                    currency += 1;
                                    placed[x + y * 8] = 2;
                                    BufferedImage tileImage1 = ImageIO.read(getClass().getResourceAsStream("/textures/factory.png"));
                                    tileTexture = new TexturePaint(tileImage1, new Rectangle(0, 0, tileImage1.getWidth(), tileImage1.getHeight()));
                                    g2d.setPaint(tileTexture);
                                    g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                }
                                if (!(((fr[x + y * 8] == 1 && fr1 && placed[x + y * 8] != 2) || placed[x + y * 8] == 1) || ((fr[x + y * 8] == 2 && fr2) || placed[x + y * 8] == 2))) {

                                    tileImage = ImageIO.read(getClass().getResourceAsStream("/textures/dfloor.png"));
                                    tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, tileImage.getWidth(), tileImage.getHeight()));
                                    g2d.setPaint(tileTexture);
                                    g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                }
                            }
                            currency += 1;
                        }
                    }
                    tileImage = ImageIO.read(getClass().getResourceAsStream("/textures/grass.png"));
                    for (int x = 0; x < 8; ++x) {
                        if (tutorial[3][x] == 1) {
                            tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, tileImage.getWidth(), tileImage.getHeight()));
                            g2d.setPaint(tileTexture);
                            g2d.fillRect(x * 135, 150 + 3 * 135, 130, 130);
                        } else {
                            tileTexture = new TexturePaint(AltTileImage, new Rectangle(0, 0, AltTileImage.getWidth(), AltTileImage.getHeight()));
                            g2d.setPaint(tileTexture);
                            g2d.fillRect(x * 135, 150 + 3 * 135, 130, 100);
                        }
                    }
                    if (points == 0) {
                        zombies[3].getImage();
                        animatewalk();
                        zombies[3].z.hp -= dmg[1];
                        zombie[1] = 1;
                        zombies[3].draw(sprite, g2d);
                        if (zombies[3].z.dead == true) {
                            dmg[1] = 0;
                            zombie[1] = 0;
                            ++points;
                            ++zombiecount;
                        }
                        if (zombies[3].z.gameOver == true) {
                            gameState = 12;
                            initial=true;
                        }
                        placed[zombies[3].z.collision] = 0;
                        fr[zombies[3].z.collision] = 0;
                        BufferedImage Image = ImageIO.read(getClass().getResourceAsStream("/textures/Faye.png"));
                        TexturePaint Texture = new TexturePaint(Image, new Rectangle(5, 675, 50, 50));
                        g2d.setPaint(Texture);
                        g2d.fillRect(5, 675, 50, 50);
                        g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 10F));
                        text = "Faye:Va rog omorati acei zombie cat am ceva treaba prin zona!";
                        g2d.setPaint(Color.white);
                        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                        g2d.drawString(text, 50, 700);
                    } else if (points == 1) {
                        zombies[4].getImage();
                        zombies[5].getImage();
                        animatewalk();
                        zombies[4].z.hp -= dmg[1];
                        zombies[5].z.hp -= dmg[0];
                        zombie[0] = 1;
                        zombie[1] = 1;
                        zombies[4].draw(sprite, g2d);
                        zombies[5].draw(sprite, g2d);
                        if (zombies[4].z.dead == true) {
                            zombie[1] = 0;
                        }
                        if (zombies[5].z.dead == true) {
                            zombie[0] = 0;
                        }
                        if (zombies[4].z.dead == true && zombies[5].z.dead == true) {
                            dmg[1] = 0;
                            dmg[0] = 0;
                            ++points;
                            zombiecount += 2;
                        }
                        if (zombies[4].z.gameOver == true || zombies[5].z.gameOver == true) {
                            gameState = 12;
                            initial=true;
                        }
                        placed[zombies[4].z.collision] = 0;
                        fr[zombies[4].z.collision] = 0;
                        placed[zombies[5].z.collision] = 0;
                        fr[zombies[5].z.collision] = 0;
                    } else if (points == 2) {
                        zombies[6].getImage();
                        zombies[6].z.hp -= dmg[2];
                        zombies[6].draw(sprite, g2d);
                        if (zombies[6].z.dead == false) {
                            zombie[2] = 2;
                            placed[zombies[6].z.collision] = 0;
                            fr[zombies[6].z.collision] = 0;
                        }
                        if (zombies[6].z.dead == true && zombie[2] == 2) {
                            dmg[2] = 0;
                            zombie[2] = 1;
                        }
                        if (zombies[6].z.dead == true && zombies[7].z.dead == true) {
                            dmg[2] = 0;
                            ++points;
                            zombiecount += 2;
                            zombie[2]=0;
                        }
                        if (zombies[6].z.gameOver == true || zombies[7].z.gameOver == true) {
                            gameState = 12;
                            initial=true;
                        }
                        if ((zombies[6].z.collision <= 22 || zombies[6].z.dead == true) && zombies[7].z.dead==false) {
                            zombies[7].getImage();
                            animatewalk();
                            if (zombies[6].z.dead) {
                                zombies[7].z.hp -= dmg[2];
                            }
                            zombie[2] = 1;
                            zombies[7].draw(sprite, g2d);
                            placed[zombies[7].z.collision] = 0;
                            fr[zombies[7].z.collision] = 0;
                        }
                    }else if(points == 3) {
                        zombies[24].getImage();
                        zombies[24].z.hp -= dmg[0];
                        zombies[24].draw(sprite, g2d);
                        if (zombies[24].z.dead == false) {
                            zombie[0] = 2;
                            placed[zombies[24].z.collision] = 0;
                            fr[zombies[24].z.collision] = 0;
                        }
                        if (zombies[24].z.dead == true && zombie[2] == 2) {
                            dmg[0] = 0;
                            zombie[0] = 1;
                        }
                        if (zombies[24].z.dead == true && zombies[25].z.dead == true) {
                            dmg[0] = 0;
                            ++points;
                            zombiecount += 2;
                            zombie[0] = 0;
                        }
                        if (zombies[24].z.gameOver == true || zombies[25].z.gameOver == true) {
                            gameState = 12;
                            initial = true;
                        }
                        if ((zombies[24].z.collision <= 22 || zombies[24].z.dead == true) && zombies[25].z.dead == false) {
                            zombies[25].getImage();
                            animatewalk();
                            if (zombies[24].z.dead) {
                                zombies[25].z.hp -= dmg[0];
                            }
                            zombie[0] = 1;
                            zombies[25].draw(sprite, g2d);
                            placed[zombies[25].z.collision] = 0;
                            fr[zombies[25].z.collision] = 0;
                        }
                    }
                    else {
                        completat1 = true;
                        Connection c = null;
                        Statement stmt = null;
                        try {
                            Class.forName("org.sqlite.JDBC");
                            c = DriverManager.getConnection("jdbc:sqlite:db.db");
                            c.setAutoCommit(false);
                            stmt = c.createStatement();
                            String str1, str2;
                            if (completatTutorial) str1 = "true";
                            else str1 = "false";
                            if (completat2) str2 = "true";
                            else str2 = "false";
                            String sql = "INSERT INTO GAME (ID, COMPLETATTUTORIAL, COMPLETAT1, COMPLETAT2, ZOMBIECOUNT) " +
                                    "VALUES (" + (++id) + ",'" + str1 + "', 'true', '" + str2 + "', " + zombiecount + " );";
                            stmt.executeUpdate(sql);
                            stmt.close();
                            c.commit();
                            c.close();
                        } catch (Exception e) {
                            System.err.println(e.getClass().getName() + ": " + e.getMessage());
                            System.exit(0);
                        }

                        points = 0;
                        gameState = 6;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (gameState == 6) {
                BufferedImage tileImage = null;
                try {
                    tileImage = ImageIO.read(getClass().getResourceAsStream("/textures/victory.png"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                TexturePaint tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, tileImage.getWidth(), tileImage.getHeight()));
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.06F));
                g2d.setPaint(tileTexture);
                g2d.fillRect(0, 0, 1080, 720);
                BufferedImage Image = null;
                try {
                    Image = ImageIO.read(getClass().getResourceAsStream("/textures/Jet.png"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                TexturePaint Texture = new TexturePaint(Image, new Rectangle(20, 250, 100, 100));
                g2d.setPaint(Texture);
                g2d.fillRect(20, 150, 100, 100);
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 15F));
                String text = "Jet:Am si eu o lupta pentru voi.";
                g2d.setPaint(Color.white);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                g2d.drawString(text, 125, 250);
                text = "Ai deblocat barca pentru Misiunea lui Jet.";
                g2d.setPaint(Color.white);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                g2d.drawString(text, 125, 400);
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 25F));
                text = "Apasa Space";
                g2d.setPaint(Color.white);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                g2d.drawString(text, 405, 630);
                if (kh.skip) {
                    gameState = 2;
                }
            } else if (gameState == 7) {
                if (music == false) {
                    playTutorial();
                    music = true;
                }
                if (fr1 == true) {
                    g2d.setColor(Color.white);
                } else {
                    g2d.setColor(Color.gray);
                }
                g2d.fillRect(icon1x, icon1y, 50, 50);
                BufferedImage tileImage = null;
                try {
                    tileImage = ImageIO.read(getClass().getResourceAsStream("/textures/Shot_1.png"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                g2d.fillRect(icon1x, icon1y, 50, 50);
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 25F));
                String text = "Puncte:" + points;
                g2d.setPaint(Color.white);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                g2d.drawString(text, 700, 25);
                TexturePaint tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, 50, 50));
                g2d.setPaint(tileTexture);
                g2d.fillRect(icon1x, icon1y, 50, 50);
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 15F));
                text = "250";
                g2d.setPaint(Color.white);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                g2d.drawString(text, 0, 50);
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 25F));
                text = "Arme:" + currency;
                g2d.setPaint(Color.white);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                g2d.drawString(text, 700, 50);
                if (fr2 == true) {
                    g2d.setColor(Color.white);
                } else {
                    g2d.setColor(Color.gray);
                }
                g2d.fillRect(icon1x + 55, icon1y, 50, 50);
                tileImage = null;
                try {
                    tileImage = ImageIO.read(getClass().getResourceAsStream("/textures/factory.png"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, 50, 50));
                g2d.setPaint(tileTexture);
                g2d.fillRect(icon1x + 55, icon1y, 50, 50);
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 15F));
                text = "100";
                g2d.setPaint(Color.white);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                g2d.drawString(text, 55, 50);
                if (fr3 == true) {
                    g2d.setColor(Color.white);
                } else {
                    g2d.setColor(Color.gray);
                }
                g2d.fillRect(icon1x + 110, icon1y, 50, 50);
                tileImage = null;
                try {
                    tileImage = ImageIO.read(getClass().getResourceAsStream("/textures/boat.png"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, 50, 50));
                g2d.setPaint(tileTexture);
                g2d.fillRect(icon1x + 110, icon1y, 50, 50);
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 15F));
                text = "50";
                g2d.setPaint(Color.white);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                g2d.drawString(text, 110, 50);
                try {
                    BufferedImage TileImage = ImageIO.read(getClass().getResourceAsStream("/textures/grass.png"));
                    BufferedImage AltTileImage = ImageIO.read(getClass().getResourceAsStream("/textures/dirt.png"));
                    for (int x = 0; x < 8; x++) {
                        for (int y = 0; y < 3; y++) {
                            if (altmap[y][x] == 0) {
                                if (y == 1) {
                                    BufferedImage TileImage1 = ImageIO.read(getClass().getResourceAsStream("/textures/water.png"));
                                    if (placable[x + y * 8] == 1 && (fr[x + y * 8] == 1 && fr1) || placed[x + y * 8] == 1) {
                                        tileTexture = new TexturePaint(TileImage1, new Rectangle(0, 0, TileImage1.getWidth(), TileImage1.getHeight()));
                                        g2d.setPaint(tileTexture);
                                        g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                        timer.start();
                                        placed[x + y * 8] = 1;
                                        BufferedImage tileImage1 = ImageIO.read(getClass().getResourceAsStream("/textures/Shot_1 water.png"));
                                        BufferedImage tileImage2 = ImageIO.read(getClass().getResourceAsStream("/textures/Shot_2 water.png"));
                                        animateshot1();
                                        tileImage = toggleImage ? tileImage1 : tileImage2;
                                        tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, tileImage.getWidth(), tileImage.getHeight()));
                                        g2d.setPaint(tileTexture);
                                        g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                    }
                                    if ((fr[x + y * 8] == 3 && fr3) || placed[x + y * 8] == 3) {
                                        tileTexture = new TexturePaint(TileImage1, new Rectangle(0, 0, TileImage1.getWidth(), TileImage1.getHeight()));
                                        g2d.setPaint(tileTexture);
                                        g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                        placed[x + y * 8] = 3;
                                        placable[x + y * 8] = 1;
                                        BufferedImage tileImage1 = ImageIO.read(getClass().getResourceAsStream("/textures/boat.png"));
                                        tileTexture = new TexturePaint(tileImage1, new Rectangle(0, 0, tileImage1.getWidth(), tileImage1.getHeight()));
                                        g2d.setPaint(tileTexture);
                                        g2d.fillRect(x * 135, 150 + y * 135, 130, 46);
                                    }
                                    if (!((placable[x + y * 8] == 1 && (fr[x + y * 8] == 1 && fr1 && placed[x + y * 8] != 2 && placed[x + y * 8] != 3) || placed[x + y * 8] == 1) || (fr[x + y * 8] == 3 && fr2) || placed[x + y * 8] == 3)) {
                                        tileImage = ImageIO.read(getClass().getResourceAsStream("/textures/water.png"));
                                        tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, tileImage.getWidth(), tileImage.getHeight()));
                                        g2d.setPaint(tileTexture);
                                        g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                    }
                                } else {
                                    if ((fr[x + y * 8] == 1 && fr1 && placed[x + y * 8] != 2) || placed[x + y * 8] == 1) {
                                        tileTexture = new TexturePaint(TileImage, new Rectangle(0, 0, TileImage.getWidth(), TileImage.getHeight()));
                                        g2d.setPaint(tileTexture);
                                        g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                        timer.start();
                                        placed[x + y * 8] = 1;
                                        BufferedImage tileImage1 = ImageIO.read(getClass().getResourceAsStream("/textures/Shot_1.png"));
                                        BufferedImage tileImage2 = ImageIO.read(getClass().getResourceAsStream("/textures/Shot_2.png"));
                                        animateshot1();
                                        tileImage = toggleImage ? tileImage1 : tileImage2;
                                        tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, tileImage.getWidth(), tileImage.getHeight()));
                                        g2d.setPaint(tileTexture);
                                        g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                    }
                                    if ((fr[x + y * 8] == 2 && fr2) || placed[x + y * 8] == 2) {
                                        tileTexture = new TexturePaint(TileImage, new Rectangle(0, 0, TileImage.getWidth(), TileImage.getHeight()));
                                        g2d.setPaint(tileTexture);
                                        g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                        currency += 1;
                                        placed[x + y * 8] = 2;
                                        BufferedImage tileImage1 = ImageIO.read(getClass().getResourceAsStream("/textures/factory.png"));
                                        tileTexture = new TexturePaint(tileImage1, new Rectangle(0, 0, tileImage1.getWidth(), tileImage1.getHeight()));
                                        g2d.setPaint(tileTexture);
                                        g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                    }
                                    if (!(((fr[x + y * 8] == 1 && fr1 && placed[x + y * 8] != 2) || placed[x + y * 8] == 1) || ((fr[x + y * 8] == 2 && fr2) || placed[x + y * 8] == 2))) {
                                        tileImage = ImageIO.read(getClass().getResourceAsStream("/textures/grass.png"));
                                        tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, tileImage.getWidth(), tileImage.getHeight()));
                                        g2d.setPaint(tileTexture);
                                        g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                    }
                                }
                            } else {
                                if (y == 1) {
                                    BufferedImage TileImage1 = ImageIO.read(getClass().getResourceAsStream("/textures/water.png"));
                                    if (placable[x + y * 8] == 1 && (fr[x + y * 8] == 1 && fr1) || placed[x + y * 8] == 1) {
                                        tileTexture = new TexturePaint(TileImage1, new Rectangle(0, 0, TileImage1.getWidth(), TileImage1.getHeight()));
                                        g2d.setPaint(tileTexture);
                                        g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                        timer.start();
                                        placed[x + y * 8] = 1;
                                        BufferedImage tileImage1 = ImageIO.read(getClass().getResourceAsStream("/textures/Shot_1.png"));
                                        BufferedImage tileImage2 = ImageIO.read(getClass().getResourceAsStream("/textures/Shot_2.png"));
                                        animateshot1();
                                        tileImage = toggleImage ? tileImage1 : tileImage2;
                                        tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, tileImage.getWidth(), tileImage.getHeight()));
                                        g2d.setPaint(tileTexture);
                                        g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                    }
                                    if ((fr[x + y * 8] == 3 && fr3) || placed[x + y * 8] == 3) {
                                        tileTexture = new TexturePaint(TileImage1, new Rectangle(0, 0, TileImage1.getWidth(), TileImage1.getHeight()));
                                        g2d.setPaint(tileTexture);
                                        g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                        currency += 1;
                                        placed[x + y * 8] = 2;
                                        placable[x + y * 8] = 1;
                                        BufferedImage tileImage1 = ImageIO.read(getClass().getResourceAsStream("/textures/boat.png"));
                                        tileTexture = new TexturePaint(tileImage1, new Rectangle(0, 0, tileImage1.getWidth(), tileImage1.getHeight()));
                                        g2d.setPaint(tileTexture);
                                        g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                    }
                                    if (!(((fr[x + y * 8] == 1 && fr1 && placed[x + y * 8] != 2) || placed[x + y * 8] == 1) || ((fr[x + y * 8] == 2 && fr2) || placed[x + y * 8] == 2))) {
                                        tileImage = ImageIO.read(getClass().getResourceAsStream("/textures/water.png"));
                                        tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, tileImage.getWidth(), tileImage.getHeight()));
                                        g2d.setPaint(tileTexture);
                                        g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                    }
                                } else {
                                    if ((fr[x + y * 8] == 1 && fr1 && placed[x + y * 8] != 2) || placed[x + y * 8] == 1) {
                                        tileTexture = new TexturePaint(AltTileImage, new Rectangle(0, 0, AltTileImage.getWidth(), AltTileImage.getHeight()));
                                        g2d.setPaint(tileTexture);
                                        g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                        timer.start();
                                        placed[x + y * 8] = 1;
                                        BufferedImage tileImage1 = ImageIO.read(getClass().getResourceAsStream("/textures/Shot_1.png"));
                                        BufferedImage tileImage2 = ImageIO.read(getClass().getResourceAsStream("/textures/Shot_2.png"));
                                        animateshot1();
                                        tileImage = toggleImage ? tileImage1 : tileImage2;
                                        tileTexture = new TexturePaint(AltTileImage, new Rectangle(0, 0, AltTileImage.getWidth(), AltTileImage.getHeight()));
                                        g2d.setPaint(tileTexture);
                                        g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                    }
                                    if ((fr[x + y * 8] == 2 && fr2) || placed[x + y * 8] == 2) {
                                        tileTexture = new TexturePaint(AltTileImage, new Rectangle(0, 0, AltTileImage.getWidth(), AltTileImage.getHeight()));
                                        g2d.setPaint(tileTexture);
                                        g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                        currency += 1;
                                        placed[x + y * 8] = 2;
                                        BufferedImage tileImage1 = ImageIO.read(getClass().getResourceAsStream("/textures/factory.png"));
                                        tileTexture = new TexturePaint(tileImage1, new Rectangle(0, 0, tileImage1.getWidth(), tileImage1.getHeight()));
                                        g2d.setPaint(tileTexture);
                                        g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                    }
                                    if (!(((fr[x + y * 8] == 1 && fr1 && placed[x + y * 8] != 2) || placed[x + y * 8] == 1) || ((fr[x + y * 8] == 2 && fr2) || placed[x + y * 8] == 2))) {
                                        tileImage = ImageIO.read(getClass().getResourceAsStream("/textures/dirt.png"));
                                        tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, tileImage.getWidth(), tileImage.getHeight()));
                                        g2d.setPaint(tileTexture);
                                        g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                    }
                                }
                            }
                            currency += 1;
                        }
                    }
                    tileImage = ImageIO.read(getClass().getResourceAsStream("/textures/grass.png"));
                    for (int x = 0; x < 8; ++x) {
                        if (altmap[3][x] == 1) {
                            tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, tileImage.getWidth(), tileImage.getHeight()));
                            g2d.setPaint(tileTexture);
                            g2d.fillRect(x * 135, 150 + 3 * 135, 130, 130);
                        } else {
                            tileTexture = new TexturePaint(AltTileImage, new Rectangle(0, 0, AltTileImage.getWidth(), AltTileImage.getHeight()));
                            g2d.setPaint(tileTexture);
                            g2d.fillRect(x * 135, 150 + 3 * 135, 130, 100);
                        }
                    }
                    if (points == 0) {
                        zombies[8].getImage();
                        animatewalk();
                        zombies[8].z.hp -= dmg[2];
                        zombie[2] = 1;
                        zombies[8].draw(sprite, g2d);
                        if (zombies[8].z.dead == true) {
                            dmg[2] = 0;
                            zombie[2] = 0;
                            ++points;
                            ++zombiecount;
                        }
                        if (zombies[8].z.gameOver == true) {
                            gameState = 12;
                            initial=true;
                        }
                        placed[zombies[8].z.collision] = 0;
                        fr[zombies[8].z.collision] = 0;
                        BufferedImage Image = ImageIO.read(getClass().getResourceAsStream("/textures/Faye.png"));
                        TexturePaint Texture = new TexturePaint(Image, new Rectangle(5, 675, 50, 50));
                        g2d.setPaint(Texture);
                        g2d.fillRect(5, 675, 50, 50);
                        g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 10F));
                        text = "Faye:Nu poti pune soldati direct pe apa trebuie sa folosesti barcile.";
                        g2d.setPaint(Color.white);
                        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                        g2d.drawString(text, 50, 700);
                    } else if (points == 1) {
                        zombies[9].getImage();
                        animatewalk();
                        zombies[9].z.hp -= dmg[1];
                        zombie[1] = 1;
                        zombies[9].draw(sprite, g2d);
                        if (zombies[9].z.dead == true) {
                            dmg[1] = 0;
                            zombie[1] = 0;
                            ++points;
                            ++zombiecount;
                        }
                        if (zombies[9].z.gameOver == true) {
                            gameState = 12;
                            initial=true;
                        }
                        placed[zombies[9].z.collision] = 0;
                        fr[zombies[9].z.collision] = 0;
                    } else if (points == 2) {
                        zombies[10].getImage();
                        zombies[11].getImage();
                        animatewalk();
                        zombies[10].z.hp -= dmg[1];
                        zombies[11].z.hp -= dmg[0];
                        zombie[1] = 1;
                        zombie[0] = 1;
                        zombies[10].draw(sprite, g2d);
                        zombies[11].draw(sprite, g2d);
                        if (zombies[10].z.dead == true) {
                            zombie[1] = 0;
                        }
                        if (zombies[11].z.dead == true) {
                            zombie[0] = 0;
                        }
                        if (zombies[10].z.dead == true && zombies[11].z.dead == true) {
                            dmg[1] = 0;
                            dmg[0] = 0;
                            ++points;
                            zombiecount += 2;
                        }
                        if (zombies[10].z.gameOver == true || zombies[11].z.gameOver == true) {
                            gameState = 12;
                            initial=true;
                        }
                        placed[zombies[10].z.collision] = 0;
                        fr[zombies[10].z.collision] = 0;
                        placed[zombies[11].z.collision] = 0;
                        fr[zombies[11].z.collision] = 0;
                    } else if (points == 3) {
                        zombies[21].getImage();
                        zombies[22].getImage();
                        zombies[23].getImage();
                        animatewalk();
                        zombies[21].z.hp -= dmg[1];
                        zombies[22].z.hp -= dmg[2];
                        zombies[23].z.hp -= dmg[0];
                        zombie[2] = 1;
                        zombie[1] = 1;
                        zombie[0] = 1;
                        zombies[21].draw(sprite, g2d);
                        zombies[22].draw(sprite, g2d);
                        zombies[23].draw(sprite, g2d);
                        if (zombies[21].z.dead == true) {
                            zombie[1] = 0;
                        }
                        if (zombies[22].z.dead == true) {
                            zombie[2] = 0;
                        }
                        if (zombies[23].z.dead == true) {
                            zombie[0] = 0;
                        }
                        if (zombies[21].z.dead == true && zombies[22].z.dead == true && zombies[23].z.dead == true) {
                            dmg[1] = 0;
                            dmg[2] = 0;
                            dmg[0] = 0;
                            ++points;
                            zombiecount += 3;
                        }
                        if (zombies[21].z.gameOver == true || zombies[22].z.gameOver == true || zombies[23].z.gameOver == true) {
                            gameState = 12;
                            initial=true;
                        }
                        placed[zombies[21].z.collision] = 0;
                        fr[zombies[21].z.collision] = 0;
                        placed[zombies[22].z.collision] = 0;
                        fr[zombies[22].z.collision] = 0;
                        placed[zombies[23].z.collision] = 0;
                        fr[zombies[23].z.collision] = 0;

                    } else {
                        completat2 = true;

                        Connection c = null;
                        Statement stmt = null;
                        try {
                            Class.forName("org.sqlite.JDBC");
                            c = DriverManager.getConnection("jdbc:sqlite:db.db");
                            c.setAutoCommit(false);
                            stmt = c.createStatement();
                            String str1, str2;
                            if (completatTutorial) str1 = "true";
                            else str1 = "false";
                            if (completat1) str2 = "true";
                            else str2 = "false";
                            String sql = "INSERT INTO GAME (ID, COMPLETATTUTORIAL, COMPLETAT1, COMPLETAT2, ZOMBIECOUNT) " +
                                    "VALUES (" + (++id) + ",'" + str1 + "', '" + str2 + "','true', " + zombiecount + " );";
                            stmt.executeUpdate(sql);
                            stmt.close();
                            c.commit();
                            c.close();
                        } catch (Exception e) {
                            System.err.println(e.getClass().getName() + ": " + e.getMessage());
                            System.exit(0);
                        }

                        points = 0;
                        gameState = 8;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (gameState == 8) {
                BufferedImage tileImage = null;
                try {
                    tileImage = ImageIO.read(getClass().getResourceAsStream("/textures/victory.png"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                TexturePaint tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, tileImage.getWidth(), tileImage.getHeight()));
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,  0.06F));
                g2d.setPaint(tileTexture);
                g2d.fillRect(0, 0, 1080, 720);
                BufferedImage Image = null;
                try {
                    Image = ImageIO.read(getClass().getResourceAsStream("/textures/Spike.png"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                TexturePaint Texture = new TexturePaint(Image, new Rectangle(20, 250, 100, 100));
                g2d.setPaint(Texture);
                g2d.fillRect(20, 150, 100, 100);
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 15F));
                String text = "Spike:Vreti sa salvam pe cineva?";
                g2d.setPaint(Color.white);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                g2d.drawString(text, 125, 250);
                text = "Ai deblocat nivelul Final.";
                g2d.setPaint(Color.white);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                g2d.drawString(text, 125, 400);
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 25F));
                text = "Apasa Space";
                g2d.setPaint(Color.white);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                g2d.drawString(text, 405, 630);
                if (kh.skip) {
                    gameState = 2;
                }
            } else if (gameState == 9) {
                if (music == false) {
                    playFinale();
                    music = true;
                }
                if (fr1 == true) {
                    g2d.setColor(Color.white);
                } else {
                    g2d.setColor(Color.gray);
                }
                g2d.fillRect(icon1x, icon1y, 50, 50);
                BufferedImage tileImage = null;
                try {
                    tileImage = ImageIO.read(getClass().getResourceAsStream("/textures/Shot_1.png"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                g2d.fillRect(icon1x, icon1y, 50, 50);
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 25F));
                String text = "Puncte:" + points;
                g2d.setPaint(Color.white);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                g2d.drawString(text, 700, 25);
                TexturePaint tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, 50, 50));
                g2d.setPaint(tileTexture);
                g2d.fillRect(icon1x, icon1y, 50, 50);
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 15F));
                text = "250";
                g2d.setPaint(Color.white);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                g2d.drawString(text, 0, 50);
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 25F));
                text = "Arme:" + currency;
                g2d.setPaint(Color.white);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                g2d.drawString(text, 700, 50);
                if (fr2 == true) {
                    g2d.setColor(Color.white);
                } else {
                    g2d.setColor(Color.gray);
                }
                g2d.fillRect(icon1x + 55, icon1y, 50, 50);
                tileImage = null;
                try {
                    tileImage = ImageIO.read(getClass().getResourceAsStream("/textures/factory.png"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, 50, 50));
                g2d.setPaint(tileTexture);
                g2d.fillRect(icon1x + 55, icon1y, 50, 50);
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 15F));
                text = "100";
                g2d.setPaint(Color.white);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                g2d.drawString(text, 55, 50);
                if (fr3 == true) {
                    g2d.setColor(Color.white);
                } else {
                    g2d.setColor(Color.gray);
                }
                g2d.fillRect(icon1x + 110, icon1y, 50, 50);
                tileImage = null;
                try {
                    tileImage = ImageIO.read(getClass().getResourceAsStream("/textures/boat.png"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, 50, 50));
                g2d.setPaint(tileTexture);
                g2d.fillRect(icon1x + 110, icon1y, 50, 50);
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 15F));
                text = "50";
                g2d.setPaint(Color.white);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                g2d.drawString(text, 110, 50);
                tileImage = null;
                try {
                    tileImage = ImageIO.read(getClass().getResourceAsStream("/textures/boat.png"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, 50, 50));
                g2d.setPaint(tileTexture);
                g2d.fillRect(icon1x + 110, icon1y, 50, 50);
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 15F));
                text = "50";
                g2d.setPaint(Color.white);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                g2d.drawString(text, 110, 50);


                if (fr4 == true) {
                    g2d.setColor(Color.white);
                } else {
                    g2d.setColor(Color.gray);
                }
                g2d.fillRect(icon1x + 165, icon1y, 50, 50);
                tileImage = null;
                try {
                    tileImage = ImageIO.read(getClass().getResourceAsStream("/textures/radar.png"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, 50, 50));
                g2d.setPaint(tileTexture);
                g2d.fillRect(icon1x + 165, icon1y, 50, 50);
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 15F));
                text = "300";
                g2d.setPaint(Color.white);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                g2d.drawString(text, 165, 50);
                tileImage = null;
                try {
                    tileImage = ImageIO.read(getClass().getResourceAsStream("/textures/radar.png"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, 50, 50));
                g2d.setPaint(tileTexture);
                g2d.fillRect(icon1x + 165, icon1y, 50, 50);
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 15F));
                text = "50";
                g2d.setPaint(Color.white);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                g2d.drawString(text, 165, 50);

                try {
                    BufferedImage TileImage = ImageIO.read(getClass().getResourceAsStream("/textures/grass1.png"));
                    BufferedImage AltTileImage = ImageIO.read(getClass().getResourceAsStream("/textures/dirt1.png"));
                    for (int x = 0; x < 8; x++) {
                        for (int y = 0; y < 3; y++) {
                            if (altmap[y][x] == 0) {
                                if (y == 1) {
                                    BufferedImage TileImage1 = ImageIO.read(getClass().getResourceAsStream("/textures/water1.png"));
                                    if (placable[x + y * 8] == 1 && (fr[x + y * 8] == 1 && fr1) || placed[x + y * 8] == 1) {
                                        tileTexture = new TexturePaint(TileImage1, new Rectangle(0, 0, TileImage1.getWidth(), TileImage1.getHeight()));
                                        g2d.setPaint(tileTexture);
                                        g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                        timer.start();
                                        placed[x + y * 8] = 1;
                                        BufferedImage tileImage1 = ImageIO.read(getClass().getResourceAsStream("/textures/Shot_1 water.png"));
                                        BufferedImage tileImage2 = ImageIO.read(getClass().getResourceAsStream("/textures/Shot_2 water.png"));
                                        animateshot1();
                                        tileImage = toggleImage ? tileImage1 : tileImage2;
                                        tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, tileImage.getWidth(), tileImage.getHeight()));
                                        g2d.setPaint(tileTexture);
                                        g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                    }
                                    if ((fr[x + y * 8] == 3 && fr3) || placed[x + y * 8] == 3) {
                                        tileTexture = new TexturePaint(TileImage1, new Rectangle(0, 0, TileImage1.getWidth(), TileImage1.getHeight()));
                                        g2d.setPaint(tileTexture);
                                        g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                        placed[x + y * 8] = 3;
                                        placable[x + y * 8] = 1;
                                        BufferedImage tileImage1 = ImageIO.read(getClass().getResourceAsStream("/textures/boat.png"));
                                        tileTexture = new TexturePaint(tileImage1, new Rectangle(0, 0, tileImage1.getWidth(), tileImage1.getHeight()));
                                        g2d.setPaint(tileTexture);
                                        g2d.fillRect(x * 135, 150 + y * 135, 130, 46);
                                    }
                                    if (!((placable[x + y * 8] == 1 && (fr[x + y * 8] == 1 && fr1 && placed[x + y * 8] != 2 && placed[x + y * 8] != 3) || placed[x + y * 8] == 1) || (fr[x + y * 8] == 3 && fr2) || placed[x + y * 8] == 3)) {
                                        tileImage = ImageIO.read(getClass().getResourceAsStream("/textures/water1.png"));
                                        tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, tileImage.getWidth(), tileImage.getHeight()));
                                        g2d.setPaint(tileTexture);
                                        g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                    }
                                } else {
                                    if ((fr[x + y * 8] == 1 && fr1 && placed[x + y * 8] != 2) || placed[x + y * 8] == 1) {
                                        tileTexture = new TexturePaint(TileImage, new Rectangle(0, 0, TileImage.getWidth(), TileImage.getHeight()));
                                        g2d.setPaint(tileTexture);
                                        g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                        timer.start();
                                        placed[x + y * 8] = 1;
                                        BufferedImage tileImage1 = ImageIO.read(getClass().getResourceAsStream("/textures/Shot_1.png"));
                                        BufferedImage tileImage2 = ImageIO.read(getClass().getResourceAsStream("/textures/Shot_2.png"));
                                        animateshot1();
                                        tileImage = toggleImage ? tileImage1 : tileImage2;
                                        tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, tileImage.getWidth(), tileImage.getHeight()));
                                        g2d.setPaint(tileTexture);
                                        g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                    }
                                    if ((fr[x + y * 8] == 2 && fr2) || placed[x + y * 8] == 2) {
                                        tileTexture = new TexturePaint(TileImage, new Rectangle(0, 0, TileImage.getWidth(), TileImage.getHeight()));
                                        g2d.setPaint(tileTexture);
                                        g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                        currency += 1;
                                        placed[x + y * 8] = 2;
                                        BufferedImage tileImage1 = ImageIO.read(getClass().getResourceAsStream("/textures/factory.png"));
                                        tileTexture = new TexturePaint(tileImage1, new Rectangle(0, 0, tileImage1.getWidth(), tileImage1.getHeight()));
                                        g2d.setPaint(tileTexture);
                                        g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                    }
                                    if (!(((fr[x + y * 8] == 1 && fr1 && placed[x + y * 8] != 2) || placed[x + y * 8] == 1) || ((fr[x + y * 8] == 2 && fr2) || placed[x + y * 8] == 2))) {
                                        tileImage = ImageIO.read(getClass().getResourceAsStream("/textures/grass1.png"));
                                        tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, tileImage.getWidth(), tileImage.getHeight()));
                                        g2d.setPaint(tileTexture);
                                        g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                    }
                                }
                            } else {
                                if (y == 1) {
                                    BufferedImage TileImage1 = ImageIO.read(getClass().getResourceAsStream("/textures/water1.png"));
                                    if (placable[x + y * 8] == 1 && (fr[x + y * 8] == 1 && fr1) || placed[x + y * 8] == 1) {
                                        tileTexture = new TexturePaint(TileImage1, new Rectangle(0, 0, TileImage1.getWidth(), TileImage1.getHeight()));
                                        g2d.setPaint(tileTexture);
                                        g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                        timer.start();
                                        placed[x + y * 8] = 1;
                                        BufferedImage tileImage1 = ImageIO.read(getClass().getResourceAsStream("/textures/Shot_1.png"));
                                        BufferedImage tileImage2 = ImageIO.read(getClass().getResourceAsStream("/textures/Shot_2.png"));
                                        animateshot1();
                                        tileImage = toggleImage ? tileImage1 : tileImage2;
                                        tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, tileImage.getWidth(), tileImage.getHeight()));
                                        g2d.setPaint(tileTexture);
                                        g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                    }
                                    if ((fr[x + y * 8] == 3 && fr3) || placed[x + y * 8] == 3) {
                                        tileTexture = new TexturePaint(TileImage1, new Rectangle(0, 0, TileImage1.getWidth(), TileImage1.getHeight()));
                                        g2d.setPaint(tileTexture);
                                        g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                        currency += 1;
                                        placed[x + y * 8] = 2;
                                        placable[x + y * 8] = 1;
                                        BufferedImage tileImage1 = ImageIO.read(getClass().getResourceAsStream("/textures/boat.png"));
                                        tileTexture = new TexturePaint(tileImage1, new Rectangle(0, 0, tileImage1.getWidth(), tileImage1.getHeight()));
                                        g2d.setPaint(tileTexture);
                                        g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                    }
                                    if (!(((fr[x + y * 8] == 1 && fr1 && placed[x + y * 8] != 2) || placed[x + y * 8] == 1) || ((fr[x + y * 8] == 2 && fr2) || placed[x + y * 8] == 2))) {
                                        tileImage = ImageIO.read(getClass().getResourceAsStream("/textures/water1.png"));
                                        tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, tileImage.getWidth(), tileImage.getHeight()));
                                        g2d.setPaint(tileTexture);
                                        g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                    }
                                } else {
                                    if ((fr[x + y * 8] == 1 && fr1 && placed[x + y * 8] != 2) || placed[x + y * 8] == 1) {
                                        tileTexture = new TexturePaint(AltTileImage, new Rectangle(0, 0, AltTileImage.getWidth(), AltTileImage.getHeight()));
                                        g2d.setPaint(tileTexture);
                                        g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                        timer.start();
                                        placed[x + y * 8] = 1;
                                        BufferedImage tileImage1 = ImageIO.read(getClass().getResourceAsStream("/textures/Shot_1.png"));
                                        BufferedImage tileImage2 = ImageIO.read(getClass().getResourceAsStream("/textures/Shot_2.png"));
                                        animateshot1();
                                        tileImage = toggleImage ? tileImage1 : tileImage2;
                                        tileTexture = new TexturePaint(AltTileImage, new Rectangle(0, 0, AltTileImage.getWidth(), AltTileImage.getHeight()));
                                        g2d.setPaint(tileTexture);
                                        g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                    }
                                    if ((fr[x + y * 8] == 2 && fr2) || placed[x + y * 8] == 2) {
                                        tileTexture = new TexturePaint(AltTileImage, new Rectangle(0, 0, AltTileImage.getWidth(), AltTileImage.getHeight()));
                                        g2d.setPaint(tileTexture);
                                        g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                        currency += 1;
                                        placed[x + y * 8] = 2;
                                        BufferedImage tileImage1 = ImageIO.read(getClass().getResourceAsStream("/textures/factory.png"));
                                        tileTexture = new TexturePaint(tileImage1, new Rectangle(0, 0, tileImage1.getWidth(), tileImage1.getHeight()));
                                        g2d.setPaint(tileTexture);
                                        g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                    }
                                    if (!(((fr[x + y * 8] == 1 && fr1 && placed[x + y * 8] != 2) || placed[x + y * 8] == 1) || ((fr[x + y * 8] == 2 && fr2) || placed[x + y * 8] == 2))) {
                                        tileImage = ImageIO.read(getClass().getResourceAsStream("/textures/dirt1.png"));
                                        tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, tileImage.getWidth(), tileImage.getHeight()));
                                        g2d.setPaint(tileTexture);
                                        g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                    }
                                }
                            }
                            currency += 1;
                        }
                    }
                    tileImage = ImageIO.read(getClass().getResourceAsStream("/textures/grass1.png"));
                    for (int x = 0; x < 8; ++x) {
                        if (altmap[3][x] == 1) {
                            tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, tileImage.getWidth(), tileImage.getHeight()));
                            g2d.setPaint(tileTexture);
                            g2d.fillRect(x * 135, 150 + 3 * 135, 130, 130);
                        } else {
                            tileTexture = new TexturePaint(AltTileImage, new Rectangle(0, 0, AltTileImage.getWidth(), AltTileImage.getHeight()));
                            g2d.setPaint(tileTexture);
                            g2d.fillRect(x * 135, 150 + 3 * 135, 130, 100);
                        }
                    }
                    if (points == 0) {
                        zombies[12].getImage();
                        animatewalk();
                        zombies[12].z.hp -= dmg[2];
                        zombie[2] = 1;
                        zombies[12].draw(sprite, g2d);
                        if (zombies[12].z.dead == true) {
                            dmg[2] = 0;
                            zombie[2] = 0;
                            ++points;
                            ++zombiecount;
                        }
                        if (zombies[12].z.gameOver == true) {
                            gameState = 12;
                            initial=true;
                        }
                        placed[zombies[12].z.collision] = 0;
                        fr[zombies[12].z.collision] = 0;
                        BufferedImage Image = ImageIO.read(getClass().getResourceAsStream("/textures/Lucia.png"));
                        TexturePaint Texture = new TexturePaint(Image, new Rectangle(5, 675, 50, 50));
                        g2d.setPaint(Texture);
                        g2d.fillRect(5, 675, 50, 50);
                        g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 10F));
                        text = "Lucia:Spike salveaza-ma!";
                        g2d.setPaint(Color.white);
                        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                        g2d.drawString(text, 50, 700);

                        if (fog)
                            for (int x = 0; x < 8; x++) {
                                for (int y = 0; y < 3; y++) {
                                    if (altmap[y][x] == 0) {
                                        if (x + y * 8 == 6 || x + y * 8 == 7 || x + y * 8 == 14 || x + y * 8 == 15 || x + y * 8 == 22 || x + y * 8 == 23) {
                                            TileImage = ImageIO.read(getClass().getResourceAsStream("/textures/smoke1.png"));
                                            if ((fr[x + y * 8] == 1 && fr1 && placed[x + y * 8] != 2) || placed[x + y * 8] == 1) {
                                                tileTexture = new TexturePaint(TileImage, new Rectangle(0, 0, TileImage.getWidth(), TileImage.getHeight()));
                                                g2d.setPaint(tileTexture);
                                                g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                                timer.start();
                                                placed[x + y * 8] = 1;
                                                BufferedImage tileImage1 = ImageIO.read(getClass().getResourceAsStream("/textures/Shot_1.png"));
                                                BufferedImage tileImage2 = ImageIO.read(getClass().getResourceAsStream("/textures/Shot_2.png"));
                                                animateshot1();
                                                tileImage = toggleImage ? tileImage1 : tileImage2;
                                                tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, tileImage.getWidth(), tileImage.getHeight()));
                                                g2d.setPaint(tileTexture);
                                                g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                            }
                                            if ((fr[x + y * 8] == 2 && fr2) || placed[x + y * 8] == 2) {
                                                tileTexture = new TexturePaint(TileImage, new Rectangle(0, 0, TileImage.getWidth(), TileImage.getHeight()));
                                                g2d.setPaint(tileTexture);
                                                g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                                currency += 1;
                                                placed[x + y * 8] = 2;
                                                BufferedImage tileImage1 = ImageIO.read(getClass().getResourceAsStream("/textures/factory.png"));
                                                tileTexture = new TexturePaint(tileImage1, new Rectangle(0, 0, tileImage1.getWidth(), tileImage1.getHeight()));
                                                g2d.setPaint(tileTexture);
                                                g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                            }
                                            if (!(((fr[x + y * 8] == 1 && fr1 && placed[x + y * 8] != 2) || placed[x + y * 8] == 1) || ((fr[x + y * 8] == 2 && fr2) || placed[x + y * 8] == 2))) {
                                                tileImage = ImageIO.read(getClass().getResourceAsStream("/textures/smoke1.png"));
                                                tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, tileImage.getWidth(), tileImage.getHeight()));
                                                g2d.setPaint(tileTexture);
                                                g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                            }
                                        }
                                    }
                                }
                            }
                    } else if (points == 1) {
                        zombies[13].getImage();
                        animatewalk();
                        zombies[13].z.hp -= dmg[1];
                        zombie[1] = 1;
                        zombies[13].draw(sprite, g2d);
                        if (zombies[13].z.dead == true) {
                            dmg[1] = 0;
                            zombie[1] = 0;
                            ++points;
                            ++zombiecount;
                        }
                        if (zombies[13].z.gameOver == true) {
                            gameState = 12;
                            initial=true;
                        }
                        placed[zombies[13].z.collision] = 0;
                        fr[zombies[13].z.collision] = 0;

                        if (fog)
                            for (int x = 0; x < 8; x++) {
                                for (int y = 0; y < 3; y++) {
                                    if (altmap[y][x] == 0) {
                                        if (x + y * 8 == 6 || x + y * 8 == 7 || x + y * 8 == 14 || x + y * 8 == 15 || x + y * 8 == 22 || x + y * 8 == 23) {
                                            TileImage = ImageIO.read(getClass().getResourceAsStream("/textures/smoke1.png"));
                                            if ((fr[x + y * 8] == 1 && fr1 && placed[x + y * 8] != 2) || placed[x + y * 8] == 1) {
                                                tileTexture = new TexturePaint(TileImage, new Rectangle(0, 0, TileImage.getWidth(), TileImage.getHeight()));
                                                g2d.setPaint(tileTexture);
                                                g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                                timer.start();
                                                placed[x + y * 8] = 1;
                                                BufferedImage tileImage1 = ImageIO.read(getClass().getResourceAsStream("/textures/Shot_1.png"));
                                                BufferedImage tileImage2 = ImageIO.read(getClass().getResourceAsStream("/textures/Shot_2.png"));
                                                animateshot1();
                                                tileImage = toggleImage ? tileImage1 : tileImage2;
                                                tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, tileImage.getWidth(), tileImage.getHeight()));
                                                g2d.setPaint(tileTexture);
                                                g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                            }
                                            if ((fr[x + y * 8] == 2 && fr2) || placed[x + y * 8] == 2) {
                                                tileTexture = new TexturePaint(TileImage, new Rectangle(0, 0, TileImage.getWidth(), TileImage.getHeight()));
                                                g2d.setPaint(tileTexture);
                                                g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                                currency += 1;
                                                placed[x + y * 8] = 2;
                                                BufferedImage tileImage1 = ImageIO.read(getClass().getResourceAsStream("/textures/factory.png"));
                                                tileTexture = new TexturePaint(tileImage1, new Rectangle(0, 0, tileImage1.getWidth(), tileImage1.getHeight()));
                                                g2d.setPaint(tileTexture);
                                                g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                            }
                                            if (!(((fr[x + y * 8] == 1 && fr1 && placed[x + y * 8] != 2) || placed[x + y * 8] == 1) || ((fr[x + y * 8] == 2 && fr2) || placed[x + y * 8] == 2))) {
                                                tileImage = ImageIO.read(getClass().getResourceAsStream("/textures/smoke1.png"));
                                                tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, tileImage.getWidth(), tileImage.getHeight()));
                                                g2d.setPaint(tileTexture);
                                                g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                            }
                                        }
                                    }
                                }
                            }
                    } else if (points == 2) {
                        zombies[14].getImage();
                        zombies[15].getImage();
                        animatewalk();
                        zombies[14].z.hp -= dmg[1];
                        zombies[15].z.hp -= dmg[0];
                        zombie[1] = 1;
                        zombie[0] = 1;
                        zombies[14].draw(sprite, g2d);
                        zombies[15].draw(sprite, g2d);
                        if (zombies[14].z.dead == true) {
                            zombie[1] = 0;
                        }
                        if (zombies[15].z.dead == true) {
                            zombie[0] = 0;
                        }
                        if (zombies[14].z.dead == true && zombies[15].z.dead == true) {
                            dmg[1] = 0;
                            dmg[0] = 0;
                            ++points;
                            zombiecount += 2;
                        }
                        if (zombies[14].z.gameOver == true || zombies[15].z.gameOver == true) {
                            gameState = 12;
                            initial=true;
                        }
                        placed[zombies[14].z.collision] = 0;
                        fr[zombies[14].z.collision] = 0;
                        placed[zombies[15].z.collision] = 0;
                        fr[zombies[15].z.collision] = 0;

                        if (fog)
                            for (int x = 0; x < 8; x++) {
                                for (int y = 0; y < 3; y++) {
                                    if (altmap[y][x] == 0) {
                                        if (x + y * 8 == 6 || x + y * 8 == 7 || x + y * 8 == 14 || x + y * 8 == 15 || x + y * 8 == 22 || x + y * 8 == 23) {
                                            TileImage = ImageIO.read(getClass().getResourceAsStream("/textures/smoke1.png"));
                                            if ((fr[x + y * 8] == 1 && fr1 && placed[x + y * 8] != 2) || placed[x + y * 8] == 1) {
                                                tileTexture = new TexturePaint(TileImage, new Rectangle(0, 0, TileImage.getWidth(), TileImage.getHeight()));
                                                g2d.setPaint(tileTexture);
                                                g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                                timer.start();
                                                placed[x + y * 8] = 1;
                                                BufferedImage tileImage1 = ImageIO.read(getClass().getResourceAsStream("/textures/Shot_1.png"));
                                                BufferedImage tileImage2 = ImageIO.read(getClass().getResourceAsStream("/textures/Shot_2.png"));
                                                animateshot1();
                                                tileImage = toggleImage ? tileImage1 : tileImage2;
                                                tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, tileImage.getWidth(), tileImage.getHeight()));
                                                g2d.setPaint(tileTexture);
                                                g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                            }
                                            if ((fr[x + y * 8] == 2 && fr2) || placed[x + y * 8] == 2) {
                                                tileTexture = new TexturePaint(TileImage, new Rectangle(0, 0, TileImage.getWidth(), TileImage.getHeight()));
                                                g2d.setPaint(tileTexture);
                                                g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                                currency += 1;
                                                placed[x + y * 8] = 2;
                                                BufferedImage tileImage1 = ImageIO.read(getClass().getResourceAsStream("/textures/factory.png"));
                                                tileTexture = new TexturePaint(tileImage1, new Rectangle(0, 0, tileImage1.getWidth(), tileImage1.getHeight()));
                                                g2d.setPaint(tileTexture);
                                                g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                            }
                                            if (!(((fr[x + y * 8] == 1 && fr1 && placed[x + y * 8] != 2) || placed[x + y * 8] == 1) || ((fr[x + y * 8] == 2 && fr2) || placed[x + y * 8] == 2))) {
                                                tileImage = ImageIO.read(getClass().getResourceAsStream("/textures/smoke1.png"));
                                                tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, tileImage.getWidth(), tileImage.getHeight()));
                                                g2d.setPaint(tileTexture);
                                                g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                            }
                                        }
                                    }
                                }
                            }
                    } else if (points == 3) {
                        zombies[16].getImage();
                        zombies[16].z.hp -= dmg[2];
                        zombies[16].draw(sprite, g2d);
                        if (zombies[16].z.dead == false) {
                            zombie[2] = 2;
                            placed[zombies[16].z.collision] = 0;
                            fr[zombies[16].z.collision] = 0;
                        }
                        if (zombies[16].z.dead == true && zombie[2] == 2) {
                            dmg[2] = 0;
                            zombie[2] = 1;
                        }
                        if (zombies[16].z.dead == true && zombies[17].z.dead == true) {
                            dmg[2] = 0;
                            ++points;
                            zombiecount += 2;
                        }
                        if (zombies[16].z.gameOver == true || zombies[17].z.gameOver == true) {
                            gameState = 12;
                            initial=true;
                        }
                        if ((zombies[16].z.collision <= 22 || zombies[16].z.dead == true) && zombies[17].z.dead==false) {
                            zombies[17].getImage();
                            animatewalk();
                            if (zombies[16].z.dead) {
                                zombies[17].z.hp -= dmg[2];
                                zombie[2] = 0;
                            }
                            zombie[2] = 1;
                            zombies[17].draw(sprite, g2d);
                            placed[zombies[17].z.collision] = 0;
                            fr[zombies[17].z.collision] = 0;
                        }

                        if (fog)
                            for (int x = 0; x < 8; x++) {
                                for (int y = 0; y < 3; y++) {
                                    if (altmap[y][x] == 0) {
                                        if (x + y * 8 == 6 || x + y * 8 == 7 || x + y * 8 == 14 || x + y * 8 == 15 || x + y * 8 == 22 || x + y * 8 == 23) {
                                            TileImage = ImageIO.read(getClass().getResourceAsStream("/textures/smoke1.png"));
                                            if ((fr[x + y * 8] == 1 && fr1 && placed[x + y * 8] != 2) || placed[x + y * 8] == 1) {
                                                tileTexture = new TexturePaint(TileImage, new Rectangle(0, 0, TileImage.getWidth(), TileImage.getHeight()));
                                                g2d.setPaint(tileTexture);
                                                g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                                timer.start();
                                                placed[x + y * 8] = 1;
                                                BufferedImage tileImage1 = ImageIO.read(getClass().getResourceAsStream("/textures/Shot_1.png"));
                                                BufferedImage tileImage2 = ImageIO.read(getClass().getResourceAsStream("/textures/Shot_2.png"));
                                                animateshot1();
                                                tileImage = toggleImage ? tileImage1 : tileImage2;
                                                tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, tileImage.getWidth(), tileImage.getHeight()));
                                                g2d.setPaint(tileTexture);
                                                g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                            }
                                            if ((fr[x + y * 8] == 2 && fr2) || placed[x + y * 8] == 2) {
                                                tileTexture = new TexturePaint(TileImage, new Rectangle(0, 0, TileImage.getWidth(), TileImage.getHeight()));
                                                g2d.setPaint(tileTexture);
                                                g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                                currency += 1;
                                                placed[x + y * 8] = 2;
                                                BufferedImage tileImage1 = ImageIO.read(getClass().getResourceAsStream("/textures/factory.png"));
                                                tileTexture = new TexturePaint(tileImage1, new Rectangle(0, 0, tileImage1.getWidth(), tileImage1.getHeight()));
                                                g2d.setPaint(tileTexture);
                                                g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                            }
                                            if (!(((fr[x + y * 8] == 1 && fr1 && placed[x + y * 8] != 2) || placed[x + y * 8] == 1) || ((fr[x + y * 8] == 2 && fr2) || placed[x + y * 8] == 2))) {
                                                tileImage = ImageIO.read(getClass().getResourceAsStream("/textures/smoke1.png"));
                                                tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, tileImage.getWidth(), tileImage.getHeight()));
                                                g2d.setPaint(tileTexture);
                                                g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                            }
                                        }
                                    }
                                }
                            }
                    } else if (points == 4) {
                        zombies[18].getImage();
                        zombies[19].getImage();
                        zombies[20].getImage();
                        animatewalk();
                        zombies[18].z.hp -= dmg[1];
                        zombies[19].z.hp -= dmg[2];
                        zombies[20].z.hp -= dmg[0];
                        zombie[2] = 1;
                        zombie[1] = 1;
                        zombie[0] = 1;
                        zombies[18].draw(sprite, g2d);
                        zombies[19].draw(sprite, g2d);
                        zombies[20].draw(sprite, g2d);
                        if (zombies[18].z.dead == true) {
                            zombie[1] = 0;
                        }
                        if (zombies[19].z.dead == true) {
                            zombie[2] = 0;
                        }
                        if (zombies[20].z.dead == true) {
                            zombie[0] = 0;
                        }
                        if (zombies[18].z.dead == true && zombies[19].z.dead == true && zombies[20].z.dead == true) {
                            dmg[1] = 0;
                            dmg[2] = 0;
                            dmg[0] = 0;
                            ++points;
                            zombiecount += 3;
                        }
                        if (zombies[18].z.gameOver == true || zombies[19].z.gameOver == true || zombies[20].z.gameOver == true) {
                            gameState = 12;
                            initial=true;
                        }
                        placed[zombies[18].z.collision] = 0;
                        fr[zombies[18].z.collision] = 0;
                        placed[zombies[19].z.collision] = 0;
                        fr[zombies[19].z.collision] = 0;
                        placed[zombies[20].z.collision] = 0;
                        fr[zombies[20].z.collision] = 0;

                        if (fog)
                            for (int x = 0; x < 8; x++) {
                                for (int y = 0; y < 3; y++) {
                                    if (altmap[y][x] == 0) {
                                        if (x + y * 8 == 6 || x + y * 8 == 7 || x + y * 8 == 14 || x + y * 8 == 15 || x + y * 8 == 22 || x + y * 8 == 23) {
                                            TileImage = ImageIO.read(getClass().getResourceAsStream("/textures/smoke1.png"));
                                            if ((fr[x + y * 8] == 1 && fr1 && placed[x + y * 8] != 2) || placed[x + y * 8] == 1) {
                                                tileTexture = new TexturePaint(TileImage, new Rectangle(0, 0, TileImage.getWidth(), TileImage.getHeight()));
                                                g2d.setPaint(tileTexture);
                                                g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                                timer.start();
                                                placed[x + y * 8] = 1;
                                                BufferedImage tileImage1 = ImageIO.read(getClass().getResourceAsStream("/textures/Shot_1.png"));
                                                BufferedImage tileImage2 = ImageIO.read(getClass().getResourceAsStream("/textures/Shot_2.png"));
                                                animateshot1();
                                                tileImage = toggleImage ? tileImage1 : tileImage2;
                                                tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, tileImage.getWidth(), tileImage.getHeight()));
                                                g2d.setPaint(tileTexture);
                                                g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                            }
                                            if ((fr[x + y * 8] == 2 && fr2) || placed[x + y * 8] == 2) {
                                                tileTexture = new TexturePaint(TileImage, new Rectangle(0, 0, TileImage.getWidth(), TileImage.getHeight()));
                                                g2d.setPaint(tileTexture);
                                                g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                                currency += 1;
                                                placed[x + y * 8] = 2;
                                                BufferedImage tileImage1 = ImageIO.read(getClass().getResourceAsStream("/textures/factory.png"));
                                                tileTexture = new TexturePaint(tileImage1, new Rectangle(0, 0, tileImage1.getWidth(), tileImage1.getHeight()));
                                                g2d.setPaint(tileTexture);
                                                g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                            }
                                            if (!(((fr[x + y * 8] == 1 && fr1 && placed[x + y * 8] != 2) || placed[x + y * 8] == 1) || ((fr[x + y * 8] == 2 && fr2) || placed[x + y * 8] == 2))) {
                                                tileImage = ImageIO.read(getClass().getResourceAsStream("/textures/smoke1.png"));
                                                tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, tileImage.getWidth(), tileImage.getHeight()));
                                                g2d.setPaint(tileTexture);
                                                g2d.fillRect(x * 135, 150 + y * 135, 130, 130);
                                            }
                                        }
                                    }
                                }
                            }
                    } else {
                        points = 0;
                        gameState = 10;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (gameState == 10) {
                BufferedImage tileImage = null;
                try {
                    tileImage = ImageIO.read(getClass().getResourceAsStream("/textures/victory.png"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                TexturePaint tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, tileImage.getWidth(), tileImage.getHeight()));
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.06F));
                g2d.setPaint(tileTexture);
                g2d.fillRect(0, 0, 1080, 720);
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 15F));
                String text = "Ai omorat " + zombiecount + " zombie, ai cunoscut un nou personaj si ai facut 3 misiuni pentru Faye, Jet si Spike. Felicitari!";
                g2d.setPaint(Color.white);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                g2d.drawString(text, 125, 250);
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 25F));
                text = "Apasa Space";
                g2d.setPaint(Color.white);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                g2d.drawString(text, 405, 630);
                if (kh.skip) {
                    gameState = 11;
                }
            } else if (gameState == 11) {
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 15F));
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                String text = "Ne mai vedem zombie hunter...";
                g2d.setPaint(Color.white);
                g2d.drawString(text, 125, 250);
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 25F));
            }
            else if(gameState == 12)
            {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 25F));
                g2d.setPaint(Color.white);
                String text = "Esti prea lent.";
                g2d.drawString(text, 405, 630);
                if (dead > 1500) {
                    gameState = 2;
                }
                ++dead;
            }
            g2d.dispose();
        } else if(gameState>2){
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            BufferedImage tileImage = null;
            try {
                tileImage = ImageIO.read(getClass().getResourceAsStream("/textures/japanese pattern4.png"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            TexturePaint tileTexture = new TexturePaint(tileImage, new Rectangle(0, 0, tileImage.getWidth(), tileImage.getHeight()));
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25F));
            g2d.setPaint(tileTexture);
            g2d.fillRect(0, 0, 1080, 720);
            g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 15F));
            String text = "Se pare ca ai luat o pauza de la omorat zombie.";
            g2d.setPaint(Color.white);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
            g2d.drawString(text, 125, 250);
            g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 15F));
            text = "Apasa r ca sa resetezi nivelul.";
            g2d.setPaint(Color.white);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
            g2d.drawString(text, 125, 310);
            text = "Apasa q ca sa salvezi.";
            g2d.setPaint(Color.white);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
            g2d.drawString(text, 125, 370);
            text = "Apasa w ca sa dai load.";
            g2d.setPaint(Color.white);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
            g2d.drawString(text, 125, 430);
            text = "Apasa t ca sa iesi la meniu.";
            g2d.setPaint(Color.white);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
            g2d.drawString(text, 125, 490);
            if(kh.Pressed5)
            {
                gameState = 2;
                paused=false;
            }
            if (kh.Pressed4) {
                music=false;
                stopMusic();
                paused=false;
                currency = 0;
                for (int i = 0; i < 24; ++i) {
                    fr[i] = placed[i] = 0;
                }
                dmg[0]=dmg[1]=dmg[2]=0;
                dead=0;
                zombies[0] = new Zombie(135 * 7, 150 + 135 * 1);
                zombies[1] = new Zombie(135 * 7, 150 + 135 * 0);
                zombies[2] = new Zombie(135 * 7, 150 + 135 * 2, 3000, 5);

                zombies[3] = new Zombie(135 * 7, 150 + 135 * 1);
                zombies[4] = new Zombie(135 * 7, 150 + 135 * 1);
                zombies[5] = new Zombie(135 * 7, 150 + 135 * 0);
                zombies[6] = new Zombie(135 * 7, 150 + 135 * 2);
                zombies[7] = new Zombie(135 * 7, 150 + 135 * 2);
                zombies[24] = new Zombie(135 * 7, 150 + 135 * 0, 3000, 5);
                zombies[25] = new Zombie(135 * 7, 150 + 135 * 0);

                zombies[8] = new Zombie(135 * 7, 150 + 135 * 2);
                zombies[9] = new Zombie(135 * 7, 150 + 135 * 1, 3000, 5, 1);
                zombies[10] = new Zombie(135 * 7, 150 + 135 * 1, 3000, 5, 1);
                zombies[11] = new Zombie(135 * 7, 150 + 135 * 0, 3000, 5);
                zombies[21] = new Zombie(135 * 7, 150 + 135 * 1, 3000, 5, 1);
                zombies[22] = new Zombie(135 * 7, 150 + 135 * 2, 3000, 5);
                zombies[23] = new Zombie(135 * 7, 150 + 135 * 0, 3000, 5);

                zombies[12] = new Zombie(135 * 7, 150 + 135 * 2);
                zombies[13] = new Zombie(135 * 7, 150 + 135 * 1, 3000, 5, 1);
                zombies[14] = new Zombie(135 * 7, 150 + 135 * 1, 3000, 5, 1);
                zombies[15] = new Zombie(135 * 7, 150 + 135 * 0, 3000, 5);
                zombies[16] = new Zombie(135 * 7, 150 + 135 * 2);
                zombies[17] = new Zombie(135 * 7, 150 + 135 * 2);
                zombies[18] = new Zombie(135 * 7, 150 + 135 * 1, 3000, 5, 1);
                zombies[19] = new Zombie(135 * 7, 150 + 135 * 2, 3000, 5);
                zombies[20] = new Zombie(135 * 7, 150 + 135 * 0, 3000, 5);
                fog = true;
                points = 0;
                currency = 0;
            }
            if (kh.Pressed1) {
                String sql = "INSERT INTO MatrixTable (ID, Col1, Col2, Col3, Col4, Col5, Col6, Col7, Col8, Col9, Col10, Col11, Col12, " +
                        "Col13, Col14, Col15, Col16, Col17, Col18, Col19, Col20, Col21, Col22, Col23, Col24, level, points, currency, zombiecount) " +
                        "VALUES (" + (id2++) + ", " +
                        placed[0] + ", " + placed[1] + ", " + placed[2] + ", " + placed[3] + ", " + placed[4] + ", " + placed[5] + ", " +
                        placed[6] + ", " + placed[7] + ", " + placed[8] + ", " + placed[9] + ", " + placed[10] + ", " + placed[11] + ", " +
                        placed[12] + ", " + placed[13] + ", " + placed[14] + ", " + placed[15] + ", " + placed[16] + ", " + placed[17] + ", " +
                        placed[18] + ", " + placed[19] + ", " + placed[20] + ", " + placed[21] + ", " + placed[22] + ", " + placed[23] + ", " +
                        gameState + ", " + points + ", " + currency + ", " + zombiecount + ");";
                Statement stmt = null;
                Connection conn2 = null;
                try {
                    Class.forName("org.sqlite.JDBC");
                    conn2 = DriverManager.getConnection("jdbc:sqlite:db.db");
                    stmt = conn2.createStatement();
                    stmt.executeUpdate(sql);
                    stmt.close();
                    System.out.println("saved state");
                    conn2.close();
                } catch (Exception e) {
                    System.err.println(e.getClass().getName() + " aici1: " + e.getMessage());
                    System.exit(0);
                }
            }
            if (kh.Pressed2 && database2) {
                zombies[0] = new Zombie(135 * 7, 150 + 135 * 1);
                zombies[1] = new Zombie(135 * 7, 150 + 135 * 0);
                zombies[2] = new Zombie(135 * 7, 150 + 135 * 2, 3000, 5);

                zombies[3] = new Zombie(135 * 7, 150 + 135 * 1);
                zombies[4] = new Zombie(135 * 7, 150 + 135 * 1);
                zombies[5] = new Zombie(135 * 7, 150 + 135 * 0);
                zombies[6] = new Zombie(135 * 7, 150 + 135 * 2);
                zombies[7] = new Zombie(135 * 7, 150 + 135 * 2);
                zombies[24] = new Zombie(135 * 7, 150 + 135 * 0, 3000, 5);
                zombies[25] = new Zombie(135 * 7, 150 + 135 * 0);

                zombies[8] = new Zombie(135 * 7, 150 + 135 * 2);
                zombies[9] = new Zombie(135 * 7, 150 + 135 * 1, 3000, 5, 1);
                zombies[10] = new Zombie(135 * 7, 150 + 135 * 1, 3000, 5, 1);
                zombies[11] = new Zombie(135 * 7, 150 + 135 * 0, 3000, 5);
                zombies[21] = new Zombie(135 * 7, 150 + 135 * 1, 3000, 5, 1);
                zombies[22] = new Zombie(135 * 7, 150 + 135 * 2, 3000, 5);
                zombies[23] = new Zombie(135 * 7, 150 + 135 * 0, 3000, 5);

                zombies[12] = new Zombie(135 * 7, 150 + 135 * 2);
                zombies[13] = new Zombie(135 * 7, 150 + 135 * 1, 3000, 5, 1);
                zombies[14] = new Zombie(135 * 7, 150 + 135 * 1, 3000, 5, 1);
                zombies[15] = new Zombie(135 * 7, 150 + 135 * 0, 3000, 5);
                zombies[16] = new Zombie(135 * 7, 150 + 135 * 2);
                zombies[17] = new Zombie(135 * 7, 150 + 135 * 2);
                zombies[18] = new Zombie(135 * 7, 150 + 135 * 1, 3000, 5, 1);
                zombies[19] = new Zombie(135 * 7, 150 + 135 * 2, 3000, 5);
                zombies[20] = new Zombie(135 * 7, 150 + 135 * 0, 3000, 5);
                database2=false;
                Connection c = null;
                Statement stmt = null;
                try {
                    Class.forName("org.sqlite.JDBC");
                    c = DriverManager.getConnection("jdbc:sqlite:db.db");
                    c.setAutoCommit(false);
                    stmt = c.createStatement();
                    ResultSet rs2 = stmt.executeQuery("SELECT * from MatrixTable WHERE id = (SELECT MAX(id) FROM MatrixTable)");
                    int idd = rs2.getInt(1);
                    id2 = idd;
                    for (int i = 0; i < 24; i++) {
                        int j = rs2.getInt(i + 2);
                        placed[i] = j;
                    }
                    gameState = rs2.getInt(26);
                    points = rs2.getInt(27);
                    System.out.println("loaded state");
                } catch (Exception e) {
                    System.err.println(e.getClass().getName() + " aici2: " + e.getMessage());
                }
            }
        }
    }

    Timer timer = new Timer(50, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            animateshot1();
            animatepress();
            animatewalk();
        }
    });
    public void animateshot1() {
        toggleImage = !toggleImage;
        int s1 = 0, s2 = 0, s3 = 0;

        for (int i = 0; i < placed.length; i++) {
            if (placed[i] == 1) {
                if (i < 8) s1 += placed[i];
                else if (i < 16) s2 += placed[i];
                else s3 += placed[i];
            }
        }
        if(zombie[0]>=1)
        {
            dmg[0]+=s1;
        }
        if(zombie[1]>=1)
        {
            dmg[1]+=s2;
        }
        if(zombie[2]>=1)
        {
            dmg[2]+=s3;
        }
        repaint();
    }
    public void animatewalk() {
        sprite++;
        repaint();
    }
    public void animatepress()
    {
        toggleText++;
        repaint();
    }
    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        if(x<=50 && y<=100)
        {
            fr1=!fr1;
            fr2=false;
            fr3=false;
            fr4=false;
        }
        else if(x<=100 && y<=100)
        {
            fr2=!fr2;
            fr1=false;
            fr3=false;
            fr4=false;
        }
        else if(x<=150 && y<=100)
        {
            fr3=!fr3;
            fr1=false;
            fr2=false;
            fr4=false;
        }
        else if(x<=200 && y<=100)
        {
            fr4=!fr4;
            fr3=false;
            fr1=false;
            fr2=false;
        }
        int colIndex = x / 135;
        int rowIndex = (y - 150) / 135;
        if (y >= 150 && colIndex < 8 && rowIndex < 3 && (placed[colIndex + rowIndex * 8]==0 || placed[colIndex + rowIndex * 8]==3)) {
            int index = rowIndex * 8 + colIndex;
            if(fr1 && currency>=250){fr[index]=1; currency-=250;}
            else if(fr2 && currency>=100 ){fr[index]=2; currency-=100;}
            else if(fr3 && currency>=50){fr[index]=3; currency-=50;}
            else if(fr4 && currency>=300){fog=false; currency-=300;}
        }
    }
    @Override
    public void mousePressed(MouseEvent e) {
    }
    @Override
    public void mouseReleased(MouseEvent e) {
    }
    @Override
    public void mouseEntered(MouseEvent e) {
    }
    @Override
    public void mouseExited(MouseEvent e) {
    }
    public void playMenu()
    {
        sound.playM();
    }
    public void playTutorial()
    {
        sound.playT();
    }
    public void playFinale()
    {
        sound.playF();
    }
    public void stopMusic(){sound.stopMusic();}
}