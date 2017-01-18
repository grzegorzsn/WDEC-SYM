package com.wdec;

import java.math.BigDecimal;

/*
 * Created by Grzegorz on 2017-01-12.
 */
public class DecisionMaker {

    public static Decision decide(DecisionDatabase database, BigDecimal availableMoney)
    {
        return decide(database, availableMoney, null);
    }

    public static Decision decide(DecisionDatabase database, BigDecimal availableMoney, BigDecimal desiredRisk)
    {
        if(desiredRisk != null) {
            return decideWithRisk(database, availableMoney, desiredRisk);
        }
        else {
            return decideWithoutRisk(database, availableMoney);
        }
    }

    private static Decision decideWithoutRisk(DecisionDatabase database, BigDecimal availableMoney)
    {
        Decision decision = database.findBestRecordWithMoneyBelow(availableMoney);
        recalculate(decision, availableMoney);
        return decision;
    }

    private static Decision decideWithRisk(DecisionDatabase database, BigDecimal availableMoney, BigDecimal desiredRisk)  {
        /*TODO*/
        Decision firstChoice = database.findRecordWithNearestRisk(desiredRisk,availableMoney);
        Decision secondChoice = database.findBestDecisionSimilarTo(firstChoice, availableMoney );
        return secondChoice;
    }

    private static void recalculate(Decision decision, BigDecimal availableMoney)
    {
        /*
        * TODO
        */
    }
}
