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
		initPts();
		initCats();
		playGame();
	}

	private void playGame() {
		int turn = 0;
		while(true) {
			turn++;
			if (turn > N_SCORING_CATEGORIES) break;
			for (int i = 1; i <= nPlayers; i++) {
				enactTurn(i);
			}
			endGame();
		}
	}
	
// Initializes point totals to 0
	private void initPts() {
		upScore = new int[nPlayers];
		upBonus = new int[nPlayers];
		lowScore = new int[nPlayers];
		totScore = new int[nPlayers];
		for (int i = 0; i < nPlayers; i++) {
			upBonus[i] = 0;
			upScore[i] = 0;
			lowScore[i] = 0;
			totScore[i] = 0;
		}
	}
	
/**
 * Updates lower, upper, upper bonus and total scores
 * @param plyr Player
 * @param cat Selected scoring category
 * @param score Score achieved for selected category
 */
	private void updateTotals(int plyr, int cat, int score) {
		if (cat < UPPER_SCORE) {
			upScore[plyr - 1] += score;
			// Update and set bonus only if not set and upper pts > threshold
			if (isCatSet[plyr - 1][UPPER_BONUS - 1] == false && upScore[plyr - 1] >= UP_BONUS_THRESH) {
				upBonus[plyr - 1] = UP_BONUS_PTS;
				display.updateScorecard(UPPER_BONUS, plyr, upBonus[plyr - 1]);
				isCatSet[plyr - 1][UPPER_BONUS - 1] = true;
			}
		} else {
			lowScore[plyr - 1] += score;		
		}
		display.updateScorecard(LOWER_SCORE, plyr, lowScore[plyr - 1]);
		display.updateScorecard(UPPER_SCORE, plyr, upScore[plyr - 1]);
		totScore[plyr - 1] = upScore[plyr - 1] + upBonus[plyr - 1] + lowScore[plyr - 1];
		display.updateScorecard(TOTAL, plyr, totScore[plyr - 1]);
	}
	
// Initializes all category values for all players to false
	private void initCats() {
		isCatSet = new boolean[nPlayers][N_CATEGORIES];
		for (int i = 0; i < nPlayers; i++) {
			for (int j = 0; j < N_CATEGORIES; j++) {
				isCatSet[i][j] = false;
			}
		}
	}
	
/**
 * Processes a players entire turn
 * @param plyr Player number
 */
	private void enactTurn(int plyr) {
		int scCat;
		display.printMessage(playerNames[plyr - 1] + PLYR_ROLL_MSG);
		display.waitForPlayerToClickRoll(plyr);
		// Loops through all of player's rolls
		for (int i = 0; i < MAX_ROLLS; i++) {
			display.displayDice(rollDice(i));
			display.printMessage(RE_ROLL_MSG);
			if (i < MAX_ROLLS - 1) display.waitForPlayerToSelectDice();
		}
		// Gets player's scoring category and checks availability
		display.printMessage(SEL_CAT_MSG);
		while(true) {
			// Subtract 1 to align response with array
			scCat = display.waitForPlayerToSelectCategory();
			if (isCatAvail(plyr, scCat)) break;
			display.printMessage(SEL_DIF_CAT_MSG);
		}
		int score = calcScore(scCat);
		display.updateScorecard(scCat, plyr, score);
		isCatSet[plyr - 1][scCat - 1] = true;
		updateTotals(plyr, scCat - 1, score);
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
	private boolean isCatAvail(int plyr, int cat) {
		if (cat == UPPER_SCORE || cat == UPPER_BONUS || cat == LOWER_SCORE || cat == TOTAL) {
			return false;
		} else if (!isCatSet[plyr - 1][cat - 1]) {
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
			int cnt = 1;
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
		if (trip && dbl) {
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
		int xclud = 0;
		for (int i = 0; i < N_DICE - n + 1; i++) {
			int cnt = 1;
			for (int j = i + 1; j < N_DICE; j++) {
				if (dice[i] != xclud && dice[i] == dice[j]) cnt++;
			}
			if (n == cnt) {
				return true;
			// Ensures match of greater than n doesn't hit midway through dice eval
			} else if (cnt >= n) {
				xclud = dice[i];
			}
		}
		return false;
	}	
	
/**
 * Checks for a small or large straight and returns the appropriate score
 * @param cat Category for small or large straight
 * @return Score
 */
	private int straightScore(int cat) {
		boolean[] numStr = new boolean[N_DICE];
		// Number of starting points needed to check (e.g. only 1-5 and 2-6 for large straight)
		int checkStr;
		int strScore;
		if (cat == SMALL_STRAIGHT) {
			checkStr = 3;
			strScore = SM_STAIGHT_PTS;
		} else {
			checkStr = 2;
			strScore = LG_STRAIGHT_PTS;
		}
		// Checks possible straight combinations
		for (int i = 1; i <= checkStr; i++) {
			boolean isStraight = false;
			// Set all placeholder to false
			for (int j = 0; j < N_DICE; j++) {
				numStr[j] = false;
			}
			// Set a given placeholder to true if value hits
			for (int j = 0; j < N_DICE; j++) {
				if (dice[j] == i) numStr[0] = true;
				if (dice[j] == i + 1) numStr[1] = true;
				if (dice[j] == i + 2) numStr[2] = true;
				if (dice[j] == i + 3) numStr[3] = true;
				if (dice[j] == i + 4) numStr[4] = true;
			}
			if (cat == SMALL_STRAIGHT) numStr[N_DICE - 1] = true;
			// Checks if all placeholder are true
			for (int j = 0; j < N_DICE; j++) {
				isStraight = numStr[j];
				if (isStraight == false) break;
			}
			if (isStraight) return strScore;
		}
		return 0;
	}
	
/**
 * Checks for yahtzee and returns appropriate score
 * @return Score
 */
	private int yahtzeeScore() {
		// Return 0 if any die value does not equal that of prior die
		for (int i = 1; i < N_DICE; i++) {
			if (dice[i] != dice[i-1]) return 0;
		}
		return YAHTZEE_PTS;
	}
	
/**
 * Returns chance score (sum of all dice values)
 * @return Score
 */
	private int chanceScore() {
		int score = 0;
		for (int i = 0; i < N_DICE; i++) {
			score += dice[i];
		}
		return score;
	}
	
/**
 * Determines player with highest score and displays victory message
 */
	private void endGame() {
		int winner = 0;
		for (int i = 0; i < nPlayers; i++) {
			if (upBonus[i] == 0) display.updateScorecard(UPPER_BONUS, i, 0);
		}
		for (int i = 1; i < nPlayers; i++) {
			if (totScore[i] > totScore[winner]) winner = i;
		}
		display.printMessage("Congratulations, " + playerNames[winner] + ", you're the winner with a total score of " + totScore[winner] + "!");
	}

		
/* Private instance variables */
	private int nPlayers;
	private String[] playerNames;
	private YahtzeeDisplay display;
	private RandomGenerator rgen = new RandomGenerator();
	private int[] dice = new int[N_DICE];
	private boolean[][] isCatSet;
	private int[] upScore;
	private int[] upBonus;
	private int[] lowScore;
	private int[] totScore;
}
