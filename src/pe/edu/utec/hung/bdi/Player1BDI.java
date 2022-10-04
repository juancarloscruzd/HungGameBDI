package pe.edu.utec.hung.bdi;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.swing.ImageIcon;

import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.types.message.MessageType;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentFeature;
import jadex.micro.annotation.AgentMessageArrived;
import jadex.micro.annotation.Description;
import pe.edu.utec.hung.ui.FrameView;

@Agent
@Description("Player1")
public class Player1BDI {

	static Logger logger = Logger.getLogger(Player1BDI.class.getName());

	@AgentFeature
	protected IBDIAgentFeature bdiAgentFeature;

	@AgentFeature
	protected IMessageFeature messageFeature;

	@AgentArgument
	protected IComponentIdentifier guesser;

	@Belief
	private Set<String> words; // PalabrasEspañol

	@Belief
	protected String wordToGuess; // Creencia PalabraSecreta

	@Belief
	protected String[] guessedLetters; // Creencia PalabraAdivinada

	@Belief
	protected Integer hangmanState; // Creencia ParteAhorcadoActual
	
	
	@AgentCreated
	public void init() {
		System.out.println("[Hangman] player created");
		words = getSpanishWords();
		wordToGuess = pickRandomWord(words);
		System.out.println("[Hangman] word to guess: " + wordToGuess);

		guessedLetters = new String[wordToGuess.length()];
		hangmanState = 0;
	}

	@AgentBody
	public void body() {
		System.out.println("[Hangman] the game started");
		printHangmanState();
		sendGuessedLetters();
	}

	@AgentMessageArrived
	public void messageArrived(Map<String, Object> msg, MessageType mt) {
		String letter = ((String) msg.get(SFipa.CONTENT)).toLowerCase();

		Boolean letterIsCorrect = updateGuessedLetter(letter);

		if (!letterIsCorrect) {
			hangmanState++;
		}

		printHangmanState();

		sendLetterStatus(letterIsCorrect);
		sendGuessedLetters();
	}

	private void sendLetterStatus(Boolean letterIsCorrect) {
		Map<String, Object> msg = new HashMap<String, Object>();
		msg.put(SFipa.CONTENT, letterIsCorrect);
		msg.put(SFipa.RECEIVERS, guesser);

		messageFeature.sendMessage(msg, SFipa.FIPA_MESSAGE_TYPE).get();
	}

	private void printHangmanState() {
		String guessedWord = String.join(" ",
				Arrays.stream(guessedLetters).map(o -> o == null ? "_" : o).collect(Collectors.toList()));

		System.out.println("[Hangman] word: " + guessedWord);
		System.out.println(hangmanStates[hangmanState]);
		FrameView.myLabel.setIcon(new ImageIcon(String.format("resources/images/%s.gif", hangmanState + 1) ));
	}

	private Boolean updateGuessedLetter(String letter) {
		Boolean guessed = false;
		for (int i = 0; i < wordToGuess.length(); i++) {
			if (wordToGuess.charAt(i) == letter.charAt(0)) {
				guessedLetters[i] = letter;
				guessed = true;
			}
		}

		return guessed;
	}

	private void sendGuessedLetters() {
		Map<String, Object> msg = new HashMap<String, Object>();
		msg.put(SFipa.CONTENT, guessedLetters);
		msg.put(SFipa.RECEIVERS, guesser);

		messageFeature.sendMessage(msg, SFipa.FIPA_MESSAGE_TYPE).get();
	}

	private String pickRandomWord(Set<String> setOfWords) {
		// get a random number between 0 and the set size;
		Integer randomIndex = new Random().nextInt(setOfWords.size());

		// get the word at that index.
		return (String) setOfWords.toArray()[randomIndex];
	}

	private Set<String> getSpanishWords() {
		// Read words.txt file
		InputStream inputStream = getClass().getResourceAsStream("/corpus");
		try (Scanner wordsFile = new Scanner(inputStream)) {
			// Put each line of the file into a set.
			Set<String> setOfWords = new HashSet<>();
			while (wordsFile.hasNext()) {
				setOfWords.add(wordsFile.nextLine().trim().toLowerCase());
			}
			return setOfWords;
		}
	}

	private final String[] hangmanStates = new String[] {
			"\n       __________\n       |        |\n       |\n       |\n       |\n       |\n       |\n    ========\n",
			"\n       __________\n       |        |\n       |        O\n       |\n       |\n       |\n       |\n    ========\n",
			"\n       __________\n       |        |\n       |        O\n       |        |\n       |        |\n       |\n       |\n    ========\n",
			"\n       __________\n       |        |\n       |        O\n       |        |\n       |        |\n       |       /\n       |\n    ========\n",
			"\n       __________\n       |        |\n       |        O\n       |        |\n       |        |\n       |       / \\\n       |\n    ========\n",
			"\n       __________\n       |        |\n       |        O\n       |      --|\n       |        |\n       |       / \\\n       |\n    ========\n",
			"\n       __________\n       |        |\n       |        O\n       |      --|--\n       |        |\n       |       / \\\n       |\n    ========\n" };
}
