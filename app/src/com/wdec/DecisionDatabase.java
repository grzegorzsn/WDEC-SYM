package com.wdec;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * Created by Grzegorz on 2017-01-12.
 */
public class DecisionDatabase {

    private ArrayList<Decision> records;
    private HashMap<BigDecimal, HashMap<BigDecimal, BigDecimal>> costsDictionary;



    public DecisionDatabase() {
        this.records = new ArrayList<Decision>();
    }

    public DecisionDatabase(String decisionCsvFilePath, String costsCsvFilePath) throws Exception {

        ReadDecisionsFromCSV(decisionCsvFilePath);
        ReadSampleCostsFromCSV(costsCsvFilePath);
    }

    public void ReadDecisionsFromCSV(String csvFilePath) throws Exception {
        this.records = new ArrayList<Decision>();
        BufferedReader br = null;
        String line = "";
        try {
            InputStream input = getClass().getResourceAsStream(csvFilePath);
            br = new BufferedReader(new InputStreamReader(input));
            while ((line = br.readLine()) != null) {
                records.add(new Decision(line));
            }
        } catch (FileNotFoundException e) {
            throw new Exception();
        } catch (IOException e) {
            throw new Exception();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    throw new Exception();
                }
            }
        }
    }

    public void ReadSampleCostsFromCSV(String csvFilePath) throws Exception
    {
        this.costsDictionary = new HashMap<>();
        BufferedReader br = null;
        String line = "";
        costsDictionary = new HashMap<>();
        try {
            InputStream input = getClass().getResourceAsStream(csvFilePath);
            br = new BufferedReader(new InputStreamReader(input));
            while ((line = br.readLine()) != null) {
                addToCostsDict(line);
            }
        } catch (FileNotFoundException e) {
            throw new Exception();
        } catch (IOException e) {
            throw new Exception();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    throw new Exception();
                }
            }
        }
    }

    private void addToCostsDict(String line) {
        String[] values = line.split(";");
        BigDecimal volume = new BigDecimal(values[0]);
        BigDecimal quality = new BigDecimal(values[1]);
        BigDecimal cost = new BigDecimal(values[2]);
        if (costsDictionary.containsKey(volume)){
            costsDictionary.get(volume).put(quality,cost);
        }
        else
        {
            costsDictionary.put(volume, new HashMap<>());
            costsDictionary.get(volume).put(quality,cost);
        }
    }

    public void addRecord(Decision record) {
        records.add(record);
    }

    public Decision findBestRecordWithMoneyBelow(BigDecimal money) {
        Decision bestRecord = null;
        for (Decision record : records) {
            if (record.getMoney().compareTo(money) == -1) {
                if (bestRecord == null) {
                    bestRecord = record;
                } else if (record.getProfit().compareTo(bestRecord.getProfit()) == 1) {
                    bestRecord = record;
                }
            }
        }
        return bestRecord;
    }

    public Decision findRecordWithNearestRisk(BigDecimal riskSearched, BigDecimal maxMoney)
    {
        BigDecimal minDiff = new BigDecimal(1);
        Decision foundRecord =  null;
        for (Decision record : records) {
            if(maxMoney.compareTo(record.getMoney()) == -1) continue;
            BigDecimal risk = record.getRisk();
            BigDecimal diff = risk.subtract(riskSearched).abs();
            if(diff.compareTo(minDiff) == -1)
            {
                foundRecord = record;
                minDiff = diff;
            }
        }
        return foundRecord;
    }


    public Decision findBestDecisionSimilarTo(Decision decision, BigDecimal maxMoney, BigDecimal similarityEpsilon) {
        BigDecimal startingRisk = decision.getRisk();
        for (Decision record : records) {
            if(maxMoney.compareTo(record.getMoney()) == -1) continue;
            BigDecimal risk = record.getRisk();
            BigDecimal diff = risk.subtract(startingRisk).abs();
            if(diff.compareTo(similarityEpsilon) == -1 && record.getProfit().compareTo(decision.getProfit()) == 1)
            {
                decision = record;
            }
        }
        return decision;
    }

    public BigDecimal getSampleCost(Decision decision)
    {
        BigDecimal upperVolume = findUpperVolume(decision.getVolume());
        BigDecimal lowerVolume = findLowerVolume(decision.getVolume());
        BigDecimal lowerQualityOfUpperVolume = findLowerQuality(decision.getQuality(), upperVolume);
        BigDecimal lowerQualityOfLowerVolume = findLowerQuality(decision.getQuality(),lowerVolume);
        BigDecimal upperQualityOfUpperVolume = findUpperQuality(decision.getQuality(), upperVolume);
        BigDecimal upperQualityOfLowerVolume = findUpperQuality(decision.getQuality(),lowerVolume);

        ArrayList<BigDecimal> costs = new ArrayList<>();
        costs.add(costsDictionary.get(lowerVolume).get(lowerQualityOfLowerVolume));
        costs.add(costsDictionary.get(lowerVolume).get(upperQualityOfLowerVolume));
        costs.add(costsDictionary.get(upperVolume).get(lowerQualityOfUpperVolume));
        costs.add(costsDictionary.get(upperVolume).get(upperQualityOfLowerVolume));

        return Collections.max(costs,BigDecimal::compareTo);
    }

    private BigDecimal findLowerQuality(BigDecimal quality, BigDecimal volume)
    {
        Set<BigDecimal> qualitySet = costsDictionary.get(volume).keySet();
        BigDecimal result = null;
        for (BigDecimal q : qualitySet)
            if (q.compareTo(quality) == -1 || q.compareTo(quality) == 0) {
                if (result == null) result = q;
                if (q.subtract(quality).compareTo((result.subtract(quality))) == -1) result = q;
            }
        return result;
    }

    private BigDecimal findUpperQuality(BigDecimal quality, BigDecimal volume)
    {
        Set<BigDecimal> qualitySet = costsDictionary.get(volume).keySet();
        BigDecimal result = null;
        for (BigDecimal q : qualitySet)
            if (q.compareTo(quality) == 1 || q.compareTo(quality) == 0) {
                if (result == null) result = q;
                if (q.subtract(quality).compareTo((result.subtract(quality))) == -1) result = q;
            }
        return result;
    }

    private BigDecimal findUpperVolume(BigDecimal volume) {
        Set<BigDecimal> volumeSet = costsDictionary.keySet();
        BigDecimal result = null;
        for (BigDecimal v : volumeSet)
            if (v.compareTo(volume) == 1 || v.compareTo(volume) == 0) {
                if (result == null) result = v;
                if (v.subtract(volume).compareTo((result.subtract(volume))) == -1) result = v;
            }
        return result;
    }

    private BigDecimal findLowerVolume(BigDecimal volume)
    {
        Set<BigDecimal> volumeSet = costsDictionary.keySet();
        BigDecimal result = null;
        for( BigDecimal v : volumeSet)
            if( v.compareTo(volume) == -1 || v.compareTo(volume)==0)
            {
                if(result == null) result = v;
                if(v.subtract(volume).abs().compareTo((result.subtract(volume).abs())) == -1) result = v;
            }
        return  result;
    }


    @Override
    public String toString() {
        return "Database{" +
                "records=" + records +
                '}';
    }

}
