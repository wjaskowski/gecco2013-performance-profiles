package put.ci.cevo.games.othello;

import java.util.Arrays;

import org.apache.commons.lang.NotImplementedException;

import put.ci.cevo.games.Board;

import com.carrotsearch.hppc.IntArrayList;

public class OthelloBoard implements Board {
	
	public static final int SIZE = 8;
	public static final int MARGIN = 2;
	public static final int WIDTH = SIZE + MARGIN;
	public static final int BUFFER_SIZE = WIDTH * WIDTH;

	public static final int WALL = -2;

	public static final int DIRS[] = { -WIDTH - 1, -WIDTH, -WIDTH + 1, -1, +1, WIDTH - 1, WIDTH, WIDTH + 1 };

	public final int[] buffer = new int[BUFFER_SIZE];

	public OthelloBoard() {
		reset();
	}

	public OthelloBoard(int[][] board) {
		initMargins();
		for (int r = 0; r < board.length; r++)
			for (int c = 0; c < board[r].length; c++)
				setValue(r, c, board[r][c]);
	}

	public void reset() {
		Arrays.fill(buffer, Board.EMPTY);
		initMargins();
		initBoard();
	}

	private void initMargins() {
		for (int i = 0; i < WIDTH; ++i) {
			setValueInternal(0, i, WALL);
			setValueInternal(WIDTH - 1, i, WALL);
			setValueInternal(i, 0, WALL);
			setValueInternal(i, WIDTH - 1, WALL);
		}
	}

	private void initBoard() {
		setValue(3, 3, Board.WHITE);
		setValue(3, 4, Board.BLACK);
		setValue(4, 4, Board.WHITE);
		setValue(4, 3, Board.BLACK);
	}

	@Override
	public int countPieces(int color) {
		int count = 0;
		for (int row = 0; row < buffer.length; row++)
			if (buffer[row] == color)
				count++;
		return count;
	}

	@Override
	public int getSize() {
		return SIZE;
	}

	@Override
	public void setValue(int row, int col, int color) {
		buffer[toPos(row, col)] = color;
	}

	public void setValueInternal(int row, int col, int color) {
		buffer[toPosInternal(row, col)] = color;
	}
	
	public int getValueInternal(int row, int col) {
		return buffer[toPosInternal(row, col)];
	}

	@Override
	public int getValue(int row, int col) {
		return buffer[toPos(row, col)];
	}

	@Override
	public boolean isEmpty(int row, int col) {
		return isEmpty(toPos(row, col));
	}

	boolean isEmpty(int pos) {
		return buffer[pos] == EMPTY;
	}

	boolean isValid(int pos) {
		return buffer[pos] != WALL;
	}

	
	public IntArrayList simulateMove(int move, int player) {
		assert (isEmpty(move));
		IntArrayList positionsChanged = new IntArrayList();

		for (int i = 0; i < DIRS.length; i++) {
			int dir = DIRS[i];
			int pos = move + dir;

			while (buffer[pos] == getOpponent(player)) {
				pos += dir;
			}

			if (buffer[pos] == player) {
				pos -= dir;
				while (buffer[pos] == getOpponent(player)) {
					positionsChanged.add(pos);
					pos -= dir;
				}
			}
		}

		boolean moveIsValid = positionsChanged.size() > 0;
		if (!moveIsValid)
			return null;
		positionsChanged.add(move);
		return positionsChanged;
	}

	public static int getOpponent(int player) {
		return -player;
	}

	public void makeMove(int move, int color) {
		IntArrayList piecesToMove = simulateMove(move, color);
		makeMove(piecesToMove, color);
	}

	private void makeMove(IntArrayList piecesToMove, int color) {
		for (int i = 0; i < piecesToMove.size(); i++) {
			int pos = piecesToMove.buffer[i];
			buffer[pos] = color;
		}
	}

	private int toPos(int row, int col) {
		return (row + 1) * WIDTH + (col + 1);
	}

	private int toPosInternal(int row, int col) {
		return row * WIDTH + col;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("  ");
		for (int i = 0; i < getSize(); i++) {
			builder.append((char) ('A' + i));
		}
		builder.append("\n");

		for (int r = 0; r < getSize(); r++) {
			builder.append(r + 1 + " ");
			for (int c = 0; c < getSize(); c++) {
				builder.append(pieceToChar(getValue(r, c)));
			}
			builder.append("\n");
		}
		return builder.toString();
	}

	private char pieceToChar(int piece) {
		if (piece == EMPTY)
			return '-';
		if (piece == BLACK)
			return 'b';
		if (piece == WHITE)
			return 'w';

		assert (false);
		return '?';
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		Board another = (Board) obj;
		if (getSize() != another.getSize())
			return false;
		for (int r = 0; r < getSize(); ++r)
			for (int c = 0; c < getSize(); ++c)
				if (getValue(r, c) != another.getValue(r, c))
					return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		throw new NotImplementedException();		
	}
	
	public String toJavaArrayString() {
		StringBuilder str = new StringBuilder();
		str.append("new int[][] {\n");
		for (int r = 0; r < SIZE; r++) {
			str.append("{");
			for (int c = 0; c < SIZE; c++) {
				str.append(getValue(r,c) + ",");
			}
			str.append("},\n");
		}
		str.append("}");
		return str.toString();
	}

	public boolean isWall(int pos) {
		return buffer[pos] == WALL;
	}
}
