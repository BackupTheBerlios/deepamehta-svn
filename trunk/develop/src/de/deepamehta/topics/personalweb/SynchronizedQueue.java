// PersonalWeb
// by Jörg Richter
// jri@prz.tu-berlin.de
// 
// 1.0b1	7.10.96		First release



package de.deepamehta.topics.personalweb;

import java.util.Vector;



class SynchronizedQueue extends Vector {

	private PersonalWeb pw;
	private	boolean isBusy;
	private	boolean	eatThemUp;
	private	int	getIndex;

	SynchronizedQueue(PersonalWeb pw, boolean eatThemUp) {
		super(50, 50);
		this.pw = pw;
		this.isBusy = false;
		this.eatThemUp = eatThemUp;
		this.getIndex = 0;
	}
	
	synchronized void put(Object obj) {
		addElement(obj);
		isBusy = true;
		notify();
	}
	
	synchronized Object get() {
		if (queueIsEmpty()) {
			isBusy = false;
			try {
				wait();
			} catch (InterruptedException e) {
				pw.writeLog("*** SynchronizedQueue.get(): " + e);
			}
		}
		if (queueIsEmpty()) {
			pw.writeLog("SynchronizedQueue.get(): queue is empty at notify");
			return null;
		}
		Object obj = elementAt(getIndex);
		if (eatThemUp) {
			removeElementAt(0);
		} else {
			getIndex++;
		}
		return obj;
	}

	synchronized boolean isBusy() {
		return isBusy;
	}

	synchronized boolean queueIsEmpty() {
		return getSize() == 0;
	}
	
	synchronized int getSize() {
		return size() - getIndex;
	}

	synchronized void notifyQueue() {
		notify();
	}
}
