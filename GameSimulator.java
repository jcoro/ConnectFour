import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Runs a simulation of games, adapts code from Connect4Panel.
 * Upshot is this runs without any Swing GUI so it should be reasonably quick,
 * though terminal IO (printf's etc) expense will add up over many iterations.
 */
public class GameSimulator {

    private final static Logger LOG = Logger.getLogger(GameSimulator.class.getName());


    private static final int SIM_ITERATIONS = 1000;

    private enum Outcome { RED_WINS, YELLOW_WINS, DRAW_GAME, GAME_ERRORED };

    private static Map<String, Integer> errors = new HashMap<>();


    private static Outcome playGame(Connect4Game game, Agent redAgent,
                                    Agent yellowAgent) {
        Random rng = new Random();

        game.clearBoard();

        boolean gameActive = true;
        boolean redPlayerturn = rng.nextBoolean();

        game.setRedPlayedFirst(redPlayerturn);

        while (gameActive) {
            Connect4Game oldBoard = new Connect4Game(game);

            if (redPlayerturn) {
                redAgent.move();
            } else {
                yellowAgent.move();
            }
            redPlayerturn = !redPlayerturn;

            String validateResult = oldBoard.validate(game);
            if (validateResult.length() > 0) {
                // log error and stop this game
                Integer numErrors = GameSimulator.errors.get(validateResult);
                if (numErrors == null) { numErrors = 0; }

                GameSimulator.errors.put(validateResult, ++numErrors);

                gameActive = false;
            }

            if (game.gameWon() != 'N' || game.boardFull()) {
                // the game has been won or there's a draw
                gameActive = false;
            }
        }

        switch (game.gameWon()) {
            case 'R': { return Outcome.RED_WINS; }
            case 'Y': { return Outcome.YELLOW_WINS; }
            default: {
                if (game.boardFull()) { return Outcome.DRAW_GAME; }
                return Outcome.GAME_ERRORED;
            }
        }
    }


    public static void main(String[] args) {
        int redWins = 0, yellowWins = 0, draws = 0, gameErrors = 0;

        String redName = "ERR", yellowName = "ERR";

        // SwagAgent is a bit noisy for running a lot of iterations
       

        for (int i = 0; i < SIM_ITERATIONS; i++) {
            Connect4Game game = new Connect4Game(7, 6);

            Agent redAgent    = new MyAgent2(game, true); // set to your agent
            Agent yellowAgent = new BrilliantAgent(game, false);

            redName    = redAgent.getName();
            yellowName = yellowAgent.getName();

            switch (GameSimulator.playGame(game, redAgent, yellowAgent)) {
                case RED_WINS:     { redWins++;    } break;
                case YELLOW_WINS:  { yellowWins++; } break;
                case DRAW_GAME:    { draws++;      } break;
                case GAME_ERRORED: { gameErrors++; } break;
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n===== SIM COMPLETE =====\n");
        sb.append(String.format("\"%s\" as RED\n\"%s\" as YELLOW\n", redName, yellowName));
        sb.append("                 Amt    Pct\n");
        sb.append(String.format("  Red Wins:     %5d  %6.2f%%\n", redWins,    redWins   /(double)SIM_ITERATIONS*100));
        sb.append(String.format("  Yellow Wins:  %5d  %6.2f%%\n", yellowWins, yellowWins/(double)SIM_ITERATIONS*100));
        sb.append(String.format("  Draws:        %5d  %6.2f%%\n", draws,      draws     /(double)SIM_ITERATIONS*100));
        sb.append(String.format("  Errors:       %5d  %6.2f%%\n", gameErrors, gameErrors/(double)SIM_ITERATIONS*100));
        for (String key : GameSimulator.errors.keySet()) {
            int numErrors = GameSimulator.errors.get(key);
            sb.append(String.format("   %5dx: \"%s\"\n", numErrors, key));
        }
        sb.append("========================\n");
        LOG.info(sb.toString());
    }
}