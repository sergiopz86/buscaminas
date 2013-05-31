import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class Splash extends JFrame{
	
	private static final long	serialVersionUID = -5642324963632524387L;
	private PanelSplash			p;
	
	public Splash() {
		setUndecorated(true);
		add(p = new PanelSplash());
		pack();
		Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((scr.width - getWidth()) / 2, (scr.height - getHeight()) / 2);
	}
	
	public void explotarMinas() { p.explotarMinas(); }
	
	private class PanelSplash extends JPanel{
		
		private class Mina {
			private int fil;
			private int col;
			private int dx;
			
			public Mina(int fil, int col) {
				this.fil = fil;
				this.col = col;
				dx = 0;
			}
			
			@Override
			public boolean equals(Object o) {
				return (fil == ((Mina) o).fil && col == ((Mina) o).col);
			}
		}
		
		private static final long	serialVersionUID = -3421063293579386723L;
		private static final int	F = 5;
		private static final int	C = 8;
		private BufferedImage		imgMina;
		private BufferedImage		imgSplash;
		private Vector<Mina>		se = new Vector<Mina>();
		private Random				r = new Random();
		
		public PanelSplash() {
			try {
				imgMina = ImageIO.read(getClass().getResource("/img/mina.png"));
				imgSplash = ImageIO.read(getClass().getResource("/img/splash.png"));
			} catch (IOException e) { e.printStackTrace(); }
			
			setPreferredSize(new Dimension(C * 64, F * 64));
			
			Mina mina;
			int n = F * C - 10;
			for (int i=0; i<n; i++){
				do {
					mina = new Mina(r.nextInt(F), r.nextInt(C));
				} while (se.contains(mina));
				se.addElement(mina);
			}
		}
		
		
		@Override
		public synchronized void paint(Graphics g) {
			Mina m;
			int x;
			int y;
			boolean activar = true;
			
			g.drawImage(imgSplash, 0, 0, this);
			Enumeration<Mina> e = se.elements();
			while (e.hasMoreElements()) {
				m = e.nextElement();
				x = m.col * 64;
				y = m.fil * 64;
				g.drawImage(imgMina, x, y, x + 64, y + 64, m.dx, 0, m.dx + 64, 64, this);
				if (activar && m.dx == 0) {
					m.dx += 64;
					activar = false;
				}
				else if (m.dx > 0 && m.dx < imgMina.getWidth() && (m.dx += 64) == imgMina.getWidth())
					se.removeElement(m);
			}
		}
		
		public void explotarMinas() {
			do {
				repaint();
				try { Thread.sleep(50); } catch (InterruptedException ex) {}
			} while (!se.isEmpty());	
		}
	}

}
