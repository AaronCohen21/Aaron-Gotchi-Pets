//Aaron Cohen

//Computer Science 20

//Henry Wise Wood High School

//2020-2021 Semester 1

/*
 * Credit to https://alvinalexander.com/java/jlist-image-jlabel-renderer/ for some of the code
 * Really helpful guide to setting up this class for the CellRenderer and explaining how it all works
 */

import java.awt.Color;
import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;

@SuppressWarnings("rawtypes")
public class ImageCellRenderer implements ListCellRenderer{

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		
		if (value instanceof JPanel) {
	      Component component = (Component) value;
	      component.setForeground (Color.LIGHT_GRAY);
	      component.setBackground (isSelected ? Color.GRAY : Color.LIGHT_GRAY);
	      ((JComponent) component).setBorder(isSelected ? new SoftBevelBorder(BevelBorder.LOWERED, Color.DARK_GRAY, Color.BLACK) : null);
	      return component;
	    }
	    
		else {
	      return new JLabel("Error: Use ImageCell.getCellPanel()");
	    }
	}
}
	
	


