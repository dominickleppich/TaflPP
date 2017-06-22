package taflPP.preset;

public interface Viewer<T extends Square> {
    boolean isRed();

    int getSize();

    T getSquare(int letter, int number);
    
    Status getStatus();
}
