package us.kosdt.chestshopfinder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.math.BlockPos;

public class ChestShop {

    private static enum SearchTextState {
        MATCHING_ALL,
        MATCHING_PLAYER,
        MATCHING_ITEM,
    }

    public String player;
    public int amount;
    public double buyPrice;
    public double sellPrice;
    public String itemName;
    public boolean buyPriceFound;
    public boolean sellPriceFound;
    public BlockPos position;

    private boolean invalidShop;

    private static final Pattern PRICES_PATTERN = Pattern.compile("([BS]) (\\d*\\.?\\d*)(?:\\s*:\\s*([BS]) (\\d*\\.*\\d*))?");

    public static final Comparator<ChestShop> BY_ITEM_NAME = (s1, s2) -> s1.itemName.compareTo(s2.itemName);
    public static final Comparator<ChestShop> BY_PLAYER_NAME = (s1, s2) -> s1.player.compareTo(s2.player);
    public static final Comparator<ChestShop> BY_BUY_PRICE = (s1, s2) -> s1.buyPriceFound ? (s2.buyPriceFound ? Double.compare(s1.buyPrice / s1.amount, s2.buyPrice / s2.amount) : 1) : (s2.buyPriceFound ? -1 : 0);
    public static final Comparator<ChestShop> BY_SELL_PRICE = (s1, s2) -> s1.sellPriceFound ? (s2.sellPriceFound ? Double.compare(s1.sellPrice / s1.amount, s2.sellPrice / s2.amount) : 1) : (s2.sellPriceFound ? -1 : 0);


    public ChestShop(TileEntitySign sign) {
        invalidShop = false;
        buyPriceFound = false;
        sellPriceFound = false;

        position = sign.getPos();
        player = sign.signText[0].getUnformattedText();
        amount = Integer.parseInt(sign.signText[1].getUnformattedText()); // Should be checked beforehand
        Matcher pricesMatcher = PRICES_PATTERN.matcher(sign.signText[2].getUnformattedText());
        pricesMatcher.find();
        for (int i = 1; i <= pricesMatcher.groupCount() && pricesMatcher.group(i) != null; i++) {
            if (!buyPriceFound && pricesMatcher.group(i).equals("B")) {
                buyPrice = Double.parseDouble(pricesMatcher.group(++i));
                buyPriceFound = true;
            } else if (!sellPriceFound && pricesMatcher.group(i).equals("S")) {
                sellPrice = Double.parseDouble(pricesMatcher.group(++i));
                sellPriceFound = true;
            } else {
                invalidShop = true;
            }
        }
        itemName = sign.signText[3].getUnformattedText();
    }

    public boolean searchTextMatches(String searchText) {
        List<String> terms = new ArrayList<String>(Arrays.asList(searchText.split(" ")));
        boolean matches = false;
        boolean matchesTmp = true;
        SearchTextState state = SearchTextState.MATCHING_ALL;
        for (int i = 0; i < terms.size(); i++) {
            String term = terms.get(i);
            if (term.length() == 0) continue;
            if (term.equals("|")) {
                matches |= matchesTmp;
                matchesTmp = true;
                continue;
            } else if (term.contains("|")) {
                List<String> newTerms = new ArrayList<String>(Arrays.asList(term.split("|")));
                for (int j = newTerms.size(); j > 0; j--) {
                    newTerms.add(j, "|");
                }
                terms.addAll(i+1, newTerms);
                continue;
            }
            switch (state) {
                case MATCHING_ALL:
                    if (term.charAt(0) == '@') {
                        if (term.equalsIgnoreCase("@player")) {
                            state = SearchTextState.MATCHING_PLAYER;
                        } else if (term.equalsIgnoreCase("@item")) {
                            state = SearchTextState.MATCHING_ITEM;
                        }
                    } else {
                        if (!(
                            itemName.toLowerCase().contains(term.toLowerCase())
                                    || player.toLowerCase().contains(term.toLowerCase()))
                        ) {
                            matchesTmp = false;
                        }
                    }
                    break;
                case MATCHING_ITEM:
                    if (!itemName.toLowerCase().contains(term.toLowerCase()))
                        matchesTmp = false;
                    state = SearchTextState.MATCHING_ALL;
                    break;
                case MATCHING_PLAYER:
                    if (!player.toLowerCase().contains(term.toLowerCase()))
                        matchesTmp = false;
                    state = SearchTextState.MATCHING_ALL;
                    break;
            }
            
        }
        matches |= matchesTmp;
        return matches;
    }

    /**
     * Roughly filter out chest shop signs.
     * @param sign The sign to be checked.
     * @return Whether the sign could be a chest shop.
     */
    public static boolean isAChestShopPreliminary(TileEntitySign sign) {
        return sign.signText[0].getUnformattedText().length() > 0
                && sign.signText[1].getUnformattedText().matches("\\d+") // I'm sorry
                && sign.signText[2].getUnformattedText().matches(PRICES_PATTERN.toString()) // Even more sorry
                && sign.signText[3].getUnformattedText().length() > 0;
    }

    public boolean validShop() {
        return !invalidShop;
    }

    public String prices() {
        String out = "";
        if (buyPriceFound) {
            out += String.format("Buy: %.2f (%.2f per) ", buyPrice, buyPrice / amount);
        }
        if (sellPriceFound) {
            out += String.format("Sell: %.2f (%.2f per) ", sellPrice, sellPrice / amount);
        }
        return out;
    }

    @Override
    public String toString() {
        String out = "";
        out += String.format("Position: %d %d %d ", position.getX(), position.getY(), position.getZ());
        out += String.format("Player: %s ", player);
        out += String.format("Amount: %d ", amount);
        if (buyPriceFound) {
            out += String.format("Buy Price: %.2f ", buyPrice);
        }
        if (sellPriceFound) {
            out += String.format("Sell Price: %.2f ", sellPrice);
        }
        out += String.format("Item Name: %s", itemName);
        return out;
    }

}