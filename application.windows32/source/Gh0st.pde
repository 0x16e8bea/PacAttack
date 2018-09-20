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
            maxSpeed = 0.5; //Set value to limit max speed
            maxForce = 0.25; //Set value to limit max force used to seperate ghosts
        }
    }

    void display() { //Method for displaying ghosts
        pushMatrix(); //Save current coordinate system
        translate(locationEnemy.x, locationEnemy.y); //Tanslate ghost to vector coordinates
        image(ghost[int(index)], 0, 0, 32, 32); //Load ghost image for the corresponding index
        index += 0.2; //Increment the value by 0.2 to slowly animate
        index = (index) % ghost.length; //Cycle through the ghost array using the remainder
        popMatrix(); //Restore coordinate system

        //ellipse(locationEnemy.x + velocity.x * 5, locationEnemy.y + velocity.y * 5, 10, 10); //debug velocity

        //pushStyle(); //Save style
        //noFill(); //Hide fill
        //stroke(255, 0, 0); //set stroke color to red
        //ellipse(locationEnemy.x, locationEnemy.y, 300, 300); // debug detection
        //popStyle(); //Restore style
    }

    void update() { //Method for updating ghosts
        maxSpeed = 0.5 + lvl / 20; //Not working probably for now... lvl/20 is temporary
        maxSpeed = constrain(maxSpeed, 0, 1.8); //Constrain speed 


        if (savedTime2 > totalTimeOne + randomTime) { //If savedTime is greater than totalTime + randomTime 
            turn += 0.05 * sign; //Increment turn so that the ghost turn around and * sign
        }

        if (savedTime2 > totalTimeOne + randomTime * 1.5) { //If saved time is greater than totalTime plus randomtime times 1.5 
            totalTimeOne = savedTime2; //Reset totalTimeOne
            sign = int(random(-2, 2)); //Set sign to either 1 or -1 
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
            velocity.limit(maxSpeed * 0.50); //Limit velocity 
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

    PVector separate (ArrayList<Ghost> ghosts) { //Method for checking seperating ghosts (the return type is a vector)
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
