package put.ci.cevo.games.othello;

import put.ci.cevo.games.encodings.WPC;

/**
 * WPC with Margin
 * 
 * @author wojciech
 * 
 */
public class OthelloWPC {
	final public double buffer[];

	public OthelloWPC(WPC wpc) {
		assert (wpc.getSize() == OthelloBoard.SIZE * OthelloBoard.SIZE);
		buffer = new double[OthelloBoard.WIDTH * OthelloBoard.WIDTH];

		for (int r = 0; r < OthelloBoard.SIZE; ++r) {
			for (int c = 0; c < OthelloBoard.SIZE; ++c) {
				double value = wpc.get(r * OthelloBoard.SIZE + c);
				buffer[(r + 1) * OthelloBoard.WIDTH + (c + 1)] = value;
			}
		}
	}

	public double getValue(int row, int col) {
		assert (0 <= row && row < OthelloBoard.SIZE);
		assert (0 <= col && col < OthelloBoard.SIZE);
		return buffer[(row + 1) * OthelloBoard.WIDTH + col + 1];
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("  ");
		for (int c = 0; c < OthelloBoard.SIZE; c++) {
			builder.append(" " + (char) ('A' + c) + "     ");
		}
		builder.append("\n");

		for (int r = 0; r < OthelloBoard.SIZE; r++) {
			builder.append(r + " ");
			for (int c = 0; c <  OthelloBoard.SIZE; c++) {
				builder.append(String.format("%6.3f ", getValue(r, c)));
			}
			builder.append("\n");
		}
		return builder.toString();
	}
}
