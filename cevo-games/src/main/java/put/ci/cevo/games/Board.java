package put.ci.cevo.games;

public interface Board {

	public static final int WHITE = -1;
	public static final int BLACK = 1;
	public static final int EMPTY = 0;
	
	public int getValue(int row, int col);
	public void setValue(int row, int col, int color);

	public int getSize();
	public int countPieces(int color);

	public boolean isEmpty(int row, int col);

}
