package edu.cmu.pocketsphinx.demo;
import android.speech.tts.TextToSpeech;

import java.util.*;

//bug list:
//wrong and winStraight

public class Pingpong
{
    private Player player1;
    private Player player2;
    private boolean done;
    private ArrayList<String> records;
    private int serve, winStraight, recordsCount;
    private TextToSpeech t1;
    private String[] funWords = {"fun words 1",
            "fun words 2",
            "fun words 3",
            "fun words 4",
            "fun words 5"};

    public Pingpong(String player1, String player2, TextToSpeech speaker)// init with a pass in SR
    {
        this.player1 = new Player(player1);
        this.player2 = new Player(player2);
        done = false;
        serve = 0;
        winStraight = 0;
        recordsCount = 0;
        records = new ArrayList<>();
        t1 = speaker;
    }

    public boolean getDone()
    {
        return done;
    }

    public void gameUpdate(String commands)
    {
        if (commands.equals("serve"))
            serve();
        else if (commands.equals("rematch"))
            rematch();
        else if (commands.equals("pointwhite"))
            point(commands);
        else if (commands.equals("pointblack"))
            point(commands);
        else if (commands.equals("wrong"))
            restorePoint();
        else if (commands.equals("score"))
            displayScore();
//        else if (commands.equals("a"))
//            point(commands);
//        else if (commands.equals("b"))
//            point(commands);
    }

    public void point(String player)
    {
        addPoint(player);
        displayScore();
        calWinStraight();
        updateServe();
        checkIfDone();
    }

    public void rematch()
    {
        player1.resetScore();
        player2.resetScore();
        recordsCount = 0;
        serve = 0;
        winStraight = 0;
    }

    public void restorePoint()
    {
        if (recordsCount > 0)
        {
            updateServe();
            decresePoint();
            checkIfDone();
            displayScore();
            calWinStraight();
        }
        else
            speakToUser("There is no point to decrease");
//        else
//            System.out.println("There is no point to decrease");
    }

    public void serve()
    {
        if (serve == 0)
            System.out.println(player1.getName() + " serve");// TODO: SR out
        else
            System.out.println(player2.getName() + " serve");// TODO: SR out

        if (serve == 0)
            speakToUser(player1.getName() + " serve");
        else
            speakToUser(player2.getName() + " serve");

    }

    public void calWinStraight()
    {
        winStraight = 1;
        if (recordsCount == 0)
        {
            return;
        }
        else
        {
            String curr = records.get(recordsCount - 1);
            for (int i = recordsCount - 2; i >= 0; i--)
            {
                if (records.get(i).equals(curr))
                    winStraight++;
                else
                    break;
            }
        }

        if (winStraight >= 3)
        {
            Random random = new Random();
            int i = random.nextInt(funWords.length);
            System.out.println(winStraight + " in a row!!!");// TODO: SR out
//            System.out.println(funWords[i]);
            speakToUser(winStraight + " in a row!!!");
        }
    }

    public void addPoint(String pointEarner)
    {
        if (pointEarner.equals(player1.getName()))
        {
            player1.addPoint();
            records.add(recordsCount ,pointEarner);
            recordsCount++;
        }
        else if (pointEarner.equals(player2.getName()))
        {
            player2.addPoint();
            records.add(recordsCount ,pointEarner);
            recordsCount++;
        }
    }

    public void displayScore()// TODO: SR out
    {
        System.out.print("Current score: \n" + player1.getName() + " " + player1.getScore() + ": ");
        System.out.println(player2.getScore() + " " + player2.getName());
        speakToUser("Current score: \n" + player1.getName() + " " + player1.getScore() + ": " + player2.getScore() + " " + player2.getName());
    }


    public void updateServe()
    {
        if (player1.getScore() >= 10 && player2.getScore() >= 10)
            serve = (serve + 1) % 2;
        else if (recordsCount % 2 == 0)
            serve = (serve + 1) % 2;
    }

    public void checkIfDone()
    {
        if (player1.getScore() >= 11 || player2.getScore() >= 11)
            if (player1.getScore() - player2.getScore() >= 2 ||
                    player1.getScore() - player2.getScore() <= -2)
                done = true;
    }

    public void decresePoint()
    {
        String prePointOwner = records.get(recordsCount - 1);
        recordsCount--;
        if (prePointOwner.equals(player1.getName()))
            player1.decresePoint();
        else if (prePointOwner.equals(player2.getName()))
            player2.decresePoint();
    }

    public void winingMessage()
    {
        if (player1.getScore() > player2.getScore())
            System.out.println(player1.getName() + " win");// TODO: SR out
        else
            System.out.println(player2.getName() + " win");// TODO: SR out

        if (player1.getScore() > player2.getScore())
            speakToUser(player1.getName() + " win");
        else
            speakToUser(player1.getName() + " win");
    }

    public void speakToUser(String words)
    {
        boolean end = false;
        while (!end)
            if (!t1.isSpeaking())
                t1.speak(words, TextToSpeech.QUEUE_FLUSH, null);
            else
                end = true;
    }
}