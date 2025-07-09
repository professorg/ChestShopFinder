package dev.kosdt.chestshopfinder;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ChestShopFinderGui extends Screen {

    private ButtonWidget mButtonScan = ButtonWidget
            .builder(Text.literal("Scan"), btn -> {
                chestShops = ChestShopUtils.getAllChestShops();
                replaceChestShops();
            })
            .build();

    private ButtonWidget mButtonClose = ButtonWidget
            .builder(Text.literal("Close"), btn -> {
                close();
            })
            .build();

    private ButtonWidget mButtonSortByItemName = ButtonWidget
            .builder(Text.literal("Item"), btn -> {
                sortBy(ChestShop.BY_ITEM_NAME);
            })
            .build();

    private ButtonWidget mButtonSortByPlayerName = ButtonWidget
            .builder(Text.literal("Player"), btn -> {
                sortBy(ChestShop.BY_PLAYER_NAME);
            })
            .build();

    private ButtonWidget mButtonSortByBuyPrice = ButtonWidget
            .builder(Text.literal("Buy"), btn -> {
                sortBy(ChestShop.BY_BUY_PRICE);
            })
            .build();

    private ButtonWidget mButtonSortBySellPrice = ButtonWidget
            .builder(Text.literal("Sell"), btn -> {
                sortBy(ChestShop.BY_SELL_PRICE);
            })
            .build();

    private TextFieldWidget mTextFieldSearchBox;

    private List<ChestShop> chestShops = new ArrayList<ChestShop>();

    private ChestShopListWidget mChestShopListWidget;

    private static final int OUTER_PADDING = 10;
    private static final int INNER_PADDING = 10;
    private static final int BUTTON_WIDTH = 100;
    private static final int BUTTON_HEIGHT = 20;
    private static final int INNER_WIDTH_PLACEHOLDER = 0;

    protected ChestShopFinderGui() {
        super(Text.literal("ChestShopFinder"));
    }

    public void updateDimensions() {
        int innerWidth = (width - 2*OUTER_PADDING - 3*INNER_PADDING) / 4;
        mButtonScan.setDimensionsAndPosition(BUTTON_WIDTH, BUTTON_HEIGHT, OUTER_PADDING, OUTER_PADDING);
        mButtonClose.setDimensionsAndPosition(
                BUTTON_WIDTH, BUTTON_HEIGHT,
                width - OUTER_PADDING - BUTTON_WIDTH, OUTER_PADDING);
        mButtonSortByItemName.setDimensionsAndPosition(
                innerWidth,
                BUTTON_HEIGHT,
                OUTER_PADDING,
                height - OUTER_PADDING - BUTTON_HEIGHT);
        mButtonSortByPlayerName.setDimensionsAndPosition(
                innerWidth,
                BUTTON_HEIGHT,
                OUTER_PADDING + innerWidth + INNER_PADDING,
                height - OUTER_PADDING - BUTTON_HEIGHT);
        mButtonSortByBuyPrice.setDimensionsAndPosition(
                innerWidth,
                BUTTON_HEIGHT,
                OUTER_PADDING + 2*innerWidth + 2*INNER_PADDING,
                height - OUTER_PADDING - BUTTON_HEIGHT);
        mButtonSortBySellPrice.setDimensionsAndPosition(
                innerWidth,
                BUTTON_HEIGHT,
                OUTER_PADDING + 3*innerWidth + 3*INNER_PADDING,
                height - OUTER_PADDING - BUTTON_HEIGHT);
        mTextFieldSearchBox.setDimensionsAndPosition(
                width - 2 * INNER_PADDING - 2 * BUTTON_WIDTH - 2 * OUTER_PADDING,
                BUTTON_HEIGHT,
                OUTER_PADDING + BUTTON_WIDTH + INNER_PADDING,
                OUTER_PADDING);
        mChestShopListWidget.setDimensionsAndPosition(
                width,
                height - 2 * OUTER_PADDING - 2 * BUTTON_HEIGHT - 2 * INNER_PADDING,
                0,
                OUTER_PADDING + BUTTON_HEIGHT + INNER_PADDING);
    }

    protected void updateSearchSuggestion() {
        if (mTextFieldSearchBox.getText().isEmpty())
            mTextFieldSearchBox.setSuggestion("Search");
        else
            mTextFieldSearchBox.setSuggestion("");
    }

    @Override
    public void init() {
        super.init();

        if (mTextFieldSearchBox == null) {
            mTextFieldSearchBox = new TextFieldWidget(textRenderer,
                    OUTER_PADDING + BUTTON_WIDTH + INNER_PADDING,
                    OUTER_PADDING,
                    width - 2 * INNER_PADDING - 2 * BUTTON_WIDTH - 2 * OUTER_PADDING,
                    BUTTON_HEIGHT,
                    Text.empty());
        }

        if (mChestShopListWidget == null) {
            mChestShopListWidget = new ChestShopListWidget(client,
                    width,
                    height - 2 * OUTER_PADDING - 2 * BUTTON_HEIGHT - 2 * INNER_PADDING,
                    OUTER_PADDING + BUTTON_HEIGHT + INNER_PADDING,
                    chestShops,
                    mTextFieldSearchBox.getText());
        }

        addDrawableChild(mButtonScan);
        addDrawableChild(mTextFieldSearchBox);
        mTextFieldSearchBox.setMaxLength(200);
        updateSearchSuggestion();
        mTextFieldSearchBox.setChangedListener(s -> {
            updateSearchSuggestion();
            updateFilteredChestShops();
        });
        addDrawableChild(mButtonClose);

        addDrawableChild(mButtonSortByItemName);
        addDrawableChild(mButtonSortByPlayerName);
        addDrawableChild(mButtonSortByBuyPrice);
        addDrawableChild(mButtonSortBySellPrice);


        addDrawableChild(mChestShopListWidget);

        updateDimensions();
    }

    private void replaceChestShops() {
        mChestShopListWidget.updateList(chestShops);
        updateFilteredChestShops();
    }

    protected void sortBy(Comparator<ChestShop> comparator) {
        mChestShopListWidget.sortBy(comparator);
    }

    private void updateFilteredChestShops() {
        mChestShopListWidget.filterText = mTextFieldSearchBox.getText();
        mChestShopListWidget.updateFilter();
        mChestShopListWidget.updateView();
    }
}