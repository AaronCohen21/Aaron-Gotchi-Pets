//Aaron Cohen

//Computer Science 20

//Henry Wise Wood High School

//2020-2021 Semester 1

//Great help from https://docs.oracle.com/javase/tutorial/uiswing/components/icon.html

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ImageCell implements java.io.Serializable {
	
	private String text;
	private ImageIcon img;
	private Pet pet;
	
	public ImageCell(String text, String imageLocation, Pet pet) {
		this.text = text;
		this.img = new ImageIcon(imageLocation, this.text);
		this.pet = pet;
	}
	
	public ImageCell(String text, String imageLocation, Pet pet, boolean isSrcImg) {
		this.text = text;
		this.img = new ImageIcon(ImageCell.class.getResource(imageLocation), this.text);
		this.pet = pet;
	}

	public Pet getPet() {
		return pet;
	}

	public void setPet(Pet pet) {
		this.pet = pet;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public ImageIcon getImg() {
		return img;
	}

	public void setImg(ImageIcon img) {
		this.img = img;
	}
	
	public JPanel getCellPanel() {
		JLabel label = new JLabel(this.text, new ImageIcon(this.img.getImage().getScaledInstance(32, 32, Image.SCALE_DEFAULT)), JLabel.LEFT);
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		if (this.getPet().getHealth() == 0 && !this.getPet().isPetCreator) {	//if the pet is dead, make the label red
			label.setForeground(Color.RED);
		} else {
			label.setForeground(Color.BLACK);
		}
		panel.add(label);
		return panel;
	}
	
	public JPanel getCellPanel(boolean tool) {
		JLabel label = new JLabel(new ImageIcon(this.img.getImage().getScaledInstance(32, 32, Image.SCALE_DEFAULT)));
		JPanel panel = new JPanel();
		panel.add(label);
		return panel;
	}
	
}
