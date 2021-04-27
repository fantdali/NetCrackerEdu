package com.netcracker.edu.problem4;

/**
 * Class stores id, description, color and size of shirt.
 * This properties can be set to object by {@link #setShirt(String shirt)},
 * where String shirt contains all that properties, separated by commas(,)
 */
public class Shirt {
    private String id;
    private String description;
    private String color;
    private String size;

    /**
     * Creates Shirt and set fields from String shirt.
     * @param shirt
     */
    public Shirt(String shirt) {
        setShirt(shirt);
    }

    /**
     * Sets class fields from properties in String shirt.
     * @param shirt
     */
    public void setShirt(String shirt) {
        String[] properties = shirt.split(",", 4);
        id = properties[0].trim();
        description = properties[1].trim();
        color = properties[2].trim();
        size = properties[3].trim();
    }

    /**
     * Represents Shirt object as String.
     * @return this representation.
     */
    @Override
    public String toString() {
        return "Shirt{" + '\n' +
                "\tid= '" + id + '\'' + '\n' +
                "\tdescription= '" + description + '\'' + '\n' +
                "\tcolor= '" + color + '\'' + '\n' +
                "\tsize= '" + size + '\'' + '\n' +
                '}';
    }

    public static void main(String[] args) {
        String[] shirts = new String[11];
        shirts[0] = "S001,Black Polo Shirt,Black,XL";
        shirts[1] = "S002,Black Polo Shirt,Black,L";
        shirts[2] = "S003,Blue Polo Shirt,Blue,XL";
        shirts[3] = "S004,Blue Polo Shirt,Blue,M";
        shirts[4] = "S005,Tan Polo Shirt,Tan,XL";
        shirts[5] = "S006,Black T-Shirt,Black,XL";
        shirts[6] = "S007,White T-Shirt,White,XL";
        shirts[7] = "S008,White T-Shirt,White,L";
        shirts[8] = "S009,Green T-Shirt,Green,S";
        shirts[9] = "S010,Orange T-Shirt,Orange,S";
        shirts[10] = "S011,Maroon Polo Shirt,Maroon,S";

        Shirt[] shirtsObj = new Shirt[11];
        for (int i = 0; i < shirtsObj.length; ++i) {
            shirtsObj[i] = new Shirt(shirts[i]);
            System.out.println(shirtsObj[i].toString() + "\n");
        }
    }
}
