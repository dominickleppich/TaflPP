package taflPP.preset;

public class Position implements java.io.Serializable {
    public Position(int letter, int number) {
	this.letter = letter;
	this.number = number;
    }

    public Position(Position pos) {
	letter = pos.getLetter();
	number = pos.getNumber();
    }

    //----------------------------------------------------------------
    public int getLetter() {
	return letter;
    }

    public int getNumber() {
	return number;
    }

    public String getAlphabet() {
	return alphabet;
    }

    //----------------------------------------------------------------
    public boolean equals(Position pos) {
	return (letter == pos.getLetter()) && (number == pos.getNumber());
    }

    public String toString() {
	String s = "";
	int l = letter;

	do {
	    s = alphabet.charAt(l%26) + s;
	    l = (l - l%26)/26;
	} while(l > 0);

	return s + (number+1);
    }

    // private -------------------------------------------------------
    private int letter;
    private int number;

    // private static ------------------------------------------------
    private static final long serialVersionUID = 1L;
    private static final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    
}
