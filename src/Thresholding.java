/*
 * Thresholding (IBV assignment)
 * By Laurens Holst, 0122173
 */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.JOptionPane;

public class Thresholding extends Frame implements WindowListener, ActionListener, ItemListener {
	
	static final long serialVersionUID = 7390209216841389153L;
	
	MenuBar mb;
	Menu mFile, mView;
	MenuItem miFileOpen, miFileSave, miFileExit;
	MenuItem miViewSetSegments;
	CheckboxMenuItem miViewOriginal, miViewGrayscale, miViewBlurred, miViewStandard, miViewImproved;
	CheckboxMenuItem miViewHistogram;
	ImageCanvas ic;
	
	public enum View { ORIGINAL, GRAYSCALE, BLURRED, STANDARD, IMPROVED };
	private View view = View.GRAYSCALE;
	private boolean viewHistogram = false;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length > 0) {
			new Thresholding(args[0]);
		} else {
			new Thresholding(null);
		}
	}
	
	public Thresholding(String filename) {
		this.setTitle("Thresholding — IBV, Laurens Holst, 0122173");
		this.setSize(1000, 700);
		this.addWindowListener(this);
		
		mb = new MenuBar();
		mFile = new Menu("File");
		miFileOpen = new MenuItem("Open...", new MenuShortcut(KeyEvent.VK_O));
		miFileSave = new MenuItem("Save images", new MenuShortcut(KeyEvent.VK_S));
		miFileExit = new MenuItem("Exit", new MenuShortcut(KeyEvent.VK_X));
		miFileOpen.addActionListener(this);
		miFileSave.addActionListener(this);
		miFileExit.addActionListener(this);
		mFile.add(miFileOpen);
		mFile.add(miFileSave);
		mFile.add(new MenuItem("-"));
		mFile.add(miFileExit);
		mb.add(mFile);
		mView = new Menu("View");
		miViewSetSegments = new MenuItem("Set number of segments...");
		miViewOriginal = new CheckboxMenuItem("Original");
		miViewGrayscale = new CheckboxMenuItem("Grayscale");
		miViewBlurred = new CheckboxMenuItem("Blurred");
		miViewStandard = new CheckboxMenuItem("Standard thresholding");
		miViewImproved = new CheckboxMenuItem("Improved thresholding");
		miViewHistogram = new CheckboxMenuItem("Overlay histogram and thresholds", viewHistogram);
		miViewSetSegments.addActionListener(this);
		miViewOriginal.addItemListener(this);
		miViewGrayscale.addItemListener(this);
		miViewBlurred.addItemListener(this);
		miViewStandard.addItemListener(this);
		miViewImproved.addItemListener(this);
		miViewHistogram.addItemListener(this);
		mView.add(miViewSetSegments);
		mView.add(new MenuItem("-"));
		mView.add(miViewOriginal);
		mView.add(miViewGrayscale);
		mView.add(miViewBlurred);
		mView.add(miViewStandard);
		mView.add(miViewImproved);
		mView.add(new MenuItem("-"));
		mView.add(miViewHistogram);
		updateViewMenu();
		mb.add(mView);
		this.setMenuBar(mb);
		
		if (filename == null) {
			ic = new ImageCanvas(this);
		} else {
			ic = new ImageCanvas(new File(filename), this);
		}
		this.add(ic);
		
		this.setVisible(true);
	}
	
	// WindowListener methodes
	/**
	 * WindowClosing: Dispose het window.
	 */
	public void windowClosing(WindowEvent e) {
		this.dispose();
	}
	
	public void windowActivated(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == miFileOpen)
		{
			FileDialog fd = new FileDialog(this,
					"Please choose the file to open:",
					FileDialog.LOAD);
			fd.setVisible(true);
			if (fd.getFile() != null) {
				File f = new File(fd.getDirectory(), fd.getFile());
				ic.load(f);
			}
		}
		else if (e.getSource() == miFileSave)
		{
			ic.save();
		}
		else if (e.getSource() == miFileExit)
		{
			this.dispose();
		}
		else if (e.getSource() == miViewSetSegments)
		{
			try {
				int segments = Integer.parseInt(JOptionPane.showInputDialog(this, "Number of segments:", ic.K));
				if (segments > 1) {
					ic.initialize();
				} else {
					System.err.println("*** Minimum number of segments must be 1");
				}
			} catch (NumberFormatException nfe) {
				System.err.println("*** Invalid threshold number");
			}
		}
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == miViewOriginal)
		{
			setView(View.ORIGINAL);
		}
		if (e.getSource() == miViewGrayscale)
		{
			setView(View.GRAYSCALE);
		}
		if (e.getSource() == miViewBlurred)
		{
			setView(View.BLURRED);
		}
		else if (e.getSource() == miViewStandard)
		{
			setView(View.STANDARD);
		}
		else if (e.getSource() == miViewImproved)
		{
			setView(View.IMPROVED);
		}
		else if (e.getSource() == miViewHistogram)
		{
			setViewHistogram(!isViewHistogram());
		}
	}
	
	public void updateViewMenu() {
		miViewOriginal.setState(view == View.ORIGINAL);
		miViewGrayscale.setState(view == View.GRAYSCALE);
		miViewBlurred.setState(view == View.BLURRED);
		miViewStandard.setState(view == View.STANDARD);
		miViewImproved.setState(view == View.IMPROVED);
	}
	
	public void setView(View view) {
		this.view = view;
		updateViewMenu();
		ic.repaint();
	}
	
	public View getView() {
		return this.view;
	}

	public boolean isViewHistogram() {
		return viewHistogram;
	}

	public void setViewHistogram(boolean viewHistogram) {
		this.viewHistogram = viewHistogram;
		miViewHistogram.setState(viewHistogram);
		ic.repaint();
	}

}
