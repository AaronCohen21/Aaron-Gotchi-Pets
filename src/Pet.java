//Aaron Cohen

//Computer Science 20

//Henry Wise Wood High School

//2020-2021 Semester 1

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;
import AppPackage.AnimationClass;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@SuppressWarnings("serial")
public class Pet implements java.io.Serializable {
	
	public boolean isPetCreator;
	
	private String petName;
	private ImageIcon petImg;
	private String petImgPath;
	
	private LocalDateTime birthday;
	private LocalDateTime deathDay;
	
	private int health;
	private int tempHealth;
	private int food;
	private LocalDateTime lastFed;
	private int tempFood;
	private boolean alive;
	
	private int happiness;
	private int tempHappiness;
	private LocalDateTime lastPlayed;
	
	public Color[][] imageArray;
	
	public Pet(String petName, String petImgPath) {
		this.petName = petName;
		this.petImgPath = petImgPath;
		this.petImg = new ImageIcon(petImgPath, petName);
		this.birthday = LocalDateTime.now();
		this.health = 5;
		this.tempHealth = 5;
		this.food = 5;
		this.tempFood = 5;
		this.happiness = 5;
		this.tempHappiness = 5;
		this.lastFed = LocalDateTime.now();
		this.lastPlayed = LocalDateTime.now();
		this.alive = true;
		this.finishedPlaying = true;
	}
	
	public Pet(String petName, String petImgPath, int timeMachineAdjustment) {
		this.petName = petName;
		this.petImgPath = petImgPath;
		this.petImg = new ImageIcon(petImgPath, petName);
		this.birthday = LocalDateTime.now().plusHours(timeMachineAdjustment);
		this.health = 5;
		this.tempHealth = 5;
		this.food = 5;
		this.tempFood = 5;
		this.happiness = 5;
		this.tempHappiness = 5;
		this.lastFed = LocalDateTime.now().plusHours(timeMachineAdjustment);
		this.lastPlayed = LocalDateTime.now().plusHours(timeMachineAdjustment);
		this.alive = true;
		this.finishedPlaying = true;
	}
	
	public Pet(String petName, String petImgPath, int timeMachineAdjustment, boolean isPetCreator) {
		this.petName = petName;
		this.petImgPath = petImgPath;
		this.petImg = new ImageIcon(Pet.class.getResource(petImgPath), petName);
		this.birthday = LocalDateTime.now().plusHours(timeMachineAdjustment);
		this.health = 5;
		this.tempHealth = 5;
		this.food = 5;
		this.tempFood = 5;
		this.happiness = 5;
		this.tempHappiness = 5;
		this.lastFed = LocalDateTime.now().plusHours(timeMachineAdjustment);
		this.lastPlayed = LocalDateTime.now().plusHours(timeMachineAdjustment);
		this.alive = true;
		this.finishedPlaying = true;
		
		this.isPetCreator = isPetCreator;
	}
	
	/*
	 * TODO - IMPORTANT: Never set this.alive to false, if the pet is supposed to be dead,
	 * set this.health to 0, for the GUI to display accurate information, it must be able to read
	 * that the health is at 0, update information, and call the kill() method so it knows that
	 * everything has been properly updated to register the pet as dead.
	 * 
	 * If this.alive = false, it means that in the system the pet has been registered as dead.
	 * Manually setting this.alive to false will not register the pet as dead.
	 */
	
	public void softUpdate(int timeMachineAdjustment) {
		//if the pet has gone an hour without being fed, remove one hunger point for each hour it hasn't been fed
		this.tempFood = this.food - (int) lastFed.until(LocalDateTime.now().plusHours(timeMachineAdjustment), ChronoUnit.HOURS);
		this.tempHappiness = this.happiness - (int) lastPlayed.until(LocalDateTime.now().plusHours(timeMachineAdjustment), ChronoUnit.HOURS);
		
		//update tempHealth
		if (this.tempHappiness <= 0) {
			this.tempHealth = this.health - (((int) Math.abs(tempHappiness/5)) + 1);
		}
		
		//if the pet is dead from starvation
		if (this.tempFood <= 0) {
			this.food = 0;
			this.tempFood = 0;
			this.health = 0;
			this.tempHealth = 0;
			this.happiness = 0;
			this.tempHappiness = 0;
		}
		
		//if the pet is dead from not being happy
		if (this.tempHappiness <= -25) {
			this.food = 0;
			this.tempFood = 0;
			this.health = 0;
			this.tempHealth = 0;
			this.happiness = 0;
			this.tempHappiness = 0;
		}
		
		//if the pet is dead from dying
		if (this.tempHealth <= 0) {
			this.food = 0;
			this.tempFood = 0;
			this.health = 0;
			this.tempHealth = 0;
			this.happiness = 0;
			this.tempHappiness = 0;
		}
		
	}
	
	public int getHappiness() {
		if (!this.alive) {
			return 0;
		}
		int calculate = tempHappiness;
		
		while (calculate <= 0) {
			calculate += 5;
		}
		
		return calculate;
	}
	
	public int getFood() {
		return tempFood;
	}
	
	public void addFood(int timeMachineAdjustment) {
		if (this.getFood() < 5 && (int) lastFed.until(LocalDateTime.now().plusHours(timeMachineAdjustment), ChronoUnit.MINUTES) >= 30) {
			food = this.getFood() + 1;
			lastFed = LocalDateTime.now().plusHours(timeMachineAdjustment);
		} else if (food < 5 && lastFed.until(LocalDateTime.now().plusHours(timeMachineAdjustment), ChronoUnit.MINUTES) < 30){
			//pet is still hungry but cannot eat
			String cantFeedPet = "You Cannot Feed Your Pet For Another " + (30 - (int) lastFed.until(LocalDateTime.now().plusHours(timeMachineAdjustment), ChronoUnit.MINUTES)) + " Minutes";
			GUI.displayPanelMessage(cantFeedPet);
		} else {
			//pet is not hungry
			String cantFeedPet = "Your Pet Is Not Hungry";
			GUI.displayPanelMessage(cantFeedPet);
		}
	}
	
	private int currentPlayAmount;
	public boolean finishedPlaying;
	private boolean inAnimation;
	
	public void play(int timeMachineAdjustment) {
		if (this.getHappiness() < 5 && (int) lastPlayed.until(LocalDateTime.now().plusHours(timeMachineAdjustment), ChronoUnit.MINUTES) >= 30) {
			//so the GUI class doesn't get rid of the panel immediately
			finishedPlaying = false;
			inAnimation = false;
			
			//clear the components from the display panel
			GUI.displayPanel.removeAll();
			//add the new components here
			JLabel ground = new JLabel(new ImageIcon(new ImageIcon(Pet.class.getResource("/src/img/ground.png")).getImage().getScaledInstance(438, 78, Image.SCALE_DEFAULT)));
			ground.setBounds(3, 147, 438, 78);
			GUI.displayPanel.add(ground);
			
			ImageIcon ball = new ImageIcon(new ImageIcon(Pet.class.getResource("/src/img/ball.png")).getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT));
			
			JLabel ballLabel = new JLabel(ball);
			ballLabel.setBounds(78, 97, 50, 50);
			GUI.displayPanel.add(ballLabel);
			
			JLabel petLabel = new JLabel(new ImageIcon(this.getPetImg().getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT)));
			petLabel.setBounds(28, 97, 50, 50);
			GUI.displayPanel.add(petLabel);
			
			//refresh the display panel with the new components
			GUI.displayPanel.repaint();
			
			//for the animation and then adding the happiness point to the pet, see the MouseAdapter below
			currentPlayAmount = 0;
			int totalPlayAmount = ((int) (Math.random() * 3)) + 3;	//random number between 3 and 5
			AnimationClass animator = new AnimationClass();
			GUI.displayPanel.addMouseListener(new MouseAdapter() {
				public void finished() {
					health = tempHealth;
					
					happiness = getHappiness() + 1;
					lastPlayed = LocalDateTime.now().plusHours(timeMachineAdjustment);
					GUI.displayPanel.removeMouseListener(this);
					finishedPlaying = true;
				}
				
				@Override
				public void mouseClicked(MouseEvent e) {
					if (!inAnimation) {
						
						inAnimation = true;
						
						//the code that will run when the user clicks on the JPanel
						currentPlayAmount++;
						
						//move the ball to the other side of the screen and start the animation
						animator.jLabelXLeft(316, 28, 0, 316-28, petLabel);
						animator.jLabelXRight(78, 366, 0, 288, ballLabel);
						animator.jLabelYUp(97, 47, 0, 50, ballLabel);
						
						//move the ball down
						try {
							Thread.sleep(30);
						} catch (InterruptedException e1) {}
						animator.jLabelYDown(47, 97, 4, 1, ballLabel);
						
						Timer ballDownTimer = new Timer(4*50, new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent ballHitsFloor) {
								//this code runs when the ball hits the floor
								animator.jLabelXRight(28, 316, 5, 1, petLabel);
								Timer petRightTimer = new Timer((5*(366)), new ActionListener(){
									@Override
									public void actionPerformed(ActionEvent petHasBall) {
										//start pet jump with ball animation
										animator.jLabelYUp(97, 67, 2, 1, petLabel);
										animator.jLabelYUp(97, 67, 2, 1, ballLabel);
										Timer spritesUpTimer = new Timer(60*2, new ActionListener(){
											@Override
											public void actionPerformed(ActionEvent jumpAtPeak) {
												//when the pet and ball reach the peak of the jump
												animator.jLabelYDown(67, 97, 2, 1, petLabel);
												animator.jLabelYDown(67, 97, 2, 1, ballLabel);
											
												Timer finishedTimer = new Timer(60*2, new ActionListener(){
													@Override
													public void actionPerformed(ActionEvent animationFinished) {	//runs when the animation is completely finished to see if it should remove the 'play panel'
														inAnimation = false;
														if (currentPlayAmount == totalPlayAmount) {
															finished();
														}
													}
												
												});
												finishedTimer.setRepeats(false);
												finishedTimer.start();
											}
										
										});
										spritesUpTimer.setRepeats(false);
										spritesUpTimer.start();
									}
								
								});
								petRightTimer.setRepeats(false);
								petRightTimer.start();
							}
						
						});
						ballDownTimer.setRepeats(false);
						ballDownTimer.start();
					}
				}
			});
			
			
			
		} else if (happiness < 5 && lastPlayed.until(LocalDateTime.now().plusHours(timeMachineAdjustment), ChronoUnit.MINUTES) < 30){
			//pet is still hungry but cannot eat
			String cantFeedPet = "You Cannot Play With Your Pet For Another " + (30 - (int) lastPlayed.until(LocalDateTime.now().plusHours(timeMachineAdjustment), ChronoUnit.MINUTES)) + " Minutes";
			GUI.displayPanelMessage(cantFeedPet);
		} else {
			//pet is not hungry
			String cantFeedPet = "Your Pet Is Does Not Want To Play";
			GUI.displayPanelMessage(cantFeedPet);
		}
	}
	
	public void removeFood() {
		food--;
	}
	
	public void kill() {
		this.alive = false;
		deathDay = LocalDateTime.now();
	}
	
	public void kill(int timeMachineAdjustment) {
		this.alive = false;
		deathDay = LocalDateTime.now().plusHours(timeMachineAdjustment);
	}
	
	public boolean isAlive() {
		return alive;
	}
	
	public void addHealth() {
		if (health < 5) {
			health++;
		}
	}
	
	public void removeHealth() {
		if (health > 0) {
			health--;
		}
	}
	
	public int getHealth() {
		return tempHealth;
	}
	
	public LocalDateTime getBirthday() {
		return birthday;
	}
	
	public int age() {
		if (this.alive) {
			return (int) birthday.until(LocalDateTime.now(), ChronoUnit.DAYS);	//returns the age in days of the pet (how many days from when the constructor was called)
		} else {
			return (int)birthday.until(deathDay, ChronoUnit.DAYS);
		}
	}
	
	public int age(int timeMachineAdjustment) {
		if (this.alive) {
			return (int) birthday.until(LocalDateTime.now().plusHours(timeMachineAdjustment), ChronoUnit.DAYS);	//returns the age in days of the pet (how many days from when the constructor was called)
		} else {
			return (int) birthday.until(deathDay, ChronoUnit.DAYS);	//returns the age in days of the pet (how many days from when the constructor was called)
		}
	}
	
	public int ageInHours() {
		if (this.alive) {
			return (int) birthday.until(LocalDateTime.now(), ChronoUnit.HOURS);
		} else {
			return (int) birthday.until(deathDay, ChronoUnit.HOURS);
		}
	}
	
	public int ageInHours(int timeMachineAdjustment) {
		if (this.alive) {
			return (int) birthday.until(LocalDateTime.now().plusHours(timeMachineAdjustment), ChronoUnit.HOURS);
		} else {
			return (int) birthday.until(deathDay, ChronoUnit.HOURS);
		}
	}
	public String getPetName() {
		return petName;
	}

	public void setPetName(String petName) {
		this.petName = petName;
	}

	public ImageIcon getPetImg() {
		return petImg;
	}

	public void setPetImg(ImageIcon petImg) {
		this.petImg = petImg;
	}

	public String getPetImgPath() {
		return petImgPath;
	}

	public void setPetImgPath(String petImgPath) {
		this.petImgPath = petImgPath;
	}

}
