package dev.kosdt.chestshopfinder;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ChestShopFinderGui extends Screen {

    private ButtonWidget mButtonClose;
    private ButtonWidget mButtonScan;
    private ButtonWidget mButtonSortByItemName;
    private ButtonWidget mButtonSortByPlayerName;
    private ButtonWidget mButtonSortByBuyPrice;
    private ButtonWidget mButtonSortBySellPrice;

    private TextFieldWidget mTextFieldSearchBox;

    private List<ChestShop> chestShops;

    private ChestShopListWidget mChestShopListWidget;

    private static final int OUTER_PADDING = 10;
    private static final int INNER_PADDING = 10;
    private static final int BUTTON_WIDTH = 100;
    private static final int BUTTON_HEIGHT = 20;

    protected ChestShopFinderGui() {
        super(Text.literal("ChestShopFinder"));
    }

    @Override
    public void init() {
        super.init();
        addDrawableChild(
            mButtonScan = ButtonWidget
                    .builder(Text.literal("Scan"), btn -> {
                        chestShops = ChestShopUtils.getAllChestShops();
                        replaceChestShops();
                    })
                    .dimensions(OUTER_PADDING, OUTER_PADDING, BUTTON_WIDTH, BUTTON_HEIGHT)
                    .build()
        );
        addDrawableChild(
                mTextFieldSearchBox = new TextFieldWidget(textRenderer,
                    OUTER_PADDING + BUTTON_WIDTH + INNER_PADDING,
                    OUTER_PADDING,
                    width - 2*INNER_PADDING - 2*BUTTON_WIDTH - 2*OUTER_PADDING,
                    BUTTON_HEIGHT,
                    Text.empty())
        );
        mTextFieldSearchBox.setMaxLength(200);
        mTextFieldSearchBox.setSuggestion("Search");
        mTextFieldSearchBox.setChangedListener(s -> {
            if (s.isEmpty())
                mTextFieldSearchBox.setSuggestion("Search");
            else
                mTextFieldSearchBox.setSuggestion("");
            updateFilteredChestShops();
        });
        addDrawableChild(
            mButtonClose = ButtonWidget
                    .builder(Text.literal("Close"), btn -> {
                        close();
                    })
                    .dimensions(width - OUTER_PADDING - BUTTON_WIDTH, OUTER_PADDING, BUTTON_WIDTH, BUTTON_HEIGHT)
                    .build()
        );
        int innerWidth = (width - 2*OUTER_PADDING - 3*INNER_PADDING) / 4;
        addDrawableChild(
            mButtonSortByItemName = ButtonWidget
                    .builder(Text.literal("Item"), btn -> {
                        sortBy(ChestShop.BY_ITEM_NAME);
                    })
                    .dimensions(
                            OUTER_PADDING,
                            height - OUTER_PADDING - BUTTON_HEIGHT,
                            innerWidth,
                            BUTTON_HEIGHT)
                    .build()
        );
        addDrawableChild(
            mButtonSortByPlayerName = ButtonWidget
                    .builder(Text.literal("Player"), btn -> {
                        sortBy(ChestShop.BY_PLAYER_NAME);
                    })
                    .dimensions(
                            OUTER_PADDING + innerWidth + INNER_PADDING,
                            height - OUTER_PADDING - BUTTON_HEIGHT,
                            innerWidth,
                            BUTTON_HEIGHT)
                    .build()
        );
        addDrawableChild(
            mButtonSortByBuyPrice = ButtonWidget
                    .builder(Text.literal("Buy"), btn -> {
                        sortBy(ChestShop.BY_BUY_PRICE);
                    })
                    .dimensions(
                            OUTER_PADDING + 2*innerWidth + 2*INNER_PADDING,
                            height - OUTER_PADDING - BUTTON_HEIGHT,
                            innerWidth,
                            BUTTON_HEIGHT)
                    .build()
        );
        addDrawableChild(
            mButtonSortBySellPrice = ButtonWidget
                    .builder(Text.literal("Sell"), btn -> {
                        sortBy(ChestShop.BY_SELL_PRICE);
                    })
                    .dimensions(
                            OUTER_PADDING + 3*innerWidth + 3*INNER_PADDING,
                            height - OUTER_PADDING - BUTTON_HEIGHT,
                            innerWidth,
                            BUTTON_HEIGHT)
                    .build()
        );


        chestShops = new ArrayList<ChestShop>();
        addDrawableChild(
            mChestShopListWidget = new ChestShopListWidget(client,
                    width,
                    height - 2*OUTER_PADDING - 2*BUTTON_HEIGHT - 2*INNER_PADDING,
                    OUTER_PADDING + BUTTON_HEIGHT + INNER_PADDING,
                    chestShops,
                    mTextFieldSearchBox.getText())
        );
        updateFilteredChestShops();
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
        mChestShopListWidget.updateView();
    }
}