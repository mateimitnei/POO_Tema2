package org.poo.system;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import org.poo.fileio.CommandInput;
import org.poo.fileio.CommerciantInput;
import org.poo.fileio.ObjectInput;
import org.poo.fileio.UserInput;
import org.poo.system.commands.*;

import java.util.ArrayList;

@Getter
public final class Engine {
    private ObjectInput input;
    private ArrayList<User> users;
    private ArrayList<Commerciant> commerciants;
    private ObjectMapper objectMapper;
    private static Engine instance;

    /**
     * Private constructor for Engine so the Singleton can work correctly.
     */
    private Engine() {
    }

    /**
     * Singleton instance for Engine.
     *
     * @return the engine instance
     */
    public static Engine getInstance() {
        if (instance == null) {
            instance = new Engine();
        }
        return instance;
    }

    /**
     * Initializes the engine with the fileInput.
     *
     * @param fileInput the input file in JSON format
     */
    public void init(final ObjectInput fileInput) {
        this.input = fileInput;
        users = new ArrayList<>();
        objectMapper = new ObjectMapper();

        for (UserInput userInput : fileInput.getUsers()) {
            users.add(new User(userInput));
        }
        for (CommerciantInput commerciantInput : fileInput.getCommerciants()) {
            commerciants.add(new Commerciant(commerciantInput));
        }
    }

    /**
     * Executes every command from the input.
     */
    public void execute() {
        for (CommandInput command : input.getCommands()) {

            CommandHandler handler = new CommandHandler();
            switch (command.getCommand()) {
                case "printUsers" -> handler.setStrategy(new PrintUsers());
                case "addAccount" -> handler.setStrategy(new AddAccount());
                case "deleteAccount" -> handler.setStrategy(new DeleteAccount());
                case "createCard", "createOneTimeCard" -> handler.setStrategy(new CreateCard());
                case "deleteCard" -> handler.setStrategy(new DeleteCard());
                case "addFunds" -> handler.setStrategy(new AddFunds());
                case "setMinimumBalance" -> handler.setStrategy(new SetMinBalance());
                case "payOnline" -> handler.setStrategy(new PayOnline());
                case "sendMoney" -> handler.setStrategy(new SendMoney());
                case "printTransactions" -> handler.setStrategy(new PrintTransactions());
                case "checkCardStatus" -> handler.setStrategy(new CardStatus());
                case "changeInterestRate" -> handler.setStrategy(new ChangeInterestRate());
                case "addInterest" -> handler.setStrategy(new AddInterest());
                case "setAlias" -> handler.setStrategy(new SetAlias());
                case "splitPayment" -> handler.setStrategy(new SplitPayment());
                case "report" -> handler.setStrategy(new Report());
                case "spendingsReport" -> handler.setStrategy(new SpendingsReport());
                case "withdrawSavings" -> handler.setStrategy(new WithdrawSavings());
                default -> { }
            }

            if (handler.getStrategy() != null) {
                handler.applyStrategy(command);
            }
        }
    }
}
