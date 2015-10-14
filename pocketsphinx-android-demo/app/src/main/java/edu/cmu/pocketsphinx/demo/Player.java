package edu.cmu.pocketsphinx.demo;

public class Player
{
    private String name;
    private int score;
    public Player(String name)
    {
        this.name = name;
        this.score = 0;
    }
    
    public String getName()
    {
        return name;
    }
    
    public int getScore()
    {
        return score;
    }
    
    public void addPoint()
    {
        score++;
    }
    
    public void resetScore()
    {
        score = 0;
    }
    
    public void decresePoint()
    {
        score--;
    }
}