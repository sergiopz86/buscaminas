import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;


public class Buscaminas extends JFrame {

	private static final long serialVersionUID = -4852502814204828613L;
	
	public Buscaminas() {
		super("Buscaminas");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		this.setResizable(false);
		add(new PanelCampoMinas(16, 30, 100), BorderLayout.CENTER);
		pack();
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((d.width - getWidth()) / 2, (d.height - getHeight()) / 2 );
	}

	public static void main(String[] args) {
		Splash splash = new Splash();
		splash.setVisible(true);
		try { Thread.sleep(1000); } catch (InterruptedException e) {}
		splash.explotarMinas();
		try { Thread.sleep(1000); } catch (InterruptedException e) {}
		splash.dispose();
		new Buscaminas().setVisible(true);
	}
}
