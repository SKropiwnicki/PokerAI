import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;


public class StealerBot extends Bot
{
    public StealerBot() {}


    public StealerBot(String name, int gold)
    {
        super(name, gold);
    }

    public void AI(List<Bot> bots, Round round)
    {
        this.bots = bots;
        this.round = round;

        if(round.getStage()==Stage.PREFLOP) {lastStage=Stage.PREFLOP; noRaiseCounter=0;}
        // to linijka uniemozliwiajaca bugi w wykrywaniu nowej fazy

        //Testowe sprawdzanie fazy gry
        if (lastStage==round.getStage()) { isNewPhase=false; }
        else { isNewPhase=true; lastStage=round.getStage(); }

        calculateStartingValue();
        clearFlagsAndValues();
        checkFlags();
        if (round.getStage() ==Stage.PREFLOP)
        {
            if(gold>round.getBb()*400)
            {
                if(StartingValue>20 && round.getInValue()<60)
                {
                    why="Mam duzy stack, moge zobaczyc flopa";
                    CheckIn();
                    return;
                }
            }

            if(StartingValue>=36)
            {
                if (isShortStack){ why="Short stack i dobra reka, wiec daje all in"; Bet(gold); return; }
                if (round.getInValue()>round.getBb()*30 || goldInGame > gold)
                {
                    why="Dobra reka, ale lepiej uwazac z podbijaniem";
                    CheckIn();
                    return;
                }
                why="Mam swietna reke! Podbijam!";
                Bet(round.getInValue()*3);
                return;
            }
            else if(isLatePosition)
            {
                System.out.println("Jestem late\t");
                if (someoneRaised)
                {
                    if(round.getInValue() > goldInGame*4)
                    {
                        if (goldInGame > round.getBb()*10 )
                        {
                            if(StartingValue>=32)
                            {
                                why="Jesli chca sie betowac to smialo, moja reka jest mocna!";
                                Bet(round.getInValue()*3);
                                return;
                            }
                            else
                            {
                                why="Duzo juz w to wlozylem, lepiej zobaczyc flopa, bo reka nienajgorsza";
                                CheckIn();
                                return;
                            }
                        }
                        else
                        {
                            if(StartingValue>=32)
                            {
                                why="Dobra reka, Podbijam!";
                                Bet(round.getInValue()*3);
                                return;
                            }
                            else
                            {
                                why="Nie mam dosc dobrych kart by ryzykowac + nie wrzucilem duzo w te gre";
                                Fold();
                                return;
                            }
                        }
                    }
                    else if(StartingValue>=29) {CheckIn(); return;}
                }
                else if(StartingValue>=18)
                     {
                         why="Stealuje to!";
                         Bet(round.getBb()*5);
                         return;
                     }
                     else
                     {
                         why="Slabe karty, ale zobaczenie flopa jest tanie. Moze cos wpadnie.";
                         CheckIn();
                         return;
                     }

            }

            else if(isMediumPosition)
            {
                System.out.println("Jestem medium \t");
                if (someoneRaised)
                {
                    if(round.getInValue() > goldInGame*4)
                    {
                        if (goldInGame > round.getBb()*10 )
                        {
                            if(StartingValue>=33)
                            {
                                why="Z taka dobre reka zaryzykuje dalsze podbicie!";
                                Bet(round.getInValue()*3);
                                return;
                            }
                            else if(StartingValue>23)
                            {
                                why="Cholera, niebiezpiecznie sie robi.. ";
                                CheckIn();
                                return;
                            }
                            else
                            {
                                why="Nie ma szans zeby sie dalej licytowal z tymi kartami...";
                                Fold();
                                return;
                            }
                        }
                        else
                        {
                            if(StartingValue>=33)
                            {
                                why="Podbije, bo karty fajne.";
                                Bet(round.getInValue()*3);
                                return;
                            }
                            else
                            {
                                why="No.. jak dla mnie to za duze ryzyko. Fold..";
                                Fold();
                                return;
                            }
                        }
                    }
                    else if(StartingValue>=31) {why="Ktos podbil, ale flop moze duzo zmienic."; CheckIn(); return;}
                }
                else if(StartingValue>=20) {why="Nie ma podbicia, sprobuje zrobic steal"; Bet(round.getInValue()*5);
                    return;}
            }
            else if(isEarlyPosition)
            {
                System.out.println("Jestem early \t");
                if (someoneRaised)
                {
                    if(round.getInValue() > goldInGame*4)
                    {
                        if (goldInGame > round.getBb()*10 )
                        {
                            if(StartingValue>=34)
                            {
                                why="Z taka dobre reka zaryzykuje dalsze podbicie!";
                                Bet(round.getInValue()*3);
                                return;
                            }
                            else if(StartingValue>24)
                            {
                                why="Cholera, niebiezpiecznie sie robi.. ";
                                CheckIn();
                                return;
                            }
                            else
                            {
                                why="Nie ma szans zeby sie dalej licytowal z tymi kartami...";
                                Fold();
                                return;
                            }
                        }
                        else
                        {
                            if(StartingValue>=34)
                            {
                                why="Dobra reka, slaba pozycja, ale sprawdzimy co zrobi reszta";
                                Bet(round.getInValue()*3);
                                return;
                            }
                            else
                            {
                                why="Nie z tej pozycji, nie z tymi kartami. Moze nastepna runda bedzie lepsza";
                                Fold();
                                return;
                            }
                        }
                    }
                    else if(StartingValue>=34) {why="Ktos podbil, ale flop moze duzo zmienic."; CheckIn(); return;}
                }
                else if(StartingValue>=30) {why="Nie ma podbicia, wiec wejdzmy z tym"; CheckIn(); return;}
            }
        }
        //======================================================================
        if(noRaiseCounter>2)
        {
            why="Zablefuje!";
            Bet(round.getInValue()*5);
            return;
        }
        if (hasFour())
        {
            if(isFourOnTable)
            {
                if(someoneRaised)
                {
                    if (hasHighestOnHand())
                    {
                        why = "4 na stole, ale mam wysoka karte, damy rade!";
                        Bet(round.getInValue() * 3);
                        return;
                    } else
                    {
                        why = "4 na stole i leci bet czyjs. Wyglada nieciekawie!";
                        Fold();
                        return;
                    }
                }
                else
                {
                    if (hasHighestOnHand())
                    {
                        why = "4 na stole, ale mam wysoka karte, damy rade!";
                        Bet(round.getInValue() * 3);
                        return;
                    } else
                    {
                        why = "4 na stole. Wyglada nieciekawie!";
                        CheckIn();
                        return;
                    }
                }
            }
            else
            {
                why="Mam 4, bede podbijal zeby zmusic ich do all-ina";
                Bet(round.getInValue()*5);
                return;
            }
        }

        else if (hasFull())
        {

            if(isFullOnTable)
            {
                if(someoneRaised)
                {
                    if(round.getInValue()>goldInGame*3 && goldInGame<=400)
                    {
                        why="Chyba ma 4, to zbyt ryzykowne";
                        Fold();
                        return;
                    }
                    else
                    {
                        why="Nie wydaje mi sie zeby ktos mial cos lepszego niz full ze stolu";
                        CheckIn();
                        return;
                    }
                }
                else
                {
                    why="Nieciekawa sytuacja, full na stole";
                    CheckIn();
                    return;
                }
            }
            else
            {
                if(someoneRaised)
                {
                    if(round.getInValue()>goldInGame*5 && goldInGame<=400)
                    {
                        why="Wole wiecej nie podbijac, nie wiem czemu przeciwnik tak podbil";
                        CheckIn();
                        return;
                    }
                    else
                    {
                        why="Ciekawe jak spodoba mu sie re-raise";
                        Bet(round.getInValue()*3);
                        return;
                    }
                }
                else
                {
                    why="Podbijmy stawke, skoro mam fulla.";
                    Bet(round.getInValue()*4);
                    return;
                }
            }
        }

        else if (hasSuit())
        {
            if(isSuitOnTable)
            {
                    why="Kolor na stole, zobaczmy co z tego wyjdzie";
                    CheckIn();
                    return;

            }
            else if(isFourColorsOnTable)
            {
                if (someoneRaised)
                {
                    if(round.getInValue()>500 && goldInGame<=100)
                    {
                        why="Szkoda zachodu na taka gre. Ryzyko ze ma lepsza reke jest duze";
                        Fold();
                        return;
                    }
                    else
                    {
                        why="Sprawdzmy to. Mam dobre przeczucie co do tego koloru";
                        CheckIn();
                        return;
                    }
                }
                else
                {
                    why="Podbijmy stawke, jest kolor jest zabawa!";
                    Bet(round.getInValue()*2);
                    return;
                }
            }
            else
            {
                if (someoneRaised)
                {
                    if(round.getInValue()>300)
                    {
                        why="Szkoda zachodu na taka gre. Ryzyko ze ma lepsza reke jest duze";
                        CheckIn();
                        return;
                    }
                    else
                    {
                        why="Sprawdzmy to. Mam dobre przeczucie co do tego koloru";
                        Bet(round.getInValue() * 2);
                        return;
                    }
                }
                else
                {
                    why="Kolor jest, mozna zwiekszy stawke tej gry";
                    Bet(round.getInValue()*3);
                    return;
                }
            }
        }
        else if(hasStraight(CardsCombos()))
        {
            if(isStraightOnTable)
            {
                if (someoneRaised)
                {
                    if(round.getInValue()>500 && goldInGame<=100)
                    {
                        why="Straight na stole i taki raise. Obawiam sie, ze moze byc slabo,";
                        Fold();
                        return;
                    }
                    else
                    {
                        why="Sprawdze to. Moze i jest straight na stole, ale jednak";
                        CheckIn();
                        return;
                    }
                }
                else
                {
                    why="Niebezpieczny ten stol ze straightem...";
                    CheckIn();
                    return;
                }
            }
            else
            {
                if (someoneRaised)
                {
                    if(round.getInValue()>300)
                    {
                        why="Straight jest, ale lepiej uwazac z podbijaniem";
                        CheckIn();
                        return;
                    }
                    else
                    {
                        why="Zobaczymy jak zareaguje na to. Mam straight, wiec podbije.";
                        Bet(round.getInValue() * 2);
                        return;
                    }
                }
                else
                {
                    why="Straight jest, mozna zwiekszyc stawke tej gry";
                    Bet(round.getInValue()*3);
                    return;
                }
            }
        }
        else if(hasThree())
        {
            if (someoneRaised)
            {
                if (isThreeOnTable)
                {
                    why = "jest 3 ze stolu, lepiej dac spokoj";
                    Fold();
                    return;
                }
                else
                {
                    why = "MAM 3 of kind! " + threeValue;
                    if (round.getInValue()>goldInGame*4 && goldInGame<300)
                    {
                        why= "Wyglada zbyt ryzykownie. Moze i mam 3, ale ten bet jest wysoki.";
                        Fold();
                        return;
                    }
                    else if(round.getInValue()<round.getBb()*20)
                    {
                        why= "Podbijmy troche stawke!";
                        Bet(round.getInValue()*5);
                        return;
                    }
                    else
                    {
                        CheckIn();
                        return;
                    }
                }
            }
            else
            {
                if (isThreeOnTable)
                {
                    why = "jest 3 ze stolu, lepiej bede uwazac";
                    CheckIn();
                    return;
                }
                else
                {
                    why = "MAM 3 of kind! " + threeValue;
                    Bet(round.getInValue()*5);
                    return;
                }
            }
        }
        else if (hasTwoPairs())
        {
            if(someoneRaised)
            {
                if (isTwoPairsOnTable)
                {
                    {
                        why = "Dwie pary na stole, wyglada niebezpiecznie!";
                        Fold();
                        return;
                    }
                }
                else if (isOneOfTwoPairsOnTable)
                {

                    if(round.getInValue()>goldInGame*3 && goldInGame>=100)
                    {
                        why = "Mam dwie pary, ale jedna z nich jest na stole. Ktos moze miec trojke";
                        Fold();
                        return;
                    }
                    else
                    {
                        why= "Niebezpiecznie, ale mozemy pograc";
                        CheckIn();
                        return;
                    }
                }
                else
                {
                    if(round.getInValue()>round.getBb()*100 && goldInGame<700)
                    {
                        why = "To zaczyna sie robic niebzpieczne, za duza stawka, a ktos za duzo podbija";
                        Fold();
                        return;
                    }
                    else
                    {
                        why= "Wysokie stawki, lepiej poczekamy tutaj na rozwoj stolu.";
                        CheckIn();
                        return;
                    }
                }
            }
            else
            {
                if (isTwoPairsOnTable)
                {
                    if (hasHighestOnHand())
                    {
                        why = "Dwie pary na stole, ale mam wysoka karte na rece";
                        Bet(round.getInValue() * 2);
                        return;
                    } else
                    {
                        why = "Dwie pary na stole, wyglada niebezpiecznie!";
                        CheckIn();
                        return;
                    }
                } else
                {
                    why = "MAM DWIE PARY LOL!";
                    Bet(round.getInValue() * 5);
                    return;
                }
            }
        }

        else if(hasOnePair())
        {
            if (someoneRaised)
            {
                if (isPairOnTable)
                {
                    why = "Moze I jest para, ale to ze stolu i jeszce ktos raisuje. Odpuszczam";
                    Fold();
                    return;
                }
                if (canBeHigherPair)
                {
                    why = "Moze i mam pare, ale ktos moze miec lepsza... Do tego ten raise";
                    Fold();
                    return;
                } else
                {
                    if(round.getInValue()>goldInGame*3 && goldInGame>=10)
                    {
                        why="Za duze ryzyko pchac sie tu z jedna para";
                        Fold();
                        return;
                    }
                    else
                    {
                        why="Zobaczmy co z tego wyjdzie";
                        CheckIn();
                        return;
                    }
                }
            }
            else
            {
                if (isPairOnTable)
                {
                    why = "Moze I jest para, ale to ze stolu. Poczekam.";
                    CheckIn();
                    return;
                }
                else if (canBeHigherPair)
                {
                    why = "Moze i mam pare, ale ktos moze miec lepsza";
                    CheckIn();
                    return;
                }
                else
                {
                    why= "Lekkie podbicie nikomu jeszcze nie zaszkodzilo";
                    Bet(round.getInValue()*3);
                    return;
                }
            }
        }



        if(round.getInValue()>goldInGame) {why="Dam Fold. Mam slabe przeczucie co do tej rundy"; Fold();
            return;}
        else {why="Narazie poczekamy na nastepne karty"; CheckIn();}

    }


}
