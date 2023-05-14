package mod.chiselsandbits.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.chiselsandbits.api.multistate.snapshot.IMultiStateSnapshot;
import mod.chiselsandbits.api.util.constants.Constants;
import mod.chiselsandbits.client.screens.widgets.MultiStateSnapshotWidget;
import mod.chiselsandbits.container.ModificationTableContainer;
import mod.chiselsandbits.multistate.snapshot.EmptySnapshot;
import mod.chiselsandbits.recipe.modificationtable.ModificationTableRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ModificationTableScreen extends AbstractContainerScreen<ModificationTableContainer>
{
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Constants.MOD_ID, "textures/gui/container/modification_table.png");
    private               float            sliderProgress;
    /** Is {@code true} if the player clicked on the scroll wheel in the GUI. */
    private               boolean          clickedOnSroll;
    /**
     * The index of the first recipe to display.
     * The number of recipes displayed at any time is 12 (4 recipes per row, and 3 rows). If the player scrolled down one
     * row, this value would be 4 (representing the index of the first slot on the second row).
     */
    private               int              recipeIndexOffset;
    private boolean hasItemsInInputSlot;

    private MultiStateSnapshotWidget snapshotWidget;
    private int                      lastRenderedSelectedRecipeIndex = -1;

    public ModificationTableScreen(ModificationTableContainer containerIn, Inventory playerInv, Component titleIn) {
        super(containerIn, playerInv, titleIn);
        containerIn.setInventoryUpdateListener(this::onInventoryUpdate);
        --this.titleLabelY;
        this.imageHeight = 197;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init()
    {
        super.init();
        this.snapshotWidget = this.addRenderableWidget(new MultiStateSnapshotWidget(this.leftPos + 51,this.topPos + 71, 66,28, Component.translatable(Constants.MOD_ID + ".screen.widgets.multistate.preview")));
    }

    public void render(@NotNull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @SuppressWarnings("deprecation")
    protected void renderBg(@NotNull PoseStack matrixStack, float partialTicks, int x, int y) {
        this.renderBackground(matrixStack);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
        int left = this.leftPos;
        int top = this.topPos;
        this.blit(matrixStack, left, top, 0, 0, this.imageWidth, this.imageHeight);
        int sliderOffset = (int)(41.0F * this.sliderProgress);
        this.blit(matrixStack, left + 119, top + 15 + sliderOffset, this.imageWidth + (this.canScroll() ? 0 : 12), 0, 12, 15);
        int recipesLeft = this.leftPos + 52;
        int recipesTop = this.topPos + 14;
        int recipeIndexOffsetMax = this.recipeIndexOffset + 12;
        this.renderButtons(matrixStack, x, y, recipesLeft, recipesTop, recipeIndexOffsetMax);
        this.drawRecipesItems(matrixStack, recipesLeft, recipesTop, recipeIndexOffsetMax);

        if (this.lastRenderedSelectedRecipeIndex != this.menu.getSelectedRecipe() && this.hasItemsInInputSlot) {
            this.lastRenderedSelectedRecipeIndex = this.menu.getSelectedRecipe();

            final IMultiStateSnapshot snapshot = this.menu.getRecipeList().get(this.lastRenderedSelectedRecipeIndex).getAppliedSnapshot(this.menu.inputInventory);
            this.snapshotWidget.setSnapshot(snapshot);
        }
    }

    protected void renderTooltip(@NotNull PoseStack matrixStack, int x, int y) {
        super.renderTooltip(matrixStack, x, y);
        if (this.hasItemsInInputSlot) {
            int i = this.leftPos + 52;
            int j = this.topPos + 14;
            int k = this.recipeIndexOffset + 12;
            List<ModificationTableRecipe> list = this.menu.getRecipeList();

            for(int l = this.recipeIndexOffset; l < k && l < this.menu.getRecipeListSize(); ++l) {
                int i1 = l - this.recipeIndexOffset;
                int j1 = i + i1 % 4 * 16;
                int k1 = j + i1 / 4 * 18 + 2;
                if (x >= j1 && x < j1 + 16 && y >= k1 && y < k1 + 18) {
                    this.renderTooltip(matrixStack, list.get(l).getDisplayName(), x, y);
                }
            }
        }

    }

    private void renderButtons(PoseStack matrixStack, int x, int y, int p_238853_4_, int p_238853_5_, int p_238853_6_) {
        for(int i = this.recipeIndexOffset; i < p_238853_6_ && i < this.menu.getRecipeListSize(); ++i) {
            int j = i - this.recipeIndexOffset;
            int k = p_238853_4_ + j % 4 * 16;
            int l = j / 4;
            int i1 = p_238853_5_ + l * 18 + 2;
            int j1 = this.imageHeight;
            if (i == this.menu.getSelectedRecipe()) {
                j1 += 18;
            } else if (x >= k && y >= i1 && x < k + 16 && y < i1 + 18) {
                j1 += 36;
            }

            this.blit(matrixStack, k, i1 - 1, 0, j1, 16, 18);
        }

    }

    private void drawRecipesItems(final PoseStack poseStack, int recipesLeft, int recipesTop, int recipeIndexOffsetMax) {
        List<ModificationTableRecipe> list = this.menu.getRecipeList();

        for(int offset = this.recipeIndexOffset; offset < recipeIndexOffsetMax && offset < this.menu.getRecipeListSize(); ++offset) {
            int itemIndex = offset - this.recipeIndexOffset;
            int itemX = recipesLeft + itemIndex % 4 * 16;
            int rowIndex = itemIndex / 4;
            int itemY = recipesTop + rowIndex * 18 + 2;
            if (this.minecraft != null)
            {
                this.minecraft.getItemRenderer().renderAndDecorateItem(poseStack, list.get(offset).getCraftingBlockResult(this.menu.inputInventory), itemX, itemY);
            }
        }

    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.clickedOnSroll = false;
        if (this.hasItemsInInputSlot) {
            int i = this.leftPos + 52;
            int j = this.topPos + 14;
            int k = this.recipeIndexOffset + 12;

            for(int l = this.recipeIndexOffset; l < k; ++l) {
                int i1 = l - this.recipeIndexOffset;
                double d0 = mouseX - (double)(i + i1 % 4 * 16);
                double d1 = mouseY - (double)(j + i1 / 4 * 18);
                if (this.minecraft != null && this.minecraft.player != null && d0 >= 0.0D && d1 >= 0.0D && d0 < 16.0D && d1 < 18.0D
                      && this.menu.clickMenuButton(this.minecraft.player, l))
                {
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
                    if (this.minecraft.gameMode != null)
                    {
                        this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, l);
                    }
                    return true;
                }
            }

            i = this.leftPos + 119;
            j = this.topPos + 9;
            if (mouseX >= (double)i && mouseX < (double)(i + 12) && mouseY >= (double)j && mouseY < (double)(j + 54)) {
                this.clickedOnSroll = true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.clickedOnSroll && this.canScroll()) {
            int i = this.topPos + 14;
            int j = i + 54;
            this.sliderProgress = ((float)mouseY - (float)i - 7.5F) / ((float)(j - i) - 15.0F);
            this.sliderProgress = Mth.clamp(this.sliderProgress, 0.0F, 1.0F);
            this.recipeIndexOffset = (int)((double)(this.sliderProgress * (float)this.getHiddenRows()) + 0.5D) * 4;
            return true;
        } else {
            if (this.snapshotWidget.isMouseOver(mouseX, mouseY)) {
                return this.snapshotWidget.mouseDragged(mouseX, mouseY, button, dragX, dragY);
            }
            return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (this.snapshotWidget.isMouseOver(mouseX, mouseY)) {
            return this.snapshotWidget.mouseScrolled(mouseX, mouseY, delta);
        }

        if (this.canScroll()) {
            int i = this.getHiddenRows();
            this.sliderProgress = (float)((double)this.sliderProgress - delta / (double)i);
            this.sliderProgress = Mth.clamp(this.sliderProgress, 0.0F, 1.0F);
            this.recipeIndexOffset = (int)((double)(this.sliderProgress * (float)i) + 0.5D) * 4;
        }

        return true;
    }

    private boolean canScroll() {
        return this.hasItemsInInputSlot && this.menu.getRecipeListSize() > 12;
    }

    protected int getHiddenRows() {
        return (this.menu.getRecipeListSize() + 4 - 1) / 4 - 3;
    }

    /**
     * Called every time this screen's container is changed (is marked as dirty).
     */
    private void onInventoryUpdate() {
        this.hasItemsInInputSlot = this.menu.hasItemsInInputSlot();
        if (!this.hasItemsInInputSlot) {
            this.sliderProgress = 0.0F;
            this.recipeIndexOffset = 0;
            this.snapshotWidget.setSnapshot(EmptySnapshot.INSTANCE);
            this.lastRenderedSelectedRecipeIndex = -1;
        }
    }
}
