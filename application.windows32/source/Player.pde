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

    void create(int startX, int startY) { //Constructor requiring a startX- and startY position 

        for (int i = 0; i < pacman.length; i++ ) { //Cycle through the pacman array 
            pacman[i] = loadImage("Pacman " + i + ".png"); //Load image for each instance of i in the array
        }

        blood = loadImage("blood.png"); //Load that bloody image! 

        location = new PVector(startX, startY); //Create new location vector with starting coordinates 
        velocity = new PVector(0, 0); //Create new velocity vector with no velocity 
        maxSpeed = 2; //Limit speed to 2 
    }

    void move() { //Method for moving pacman 

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
                image(pacman[int(index)], 0, 0, 32, 32); //Display the pacman image array
                index += 0.5; //Increment index for animation
                index = (index) % pacman.length; //Modulus used to reset index 
        } else { //If game over is true 
            image(blood, 0, 15); //Show image blood
            splat.setGain(500); //Set gain of splat sound effect
            splat.play(); //Play splat 
        }
        popMatrix(); //Restore coordinate system 

    }

    void update() { //Method for updating Pacman
        if (gameOver != true) { //If game over is false

            PVector dot = new PVector(dots.get(0).posX, dots.get(0).posY); //Make a new (local) vector taking the posX and posY from the dot in element zero. 
            PVector dir = PVector.sub(dot, location); //Make a direction vector by subtracting the dot vector and Pacmans location vector.

            proximity = dist(dots.get(0).posX, dots.get(0).posY, location.x, location.y); //Set proximity to distance between next dot and Pacman

            if (dots.size() == 1 && dist(dots.get(0).posX, dots.get(0).posY, location.x, location.y) < 80) { //If there is only one dot and pacman is close by.
                proximity = map(proximity, 32, 80, 0, 1); //Map proximity so that at dist = 80 Pacman will start to slow down and then stop at dist = 35
            } else { //If that is not the case 
                proximity = map(proximity, 0, 100, 0.8, 1); //Make it so that Pacman slows down only a tiny bit
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
