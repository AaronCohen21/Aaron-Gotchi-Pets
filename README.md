# Aaron-Gotchi Pets

This is a Tamagotchi pet simulator created using the Java Swing library.
This was made as a project for my grade 11 Computer Science class.
All data for the application is stored in the directory `User/pets`
The data consists of the .png files of the pets, and information about the pets' food, and happiness levels.

# TODO

- [ ] Implement _"bed time"_ for pets so they can last overnight
- [ ] Use RNG to determine how long pets can go before losing food & happiness
  - [ ] Display this as a "pet stat"
  - [ ] Make the rate a pet loses food and loses happiness different from one another

# Creating A Pet

To create a pet the user is sent to an image editor where they will create the pixel art for their pet in an 8x8 grid.
This art is then saved as a .png file in `pets/img/petName.png` and is used to preview the pet, as well as in animations to play with the pet.

# The Time Machine

This application uses System time to update the pets' age, however having to wait hours to test and showcase the app isn't ideal.
This is why for purposes of testing the app and showcasing it, there is an "Add Hour" button to increase the pets' age.