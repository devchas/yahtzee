/*
 * File: Yahtzee.java
 * ------------------
 * This program will eventually play the Yahtzee game.
 */

import acm.io.*;
import acm.program.*;
import acm.util.*;

public class Yahtzee extends GraphicsProgram implements YahtzeeConstants {
	
	public static void main(String[] args) {
		new Yahtzee().start(args);
	}
	
	public void run() {
		IODialog dialog = getDialog();
		nPlayers = dialog.readInt("Enter number of players");
		playerNames = new String[nPlayers];
		for (int i = 1; i <= nPlayers; i++) {
			playerNames[i - 1] = dialog.readLine("Enter name for player " + i);
		}
		display = new YahtzeeDisplay(getGCanvas(), playerNames);
		playGame();
	}

	private void playGame() {
		int turn = 0;
		while(true) {
			turn++;
			if (turn > N_SCORING_CATEGORIES) break;
			for (int i = 0; i < nPlayers; i++) {
				enactTurn(i);
			}
			calcFinalScores();
			endGame();
		}
	}
	
/**
 * Processes a players entire turn
 * @param plyr Player number
 */
	private void enactTurn(int plyr) {
		display.printMessage(playerNames[plyr] + PLYR_ROLL_MSG);
		// Loops through all of player's rolls
		for (int i = 0; i < MAX_ROLLS; i++) {
			display.waitForPlayerToClickRoll(plyr);
			display.displayDice(rollDice());
			display.waitForPlayerToClickRoll(plyr);
			display.printMessage(RE_ROLL_MSG);
		}
		// Gets player's scoring category and checks availability
		display.printMessage(SEL_CAT_MSG);
		while(true) {
			// Subtract 1 to align response with array
			int scCat = display.waitForPlayerToSelectCategory() - 1;
			if (catAvail(plyr, scCat)) break;
			display.printMessage(SEL_DIF_CAT_MSG);
		}
		display.updateScorecard(scCat, plyr, calcScore(scCat));
		
	}
		
/* Private instance variables */
	private int nPlayers;
	private String[] playerNames;
	private YahtzeeDisplay display;
	private RandomGenerator rgen = new RandomGenerator();
	private int[] dice = new int[N_DICE];
	private int[] scCats = new int[N_SCORING_CATEGORIES];
}
