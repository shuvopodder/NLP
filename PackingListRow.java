/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package irevaluate;

/**
 *
 * @author Shuvo Podder
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Shuvo Podder
 */
public class PackingListRow {
    private final String isbn;
    private final int palletNumber;

    public PackingListRow(String isbn, int palletNumber) {
        this.isbn = isbn;
        this.palletNumber = palletNumber;
    }



    @Override
    public int hashCode() {
        return palletNumber * 31 + ((isbn == null) ? 0 : isbn.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PackingListRow other = (PackingListRow) obj;
        if (isbn == null) {
            if (other.isbn != null)
                return false;
        } else if (!isbn.equals(other.isbn))
            return false;
        if (palletNumber != other.palletNumber)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return isbn+":"+palletNumber;
    }
}
