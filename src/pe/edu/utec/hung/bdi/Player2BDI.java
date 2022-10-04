package pe.edu.utec.hung.bdi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalRecurCondition;
import jadex.bdiv3.annotation.GoalTargetCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.types.message.MessageType;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentFeature;
import jadex.micro.annotation.AgentMessageArrived;
import jadex.micro.annotation.Description;
import pe.edu.utec.hung.ui.FrameView;
import pe.edu.utec.hung.util.Alphabet;

@Agent
@Description("Guesser player")
public class Player2BDI {

	static Logger logger = Logger.getLogger(Player2BDI.class.getName());

	@AgentFeature
	protected IBDIAgentFeature bdiAgentFeature;

	@Agent
	protected IInternalAccess internalAccess;

	@AgentFeature
	protected IMessageFeature messageFeature;

	@AgentFeature
	protected IExecutionFeature executionFeature;

	@Belief
	protected List<String> usedLetters;

	@Belief
	protected Boolean gameEnd;

	@Belief
	protected Integer failedAttempts;

	@Belief
	protected Integer maxAttempts;

	LinkedList<String> listOfLetterWithHighProbability = Alphabet.asLinkedList();
	
	private Random random = new Random();

	@AgentCreated
	public void init() {
		System.out.println("[Guesser] player created");

		usedLetters = new ArrayList<>();

		gameEnd = false;

		failedAttempts = 0;

		maxAttempts = 6;
	}

	@Goal()
	public class GuessWord {
		@GoalRecurCondition(beliefs = "gameEnd")
		protected boolean maintain() {
			return !gameEnd;
		}

		@GoalTargetCondition(beliefs = "gameEnd")
		protected boolean target() {
			return gameEnd;
		}
	}

	@Plan(trigger = @Trigger(goals = GuessWord.class))
	protected void thinkAndSelectAnotherLetter() {
		String nextLetter = pickNextLetter();

		usedLetters.add(nextLetter);
	}

	@Plan(trigger = @Trigger(factaddeds = "usedLetters"))
	protected void sendSelectedLetter() {
		String nextLetter = usedLetters.get(usedLetters.size() - 1);
		logger.info("Selected letter: " + nextLetter);
		FrameView.attemptLabel.setText("Player 1 attempt: " + nextLetter);

		reply.put(SFipa.CONTENT, nextLetter);
		reply.put(SFipa.SENDER, internalAccess.getComponentIdentifier());

		executionFeature.waitForDelay(1000L).get();
		messageFeature.sendMessage(reply, SFipa.FIPA_MESSAGE_TYPE).get();
	}

	@AgentBody
	public void body() {
	}

	private Map<String, Object> reply;

	@AgentMessageArrived
	public void messageArrived(Map<String, Object> msg, final MessageType mt) {
		if (gameEnd) {
			logger.info(failedAttempts < maxAttempts ? "Player2 won the game" : "Player 1 won the game");
		} else {
			if (msg.get(SFipa.CONTENT) instanceof Boolean) {
				if ((Boolean) msg.get(SFipa.CONTENT) == false) {
					failedAttempts++;
				}

				gameEnd = failedAttempts == maxAttempts;
				
			} else {
				String[] guessedLetters = (String[]) msg.get(SFipa.CONTENT);

				Boolean continueGame = Arrays.stream(guessedLetters).anyMatch(o -> o == null);

				gameEnd = !continueGame;

				reply = mt.createReply(msg);

				bdiAgentFeature.dispatchTopLevelGoal(new GuessWord()).get();
			}
		}
	}

	private String pickNextLetter() {
		String selectedLetter = "_";
		do {
			String next = "";
			// Getting true the 85% of times, to prefer the letters with more probability
			// This allows to pull the least probable letters from time to time to reduce the chance to loss a turn
			Boolean radomBoolean = getRandomBoolean(0.85f);
			if (radomBoolean) 
				next = listOfLetterWithHighProbability.poll();
			else
				next = listOfLetterWithHighProbability.pollLast();
			if (!usedLetters.contains(next)) {
				selectedLetter = next;
			}
		} while (selectedLetter == "_");

		return selectedLetter;
	}
	
	// Gets true or false with bias
	private boolean getRandomBoolean(float p){
	    return random.nextFloat() < p;
	}
}
