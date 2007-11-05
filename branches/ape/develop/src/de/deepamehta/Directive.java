package de.deepamehta;



public class Directive {



	// **************
	// *** Fields ***
	// **************



	public int type;
	public Object param1, param2, param3, param4, param5;



	// ********************
	// *** Constructors ***
	// ********************



	public Directive(int type) {
		this.type = type;
	}

	public Directive(int type, Object param) {
		this(type);
		this.param1 = param;
	}

	public Directive(int type, Object param1, Object param2) {
		this(type, param1);
		this.param2 = param2;
	}

	public Directive(int type, Object param1, Object param2, Object param3) {
		this(type, param1, param2);
		this.param3 = param3;
	}

	public Directive(int type, Object param1, Object param2, Object param3,
															 Object param4) {
		this(type, param1, param2, param3);
		this.param4 = param4;
	}

	public Directive(int type, Object param1, Object param2, Object param3,
											  Object param4, Object param5) {
		this(type, param1, param2, param3, param4);
		this.param5 = param5;
	}
}

