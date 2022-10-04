package pe.edu.utec.hung.bdi;

import java.util.ArrayList;
import java.util.Arrays;
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
    protected List<String> usedLetters; // Creencia LetrasUtilizadas

    @Belief
    protected Boolean gameOver; // Creencia FinJuego

    @Belief
    protected Integer failedAttempts;

    @Belief
    protected Integer maxAttempts;

    @AgentCreated
    public void init() {
        System.out.println("[Guesser] player created");

        usedLetters = new ArrayList<>();

        gameOver = false;

        failedAttempts = 0;

        maxAttempts = 6;
    }

    @Goal()
    public class GuessWord {
        @GoalRecurCondition(beliefs = "gameOver")
        protected boolean maintain() {
            return !gameOver;
        }
        @GoalTargetCondition(beliefs = "gameOver")
        protected boolean target() {
            return gameOver;
        }
    }

    @Plan(trigger = @Trigger(goals = GuessWord.class))
    protected void thinkAndSelectAnotherLetter (){
        String nextLetter = getNextRandomLetter();

        usedLetters.add(nextLetter);
    }

    @Plan(trigger = @Trigger(factaddeds = "usedLetters"))
    protected void sendSelectedLetter() {
        String nextLetter = usedLetters.get(usedLetters.size() - 1);
        System.out.println("[Guesser] selected letter: " + nextLetter);

        reply.put(SFipa.CONTENT, nextLetter);
        reply.put(SFipa.SENDER, internalAccess.getComponentIdentifier());

        executionFeature.waitForDelay(1000L).get();
        messageFeature.sendMessage(reply, SFipa.FIPA_MESSAGE_TYPE).get();
    }

    @AgentBody
    public void body() {
        // ...
    }

    private Map<String, Object> reply;

    @AgentMessageArrived
    public void messageArrived(Map<String, Object> msg, final MessageType mt)
    {
        if (gameOver) {
            System.out.println(failedAttempts < maxAttempts ? "Guesser wins" : "Hangman wins");
        } else {
            if (msg.get(SFipa.CONTENT) instanceof Boolean) {
                if ((Boolean) msg.get(SFipa.CONTENT) == false) {
                    failedAttempts++;
                }

                gameOver = failedAttempts == maxAttempts;
            } else {
                String[] guessedLetters = (String[]) msg.get(SFipa.CONTENT);

                Boolean hasMissingLetters = Arrays.stream(guessedLetters).anyMatch(o -> o == null);

                gameOver = !hasMissingLetters;

                // Set the reply object
                reply = mt.createReply(msg);

                // Start the Goal GuessWord
                bdiAgentFeature.dispatchTopLevelGoal(new GuessWord()).get();
            }
        }
    }

    private String getNextRandomLetter() {
        String nextLetter = "_";
        do {
            String randomLetter = Alphabet.asArray[new Random().nextInt(Alphabet.asArray.length)];
            if (!usedLetters.contains(randomLetter)){
                nextLetter = randomLetter;
            }
        }while(nextLetter == "_");

        return nextLetter;
    }
}
