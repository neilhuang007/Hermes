package dev.hermes.module.impl.render;

import dev.hermes.event.EventTarget;
import dev.hermes.event.events.impl.render.EventRender2D;
import dev.hermes.manager.*;
import dev.hermes.module.Module;
import dev.hermes.module.api.Category;
import dev.hermes.module.api.ModuleInfo;
import dev.hermes.module.value.impl.BooleanValue;
import dev.hermes.module.value.impl.ListValue;
import dev.hermes.module.value.impl.NumberValue;
import dev.hermes.utils.projection.ProjectionUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@ModuleInfo(name = "ESP2D", category = Category.RENDER)
public class ESP2D extends Module {

    private final BooleanValue Range = new BooleanValue("Range", this, false);

    private final NumberValue DisplayRange = new NumberValue("DisplayRange", this, 32, 10, 64, 1, () -> Range.getValue());

    private final ListValue<Mode5> colorMode = new ListValue<>("Color Mode", this);

    private final BooleanValue TeamsColor = new BooleanValue("Teams Color", this, true);

    public final BooleanValue outline = new BooleanValue("Outline", this, true);
    public final ListValue<mode> boxMode = new ListValue<>("Mode", this, outline::getValue);

    private final NumberValue saturationValue = new NumberValue("Saturation", this,  1.0f, 0.0f, 1.0f, () -> colorMode.getValue() != Mode5.Custom && outline.getValue());
    private final NumberValue brightnessValue = new NumberValue("Brightness", this,  1.0f, 0.0f, 1.0f, () -> colorMode.getValue() != Mode5.Custom && outline.getValue());
    private final NumberValue mixerSecondsValue = new NumberValue("Seconds", this,  2, 1, 10, () -> colorMode.getValue() != Mode5.Custom && outline.getValue());
    public final BooleanValue outlineFont = new BooleanValue("OutlineFont", this,  true);
    public final BooleanValue healthBar = new BooleanValue("Health-bar", this,  true);
    public final ListValue<Mode2> hpBarMode = new ListValue<>("HBar-Mode", this, healthBar::getValue);
    public final BooleanValue healthNumber = new BooleanValue("Health-Number", this,  true, () -> healthBar.getValue());
    public final ListValue<Mode4> hpMode = new ListValue<>("HP-Mode", this, () -> healthNumber.getValue() && healthBar.getValue());
    public final BooleanValue hoverValue = new BooleanValue("Details-HoverOnly", this,  false);
    public final BooleanValue itemTagsValue = new BooleanValue("ItemTags", this,  true);
    public final BooleanValue itemValue = new BooleanValue("Item", this,  true, itemTagsValue::getValue);
    public final BooleanValue tagsValue = new BooleanValue("Tags", this,  true);
    public final BooleanValue tagsBGValue = new BooleanValue("Tags-Background", this,  true, () -> tagsValue.getValue() || itemTagsValue.getValue());
//    public final BooleanValue clearNameValue = new BooleanValue("Use-Clear-Name", this,  false, tagsValue::getValue);
    public final BooleanValue armorBar = new BooleanValue("ArmorBar", this,  true);
    public final BooleanValue armorItems = new BooleanValue("ArmorItems", this,  true);
    public final BooleanValue armorDur = new BooleanValue("ArmorDur", this,  true, armorItems::getValue);
    public final BooleanValue friendColor = new BooleanValue("FriendColor", this,  true);
    public final BooleanValue localPlayer = new BooleanValue("Local-Player", this,  true);
    public final BooleanValue mobs = new BooleanValue("Mobs", this, false);
    public final BooleanValue animals = new BooleanValue("Animals", this,  false);
    public final BooleanValue droppedItems = new BooleanValue("Dropped-Items", this,  false);
    private final NumberValue fontScaleValue = new NumberValue("Font-Scale", this, 0.5f, 0.0f, 1.0f,0.1);

    private IntBuffer viewport;
    private FloatBuffer modelview;
    private FloatBuffer projection;
    private FloatBuffer vector;
    private int backgroundColor;
    private int black;
    private final DecimalFormat dFormat = new DecimalFormat("0.0");

    public static final ArrayList collectedEntities = new ArrayList();
    private static final Pattern COLOR_PATTERN = Pattern.compile("(?i)\u00a7[0-9A-FK-OR]");
    private static final int[] DISPLAY_LISTS_2D = new int[4];
    private static final Frustum frustrum = new Frustum();


    public ESP2D(){
        // basic setup
        viewport = GLAllocation.createDirectIntBuffer(16);
        modelview = GLAllocation.createDirectFloatBuffer(16);
        projection = GLAllocation.createDirectFloatBuffer(16);
        vector = GLAllocation.createDirectFloatBuffer(4);
        backgroundColor = new Color(0, 0, 0, 120).getRGB();
        black = Color.BLACK.getRGB();
        // now append the modes or we can set it as list
        for (Mode5 mode5 : Mode5.values()) {
            colorMode.add(mode5);
        }
        colorMode.setDefault(Mode5.Custom);
        // now append the modes or we can set it as list
        for (Mode4 mode4 : Mode4.values()) {
            hpMode.add(mode4);
        }
        hpMode.setDefault(Mode4.Health);
        // now append the modes or we can set it as list
        for (Mode2 mode2 : Mode2.values()) {
            hpBarMode.add(mode2);
        }
        hpBarMode.setDefault(Mode2.Dot);
        // now append the modes or we can set it as list
        for (mode mode : mode.values()) {
            boxMode.add(mode);
        }
        boxMode.setDefault(mode.Box);

    }



    @Override
    public void onDisable() {
        collectedEntities.clear();
    }

    @EventTarget
    public void onRender2D(EventRender2D event) {
        GL11.glPushMatrix();
        this.collectEntities();
        float partialTicks = event.getPartialTicks();
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        int scaleFactor = scaledResolution.getScaleFactor();
        double scaling = (double)scaleFactor / Math.pow(scaleFactor, 2.0);
        GL11.glScaled(scaling, scaling, scaling);
        int black = this.black;
        net.minecraft.client.renderer.entity.RenderManager renderMng = mc.getRenderManager();
        EntityRenderer entityRenderer = mc.entityRenderer;
        boolean outline = this.outline.getValue();
        boolean health = this.healthBar.getValue();
        int collectedEntitiesSize = collectedEntities.size();
        for (Object collectedEntity : collectedEntities) {
            int m;
            boolean living;
            Entity entity = (Entity) collectedEntity;
            int color = this.getColor(entity).getRGB();
            if (!isInViewFrustrum(entity)) continue;
            Vector4d position = ProjectionUtil.get(entity);
            entityRenderer.setupCameraTransform(partialTicks, 0);
            if (position == null) continue;
            double posX = position.x;
            double posY = position.y;
            double endPosX = position.z;
            double endPosY = position.w;
            entityRenderer.setupOverlayRendering();
            String entityName = entity.getName(); // Assuming 'entity' is the entity you're working with
            if (this.boxMode.getValue() == mode.Box) {
                newDrawRect(entityName + "_box_top", posX - 1.0, posY, posX + 0.5, endPosY + 0.5, black);
                newDrawRect(entityName + "_box_left", posX - 1.0, posY - 0.5, endPosX + 0.5, posY + 0.5 + 0.5, black);
                newDrawRect(entityName + "_box_right", endPosX - 0.5 - 0.5, posY, endPosX + 0.5, endPosY + 0.5, black);
                newDrawRect(entityName + "_box_bottom", posX - 1.0, endPosY - 0.5 - 0.5, endPosX + 0.5, endPosY + 0.5, black);
                newDrawRect(entityName + "_box_inner_left", posX - 0.5, posY, posX + 0.5 - 0.5, endPosY, color);
                newDrawRect(entityName + "_box_inner_bottom", posX, endPosY - 0.5, endPosX, endPosY, color);
                newDrawRect(entityName + "_box_inner_top", posX - 0.5, posY, endPosX, posY + 0.5, color);
                newDrawRect(entityName + "_box_inner_right", endPosX - 0.5, posY, endPosX, endPosY, color);
            } else {
                newDrawRect(entityName + "_gradient_top_left", posX + 0.5, posY, posX - 1.0, posY + (endPosY - posY) / 4.0 + 0.5, black);
                newDrawRect(entityName + "_gradient_bottom_left", posX - 1.0, endPosY, posX + 0.5, endPosY - (endPosY - posY) / 4.0 - 0.5, black);
                newDrawRect(entityName + "_gradient_top_middle", posX - 1.0, posY - 0.5, posX + (endPosX - posX) / 3.0 + 0.5, posY + 1.0, black);
                newDrawRect(entityName + "_gradient_top_right", endPosX - (endPosX - posX) / 3.0 - 0.5, posY - 0.5, endPosX, posY + 1.0, black);
                newDrawRect(entityName + "_gradient_top_left", endPosX - 1.0, posY, endPosX + 0.5, posY + (endPosY - posY) / 4.0 + 0.5, black);
                newDrawRect(entityName + "_gradient_bottom_left", endPosX - 1.0, endPosY, endPosX + 0.5, endPosY - (endPosY - posY) / 4.0 - 0.5, black);
                newDrawRect(entityName + "_gradient_bottom_middle", posX - 1.0, endPosY - 1.0, posX + (endPosX - posX) / 3.0 + 0.5, endPosY + 0.5, black);
                newDrawRect(entityName + "_gradient_bottom_right", endPosX - (endPosX - posX) / 3.0 - 0.5, endPosY - 1.0, endPosX + 0.5, endPosY + 0.5, black);
                newDrawRect(entityName + "_gradient_inner_top_left", posX, posY, posX - 0.5, posY + (endPosY - posY) / 4.0, color);
                newDrawRect(entityName + "_gradient_inner_bottom_left", posX, endPosY, posX - 0.5, endPosY - (endPosY - posY) / 4.0, color);
                newDrawRect(entityName + "_gradient_inner_top_middle", posX - 0.5, posY, posX + (endPosX - posX) / 3.0, posY + 0.5, color);
                newDrawRect(entityName + "_gradient_inner_top_right", endPosX - (endPosX - posX) / 3.0, posY, endPosX, posY + 0.5, color);
                newDrawRect(entityName + "_gradient_inner_top_left", endPosX - 0.5, posY, endPosX, posY + (endPosY - posY) / 4.0, color);
                newDrawRect(entityName + "_gradient_inner_bottom_left", endPosX - 0.5, endPosY, endPosX, endPosY - (endPosY - posY) / 4.0, color);
                newDrawRect(entityName + "_gradient_inner_bottom_middle", posX, endPosY - 0.5, posX + (endPosX - posX) / 3.0, endPosY, color);
                newDrawRect(entityName + "_gradient_inner_bottom_right", endPosX - (endPosX - posX) / 3.0, endPosY - 0.5, endPosX - 0.5, endPosY, color);
            }
            if (!(living = entity instanceof EntityLivingBase)) continue;
            EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
            if (health) {
                float EntityHealth = entityLivingBase.getHealth();
                float MaxHealth = entityLivingBase.getMaxHealth();

                String healthDisplay = this.dFormat.format(entityLivingBase.getHealth() + entityLivingBase.getAbsorptionAmount()) + " \u00a7c\u2764";
                String healthPercent = (int) (entityLivingBase.getHealth() / MaxHealth * 100.0f) + "%";

                double HealthRatio = EntityHealth / MaxHealth;
                double textWidth = (endPosY - posY) * HealthRatio;

                if (this.healthNumber.getValue() && (!this.hoverValue.getValue() || entity == mc.thePlayer || this.isHovering(posX, endPosX, posY, endPosY, scaledResolution))) {
                    this.drawScaledString(this.hpMode.getValue() == Mode4.Health ? healthDisplay : healthPercent, posX - 4.0 - (double) ((float) TextManager.getStringWidth(this.hpMode.getValue() == Mode4.Health ? healthDisplay : healthPercent) * this.fontScaleValue.getValue().doubleValue()), endPosY - textWidth - (double) ((float) mc.fontRendererObj.FONT_HEIGHT / 2.0f * this.fontScaleValue.getValue().doubleValue()), this.fontScaleValue.getValue().doubleValue());
                }

                newDrawRect(entityName + "_health",posX - 3.5, posY - 0.5, posX - 1.5, endPosY + 0.5, this.backgroundColor);


                if (EntityHealth > 0.0f) {
                    double totalheight = (endPosY - posY) * HealthRatio;
                    Color healthColor = getHealthColor(EntityHealth, MaxHealth);
//                    RenderManager.rectangle(entityName + "_health_total",posX - 3.5, posY -0.5, 1.5, (endPosY - posY) * HealthRatio, healthColor);
                    double gap = 0.5; // Define the size of the gap between each health bar
                    double barheight = (endPosY - posY - gap*EntityHealth) / 20.0; // Define the height of each health bar
                    double yvalue = endPosY;
                    if(EntityHealth > 40){
                        // prevent lagging out because of too many rectangles
                        RenderManager.rectangle(entityName + "_health_total",posX - 3.5, posY -0.5, 1.5, (endPosY - posY) * HealthRatio, healthColor);
                    }else{
                        for (int i = 0; i < EntityHealth; i++) {
                            RenderManager.rectangle(entityName + "_health_" + i, posX - 1.5, yvalue - barheight, 1, barheight, healthColor);
                            yvalue -= barheight + gap;
                        }
                    }
                }
            }
//            if (this.tagsValue.getValue()) {
//                String entName;
//                String string = entName = this.clearNameValue.getValue() ? entityLivingBase.getName() : entityLivingBase.getDisplayName().getFormattedText();
//                if (this.friendColor.getValue() && TeamsManager.getTeams().containsKey(entityLivingBase.getName())) {
//                    entName = "\u00a7b" + entName;
//                }
//                if (this.tagsBGValue.getValue()) {
//                    newDrawRect(entityName+"_tags_bg",posX + (endPosX - posX) / 2.0 - (double) (((float) mc.fontRendererObj.getStringWidth(entName) / 2.0f + 2.0f) * this.fontScaleValue.getValue().doubleValue()), posY - 1.0 - (double) (((float) mc.fontRendererObj.FONT_HEIGHT + 2.0f) * this.fontScaleValue.getValue().doubleValue()), posX + (endPosX - posX) / 2.0 + (double) (((float) mc.fontRendererObj.getStringWidth(entName) / 2.0f + 2.0f) * this.fontScaleValue.getValue().doubleValue()), posY - 1.0 + (double) (2.0f * this.fontScaleValue.getValue().doubleValue()), -1610612736);
//                }
//                this.drawScaledCenteredString(entName, posX + (endPosX - posX) / 2.0, posY - 1.0 - (double) ((float) mc.fontRendererObj.FONT_HEIGHT * this.fontScaleValue.getValue().doubleValue()), this.fontScaleValue.getValue().doubleValue());
//            }
            if (this.armorBar.getValue() && entity instanceof EntityPlayer) {
                double constHeight = (endPosY - posY) / 4.0;
                for (m = 4; m > 0; --m) {
                    String peice = "helmet";
                    ItemStack armorStack = entityLivingBase.getCurrentArmor(m-1);
                    if (m == 3) {
                        peice = "chestplate";
                    }
                    if (m == 2) {
                        peice = "leggings";
                    }
                    if (m == 1) {
                        peice = "boots";
                    }
                    double theHeight = constHeight + 0.25;
                    if(armorStack != null){
                        newDrawRect(entityName+"_armor_" + peice, endPosX + 1.5, endPosY + 0.5 - theHeight * (double) m, endPosX + 3.5, endPosY + 0.5 - theHeight * (double) (m - 1), new Color(0, 0, 0, 120).getRGB());
                        newDrawRect(entityName+"_armor_" + peice + "_durability", endPosX + 2.0, endPosY + 0.5 - theHeight * (double) (m) - 0.25, endPosX + 3.0, endPosY + 0.5 - theHeight * (double) (m) - 0.25 - (constHeight - 0.25) * MathManager.clamp((double) InvManager.getItemDurability(armorStack) / (double) armorStack.getMaxDamage(), 0.0, 1.0), new Color(0, 255, 255).getRGB());
                        newDrawRect(entityName+"_armor_" + peice, endPosX + 1.5, endPosY + 0.5 - theHeight * (double) m, endPosX + 3.5, endPosY + 0.5 - theHeight * (double) (m - 1), new Color(0, 0, 0, 120).getRGB());
                    }
                }
            }
            if (this.armorItems.getValue() && (!this.hoverValue.getValue() || entity == mc.thePlayer || this.isHovering(posX, endPosX, posY, endPosY, scaledResolution))) {
                double yDist = (endPosY - posY) / 4.0;
                for (m = 4; m > 0; --m) {
                    ItemStack armorStack = entityLivingBase.getCurrentArmor(m-1);
                    if(armorStack != null){
                        this.renderItemStack(armorStack, endPosX + (this.armorBar.getValue() ? 10.0 : 2.0), posY + yDist * (double) (4 - m) + yDist / 2.0 - 5.0);
                        if (!this.armorDur.getValue()) continue;
                        this.drawScaledCenteredString(String.valueOf(InvManager.getItemDurability(armorStack)), endPosX + (this.armorBar.getValue() ? 10.0 : 2.0) + 4.5, posY + yDist * (double) (4 - m) + yDist / 2.0 + 4.0, this.fontScaleValue.getValue().doubleValue(), -1);
                    }
                }
            }
            if (!this.itemTagsValue.getValue()) continue;
            if (!this.itemValue.getValue()) {
                String itemName = entityLivingBase.getHeldItem().getDisplayName();
                if (this.tagsBGValue.getValue()) {

                    newDrawRect(entityName+"_MainHand" ,posX + (endPosX - posX) / 2.0 - (double) (((float) mc.fontRendererObj.getStringWidth(itemName) / 2.0f + 2.0f) * this.fontScaleValue.getValue().doubleValue()), endPosY + 1.0 - (double) (2.0f * this.fontScaleValue.getValue().doubleValue()), posX + (endPosX - posX) / 2.0 + (double) (((float) mc.fontRendererObj.getStringWidth(itemName) / 2.0f + 2.0f) * this.fontScaleValue.getValue().doubleValue()), endPosY + 1.0 + (double) (((float) mc.fontRendererObj.FONT_HEIGHT + 2.0f) * this.fontScaleValue.getValue().doubleValue()), -1610612736);
                }
                this.drawScaledCenteredString(itemName, posX + (endPosX - posX) / 2.0, endPosY + 1.0, this.fontScaleValue.getValue().doubleValue(), -1);
                continue;
            }
            this.renderItemStack(entityLivingBase.getHeldItem(), posX, endPosY);
        }
        GL11.glPopMatrix();
        GlStateManager.enableBlend();
        GlStateManager.resetColor();
        entityRenderer.setupOverlayRendering();
    }


    public static Color getHealthColor(float health, float maxHealth) {
        float[] fractions = new float[]{0.0f, 0.5f, 1.0f};
        Color[] colors = new Color[]{new Color(108, 0, 0), new Color(255, 51, 0), Color.GREEN};
        float progress = health / maxHealth;
        return blendColors(fractions, colors, progress).brighter();
    }

    public static Color blendColors(float[] fractions, Color[] colors, float progress) {
        if (fractions.length == colors.length) {
            int[] indices = getFractionIndices(fractions, progress);
            float[] range = new float[]{fractions[indices[0]], fractions[indices[1]]};
            Color[] colorRange = new Color[]{colors[indices[0]], colors[indices[1]]};
            float max = range[1] - range[0];
            float value = progress - range[0];
            float weight = value / max;
            return blend(colorRange[0], colorRange[1], 1.0f - weight);
        }
        throw new IllegalArgumentException("Fractions and colours must have equal number of elements");
    }

    public static Color blend(Color color1, Color color2, double ratio) {
        float r = (float)ratio;
        float ir = 1.0f - r;
        float[] rgb1 = color1.getColorComponents(new float[3]);
        float[] rgb2 = color2.getColorComponents(new float[3]);
        float red = rgb1[0] * r + rgb2[0] * ir;
        float green = rgb1[1] * r + rgb2[1] * ir;
        float blue = rgb1[2] * r + rgb2[2] * ir;
        if (red < 0.0f) {
            red = 0.0f;
        } else if (red > 255.0f) {
            red = 255.0f;
        }
        if (green < 0.0f) {
            green = 0.0f;
        } else if (green > 255.0f) {
            green = 255.0f;
        }
        if (blue < 0.0f) {
            blue = 0.0f;
        } else if (blue > 255.0f) {
            blue = 255.0f;
        }
        Color color3 = null;
        try {
            color3 = new Color(red, green, blue);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            // empty catch block
        }
        return color3;
    }

    public static int[] getFractionIndices(float[] fractions, float progress) {
        int startPoint;
        int[] range = new int[2];
        for (startPoint = 0; startPoint < fractions.length && fractions[startPoint] <= progress; ++startPoint) {
        }
        if (startPoint >= fractions.length) {
            startPoint = fractions.length - 1;
        }
        range[0] = startPoint - 1;
        range[1] = startPoint;
        return range;
    }

    public static String stripColor(String input) {
        return COLOR_PATTERN.matcher(input).replaceAll("");
    }

    public static Color fade(Color color, int index, int count) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
        float brightness = Math.abs(((float)(System.currentTimeMillis() % 2000L) / 1000.0f + (float)index / (float)count * 2.0f) % 2.0f - 1.0f);
        brightness = 0.5f + 0.5f * brightness;
        hsb[2] = brightness % 2.0f;
        return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
    }

    public static Color slowlyRainbow(long time, int count, float qd, float sq) {
        Color color = new Color(Color.HSBtoRGB(((float)time + (float)count * -3000000.0f) / 2.0f / 1.0E9f, qd, sq));
        return new Color((float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, (float)color.getAlpha() / 255.0f);
    }

    public static void setColor(Color color) {
        float alpha = (float)(color.getRGB() >> 24 & 0xFF) / 255.0f;
        float red = (float)(color.getRGB() >> 16 & 0xFF) / 255.0f;
        float green = (float)(color.getRGB() >> 8 & 0xFF) / 255.0f;
        float blue = (float)(color.getRGB() & 0xFF) / 255.0f;
        GL11.glColor4f(red, green, blue, alpha);
    }

    public static int getRainbowOpaque(int seconds, float saturation, float brightness, int index) {
        float hue = (float)((System.currentTimeMillis() + (long)index) % (long)(seconds * 1000L)) / (float)(seconds * 1000);
        return Color.HSBtoRGB(hue, saturation, brightness);
    }

    public static double interpolate(double current, double old, double scale) {
        return old + (current - old) * scale;
    }

    public static boolean isInViewFrustrum(Entity entity) {
        return isInViewFrustrum(entity.getEntityBoundingBox()) || entity.ignoreFrustumCheck;
    }

    private static boolean isInViewFrustrum(AxisAlignedBB bb) {
        Entity current = mc.getRenderViewEntity();
        frustrum.setPosition(current.posX, current.posY, current.posZ);
        return frustrum.isBoundingBoxInFrustum(bb);
    }

    public static void drawRect(String id, double x, double y, double x2, double y2, int color) {
        float alpha = (float)(color >> 24 & 0xFF) / 255.0f;
        float red = (float)(color >> 16 & 0xFF) / 255.0f;
        float green = (float)(color >> 8 & 0xFF) / 255.0f;
        float blue = (float)(color & 0xFF) / 255.0f;
        RenderManager.rectangle(id, x, y, Math.abs(x-x2), Math.abs(y-y2),new Color(red,green,blue,alpha));
    }

    public static void newDrawRect(String id, double left, double top, double right, double bottom, int hex) {
        float alpha = (float)(hex >> 24 & 0xFF) / 255.0f;
        float red = (float)(hex >> 16 & 0xFF) / 255.0f;
        float green = (float)(hex >> 8 & 0xFF) / 255.0f;
        float blue = (float)(hex & 0xFF) / 255.0f;
        RenderManager.rectangle(id, left, top, Math.abs(right - left), Math.abs(bottom - top), new Color(red, green, blue, alpha));
    }
    public static void color(Color color) {
        if (color == null) {
            color = Color.white;
        }
        color((float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, (float)color.getAlpha() / 255.0f);
    }

    public static void color(double red, double green, double blue, double alpha) {
        GL11.glColor4d(red, green, blue, alpha);
    }

    public static void glColor(int hex) {
        float alpha = (float)(hex >> 24 & 0xFF) / 255.0f;
        float red = (float)(hex >> 16 & 0xFF) / 255.0f;
        float green = (float)(hex >> 8 & 0xFF) / 255.0f;
        float blue = (float)(hex & 0xFF) / 255.0f;
        GlStateManager.color(red, green, blue, alpha);
    }

    public static void render(int mode2, Runnable render) {
        GL11.glBegin(mode2);
        render.run();
        GL11.glEnd();
    }

    public Color getColor(Entity entity) {
        if (entity instanceof EntityPlayer && TeamsColor.getValue() && TeamsManager.getTeams().containsKey(entity.getName())) {
            return Color.cyan;
        }
        switch (colorMode.getValue()) {
            case Custom: {
                return new Color(200, 200, 200);
            }
            case AnotherRainbow: {
                return new Color(getRainbowOpaque(mixerSecondsValue.getValue().intValue(), saturationValue.getValue().floatValue(), brightnessValue.getValue().floatValue(), 0));
            }
            case Slowly: {
                return slowlyRainbow(System.nanoTime(), 0, saturationValue.getValue().floatValue(), brightnessValue.getValue().floatValue());
            }
        }
        return fade(new Color(200, 200, 200), 0, 100);
    }

    private void drawScaledCenteredString(String text2, double x, double y, double scale, int color) {
        this.drawScaledString(text2, x - (double)((float)mc.fontRendererObj.getStringWidth(text2) / 2.0f) * scale, y, scale, color);
    }

    private void drawScaledString(String text2, double x, double y, double scale, int color) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, x);
        GlStateManager.scale(scale, scale, scale);
        if (this.outlineFont.getValue()) {
            this.drawOutlineStringWithoutGL(text2, 0.0f, 0.0f, color, mc.fontRendererObj);
        } else {
            mc.fontRendererObj.drawStringWithShadow(text2, 0.0f, 0.0f, color);
        }
        GlStateManager.popMatrix();
    }

    private void renderItemStack(ItemStack stack, double x, double y) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, x);
        GlStateManager.scale(0.5, 0.5, 0.5);
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderHelper.enableGUIStandardItemLighting();
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, 0, 0);
        mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, stack, 0, 0);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private boolean isHovering(double minX, double maxX, double minY, double maxY, ScaledResolution sc) {
        return (double)sc.getScaledWidth() / 2.0 >= minX && (double)sc.getScaledWidth() / 2.0 < maxX && (double)sc.getScaledHeight() / 2.0 >= minY && (double)sc.getScaledHeight() / 2.0 < maxY;
    }

    public void drawOutlineStringWithoutGL(String s, float x, float y, int color, FontRenderer fontRenderer) {
        fontRenderer.drawString(stripColor(s), (int)(x * 2.0f - 1.0f), (int)(y * 2.0f), Color.BLACK.getRGB());
        fontRenderer.drawString(stripColor(s), (int)(x * 2.0f + 1.0f), (int)(y * 2.0f), Color.BLACK.getRGB());
        fontRenderer.drawString(stripColor(s), (int)(x * 2.0f), (int)(y * 2.0f - 1.0f), Color.BLACK.getRGB());
        fontRenderer.drawString(stripColor(s), (int)(x * 2.0f), (int)(y * 2.0f + 1.0f), Color.BLACK.getRGB());
        fontRenderer.drawString(s, (int)(x * 2.0f), (int)(y * 2.0f), color);
    }

    private void drawScaledString(String text2, double x, double y, double scale) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, x);
        GlStateManager.scale(scale, scale, scale);
        if (this.outlineFont.getValue()) {
            this.drawOutlineStringWithoutGL(text2, 0.0f, 0.0f, -1, mc.fontRendererObj);
        } else {
            mc.fontRendererObj.drawStringWithShadow(text2, 0.0f, 0.0f, -1);
        }
        GlStateManager.popMatrix();
    }

    private void drawScaledCenteredString(String text2, double x, double y, double scale) {
        this.drawScaledString(text2, x - (double)((float)mc.fontRendererObj.getStringWidth(text2) / 2.0f) * scale, y, scale);
    }

    private void collectEntities() {
        collectedEntities.clear();
        List EntitiesList = mc.theWorld.loadedEntityList;
        for (Object playerEntity : EntitiesList) {
            Entity entity = (Entity) playerEntity;
            if(Range.getValue() && entity.getDistanceSqToEntity(mc.thePlayer) > DisplayRange.getValue().doubleValue()) continue;
            if (!this.isSelected(entity, false) && (!this.localPlayer.getValue() || !(entity instanceof EntityPlayerSP) || mc.gameSettings.thirdPersonView == 0) && (!this.droppedItems.getValue() || !(entity instanceof EntityItem)))
                continue;
            collectedEntities.add(entity);
        }
    }

    public boolean isSelected(Entity entity, boolean canAttackCheck) {
        if (entity instanceof EntityLivingBase && entity.isEntityAlive() && entity != mc.thePlayer) {
            if (entity instanceof EntityPlayer) {
                if (canAttackCheck) {
                    if(!isInViewFrustrum(entity)){
                        return false;
                    }
                    if (TeamsManager.teams.containsKey((EntityPlayer)entity)) {
                        return false;
                    }
                    if (((EntityPlayer)entity).isSpectator()) {
                        return false;
                    }
                    return !((EntityPlayer)entity).isPlayerSleeping();
                }
                return true;
            }
            return this.isMob(entity) && this.mobs.getValue() || this.isAnimal(entity) && this.animals.getValue();
        }
        return false;
    }

    public boolean isAnimal(Entity entity) {
        return entity instanceof EntityAnimal || entity instanceof EntitySquid || entity instanceof EntityGolem || entity instanceof EntityVillager || entity instanceof EntityBat;
    }

    public boolean isMob(Entity entity) {
        return entity instanceof EntityMob || entity instanceof EntitySlime || entity instanceof EntityGhast || entity instanceof EntityDragon;
    }

    private Vector3d project2D(int scaleFactor, double x, double y, double z) {
        GL11.glGetFloat(2982, this.modelview);
        GL11.glGetFloat(2983, this.projection);
        GL11.glGetInteger(2978, this.viewport);
        return GLU.gluProject((float)x, (float)y, (float)z, this.modelview, this.projection, this.viewport, this.vector) ? new Vector3d(this.vector.get(0) / (float)scaleFactor, ((float) Display.getHeight() - this.vector.get(1)) / (float)scaleFactor, this.vector.get(2)) : null;
    }

//    static {
//        for (int i = 0; i < DISPLAY_LISTS_2D.length; ++i) {
//            DISPLAY_LISTS_2D[i] = GL11.glGenLists(1);
//        }
//        GL11.glNewList(DISPLAY_LISTS_2D[0], 4864);
//        quickDrawRect(-7.0f, 2.0f, -4.0f, 3.0f);
//        quickDrawRect(4.0f, 2.0f, 7.0f, 3.0f);
//        quickDrawRect(-7.0f, 0.5f, -6.0f, 3.0f);
//        quickDrawRect(6.0f, 0.5f, 7.0f, 3.0f);
//        GL11.glEndList();
//        GL11.glNewList(DISPLAY_LISTS_2D[1], 4864);
//        quickDrawRect(-7.0f, 3.0f, -4.0f, 3.3f);
//        quickDrawRect(4.0f, 3.0f, 7.0f, 3.3f);
//        quickDrawRect(-7.3f, 0.5f, -7.0f, 3.3f);
//        quickDrawRect(7.0f, 0.5f, 7.3f, 3.3f);
//        GL11.glEndList();
//        GL11.glNewList(DISPLAY_LISTS_2D[2], 4864);
//        quickDrawRect(4.0f, -20.0f, 7.0f, -19.0f);
//        quickDrawRect(-7.0f, -20.0f, -4.0f, -19.0f);
//        quickDrawRect(6.0f, -20.0f, 7.0f, -17.5f);
//        quickDrawRect(-7.0f, -20.0f, -6.0f, -17.5f);
//        GL11.glEndList();
//        GL11.glNewList(DISPLAY_LISTS_2D[3], 4864);
//        quickDrawRect(7.0f, -20.0f, 7.3f, -17.5f);
//        quickDrawRect(-7.3f, -20.0f, -7.0f, -17.5f);
//        quickDrawRect(4.0f, -20.3f, 7.3f, -20.0f);
//        quickDrawRect(-7.3f, -20.3f, -4.0f, -20.0f);
//        GL11.glEndList();
//    }

    public static enum Mode5 {
        Custom,
        Slowly,
        AnotherRainbow

    }

    public static enum Mode4 {
        Health,
        Percent

    }

    public static enum Mode2 {
        Dot,
        Line

    }

    public static enum mode {
        Corners,
        Box

    }



}
