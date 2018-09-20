class Dot { //Create new class called Dot for displaying and destroying dots. 
    int posX; //Local integer containing the x-coordinate. 
    int posY; //Local integer containing the y-coordinate. 

    Dot(int posX_, int posY_) { //Constructor requiring two arguments to work. 
        posX = posX_; //Set posX equal to the first argument.
        posY = posY_; //Set posY equal to the second argument.
    }

    void display() { //Simply method to display a dot.
        ellipse(posX, posY, 5, 5); //Draw an ellipse to represent a dot.  
    }

    void destroy() { //Method for destroying dots. 

        if (dist(dots.get(0).posX, dots.get(0).posY, location.x, location.y) < 10 && dots.size() > 1) { //If Pacman touch the first dot (element zero) then remove it. 
            dots.remove(0); //Remove element zero.
            chomp.rewind(); //Rewind chomp sound effect. 
        }

        if (dots.size() > 500) { //If the amount of points exceed 500, remove the first point (not the best solution)
            dots.remove(0); //Remove elemet zero. 
        }
    }



}
