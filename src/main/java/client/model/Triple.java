package client.model;

/**
 * <P>Triple generic object</P>
 *
 * @author Andrea Cimmino
 * @version 1.0
 */
public class Triple<T,M,V> {

    private T firstElement;
    private M secondElement;
    private V thirdElement;

    /**
     * Contructor of Triple class
     * @param firstElement Any element to be stored as the first
     * @param secondElement Any element to be stored as the second
     * @param thirdElement Any element to be stored as the third
     */
    public Triple(T firstElement, M secondElement, V thirdElement) {
        this.firstElement = firstElement;
        this.secondElement = secondElement;
        this.thirdElement = thirdElement;
    }

    /**
     * This method outputs the first element within this Triple
     * @return The first element of this triple
     */
    public T getFirstElement() {
        return firstElement;
    }

    /**
     * This method sets the first element value
     * @param firstElement Any element to be stored as the first
     */
    public void setFirstElement(T firstElement) {
        this.firstElement = firstElement;
    }

    /**
     * This method outputs the second element within this Triple
     * @return The second element of this triple
     */
    public M getSecondElement() {
        return secondElement;
    }

    /**
     * This method sets the second element value
     * @param secondElement Any element to be stored as the second
     */
    public void setSecondElement(M secondElement) {
        this.secondElement = secondElement;
    }

    /**
     * This method outputs the third element within this Triple
     * @return The third element of this triple
     */
    public V getThirdElement() {
        return thirdElement;
    }

    /**
     * This method sets the third element value
     * @param thirdElement Any element to be stored as the third
     */
    public void setThirdElement(V thirdElement) {
        this.thirdElement = thirdElement;
    }

    /**
     * This method provides a string representation of the current object
     * @return A string representation of this Triple object
     */
    @Override
    public String toString() {
        StringBuilder representation = new StringBuilder();
        representation.append("[").append(firstElement).append(", ");
        representation.append(secondElement).append(", ");
        return representation.toString();
    }
}
