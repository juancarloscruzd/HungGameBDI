package pe.edu.utec.hung.ui;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;



public class FrameView {
	public static JFrame f = new JFrame();
	public static JLabel gameStateLabel = new JLabel(new ImageIcon("resources/images/loader.gif"));
	public static JLabel selectedWordLabel = new JLabel("Selected word: ");
	public static JLabel progressLabel = new JLabel("Progress: ");
	public static JLabel attemptLabel = new JLabel("Player 1 attempt: ");

	public static void init() {
		f = new JFrame("Hung Game");
		JPanel panel = new JPanel();
		gameStateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		selectedWordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		progressLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		attemptLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.setPreferredSize(new Dimension(400, 400));
		BoxLayout boxlayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
		panel.setLayout(boxlayout);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameStateLabel.setPreferredSize(new Dimension(400, 400));
		panel.add(gameStateLabel);
		panel.add(selectedWordLabel);
		panel.add(attemptLabel);
		panel.add(progressLabel);
		
		
		f.add(panel);
		f.setSize(500, 500);
		f.pack();
		f.setVisible(true);
	}

	public void changeImage(String ImagePath) {
		FrameView.gameStateLabel.setIcon(new ImageIcon(ImagePath));
	}
}
