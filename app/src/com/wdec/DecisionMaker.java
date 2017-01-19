package com.wdec;

import java.math.BigDecimal;

/*
 * Created by Grzegorz on 2017-01-12.
 */
public class DecisionMaker {


    private static BigDecimal similarityEpsilon = new BigDecimal("0.05");

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
        decision = recalculate(database, new Decision(decision), availableMoney);
        return decision;
    }

    private static Decision decideWithRisk(DecisionDatabase database, BigDecimal availableMoney, BigDecimal desiredRisk)  {
        Decision firstChoice = database.findRecordWithNearestRisk(desiredRisk,availableMoney);
        Decision secondChoice = database.findBestDecisionSimilarTo(firstChoice, availableMoney, similarityEpsilon);
        return secondChoice;
    }

    private static Decision recalculate(DecisionDatabase database, Decision decision, BigDecimal availableMoney)
    {
        BigDecimal lastSampleCost = database.getSampleCost(decision);
        BigDecimal initialVolume = decision.getVolume();
        BigDecimal sampleCost = lastSampleCost;
        BigDecimal newVolume = initialVolume;
        BigDecimal lastVolume = null;
        while(!newVolume.equals(lastVolume))
        {
            sampleCost = database.getSampleCost(decision);
            lastVolume = newVolume;
            newVolume = decision.getMoney().divide(sampleCost,0, BigDecimal.ROUND_HALF_DOWN);
            decision.setVolume(newVolume);
        }
        if(newVolume.compareTo(initialVolume) == 1) decision.setVolume(newVolume);
        else decision.setVolume(initialVolume);

        BigDecimal additionalVolume = decision.getVolume().subtract(initialVolume);
        BigDecimal additionalProfit = decision.getPrice().multiply(additionalVolume);
        decision.setProfit(decision.getProfit().add(additionalProfit));

        return decision;
    }


    
    public static BigDecimal getSimilarityEpsilon() {
        return similarityEpsilon;
    }

    public static void setSimilarityEpsilon(BigDecimal similarityEpsilon) {
        DecisionMaker.similarityEpsilon = similarityEpsilon;
    }
}
