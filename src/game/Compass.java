package game;

/**
 * Represent the 8 compass directions.
 * <p>
 *          N
 *      NW     NE
 *   W      X      E
 *      SW     SE
 *          S
 * </p>
 *
 * @author Brock Dyer.
 */
public enum Compass {

    /** North */
    N(new int[]{-1, 0}),
    /** South */
    S(new int[]{1, 0}),
    /** East */
    E(new int[]{0, 1}),
    /** West */
    W(new int[]{0, -1}),
    /** Northeast */
    NE(new int[]{-1, 1}),
    /** Northwest */
    NW(new int[]{-1, -1}),
    /** Southeast */
    SE(new int[]{1, 1}),
    /** Southwest */
    SW(new int[]{1, -1});

    /** The coordinates of this direction. <br>
     *  These are used to calculate the position of the next piece in the given direction.
     */
    private int[] coords;

    /**
     * A private constructor to construct the enum values with an int array.
     *
     * @param coords the coordinates of this direction.
     */
    Compass(int[] coords){
        this.coords = coords;
    }

    /**
     * Get the x-coord of this direction.
     *
     * @return the x-coordinate.
     */
    public int getX(){
        return this.coords[0];
    }

    /**
     * Get the y-coord of this direction.
     *
     * @return the y-coordinate.
     */
    public int getY(){
        return this.coords[1];
    }

}
