package com.jhe.hexed;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

/**
 * Adapted from http://sourceforge.net/projects/jhecomponent/
 */
public class JHexEditor extends JPanel implements FocusListener, AdjustmentListener, MouseWheelListener {
	private static final long serialVersionUID = 1L;
	byte[] buff;
	public int cursor;
	protected static Font font = new Font("Courier", 0, 12);
	protected int border = 2;
	public boolean DEBUG = false;
	private JPanel panel;
	private JScrollBar sb;
	private int start = 0;
	private int lines = 10;

	public JHexEditor(byte[] buff) {
		super();
		this.addMouseWheelListener(this);
		setBytes(buff);
	}
	
	public void setBytes(byte[] buff) {
		this.buff = buff;
		removeAll();
		
		sb = new JScrollBar(JScrollBar.VERTICAL);
		sb.addAdjustmentListener(this);
		sb.setMinimum(0);
		sb.setMaximum(buff.length / getLines());
		
		JPanel p1, p2, p3;
		// center
		p1 = new JPanel(new BorderLayout(1, 1));
		p1.setBackground(Color.white);
		p1.setBorder(BorderFactory.createLineBorder(Color.gray));
		p1.add(new JHexEditorHEX(this), BorderLayout.CENTER);
		p1.add(new Columns(), BorderLayout.NORTH);

		// left
		p2 = new JPanel(new BorderLayout(1, 1));
		p2.setBackground(Color.white);
		p2.add(new Rows(), BorderLayout.CENTER);
		p2.add(new Box(""), BorderLayout.NORTH);

		// right
		p3 = new JPanel(new BorderLayout(1, 1));
		p3.setBackground(Color.white);
		p3.add(sb, BorderLayout.EAST);
		p3.add(new JHexEditorASCII(this), BorderLayout.CENTER);
		p3.add(new Box("ASCII"), BorderLayout.NORTH);

		panel = new JPanel();
		panel.setBackground(Color.white);
		panel.setLayout(new BorderLayout(1, 1));
		panel.add(p1, BorderLayout.CENTER);
		panel.add(p2, BorderLayout.WEST);
		panel.add(p3, BorderLayout.EAST);
		

		this.setLayout(new BorderLayout(1, 1));
		this.add(panel, BorderLayout.CENTER);
		setBackground(Color.white);
		revalidate();
		repaint();
	}
	
	public void paint(Graphics g) {
		sb.setValues(getStart(), +getLines(), 0, buff.length / 16);
		sb.setValueIsAdjusting(true);
		super.paint(g);
	}

	protected void updateCursor() {
		int n = (cursor / 16);

		if (n < start)
			start = n;
		else if (n >= start + lines)
			start = n - (lines - 1);

		repaint();
	}

	protected int getStart() {
		return start;
	}

	protected int getLines() {
		return lines;
	}

	protected void bottom(Graphics g, int x, int y, int s) {
		((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		FontMetrics fn = getFontMetrics(font);
		g.fillRect(((fn.stringWidth(" ") + 1) * x) + border, (fn.getHeight() * y) + border,
				((fn.stringWidth(" ") + 1) * s), fn.getHeight() + 1);
	}

	protected void frame(Graphics g, int x, int y, int s) {
		((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		FontMetrics fn = getFontMetrics(font);
		g.drawRect(((fn.stringWidth(" ") + 1) * x) + border, (fn.getHeight() * y) + border,
				((fn.stringWidth(" ") + 1) * s), fn.getHeight() + 1);
	}

	protected void printString(Graphics g, String s, int x, int y) {
		((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		FontMetrics fn = getFontMetrics(font);
		g.drawString(s, ((fn.stringWidth(" ") + 1) * x) + border,
				((fn.getHeight() * (y + 1)) - fn.getMaxDescent()) + border);
	}

	public void focusGained(FocusEvent e) {
		this.repaint();
	}

	public void focusLost(FocusEvent e) {
		this.repaint();
	}

	public void adjustmentValueChanged(AdjustmentEvent e) {
		start = e.getValue();
		if (start < 0)
			start = 0;
		repaint();
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		start += (e.getUnitsToScroll());
		if ((start + lines) >= buff.length / 16)
			start = (buff.length / 16) - lines;
		if (start < 0)
			start = 0;
		repaint();
	}

	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case 33: // rep
			if (cursor >= (16 * lines))
				cursor -= (16 * lines);
			updateCursor();
			break;
		case 34: // fin
			if (cursor < (buff.length - (16 * lines)))
				cursor += (16 * lines);
			updateCursor();
			break;
		case 35: // fin
			cursor = buff.length - 1;
			updateCursor();
			break;
		case 36: // ini
			cursor = 0;
			updateCursor();
			break;
		case 37: // <--
			if (cursor != 0)
				cursor--;
			updateCursor();
			break;
		case 38: // <--
			if (cursor > 15)
				cursor -= 16;
			updateCursor();
			break;
		case 39: // -->
			if (cursor != (buff.length - 1))
				cursor++;
			updateCursor();
			break;
		case 40: // -->
			if (cursor < (buff.length - 16))
				cursor += 16;
			updateCursor();
			break;
		}
	}

	private class Columns extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = -1734199617526339842L;

		public Columns() {
			this.setLayout(new BorderLayout(1, 1));
		}

		public Dimension getPreferredSize() {
			return getMinimumSize();
		}

		public Dimension getMinimumSize() {
			Dimension d = new Dimension();
			FontMetrics fn = getFontMetrics(font);
			int h = fn.getHeight();
			int nl = 1;
			d.setSize(((fn.stringWidth(" ") + 1) * +((16 * 3) - 1)) + (border * 2) + 1, h * nl + (border * 2) + 1);
			return d;
		}

		public void paint(Graphics g) {
			Dimension d = getMinimumSize();
			g.setColor(Color.white);
			g.fillRect(0, 0, d.width, d.height);
			g.setColor(Color.black);
			g.setFont(font);
			((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			for (int n = 0; n < 16; n++) {
				if (n == (cursor % 16))
					frame(g, n * 3, 0, 2);
				String s = "00" + Integer.toHexString(n);
				s = s.substring(s.length() - 2);
				printString(g, s, n * 3, 0);
			}
		}
	}

	private class Box extends JLabel {
		private static final long serialVersionUID = -6124062720565016834L;

		public Box(String text) {
			super(text);
			setHorizontalAlignment(JLabel.CENTER);
		}
		
		public Dimension getPreferredSize() {
			return getMinimumSize();
		}

		public Dimension getMinimumSize() {
			Dimension d = new Dimension();
			FontMetrics fn = getFontMetrics(font);
			int h = fn.getHeight();
			d.setSize((fn.stringWidth(" ") + 1) + (border * 2) + 1, h + (border * 2) + 1);
			return d;
		}
	}

	private class Rows extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 8797347523486018051L;

		public Rows() {
			this.setLayout(new BorderLayout(1, 1));
		}

		public Dimension getPreferredSize() {
			return getMinimumSize();
		}

		public Dimension getMinimumSize() {
			Dimension d = new Dimension();
			FontMetrics fn = getFontMetrics(font);
			int h = fn.getHeight();
			int nl = getLines();
			d.setSize((fn.stringWidth(" ") + 1) * (8) + (border * 2) + 1, h * nl + (border * 2) + 1);
			return d;
		}

		public void paint(Graphics g) {
			Dimension d = getMinimumSize();
			g.setColor(Color.white);
			g.fillRect(0, 0, d.width, d.height);
			g.setColor(Color.black);
			g.setFont(font);

			int ini = getStart();
			int fin = ini + getLines();
			int y = 0;
			for (int n = ini; n < fin; n++) {
				if (n == (cursor / 16))
					frame(g, 0, y, 8);
				String s = "0000000000000" + Integer.toHexString(n);
				s = s.substring(s.length() - 8);
				printString(g, s, 0, y++);
			}
		}
	}

}
