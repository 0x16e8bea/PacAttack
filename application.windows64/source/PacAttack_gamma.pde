//GAME DESCRIPTION//
/* PacAttack is a survial horror game :) Your job is to guide
Pacman away from ghosts and eat cherries to satisfy his
hunger if you do not eat you die! The art assets (not the music)
were hand-crafted by me, but sadly do not own the rights to
Pacman.
*/

//HOW TO PLAY//
/* Use the mouse to control Pacman. You can draw dots
(by left mouse dragging) that he will follow by eating them.
Clicking and dragging from Pacman creates a new path of dots;
you can continue the path by continuing the existing path.

Eating cherries gives +100 points... and the survival time
also counts towards the final score.
*/

//NOTES//
/* Some might experience slow-downs... I have not been able
to fully test this as I have no problems on both my desktop
and laptop.
*/

/*Declare array-lists*/
ArrayList <Dot> dots = new ArrayList <Dot> (); //Array list holding dots (used to guide Pacman).
ArrayList <Cherry> cherries = new ArrayList <Cherry> (); //Array list holding cherries (consumables to collect).
ArrayList <Ghost> ghosts = new ArrayList <Ghost> (); //Array list holding ghosts (enemies that will try to kill Pacman).

/*Import library*/
import ddf.minim.*; //Import minim for sound-integration.

/*Declare objects*/
Player player = new Player(); //Create object of the type Player... contains methods linked to player behavior etc.
Trail trail = new Trail(); //Create trail object. It's primary function is to enable the player to draw paths.
Score score = new Score(); //Create a score object to keep track of the score, hunger and so forth.
Difficulty level = new Difficulty(); //Create difficulty object making it more difficult as you go along.

/*Minim declarations*/
Minim minim; //Object refering to the Minim class.
AudioPlayer bgMusic; //AudioPlayer object used for handling the background music.

/*Images*/
PImage bg; //PImage used for the background.

/*Declare variables*/
float audioLvl; //A float for storing the audio level from the background music.
float savedTime;
float savedTime2; //A float for storing the elapsed time.

void setup() { //A setup function that is called only once.

    /*Configuration*/
    size(800, 600); //Set size of screen.
    background(0); //Set start-background to black.
    frameRate(60); //Operate at 60 fps.
    smooth(); //Anti-aliase edges.
    bg = loadImage("bg.png"); //Load background


    player.create(-50, height / 2); //Call the 'create' method of the player class. The arguments determine the starting position.

    /*Set up minim*/
    minim = new Minim(this); //'this' means that we are working with "this" paticular object.

    /*Load music and sound effects*/
    bgMusic = minim.loadFile("music.mp3"); //Load file "music.mp3".
    bgMusic.play(); //Play file "music.mp3".
    splat = minim.loadFile("splat.wav"); //Load file "splat.wav".
    splat.setGain(50); //Set gain of splat.was to +50.
    chomp = minim.loadFile("chomp.wav"); //Load sound effect "chomp.wav" from the data folder.

    /*Set up arrays*/
    dots.add(new Dot(width / 2, height / 2)); //Add one dot object in the middle (for Pacmans entrance).
    cherries.add(new Cherry(int(random(height)), int(random(width)))); //Add one cherry at the beginning of the game. A random location is parsed into the constructor.
    cherries.add(new Cherry(int(random(height)), int(random(width)))); //Add another cherry.
    cherries.add(new Cherry(int(random(height)), int(random(width)))); //Add yet another cherry.
    ghosts.add(new Ghost(width / 2 + 200, height / 2)); //Add a ghost at the beginning of the game. A 'default' location is parsed into the constructor.
}

void draw() { //The draw function is looped.

    /*Background stuff*/
    imageMode(CENTER); //Center background.
    pushMatrix(); //Push transformation matrix onto the matrix stack.
    translate(width / 2, height / 2); //Translate coordinate system to the middle of the screen.
    scale(1 + audioLvl / 200 * scoreTotal / 1000); //Scale background with audio level.
    rotate(savedTime2 / 20000); //Rotate background slowly.
    image(bg, 0, 0); //Load image in origo (the origin) which is now in the center of the screen.
    popMatrix(); //Restore coordinate system.

    /*Initialize object behaviours*/
    player.move(); //Call move player method.
    trail.create(); //Call the create trail method.
    player.update(); //Call the update player method.

    /*Set time*/
    //savedTime = millis();
    savedTime2 = millis(); //Set savedTime to count milliseconds.

    /*BG music stuff*/
    audioLvl = bgMusic.left.level() * 20 + bgMusic.right.level() * 20; //Get audiolevel of bgMusic from both channels.
    bgMusic.setGain(-50 / float(scoreTotal/300) * 0.5 ); //Make the volume dynamic... the music will get louder as the score goes up. I am casting the lvl integer because I would otherwise get an error.

    /*Boolean for looping background music if not playing*/
    if (bgMusic.isPlaying() == false) { //If the background music is not playing.
        bgMusic.rewind(); //Rewind bgMusic.
        bgMusic.play(); //Play bgMusic (may not be necessary though?).
    }

    /*Arrays to display objects in the array lists*/
    for (int i = cherries.size() - 1; i >= 0; i--) { //Cycles through the cherries array.
        Cherry pointObj = (Cherry) cherries.get(i); //Create local object initializing for all elements of the array.
        pointObj.update(); //Update all cherries.
        pointObj.display(); //Display all cherries.
    }

    for (int i = ghosts.size() - 1; i >= 0; i--) { //Cycle through ghost array. 
        Ghost ghostObj = (Ghost) ghosts.get(i); //Set all elements of the array to a ghost.
        ghostObj.update(); //CUpdate ghosts.
        ghostObj.display(); //Display ghosts.
    }

    /*Stats and difficulty*/
    level.set(); //Call set level method (makes the game more difficult).
    score.display(); //Call the display score function.
}

