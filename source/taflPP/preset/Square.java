package taflPP.preset;

public interface Square {

    // man + label
    int NONE = 0;

    // man
    int RED  = 1;
    int BLUE = 2;
    int KING = 3;

    // label
    int THRONE = 10;
    int CASTLE = 11;

    int getMan();
    void setMan(int man);

    int getLabel();
    void setLabel(int label);
}
