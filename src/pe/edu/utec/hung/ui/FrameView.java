package pe.edu.utec.hung.ui;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class FrameView {
	public static JFrame f = new JFrame();
	public static JLabel myLabel = new JLabel(new ImageIcon("resources/images/loader.gif"));

	public static void init() {
		f = new JFrame("Hung Game");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setLocation(30, 30);
		f.add(myLabel);
		f.setSize(400, 400);
		f.pack();
		f.setVisible(true);
	}

	public void changeImage(String ImagePath) {
		FrameView.myLabel.setIcon(new ImageIcon(ImagePath));
	}
}
