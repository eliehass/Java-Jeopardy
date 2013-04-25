import java.util.Random;

public class Host implements Runnable
{
	Random randomNum = new Random(System.currentTimeMillis());
	Thread myThread;
	public static long time = System.currentTimeMillis();
	private int numRounds = 3;
	private int numQuestions = 6;
	private int questionValues = 200;
	private double rightPercent = 0.70;
	private int currentRound = 1;
	private int currentQuestion = 1;
	private static Contestant[] contestants;
	private static boolean finalJeopardy = false;
	private static boolean finalQuestion = false;
	private static boolean finalQuestionAnswered = false;
	
	public  Host(String threadName)
	{
		//takes the array of selected contestants from the announcer
		contestants = Announcer.getSelectedContestants();
		myThread = new Thread(this, threadName);
		myThread.start();
	}
	
	public void run()
	{
		int answeringContestant = 0;

		//sleep
		try {
			myThread.sleep(1000000000);
		} catch (InterruptedException e) {}
			
		while(currentRound <= numRounds)
		{
			this.msg("Lets begin round " + currentRound);
			currentQuestion = 1;
			while(currentQuestion <= numQuestions)
			{
				this.msg("And now here is your question!");
				try {
					myThread.sleep(randomNum.nextInt(5000));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				answeringContestant = randomNum.nextInt(3);
				contestants[answeringContestant].setIsAnswering(true);
				//Busy Wait until the contestant finishes answering
				while(contestants[answeringContestant].getIsAnswering())
				{
					boolean garbageBusyWaitingVariable = true;
					myThread.yield();
				}
				
				//decide if the answer was correct
				if(randomNum.nextInt(100) > (rightPercent * 100))
				{
					this.msg(contestants[answeringContestant].getName() + ", that's correct!");
					contestants[answeringContestant].incrementScore(questionValues);
				}
				else
				{
					this.msg("I'm sorry, " + contestants[answeringContestant].getName() + ", that's incorrect...");
					contestants[answeringContestant].decrementScore(questionValues);
				}
				currentQuestion++;
			}
			currentRound++;
		}
			
		//final Jeopardy
		finalJeopardy = true;
		
		//busy wait until all the contestants are either out or have decided on how much to wager
		while(Contestant.getContestantsReady() + Contestant.getLeftNumber() != 3)
		{
			boolean garbageBusyWaitingVariable = true;
			myThread.yield();
		}
		
		if(Contestant.getContestantsReady() > 0)
		{
			this.msg("Here is the final jeopardy question!");
			finalQuestion = true;
			while(!finalQuestionAnswered)
			{
				boolean garbageBusyWaitingVariable= true;
				myThread.yield();
			}
		}
		
		//print the scores
		this.msg("The scores are in");
		for(int i = 0; i < 3; i++)
		{
			this.msg(contestants[i].getName() + " has a score of " + contestants[i].getScore());
		}
		
		//determine the winner
		int maxScore = contestants[0].getScore();
		int winnerIndex = 0;
		for(int i = 1; i < 3; i++)
		{
			if(contestants[i].getScore() >= maxScore)
			{
				maxScore = contestants[i].getScore();
				winnerIndex = i;
			}
		}
		
		this.msg("And the winner is: " + contestants[winnerIndex].getName());
			
		myThread.yield();

	}
	
	public void msg(String m) {
		System.out.println(myThread.getName()+" ["+(System.currentTimeMillis()-time)+"] "+": "); System.out.println(m);
		}
	
	//checks if it's final Jeopardy
	public static boolean getFinalJeopardy()
	{
		return finalJeopardy;
	}
	
	//checks to see if the final question has been asked
	public static boolean getFinalQuestion()
	{
		return finalQuestion;
	}
	
	//lets the host know that the final jeopardy question has been answered
	public static void setFinalQuestionAnswered(boolean value)
	{
		finalQuestionAnswered = value;
	}
	
	public static Contestant[] getContestants()
	{
		return contestants;
	}
	
	public void interrupt()
	{
		myThread.interrupt();
	}
}
