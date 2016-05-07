import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Vuko and NotVuko on 2015-05-26.
 */
public class Bot
{
    protected List<Bot> bots;
    protected Round round;

    protected String preName;
    protected String name;
    protected String why="no idea";
    protected int gold;
    protected int goldInGame;
    protected Card cards[] = new Card[2];

    protected Card winCards[] = new Card[5];
    protected int winValue; //dla latwiejszego sprawdzania kto ma najlepszy uklad //todo: mozna zrefaktoryzowac na endValue czy cos takiego patrz wyzej tez
    protected int secondValue; //pomocnicze dla ustalania kto ma lepszy uklad, zastosowanie ma tylko przy dwoch parach
    protected CardSystem endCardSystem;


    protected int startRoundPosition;
    protected int position;
    protected int StartingValue;
    protected int Value;

    protected String lastAction;
    protected boolean didIMove;


    protected int onePairValue=0;
    protected boolean canBeHigherPair=false;
    protected boolean isPairOnTable=false;

    protected int threeValue=0;
    protected boolean isThreeOnTable=false;

    protected int TwoPairsValueFirst=0;
    protected int TwoPairsValueSecond=0;
    protected boolean isOneOfTwoPairsOnTable=false;
    protected boolean isTwoPairsOnTable=false;

    protected int fourValue=0;
    protected boolean isFourOnTable=false;

    protected boolean chanceForSuit=false;
    protected boolean isSuitOnTable=false;
    protected boolean isFourColorsOnTable=false;

    protected boolean isFullOnTable=false;

    protected boolean isStraightOnTable=false;

    protected boolean isShortStack=false;

    protected boolean isEarlyPosition=false;
    protected boolean isMediumPosition=false;
    protected boolean isLatePosition=false;
    protected boolean isDuel=false;

    protected boolean someoneRaised=false;
    protected int noRaiseCounter=0;
    protected boolean isGoldOverBB=false; //TODO:  PO COTO KOMU VUKO?

    protected boolean isNewPhase=true;
    protected Stage lastStage=Stage.PREFLOP;

    protected int howManyPlayersInGameWhenBotAllIn = -1;

    public Bot() {}

    public Bot(String name, int gold)
    {
        winValue = 0;
        goldInGame = 0;
        this.name = name;
        this.gold = gold;
    }

    public void MakeMove(List<Bot> bots, Round round)
    {
        didIMove = true;
        why ="no idea";
        if(gold == 0)
        {
            Card cards[] = this.cards.clone();
            System.out.print("\t" + cards[0].getName() + " " + cards[0].getSuit() + "  ");
            System.out.println(cards[1].getName() + " " + cards[1].getSuit());
            return;
        }

        if(bots.size() > 1)
            AI(bots, round);

        System.out.println(position + " " + preName + "-> " + startRoundPosition + "\t" + name);

        Card cards[] = this.cards.clone();
        System.out.print("\t" + cards[0].getName() + " " + cards[0].getSuit() + "  ");
        System.out.println(cards[1].getName() + " " + cards[1].getSuit());

        System.out.println("\t" + "Gold: " + gold);
        System.out.println("\t" + "GoldInGame: " +goldInGame);
        System.out.println("\t" + "Hand Value: " +StartingValue);
        System.out.println("\t" + "Why: " +why);

        System.out.println("Last Action: " + lastAction);


        System.out.println("");
    }

    protected void AI(List<Bot> bots, Round round)
    {
    }

    protected void CheckIn()
    {
        int toPay = round.getInValue() - goldInGame;
        if(toPay == 0)
        {
            lastAction = "Check In";
            return;
        }

        if(gold - toPay > 0)
        {
            gold-=toPay;
            goldInGame+=toPay;
            round.setGoldAtTable(round.getGoldAtTable() + toPay);
            lastAction = "Check In";
        }
        else
        {
            int copyGold = gold;
            gold = 0;
            goldInGame+=copyGold;
            round.setGoldAtTable(round.getGoldAtTable() + copyGold);
            lastAction = "All In";
        }
        round.setNumberOfPlayersInGame(round.getNumberOfPlayersInGame() + 1);
    }

    protected void Bet(int betValue)
    {
        int toPay = betValue - goldInGame;

        if(betValue < round.getInValue() || (gold + goldInGame) < round.getInValue())
        {
            CheckIn();
            return;
        }

        //takie tam zabezpieczonko do testow
        if(toPay == 0)
        {
            round.setNumberOfPlayersInGame(round.getNumberOfPlayersInGame() - 1);
            CheckIn();
            return;
        }

        round.setWhoBetLast(position);

        if(gold - toPay > 0)
        {
            gold-=toPay;
            goldInGame+=toPay;
            round.setGoldAtTable(round.getGoldAtTable() + toPay);
            lastAction = "Bet to: " + betValue;
            round.setInValue(betValue);

            round.setNumberOfPlayersInGame(1);
        }
        else
        {
            int copyGold = gold;
            gold = 0;
            goldInGame+=copyGold;
            round.setGoldAtTable(round.getGoldAtTable() + copyGold);
            lastAction = "All In";
            round.setInValue(goldInGame);

            round.setNumberOfPlayersInGame(round.getNumberOfPlayersInGame() + 1);
        }
    }

    protected void Fold()
    {
        if(this.goldInGame == round.getInValue())
        {
            CheckIn();
            return;
        }

        for(Bot bot : bots)
        {
            if(bot.position > this.position)
            {
                bot.setPosition(bot.getPosition() - 1);
                //bot.setPreName(bot.getPosition() + "->");
            }
        }

        position = startRoundPosition;
        round.getFoldBots().add(this);
        lastAction = "Fold";

        bots.remove(this);
    }

    protected void calculateStartingValue()
    {
        StartingValue = cards[0].getWorth() + cards[1].getWorth();
        if(cards[0].getValue() == cards[1].getValue() ) StartingValue+=10;
        if(cards[0].getSuit() == cards[1].getSuit() ) StartingValue+=4;
        if(Math.abs(cards[0].getValue() - cards[1].getValue()) == 1) StartingValue+=3;
        if(Math.abs(cards[0].getValue() - cards[1].getValue()) == 2) StartingValue+=2;
        if(Math.abs(cards[0].getValue() - cards[1].getValue()) == 3) StartingValue+=1;
    }
    protected void checkFlags()
    {
        if(gold< 12* round.getBb()) isShortStack=true;

        //Check Position.
        if(round.botsInitialSize == 6)
        {
            if (startRoundPosition == 0 || startRoundPosition == 1) isEarlyPosition = true;
            else if (startRoundPosition == 2 || startRoundPosition == 3) isMediumPosition = true;
            else if (startRoundPosition == 4 || startRoundPosition == 5) isLatePosition = true;
        }
        else if(round.botsInitialSize == 5)
        {
            if (startRoundPosition == 0 || startRoundPosition == 1) isEarlyPosition = true;
            else if (startRoundPosition == 2) isMediumPosition = true;
            else if (startRoundPosition == 3 || startRoundPosition == 4) isLatePosition = true;
        }
        else if(round.botsInitialSize == 4)
        {
            if (startRoundPosition == 0) isEarlyPosition = true;
            else if (startRoundPosition == 1 || startRoundPosition == 2) isMediumPosition = true;
            else if (startRoundPosition == 3) isLatePosition = true;
        }
        else if(round.botsInitialSize == 3)
        {
            if (startRoundPosition == 0) isEarlyPosition = true;
            else if (startRoundPosition == 1) isMediumPosition = true;
            else if (startRoundPosition == 2) isLatePosition = true;
        }
        else if(round.botsInitialSize == 2)
        {
            isDuel=true;
        }

        for(Bot bot : bots)
        {
            CharSequence cs1="Bet to";
            CharSequence cs2="All in";
            if(bot.getName().equals(name))continue; //nie ma sensu sprawdzac czy samemu sie raise'owalo :D
            if (bot.lastAction!=null) if (bot.lastAction.contains(cs1) || bot.lastAction.contains(cs2)) someoneRaised = true;
            if (someoneRaised)  break;
        }

        if(!someoneRaised) noRaiseCounter++;

    }
    protected void clearFlagsAndValues()
    {
        onePairValue=0;
        canBeHigherPair=false;
        isPairOnTable=false;

        threeValue=0;
        isThreeOnTable=false;

        TwoPairsValueFirst=0;
        TwoPairsValueSecond=0;
        isOneOfTwoPairsOnTable=false;
        isTwoPairsOnTable=false;

        fourValue=0;
        isFourOnTable=false;

        chanceForSuit=false;
        isSuitOnTable=false;
        isFourColorsOnTable=false;

        isFullOnTable=false;

        isStraightOnTable=false;

        isShortStack=false;

        isEarlyPosition=false;
        isMediumPosition=false;
        isLatePosition=false;
        isDuel=false;

        someoneRaised=false;
    }

    protected boolean hasOnePair( )
    {
        boolean result = false;
        boolean found = false;
        List<Card> allCards = new LinkedList<Card>();


        allCards.add(cards[0]);
        allCards.add(cards[1]);
        int length = round.getTableCards().length;
        for (int i=0; i<length; i++)
        {
            allCards.add(round.getTableCards()[i]);
        }

        //Ustalamy ile kart analizujemy
        int size=0;
        if (round.getStage()==Stage.FLOP) size=5;
        else if (round.getStage()==Stage.TURN) size=6;
        else if (round.getStage()==Stage.RIVER) size=7;

        for(int i=0; i<size; i++)
        {

            for(int j=0; j<size; j++)
            {
                if(i == j) continue;
                if(allCards.get(i).getValue() == allCards.get(j).getValue())
                {

                    if (found)
                    {
                        if(allCards.get(i).getValue()>onePairValue)
                        {
                            onePairValue=allCards.get(i).getValue();
                        }
                    }
                    else
                    {
                        found=true;
                        result=true;
                        onePairValue=allCards.get(i).getValue();
                    }
                }
            }

        }
        //Sprawdzamy czy posiadana para to najwyzsza mozliwa para ze stolu.
        for(int i=0; i<size-2; i++)
        {
            if (round.getTableCards()[i].getValue() > onePairValue) canBeHigherPair=true;
        }

        //Sprawdzamy czy para nie jest ze stolu, czyli czy nie jest dostepna dla kazdego.
        if(result && onePairValue!=cards[0].getValue() && onePairValue!=cards[1].getValue())
        {
            isPairOnTable=true;
        }
        return result;
    }

    protected boolean hasThree( )
    {
        boolean result = false;
        boolean found = false;
        List<Card> allCards = new LinkedList<Card>();


        allCards.add(cards[0]);
        allCards.add(cards[1]);
        int length = round.getTableCards().length;
        for (int i = 0; i < length; i++)
        {
            allCards.add(round.getTableCards()[i]);
        }

        //Ustalamy ile kart analizujemy
        int size = 0;
        if (round.getStage() == Stage.FLOP) size = 5;
        else if (round.getStage() == Stage.TURN) size = 6;
        else if (round.getStage() == Stage.RIVER) size = 7;

        if (size<5) return false;

        for (int i = 0; i < size; i++)
        {

            for (int j = 0; j < size; j++)
            {
                if(i == j) continue;
                for (int k = 0; k < size; k++)
                {
                    if (i == k || j == k) continue;
                    if (allCards.get(i).getValue() == allCards.get(j).getValue() && allCards.get(i).getValue() ==
                            allCards.get(k).getValue() )
                    {
                        /*System.out.println("Znalazlem dla i="+i+" "+allCards.get(i).getValue()+" j="+j+" "+allCards
                                .get(j).getValue()+" k="+k+" "+allCards.get(k).getValue());*/

                        if (found)
                        {
                            if (allCards.get(i).getValue() > threeValue)
                            {
                                threeValue = allCards.get(i).getValue();
                            }
                        } else
                        {
                            found = true;
                            result = true;
                            threeValue = allCards.get(i).getValue();
                        }
                    }
                }
            }

        }
        if(result && threeValue!=cards[0].getValue() && threeValue!=cards[1].getValue()) isThreeOnTable=true;
        return result;
    }
    protected boolean hasHighestOnHand()
    {

        boolean result = true;
        int high=
                0;
        if(cards[0].getValue()>cards[1].getValue()) high=cards[0].getValue();
        else high = cards[1].getValue();


        //Ustalamy ile kart analizujemy
        int size = 0;
        if (round.getStage() == Stage.FLOP) size = 5;
        else if (round.getStage() == Stage.TURN) size = 6;
        else if (round.getStage() == Stage.RIVER) size = 7;

        if (size<5) return false;


        for(int i=0; i<size-2; i++)
        {
            if (round.getTableCards()[i].getValue() >= high) result=false;
        }
        return result;
    }
    protected boolean hasTwoPairs()
    {
        boolean result = false;
        boolean found = false;
        List<Card> allCards = new LinkedList<Card>();


        allCards.add(cards[0]);
        allCards.add(cards[1]);
        int length = round.getTableCards().length;
        for (int i=0; i<length; i++)
        {
            allCards.add(round.getTableCards()[i]);
        }

        //Ustalamy ile kart analizujemy
        int size=0;
        if (round.getStage()==Stage.FLOP) size=5;
        else if (round.getStage()==Stage.TURN) size=6;
        else if (round.getStage()==Stage.RIVER) size=7;

        for(int i=0; i<size; i++)
        {

            for(int j=0; j<size; j++)
            {
                if(i == j) continue;
                if(allCards.get(i).getValue() == allCards.get(j).getValue())
                {

                    if (found)
                    {
                        if(allCards.get(i).getValue()!=TwoPairsValueFirst)
                        {
                            TwoPairsValueSecond=allCards.get(i).getValue();
                            result=true;
                        }
                    }
                    else
                    {
                        found=true;
                        TwoPairsValueFirst=allCards.get(i).getValue();
                    }
                }
            }

        }



        //Sprawdzamy czy para nie jest ze stolu, czyli czy nie jest dostepna dla kazdego.
        if(result && TwoPairsValueFirst!=cards[0].getValue() && TwoPairsValueFirst!=cards[1].getValue())
        {
            isOneOfTwoPairsOnTable=true;
        }
        if(isOneOfTwoPairsOnTable)
            if( TwoPairsValueSecond!=cards[0].getValue() && TwoPairsValueSecond!=cards[1].getValue())
            {
                isTwoPairsOnTable=true;
            }
        return result;
    }

    protected boolean hasFull( )
    {
        boolean result = false;
        boolean found = false;
        List<Card> allCards = new LinkedList<Card>();


        allCards.add(cards[0]);
        allCards.add(cards[1]);
        int length = round.getTableCards().length;
        for (int i = 0; i < length; i++)
        {
            allCards.add(round.getTableCards()[i]);
        }

        //Ustalamy ile kart analizujemy
        int size = 0;
        if (round.getStage() == Stage.FLOP) size = 5;
        else if (round.getStage() == Stage.TURN) size = 6;
        else if (round.getStage() == Stage.RIVER) size = 7;

        if (size<5) return false;

        if(hasOnePair())
        {
            for (int i = 0; i < size; i++)
            {

                for (int j = 0; j < size; j++)
                {
                    if (i == j) continue;
                    for (int k = 0; k < size; k++)
                    {
                        if (i == k || j == k) continue;
                        if (allCards.get(i).getValue() == allCards.get(j).getValue() && allCards.get(i).getValue() ==
                                allCards.get(k).getValue() && allCards.get(i).getValue()!=onePairValue)
                        {
                        /*System.out.println("Znalazlem dla i="+i+" "+allCards.get(i).getValue()+" j="+j+" "+allCards
                                .get(j).getValue()+" k="+k+" "+allCards.get(k).getValue());*/

                            if (found)
                            {
                                if (allCards.get(i).getValue() > threeValue)
                                {
                                    threeValue = allCards.get(i).getValue();
                                }
                            } else
                            {
                                found = true;
                                result = true;
                                threeValue = allCards.get(i).getValue();
                            }
                        }
                    }
                }

            }
        }
        if(result && isPairOnTable && threeValue!=cards[0].getValue() && threeValue!=cards[1].getValue())
            isFullOnTable=true;
        return result;
    }

    protected boolean hasFour( )
    {
        boolean result = false;
        boolean found = false;
        List<Card> allCards = new LinkedList<Card>();


        allCards.add(cards[0]);
        allCards.add(cards[1]);
        int length = round.getTableCards().length;
        for (int i = 0; i < length; i++)
        {
            allCards.add(round.getTableCards()[i]);
        }

        //Ustalamy ile kart analizujemy
        int size = 0;
        if (round.getStage() == Stage.FLOP) size = 5;
        else if (round.getStage() == Stage.TURN) size = 6;
        else if (round.getStage() == Stage.RIVER) size = 7;

        if (size<5) return false;

        for (int i = 0; i < size; i++)
        {

            for (int j = 0; j < size; j++)
            {
                if(i == j) continue;
                for (int k = 0; k < size; k++)
                {
                    if (i == k || j == k) continue;
                    for (int l=0; l < size; l++)
                    {
                        if (i == l || j == l || k == l) continue;
                        if (allCards.get(i).getValue() == allCards.get(j).getValue() && allCards.get(i).getValue() ==
                                allCards.get(k).getValue() && allCards.get(i).getValue() == allCards.get(l)
                                .getValue() )
                        {
                            if (found)
                            {
                                if (allCards.get(i).getValue() > threeValue)
                                {
                                    fourValue = allCards.get(i).getValue();
                                }
                            } else
                            {
                                found = true;
                                result = true;
                                fourValue = allCards.get(i).getValue();
                            }
                        }
                    }
                }
            }

        }
        if(result && fourValue!=cards[0].getValue() && fourValue!=cards[1].getValue()) isFourOnTable=true;
        return result;
    }

    protected boolean hasSuit( )
    {
        boolean result = false;
        int club=0,diamond=0,heart=0,spade=0;
        Suit suitColor=null;
        List<Card> allCards = new LinkedList<Card>();


        allCards.add(cards[0]);
        allCards.add(cards[1]);
        int length = round.getTableCards().length;
        for (int i=0; i<length; i++)
        {
            allCards.add(round.getTableCards()[i]);
        }

        //Ustalamy ile kart analizujemy
        int size=0;
        if (round.getStage()==Stage.FLOP) size=5;
        else if (round.getStage()==Stage.TURN) size=6;
        else if (round.getStage()==Stage.RIVER) size=7;


        for(int i=0; i<size; i++)
        {
            if(allCards.get(i).getSuit()==Suit.CLUB) club++;
            else if (allCards.get(i).getSuit()==Suit.DIAMOND) diamond++;
            else if (allCards.get(i).getSuit()==Suit.HEART) heart++;
            else if (allCards.get(i).getSuit()==Suit.SPADE) spade++;
        }

        if(club>=5){result=true; suitColor=Suit.CLUB;}
        else if(diamond>=5) {result=true; suitColor=Suit.DIAMOND;}
        else if(heart>=5) {result=true; suitColor=Suit.HEART;}
        else if(spade>=5) {result=true; suitColor=Suit.SPADE;}


        else if(club==4){chanceForSuit=true; suitColor=Suit.CLUB;}
        else if(diamond==4) {chanceForSuit=true; suitColor=Suit.DIAMOND;}
        else if(heart==4) {chanceForSuit=true; suitColor=Suit.HEART;}
        else if(spade==4) {chanceForSuit=true; suitColor=Suit.SPADE;}


        if(result && suitColor!=cards[0].getSuit() && suitColor!=cards[1].getSuit())
        {
            isSuitOnTable=true;
        }
        else if(result && (suitColor!=cards[0].getSuit() || suitColor!=cards[1].getSuit()))
        {
            isFourColorsOnTable=true;
        }

        if(chanceForSuit && suitColor!=cards[0].getSuit() && suitColor!=cards[1].getSuit())
        {
            isFourColorsOnTable=true;
        }




        return result;
    }

    protected boolean hasStraight(List < List <Card> > allCards)
    {
        for(List <Card> list : allCards)
        {
            int count = 1;
            int v = list.get(0).getValue();
            for (int i = 1; i < 5; i++)
            {
                if(list.get(i).getValue() + i == v)
                    count++;
                else
                    break;

                if(count == 5)
                {
                    if (!list.contains(cards[0]) && !list.contains(cards[1]) ) isStraightOnTable=true;
                    return true;
                }
            }
        }
        return false;
    }

    protected List <List <Card> > CardsCombos()
    {
        List<Card> allCards = new LinkedList<Card>();
        List<Card> fiveCards = new LinkedList<Card>();
        List <List <Card> > resultList = new LinkedList<List<Card>>();

        //Ustalamy ile kart analizujemy
        int size=0;
        if (round.getStage()==Stage.FLOP) size=5;
        else if (round.getStage()==Stage.TURN) size=6;
        else if (round.getStage()==Stage.RIVER) size=7;

        //w jedna liste wszystkie mozliwe karty
        allCards.add(cards[0]);
        allCards.add(cards[1]);
        for (int i=0; i<size-2; i++)
        {
            allCards.add(round.getTableCards()[i]);
        }

        //tworzymy kombinacje 5 deckow z mozliwych kart
        if (size==5)
        {
            Collections.sort(allCards, new Comparator<Card>() {
                @Override
                public int compare(Card c1, Card c2) {
                    return c2.getValue() - c1.getValue();
                }
            });
            resultList.add(allCards);
        }
        else if(size==6)
        {
            for (int i = 0; i < 6; i++)
            {
                List<Card> xcards = new LinkedList<Card>();
                for (int j = 0; j < 6; j++)
                {
                    if (i == j) continue;
                    xcards.add(allCards.get(j));
                }

                Collections.sort(xcards, new Comparator<Card>()
                {
                    @Override
                    public int compare(Card c1, Card c2)
                    {
                        return c2.getValue() - c1.getValue();
                    }
                });
                resultList.add(xcards);
            }

        }
        else if(size==7)
        {
            for (int i = 0; i < 7; i++)
            {
                for (int j = 0; j < 7; j++)
                {
                    if (i == j) continue;

                    List<Card> xcards = new LinkedList<Card>();

                    for (int k = 0; k < 7; k++)
                    {
                        if (k == i || k == j) continue;

                        xcards.add(allCards.get(k));
                    }

                    Collections.sort(xcards, new Comparator<Card>()
                    {
                        @Override
                        public int compare(Card c1, Card c2)
                        {
                            return c2.getValue() - c1.getValue();
                        }
                    });
                    resultList.add(xcards);
                }
            }
        }
        return resultList;
    }



    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getGold()
    {
        return gold;
    }

    public void setGold(int gold)
    {
        this.gold = gold;
    }

    public Card[] getCards()
    {
        return cards;
    }

    public void setCards(Card[] cards)
    {
        this.cards = cards;
    }

    public int getPosition()
    {
        return position;
    }

    public void setPosition(int position)
    {
        this.position = position;
    }

    public int getStartingValue()
    {
        return StartingValue;
    }

    public void setStartingValue(int startingValue)
    {
        StartingValue = startingValue;
    }

    public int getValue()
    {
        return Value;
    }

    public void setValue(int value)
    {
        Value = value;
    }

    public int getGoldInGame()
    {
        return goldInGame;
    }

    public void setGoldInGame(int goldInGame)
    {
        this.goldInGame = goldInGame;
    }

    public String getLastAction()
    {
        return lastAction;
    }

    public void setLastAction(String lastAction)
    {
        this.lastAction = lastAction;
    }

    public String getPreName()
    {
        return preName;
    }

    public void setPreName(String preName)
    {
        this.preName = preName;
    }

    public CardSystem getEndCardSystem()
    {
        return endCardSystem;
    }

    public void setEndCardSystem(CardSystem endCardSystem)
    {
        this.endCardSystem = endCardSystem;
    }

    public int getStartRoundPosition()
    {
        return startRoundPosition;
    }

    public void setStartRoundPosition(int startRoundPosition)
    {
        this.startRoundPosition = startRoundPosition;
    }

    public int getHowManyPlayersInGameWhenBotAllIn()
    {
        return howManyPlayersInGameWhenBotAllIn;
    }

    public void setHowManyPlayersInGameWhenBotAllIn(int howManyPlayersInGameWhenBotAllIn)
    {
        this.howManyPlayersInGameWhenBotAllIn = howManyPlayersInGameWhenBotAllIn;
    }

    public int getWinValue()
    {
        return winValue;
    }

    public void setWinValue(int winValue)
    {
        this.winValue = winValue;
    }
}
