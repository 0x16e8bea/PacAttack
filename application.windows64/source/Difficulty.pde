int lvl = 0; // global integer counting the level. 

class Difficulty { //Create new class called Difficulty used to make the game more difficult.

    void set() { //Constructor called set used to set difficulty.
        if (scoreTotal > 500 + 500 * pow(lvl,1.05)) { //If scores is greater than a value given by 500 + a power function add ghost and increment level.
            ghosts.add(new Ghost(int(random(width)),int(random(height)))); //Add ghost at a random spot (not the best choice, because the ghost could spawn on the player).
            lvl += 1; //Increment the lvl value by one. 
        }
    }
}
