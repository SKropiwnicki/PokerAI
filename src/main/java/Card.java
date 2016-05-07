/**
 * Created by Vuko on 2015-05-26.
 */
public class Card
{
    private String name;
    private int value;
    private Suit suit;
    private int worth;

    public Card(String name, int value, Suit suit, int worth)
    {
        this.name = name;
        this.value = value;
        this.suit = suit;
        this.worth = worth;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getValue()
    {
        return value;
    }

    public void setValue(int value)
    {
        this.value = value;
    }

    public Suit getSuit()
    {
        return suit;
    }

    public void setSuit(Suit suit)
    {
        this.suit = suit;
    }

    public int getWorth()
    {
        return worth;
    }

    public void setWorth(int worth)
    {
        this.worth = worth;
    }
}
