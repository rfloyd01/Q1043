package com.projectfloyd.Q1043.services;

import com.projectfloyd.Q1043.models.CoinValue;
import com.projectfloyd.Q1043.models.FrontendCoin;
import com.projectfloyd.Q1043.models.RedbookCoin;
import com.projectfloyd.Q1043.repo.RedbookCoinDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CoinService {

    private RedbookCoinDAO coinDAO;
    private double detailsMultiplier = 0.75;

    @Autowired
    public CoinService(RedbookCoinDAO coinDAO) {
        this.coinDAO = coinDAO;
    }

    public RedbookCoin getCoinById(int id) {
        Optional<RedbookCoin> coin = this.coinDAO.findById(id);
        if (!coin.isEmpty()) return coin.get();
        else return null;
    }

    public boolean addCoin(RedbookCoin coin) {
        if (coinDAO.save(coin) != null) return true;
        else return false;
    }

    public ArrayList<RedbookCoin> getCoinsByName(String name) {
        Optional<List<RedbookCoin>> coins = coinDAO.findByCoinName(name);
        if (!coins.isEmpty()) return new ArrayList<>(coins.get());
        else return null;
    }

    public ArrayList<RedbookCoin> getCoinsByNameAndYear(String name, Integer year) {
        //same as the getCoinsByName() method, but also filters coins that are from a specific year
        ArrayList<RedbookCoin> allCoins = this.getCoinsByName(name);
        if (allCoins != null) {
            for (int i = allCoins.size() - 1; i >= 0; i--)
                if (!allCoins.get(i).getManufacture_year().equals(year)) allCoins.remove(i);
        }

        return allCoins;
    }

    public List<FrontendCoin> getCoinValues(List<FrontendCoin> coins, String... types) {
        //This function looks at all the coins passed in from the front end that have bids and grades associated
        //with them and assigns them their actual value based on the grade and Redbook database info on the coin.

        //Find all coins in the database of the given types and place them into an array of maps. Each element
        //in the array will hold coins of a certain type (i.e. Half Cent, Large Cent, Quarter Dollar...). Each
        //of these elements will in turn be a map that is sub-divided by coin names (i.e. Classic Head, Braided Hair...).
        //Splitting of the coins like this is to make it easier to match them with the coins coming in from the front
        //end. Furthermore, some coins of different types have the same name (i.e. there's a Half Cent and Large Cent
        //Draped Bust)
        ArrayList<Map<String, ArrayList<RedbookCoin>>> redbookCoins = new ArrayList<>();

        for (int i = 0; i < types.length; i++) {
            redbookCoins.add(new HashMap<>());
            Optional<List<RedbookCoin>> foundCoins = coinDAO.findByCoinType(types[i]);
            if (foundCoins.isPresent()) {
                ArrayList<RedbookCoin> foundCoinsByType = new ArrayList<>(foundCoins.get());
                for (RedbookCoin coin : foundCoinsByType) {
                    if (redbookCoins.get(i).containsKey(coin.getCoinName())) redbookCoins.get(i).get(coin.getCoinName()).add(coin);
                    else {
                        ArrayList<RedbookCoin> newCoinList = new ArrayList<>();
                        newCoinList.add(coin);
                        redbookCoins.get(i).put(coin.getCoinName(), newCoinList);
                    }
                }
            }
        }

        //The below block of code just prints out all coins grabbed form the DB in a formatted manner
//        for (int i = 0; i < types.length; i++) {
//            System.out.println("Coins of type " + types[i]);
//            for (Map.Entry<String, ArrayList<RedbookCoin>> entry : redbookCoins.get(i).entrySet()) {
//                System.out.println("Coins with name " + entry.getKey());
//                for (RedbookCoin coin : entry.getValue()) System.out.println(coin);
//            }
//        }

        //To find a match for each coin we first filter by year, then coin name, then variant and then finally
        //by mint type
        for (FrontendCoin coin:coins) {
            boolean found = false;
            int typeIndex = 0;
            for (int i = 0; i < types.length; i++) {
                if (types[i].equals(coin.getCoinType())) typeIndex = i;
            }

            //only look at coins that are the correct type and have the same name
            ArrayList<RedbookCoin> matchingCoins =  redbookCoins.get(typeIndex).get(coin.getCoinName());
            int bestComparisonScore = -1; //used for finding the appropriate variant of the given coin

            if (matchingCoins != null) {
                for (int i = 0; i < matchingCoins.size(); i++) {
                    //need to account for potential null values in the database
                    String redbookVariant = matchingCoins.get(i).getVariant() == null ? "" : matchingCoins.get(i).getVariant();
                    String redbookMint = matchingCoins.get(i).getMint() == null ? "" : matchingCoins.get(i).getVariant();

                    if (!coin.getManufactureYear().equals(matchingCoins.get(i).getManufacture_year())) continue; //year can't be null
                    if (!coin.getMint().equals(redbookMint)) continue;
                    found = true; //we found at least one matching coin so set the found variable to true

                    //We're looking at a coin from the correct year and from the same mint. Now we need to check for the
                    //particular variant of the coin. This is intrinsically the trickiest part because spelling may be
                    //different between the redbook and Great Collections (i.e. "Small Berries, No Stems" vs. "Stemless, Berryless"
                    //or something similar. Instead of checking for an exact string match, a score will be given based on
                    //how similar the two strings are and the coin with the highest score will be deemed to be the correct one
                    int comparisonScore = compareVariants(coin.getVariant(), redbookVariant);

                    if (comparisonScore > bestComparisonScore) bestComparisonScore = comparisonScore;
                    else continue;

                    //The current coin beats our current best match so go ahead and update the estimated value
                    coin.setRedbookValue(getCoinValue(coin.getGrade(), matchingCoins.get(i)));
                    //System.out.println(matchingCoins.get(i).getCoinValues());

                    //Just for reference, also set the mintage if it's available
                    if (matchingCoins.get(i).getMintage() != null) coin.setMintage(matchingCoins.get(i).getMintage());
                }
            }

            if (!found) {
                //for whatever reason we weren't able to match the coin, most likely because the variant didn't
                //turn up anything, for now just set the value to -1 to indicate in the front end that the value
                //wasn't found
                coin.setRedbookValue(-1);
            }
            else {
                //we found a matching coin, see if our current Great Collections coin is in "details" condition or not
                //and update the price accordingly
                //if the coin is a details coin, then multiply its value by the detail multiplier
                if (coin.getDetails()) coin.setRedbookValue((int)(coin.getRedbookValue() * this.detailsMultiplier));
            }
        }

        //With all of the frontend coin array that's been updated with values and mintages
        return coins;
    }

    private int compareVariants(String variant, String redbookVariant) {
        //The purpose of this function is to compare two strings and give a numerical score based on
        //how similar they are. An exact match would get a score of 100, and two words without even
        //a single overlapping letter would get a score of 0

        //Before carrying out any logic, cast both strings to all lowercase letters and remove all punctuation, then
        //see if there's an exact match
        variant = variant.toLowerCase(Locale.ROOT);
        redbookVariant = redbookVariant.toLowerCase(Locale.ROOT);

        variant.replaceAll("[^0-9a-z]", "");
        redbookVariant.replaceAll("[^0-9a-z]", "");
        if (variant.equals(redbookVariant)) {
            System.out.println("Comparison score for the following strings: " + variant + ", " + redbookVariant + " = 100");
            return 100;
        }

        //The way this algorithm works is by simply comparing the total number of letters and numbers that the
        //two strings have in common. It doesn't care about the order of the letters, only what the actual letters
        //are (i.e. "hello" and "elloh" would be considered as the same string, although since they aren't an exact
        //match the score for these two strings would be 99 as opposed to 100).

        //Create two arrays of length 128 (the number of standard ASCII characters) to keep track of the occurence
        //if each character in each word.
        int[] variantCharacters = new int[128];
        int[] redbookCharacters = new int[128];

        for (int i = 0; i < variant.length(); i++) variantCharacters[(int)variant.charAt(i)]++;
        for (int i = 0; i < redbookVariant.length(); i++) redbookCharacters[(int)redbookVariant.charAt(i)]++;

        //now iterate over the arrays and see the ratio of letters that are the same. These ratios go into a weighted
        //average based on the length of the shorter string. For example, if the first string has a length of 10 and the
        //second string a length of 12, and both words contain 3 of the character 'a', then the contribution to the
        //weighted average would be 1/128 * 4/4 * 10 = 0.078. The final score is obtained by looking at the ratio of
        //the length of the longer word and the weighted average and then multiplying it by 99 (because 100 is reserved
        //for words that are an exact match)
        int shorterLength = Math.min(variant.length(), redbookVariant.length());
        int longerLength = Math.max(variant.length(), redbookVariant.length());
        int nonZeroCharacters = 0;
        double weightedAverage = 0;

        //convert all zeros to ones to ensure that no division by zero occurs. This technically throws off the
        //percentages a bit, but since it happens for all letters it should be fine.
        for (int i = 0; i < 128; i++) {
            int lessCharacters = (variantCharacters[i] < redbookCharacters[i]) ? variantCharacters[i] : redbookCharacters[i];
            int moreCharacters = (lessCharacters == variantCharacters[i]) ? redbookCharacters[i] : variantCharacters[i];
            if (moreCharacters == 0) continue;
            nonZeroCharacters++;

            weightedAverage += ((double) lessCharacters / moreCharacters);
        }
        weightedAverage *= ((double) shorterLength / nonZeroCharacters);

        System.out.println("Comparison score for the following strings: " + variant + ", " + redbookVariant + " = " + (int)(99 * (weightedAverage / longerLength)));

        return (int)(99 * (weightedAverage / longerLength));
    }

    private int getCoinValue(int grade, RedbookCoin coin) {
        //Looking at the current grade for the coin, a reasonable value for it is calculated with linear
        //extrapolation. The closest known grades, both above and below the coin grade are used.

        Integer lower_val = 0, upper_val = 0;
        double lower_grade = 0, upper_grade = 0;

        if (coin.getCoinValues() == null) coin.setValueArray();
        ArrayList<CoinValue> coinValues = coin.getCoinValues();

        for (int i = 0; i < coinValues.size(); i++) {
            int currentGrade = coinValues.get(i).grade;

            if (currentGrade == grade) return coinValues.get(i).value;

            if (currentGrade > grade) {
                lower_val = coinValues.get(i - 1).value;
                lower_grade = coinValues.get(i - 1).grade;

                upper_val = coinValues.get(i).value;
                upper_grade = currentGrade;
                break;
            }
        }

        //return a weighted average of the lower and upper values of the coin
        double weighted_average;
        if (upper_grade == lower_grade) {
            System.out.println("Something went wrong, upper and lower grades should be different");
            weighted_average = 0;
        }
        else weighted_average = (upper_grade - grade) / (upper_grade - lower_grade) * lower_val + (grade - lower_grade) / (upper_grade - lower_grade) * upper_val;

        return (int)weighted_average; //round down cents and return a whole number
    }
}
