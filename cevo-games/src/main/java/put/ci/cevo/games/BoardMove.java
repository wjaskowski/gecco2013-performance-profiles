package put.ci.cevo.games;

public class BoardMove {

	public final int row;
	public final int col;

	public BoardMove(int row, int col) {
		this.row = row;
		this.col = col;
	}

	@Override
	public String toString() {
		return "" + (char) ('A' + col - 1) + row;
	}

	@Override
	public int hashCode() {
		return row ^ col;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		BoardMove other = (BoardMove) obj;
		return col == other.col && row == other.row;
	}
}
