import java.util.Random;

public class Contestant implements Runnable
{
	Random randomNum1 = new Random(System.currentTimeMillis());
	Random randomNum = new Random(randomNum1.nextInt());
	Thread myThread;
	public static long time = System.currentTimeMillis();
	static long startTime =  System.currentTimeMillis();
	protected int numberGuessed = 0;
	protected boolean selected = false;
	//keeps track of the amount of contestants that have selected a number
	protected static int counter = 0;
	//lets the contestants know if it's time for them to introduce themselves
	protected static boolean introduce = false;
	//keeps track of how many of the contestants have introduced themselves
	protected static int introCounter = 0;
	private boolean isAnswering = false;
	private int score = 0;
	private int wager = 0;
	//keeps track of which number thread this is to wake up after answering the final jeopardy question
	private static int wokeNumber = 0;
	//keeps track of the number of contestants that didn't make it to final jeopardy
	private static int leftNumber = 0;
	//a flag for the first contestant who wakes up from the final jeopardy
	private boolean first = false;
	//a flag for the second contestant who wakes up from the final jeopardy
	private boolean second = false;
	//keeps track of how many contestants are ready for the final jeopardy question
	private static int contestantsReady = 0;
	
	//Constructor
	public  Contestant(String threadName)
	{
		myThread = new Thread(this, threadName);
		myThread.start();
	}
	
	public void run() 
	{
		//picks a random number up to 500 and announces it
		numberGuessed = randomNum.nextInt(501);
		//System.out.println(myThread.getName() + ": picked " + numberGuessed);
		counted();
		//System.out.println(myThread.getName() + " has picked the number " + numberGuessed);
		//busy waits until the announcer selects the winners
		while(!Announcer.getDoneSelecting())
		{
			boolean garbageBusyWaitVariable = true;
			myThread.yield();
		}
		if(selected == true)
		{
			//sleep random time
			try 
			{
				myThread.sleep(randomNum.nextInt(5000));
			} catch (InterruptedException e) {}
			
			//busy wait
			while(introduce == false)
			{
				boolean garbageBusyWaitVariable = true;
				myThread.yield();
			}
			
			//introduce yourself
			myThread.setPriority(10);
			this.msg(": Hi, my name is " + myThread.getId());
			introCounted();
			myThread.setPriority(5);
			myThread.yield();
			
			//regular jeopardy
			while(!Host.getFinalJeopardy())
			{
				//Busy wait until Host is done asking the question
				while(!isAnswering && !Host.getFinalJeopardy())
				{
					boolean garbageBusyWaitVariable = true;
					myThread.yield();
				}
				if(isAnswering)
				{
					this.msg("Here is my answer.");
					isAnswering = false;
				}
			}
			
			//final jeopardy
			if(this.score > 0)
			{
				//increment the amount of ready contestants
				incContestantsReady();
				wager = randomNum.nextInt(score+1);
				while(!Host.getFinalQuestion())
				{
					boolean garbageBusyWaitVariable = true;
					myThread.yield();
				}
				try {
					myThread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				//what the  contestants do upon waking after the final jeopardy question
				Contestant contestants[] = Host.getContestants();
				incWoke(this);
				if(this.first == true)
				{
					for(int i = 0; i < 3; i++)
					{
						//loop through the contestants, if it's not you, and its alive, join with it
						if(contestants[i] != this && contestants[i].getThread().isAlive())
							try {
								contestants[i].getThread().join();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
					}
					Host.setFinalQuestionAnswered(true);
				}
				else if(this.second == true)
				{
					for(int i = 0; i < 3; i++)
					{
						//loop through the contestants, if it's not you, and its alive, join with it
						if(contestants[i] != this && contestants[i].getThread().isAlive() && contestants[i].first == false)
							try {
								contestants[i].getThread().join();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
					}
				}
				
				//increment the score
				if(randomNum.nextInt(10) < 5)
					this.score+=wager;
				else
					this.score-=wager;
				
			}

			this.msg("Good-bye");
			//keeps track of how many contestants did not participate in final jeopardy
			leftNumber++;
			
		}
	}
	
	//keeps track of how many contestants have counted
	public synchronized static void counted()
	{
		counter++;
	}
	
	//keeps track of how many of the contestants have introduced themselves
	public synchronized static void introCounted()
	{
		introCounter++;
	}
	
	//keeps track of which number this contestant is to wake up from the final jeopardy question
	public synchronized static void incWoke(Contestant me)
	{
		if(getWokeNumber() == 0)
		{
			wokeNumber++;
			me.first = true;
		}
		
		else if(getWokeNumber() == 1)
		{
			wokeNumber++;
			me.second = true;
		}
	}
	
	public synchronized static int getWokeNumber()
	{
		return wokeNumber;
	}
	
	public synchronized static void incContestantsReady()
	{
		contestantsReady++;
	}
	
	public synchronized static int getContestantsReady()
	{
		return contestantsReady;
	}
	public static int getCounter()
	{
		return counter;
	}
	
	public synchronized static int getIntroCounter()
	{
		return introCounter;
	}
	
	public int getNumberGuessed()
	{
		return numberGuessed;
	}
	
	public String getName()
	{
		return myThread.getName();
	}
	
	//sets if this contestant was selected
	public void setSelected(boolean value)
	{
		this.selected = value;
	}
	
	public static void setIntroduce(boolean value)
	{
		introduce = value;
	}
	
	//decides if this contestant is answering the question
	public void setIsAnswering(boolean value)
	{
		this.isAnswering = value;
	}
	
	public boolean getIsAnswering()
	{
		return isAnswering;
	}
	
	public void incrementScore(int value)
	{
		score+=value;
	}
	
	public void decrementScore(int value)
	{
		score-=value;
	}
	
	public int getScore()
	{
		return score;
	}
	
	//allow access to the thread
	public Thread getThread()
	{
		return myThread;
	}
	
	//keeps track of how many contestants have left
	public static int getLeftNumber()
	{
		return leftNumber;
	}
	
	public void msg(String m) {
		System.out.println(myThread.getName()+" ["+(System.currentTimeMillis()-time)+"] "+": "); System.out.println(m);
		}
	
	protected  final long age() 
	{
		return System.currentTimeMillis() - startTime; 
	}

}
