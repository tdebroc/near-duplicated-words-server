package com.grooptown.snorkunking.web.rest;

import com.grooptown.snorkunking.service.game.Game;
import com.grooptown.snorkunking.service.game.Message;
import com.grooptown.snorkunking.service.game.Player;
import com.grooptown.snorkunking.service.game.PlayerInstance;
import com.grooptown.snorkunking.service.game.connector.MessageResponse;
import com.grooptown.snorkunking.service.game.moves.Move;
import com.grooptown.snorkunking.service.game.moves.MoveManager;
import com.grooptown.snorkunking.service.game.moves.RecordMove;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import static com.grooptown.snorkunking.service.game.moves.MoveManager.getNextMove;

/**
 * Created by thibautdebroca on 26/11/2017.
 */
@RestController
@RequestMapping("/api/iaconnector")
public class IAConnectorResource {

    @Autowired
    SimpMessageSendingOperations messagingTemplate;


    int MIN_WORD_LENGTH = 3;
    int NUMBER_CHAR_TO_KEEP_IN_THE_WORD = 5;
    int NUMBER_OF_CHAR_DISTANCE = 50;

    @PostMapping("/findDuplicates")
    public Response findDuplicates(@RequestBody String myText)
            throws InterruptedException {
        System.out.println(myText);
        String[] lines = myText.split("\n");
        LinkedBlockingQueue currentWords = new LinkedBlockingQueue();

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];

            String[] words = line.split(" ");
            for (int j = 0; j < words.length; j++) {
                String word = words[j];
                String wordResult = word;
                if (word != null && word.length() > MIN_WORD_LENGTH) {
                        String wordToTest = word.toLowerCase();
                        wordToTest =
                            wordToTest.length() >  NUMBER_CHAR_TO_KEEP_IN_THE_WORD ?
                            wordToTest.substring(0, NUMBER_CHAR_TO_KEEP_IN_THE_WORD) : wordToTest;

                    if (currentWords.contains(wordToTest)) {
                        // result.append("## ALERT : " + SmallWord + " is duplicated\n");
                        // result.append(line + "\n\n");
                        wordResult =
                            "<span style=\"color:red\">" + word + "</span>";
                    }
                    currentWords.put(word);
                    if (currentWords.size() > NUMBER_OF_CHAR_DISTANCE) {
                        currentWords.poll();
                    }
                }
                result.append(wordResult);
                result.append(" ");
            }
            result.append("<br/>");

        }
        Response textResult = new Response();
        textResult.textResult = result.toString();
        return textResult;
    }

    public class Response {
        String textResult;

        public String getTextResult() {
            return textResult;
        }

        public void setTextResult(String textResult) {
            this.textResult = textResult;
        }
    }

    //==================================================================================================================
    //= Sockets
    //==================================================================================================================
    private void refreshGames() {
        if (messagingTemplate != null) {
            messagingTemplate.convertAndSend("/topic/refreshGames", "test");
        }
    }

    private void refreshGame(Game game) {
        messagingTemplate.convertAndSend("/topic/refreshGame", game);
    }


}
