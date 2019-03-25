package us.kosdt.chestshopfinder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import us.kosdt.chestshopfinder.ChestShopUtils.Point;
import us.kosdt.chestshopfinder.ChestShopUtils.Rectangle;

public class ChestShopFinderGui extends GuiScreen {

    private GuiButton mButtonClose;
    private GuiButton mButtonScan;
    private GuiButton mButtonSortByItemName;
    private GuiButton mButtonSortByPlayerName;
    private GuiButton mButtonSortByBuyPrice;
    private GuiButton mButtonSortBySellPrice;

    private GuiTextField mTextFieldSearchBox;

    private Rectangle listBoundingBox;
    private Rectangle scrollBarTrackBox;
    private Rectangle scrollBarBoundingBox;
    private boolean scrollable;
    private boolean scrolling;
    private int scrollingStartOffset;
    private int scrollPos;
    private int scrollTotal;
    private int cid;

    private Rectangle baseEntryBoundingBox;

    private List<ChestShop> chestShops;
    private List<ChestShop> chestShopsFiltered;

    private static final int ENTRY_HEIGHT = 45;

    @Override
    public void initGui() {
        super.initGui();
        scrolling = false;
        scrollingStartOffset = 0;
        cid = 0;
        buttonList.add(
            mButtonScan = new GuiButton(cid++, 20, 20, 100, 20, "Scan")
        );
        buttonList.add(
            mButtonClose = new GuiButton(cid++, width - 120, 20, 100, 20, "Close")
        );
        int innerWidth = (width - 40) / 4;
        buttonList.add(
            mButtonSortByItemName = new GuiButton(cid++, 20, height - 30, innerWidth, 20, "Item")
        );
        buttonList.add(
            mButtonSortByPlayerName = new GuiButton(cid++, 20 + innerWidth, height - 30, innerWidth, 20, "Player")
        );
        buttonList.add(
            mButtonSortByBuyPrice = new GuiButton(cid++, 20 + 2*innerWidth, height - 30, innerWidth, 20, "Buy")
        );
        buttonList.add(
            mButtonSortBySellPrice = new GuiButton(cid++, 20 + 3*innerWidth, height - 30, innerWidth, 20, "Sell")
        );
        mTextFieldSearchBox = new GuiTextField(cid++, fontRenderer, 130, 25, width - 260, 10);
        mTextFieldSearchBox.setMaxStringLength(200);
        mTextFieldSearchBox.setText("");

        chestShops = new ArrayList<ChestShop>();
        updateFilteredChestShops();
    }

    @Override
    public void actionPerformed(GuiButton button) throws IOException {
        if (button == mButtonClose) {
            mc.player.closeScreen();
        } else if (button == mButtonScan) {
            chestShops = ChestShopUtils.getAllChestShops();
            updateFilteredChestShops();
        } else if (button == mButtonSortByItemName) {
            sortBy(ChestShop.BY_ITEM_NAME);
        } else if (button == mButtonSortByPlayerName) {
            sortBy(ChestShop.BY_PLAYER_NAME);
        } else if (button == mButtonSortByBuyPrice) {
            sortBy(ChestShop.BY_BUY_PRICE);
        } else if (button == mButtonSortBySellPrice) {
            sortBy(ChestShop.BY_SELL_PRICE);
        }
    }
    
    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton == 0) {
            if (scrollable && scrollBarBoundingBox.contains(new Point(mouseX, mouseY))) {
                scrolling = true;
                scrollingStartOffset = scrollBarBoundingBox.position.y - mouseY;
            }
        }
        mTextFieldSearchBox.mouseClicked(mouseX, mouseY, mouseButton);
        
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        handleClickScrolling(mouseX, mouseY);
        // Draw list of chest shops
        drawList();
        if (scrollable) { // Do not need scroll bar
            drawScrollBar();
        }

        mTextFieldSearchBox.drawTextBox();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        mTextFieldSearchBox.updateCursorCounter();
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        String searchText = mTextFieldSearchBox.getText();
        mTextFieldSearchBox.textboxKeyTyped(typedChar, keyCode);
        if (!searchText.equals(mTextFieldSearchBox.getText())) {
            updateFilteredChestShops();
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        if (scrollable && !scrolling) {
            handleWheelScrolling();
        }

    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private void sortBy(Comparator<ChestShop> comparator) {
        chestShopsFiltered.sort(comparator);
    }

    private void updateScrollable() {
        listBoundingBox = new Rectangle(new Point(20, 50), width - 40, height - 90);
        baseEntryBoundingBox = listBoundingBox.withHeight(ENTRY_HEIGHT);
        scrolling = false;
        scrollPos = 0;
        scrollTotal = baseEntryBoundingBox.height * chestShopsFiltered.size();
        scrollable = chestShopsFiltered.size() * baseEntryBoundingBox.height > listBoundingBox.height;
        if (scrollable) { // Leave 10px for scroll bar
            listBoundingBox = listBoundingBox.resizeBy(-10, 0);
            scrollBarTrackBox = new Rectangle(listBoundingBox.topRight(), 10, listBoundingBox.height);
            scrollBarBoundingBox = scrollBarTrackBox.scaleBy(1.0, scrollBarTrackBox.height / (double) scrollTotal);
        } else {
            scrollBarTrackBox = new Rectangle(listBoundingBox.topRight(), 0, listBoundingBox.height);
            scrollBarBoundingBox = scrollBarTrackBox;
        }
        baseEntryBoundingBox = listBoundingBox.withHeight(ENTRY_HEIGHT);
    }

    private void updateFilteredChestShops() {
        chestShopsFiltered = chestShops.stream()
                .filter(shop -> shop.searchTextMatches(mTextFieldSearchBox.getText()))
                .collect(Collectors.toList());

        updateScrollable();
    }

    private void drawList() {
        Rectangle current;
        for (int i = 0; i < chestShopsFiltered.size(); i++) {
            int yOffset = i*baseEntryBoundingBox.height;
            current = baseEntryBoundingBox.translateBy(new Point(0, yOffset - scrollPos));
            if (!listBoundingBox.overlaps(current)) continue;
            Rectangle entryItemName = current.resizeBy(-10, 0).withHeight(10).translateBy(new Point(5, 0));
            Rectangle entryPosition = entryItemName.translateBy(new Point(0, 10));
            Rectangle entryPrices = entryPosition.translateBy(new Point(0, 10));
            Rectangle entryPlayer = entryPrices.translateBy(new Point(0, 10));
            ChestShop shop = chestShopsFiltered.get(i);
            if (listBoundingBox.contains(entryItemName))
                fontRenderer.drawString(
                    fontRenderer.trimStringToWidth(shop.itemName, entryItemName.width),
                    entryItemName.position.x,
                    entryItemName.position.y,
                    0xFFFFFF
                );
            if (listBoundingBox.contains(entryPosition))
                fontRenderer.drawString(
                    fontRenderer.trimStringToWidth(
                        String.format("%d %d %d", shop.position.getX(), shop.position.getY(), shop.position.getZ()
                    ), entryPosition.width),
                    entryPosition.position.x,
                    entryPosition.position.y,
                    0xFFFFFF
                );
            if (listBoundingBox.contains(entryPrices))
                fontRenderer.drawString(
                    fontRenderer.trimStringToWidth(shop.prices(), entryPrices.width),
                    entryPrices.position.x,
                    entryPrices.position.y,
                    0xFFFFFF
                );
            if (listBoundingBox.contains(entryPlayer))
                fontRenderer.drawString(
                    fontRenderer.trimStringToWidth(shop.player, entryPlayer.width),
                    entryPlayer.position.x,
                    entryPlayer.position.y,
                    0xFFFFFF
                );
        }
    }

    private void drawScrollBar() {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        scrollBarTrackBox.draw(0xFF202020);
        scrollBarBoundingBox.draw(0xFF909090);
        GL11.glPopMatrix(); 
    }

    private void handleClickScrolling(int mouseX, int mouseY) {

        if (Mouse.isButtonDown(0)) {
            if (scrolling) {
                int newY = Math.max(
                    Math.min(mouseY + scrollingStartOffset, scrollBarTrackBox.bottom() - scrollBarBoundingBox.height),
                    scrollBarTrackBox.top()
                );
                updateScrollPos(newY);
            }
        } else {
            scrolling = false;
        }

    }

    private void handleWheelScrolling() {
        int scrollAmount = Mouse.getEventDWheel();
        double scrollPercent = (double)scrollAmount / scrollTotal;
        int newY = Math.max(
            Math.min(scrollBarBoundingBox.top() + (int)(scrollPercent*scrollBarTrackBox.height), scrollBarTrackBox.bottom() - scrollBarBoundingBox.height),
            scrollBarTrackBox.top()
        );
        updateScrollPos(newY);
    }

    private void updateScrollPos(int newY) {
        scrollBarBoundingBox = scrollBarBoundingBox.withY(newY);
        scrollPos = (int)(
                (double)(scrollBarBoundingBox.top() - scrollBarTrackBox.top())  // Distance from top of track
                / scrollBarTrackBox.height                                      // Divided by total distance (now percent of total)
                * scrollTotal 
        );
    }

}