//Aaron Cohen

//Computer Science 20

//Henry Wise Wood High School

//2020-2021 Semester 1

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.border.LineBorder;

@SuppressWarnings("serial")
public class GUI implements WindowListener, java.io.Serializable, ActionListener {
	
	private JFrame frame;
	private JPanel panel;
	
	@SuppressWarnings("rawtypes")
	private JList pets;
	private Pet petCreator;
	private ImageCell newPet;
	private Object[] petList;
	private ScrollPane scroll;
	
	private JLabel petName;
	private JLabel petImg;
	
	public static JPanel displayPanel;
	private int selectedPet;
	
	private ArrayList<ImageCell> petListArray;
	private int timeMachine;	//time machine is the number of hours the user adds, this feature is purely for demonstration purposes
	private JButton addHour;
	
	private JButton feedButton;
	private JButton playButton;
	private JButton deleteButton;
	private JButton editButton;
	
	private Color selectedColor;
	
	private final ImageIcon ICON = new ImageIcon(new ImageIcon(GUI.class.getResource("/src/img/egg.png")).getImage().getScaledInstance(500, 500, Image.SCALE_DEFAULT));
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public GUI() {
		//try to set the application icon in the taskbar if the OS supports it
		try {
			Taskbar taskbar = Taskbar.getTaskbar();
			taskbar.setIconImage(ICON.getImage());
		} catch (UnsupportedOperationException | SecurityException e) {
			//nothing should be here
		}
		
		//set up constant instance variables
		petCreator = new Pet("New Pet", "/src/img/pencil.png", timeMachine, true);
		newPet = new ImageCell("New Pet", "/src/img/newPet.png", petCreator, true);
		
		//Load pet list and time machine
		try {
			readPetListArrayBin();
			loadTimeMachine();
		} catch (ClassNotFoundException | IOException e) {
			petListArray = new ArrayList<ImageCell>();
			timeMachine = 0;
			JOptionPane.showMessageDialog(frame, "Welcome To Aaron-Gotchi Pets!", "Welcome", JOptionPane.PLAIN_MESSAGE);
			//make directories for the files to be stored in upon saving
			File binDirectory = new File(System.getProperty("user.home") + "/pets/bin");
			binDirectory.mkdirs();
			File imgDirectory = new File(System.getProperty("user.home") + "/pets/img");
			imgDirectory.mkdirs();
		}
		
		//set up frame
		frame = new JFrame();
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(this);
		frame.setTitle("Aaron-Gotchi Pets");
		frame.setSize(743, 390);
		//Add Aaron-Gotchi pets icon
		frame.setIconImage(ICON.getImage());
		
		//set up panel
		panel = new JPanel();
		panel.setLayout(null);
		
		//ScrollPane with Pet List
		scroll = new ScrollPane(ScrollPane.SCROLLBARS_NEVER);
		scroll.setBackground(Color.LIGHT_GRAY);
		scroll.setForeground(Color.LIGHT_GRAY);
		scroll.setBounds(6, 5, 281, 353);
		
		pets = new JList();
		pets.setBackground(Color.LIGHT_GRAY);
		pets.setForeground(Color.LIGHT_GRAY);
		pets.setBounds(scroll.getBounds());
		pets.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, Color.GRAY, Color.DARK_GRAY));
		pets.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		pets.setCellRenderer(new ImageCellRenderer());
		updatePetList();
		//mouse listener for when the user selects a cell on the JList
		pets.addMouseListener(mouseListener);
		//add the pets list to the panel
		scroll.add(pets);
		panel.add(scroll);
		
		//Name
		petName = new JLabel("Aaron-Gotchi Pets", SwingConstants.LEFT);
		petName.setFont(new Font("Lucida Grande", Font.BOLD, 24));
		petName.setBounds(293, 32, 332, 52);
		
		//Image
		petImg = new JLabel("", new ImageIcon(new ImageIcon(GUI.class.getResource("/src/img/egg.png")).getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT)), SwingConstants.CENTER);
		petImg.setFont(new Font("Lucida Grande", Font.BOLD, 24));
		petImg.setBounds(625, 10, 100, 100);
		
		//Set Correct Pet Display and add to panel
		updatePetDisplay(-1);
		panel.add(petImg);
		panel.add(petName);
		
		//seperator for looks
		JSeparator separator = new JSeparator();
		separator.setBackground(Color.LIGHT_GRAY);
		separator.setForeground(Color.GRAY);
		separator.setBounds(293, 118, 444, 17);
		panel.add(separator);
		
		//display/petPanel
		displayPanel = new JPanel();
		displayPanel.setBackground(Color.LIGHT_GRAY);
		displayPanel.setLayout(null);
		displayPanel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, Color.GRAY, Color.DARK_GRAY));
		displayPanel.setBounds(293, 133, 444, 225);
		panel.add(displayPanel);
		
		//initial messages on the display panel
		JLabel welcomeMessage = new JLabel("Welcome To Aaron-Gotchi Pets!");
		welcomeMessage.setBounds(6, 6, 432, 16);
		displayPanel.add(welcomeMessage);
		
		JLabel welcomeMessageLine2 = new JLabel("Select a pet from the menu or create a new one to get started");
		welcomeMessageLine2.setBounds(6, 22, 432, 16);
		displayPanel.add(welcomeMessageLine2);
		
		//finalize the frame
		frame.getContentPane().add(panel);
		frame.setVisible(true);
		
	}	//end of class constructor
	
	private JPanel color1;
	private JPanel color2;
	private JPanel color3;
	private int colorPanelsToAdd;
	
	@SuppressWarnings("unchecked")
	public void displayData(Pet pet) {
		//clear all the components off of the display panel and add new ones below
		displayPanel.removeAll();
		
		//add pet information or display the pet creator
		if (pet.equals(petListArray.get(petListArray.size()-1).getPet())) {
			//for the name
			JLabel nameLabel = new JLabel("Name: ");
			nameLabel.setFont(new Font("Lucida Grande", Font.BOLD, 13));
			nameLabel.setBounds(16, 16, 70, 14);
			displayPanel.add(nameLabel);
			
			JTextField nameInput = new JTextField();
			nameInput.setBounds(59, 13, 150, 20);
			nameInput.setColumns(10);
			displayPanel.add(nameInput);
			
			//for the toolbar
			@SuppressWarnings("rawtypes")
			JList toolList = new JList();
			toolList.setCellRenderer(new ImageCellRenderer());
			toolList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			toolList.setBackground(Color.LIGHT_GRAY);
			toolList.setForeground(Color.LIGHT_GRAY);
			Object[] tools = {new ImageCell("","/src/img/pencil.png", null, true).getCellPanel(true), new ImageCell("","src/img/eraser.png", null, true).getCellPanel(true), new ImageCell("","src/img/eyedropper.png", null, true).getCellPanel(true)};
			toolList.setListData(tools);
			toolList.setBounds(217,13,50,150);
			toolList.setSelectedIndex(0);
			displayPanel.add(toolList);
			
			//for the image of the pet
			JPanel[][] cells = new JPanel[8][8];
			Color[][] pixels = new Color[8][8];
			
			selectedColor = Color.BLACK;
			
			//recent colors panel
			JLabel recentColors = new JLabel("Recent Colors:");
			recentColors.setBounds(16, 118, 100, 14);
			displayPanel.add(recentColors);
			
			colorPanelsToAdd = 1;
			color1 = new JPanel();
			color1.setBounds(16, 135, 50, 50);
			color1.setBackground(selectedColor);
			color1.setBorder(new LineBorder(Color.RED, 3));
			displayPanel.add(color1);
			color1.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent clickedInPanel1) {
					color1.setBorder(new LineBorder(Color.RED,3));	//set the border of this pane to be selected and others to be non, selected
					color2.setBorder(new LineBorder(Color.BLACK,3));
					color3.setBorder(new LineBorder(Color.BLACK,3));
					selectedColor = color1.getBackground();
				}
			});
			
			color2 = new JPanel();
			color2.setBounds(68, 135, 50, 50);
			color2.setBackground(new Color(0,0,0,0));
			color2.setBorder(new LineBorder(Color.BLACK,3));
			color2.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent clickedInPanel2) {
					color2.setBorder(new LineBorder(Color.RED,3));	//set the border of this pane to be selected and others to be non, selected
					color1.setBorder(new LineBorder(Color.BLACK,3));
					color3.setBorder(new LineBorder(Color.BLACK,3));
					selectedColor = color2.getBackground();
				}
			});
			
			color3 = new JPanel();
			color3.setBounds(121, 135, 50, 50);
			color3.setBackground(new Color(0,0,0,0));
			color3.setBorder(new LineBorder(Color.BLACK,3));
			color3.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent clickedInPanel3) {
					color3.setBorder(new LineBorder(Color.RED,3));	//set the border of this pane to be selected and others to be non, selected
					color1.setBorder(new LineBorder(Color.BLACK,3));
					color2.setBorder(new LineBorder(Color.BLACK,3));
					selectedColor = color3.getBackground();
				}
			});
			
			JPanel selectorContainer = new JPanel();
			JColorChooser selector = new JColorChooser();
			selector.setChooserPanels(new AbstractColorChooserPanel[] {selector.getChooserPanels()[2]});
			selector.setPreviewPanel(new JPanel());
			selector.setBounds(10,77,250,137);
			selectorContainer.add(selector);
			
			selector.getSelectionModel().addChangeListener(new ChangeListener(){
				@Override
				public void stateChanged(ChangeEvent colorSet) {
					//update color based on what the user selects
					selectedColor = selector.getColor();
				}
				
			});
			
			JButton chooseColorButton = new JButton("Choose Color");
			chooseColorButton.setBounds(16, 191, 117, 23);
			displayPanel.add(chooseColorButton);
			chooseColorButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent chooseColor) {
					Object[] options = {"OK"};
					JOptionPane.showOptionDialog(frame, selectorContainer, "Choose Color", JOptionPane.PLAIN_MESSAGE, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
					//this code runs after the user clicks ok on the optionpane
					colorPanelsToAdd++;
					if (colorPanelsToAdd == 2) {
						displayPanel.add(color2);
					} else if (colorPanelsToAdd == 3) {
						displayPanel.add(color3);
					}
					color3.setBackground(color2.getBackground());
					color3.setBorder(new LineBorder(Color.BLACK,3));
					color2.setBackground(color1.getBackground());
					color2.setBorder(new LineBorder(Color.BLACK,3));
					color1.setBackground(selectedColor);
					color1.setBorder(new LineBorder(Color.RED,3));
				}
			});
			
			for (int x = 0; x < cells.length; x++) {
				for (int y = 0; y < cells[x].length; y++) {
					//re-define x and y positions so the MouseAdapter has access to it
					int xPos = x;
					int yPos = y;
					
					//set up the JPanel and add add all cells with MouseAdapters
					cells[x][y] = new JPanel();
					cells[x][y].setBounds(274+(x*20), 13+(y*20), 20, 20);
					cells[x][y].setBorder(new LineBorder(new Color(0, 0, 0)));
					cells[x][y].setBackground(new Color(0,0,0,0));
					pixels[x][y] = new Color(0,0,0,0);
					displayPanel.add(cells[x][y]);
					cells[x][y].addMouseListener(new MouseAdapter(){
						@Override
						public void mousePressed(MouseEvent clickedOnCell) {
							if (toolList.getSelectedIndex() == 0) {	//pen tool
								cells[xPos][yPos].setBackground(selectedColor);
								pixels[xPos][yPos] = selectedColor;
							} else if (toolList.getSelectedIndex() == 1) {	//eraser tool
								cells[xPos][yPos].setBackground(new Color(0,0,0,0));
								pixels[xPos][yPos] = new Color(0,0,0,0);
							} else if (toolList.getSelectedIndex() == 2) {	//eye dropper tool
								Color colorToSet = cells[xPos][yPos].getBackground();
								if (!colorToSet.equals(new Color(0,0,0,0))) {	//if the selected color isn't an empty pixel
									selectedColor = cells[xPos][yPos].getBackground();
									colorPanelsToAdd++;
									if (colorPanelsToAdd == 2) {
										displayPanel.add(color2);
									} else if (colorPanelsToAdd == 3) {
										displayPanel.add(color3);
									}
									color3.setBackground(color2.getBackground());
									color3.setBorder(new LineBorder(Color.BLACK,3));
									color2.setBackground(color1.getBackground());
									color2.setBorder(new LineBorder(Color.BLACK,3));
									color1.setBackground(selectedColor);
									color1.setBorder(new LineBorder(Color.RED,3));
								}
							}
						}
					});
				}
			}
			
			JButton createButton = new JButton("Create Pet");
			createButton.setBounds(335, 191, 99, 23);
			displayPanel.add(createButton);
			createButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent petCreated) {
					//create the image
					BufferedImage image = new BufferedImage(8,8, BufferedImage.TYPE_INT_ARGB);
					for (int x = 0; x < cells.length; x++) {
						for (int y = 0; y < cells[x].length; y++) {
							image.setRGB(x, y, cells[x][y].getBackground().getRGB());
						}
					}
					
					//save the image as a png file
					File imageOutput = new File(System.getProperty("user.home") + "/pets/img/" + nameInput.getText() + ".png");
					try {
						ImageIO.write(image, "png", imageOutput);
					} catch (IOException e) {}
					
					petListArray.add(0, new ImageCell(nameInput.getText(), System.getProperty("user.home") + "/pets/img/" + nameInput.getText() + ".png", new Pet(nameInput.getText(), System.getProperty("user.home") + "/pets/img/" + nameInput.getText() + ".png", timeMachine)));
					updatePetList();
					updatePetDisplay(0);
					pets.setSelectedIndex(0);
					petListArray.get(0).getPet().imageArray = pixels;
				}
			});
			
			
		} else {	//pet information
			pet.softUpdate(timeMachine);	//update the pet to calculate pet information
			//age
			JLabel ageLabel = new JLabel("Age: ");
			ageLabel.setFont(new Font("Lucida Grande", Font.BOLD, 13));
			ageLabel.setBounds(6, 6, 34, 16);
			displayPanel.add(ageLabel);
			
			JLabel ageInformation = new JLabel(pet.age(timeMachine) + " Days Old");
			if (pet.age(timeMachine) == 1) {
				ageInformation.setText(pet.age(timeMachine) + " Day Old");	//if the pet is 1 day old, remove the s
			} else if (pet.age(timeMachine) < 1) {	//display time in hours if pet is less than one day old
				if (pet.ageInHours(timeMachine) == 1) {
					ageInformation.setText(pet.ageInHours(timeMachine) + " Hour Old");
				} else {
					ageInformation.setText(pet.ageInHours(timeMachine) + " Hours Old");
				}
			}
			ageInformation.setBounds(40, 6, 398, 16);
			displayPanel.add(ageInformation);
			
			//health
			JLabel healthLabel = new JLabel("Health: ");
			healthLabel.setFont(new Font("Lucida Grande", Font.BOLD, 13));
			healthLabel.setBounds(6, 34, 51, 16);
			displayPanel.add(healthLabel);
			
			//hearts
			ImageIcon heart = new ImageIcon(new ImageIcon(GUI.class.getResource("/src/img/heart.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT));
			
			JLabel heart1 = new JLabel(heart);
			heart1.setBounds(57, 34, 16, 16);
			if (pet.getHealth() < 1) {
				heart1.setEnabled(false);
			}
			displayPanel.add(heart1);
			
			JLabel heart2 = new JLabel(heart);
			heart2.setBounds(76, 34, 16, 16);
			if (pet.getHealth() < 2) {
				heart2.setEnabled(false);
			}
			displayPanel.add(heart2);
			
			JLabel heart3 = new JLabel(heart);
			heart3.setBounds(95, 34, 16, 16);
			if (pet.getHealth() < 3) {
				heart3.setEnabled(false);
			}
			displayPanel.add(heart3);
			
			JLabel heart4 = new JLabel(heart);
			heart4.setBounds(114, 34, 16, 16);
			if (pet.getHealth() < 4) {
				heart4.setEnabled(false);
			}
			displayPanel.add(heart4);
			
			JLabel heart5 = new JLabel(heart);
			heart5.setBounds(133, 34, 16, 16);
			if (pet.getHealth() < 5) {
				heart5.setEnabled(false);
			}
			displayPanel.add(heart5);
			
			//food
			JLabel foodLabel = new JLabel("Food: ");
			foodLabel.setFont(new Font("Lucida Grande", Font.BOLD, 13));
			foodLabel.setBounds(6, 62, 41, 16);
			displayPanel.add(foodLabel);
			
			feedButton = new JButton("Feed");
			feedButton.setBounds(81, 190, 75, 29);
			if (pet.isAlive()) {
				displayPanel.add(feedButton);
			}
			feedButton.addActionListener(this);
			
			//apples
			ImageIcon apple = new ImageIcon(new ImageIcon(GUI.class.getResource("/src/img/apple.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT));
			
			JLabel apple1 = new JLabel(apple);
			apple1.setBounds(46, 62, 16, 16);
			if (pet.getFood() < 1) {
				apple1.setEnabled(false);
			}
			displayPanel.add(apple1);
			
			JLabel apple2 = new JLabel(apple);
			apple2.setBounds(65, 62, 16, 16);
			if (pet.getFood() < 2) {
				apple2.setEnabled(false);
			}
			displayPanel.add(apple2);
			
			JLabel apple3 = new JLabel(apple);
			apple3.setBounds(84, 62, 16, 16);
			if (pet.getFood() < 3) {
				apple3.setEnabled(false);
			}
			displayPanel.add(apple3);
			
			JLabel apple4 = new JLabel(apple);
			apple4.setBounds(103, 62, 16, 16);
			if (pet.getFood() < 4) {
				apple4.setEnabled(false);
			}
			displayPanel.add(apple4);
			
			JLabel apple5 = new JLabel(apple);
			apple5.setBounds(122, 62, 16, 16);
			if (pet.getFood() < 5) {
				apple5.setEnabled(false);
			}
			displayPanel.add(apple5);
			
			//Happiness
			JLabel happinessLabel = new JLabel("Happiness: ");
			happinessLabel.setFont(new Font("Lucida Grande", Font.BOLD, 13));
			happinessLabel.setBounds(6, 90, 78, 16);
			displayPanel.add(happinessLabel);
			
			//stars
			ImageIcon star = new ImageIcon(new ImageIcon(GUI.class.getResource("/src/img/star.png")).getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT));
			
			JLabel star1 = new JLabel(star);
			star1.setBounds(84, 90, 16, 16);
			if (pet.getHappiness() < 1) {
				star1.setEnabled(false);
			}
			displayPanel.add(star1);
			
			JLabel star2 = new JLabel(star);
			star2.setBounds(103, 90, 16, 16);
			if (pet.getHappiness() < 2) {
				star2.setEnabled(false);
			}
			displayPanel.add(star2);
			
			JLabel star3 = new JLabel(star);
			star3.setBounds(122, 90, 16, 16);
			if (pet.getHappiness() < 3) {
				star3.setEnabled(false);
			}
			displayPanel.add(star3);
			
			JLabel star4 = new JLabel(star);
			star4.setBounds(141, 90, 16, 16);
			if (pet.getHappiness() < 4) {
				star4.setEnabled(false);
			}
			displayPanel.add(star4);
			
			JLabel star5 = new JLabel(star);
			star5.setBounds(160, 90, 16, 16);
			if (pet.getHappiness() < 5) {
				star5.setEnabled(false);
			}
			displayPanel.add(star5);
			
			playButton = new JButton("Play");
			playButton.setBounds(6, 190, 75, 29);
			if (pet.isAlive()) {
				displayPanel.add(playButton);
			}
			playButton.addActionListener(this);
			
			//time machine
			addHour = new JButton("Add Hour");
			addHour.setBounds(156, 190, 104, 29);
			addHour.addActionListener(this);
			if (pet.isAlive()) {
				displayPanel.add(addHour);
			}
			
			//Edit Pet Button
			editButton = new JButton("Edit Pet");
			editButton.setBounds(347, 6, 91, 29);
			if (pet.isAlive()) {
				displayPanel.add(editButton);
			}
			editButton.addActionListener(this);
			
			//Delete Pet Button
			deleteButton = new JButton("Delete Pet");
			deleteButton.setBounds(331, 190, 107, 29);
			displayPanel.add(deleteButton);
			deleteButton.addActionListener(this);
		}
		
		//refresh the panel to display proper information
		displayPanel.repaint();
	}
	
	private static Timer messageTimer;
	private static JLabel cantFeedPet;
	private static boolean removeMessage;
	public static void displayPanelMessage(String message) {
		
		removeMessage = false;
		
		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("Timer")) {
					
					if (!removeMessage) {
						cantFeedPet = new JLabel(message);
						cantFeedPet.setBounds(12, 172, 354, 16);
						cantFeedPet.setForeground(Color.RED);
						displayPanel.add(cantFeedPet);
						displayPanel.repaint();
						removeMessage = true;
					} else {
						displayPanel.remove(cantFeedPet);
						displayPanel.repaint();
						messageTimer.stop();
					}
					
				}
			}
		};
		
		messageTimer = new Timer(2000, listener);
		messageTimer.setInitialDelay(0);
		messageTimer.setActionCommand("Timer");
		messageTimer.start();
	}
	
	private void updatePetDisplay(int petIndex) {
		try {
			//remove the animationListener if the user switches pets while playing with their pet
			petListArray.get(petIndex).getPet().softUpdate(timeMachine);
			//set new name
			petName.setText(petListArray.get(petIndex).getPet().getPetName());
			if (petListArray.get(petIndex).getPet().getHealth() == 0 && !petListArray.get(petIndex).getPet().isPetCreator) {	//if the pet is dead make the name red and add (dead)
				petName.setForeground(Color.RED);
				updatePetList();
			} else {
				petName.setForeground(Color.BLACK);
			}
			//get icon and scale image
			ImageIcon unscaled = petListArray.get(petIndex).getPet().getPetImg();
			Icon scaled = new ImageIcon(unscaled.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT));
			//set new icon
			petImg.setIcon(scaled);
			//add the petPanel
			displayData(petListArray.get(petIndex).getPet());
		} catch (Exception NoPetSelectedException) {
			//nothing is supposed to happen here but i have to have the catch block here anyways
		}
	}
	
	@SuppressWarnings("unchecked")
	private void updatePetList() {
		/*
		 * when creating the Object[] array make sure to use the ImageCell.getCellPanel()
		 * method or it will return with an error
		 */
		readPetListArray();
		//set the JList data
		pets.setListData(petList);
	}
	
	//the code to handle when the user clicks on one of the options in the JList
	private MouseListener mouseListener = new MouseAdapter() {
		public void mouseClicked(MouseEvent e) {
			try {
				int lastPet = selectedPet;
				if (petListArray.get(lastPet).getPet().finishedPlaying) {
					selectedPet = pets.getSelectedIndex();
					//Make an ArrayList with the same values that are in the petList
					updatePetDisplay(selectedPet);
				} else {
					pets.setSelectedIndex(lastPet);
				}
			} catch (Exception petWasNotPlayingException) {
				selectedPet = pets.getSelectedIndex();
				//Make an ArrayList with the same values that are in the petList
				updatePetDisplay(selectedPet);
			}
		}
	};
	
	//IO
	private void writePetListArrayBin() throws IOException {
		FileOutputStream out = new FileOutputStream(System.getProperty("user.home") + "/pets/bin/petList.bin");
		ObjectOutputStream s = new ObjectOutputStream(out);
		s.writeObject(petListArray);
		s.close();
		out.close();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void readPetListArrayBin() throws IOException, ClassNotFoundException {
		FileInputStream in = new FileInputStream(System.getProperty("user.home") + "/pets/bin/petList.bin");
		ObjectInputStream s = new ObjectInputStream(in);
	    petListArray = (ArrayList) s.readObject();
	    s.close();
	    in.close();
	}
	
	private void readPetListArray() {
		//create petList
	    if (petListArray.size() == 0) {
	    	petListArray.add(newPet);
	    	petList = new Object[petListArray.size()];
	    } else {
	    	petList = new Object[petListArray.size()];
	    }
	    for (int i = 0; i < petListArray.size(); i++) {
	    	if (petListArray.get(i).getPet().getHealth() == 0 && petListArray.get(i).getPet().isAlive() && !petListArray.get(i).getPet().isPetCreator) {	//if the pet is dead, add (dead) to the pet name
	    		petListArray.get(i).getPet().setPetName(petListArray.get(i).getPet().getPetName() + " (dead)");
	    		petListArray.get(i).setText(petListArray.get(i).getText() + " (dead)");
	    		petListArray.get(i).getPet().kill(timeMachine);
	    		updatePetDisplay(selectedPet);
	    	}
	    	petList[i] = petListArray.get(i).getCellPanel();
	    }
	}
	
	private void loadTimeMachine() throws IOException, ClassNotFoundException {
		FileInputStream in = new FileInputStream(System.getProperty("user.home") + "/pets/bin/timeMachine.bin");
		ObjectInputStream s = new ObjectInputStream(in);
	    timeMachine = (int) s.readObject();
	    s.close();
	    in.close();
	}
	
	private void saveTimeMachine() throws IOException {
		FileOutputStream out = new FileOutputStream(System.getProperty("user.home") + "/pets/bin/timeMachine.bin");
		ObjectOutputStream s = new ObjectOutputStream(out);
		s.writeObject(timeMachine);
		s.close();
		out.close();
	}
	
	//load GUI
	public static void main(String[] args) {
		new GUI();
	}
	
	private Timer waitForFinished;
	@SuppressWarnings("unchecked")
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(addHour)) {
			timeMachine++;
			updatePetDisplay(selectedPet);
		} else if (e.getSource().equals(feedButton)) {
			//when the user feeds the pet
			petListArray.get(selectedPet).getPet().addFood(timeMachine);
			updatePetDisplay(selectedPet);
		} else if (e.getSource().equals(playButton)) {
			//when the user plays with the pet
			petListArray.get(selectedPet).getPet().play(timeMachine);
			waitForFinished = new Timer(500, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (petListArray.get(selectedPet).getPet().finishedPlaying) {
						waitForFinished.stop();
						updatePetDisplay(selectedPet);
					}
					
				}
				
			});
			waitForFinished.start();
		} else if (e.getSource().equals(deleteButton)) {
			//code to delete the pet
			petListArray.remove(selectedPet);	//remove the pet from the list
			
			//reset the displayPanel to its default state
			//add welcome messages
			displayPanel.removeAll();
			JLabel welcomeMessage = new JLabel("Welcome To Aaron-Gotchi Pets!");
			welcomeMessage.setBounds(6, 6, 432, 16);
			displayPanel.add(welcomeMessage);
			
			JLabel welcomeMessageLine2 = new JLabel("Select a pet from the menu or create a new one to get started");
			welcomeMessageLine2.setBounds(6, 22, 432, 16);
			displayPanel.add(welcomeMessageLine2);
			
			//refresh panel content
			displayPanel.repaint();
			
			//reset header text, image, and text color
			petName.setForeground(Color.BLACK);
			petName.setText("Aaron-Gotchi Pets");
			petImg.setIcon(new ImageIcon(new ImageIcon(GUI.class.getResource("/src/img/egg.png")).getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT)));
			
			//update pet list to remove deleted pet
			updatePetList();
		} else if (e.getSource().equals(editButton)) {
			displayPanel.removeAll();
			//for the name
			JLabel nameLabel = new JLabel("Name: ");
			nameLabel.setFont(new Font("Lucida Grande", Font.BOLD, 13));
			nameLabel.setBounds(16, 16, 70, 14);
			displayPanel.add(nameLabel);
			
			JTextField nameInput = new JTextField();
			nameInput.setBounds(59, 13, 150, 20);
			nameInput.setColumns(10);
			displayPanel.add(nameInput);
			nameInput.setText(petListArray.get(selectedPet).getText());
			
			//for the toolbar
			@SuppressWarnings("rawtypes")
			JList toolList = new JList();
			toolList.setCellRenderer(new ImageCellRenderer());
			toolList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			toolList.setBackground(Color.LIGHT_GRAY);
			toolList.setForeground(Color.LIGHT_GRAY);
			Object[] tools = {new ImageCell("","/src/img/pencil.png", null, true).getCellPanel(true), new ImageCell("","src/img/eraser.png", null, true).getCellPanel(true), new ImageCell("","src/img/eyedropper.png", null, true).getCellPanel(true)};
			toolList.setListData(tools);
			toolList.setBounds(217,13,50,150);
			toolList.setSelectedIndex(0);
			displayPanel.add(toolList);
			
			//for the image of the pet
			JPanel[][] cells = new JPanel[8][8];
			Color[][] updatedArray = petListArray.get(selectedPet).getPet().imageArray;
			
			selectedColor = Color.BLACK;
			
			//recent colors panel
			JLabel recentColors = new JLabel("Recent Colors:");
			recentColors.setBounds(16, 118, 100, 14);
			displayPanel.add(recentColors);
			
			colorPanelsToAdd = 1;
			color1 = new JPanel();
			color1.setBounds(16, 135, 50, 50);
			color1.setBackground(selectedColor);
			color1.setBorder(new LineBorder(Color.RED, 3));
			displayPanel.add(color1);
			color1.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent clickedInPanel1) {
					color1.setBorder(new LineBorder(Color.RED,3));	//set the border of this pane to be selected and others to be non, selected
					color2.setBorder(new LineBorder(Color.BLACK,3));
					color3.setBorder(new LineBorder(Color.BLACK,3));
					selectedColor = color1.getBackground();
				}
			});
			
			color2 = new JPanel();
			color2.setBounds(68, 135, 50, 50);
			color2.setBackground(new Color(0,0,0,0));
			color2.setBorder(new LineBorder(Color.BLACK,3));
			color2.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent clickedInPanel2) {
					color2.setBorder(new LineBorder(Color.RED,3));	//set the border of this pane to be selected and others to be non, selected
					color1.setBorder(new LineBorder(Color.BLACK,3));
					color3.setBorder(new LineBorder(Color.BLACK,3));
					selectedColor = color2.getBackground();
				}
			});
			
			color3 = new JPanel();
			color3.setBounds(121, 135, 50, 50);
			color3.setBackground(new Color(0,0,0,0));
			color3.setBorder(new LineBorder(Color.BLACK,3));
			color3.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent clickedInPanel3) {
					color3.setBorder(new LineBorder(Color.RED,3));	//set the border of this pane to be selected and others to be non, selected
					color1.setBorder(new LineBorder(Color.BLACK,3));
					color2.setBorder(new LineBorder(Color.BLACK,3));
					selectedColor = color3.getBackground();
				}
			});
			
			JPanel selectorContainer = new JPanel();
			JColorChooser selector = new JColorChooser();
			selector.setChooserPanels(new AbstractColorChooserPanel[] {selector.getChooserPanels()[2]});
			selector.setPreviewPanel(new JPanel());
			selector.setBounds(10,77,250,137);
			selectorContainer.add(selector);
			
			selector.getSelectionModel().addChangeListener(new ChangeListener(){
				@Override
				public void stateChanged(ChangeEvent colorSet) {
					//update color based on what the user selects
					selectedColor = selector.getColor();
				}
				
			});
			
			JButton chooseColorButton = new JButton("Choose Color");
			chooseColorButton.setBounds(16, 191, 117, 23);
			displayPanel.add(chooseColorButton);
			chooseColorButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent chooseColor) {
					Object[] options = {"OK"};
					JOptionPane.showOptionDialog(frame, selectorContainer, "Choose Color", JOptionPane.PLAIN_MESSAGE, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
					//this code runs after the user clicks ok on the optionpane
					colorPanelsToAdd++;
					if (colorPanelsToAdd == 2) {
						displayPanel.add(color2);
					} else if (colorPanelsToAdd == 3) {
						displayPanel.add(color3);
					}
					color3.setBackground(color2.getBackground());
					color3.setBorder(new LineBorder(Color.BLACK,3));
					color2.setBackground(color1.getBackground());
					color2.setBorder(new LineBorder(Color.BLACK,3));
					color1.setBackground(selectedColor);
					color1.setBorder(new LineBorder(Color.RED,3));
				}
			});
			
			for (int x = 0; x < cells.length; x++) {
				for (int y = 0; y < cells[x].length; y++) {
					//re-define x and y positions so the MouseAdapter has access to it
					int xPos = x;
					int yPos = y;
					
					//set up the JPanel and add add all cells with MouseAdapters
					cells[x][y] = new JPanel();
					cells[x][y].setBounds(274+(x*20), 13+(y*20), 20, 20);
					cells[x][y].setBorder(new LineBorder(new Color(0, 0, 0)));
					cells[x][y].setBackground(petListArray.get(selectedPet).getPet().imageArray[x][y]);
					displayPanel.add(cells[x][y]);
					cells[x][y].addMouseListener(new MouseAdapter(){
						@Override
						public void mousePressed(MouseEvent clickedOnCell) {
							if (toolList.getSelectedIndex() == 0) {	//pen tool
								cells[xPos][yPos].setBackground(selectedColor);
								updatedArray[xPos][yPos] = selectedColor;
							} else if (toolList.getSelectedIndex() == 1) {	//eraser tool
								cells[xPos][yPos].setBackground(new Color(0,0,0,0));
								updatedArray[xPos][yPos] = new Color(0,0,0,0);
							} else if (toolList.getSelectedIndex() == 2) {	//eye dropper tool
								Color colorToSet = cells[xPos][yPos].getBackground();
								if (!colorToSet.equals(new Color(0,0,0,0))) {	//if the selected color isn't an empty pixel
									selectedColor = cells[xPos][yPos].getBackground();
									colorPanelsToAdd++;
									if (colorPanelsToAdd == 2) {
										displayPanel.add(color2);
									} else if (colorPanelsToAdd == 3) {
										displayPanel.add(color3);
									}
									color3.setBackground(color2.getBackground());
									color3.setBorder(new LineBorder(Color.BLACK,3));
									color2.setBackground(color1.getBackground());
									color2.setBorder(new LineBorder(Color.BLACK,3));
									color1.setBackground(selectedColor);
									color1.setBorder(new LineBorder(Color.RED,3));
								}
							}
							
						}
					});
				}
			}

			JButton cancelButton = new JButton("Cancel");
			cancelButton.setBounds(224, 191, 77, 23);
			displayPanel.add(cancelButton);
			cancelButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					updatePetDisplay(selectedPet);
				}
				
			});
			
			JButton saveChangesButton = new JButton("Save Changes");
			saveChangesButton.setBounds(311, 191, 123, 23);
			displayPanel.add(saveChangesButton);
			saveChangesButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent petChanged) {
					//create the image
					BufferedImage image = new BufferedImage(8,8, BufferedImage.TYPE_INT_ARGB);
					for (int x = 0; x < cells.length; x++) {
						for (int y = 0; y < cells[x].length; y++) {
							image.setRGB(x, y, cells[x][y].getBackground().getRGB());
						}
					}
					
					//save the image as a png file
					File imageOutput = new File(System.getProperty("user.home") + "/pets/img/" + nameInput.getText() + ".png");
					try {
						ImageIO.write(image, "png", imageOutput);
					} catch (IOException e) {}
					
					petListArray.get(selectedPet).setText(nameInput.getText());
					petListArray.get(selectedPet).setImg(new ImageIcon(System.getProperty("user.home") + "/pets/img/" + nameInput.getText() + ".png"));
					petListArray.get(selectedPet).getPet().setPetName(nameInput.getText());
					petListArray.get(selectedPet).getPet().setPetImgPath(System.getProperty("user.home") + "/pets/img/" + nameInput.getText() + ".png");
					updatePetList();
					pets.setSelectedIndex(selectedPet);
					updatePetDisplay(selectedPet);
					petListArray.get(selectedPet).getPet().imageArray = updatedArray;
				}
			});
			displayPanel.repaint();
		}
	}
	
	@Override
	public void windowClosing(WindowEvent e) {	//code to save petListArray to a bin file
		try {
			petListArray.get(selectedPet).getPet().finishedPlaying = true;
			writePetListArrayBin();
			saveTimeMachine();
		} catch (IOException e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(frame, "Error Saving Pet Information");
		}
	}
	
	//useless from here on but this all has to be here or I get errors
	@Override
	public void windowOpened(WindowEvent e) {

		
	}
	
	@Override
	public void windowClosed(WindowEvent e) {

		
	}

	@Override
	public void windowIconified(WindowEvent e) {

		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {

		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		
	}
}
