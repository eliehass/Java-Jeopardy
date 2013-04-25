import java.util.Random;

public class Announcer implements Runnable
{
	Random randomNum = new Random(System.currentTimeMillis());
	Thread myThread;
	public static long time = System.currentTimeMillis();
	static long startTime =  System.currentTimeMillis();
	protected static boolean doneSelecting = false;
	private static 	Contestant selectedContestants[] = new Contestant[3];
	
	//Constructor
	public  Announcer(String threadName)
	{
		myThread = new Thread(this, threadName);
		myThread.start();
	}
	
	public void run() 
	{
		Contestant contestantArray[] = new Contestant[7];
		//create 7 contestants
		for(int i = 0; i < 7; i++)
		{
			contestantArray[i] = new Contestant("contestant" + i);
		}
		
		while(doneSelecting == false)
		{
			//when the contestants are done selecting numbers
			if(Contestant.getCounter() == 7)
			{
				int max = 0;
				int index = 0;
				
				//pick the 3 winners
				for(int i = 0; i < 3; i++)
				{
					max = 0;
					for(int j = 0; j < 7; j++)
					{
						//find the max number guessed. makes sure you don't look in a null element of the array
						if(contestantArray[j] != null && contestantArray[j].getNumberGuessed() >= max)
						{
							index = j;
							max = contestantArray[j].getNumberGuessed();
						}
					}
					//put the contestant with the highest number into the selected contestants array
					selectedContestants[i] = contestantArray[index];
					selectedContestants[i].setSelected(true);
					//remove the selected contestant from the contestantArray.
					contestantArray[index] = null;
					this.msg(selectedContestants[i].getName() + " has been selected.");
				}
				for(int i = 0; i < 7; i++)
					if(contestantArray[i] != null)
						this.msg(contestantArray[i].getName() + " has been eliminated.");
				
				doneSelecting = true;
			}
		}
		//start the show
		this.msg("Announcer: Ok contestants, it's time to intoduce yourselves!");
			
		//create the Host
		Host theHost = new Host("theHost");
		Contestant.setIntroduce(true);
			
		while(true)
		{
			if(Contestant.getIntroCounter() == 3)
			{
				this.msg("Announcer: Ok, now it's time to play JEOPARDY!");
				theHost.interrupt();
				break;
			}
			myThread.yield();
		}
	}
	
	public void msg(String m) {
		System.out.println(myThread.getName()+" ["+(System.currentTimeMillis()-time)+"] "+": "); System.out.println(m);
		}
	
	public static Contestant[] getSelectedContestants()
	{
		return selectedContestants;
	}
	
	protected static final long age() 
	{
		return System.currentTimeMillis() - startTime; 
	}
	
	public static boolean getDoneSelecting()
	{
		return doneSelecting;
	}
}
