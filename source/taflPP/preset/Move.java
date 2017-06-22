package taflPP.preset;

public class Move implements java.io.Serializable {
    public Move(Position start, Position end) {
	this.start = start;
	this.end = end;
    }

    public Move(Move mov) {
	start = new Position(mov.start);
	end = new Position(mov.end);
    }

    //----------------------------------------------------------------
    public Position getStart() {
	return start;
    }

    public Position getEnd() {
	return end;
    }

    //----------------------------------------------------------------
    public boolean equals(Move mov) {
	return start.equals(mov.getStart()) && end.equals(mov.getEnd());
    }
    
    public String toString() {
	return start + "->" + end;
    }

    // private -------------------------------------------------------
    private Position start;
    private Position end;

    // private static ------------------------------------------------
    private static final long serialVersionUID = 1L;
    
}
