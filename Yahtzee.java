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
		isCatSet = new boolean[nPlayers][N_CATEGORIES];
		initCats();
		playGame();
	}
	
// Inititalizes all category values for all players to false
	private void initCats() {
		for (int i = 0; i < nPlayers; i++) {
			for (int j = 0; j < N_CATEGORIES; j++) {
				isCatSet[i][j] = false;
			}
		}
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
		int scCat;
		display.printMessage(playerNames[plyr] + PLYR_ROLL_MSG);
		// Loops through all of player's rolls
		for (int i = 0; i < MAX_ROLLS; i++) {
			display.waitForPlayerToClickRoll(plyr);
			display.displayDice(rollDice(i));
			display.waitForPlayerToClickRoll(plyr);
			display.printMessage(RE_ROLL_MSG);
		}
		// Gets player's scoring category and checks availability
		display.printMessage(SEL_CAT_MSG);
		while(true) {
			// Subtract 1 to align response with array
			scCat = display.waitForPlayerToSelectCategory() - 1;
			if (catAvail(plyr, scCat)) break;
			display.printMessage(SEL_DIF_CAT_MSG);
		}
		display.updateScorecard(scCat, plyr, calcScore(scCat));
		isCatSet[plyr][scCat] = true;
		display.updateScorecard(TOTAL, plyr, totalScore(plyr));
	}
	
// Returns an array of the rolled dice results
	private int[] rollDice(int roll) {
		for (int i = 0; i < N_DICE; i++) {
			if (roll == 0) {
				dice[i] = rgen.nextInt(DIE_LOW, DIE_HIGH);
			} else if (display.isDieSelected(i)){
				dice[i] = rgen.nextInt(DIE_LOW, DIE_HIGH);
			}
		}
		return dice;
	}
	
/**
 * Returns whether a category is available to score for a given player
 * @param plyr Player number
 * @param cat Category number
 * @return True if category is available, else false
 */
	private boolean catAvail(int plyr, int cat) {
		if (cat == UPPER_SCORE || cat == UPPER_BONUS || cat == LOWER_SCORE || cat == TOTAL) {
			return false;
		} else if (!isCatSet[plyr][cat]) {
			return true;
		} else {
			return false;
		}
	}
	
/**
 * Returns the score given the dice state and category selected
 * @param cat Category selected
 * @return Score
 */
	private int calcScore(int cat) {
		switch (cat) {
			case ONES:
			case TWOS:
			case THREES:
			case FOURS:
			case FIVES:
			case SIXES:
				return singlesScore(cat);
			case THREE_OF_A_KIND:
			case FOUR_OF_A_KIND:
				return matchScore(cat - MATCH_DELTA);
			case FULL_HOUSE:
				return fullHouseScore();
			case SMALL_STRAIGHT:
			case LARGE_STRAIGHT:
				return straightScore(cat);
			case YAHTZEE:
				return yahtzeeScore();
			case CHANCE:
				return chanceScore();
			default:
				return 0;
		}
	}
	
/**
 * Returns the score for a given singles category (ONES - SIXES)
 * @param cat Category
 * @return Score
 */
	private int singlesScore(int cat) {
		int score = 0;
		for (int i = 0; i < N_DICE; i++) {
			if (dice[i] == cat) score += cat;
		}
		return score;
	}
	
/**
 * Returns the score for 3 or 4 of a kind selection
 * @param thresh Minimum threshold for getting same number on dice
 * @return Score
 */
	private int matchScore(int thresh) {
		for (int i = 0; i < N_DICE - thresh + 1; i++) {
			int cnt = 0;
			for (int j = i + 1; j < N_DICE; j++) {
				if (dice[i] == dice[j]) cnt++;
			}
			if (cnt >= thresh) return dice[i] * thresh;
		}
		return 0;
	}
	
/**
 * Checks for full house and gives appropriate score
 * @return Score
 */
	private int fullHouseScore() {
		if (yahtzeeScore() > 0) return FULL_HOUSE_PTS;
		boolean trip = false;
		boolean dbl = false;
		trip = checkExact(3);
		dbl = checkExact(2);
		if (trip && dub) {
			return FULL_HOUSE_PTS;
		} else {
			return 0;
		}
	}
	
/**
 * Checks to see if there are exactly n dice with the same number
 * @param n Number of dice threshold
 * @return True if there are n dice with the same number, else false
 */
	private boolean checkExact(int n) {
		for (int i = 0; i < N_DICE - n + 1; i++) {
			int cnt = 0;
			for (int j = i + 1; j < N_DICE; j++) {
				if (dice[i] ==  dice[j]) cnt++;
			}
			if (n == cnt) return true;
		}
		return false;
	}
		
/* Private instance variables */
	private int nPlayers;
	private String[] playerNames;
	private YahtzeeDisplay display;
	private RandomGenerator rgen = new RandomGenerator();
	private int[] dice = new int[N_DICE];
	private int[] scCats = new int[N_SCORING_CATEGORIES];
	private boolean[][] isCatSet;
}
