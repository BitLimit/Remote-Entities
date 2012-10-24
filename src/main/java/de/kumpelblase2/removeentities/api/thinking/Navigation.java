package de.kumpelblase2.removeentities.api.thinking;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Navigation
{
	private List<DesireItem> m_desires;
	private List<DesireItem> m_executingDesires;
	private int m_delay = 0;
	
	public Navigation()
	{
		this.m_desires = new ArrayList<DesireItem>();
		this.m_executingDesires = new ArrayList<DesireItem>();
	}
	
	public void onUpdate()
	{
		List<DesireItem> tempList = new ArrayList<DesireItem>();
		if(++this.m_delay % 3 == 0)
		{
			for(DesireItem item : this.m_desires)
			{
				if(this.m_executingDesires.contains(item))
				{
					if(this.hasHighestPriority(item) && item.getDesire().canContinue())
						continue;
								 
					item.getDesire().stopExecuting();
					this.m_executingDesires.remove(item);
				}
							 
				if(this.hasHighestPriority(item) && item.getDesire().shouldExecute())
				{
					tempList.add(item);
					this.m_executingDesires.add(item);
				}
			}
		}
		else
		{
			Iterator<DesireItem> it = this.m_executingDesires.iterator();
			while(it.hasNext())
			{
				DesireItem item = it.next();
				if(!item.getDesire().canContinue())
				{
					item.getDesire().startExecuting();
					it.remove();
				}
			}
		}
		
		for(DesireItem item : tempList)
		{
			item.getDesire().startExecuting();
		}
		
		Iterator<DesireItem> it = this.m_executingDesires.iterator();
		while(it.hasNext())
		{
			if(!it.next().getDesire().update())
				it.remove();
		}
	}
	 
	public void addDesire(Desire inDesire, int inPriority)
	{
		this.m_desires.add(new DesireItem(inDesire, inPriority));
	}
	 
	public boolean hasHighestPriority(DesireItem inItem)
	{
		for(DesireItem item : this.m_desires)
		{
			if(item.equals(inItem))
				continue;
			
			if(inItem.getPriority() >= item.getPriority())
			{
				if(this.m_executingDesires.contains(item) && !areTasksCompatible(item.getDesire(), inItem.getDesire()))
				{
					return false; 
				}
			}
			else if(this.m_executingDesires.contains(item) && !item.getDesire().isContinous())
			{				 
				return false;
			}
		} 
		return true;
	}
	 
	public static boolean areTasksCompatible(Desire inFirstDesire, Desire inSecondDesire)
	{
		return inFirstDesire.getType() != inSecondDesire.getType();
	}
	
	public List<Desire> getDesires()
	{
		List<Desire> tempDesires = new ArrayList<Desire>();
		for(DesireItem item : this.m_desires)
			tempDesires.add(item.getDesire());
		
		return tempDesires;
	}
	
	public void removeDesireByType(Class<? extends Desire> inType)
	{
		Iterator<DesireItem> it = this.m_desires.iterator();
		while(it.hasNext())
		{
			DesireItem item = it.next();
			if(item.getDesire().getClass().equals(inType))
				it.remove();
		}
		
		it = this.m_executingDesires.iterator();
		while(it.hasNext())
		{
			DesireItem item = it.next();
			if(item.getDesire().getClass().equals(inType))
			{
				item.getDesire().stopExecuting();
				it.remove();
			}
		}
	}
	
	public void clearDesires()
	{
		for(DesireItem item : this.m_executingDesires)
		{
			item.getDesire().stopExecuting();
		}
		this.m_desires.clear();
		this.m_executingDesires.clear();
	}
}
