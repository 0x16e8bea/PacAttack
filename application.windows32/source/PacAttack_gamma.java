import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import ddf.minim.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class PacAttack_gamma extends PApplet {

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
 //Import minim for sound-integration.

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

public void setup() { //A setup function that is called only once.

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
    cherries.add(new Cherry(PApplet.parseInt(random(height)), PApplet.parseInt(random(width)))); //Add one cherry at the beginning of the game. A random location is parsed into the constructor.
    cherries.add(new Cherry(PApplet.parseInt(random(height)), PApplet.parseInt(random(width)))); //Add another cherry.
    cherries.add(new Cherry(PApplet.parseInt(random(height)), PApplet.parseInt(random(width)))); //Add yet another cherry.
    ghosts.add(new Ghost(width / 2 + 200, height / 2)); //Add a ghost at the beginning of the game. A 'default' location is parsed into the constructor.
}

public void draw() { //The draw function is looped.

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
    bgMusic.setGain(-50 / PApplet.parseFloat(scoreTotal/300) * 0.5f ); //Make the volume dynamic... the music will get louder as the score goes up. I am casting the lvl integer because I would otherwise get an error.

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

PImage cherryImage; //PImage for storing the cherry sprite
boolean consumed; //Boolean determining if Pacman consume a cherry

class Cherry { //Class for updating and displaying cherries
    int posX; //Local x position
    int posY; //Local y position
    float totalTime; //Float for storing current time
    float blink; //Value to increment so that the cherries begin blinking
    AudioPlayer consume; //AudioPlayer to play sound when Pacman consume a cherry

    Cherry(int posX_, int posY_) { //Constructor taking both an x and a y position
        posX = posX_; //Set posX to the first augument
        posY = posY_; //Set posY to the second augument

        cherryImage = loadImage("cherry.png"); //Load cherry image
        consume = minim.loadFile("cherry.wav"); //Load sound effect "cherry.wav"
        consume.setGain(-20); //Set gain to -20
        totalTime = millis(); //Set totalTime once to current time
    }

    public void update() { //Method for updating cherries

        if (gameOver != true) { //If game over is not true
            if (dist(posX, posY, location.x, location.y) < 32) { //If Pacman touch a cherry
                cherries.remove(this); //Remove "this" paticular cherry
                cherries.add(new Cherry(PApplet.parseInt(random(width)), PApplet.parseInt(random(height)))); //Add a new cherry
                consume.play(); //Play sound when consumed
                consumed = true; //Set consumed to true (used later inside the Score class)
            }
        }

        /*I will change this one later as it can cause a bug where cherries does not spawn*/
        if (savedTime2 > totalTime + PApplet.parseInt(random(6000, 7000)) && cherries.size() < 3) { //If savedTime (current time) is less than totalTime + a random value (between 5 and 7 seconds) and less than 3 cherries are present
            cherries.add(new Cherry(PApplet.parseInt(random(width)), PApplet.parseInt(random(height)))); //Add new cherry
        }

        if (savedTime2 > totalTime + 10000) { //After 10 seconds
            cherries.remove(this); //Remove the cherry
        }

        if (cherries.size() > 3) { //If cherries exceed a size of 3
            cherries.remove(this); //Remove "this" cherry (not sure if it should be 'this'... but it seems to work)
        }
    }

    public void display() {
        pushMatrix(); //Save coordinate system 
        translate(posX, posY); //Translate cherry
        rotate(cos(savedTime2 / 150));
        if (savedTime2 > totalTime + 5000) { //If savedTime is greater than totalTime + 5 seconds
            /*Clock cycling through 0 and 1*/
            blink += 0.1f; //increment value
            println(PApplet.parseInt(blink) % 2);
            pushStyle(); //Save style
            tint(255, 255 - 255 / 2 * (PApplet.parseInt(blink) % 2)); //Tint cherry... alpha goes between 0 and 1 due to modulus, thereby adding 255/2 at intervals. So the value changes between 255 and 255/2
            image(cherryImage, 0, 0); //Display cherry at x = 0, y = 0 in the new coordinate system  
            popStyle(); //Restore style
        } else {
            image(cherryImage, 0, 0); //Same as the one above   
        }
        popMatrix(); //Restore coordinate system 
    }
}
int lvl = 0; // global integer counting the level. 

class Difficulty { //Create new class called Difficulty used to make the game more difficult.

    public void set() { //Constructor called set used to set difficulty.
        if (scoreTotal > 500 + 500 * pow(lvl,1.05f)) { //If scores is greater than a value given by 500 + a power function add ghost and increment level.
            ghosts.add(new Ghost(PApplet.parseInt(random(width)),PApplet.parseInt(random(height)))); //Add ghost at a random spot (not the best choice, because the ghost could spawn on the player).
            lvl += 1; //Increment the lvl value by one. 
        }
    }
}
class Dot { //Create new class called Dot for displaying and destroying dots. 
    int posX; //Local integer containing the x-coordinate. 
    int posY; //Local integer containing the y-coordinate. 

    Dot(int posX_, int posY_) { //Constructor requiring two arguments to work. 
        posX = posX_; //Set posX equal to the first argument.
        posY = posY_; //Set posY equal to the second argument.
    }

    public void display() { //Simply method to display a dot.
        ellipse(posX, posY, 5, 5); //Draw an ellipse to represent a dot.  
    }

    public void destroy() { //Method for destroying dots. 

        if (dist(dots.get(0).posX, dots.get(0).posY, location.x, location.y) < 10 && dots.size() > 1) { //If Pacman touch the first dot (element zero) then remove it. 
            dots.remove(0); //Remove element zero.
            chomp.rewind(); //Rewind chomp sound effect. 
        }

        if (dots.size() > 500) { //If the amount of points exceed 500, remove the first point (not the best solution)
            dots.remove(0); //Remove elemet zero. 
        }
    }



}
class Ghost {
    float randomTime; //Float for storing random time
    float maxSpeed; //Float for limiting max speed
    float maxForce; //Float for limiting how much force can be used to seperate ghosts
    float startX; //Float containing initial x-position
    float startY; //Float containing initial y.position
    float turn; //Value that is incremented making the ghosts turn around
    float totalTimeOne; //Float that should take savedTime
    boolean toggle; //A boolean that toggles if a ghost goes outside the screen
    int sign = 1; //Integer that shoukd change between 1 and -1. (Starting with 1)
    int collision = 1; //On collision this variable should change to -1.

    PVector locationEnemy; //PVector storing information about the enemy location
    PVector acceleration; //PVector storing information about acceleration
    PVector velocity; //PVector storing information about velocity

    float index = 0; //Index used to animate ghost(s) by incrementing the value

    PImage[] ghost = new PImage[6]; //PImage that takes 6 sprites

    Ghost(int startX_, int startY_) { //Constructor requiring a start x and y coordinate.
        startX = startX_; //Set startX to argument one
        startY = startY_; //Set startY to argument two
        locationEnemy = new PVector(startX, startY); //Create/assign vector containing startX and startY
        totalTimeOne = savedTime2; //Set totalTime once (will be reset later on)
        randomTime = random(1000, 2000); //Set random time somewhere between 1 and 2 seconds
        velocity = new PVector(0, 0); //Assign vector to velocity starting at zero velocity
        for ( int i = 0; i < ghost.length; i++ ) { //Cycle through ghost image array
            ghost[i] = loadImage("Ghost " + i + ".png"); //Assign corresponding images to all instances of i (all elements in the array)
            maxSpeed = 0.5f; //Set value to limit max speed
            maxForce = 0.25f; //Set value to limit max force used to seperate ghosts
        }
    }

    public void display() { //Method for displaying ghosts
        pushMatrix(); //Save current coordinate system
        translate(locationEnemy.x, locationEnemy.y); //Tanslate ghost to vector coordinates
        image(ghost[PApplet.parseInt(index)], 0, 0, 32, 32); //Load ghost image for the corresponding index
        index += 0.2f; //Increment the value by 0.2 to slowly animate
        index = (index) % ghost.length; //Cycle through the ghost array using the remainder
        popMatrix(); //Restore coordinate system

        //ellipse(locationEnemy.x + velocity.x * 5, locationEnemy.y + velocity.y * 5, 10, 10); //debug velocity

        //pushStyle(); //Save style
        //noFill(); //Hide fill
        //stroke(255, 0, 0); //set stroke color to red
        //ellipse(locationEnemy.x, locationEnemy.y, 300, 300); // debug detection
        //popStyle(); //Restore style
    }

    public void update() { //Method for updating ghosts
        maxSpeed = 0.5f + lvl / 20; //Not working probably for now... lvl/20 is temporary
        maxSpeed = constrain(maxSpeed, 0, 1.8f); //Constrain speed 


        if (savedTime2 > totalTimeOne + randomTime) { //If savedTime is greater than totalTime + randomTime 
            turn += 0.05f * sign; //Increment turn so that the ghost turn around and * sign
        }

        if (savedTime2 > totalTimeOne + randomTime * 1.5f) { //If saved time is greater than totalTime plus randomtime times 1.5 
            totalTimeOne = savedTime2; //Reset totalTimeOne
            sign = PApplet.parseInt(random(-2, 2)); //Set sign to either 1 or -1 
            randomTime = random(500, 1500); //Set a new random time 
        }

        PVector randomLoc = new PVector(locationEnemy.x + cos(turn), locationEnemy.y + sin(turn)); //Assign random location later used for setting a random direction

        PVector dir = PVector.sub(location, locationEnemy); //Set direction towards Pacman by subtracting two vectors 
        PVector ranDir = PVector.sub(randomLoc, locationEnemy); //Set random direction by subtracting two vectors 
        PVector sep = separate(ghosts); //Assign values to PVectpr sep by calling the method seperate with the ghosts array as argument 

        if (dist(location.x, location.y, locationEnemy.x, locationEnemy.y) < 150 && gameOver != true) {

            pushStyle(); //Save style 
            textSize(20); //Set text size 20
            text("!", locationEnemy.x, locationEnemy.y - 20); //Display question mark over ghost
            popStyle(); //Recstore style

            dir.normalize(); //Normalize direction (unit vector)
            acceleration = dir; //Set acceleration to dir (just because)
            acceleration.add(sep); //Add sep to acceleration to make ghosts repel each other 
            acceleration.mult(collision); //Multiply by collision (1 or -1) to make the ghosts bounce
            velocity.limit(maxSpeed); //Limit max velocity 
            velocity.add(acceleration); //Add acceleration to ghost 
            locationEnemy.add(velocity); //Finally add velocity vector to location making the ghost(s) move 

            if (dist(location.x, location.y, locationEnemy.x, locationEnemy.y) < 35) { //If ghosts are touching Pacman 
                damage += 1; //Take damage... hunger decreases faster. (See the Score class) 
            }
        } else { //Else... when not chasing Pacman
            acceleration = ranDir; //Set acceleration to random direction (they should now search for Pacman)
            acceleration.add(sep); //Add seperation like before 
            velocity.limit(maxSpeed * 0.50f); //Limit velocity 
            acceleration.mult(collision); //Multiply by collision (1 or -1) to make the ghosts bounce
            velocity.add(acceleration); //Add acceleration to velocity
            locationEnemy.add(velocity); //Add velocity to location 
        }

        if (locationEnemy.x + velocity.x > width && toggle == false || locationEnemy.x + velocity.x < 0 && toggle == false || locationEnemy.y + velocity.y > height && toggle == false || locationEnemy.y + velocity.y < 0 && toggle == false) {
            collision *= -1; //Value that should make the ghost go in the opposite direction
            toggle = true; //Set toggle to true so that the collision stops
        } else {
            toggle = false; //Set toggle to false so that we check for collisions again
        }

    }

    public PVector separate (ArrayList<Ghost> ghosts) { //Method for checking seperating ghosts (the return type is a vector)
        float setSeparation = 60; //Set distance of seperation
        PVector repel = new PVector(0, 0); //Create vector to repel ghosts away from each other
        int count = 0; //Declare variable to count amount of ghosts
        // For every boid in the system, check if it's too close
        for (Ghost other : ghosts) { //Cycle through all objects in the array
            float distGhosts = PVector.dist(locationEnemy, other.locationEnemy); //Calculate distance between ghosts

            if ((distGhosts > 0) && (distGhosts < setSeparation)) { // If distance is greater than 0 (self) and less than setSeparation
                PVector normalVec = PVector.sub(locationEnemy, other.locationEnemy); //Calculate vectors normal to the neighbour
                normalVec.normalize(); //Normalize the vector to get the unit vector
                normalVec.div(distGhosts); // Weight by distance
                repel.add(normalVec); //Add normalVec to the repel vector
                count++; // Count to keep track of ghost amount

                /*Debug repel vector*/
                pushStyle();
                fill(255, 0, 0);
                //ellipse(width / 2 + repel.x, height / 2 + repel.y, 10, 10);
                popStyle();
            }
        }
        if (count > 0) {
            repel.div(count); //Average the repelling force vectors
        }

        repel.normalize(); //Normalize the repel vector 
        repel.mult(maxSpeed); //Multiply by maxSpeed
        repel.limit(maxForce); //Limit the force used to seperate ghosts
        return repel; //return repel vector 
    }

}
AudioPlayer splat; //Audio player used to play a sound when Pacman dies
PVector location; //Location vector used for Pacman

class Player { //Class used for Pacman

    PImage[] pacman = new PImage[8]; //PImage array (size = 8) for storing Pacman sprites
    PImage blood = new PImage(); //PImage for storing a uncomfortable image
    float cAngle; //Float that is used to store an angle (The direction Pacman should turn)
    PVector acceleration; //Vector for acceleration
    PVector velocity; //Vector for velocity
    float maxSpeed; //Float limiting max speed
    float proximity; //Float to store the distance between Pacman and the first dot
    float index = 0; //Index used to animate Pacman by incrementing the value

    public void create(int startX, int startY) { //Constructor requiring a startX- and startY position 

        for (int i = 0; i < pacman.length; i++ ) { //Cycle through the pacman array 
            pacman[i] = loadImage("Pacman " + i + ".png"); //Load image for each instance of i in the array
        }

        blood = loadImage("blood.png"); //Load that bloody image! 

        location = new PVector(startX, startY); //Create new location vector with starting coordinates 
        velocity = new PVector(0, 0); //Create new velocity vector with no velocity 
        maxSpeed = 2; //Limit speed to 2 
    }

    public void move() { //Method for moving pacman 

        /*This was originally used for debugging... it highlights the dot, that Pacman is about to eat */
        pushStyle(); //Save style 
        fill(255, 0, 0); //Fill red
        ellipse(dots.get(0).posX, dots.get(0).posY, 10, 10); //Draw a slightly bigger ellipse underneath the existing one  
        popStyle(); //Restore style 

        cAngle = atan2(dots.get(0).posY - location.y, dots.get(0).posX - location.x); //Trigonometric function (using tan) to determine the angle between pacman at the first dot. 

        pushMatrix(); //Save coordinate system 
        translate(location.x, location.y); //Translate to location coordinate x and y from vector.
        rotate(cAngle); //Set angle of rotation
        imageMode(CENTER); //Center image 

        if (gameOver != true) { //If not game over  
                image(pacman[PApplet.parseInt(index)], 0, 0, 32, 32); //Display the pacman image array
                index += 0.5f; //Increment index for animation
                index = (index) % pacman.length; //Modulus used to reset index 
        } else { //If game over is true 
            image(blood, 0, 15); //Show image blood
            splat.setGain(500); //Set gain of splat sound effect
            splat.play(); //Play splat 
        }
        popMatrix(); //Restore coordinate system 

    }

    public void update() { //Method for updating Pacman
        if (gameOver != true) { //If game over is false

            PVector dot = new PVector(dots.get(0).posX, dots.get(0).posY); //Make a new (local) vector taking the posX and posY from the dot in element zero. 
            PVector dir = PVector.sub(dot, location); //Make a direction vector by subtracting the dot vector and Pacmans location vector.

            proximity = dist(dots.get(0).posX, dots.get(0).posY, location.x, location.y); //Set proximity to distance between next dot and Pacman

            if (dots.size() == 1 && dist(dots.get(0).posX, dots.get(0).posY, location.x, location.y) < 80) { //If there is only one dot and pacman is close by.
                proximity = map(proximity, 32, 80, 0, 1); //Map proximity so that at dist = 80 Pacman will start to slow down and then stop at dist = 35
            } else { //If that is not the case 
                proximity = map(proximity, 0, 100, 0.8f, 1); //Make it so that Pacman slows down only a tiny bit
            }

            dir.normalize(); //Normalize directional vector
            acceleration = dir; //Set acceleration = dir... because we should not confuse the two 
            velocity.limit(maxSpeed); //limit velocity
            velocity.add(acceleration); //Add acceleration to velocity
            velocity.mult(proximity); //Multiply by proximity to make Pacman speed up / slow down
            location.add(velocity); //Finally add velocity to the location vector
        }
    }
}
int scoreTotal; //Integer to count score total.
int hunger; //Integer to keep track of hunger.
int damage; //Integer diminishing hunger if attacked.
int survivalTime; //Integer for storing survival time.
boolean gameOver; //Boolean to determine if the game is lost.
int fillHunger; //Integer used fill hunger-meter

class Score { //Create class for storing score, hunger etc. (Bad name... I am going to change it later)
    

    public void display() { //Method for displaying score, hunger and time. 

        if (gameOver != true) { //If the game over state is false 
            survivalTime = PApplet.parseInt(savedTime2 / 100); //survivalTime = deciseconds 
        }

        

        if (consumed == true) { //If consumed (if Pacman eats a cherry)
            scoreTotal += 100; //Increment scoreTotal +100
            consumed = false; //Reset consumed to false

            if (hunger + 50 <= 100) { //If hunger + 50 is less than or equal to 100
                fillHunger += 50; //Fill hunger with the maximum value: 50
            } else { //If that is not the case
                fillHunger += 100 - hunger; //Fill hunger so that the sum is 100. 
            }
        }

        pushStyle(); //Save style
        textAlign(CENTER); //Align text in center
        text("Score: " + scoreTotal, width / 2, 20); //Text for displaying total score 
        text("Survival time: " + survivalTime, width / 2, 35); //Text for displaying survival time
        hunger = 100 - PApplet.parseInt(savedTime2 / 100) + fillHunger - damage; //Set hunger to 100 (max) minus time plus the value that replenish hunger minus the total damage taken. 
        hunger = constrain(hunger, 0, 100); //Constrain hunger so that it does not go into negative. It is already restrained in the top, as I put a limit on fillHunger. 

        if (hunger >= 50 && hunger <= 100) { //If hunger is between 100 and 50 make text green.
        	fill(0,255,0); //Fill green
            text("Hunger: " + hunger, width / 2, 50); //Display hunger
        } else if (hunger > 25 && hunger < 50) { //If hunger is between 50 and 25 make text yellow.
        	fill(255,255,0); //Fill yellow
        	text("Hunger: " + hunger, width / 2, 50); //Display hunger
        } else { //If below 25 make text red
			fill(255,0,0); //Fill red
        	text("Hunger: " + hunger, width / 2, 50); //Display hunger
        }
        popStyle(); //Restore style


        if (hunger == 0) { //If hunger equals zero
            pushStyle(); //Save style
            textAlign(CENTER); //Align text in center
            textSize(50); //Set text size 50
            text("GAME OVER", width / 2, height / 2); //Show text "GAME OVER"
            gameOver = true; //Set gameOver boolean to true
            textSize(35); //Set text size to 35
            text("Your highscore is: " + (scoreTotal + survivalTime), width / 2, height / 2 + 50); //Show highscore
            popStyle(); //Restore style
        }
    }
}
AudioPlayer chomp; //AudioPlayer object used for the chomp sound effect (when Pacman eats).
boolean lineExist = false; //Boolean to determine if a line exists or not. 

class Trail { //Create new class called Trail for drawing paths
    int expandField; //Declare integer for expanding the interaction field around Pacman / the last dot so that the path does not break by quick mouse-movements.

    public void create() { //Constructor for the create method

        chomp.play(); //REVIEW THIS LINE: SHOULDN'T PLAY AT THIS POINT.
        chomp.setGain(-20); //Set gain of the "chomp" sound effect to -25.

        noStroke(); //Hide strokes on dots.

        for (int i = dots.size() - 1; i >= 0; i--) { //Cycle trough array.
            Dot dot = dots.get(i); //Create a dot\u00b4for each element.
            dot.display(); //Display all dots.
        }

        if (!mousePressed) { //If mouse is not pressed.
            expandField = 0; //Reset field around Pacman if releasing mouse.
            lineExist = true; //A line does exist.
        }

        if (mousePressed && dist(mouseX, mouseY, location.x, location.y) < 50 + expandField 
            || mousePressed && dist(mouseX, mouseY, dots.get(dots.size() - 1).posX, dots.get(dots.size() - 1).posY) < 50) { // Check if mouse is close to Pacman or the last dot in the array list and if clicked
            if (dist(dots.get(dots.size() - 1).posX, dots.get(dots.size() - 1).posY, mouseX, mouseY) > 20) { //Only create a dot if the distance between the last dot and the second last is bigger than 20
                dots.add(new Dot(mouseX, mouseY)); //Create new dot
                expandField = 500; //Expand interaction field
            }
        }

        if (lineExist == true && mousePressed && dist(mouseX, mouseY, location.x, location.y) < 50) { //If a line exists and the mouse is pressed close to Pacman then delete the existing line. 

            for (int i = dots.size() - 1; i >= 1; i--) { //For all elements in the array
                dots.remove(i); //Remove all instances of i
            } 
            lineExist = false; //Set line to non-existing
        }
        //Remove dots if eaten
        dots.get(0).destroy(); //call the destroy method on the first element of the dots array.   
    }

}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "PacAttack_gamma" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
