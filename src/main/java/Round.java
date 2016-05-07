import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.util.*;

/**
 * Created by Vuko on 2015-05-26.
 */
public class Round
{
    private Game game;
    private Bot winner;

    private List<Bot> bots = new LinkedList<Bot>();
    private static List<Bot> foldBots = new LinkedList<Bot>();
    private boolean isVukomad=true; //OP

    private int numberOfPlayersInGame;
    private int numberOfBets;
    private int goldAtTable;
    private int inValue;
    private int sb;
    private int bb;
    private Stage stage;
    public int botsInitialSize;

    private int whoBetLast;

    private static int roundNumber = 0;

    private List<Card> deck = new LinkedList<Card>();

    private Card[] tableCards = new Card[5];

    private Bot WinCheck(List<Bot> bots)
    {
        System.out.println("Rozpoczynam sprawdzanie, liczba botow: " + bots.size());

        if(bots.size() == 1)
        {
            System.out.println(bots.get(0).getName() + " WON " + goldAtTable);

            winner = bots.get(0);
            bots.get(0).setGold(winner.getGold() + goldAtTable);

            return winner;
        }

        else
        {
            List <Card> allCards = new LinkedList<Card>();

            List < List <Card> > allCardsSets = new LinkedList<List<Card>>();

            for(Bot bot : bots)
            {
                allCards.clear();
                allCardsSets.clear();

                for(int i = 0 ; i < 5; i++)
                    allCards.add(tableCards[i]);
                allCards.add(bot.getCards()[0]);
                allCards.add(bot.getCards()[1]);

                Collections.sort(allCards, new Comparator<Card>()
                {
                    @Override
                    public int compare(Card c1, Card c2)
                    {
                        return c2.getValue() - c1.getValue();
                    }
                });

                //gdyby HighCard
                for(int i = 0 ; i < 5; i++)
                    bot.winCards[i] = allCards.get(i);
                bot.setWinValue(allCards.get(0).getValue());

                for(int i = 0; i < 7; i++)
                {
                    for(int j = 0; j < 7; j++)
                    {
                        if(i == j) continue;

                        List <Card> xcards = new LinkedList<Card>();

                        for(int k = 0; k < 7; k++)
                        {
                            if(k == i || k == j) continue;

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
                        allCardsSets.add(xcards);
                    }
                }

                /*for(int i = 0; i < 7; i++)
                        System.out.print(allCards.get(i).getName() + " " + allCards.get(i).getSuit() + " ");
                System.out.println();*/

                if(hasBotOnePair(bot, allCardsSets))
                {
                    System.out.print(bot.getName() + " One Pair: ");
                    for(int i = 0; i < 5; i++)
                    {
                        System.out.print(bot.winCards[i].getName() + " " + bot.winCards[i].getSuit() + " ");
                    }
                    System.out.println();
                }

                if(hasBotTwoPairs(bot, allCardsSets))
                {
                    System.out.print(bot.getName() + " Two Pairs: ");
                    for(int i = 0; i < 5; i++)
                    {
                        System.out.print(bot.winCards[i].getName() + " " + bot.winCards[i].getSuit() + " ");
                    }
                    System.out.println();
                }

                if(hasBotThreeOfAKind(bot, allCardsSets))
                {
                    System.out.print(bot.getName() + " Three of a kind: ");
                    for(int i = 0; i < 5; i++)
                    {
                        System.out.print(bot.winCards[i].getName() + " " + bot.winCards[i].getSuit() + " ");
                    }
                    System.out.println();
                }

                if(hasBotStraight(bot, allCardsSets))
                {
                    System.out.print(bot.getName() + " Straight: ");
                    for(int i = 0; i < 5; i++)
                    {
                        System.out.print(bot.winCards[i].getName() + " " + bot.winCards[i].getSuit() + " ");
                    }
                    System.out.println();
                }

                if(hasBotFlush(bot, allCardsSets))
                {
                    System.out.print(bot.getName() + " Flush: ");
                    for(int i = 0; i < 5; i++)
                    {
                        System.out.print(bot.winCards[i].getName() + " " + bot.winCards[i].getSuit() + " ");
                    }
                    System.out.println();
                }

                if(hasBotFullHouse(bot, allCardsSets))
                {
                    System.out.print(bot.getName() + " Full house: ");
                    for(int i = 0; i < 5; i++)
                    {
                        System.out.print(bot.winCards[i].getName() + " " + bot.winCards[i].getSuit() + " ");
                    }
                    System.out.println();
                }

                if(hasBotFourOfAKind(bot, allCardsSets))
                {
                    System.out.print(bot.getName() + " Four of a kind: ");
                    for(int i = 0; i < 5; i++)
                    {
                        System.out.print(bot.winCards[i].getName() + " " + bot.winCards[i].getSuit() + " ");
                    }
                    System.out.println();
                }

                if(hasBotStraightFlush(bot, allCardsSets))
                {
                    System.out.print(bot.getName() + " Straight flush: ");
                    for(int i = 0; i < 5; i++)
                    {
                        System.out.print(bot.winCards[i].getName() + " " + bot.winCards[i].getSuit() + " ");
                    }
                    System.out.println();
                }
            }
        }

        Collections.sort(bots, new Comparator<Bot>()
        {
            @Override
            public int compare(Bot b1, Bot b2)
            {
                return b2.winValue - b1.winValue;
            }
        });

        for(Bot bot : bots)
        {
            System.out.println(bot.getName() + " val : " + bot.winValue);
        }

        int reward = goldAtTable;

        int i = 0;
        while(reward > 0)
        {
            Collections.sort(bots, new Comparator<Bot>()
            {
                @Override
                public int compare(Bot b1, Bot b2)
                {
                    return b2.winValue - b1.winValue;
                }
            });

            Bot bot = bots.get(i);

            if(i < bots.size() - 1)
            {
                if(bot.winValue > bots.get(i + 1).winValue)
                {
                    if(bot.getHowManyPlayersInGameWhenBotAllIn() < 0)
                    {
                        bot.setGold(bot.getGold() + reward);
                        System.out.println(bot.getName() + " won " + reward);
                        reward = 0;
                        break;
                    }
                    else
                    {
                        int x = bot.getGold() + bot.getHowManyPlayersInGameWhenBotAllIn();
                        bot.setGold(x);
                        System.out.println(bot.getName() + " won " + reward);
                        reward-=x;
                    }
                }
                else
                {
                    if(bots.get(i).endCardSystem == CardSystem.FULL_HOUSE)
                    {
                        if(bot.secondValue > bots.get(i + 1).secondValue)
                        {
                            bots.get(i + 1).setWinValue(bots.get(i + 1).getWinValue() - 1);
                        }
                        else if (bot.secondValue < bots.get(i + 1).secondValue)
                        {
                            bots.get(i).setWinValue(bots.get(i).getWinValue() - 1);
                        }
                    }

                    else if(bots.get(i).endCardSystem == CardSystem.TWO_PAIR)
                    {
                        if(bot.secondValue > bots.get(i + 1).secondValue)
                        {
                            bots.get(i + 1).setWinValue(bots.get(i + 1).getWinValue() - 1);
                        }
                        else if (bot.secondValue < bots.get(i + 1).secondValue)
                        {
                            bots.get(i).setWinValue(bots.get(i).getWinValue() - 1);
                        }
                        else
                        {
                            if(bot.winCards[4].getValue() > bots.get(i + 1).winCards[4].getValue())
                            {
                                bots.get(i + 1).setWinValue(bots.get(i + 1).getWinValue() - 1);
                            }
                            else if(bot.winCards[4].getValue() < bots.get(i + 1).winCards[4].getValue())
                            {
                                bots.get(i).setWinValue(bots.get(i).getWinValue() - 1);
                            }
                        }
                    }
                    else
                    {
                        for (int j = 0; j < 5; j++)
                        {
                            if (bots.get(i).winCards[j].getValue() > bots.get(i + 1).winCards[j].getValue())
                            {
                                bots.get(i + 1).setWinValue(bots.get(i + 1).getWinValue() - 1);
                            }
                            else if (bots.get(i).winCards[j].getValue() < bots.get(i + 1).winCards[j].getValue())
                            {
                                bots.get(i).setWinValue(bots.get(i).getWinValue() - 1);
                            }
                        }
                    }

                    if(bot.winValue == bots.get(i + 1).winValue)
                    {
                        //tak, wiem...

                        bots.get(i).setGold(bots.get(i).getGold() + (reward / 2));
                        bots.get(i + 1).setGold(bots.get(i + 1).getGold() + (reward / 2));
                        System.out.println(bot.getName() + " AND " + bots.get(i + 1).getName() + " won " + reward);
                        reward = 0;
                        break;
                    }
                    else
                        i--;
                }
            }
            i++;
        }

        //zabezpieczonko luzne xd
        if(reward > 0)
        {
            bots.get(0).setGold(bots.get(0).getGold() + reward);
            System.out.println("AAAAAALAAAAAARM " + reward);
            reward = 0;
        }

        return winner;
    }

    private boolean hasBotOnePair(Bot bot, List < List <Card> > allCards)
    {
        boolean result = false;
        boolean is = false;

        for(List <Card> list : allCards)
        {
            is = false;
            for(int i = 0; i < 5; i++)
            {
                for (int j = 0; j < 5; j++)
                {
                    if(i == j) continue;

                    if(list.get(i).getValue() == list.get(j).getValue())
                    {
                        if(is)
                        {
                            for(int k = 0; k < 5; k++)
                            {
                                if(list.get(k).getValue() == bot.winCards[k].getValue())
                                    continue;

                                if(list.get(0).getValue() > bot.winCards[0].getValue())
                                {
                                    for(int d = 0; d < 5; d++)
                                    {
                                        bot.winCards[d] = list.get(d);
                                    }
                                    bot.winValue = 100 + list.get(i).getValue();
                                    bot.setEndCardSystem(CardSystem.ONE_PAIR);
                                    result = true;
                                    break;
                                }
                            }
                        }
                        else
                        {
                            is = true;

                            for(int d = 0; d < 5; d++)
                            {
                                bot.winCards[d] = list.get(d);
                            }
                            bot.winValue = 100 + list.get(i).getValue();
                            bot.setEndCardSystem(CardSystem.ONE_PAIR);
                            result = true;
                        }
                    }
                }
            }
        }
        return result;
    }


    private boolean hasBotTwoPairs(Bot bot, List < List <Card> > allCards)
    {
        boolean result = false;
        boolean is = false;
        List <Integer> ints = new LinkedList<Integer>();

        for(List <Card> list : allCards)
        {
            int countPairs = 0;
            ints.clear();

            for(int i = 0; i < 5; i++)
            {
                for (int j = 0; j < 5; j++)
                {
                    is = false;
                    if (i == j) continue;
                    for(Integer o : ints)
                        if(i == o || j == o) is = true;
                    if(is) continue;

                    if (list.get(i).getValue() == list.get(j).getValue())
                    {
                        if(countPairs == 0)
                        {
                            bot.secondValue = list.get(i).getValue();
                            ints.add(i);
                            ints.add(j);
                            countPairs++;
                            continue;
                        }
                        else if(countPairs == 1)
                        {
                            countPairs++;
                            for (int d = 0; d < 5; d++)
                            {
                                bot.winCards[d] = list.get(d);
                            }
                            bot.winValue = 200 + bot.secondValue;
                            bot.secondValue = list.get(i).getValue();
                            bot.setEndCardSystem(CardSystem.TWO_PAIR);
                            result = true;
                            break;
                        }
                    }
                }
            }
        }
        //todo: zmienna pomocnicza do bota ktory przechowuje value drugiej pary
        return result;
    }

    private boolean hasBotThreeOfAKind(Bot bot, List < List <Card> > allCards)
    {
        boolean result = false;
        boolean is = false;

        for(List <Card> list : allCards)
        {
            for(int i = 0; i < 5; i++)
            {
                for (int j = 0; j < 5; j++)
                {
                    if(i == j) continue;
                    for(int k = 0; k < 5; k++)
                    {
                        if(i == k || j == k) continue;

                        if(list.get(i).getValue() == list.get(j).getValue() && list.get(i).getValue() == list.get(k).getValue())
                        {
                            result = true;
                            bot.winValue = 300 + list.get(i).getValue();
                            bot.setEndCardSystem(CardSystem.THREE_OF_A_KIND);

                            if(is)
                            {
                                for(int l = 0; l < 5; l++)
                                {
                                    if (list.get(l).getValue() == bot.winCards[l].getValue())
                                        continue;

                                    else if (list.get(l).getValue() > bot.winCards[l].getValue())
                                    {
                                        for (int d = 0; d < 5; d++)
                                        {
                                            bot.winCards[d] = list.get(d);
                                        }
                                        break;
                                    }
                                    else break;
                                }
                            }

                            else
                            {
                                is = true;
                                for(int d = 0; d < 5; d++)
                                {
                                    bot.winCards[d] = list.get(d);
                                }
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    private boolean hasBotFourOfAKind(Bot bot, List < List <Card> > allCards)
    {
        for(List <Card> list : allCards)
        {
            if(list.get(1).getValue() == list.get(2).getValue() && list.get(2).getValue() == list.get(3).getValue())
            {
                if(list.get(2).getValue() == list.get(0).getValue() || list.get(2).getValue() == list.get(4).getValue())
                {
                    bot.winValue = 700 + list.get(2).getValue();
                    bot.setEndCardSystem(CardSystem.FOUR_OF_A_KIND);
                    for (int d = 0; d < 5; d++)
                    {
                        bot.winCards[d] = list.get(d);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasBotStraight(Bot bot, List < List <Card> > allCards)
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
                    bot.winValue = 400 + list.get(0).getValue();
                    bot.setEndCardSystem(CardSystem.STRAIGHT);
                    for (int d = 0; d < 5; d++)
                    {
                        bot.winCards[d] = list.get(d);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasBotStraightFlush(Bot bot, List < List <Card> > allCards)
    {
        for(List <Card> list : allCards)
        {
            int val = 1;
            int col = 1;
            int v = list.get(0).getValue();

            for (int i = 1; i < 5; i++)
            {
                if(list.get(i).getValue() + i == v)
                    val++;
                else
                    break;

                if(list.get(i).getSuit() == list.get(0).getSuit())
                    col++;
                else
                    break;

                if(val == 5 && col == 5)
                {
                    bot.winValue = 800 + list.get(0).getValue();
                    bot.setEndCardSystem(CardSystem.STRAIGHT_FLUSH);
                    for (int d = 0; d < 5; d++)
                    {
                        bot.winCards[d] = list.get(d);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasBotFlush(Bot bot, List < List <Card> > allCards)
    {
        for (List<Card> list : allCards)
        {
            int count = 1;
            Suit v = list.get(0).getSuit();

            for (int i = 1; i < 5; i++)
            {
                if(list.get(i).getSuit() == v)
                    count++;
                else
                    break;
            }

            if(count == 5)
            {
                bot.winValue = 500 + list.get(0).getValue();
                bot.setEndCardSystem(CardSystem.FLUSH);
                for (int d = 0; d < 5; d++)
                {
                    bot.winCards[d] = list.get(d);
                }
                return true;
            }
        }

        return false;
    }

    private boolean hasBotFullHouse(Bot bot, List < List <Card> > allCards)
    {
        for (List<Card> list : allCards)
        {
            boolean is = false;
            if(list.get(0).getValue() == list.get(1).getValue() && list.get(3).getValue() == list.get(4).getValue())
            {
                if(list.get(0).getValue() == list.get(2).getValue())
                {
                    bot.secondValue = list.get(4).getValue();
                    is = true;
                }
                else if(list.get(2).getValue() == list.get(4).getValue())
                {
                    bot.secondValue = list.get(0).getValue();
                    is = true;
                }

                if(is)
                {
                    bot.winValue = 600 + list.get(2).getValue();
                    bot.setEndCardSystem(CardSystem.FULL_HOUSE);
                    for (int d = 0; d < 5; d++)
                    {
                        bot.winCards[d] = list.get(d);
                    }
                    return true;
                }
            }
        }

        return false;
    }

    public Round(List<Bot> bots, int sb, int bb, Game game)
    {
        this.game = game;
        roundNumber++;
        this.bots = bots;
        goldAtTable = 0;

        this.sb = sb;
        this.bb = bb;

        MakeDeck();
        DistributeCards();
    }

   int countPlayersIN()
    {
        int playersIn = 0;
        for(Bot bot : bots)
        {
            if(bot.getGoldInGame() == inValue || bot.getGold() == 0)
            {
                playersIn++;
                if(!bot.didIMove)
                    playersIn--;
            }
        }
        return playersIn;
    }

    public void Play()
    {
        inValue = bb;

        for(Bot bot : bots)
        {
            if(bot.getPosition() == bots.size()-2)
            {
                AddGoldToGame(bot, sb);
            }

            else if(bot.getPosition() == bots.size()-1)
            {
                AddGoldToGame(bot, bb);
            }
        }

        Draw();
        stage = Stage.PREFLOP;
        botsInitialSize=bots.size();

        while(stage != Stage.END)
        {
            numberOfPlayersInGame = 1;

            for(Bot bot : bots)
            {
                bot.didIMove = false;
            }

            if(stage == Stage.FLOP)
            {
                Collections.shuffle(deck);

                tableCards[0] = deck.get(0);
                tableCards[1] = deck.get(1);
                tableCards[2] = deck.get(2);

                deck.remove(2);
                deck.remove(1);
                deck.remove(0);
            }
            else if(stage == Stage.TURN)
            {
                Collections.shuffle(deck);

                tableCards[3] = deck.get(0);

                deck.remove(0);
            }
            else if(stage == Stage.RIVER)
            {
                Collections.shuffle(deck);

                tableCards[4] = deck.get(0);

                deck.remove(0);
            }

            if(stage != Stage.PREFLOP && whoBetLast >= 0)
                for(Bot bot : bots)
                {
                    int k = (bot.getPosition() + (bots.size() - whoBetLast)) % bots.size();
                    bot.setPosition(k);
                }

            if(whoBetLast == -1)
            {
                for(Bot bot : bots)
                {
                    bot.setPosition( (bot.startRoundPosition + 2) % bots.size() );
                }
            }

            Collections.sort(bots, new Comparator<Bot>()
            {
                @Override
                public int compare(Bot b1, Bot b2)
                {
                    return b1.getPosition() - b2.getPosition();
                }
            });

            List<Bot> copyBots = new LinkedList<Bot>(bots);

            whoBetLast = -1;

            while (copyBots.size() != countPlayersIN())
            {
                for(Bot bot : copyBots)
                {
                    if(bots.size() == countPlayersIN())
                    {botsInitialSize=bots.size(); break;}
                    bot.MakeMove(bots, this);
                    if(bots.size() == 1)
                        break;
                }

                //copyBots = bots;
                copyBots = new LinkedList<Bot>(bots);

                Draw();
                try { System.in.read(); } catch (Exception e) {}

                if(bots.size() == 1)
                    break;
            }

            for(Bot bot : bots)
            {
                if(bot.lastAction == "All In" && bot.howManyPlayersInGameWhenBotAllIn == 1)
                        bot.setHowManyPlayersInGameWhenBotAllIn(bots.size());
            }

            stage = Stage.values()[stage.ordinal() + 1]; //next stage

            if(bots.size() == 1)
            {
                stage = Stage.END;
            }

                System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxx");

        }

        WinCheck(bots);
        bots.addAll(foldBots);
        foldBots.clear();

        game.setBots(bots);
        try { System.in.read(); } catch (Exception e) {}

        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxx");
        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxx");
        if(isVukomad)System.out.println("VUKO NISZCZY");

    }

    private void AddGoldToGame(Bot bot, int gold)
    {
        if(bot.getGold() - gold > 0)
        {
            bot.setGold(bot.getGold() - gold);
            bot.setGoldInGame(bot.getGoldInGame() + gold);
            goldAtTable +=gold;
        }
        else
        {
            int allBotGold = bot.getGold();
            bot.setGold(0);
            bot.setGoldInGame(bot.getGoldInGame() + allBotGold);
            bot.setLastAction("All In*");
            goldAtTable +=allBotGold;
        }
    }

    //TODO: ;delete
    private void PreFlop()
    {
        List<Bot> copyBots = new LinkedList<Bot>(bots);
        for(Bot bot : bots)
        {
            int value = bot.getStartingValue();

            if(value >= 30  && numberOfBets <= 4)
            {
                numberOfBets++;
                numberOfPlayersInGame = 1;
                bot.setLastAction("Podbicie 4*");
                AddGoldToGame(bot, 4*inValue-bot.getGoldInGame());
                inValue = bot.getGoldInGame();
            }
            else if(value > 25 && numberOfBets <= 1)
            {
                numberOfBets++;
                numberOfPlayersInGame = 1;
                bot.setLastAction("Podbicie 3*");
                AddGoldToGame(bot, 3*inValue-bot.getGoldInGame());
                inValue = bot.getGoldInGame();
            }
            else if(value > 25 && numberOfBets <= 2)
            {
                if(inValue-bot.getGoldInGame() == 0)
                    bot.setLastAction("Check*");
                else
                {
                    bot.setLastAction("Wejscie*");
                    numberOfPlayersInGame++;
                }
                AddGoldToGame(bot, inValue-bot.getGoldInGame());
                inValue = bot.getGoldInGame();
            }
            else if(value > 20 && numberOfBets <= 1)
            {
                if(inValue-bot.getGoldInGame() == 0)
                    bot.setLastAction("Check*");
                else
                {
                    bot.setLastAction("Wejscie*");
                    numberOfPlayersInGame++;
                }
                AddGoldToGame(bot, inValue-bot.getGoldInGame());
                inValue = bot.getGoldInGame();
            }
            else
            {
                if(inValue-bot.getGoldInGame() == 0)
                    bot.setLastAction("Check*");
                else
                {
                    bot.setLastAction("Pas*");
                    copyBots.remove(bot);
                }
            }

            //http://pokerzasady.pl/system-hutchinsona/
        }
        bots = copyBots;
    }

    public void Draw()
    {
        System.out.println("/////////////////////////////");

        System.out.println("Round: " + roundNumber + "\t" + "Gold at table: " + goldAtTable);
        System.out.println("Faza: " + getStage()); //TODO:Da sie cos tutaj z tym zamotac?
        System.out.println("Cards at table: ");
        for(int i = 0; i < 5; i++)
        {
            if(tableCards[i] != null)
                System.out.print(tableCards[i].getName() + " " + tableCards[i].getSuit() + " ");
        }
        System.out.println();

        /*int i = 0;
        for(Bot bot : bots)
        {
            System.out.println(bot.getPreName() + "\t" + bot.getName());

            Card cards[] = bot.getCards();
            System.out.print("\t" + cards[0].getName() + " " + cards[0].getSuit() + "  ");
            System.out.println(cards[1].getName() + " " + cards[1].getSuit());

            System.out.println("\t" + "Gold: " + bots.get(i).getGold());
            System.out.println("\t" + "GoldInGame: " + bots.get(i).getGoldInGame());

            System.out.println("\t" + "Value: " + bot.getStartingValue());

            System.out.println("Last Action: " + bot.getLastAction());

            System.out.println("");
            i++;
        }*/
    }

    //todo: bot sam pownien robic cos takiegooo.. lul - testowalem kiedys rzeczy
    private void SetStartingCardValue()
    {
        //System Hutchinsona

        for(Bot bot : bots)
        {
            Card cards[] = bot.getCards();

            int value = 0;

            int cardAValue = cards[0].getValue();
            int cardBValue = cards[1].getValue();
            value += cardAValue;
            value += cardBValue;

            if (cardAValue == cardBValue)
                value += 10;

            if (cards[0].getSuit() == cards[1].getSuit())
                value += 4;

            int difference = Math.abs(cardAValue - cardBValue);
            if (difference == 1)
                value += 3;
            else if (difference == 2)
                value += 2;
            else if (difference == 3)
                value += 1;

            bot.setStartingValue(value);
        }
    }

    private void MakeDeck()
    {
        deck.clear();
        for(int i = 0; i < 4; i++)
        {
            Suit suit = null;
            if(i == 0)
                suit = Suit.SPADE;
            else if(i == 1)
                suit = Suit.HEART;
            else if(i == 2)
                suit = Suit.DIAMOND;
            else if(i == 3)
                suit = Suit.CLUB;

            for (int j = 2; j <= 14; j++)
            {
                int k = i*13 + j-2;
                int worth = 0;

                String name = null;
                if(j <= 10)
                    name = Integer.toString(j);
                    worth = j;
                    if (j == 10) worth = 11;
                else if(j == 11)
                    {
                        name = "J";
                        worth = 12;
                    }
                else if(j == 12)
                    {
                        name = "Q";
                        worth = 13;
                    }
                else if(j == 13)
                    {
                        name = "K";
                        worth = 14;
                    }
                else if(j == 14)
                    {
                        name = "A";
                        worth = 16;
                    }

                deck.add(new Card(name, j, suit, worth));

                //System.out.println(deck.get(k).getName() + " " + deck.get(k).getValue() + " " + deck.get(k).getSuit().toString());
            }
        }
    }

    private void DistributeCards()
    {
        int i = 0;
        for(Bot bot : bots)
        {
            Card cards[] = new Card[2];
            Collections.shuffle(deck);

            cards[0] = deck.get(0);
            cards[1] = deck.get(1);

            bots.get(i).setCards(cards);

            deck.remove(1);
            deck.remove(0);

            i++;
        }
    }

    public int getNumberOfPlayersInGame()
    {
        return numberOfPlayersInGame;
    }

    public void setNumberOfPlayersInGame(int numberOfPlayersInGame)
    {
        this.numberOfPlayersInGame = numberOfPlayersInGame;
    }

    public int getNumberOfBets()
    {
        return numberOfBets;
    }

    public void setNumberOfBets(int numberOfBets)
    {
        this.numberOfBets = numberOfBets;
    }

    public int getGoldAtTable()
    {
        return goldAtTable;
    }

    public void setGoldAtTable(int goldAtTable)
    {
        this.goldAtTable = goldAtTable;
    }

    public int getInValue()
    {
        return inValue;
    }

    public void setInValue(int inValue)
    {
        this.inValue = inValue;
    }

    public static int getRoundNumber()
    {
        return roundNumber;
    }

    public static void setRoundNumber(int roundNumber)
    {
        Round.roundNumber = roundNumber;
    }

    public int getWhoBetLast()
    {
        return whoBetLast;
    }

    public void setWhoBetLast(int whoBetLast)
    {
        this.whoBetLast = whoBetLast;
    }

    public List<Bot> getFoldBots()
    {
        return foldBots;
    }

    public void setFoldBots(List<Bot> foldBots)
    {
        this.foldBots = foldBots;
    }

    public Stage getStage()
    {
        return stage;
    }

    public int getSb()
    {
        return sb;
    }

    public int getBb()
    {
        return bb;
    }

    public Card[] getTableCards()
    {
        return tableCards;
    }
}
