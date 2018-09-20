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

    void update() { //Method for updating cherries

        if (gameOver != true) { //If game over is not true
            if (dist(posX, posY, location.x, location.y) < 32) { //If Pacman touch a cherry
                cherries.remove(this); //Remove "this" paticular cherry
                cherries.add(new Cherry(int(random(width)), int(random(height)))); //Add a new cherry
                consume.play(); //Play sound when consumed
                consumed = true; //Set consumed to true (used later inside the Score class)
            }
        }

        /*I will change this one later as it can cause a bug where cherries does not spawn*/
        if (savedTime2 > totalTime + int(random(6000, 7000)) && cherries.size() < 3) { //If savedTime (current time) is less than totalTime + a random value (between 5 and 7 seconds) and less than 3 cherries are present
            cherries.add(new Cherry(int(random(width)), int(random(height)))); //Add new cherry
        }

        if (savedTime2 > totalTime + 10000) { //After 10 seconds
            cherries.remove(this); //Remove the cherry
        }

        if (cherries.size() > 3) { //If cherries exceed a size of 3
            cherries.remove(this); //Remove "this" cherry (not sure if it should be 'this'... but it seems to work)
        }
    }

    void display() {
        pushMatrix(); //Save coordinate system 
        translate(posX, posY); //Translate cherry
        rotate(cos(savedTime2 / 150));
        if (savedTime2 > totalTime + 5000) { //If savedTime is greater than totalTime + 5 seconds
            /*Clock cycling through 0 and 1*/
            blink += 0.1; //increment value
            println(int(blink) % 2);
            pushStyle(); //Save style
            tint(255, 255 - 255 / 2 * (int(blink) % 2)); //Tint cherry... alpha goes between 0 and 1 due to modulus, thereby adding 255/2 at intervals. So the value changes between 255 and 255/2
            image(cherryImage, 0, 0); //Display cherry at x = 0, y = 0 in the new coordinate system  
            popStyle(); //Restore style
        } else {
            image(cherryImage, 0, 0); //Same as the one above   
        }
        popMatrix(); //Restore coordinate system 
    }
}
