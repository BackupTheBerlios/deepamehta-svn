package de.deepamehta.service.web;



public class DisplayObject {

	public int mode;
	public Object param1, param2, param3;

	public DisplayObject(int mode) {
		this.mode = mode;
	}

	public DisplayObject(int mode, Object param1) {
		this.mode = mode;
		this.param1 = param1;
	}

	public DisplayObject(int mode, Object param1, Object param2) {
		this.mode = mode;
		this.param1 = param1;
		this.param2 = param2;
	}

	public DisplayObject(int mode, Object param1, Object param2, Object param3) {
		this.mode = mode;
		this.param1 = param1;
		this.param2 = param2;
		this.param3 = param3;
	}
}
