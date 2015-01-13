// BlackJackApp.java

// Import of the Java API Classes
import java.lang.System;
import java.lang.Integer;
import java.lang.NumberFormatException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Random;

public class BlackJackApp {
	public static void main(String[] args) throws IOException {
		// Construct the BlckJackGame Object
		BlackJackGame game = new BlackJackGame();

		//My own code starts here
		BufferedReader keyboardInput = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Is this going to loop? y or n: ");
		char loop = keyboardInput.readLine().charAt(0);

		if (loop == 'y') {
			System.out.println("How many loops? ");
			game.setLoop(Integer.parseInt(keyboardInput.readLine()));
		}
		//My own code ends here

		// ... and start playing it.
		game.play();
	}
}

/**
 * Class BlackJackGame
 * Function: The primary class with constructor and "play" methods invoked from the BlackJackApp method (main).
 *           http://en.wikipedia.org/wiki/Blackjack  
 * 
 * Data Members:
 * int bet   .... Global value representing the players bet on each hand.
 * int money .... Players total bank roll.
 * BufferedReader keyboardInput ... Persistent object enabling the user to input values from the keyboard.
 * Deck deck ...  Up to 52 shuffled playing cards.
 * Hand playersHand ... Cards held by the one and only player.
 * Hand dealersHand ... Cards held by the dealer.
 * 
 * Methods:
 * Constructor ... Primes the data members and initializes (shuffles) an initial deck.
 * play ...        The primary loop of play, dealing cards, taking input, and showing results.
 *                 Loop exited when player bets a value of 0.
 * placeBet ...    Loops until a valid bet is entered at the keyboard.
 * playerWins ...  Method to output results in the event that a player has won a hand.
 * dealerWins ...  Method to output results in the event that the dealer wins.
 * tie ...         Method outputting results in the even that the hand results in a numerical tie.
 * playerTakesAHit ... Determines - true or false - that the player is asking for a new card.
 * showResults ... Determines whether hand yet has a winner and arranges for appropriate results to be shown.
 */
class BlackJackGame {

	// Data members for BlackJackGame
	int bet;
	int money;
	BufferedReader keyboardInput;

	Deck deck; // The Deck object.

	// "Hands" for both the player and the dealer.
	Hand playersHand;
	Hand dealersHand;

	int loopCount = 0;
	int loopTotal = -1;

	public void setLoop(int loop){
		loopTotal = loop;
	}

	/**
	 * BlackJackGame Constructor
	 */
	public BlackJackGame() {
		bet = 0;
		money = 10000; // Initial players bank roll.
		keyboardInput = new BufferedReader(new InputStreamReader(System.in));
		deck = new Deck();
	}

	void play() throws IOException {
		System.out
		.println("Welcome to the Casino ... We are playing BlackJack here.");
		System.out.println("You have $" + Integer.toString(money)
				+ " available.");

		// Have the dealer start and then continue to lay cards.
		do {
			do {
				if (loopCount < loopTotal){
					if (money > 20){
						bet = 200;
					} else {
						bet = money;
					}
				} else {
					placeBet();
				}
				if (bet > 0) {
					// Initial Deal.
					System.out.println("New hand...");
					playersHand = new Hand();
					dealersHand = new Hand();
					for (int i = 0; i < 2; i++) {
						playersHand.addCard(deck.deal());
						dealersHand.addCard(deck.deal());
					}
					dealersHand.show(true, true);
					playersHand.show(false, false);

					if (playersHand.blackjack())
						playerWins();
					else {
						while (playersHand.under(22) && playerTakesAHit()) {
							playersHand.addCard(deck.deal());
							playersHand.show(false, false);
						}
						while (dealersHand.mustHit())
							dealersHand.addCard(deck.deal());
						dealersHand.show(true, false);
						showResults();
						System.out.println("Total games played: " + (loopCount + 1));
					}
				}
				loopCount++;
			} while (loopCount < loopTotal);
		} while (bet > 0);
	} // End of method play().

	void placeBet() throws IOException, NumberFormatException {
		do {
			System.out.print("Enter bet: ");
			System.out.flush();
			bet = Integer.parseInt(keyboardInput.readLine());
		} while ((bet < 0) || (bet > money));
	} // End of method placeBet().

	void playerWins() {
		money += bet;
		System.out.println("Player Wins $" + Integer.toString(bet) + ".");
		System.out.println("Player Has  $" + Integer.toString(money) + ".");
	} // End of method playerWins().

	void dealerWins() {
		money -= bet;
		System.out.println("Player Loses $" + Integer.toString(bet) + ".");
		System.out.println("Player Has   $" + Integer.toString(money) + ".");
	} // End of method dealerWins().

	void tie() {
		System.out.println("Tie!");
		System.out.println("Player Has   $" + Integer.toString(money) + ".");
	} // End of method tie().

	/**
	 * playerTakesAHit Method will loop until either a H or S is chosen.
	 * 
	 * @return Boolean True if Hit(H), False if Stay(S).
	 * @throws IOException
	 */
	boolean playerTakesAHit() throws IOException {
		char ch = ' ';
		while (true) {
			System.out.print("Hit(H) or Stay(S): ");
			System.out.flush();
			if (loopCount < loopTotal) {
				// This will by my logic if I want to hit or stay.
				if (playersHand.getTotal() < 16)
					return true;
				//			if ((ch == 'H') || (ch == 'h'))
				//				return true;
				//			if ((ch == 'S') || (ch == 's'))
				return false;
			} else {
				String playersDecision = keyboardInput.readLine();
				try {
					ch = playersDecision.charAt(0);
				} catch (StringIndexOutOfBoundsException ex) {
					;
				}
				if ((ch == 'H') || (ch == 'h'))
					return true;
				if ((ch == 'S') || (ch == 's'))
					return false;
				if ((ch == 'P') || (ch == 'p')) deck.peek();
			}
		}
	} // End of method playerTakesAHit().

	void showResults() {
		// Start by determining whether the player and/or the dealer are busted.
		boolean playerBusted = playersHand.busted();
		boolean dealerBusted = dealersHand.busted();
		if (playerBusted && dealerBusted)
			tie();
		else if (playerBusted)
			dealerWins();
		else if (dealerBusted)
			playerWins();

		// Neither is busted.
		// Compare the relative results to see who won.
		else if (playersHand.bestScore() > dealersHand.bestScore())
			playerWins();
		else if (playersHand.bestScore() < dealersHand.bestScore())
			dealerWins();
		else
			tie();
	} // End of method showResults().
} // End of class BlackJackGame

/**
 * Class Deck 
 * Function: To represent the state of the up to 52-card shuffled card deck.
 * 
 * Data Members: 
 * int card Array ... An array of numerical values, each uniquely representing one of 52 playing cards.
 * int topCard ...    The index into the card array representing the next card in the deck to be played.
 * Random random ...  A random value for choosing cards in the deck.
 * 
 * Methods:
 * Constructor ...    Initializes and shuffles the first deck of the game.
 * shuffle ...        Attempts to randomly order the cards of a deck.
 * randomCard ...     An assist method used by shuffle for choosing a random card in shuffling.
 * deal ...           Returns the next card in the deck.
 */
class Deck {
	// Data members
	int cards[]; // The array of cards remaining in this multi-deck "deck".
	int topCard; // The index of the next card in the deck.
	Random random;

	// Deck Constructor
	public Deck() {
		cards = new int[52];
		for (int i = 0; i < 52; i++)
			cards[i] = i;
		topCard = 0;
		random = new Random();
		shuffle();
	}

	public void shuffle() {
		for (int j = 0; j < 3; j++)
			for (int i = 0; i < 52; i++) {
				// Randomly exchange two cards in the deck.
				int m = randomCard();
				int n = randomCard();
				int temp = cards[m];
				cards[m] = cards[n];
				cards[n] = temp;
			}
	} // End of method shuffle().

	int randomCard() {
		int r = random.nextInt();
		if (r < 0)
			r = 0 - r;
		return (r % 52);
	} // End of method randomCard().

	Card deal() {
		if (topCard > 51) {
			shuffle();
			topCard = 0;
		}

		Card card = new Card(cards[topCard]);
		++topCard;
		return card;
	} // End of method deal().
	void peek()
	{
		if (topCard > 51) {
			shuffle();
			topCard = 0;
		}
		Card card = new Card(cards[topCard]);
		System.out.println("  " + card.value + " of " + card.suit);
	}

} // End of class Deck


/**
 * Class Hand 
 * Function: To represent the state of a hand, whether the players or the dealers.
 * 
 * Data Members:
 * int numCards ... The number of cards held by a player (up to a maximum of 12).
 * Card card array ... The set of cards held by a player.
 * 
 * Methods:
 * Constructor ...  Initializes the data members.
 * addCard ...      Adds a card to a hand.
 * show ...         Determines what to show of a hand, based on whether player is dealer or whether this is the first card.
 * blackjack ...    Determines whether the state of the hand is a "Black Jack".
 * under ...        A relative comparison of points, returning a boolean.
 * bestScore ...    Determines the points in a hand, given that neither the player or dealer are busted.
 * mustHit ...      Method to determine whether the dealer must take another card; the dealers score so far is < 17.
 * busted ...       Method which determines whether a hand has exceeded 21 points.
 */
class Hand {
	// Data members
	int numCards;
	Card cards[]; // The cards in a Hand object.
	static final int MaxCards = 12;

	int total = 0;

	public int getTotal(){
		return total;
	}

	// Hand Constructor
	public Hand() {
		numCards = 0;
		cards = new Card[MaxCards]; // Up to a maximum of 12 cards in a Hand.
	} // End of Hand Constructor

	void addCard(Card c) {
		cards[numCards] = c;
		++numCards;
	} // End of method addCard().

	void show(boolean isDealer, boolean hideFirstCard)
	{
		total = 0;
		if (isDealer)
			System.out.println("Dealer: ");
		else
			System.out.println("Player: ");
		for (int i = 0; i < numCards; i++)
		{
			if ((i == 0) && hideFirstCard)
				System.out.println("  Hidden");
			else
				System.out.println("  " + cards[i].value + " of "
						+ cards[i].suit);
			total += cards[i].iValue;
		}
		System.out.println("   Hand total = " + total);
	} // End of method show().

	boolean blackjack()
	{
		if (numCards == 2) {
			if ((cards[0].iValue == 1) && (cards[1].iValue == 10))
				return true;
			if ((cards[1].iValue == 1) && (cards[0].iValue == 10))
				return true;
		}
		return false;
	} // End of method blackjack().

	boolean under(int n) {
		int points = 0;
		for (int i = 0; i < numCards; i++)
			points += cards[i].iValue;
		if (points < n)
			return true;
		else
			return false;
	} // End of method under().

	int bestScore() {
		int points = 0;
		boolean haveAce = false;
		for (int i = 0; i < numCards; ++i) {
			points += cards[i].iValue;
			if (cards[i].iValue == 1)
				haveAce = true;
		}
		if ((haveAce) && ((points + 10) < 22))
			points += 10;
		return points;
	} // End of method bestScore().

	boolean mustHit() {
		if (bestScore() < 17)
			return true;
		return false;
	} // End of method mustHit();

	boolean busted() {
		if (!under(22))
			return true;
		else
			return false;
	} // End of method busted();
} // End of class Hand

/**
 *  Class Card
 *  Function: Allows a card to be treated like an object having card value and "suit".
 *            Used for converting from a value (of 52 states) into a card "value" and "suit".
 */
class Card {
	// Data members
	int iValue;		// Numeric value corresponding to card.
	String value;	// The string values "Ace" "2" through "9", "Ten", "Jack", "Queen", "King".
	String suit;	// "Spades" "Hearts" "Clubs" or "Diamonds".

	// Card Constructor
	public Card(int n)
	{
		int iSuit = n / 13;
		iValue = n % 13 + 1;

		switch(iSuit)
		{
		case 0:		{ suit = "Spades"; break; }
		case 1: 	{ suit = "Hearts"; break; }
		case 2:		{ suit = "Clubs"; break; }
		default: 	{ suit = "Diamonds"; }
		} // End of switch

		if (iValue == 1) value = "Ace";
		else if (iValue == 10) value = "Ten";
		else if (iValue == 11) value = "Jack";
		else if (iValue == 12) value = "Queen";
		else if (iValue == 13) value = "King";
		else value = Integer.toString(iValue);
		if (iValue > 10) iValue = 10;
	} // End of Card Constructor.

	int getValue() { return iValue; }
} // End of class Card