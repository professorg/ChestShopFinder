package dev.kosdt.chestshopfinder;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ElementListWidget;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ChestShopListWidget extends ElementListWidget<ChestShopListWidget.ChestShopWidget> {

    private final TextRenderer textRenderer;
    private static final int PADDING_AFTER = 10;

    protected String filterText;
    protected List<ChestShopListWidget.ChestShopWidget> shopEntries;
    protected Stream<ChestShopWidget> shopEntriesView;

    public ChestShopListWidget(MinecraftClient minecraftClient, int width, int height, int y,
        List<ChestShop> shops, String filterText) {
        super(minecraftClient, width, height, y, 5*minecraftClient.textRenderer.fontHeight + PADDING_AFTER);
        textRenderer = minecraftClient.textRenderer;
        this.shopEntries = chestShopWidgetsList(shops);
        this.filterText = filterText;
        shopEntriesView = shopEntries.stream();
    }

    public static Comparator<ChestShopWidget> liftComparator(Comparator<ChestShop> comparator) {
        return ((a, b) -> comparator.compare(a.chestShop, b.chestShop));
    }

    public static Predicate<ChestShopWidget> liftPredicate(Predicate<ChestShop> pred) {
        return (c -> pred.test(c.chestShop));
    }

    public static <T> Function<ChestShopWidget, T> liftMap(Function<ChestShop, T> f) {
        return (c -> f.apply(c.chestShop));
    }

    @Override
    public ChestShopWidget getSelectedOrNull() {
        return super.getSelectedOrNull();
    }

    public List<ChestShopListWidget.ChestShopWidget> chestShopWidgetsList(List<ChestShop> shops) {
        return shops.stream().map(ChestShopListWidget.ChestShopWidget::new).toList();
    }

    public void updateList(List<ChestShop> shops) {
        replaceEntries(chestShopWidgetsList(shops));
        updateFilter();
        updateView();
    }

    public void updateFilter() {
        shopEntriesView = shopEntries.stream()
                .filter(liftPredicate(c -> c.searchTextMatches(filterText)));
    }

    public void sortBy(Comparator<ChestShop> comparator) {
        updateFilter();
        shopEntriesView = shopEntriesView.sorted(liftComparator(comparator));
        updateView();
    }

    public void updateView() {
        replaceEntries(shopEntriesView.toList());
        if (!isScrollbarVisible()) {
            setScrollAmount(0.0);
        } else if (getSelectedOrNull() != null) {
            centerScrollOn(getSelectedOrNull());
        }
    }

    public class ChestShopWidget extends ElementListWidget.Entry<ChestShopWidget> {

        protected ChestShop chestShop;

        public ChestShopWidget(ChestShop chestShop) {
            this.chestShop = chestShop;
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            int h = textRenderer.fontHeight;
            if (getSelectedOrNull() == this) {
                context.drawBorder(x, y, entryWidth, entryHeight, 0xAA555555);
            }
            context.drawTextWithShadow(textRenderer, chestShop.player, x, y, 0xAACCCCCC);
            context.drawTextWithShadow(textRenderer, String.valueOf(chestShop.amount), x, y + h, 0xAACCCCCC);
            context.drawTextWithShadow(textRenderer, chestShop.prices(), x, y + 2*h, 0xAACCCCCC);
            context.drawTextWithShadow(textRenderer, chestShop.itemName, x, y + 3*h, 0xAACCCCCC);
            context.drawTextWithShadow(textRenderer, chestShop.position.toShortString(), x, y + 4*h, 0xAACCCCCC);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            super.mouseClicked(mouseX, mouseY, button);
            if (isMouseOver(mouseX, mouseY)) {
                setSelected(this);
            }
            return true;
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return Collections.emptyList();
        }

        @Override
        public List<? extends Element> children() {
            return Collections.emptyList();
        }
    }

}
