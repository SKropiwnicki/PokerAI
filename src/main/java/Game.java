import java.util.*;

/**
 * Created by Vuko on 2015-05-26.
 */
public class Game
{
    private MasterBot masterBot = new MasterBot();
    private List<SmartBot> smartBots = new LinkedList<SmartBot>();
    private List<Bot> bots = new LinkedList<Bot>();

    private int startingGold;
    private int bb;
    private int sb;
    private int numberOfPlayers;

    private int who;

    public Game()
    {
        startingGold = 2000;
        bb = 10;
        sb = 5;

        SetPlayers();

        while(true)
        {
            Collections.sort(bots, new Comparator<Bot>()
            {
                @Override
                public int compare(Bot b1, Bot b2)
                {
                    return b1.getStartRoundPosition() - b2.getStartRoundPosition();
                }
            });

            Round round = new Round(bots, sb, bb, this);

            round.Play();

            List <String> strBots = new LinkedList<String>();
            for(Bot bot : bots)
            {
                if(bot.getGold() == 0)
                {
                    strBots.add(bot.getName());
                }
            }

            List <Bot> cpyBots = new LinkedList<Bot>(bots);
            for(Bot bot : cpyBots)
            {
                for(String str : strBots)
                {
                    if(bot.getName() == str)
                    {
                        int pos = bot.getStartRoundPosition();
                        int i = 0;
                        for(Bot x : bots)
                        {
                            if(x.getStartRoundPosition() > pos)
                            {
                                bots.get(i).setStartRoundPosition(bots.get(i).getStartRoundPosition() - 1);
                            }
                            i++;
                        }
                        bots.remove(bot);
                    }
                }
            }

            if(round.getRoundNumber() > 0)
            {
                for(Bot bot : bots)
                {
                    bot.setPosition((bot.getStartRoundPosition() + 1) % (bots.size() + strBots.size()));
                    bot.setStartRoundPosition(bot.getPosition());
                }
            }

            for(Bot bot : bots)
            {
                //bot.setGold(startingGold);
                bot.setWinValue(0);
                bot.setLastAction("");
                bot.setGoldInGame(0);
                bot.setHowManyPlayersInGameWhenBotAllIn(-1);
                bot.setEndCardSystem(CardSystem.HIGH_CARD);

                String preName = new String();

                if(bot.getPosition() == bots.size()-2)
                    preName = "SB";
                else if(bot.getPosition() == bots.size()-1)
                    preName = "BB";

                bot.setPreName(preName);
            }
        }
    }

    private void SetPlayers()
    {
        bots.clear(); //just in case

        masterBot.setName("Vuko");
        masterBot.setGold(startingGold);
        bots.add(masterBot);

        SmartBot smartBotA = new SmartBot("BotA", startingGold);
        smartBots.add(smartBotA);

        SmartBot smartBotB = new SmartBot("BotB", startingGold);
        smartBots.add(smartBotB);

        StealerPassiveBot stealerPassiveBot = new StealerPassiveBot("Cwany Cyprian", startingGold);
        bots.add(stealerPassiveBot);

        StealerBot stealerBot = new StealerBot("Zlodziejski Zygmunt", startingGold);
        bots.add(stealerBot);

        PassiveBot passiveBot = new PassiveBot("Pasywny Piotrek", startingGold);
        bots.add(passiveBot);

        bots.addAll(smartBots);

        numberOfPlayers = bots.size();

        Random rand = new Random();
        who = rand.nextInt(numberOfPlayers); // 0 - start, 4 - sb, 5 - bb, laczna liczba graczy = 6

        int i = who;
        for(Bot bot : bots)
        {
            if(i - 2 >= 0)
                bot.setPosition((i-2) % numberOfPlayers);
            else
                bot.setPosition((numberOfPlayers + (i-2)) % numberOfPlayers);
            String preName = new String();// = Integer.toString(bot.getPosition());

            if(bot.getPosition() == numberOfPlayers-2)
                preName = "SB";
            else if(bot.getPosition() == numberOfPlayers-1)
                preName = "BB";
            bot.setPreName(preName);

            i++;

            bot.setStartRoundPosition(bot.getPosition());
        }
    }

    public List<Bot> getBots()
    {
        return bots;
    }

    public void setBots(List<Bot> bots)
    {
        this.bots = bots;
    }
}
