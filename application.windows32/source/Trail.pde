AudioPlayer chomp; //AudioPlayer object used for the chomp sound effect (when Pacman eats).
boolean lineExist = false; //Boolean to determine if a line exists or not. 

class Trail { //Create new class called Trail for drawing paths
    int expandField; //Declare integer for expanding the interaction field around Pacman / the last dot so that the path does not break by quick mouse-movements.

    void create() { //Constructor for the create method

        chomp.play(); //REVIEW THIS LINE: SHOULDN'T PLAY AT THIS POINT.
        chomp.setGain(-20); //Set gain of the "chomp" sound effect to -25.

        noStroke(); //Hide strokes on dots.

        for (int i = dots.size() - 1; i >= 0; i--) { //Cycle trough array.
            Dot dot = dots.get(i); //Create a dot´for each element.
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
