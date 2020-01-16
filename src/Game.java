import java.awt.*;
import javax.swing.JFrame;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class Game extends Canvas implements Runnable {

    private static final long serialVersionUID = 1L;
    private Thread thread; // permite multiplas thread, varias tarefas sendo executada ao mesmo tempo
    private boolean isRunning = false;
    private BufferedImage bImage;

    public static JFrame frame;
    // tamnho da janela
    private final int widht = 160, height = 120, scale = 4;

    public Game() {
        this.setPreferredSize(new Dimension(widht * scale, height * scale));
        InitFrame();
        bImage = new BufferedImage(widht, height, BufferedImage.TYPE_INT_RGB);
    }

    // Parte gráfica da janela do game
    public void InitFrame() {
        frame = new JFrame();
        frame.add(this); // adicona tudo ao frame
        frame.setResizable(false); // evita q a janela seja redimencionada
        frame.pack(); // re-calcula o tamanho da tela
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // fecha de vez a aplicação, não a deixa rodar em 2º plano
        frame.setLocationRelativeTo(null); // inicializa a janela no meio da tela
        frame.setVisible(true);
    }

    public synchronized void start() {
        thread = new Thread(this);
        isRunning = true;
        thread.start();
    }

    public synchronized void stop() {
        isRunning = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(final String[] args) {
        final Game game = new Game();
        game.start();
    }

    // inicialização grafica
    public void Render() {
        final BufferStrategy bStrategy = this.getBufferStrategy();
        if (bStrategy == null) {
            this.createBufferStrategy(2); // usado para otmizar a renderização
            return;
        }

        Graphics graphics = bImage.getGraphics();
        graphics.setColor(Color.BLUE);
        graphics.fillRect(0, 0, widht, height);

        // renderizando string
        graphics.setFont(new Font("Arial", Font.BOLD, 16));
        graphics.setColor(Color.white);
        graphics.drawString("Olá", 10, 10);

        // imagem principal
        graphics = bStrategy.getDrawGraphics();
        graphics.drawImage(bImage, 0, 0, widht * scale, height * scale, null);

        bStrategy.show();

    }

    // atualizações do game
    public void Update() {

    }

    @Override
    public void run() {

        // Game Looping
        long lastTime = System.nanoTime();
        final double amountOfTicks = 60.0;
        final double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        int frame = 0;
        double timer = System.currentTimeMillis();

        while (isRunning) {
            final long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            // verifica se passou 1 segundo antes de atulizar
            if (delta >= 1) {
                Update();
                Render();
                frame++;
                delta--;
            }

            // verifica se passou 1 segundo antes de mostrar o fps
            if (System.currentTimeMillis() - timer >= 1000) {
                System.out.println("FPS: " + frame);
                frame = 0;
                timer += 1000;
            }
        }

        stop();

    }

}