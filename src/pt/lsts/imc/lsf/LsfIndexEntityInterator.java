package pt.lsts.imc.lsf;

import java.io.File;
import java.util.Iterator;

import pt.lsts.imc.IMCMessage;

public class LsfIndexEntityInterator implements Iterator<IMCMessage>, Iterable<IMCMessage>{

	protected String entityName;
    protected LsfIndex index;
    protected int nextIndex;
    
    public LsfIndexEntityInterator(LsfIndex index, String entityName) {
    	this.index = index;
    	this.entityName = entityName;
    	nextIndex = index.getNextMessageOfEntity(entityName, 0);    	
	}
    
	@Override
	public boolean hasNext() {
		return nextIndex != -1;
	}
	
	@Override
	public Iterator<IMCMessage> iterator() {
		return this;
	}
	
	@Override
	public IMCMessage next() {
		if (nextIndex == -1)
			return null;
		IMCMessage cur = index.getMessage(nextIndex); 
		nextIndex = index.getNextMessageOfEntity(entityName, nextIndex); 
		return cur;
	}
	
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	public static void main(String[] args) throws Exception {
		LsfIndex index = new LsfIndex(new File("/home/zp/workspace/logs/121224_multibeam_survey/Data.lsf"));
		for (IMCMessage m : index.iterateEntityMessages("Navigation"))
			System.out.println(m.getAbbrev());
	}
}
