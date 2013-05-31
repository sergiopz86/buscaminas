import java.util.Random;

public class CampoMinas {

	public static final short MINA = 0x0100;		// la posición contiene una mina
	public static final short DESCUBIERTA = 0x0200;	// la posición ha sido descubierta
	public static final short MARCADA = 0x0400;		// la posición está marcada

	private short[][] cm;
	private int filas;
	private int columnas;
	private int numMinas;

	public CampoMinas(int filas, int columnas, int minas) {
		cm = new short[this.filas = filas][this.columnas = columnas];
		this.numMinas = minas;
		reset();
	}
	
	public void reset() {
		Random r = new Random();
		int f;
		int c;

		for (f = 0; f < filas; f++)
			for (c = 0; c < columnas; c++)
				cm[f][c] = 0;

		for (int i = 0; i < numMinas; i++) {
			do {
				f = r.nextInt(filas);
				c = r.nextInt(columnas);
			} while (!addMina(f, c));
		}
	}

	private boolean addMina(int fil, int col) {
		if (!tieneMina(fil, col)) {
			cm[fil][col] |= MINA;
			for (int f = fil - 1; f <= fil + 1; f++)
				for (int c = col - 1; c <= col + 1; c++)
					if (f != fil || c != col)
						try {
							cm[f][c]++;
						} catch (IndexOutOfBoundsException e) {
						}
			return true;
		}
		return true;
	}

	public short getNumMinas(int fil, int col) {
		return (short) (cm[fil][col] & 0x000f);
	}

	public short getNumMarcadas(int fil, int col) {
		return (short) ((cm[fil][col] & 0x00f0) >> 4);
	}
	
	public boolean tieneMina(int fil, int col) {
		return (cm[fil][col] & MINA) == MINA;
	}

	public boolean estaMarcada(int fil, int col) {
		return (cm[fil][col] & MARCADA) == MARCADA;
	}

	public boolean estaDescubierta(int fil, int col) {
		return (cm[fil][col] & DESCUBIERTA) == DESCUBIERTA;
	}

	public boolean descubrir(int fil, int col) {
		if (!estaMarcada(fil, col) && !estaDescubierta(fil, col)) {
			cm[fil][col] |= DESCUBIERTA;
			if (tieneMina(fil, col))
				return true;
			if (getNumMinas(fil, col) == 0)
				for (int f = fil - 1; f <= fil + 1; f++)
					for (int c = col - 1; c <= col + 1; c++)
						try {
							if ((f != fil || c != col) && !estaDescubierta(f, c))
								descubrir(f, c);
						} catch (IndexOutOfBoundsException e) {
						}
		}
		return false;
	}

	public boolean descubrirTodas(int fil, int col) {
		boolean mina = false;
		if (estaDescubierta(fil, col) && getNumMarcadas(fil, col) == getNumMinas(fil, col)) {
			for (int i = fil - 1; i <= fil + 1; i++)
				for (int j = col - 1; j <= col + 1; j++)
					if (i != fil || j != col)
						try {
							mina |= descubrir(i, j);
						} catch (IndexOutOfBoundsException e) {
						}
		}
		return mina;
	}

	public void conmutarMarca(int fil, int col) {
		if (!estaDescubierta(fil, col)) {
			cm[fil][col] ^= MARCADA;
			boolean marcada = estaMarcada(fil, col);
			for (int f = fil - 1; f <= fil + 1; f++)
				for (int c = col - 1; c <= col + 1; c++)
					if (f != fil || c != col)
					try {
						cm[f][c] += marcada ? 0x0010 : -0x0010;
					} catch (IndexOutOfBoundsException e) {
					}
		}
	}

}
