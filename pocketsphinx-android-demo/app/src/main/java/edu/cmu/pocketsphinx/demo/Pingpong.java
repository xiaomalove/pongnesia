package edu.cmu.pocketsphinx.demo;
import java.util.*;

//bug list:
//wrong and winStraight

public class Pingpong
{
    private Player player1;
    private Player player2;
    private boolean done;
    private String prePointOwner, command;
    private int preServe, serve, total, winStraight;
    private String[] funWords = {"fun words 1",
                                 "fun words 2",
                                 "fun words 3",
                                 "fun words 4",
                                 "fun words 5"};
    
    public Pingpong(String player1, String player2)// init with a pass in SR
    {
        this.player1 = new Player(player1);
        this.player2 = new Player(player2);
        done = false;
        prePointOwner = command = null;
        preServe = 0;
        serve = 0;
        total = 0;
        winStraight = 0;
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
    }
    
    public void point(String player)
    {
        calWinStraight(prePointOwner, player);
        addPoint(player);
        prePointOwner = player;
        displayScore();
        preServe = serve;
        updateServe();
        checkIfDone();
    }
    
    public void rematch()
    {
        player1.resetScore();
        player2.resetScore();
        total = 0;
        serve = 0;
        winStraight = 0;
    }
    
    public void restorePoint()
    {
        total -= 1;
        serve = preServe;
        decresePoint();
        checkIfDone();
        displayScore();
    }
    
    public void serve()
    {
        if (serve == 0)
            System.out.println(player1.getName() + " serve");// TODO: SR out
        else
            System.out.println(player2.getName() + " serve");// TODO: SR out
    }
    
    public void calWinStraight(String prePointOwner, String currPointOwner)
    {
        if (prePointOwner == null)
        {
            winStraight = 1;
            return;
        }
        
        Random random = new Random();
        int i = random.nextInt(funWords.length);
        if (prePointOwner.equals(currPointOwner))
            winStraight += 1;
        else
            winStraight = 0;
        
        if (winStraight >= 3)
        {
            System.out.println(winStraight + " in a row!!!");// TODO: SR out
            System.out.println(funWords[i]);
        }
    }
    
    public void addPoint(String pointEarner)
    {
        if (pointEarner.equals(player1.getName()))
        {
            player1.addPoint();
            total += 1;
        }
        else if (pointEarner.equals(player2.getName()))
        {
            player2.addPoint();
            total += 1;
        }
    }
    
    public void displayScore()// TODO: SR out
    {
        System.out.print("Current score: \n" + player1.getName() + " " + player1.getScore() + ": ");
        System.out.println(player2.getScore() + " " + player2.getName());
    }
    
    
    public void updateServe()
    {
        if (player1.getScore() >= 10 && player2.getScore() >= 10)
            serve = (serve + 1) % 2;
        else if (total > 0 && total % 2 == 0)
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
    }
}