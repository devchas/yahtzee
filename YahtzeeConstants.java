/*
 * File: YahtzeeConstants.java
 * ---------------------------
 * This file declares several constants that are shared by the
 * different modules in the Yahtzee game.
 */

public interface YahtzeeConstants {

/** The width of the application window */
	public static final int APPLICATION_WIDTH = 600;

/** The height of the application window */
	public static final int APPLICATION_HEIGHT = 350;

/** The number of dice in the game */
	public static final int N_DICE = 5;

/** The maximum number of players */
	public static final int MAX_PLAYERS = 4;

/** The total number of categories */
	public static final int N_CATEGORIES = 17;

/** The number of categories in which the player can score */
	public static final int N_SCORING_CATEGORIES = 13;

/** The constants that specify categories on the scoresheet */
	public static final int ONES = 1;
	public static final int TWOS = 2;
	public static final int THREES = 3;
	public static final int FOURS = 4;
	public static final int FIVES = 5;
	public static final int SIXES = 6;
	public static final int UPPER_SCORE = 7;
	public static final int UPPER_BONUS = 8;
	public static final int THREE_OF_A_KIND = 9;
	public static final int FOUR_OF_A_KIND = 10;
	public static final int FULL_HOUSE = 11;
	public static final int SMALL_STRAIGHT = 12;
	public static final int LARGE_STRAIGHT = 13;
	public static final int YAHTZEE = 14;
	public static final int CHANCE = 15;
	public static final int LOWER_SCORE = 16;
	public static final int TOTAL = 17;
	
/** The lowest and highest dice roll */
	public static final int DIE_LOW = 1;
	public static final int DIE_HIGH = 6;
	
/** Maximum rolls per player */
	public static final int MAX_ROLLS = 3;
	
/** Difference between match score and index above */
	public static final int MATCH_DELTA = 6;
	
/** The points rewarded for different rolls */
	public static final int FULL_HOUSE_PTS = 25;
	public static final int SM_STAIGHT_PTS = 30;
	public static final int LG_STRAIGHT_PTS = 40;
	public static final int YAHTZEE_PTS = 50;
	public static final int UP_BONUS_THRESH = 63;
	public static final int UP_BONUS_PTS = 35;
	
/** Messages displayed during game */
	public static final String PLYR_ROLL_MSG = "'s turn. Click \"Roll Dice\" button to roll the dice.";
	public static final String RE_ROLL_MSG = "Select the dice you wish to re-roll and click \"Roll Again\".";
	public static final String SEL_CAT_MSG = "Select a category for this roll.";
	public static final String SEL_DIF_CAT_MSG = "That category has already been used. Select a different category.";
  
}
