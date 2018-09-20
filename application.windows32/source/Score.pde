int scoreTotal; //Integer to count score total.
int hunger; //Integer to keep track of hunger.
int damage; //Integer diminishing hunger if attacked.
int survivalTime; //Integer for storing survival time.
boolean gameOver; //Boolean to determine if the game is lost.
int fillHunger; //Integer used fill hunger-meter

class Score { //Create class for storing score, hunger etc. (Bad name... I am going to change it later)
    

    void display() { //Method for displaying score, hunger and time. 

        if (gameOver != true) { //If the game over state is false 
            survivalTime = int(savedTime2 / 100); //survivalTime = deciseconds 
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
        hunger = 100 - int(savedTime2 / 100) + fillHunger - damage; //Set hunger to 100 (max) minus time plus the value that replenish hunger minus the total damage taken. 
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
