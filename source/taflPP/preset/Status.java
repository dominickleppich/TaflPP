package taflPP.preset;

public class Status implements java.io.Serializable {
    public static final int OK       = 0;
    public static final int REDWIN   = 1;
    public static final int BLUEWIN  = 2;
    public static final int DRAW     = 3;
    public static final int ILLEGAL  = 9;
    public static final int ERROR    = 99;

    public Status(int value) { 
        this.value = value; 
    }

    //----------------------------------------------------------------
    public boolean isOK() { 
        return value == OK;
    }

    public boolean isREDWIN() {
        return value == REDWIN;
    }

    public boolean isBLUEWIN() {
        return value == BLUEWIN;
    }

    public boolean isDRAW() {
        return value == DRAW;
    }

    public boolean isILLEGAL() {
        return value == ILLEGAL;
    }

    public boolean isERROR() {
        return value == ERROR;
    }

    //----------------------------------------------------------------
    public int getValue () {
       return value ;
    }

    public void setValue (int value) {
        this.value = value ;
    }

    //----------------------------------------------------------------
    public boolean equals(Status s) {
	return value == s.getValue();
    }

    public String toString() {
	String str = "";

	switch (value) { 
	case OK: 
	    str = "ok";
	    break;
	case REDWIN:
	    str = "red wins";
	    break;
	case BLUEWIN:
	    str = "blue wins";
	    break;
	case DRAW:
	    str = "draw";
	    break;
	case ILLEGAL: 
	    str = "illegal";
	    break;
	case ERROR:
	default:
	    str = "error";
	}

	return str;
    }


    // private -------------------------------------------------------
    private int value = OK;

    // private static ------------------------------------------------
    private static final long serialVersionUID = 1L;
}
