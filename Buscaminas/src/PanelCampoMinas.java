import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class PanelCampoMinas extends JPanel implements MouseListener,
		MouseMotionListener, ActionListener {

	private static final long serialVersionUID = -463874613837610463L;
	private CampoMinas cm;
	private int filas;
	private int columnas;
	private BufferedImage imgC;
	private BufferedImage imgE;
	private int anchoCasilla;
	private int altoCasilla;
	private boolean finPartida = false;
	private int fp;
	private int cp;
	private boolean btn1 = false;
	private boolean btn3 = false;
	private int dxe;

	public PanelCampoMinas(int filas, int columnas, int minas) {
		try {
			imgC = ImageIO.read(getClass().getResource("/img/casillas.png"));
			imgE = ImageIO.read(getClass().getResource("/img/explosion.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		anchoCasilla = imgC.getWidth() / 13;
		altoCasilla = imgC.getHeight();
		setPreferredSize(new Dimension(anchoCasilla * columnas, altoCasilla
				* filas));
		cm = new CampoMinas(this.filas = filas, this.columnas = columnas, minas);
		addMouseListener(this);
		addMouseMotionListener(this);
		registerKeyboardAction(this, "iniciar",
				KeyStroke.getKeyStroke(KeyEvent.VK_I, 0),
				JComponent.WHEN_IN_FOCUSED_WINDOW);
	}

	protected boolean alrededor(int fa, int ca, int f, int c) {
		return ((f != fa || c != ca) && fa >= f - 1 && fa <= f + 1
				&& ca >= c - 1 && ca <= c + 1);
	}

	@Override
	protected void paintComponent(Graphics g) {
		int minas;
		boolean mina;
		boolean marcada;
		boolean descubierta;
		int x;
		int y;
		int dx;
		for (int f = 0; f < filas; f++)
			for (int c = 0; c < columnas; c++) {
				minas = cm.getNumMinas(f, c);
				mina = cm.tieneMina(f, c);
				marcada = cm.estaMarcada(f, c);
				descubierta = cm.estaDescubierta(f, c);
				x = c * anchoCasilla;
				y = f * altoCasilla;

				if (finPartida && descubierta && mina)
					g.drawImage(imgE, x, y, x + anchoCasilla, y + altoCasilla,
							dxe, 0, dxe + anchoCasilla, altoCasilla, this);
				else {
					if (marcada)
						dx = finPartida && !mina ? 12 * anchoCasilla
								: 10 * anchoCasilla;
					else if (descubierta)
						dx = minas * anchoCasilla;
					else if (!finPartida
							&& ((f == fp && c == cp && btn1) || (alrededor(f,
									c, fp, cp) && btn1 && btn3)))
						dx = 0;
					else
						dx = finPartida && mina ? 11 * anchoCasilla
								: 9 * anchoCasilla;

					g.drawImage(imgC, x, y, x + anchoCasilla, y + altoCasilla,
							dx, 0, dx + anchoCasilla, altoCasilla, this);
				}
			}
	}

	private void explotarMina() {
		Thread t = new Thread() {
			@Override
			public void run() {
				synchronized (PanelCampoMinas.this) {
					while (dxe < imgE.getWidth() - anchoCasilla && finPartida) {
						repaint();
						try {
							Thread.sleep(25);
						} catch (InterruptedException e) {
						}
						dxe += anchoCasilla;
					}
					repaint();
				}
			}
		};
		t.start();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (!finPartida) {
			int f = e.getY() / altoCasilla;
			int c = e.getX() / anchoCasilla;
			if (f != fp || c != cp) {
				fp = f;
				cp = c;
				repaint();
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (!finPartida) {
			fp = e.getY() / altoCasilla;
			cp = e.getX() / anchoCasilla;
			if (e.getButton() == MouseEvent.BUTTON3) {
				btn3 = true;
				if (!btn1)
					cm.conmutarMarca(fp, cp);
			} else if (e.getButton() == MouseEvent.BUTTON1) 
				btn1 = true;
			if (btn3 || (btn1 && btn3)) repaint();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (!finPartida) {
			int f = e.getY() / altoCasilla;
			int c = e.getX() / anchoCasilla;
			if (btn1 && !btn3)
				finPartida = cm.descubrir(f, c);
			else if (btn1 && btn3)
				finPartida = cm.descubrirTodas(f, c);
			btn1 = btn3 = false;
			if (finPartida)
				explotarMina();
			else
				repaint();
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("iniciar") && finPartida) {
			cm.reset();
			finPartida = btn1 = btn3 = false;
			synchronized (this) { dxe = 0; }
			repaint();
		}
	}
}
