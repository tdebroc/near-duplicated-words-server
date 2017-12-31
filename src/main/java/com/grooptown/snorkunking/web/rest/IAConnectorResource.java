package com.grooptown.snorkunking.web.rest;

import com.grooptown.snorkunking.service.game.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
    int NUMBER_OF_WORD_DISTANCE = 50;

    String[] colors = {"red", "orange", "blue", "green", "magenta", "purple", "#BCA136", "grey"};


    @PostMapping("/findDuplicates")
    public Response findDuplicates(@RequestBody String myText,
                                   @RequestParam Integer minWordLength,
                                   @RequestParam Integer numberCharToKeepInTheWord,
                                   @RequestParam Integer numberOfWordDistance
                                   )
            throws InterruptedException {

        minWordLength = minWordLength != null ? minWordLength : MIN_WORD_LENGTH;
        numberCharToKeepInTheWord = numberCharToKeepInTheWord != null ? numberCharToKeepInTheWord : NUMBER_CHAR_TO_KEEP_IN_THE_WORD;
        numberOfWordDistance = numberOfWordDistance != null ? numberOfWordDistance : NUMBER_OF_WORD_DISTANCE;

        String[] lines = myText.split("\n");
        LinkedList<Word> currentWords = new LinkedList();
        int colorIndex = 0;

        int numberDuplicated = 0;
        List<Word> allWords = new ArrayList<>();
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];

            String[] words = line.split(" ");
            for (int j = 0; j < words.length; j++) {
                String word = words[j];
                if (word != null && word.length() > minWordLength) {
                        String wordToTest = word.toLowerCase();
                        wordToTest =
                            wordToTest.length() >  numberCharToKeepInTheWord ?
                            wordToTest.substring(0, numberCharToKeepInTheWord) : wordToTest;

                    Word wordFound = getWord(currentWords, wordToTest);
                    String color = null;
                    if (wordFound != null) {
                        if (wordFound.getTargetWord().getColor() == null) {
                            colorIndex = getNextColorIndex(colorIndex);
                            wordFound.getTargetWord().setColor(colors[colorIndex]);
                        }
                        numberDuplicated++;
                        color = wordFound.getTargetWord().getColor();
                    }

                    Word wordForAllWord = new Word(word);
                    wordForAllWord.setColor(color);
                    allWords.add(wordForAllWord);

                    Word wordForCurrentWord = new Word(wordToTest);
                    wordForCurrentWord.setTargetWord(wordForAllWord);
                    wordForCurrentWord.setColor(color);
                    currentWords.add(wordForCurrentWord);


                    if (currentWords.size() > numberOfWordDistance) {
                        currentWords.pollFirst();
                    }
                } else {
                    allWords.add(new Word(word));
                }
                allWords.add(new Word(" "));
            }
            allWords.add(new Word("<br/>"));

        }
        StringBuilder result = new StringBuilder();
        for (Word word : allWords) {
            if (word.getColor() != null) {
                result.append("<span style=\"color:" + word.getColor() + "\">" + word.getWord() +"</span>");
            } else {
                result.append(word.getWord());
            }
        }
        Response textResult = new Response();
        textResult.textResult = result.toString();
        textResult.numberDuplicated = numberDuplicated;
        return textResult;
    }

    int getNextColorIndex(int currentColorIndex) {
        currentColorIndex++;
        if (currentColorIndex >= colors.length) {
            currentColorIndex = 0;
        }
        return currentColorIndex;
    }

    public Word getWord(List<Word> words, String wordValue) {
        for (Word word : words) {
            if (word.getWord().equals(wordValue)) {
                return word;
            }
        }
        return null;
    }

    public class Word {
        String word;
        String color;
        Word targetWord;

        public Word(String s) {
            word = s;
        }

        public Word getTargetWord() {
            return targetWord;
        }

        public void setTargetWord(Word targetWord) {
            this.targetWord = targetWord;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        @Override
        public String toString() {
            return "Word{" +
                "word='" + word + '\'' +
                ", color='" + color + '\'' +
                '}';
        }
    }

    public class Response {
        String textResult;
        int numberDuplicated;

        public int getNumberDuplicated() {
            return numberDuplicated;
        }

        public void setNumberDuplicated(int numberDuplicated) {
            this.numberDuplicated = numberDuplicated;
        }

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
