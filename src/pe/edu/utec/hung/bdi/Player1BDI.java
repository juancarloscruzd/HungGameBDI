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
	protected IMessageFeature messageFeature;

	@AgentFeature
	protected IBDIAgentFeature bdiAgentFeature;

	@AgentArgument
	protected IComponentIdentifier player2;

	@Belief
	private Set<String> spanishWords;

	@Belief
	protected String secretWord;

	@Belief
	protected String[] guessedLetters;

	@Belief
	protected Integer gameCurrentState;

	@AgentCreated
	public void init() {
		gameCurrentState = 0;
		logger.info("Player1 created successfuly...");

		spanishWords = loadWords();
		secretWord = pickWord(spanishWords);

		logger.info("Selected word: " + secretWord);
		
		FrameView.selectedWordLabel.setText("Selected word: " + secretWord);

		guessedLetters = new String[secretWord.length()];
	}

	@AgentBody
	public void body() {
		logCurrentGameState();
		emitPartialResult();
	}

	@AgentMessageArrived
	public void messageArrived(Map<String, Object> msg, MessageType mt) {
		String letter = ((String) msg.get(SFipa.CONTENT)).toLowerCase();

		Boolean letterIsCorrect = checkAttempt(letter);

		if (!letterIsCorrect) {
			gameCurrentState++;
		}

		logCurrentGameState();

		emitGameStatus(letterIsCorrect);
		emitPartialResult();
	}

	private Boolean checkAttempt(String letter) {
		Boolean guessed = false;
		for (int i = 0; i < secretWord.length(); i++) {
			if (secretWord.charAt(i) == letter.charAt(0)) {
				guessedLetters[i] = letter;
				guessed = true;
			}
		}

		return guessed;
	}

	private void emitPartialResult() {
		Map<String, Object> msg = new HashMap<String, Object>();
		msg.put(SFipa.CONTENT, guessedLetters);
		msg.put(SFipa.RECEIVERS, player2);

		messageFeature.sendMessage(msg, SFipa.FIPA_MESSAGE_TYPE).get();
	}
	
	private void emitGameStatus(Boolean result) {
		Map<String, Object> msg = new HashMap<String, Object>();
		msg.put(SFipa.CONTENT, result);
		msg.put(SFipa.RECEIVERS, player2);

		messageFeature.sendMessage(msg, SFipa.FIPA_MESSAGE_TYPE).get();
	}


	private String pickWord(Set<String> words) {
		Integer randomIndex = new Random().nextInt(words.size());
		return (String) words.toArray()[randomIndex];
	}

	private Set<String> loadWords() {
		InputStream inputStream = getClass().getResourceAsStream("/corpus");
		try (Scanner wordsFile = new Scanner(inputStream)) {
			Set<String> setOfWords = new HashSet<>();
			while (wordsFile.hasNext()) {
				setOfWords.add(wordsFile.nextLine().trim().toLowerCase());
			}
			return setOfWords;
		}
	}

	private void logCurrentGameState() {
		String guessedWord = String.join(" ",
				Arrays.stream(guessedLetters).map(o -> o == null ? "_" : o).collect(Collectors.toList()));

		logger.info("Progress: " + guessedWord);
		FrameView.gameStateLabel.setIcon(new ImageIcon(String.format("resources/images/%s.gif", gameCurrentState + 1)));
		FrameView.progressLabel.setText("Progress: " + guessedWord);
	}

}
