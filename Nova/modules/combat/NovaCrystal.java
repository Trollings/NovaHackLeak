/*      */ package Nova.modules.combat;
/*      */ import Nova.util.CrystalUtils;
/*      */ import Nova.util.NDamageUtils;
/*      */ import Nova.util.TimerUtils;
/*      */ import com.google.common.util.concurrent.AtomicDouble;
/*      */ import it.unimi.dsi.fastutil.ints.Int2IntMap;
/*      */ import it.unimi.dsi.fastutil.ints.IntIterator;
/*      */ import it.unimi.dsi.fastutil.ints.IntSet;
/*      */ import java.util.ArrayList;
/*      */ import java.util.List;
/*      */ import java.util.Objects;
/*      */ import java.util.concurrent.atomic.AtomicBoolean;
/*      */ import java.util.concurrent.atomic.AtomicReference;
/*      */ import meteordevelopment.meteorclient.events.entity.EntityAddedEvent;
/*      */ import meteordevelopment.meteorclient.events.entity.EntityRemovedEvent;
/*      */ import meteordevelopment.meteorclient.events.packets.PacketEvent;
/*      */ import meteordevelopment.meteorclient.events.render.Render3DEvent;
/*      */ import meteordevelopment.meteorclient.events.world.TickEvent;
/*      */ import meteordevelopment.meteorclient.mixininterface.IRaycastContext;
/*      */ import meteordevelopment.meteorclient.mixininterface.IVec3d;
/*      */ import meteordevelopment.meteorclient.renderer.ShapeMode;
/*      */ import meteordevelopment.meteorclient.renderer.text.TextRenderer;
/*      */ import meteordevelopment.meteorclient.settings.BoolSetting;
/*      */ import meteordevelopment.meteorclient.settings.ColorSetting;
/*      */ import meteordevelopment.meteorclient.settings.DoubleSetting;
/*      */ import meteordevelopment.meteorclient.settings.EnumSetting;
/*      */ import meteordevelopment.meteorclient.settings.IntSetting;
/*      */ import meteordevelopment.meteorclient.settings.KeybindSetting;
/*      */ import meteordevelopment.meteorclient.settings.Setting;
/*      */ import meteordevelopment.meteorclient.settings.SettingGroup;
/*      */ import meteordevelopment.meteorclient.utils.entity.EntityUtils;
/*      */ import meteordevelopment.meteorclient.utils.misc.Keybind;
/*      */ import meteordevelopment.meteorclient.utils.misc.Pool;
/*      */ import meteordevelopment.meteorclient.utils.misc.Vec3;
/*      */ import meteordevelopment.meteorclient.utils.player.FindItemResult;
/*      */ import meteordevelopment.meteorclient.utils.player.InvUtils;
/*      */ import meteordevelopment.meteorclient.utils.player.Rotations;
/*      */ import meteordevelopment.meteorclient.utils.render.NametagUtils;
/*      */ import meteordevelopment.meteorclient.utils.render.color.Color;
/*      */ import meteordevelopment.meteorclient.utils.render.color.SettingColor;
/*      */ import meteordevelopment.orbit.EventHandler;
/*      */ import net.minecraft.class_1268;
/*      */ import net.minecraft.class_1293;
/*      */ import net.minecraft.class_1297;
/*      */ import net.minecraft.class_1657;
/*      */ import net.minecraft.class_1792;
/*      */ import net.minecraft.class_1799;
/*      */ import net.minecraft.class_1802;
/*      */ import net.minecraft.class_1832;
/*      */ import net.minecraft.class_2246;
/*      */ import net.minecraft.class_2338;
/*      */ import net.minecraft.class_2350;
/*      */ import net.minecraft.class_238;
/*      */ import net.minecraft.class_2382;
/*      */ import net.minecraft.class_243;
/*      */ import net.minecraft.class_2596;
/*      */ import net.minecraft.class_2663;
/*      */ import net.minecraft.class_2680;
/*      */ import net.minecraft.class_3959;
/*      */ import net.minecraft.class_3965;
/*      */ 
/*      */ public class NovaCrystal extends Module {
/*      */   public enum YawStepMode {
/*   64 */     Break,
/*   65 */     All;
/*      */   }
/*      */   
/*      */   public enum AutoSwitchMode {
/*   69 */     Normal,
/*   70 */     Silent,
/*   71 */     None;
/*      */   }
/*      */   
/*      */   public enum SupportMode {
/*   75 */     Disabled,
/*   76 */     Accurate,
/*   77 */     Fast;
/*      */   }
/*      */   
/*      */   public enum CancelCrystalMode {
/*   81 */     Hit,
/*   82 */     NoDesync;
/*      */   }
/*      */   
/*      */   public enum DamageIgnore {
/*   86 */     Always,
/*   87 */     WhileSafe,
/*   88 */     Never;
/*      */   }
/*      */   
/*      */   public enum SlowMode {
/*   92 */     Delay,
/*   93 */     Age,
/*   94 */     Both;
/*      */   }
/*      */   
/*      */   public enum SelfPopIgnore {
/*   98 */     Place,
/*   99 */     Break,
/*  100 */     Both;
/*      */   }
/*      */   
/*      */   public enum PopPause {
/*  104 */     Place,
/*  105 */     Break,
/*  106 */     Both;
/*      */   }
/*      */   
/*      */   public enum RenderMode {
/*  110 */     Normal,
/*  111 */     Fade,
/*  112 */     None;
/*      */   }
/*      */   
/*      */   public enum TrapType {
/*  116 */     BothTrapped,
/*  117 */     AnyTrapped,
/*  118 */     TopTrapped,
/*  119 */     FaceTrapped,
/*  120 */     Always;
/*      */   }
/*      */   
/*      */   public enum FacePlaceMode {
/*  124 */     Normal,
/*  125 */     Slow,
/*  126 */     None;
/*      */   }
/*      */   
/*  129 */   private final SettingGroup sgGeneral = this.settings.getDefaultGroup();
/*  130 */   private final SettingGroup sgPlace = this.settings.createGroup("Place");
/*  131 */   private final SettingGroup sgFacePlace = this.settings.createGroup("Face Place");
/*  132 */   private final SettingGroup sgSurround = this.settings.createGroup("Surround");
/*  133 */   private final SettingGroup sgBreak = this.settings.createGroup("Break");
/*  134 */   private final SettingGroup sgFastBreak = this.settings.createGroup("Fast Break");
/*  135 */   private final SettingGroup sgChainPop = this.settings.createGroup("Chain Pop");
/*  136 */   private final SettingGroup sgPause = this.settings.createGroup("Pause");
/*  137 */   private final SettingGroup sgRender = this.settings.createGroup("Render");
/*      */ 
/*      */ 
/*      */   
/*  141 */   public final Setting<Boolean> debug = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder())
/*  142 */       .name("debug-mode"))
/*  143 */       .description("Informs you what the CA is doing."))
/*  144 */       .defaultValue(Boolean.valueOf(false)))
/*  145 */       .build());
/*      */ 
/*      */   
/*  148 */   private final Setting<Double> targetRange = this.sgGeneral.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder())
/*  149 */       .name("target-range"))
/*  150 */       .description("Range in which to target players."))
/*  151 */       .defaultValue(10.0D)
/*  152 */       .min(0.0D)
/*  153 */       .sliderRange(0.0D, 20.0D)
/*  154 */       .build());
/*      */ 
/*      */   
/*  157 */   public final Setting<Double> explosionRadiusToTarget = this.sgGeneral.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder())
/*  158 */       .name("place-radius"))
/*  159 */       .description("How far crystals can be placed from the target."))
/*  160 */       .defaultValue(12.0D)
/*  161 */       .range(1.0D, 12.0D)
/*  162 */       .sliderRange(1.0D, 12.0D)
/*  163 */       .build());
/*      */ 
/*      */   
/*  166 */   private final Setting<Boolean> predictMovement = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder())
/*  167 */       .name("predict-movement"))
/*  168 */       .description("Predict the target's movement."))
/*  169 */       .defaultValue(Boolean.valueOf(false)))
/*  170 */       .build());
/*      */ 
/*      */   
/*  173 */   private final Setting<Boolean> ignoreTerrain = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder())
/*  174 */       .name("ignore-terrain"))
/*  175 */       .description("Ignore blocks if they can be blown up by crystals."))
/*  176 */       .defaultValue(Boolean.valueOf(true)))
/*  177 */       .build());
/*      */ 
/*      */   
/*  180 */   private final Setting<Boolean> fullBlocks = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder())
/*  181 */       .name("full-blocks"))
/*  182 */       .description("Treat anvils and ender chests as full blocks."))
/*  183 */       .defaultValue(Boolean.valueOf(false)))
/*  184 */       .build());
/*      */ 
/*      */   
/*  187 */   public final Setting<Boolean> hideSwings = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder())
/*  188 */       .name("hide-swings"))
/*  189 */       .description("Whether to send hand swing packets to the server."))
/*  190 */       .defaultValue(Boolean.valueOf(false)))
/*  191 */       .build());
/*      */ 
/*      */   
/*  194 */   private final Setting<AutoSwitchMode> autoSwitch = this.sgGeneral.add((Setting)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder())
/*  195 */       .name("auto-switch"))
/*  196 */       .description("Switches to crystals in your hotbar once a target is found."))
/*  197 */       .defaultValue(AutoSwitchMode.Normal))
/*  198 */       .build());
/*      */ 
/*      */   
/*  201 */   private final Setting<Boolean> noGapSwitch = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder())
/*  202 */       .name("No Gap Switch"))
/*  203 */       .description("Disables normal auto switch when you are holding a gap."))
/*  204 */       .defaultValue(Boolean.valueOf(true)))
/*  205 */       .visible(() -> (this.autoSwitch.get() == AutoSwitchMode.Normal)))
/*  206 */       .build());
/*      */ 
/*      */   
/*  209 */   private final Setting<Boolean> rotate = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder())
/*  210 */       .name("rotate"))
/*  211 */       .description("Rotates server-side towards the crystals being hit/placed."))
/*  212 */       .defaultValue(Boolean.valueOf(false)))
/*  213 */       .build());
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final Setting<YawStepMode> yawStepMode;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final Setting<Double> yawSteps;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Boolean> doPlace;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Double> PminDamage;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<DamageIgnore> PDamageIgnore;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Double> PmaxDamage;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Boolean> PantiSuicide;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Integer> placeDelay;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Double> placeRange;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Double> placeWallsRange;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Boolean> placement112;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Boolean> smallBox;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final Setting<SupportMode> support;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final Setting<Integer> supportDelay;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Boolean> facePlace;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Keybind> forceFacePlace;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Boolean> slowFacePlace;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<SlowMode> slowFPMode;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Integer> slowFPDelay;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Integer> slowFPAge;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Boolean> surrHoldPause;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Boolean> KAPause;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Boolean> CevPause;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Double> facePlaceHealth;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Double> facePlaceDurability;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Boolean> facePlaceArmor;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Boolean> burrowBreak;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Keybind> forceBurrowBreak;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Integer> burrowBreakDelay;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<TrapType> burrowBWhen;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Boolean> surroundBreak;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Keybind> forceSurroundBreak;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<TrapType> surroundBWhen;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Integer> surroundBreakDelay;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Boolean> surroundBHorse;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Boolean> surroundBDiagonal;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Boolean> surroundHold;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<TrapType> surroundHWhen;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<SlowMode> surroundHoldMode;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Integer> surroundHoldDelay;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Integer> surroundHoldAge;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final Setting<Boolean> doBreak;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Boolean> onlyBreakOwn;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Boolean> antiWeakness;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Double> BminDamage;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<DamageIgnore> BDamageIgnore;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Double> BmaxDamage;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Integer> attackFrequency;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Boolean> BantiSuicide;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<CancelCrystalMode> cancelCrystalMode;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Integer> cancelTicks;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Integer> breakDelay;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Boolean> smartDelay;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Integer> switchDelay;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Double> breakRange;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Double> breakWallsRange;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Boolean> attemptCheck;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Integer> breakAttempts;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Boolean> ageCheck;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Integer> ticksExisted;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Boolean> fastBreak;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Boolean> freqCheck;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Boolean> damageCheck;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Boolean> smartCheck;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Boolean> selfPopInvincibility;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Integer> selfPopInvincibilityTime;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<SelfPopIgnore> selfPopIgnore;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Boolean> targetPopInvincibility;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Integer> targetPopInvincibilityTime;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<PopPause> popPause;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Double> pauseAtHealth;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final Setting<Boolean> eatPause;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final Setting<Boolean> drinkPause;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final Setting<Boolean> minePause;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Setting<Boolean> renderSwing;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final Setting<RenderMode> renderMode;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final Setting<ShapeMode> shapeMode;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final Setting<Integer> fadeTime;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final Setting<Integer> fadeAmount;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final Setting<Integer> renderTime;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final Setting<SettingColor> placeSideColor;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final Setting<SettingColor> placeLineColor;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final Setting<Boolean> renderBreak;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final Setting<Integer> renderBreakTime;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final Setting<SettingColor> breakSideColor;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final Setting<SettingColor> breakLineColor;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final Setting<Boolean> renderDamage;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final Setting<Double> damageScale;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final Setting<SettingColor> damageColor;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int breakTimer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int placeTimer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int switchTimer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int ticksPassed;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final List<class_1657> targets;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final class_243 vec3d;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final class_243 playerEyePos;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final Vec3 vec3;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final class_2338.class_2339 blockPos;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final class_238 box;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final class_243 vec3dRayTraceEnd;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private class_3959 raycastContext;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final IntSet placedCrystals;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean placing;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int placingTimer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final class_2338.class_2339 placingCrystalBlockPos;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final IntSet removed;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Int2IntMap attemptedBreaks;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final Int2IntMap waitingToExplode;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int attacks;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private double serverYaw;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public class_1657 bestTarget;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private float bestTargetDamage;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int bestTargetTimer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean didRotateThisTick;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean isLastRotationPos;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final class_243 lastRotationPos;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private double lastYaw;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private double lastPitch;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int lastRotationTimer;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TimerUtils selfPoppedTimer;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TimerUtils targetPoppedTimer;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int renderTimer;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int breakRenderTimer;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final class_2338.class_2339 renderPos;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final class_2338.class_2339 breakRenderPos;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final Pool<RenderBlock> renderBlockPool;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final List<RenderBlock> renderBlocks;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final Pool<RenderBlock> renderBreakBlockPool;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final List<RenderBlock> renderBreakBlocks;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private double damage;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public NovaCrystal() {
/*  987 */     super(NovaAddon.nova, "Nova-Crystal", "Automatically places and attacks crystals."); Objects.requireNonNull(this.rotate); this.yawStepMode = this.sgGeneral.add((Setting)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("yaw-steps-mode")).description("When to run the yaw steps check.")).defaultValue(YawStepMode.Break)).visible(this.rotate::get)).build()); Objects.requireNonNull(this.rotate); this.yawSteps = this.sgGeneral.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("yaw-steps")).description("Maximum degrees to rotate in one tick.")).defaultValue(180.0D).range(1.0D, 180.0D).sliderRange(1.0D, 180.0D).visible(this.rotate::get)).build()); this.doPlace = this.sgPlace.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("place")).description("If the CA should place crystals.")).defaultValue(Boolean.valueOf(true))).build()); Objects.requireNonNull(this.doPlace); this.PminDamage = this.sgPlace.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("min-place-damage")).description("Minimum place damage the crystal needs to deal to your target.")).defaultValue(6.0D).min(0.0D).visible(this.doPlace::get)).build()); Objects.requireNonNull(this.doPlace); this.PDamageIgnore = this.sgPlace.add((Setting)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("ignore-self-damage")).description("Whether to ignore damage to yourself.")).defaultValue(DamageIgnore.Never)).visible(this.doPlace::get)).build()); this.PmaxDamage = this.sgPlace.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("max-place-damage")).description("Maximum place damage crystals can deal to yourself.")).defaultValue(6.0D).range(0.0D, 36.0D).sliderRange(0.0D, 36.0D).visible(() -> (this.PDamageIgnore.get() != DamageIgnore.Always && ((Boolean)this.doPlace.get()).booleanValue()))).build()); this.PantiSuicide = this.sgPlace.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anti-suicide-place")).description("Will not place crystals if they will pop / kill you.")).defaultValue(Boolean.valueOf(true))).visible(() -> (this.PDamageIgnore.get() != DamageIgnore.Always && ((Boolean)this.doPlace.get()).booleanValue()))).build()); Objects.requireNonNull(this.doPlace); this.placeDelay = this.sgPlace.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("place-delay")).description("The delay in ticks to wait to place a crystal after it's exploded.")).defaultValue(Integer.valueOf(0))).range(0, 20).sliderRange(0, 20).visible(this.doPlace::get)).build()); Objects.requireNonNull(this.doPlace); this.placeRange = this.sgPlace.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("place-range")).description("How far away you can place crystals.")).defaultValue(4.0D).range(0.0D, 6.0D).sliderRange(0.0D, 6.0D).visible(this.doPlace::get)).build()); Objects.requireNonNull(this.doPlace); this.placeWallsRange = this.sgPlace.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("place-walls-range")).description("How far away you can place crystals through blocks.")).defaultValue(4.0D).range(0.0D, 6.0D).sliderRange(0.0D, 6.0D).visible(this.doPlace::get)).build()); Objects.requireNonNull(this.doPlace); this.placement112 = this.sgPlace.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("1.12-placement")).description("Uses 1.12 crystal placement.")).defaultValue(Boolean.valueOf(false))).visible(this.doPlace::get)).build()); Objects.requireNonNull(this.doPlace); this.smallBox = this.sgPlace.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("small-box")).description("Allows you to place in 1x1x1 box instead of 1x2x1 boxes.")).defaultValue(Boolean.valueOf(false))).visible(this.doPlace::get)).build()); Objects.requireNonNull(this.doPlace); this.support = this.sgPlace.add((Setting)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("support")).description("Places a support block in air if no other position have been found.")).defaultValue(SupportMode.Disabled)).visible(this.doPlace::get)).build()); this.supportDelay = this.sgPlace.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("support-delay")).description("Delay in ticks after placing support block.")).defaultValue(Integer.valueOf(1))).min(0).visible(() -> (this.support.get() != SupportMode.Disabled && ((Boolean)this.doPlace.get()).booleanValue()))).build()); this.facePlace = this.sgFacePlace.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("face-place")).description("Will place crystals against the enemy's face.")).defaultValue(Boolean.valueOf(true))).build()); this.forceFacePlace = this.sgFacePlace.add((Setting)((KeybindSetting.Builder)((KeybindSetting.Builder)((KeybindSetting.Builder)(new KeybindSetting.Builder()).name("force-face-place")).description("Starts face place when this button is pressed.")).defaultValue(Keybind.none())).build()); Objects.requireNonNull(this.facePlace); this.slowFacePlace = this.sgFacePlace.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("slow-place")).description("Place slower while face placing to reserve crystals.")).defaultValue(Boolean.valueOf(false))).visible(this.facePlace::get)).build()); this.slowFPMode = this.sgFacePlace.add((Setting)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("slow-FP-mode")).description("How to measure the delay for slow face place.")).defaultValue(SlowMode.Delay)).visible(() -> (((Boolean)this.facePlace.get()).booleanValue() && ((Boolean)this.slowFacePlace.get()).booleanValue()))).build()); this.slowFPDelay = this.sgFacePlace.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("slow-FP-delay")).description("How long in ticks to wait to break a crystal.")).defaultValue(Integer.valueOf(10))).range(0, 20).sliderRange(0, 20).visible(() -> (((Boolean)this.facePlace.get()).booleanValue() && ((Boolean)this.slowFacePlace.get()).booleanValue() && this.slowFPMode.get() != SlowMode.Age))).build()); this.slowFPAge = this.sgFacePlace.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("slow-FP-age")).description("How old a crystal must be server-side in ticks to be broken.")).defaultValue(Integer.valueOf(3))).range(0, 20).sliderRange(0, 20).visible(() -> (((Boolean)this.facePlace.get()).booleanValue() && ((Boolean)this.slowFacePlace.get()).booleanValue() && this.slowFPMode.get() != SlowMode.Delay))).build()); Objects.requireNonNull(this.facePlace); this.surrHoldPause = this.sgFacePlace.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-hold")).description("Will pause face placing while surround hold is active.")).defaultValue(Boolean.valueOf(true))).visible(this.facePlace::get)).build()); Objects.requireNonNull(this.facePlace); this.KAPause = this.sgFacePlace.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-KA")).description("Will pause face placing when KA is active.")).defaultValue(Boolean.valueOf(true))).visible(this.facePlace::get)).build()); Objects.requireNonNull(this.facePlace); this.CevPause = this.sgFacePlace.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-cev")).description("Will pause face placing when Cev Breaker is active.")).defaultValue(Boolean.valueOf(true))).visible(this.facePlace::get)).build()); Objects.requireNonNull(this.facePlace); this.facePlaceHealth = this.sgFacePlace.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("face-place-health")).description("The health the target has to be at to start face placing.")).defaultValue(6.0D).range(0.0D, 36.0D).sliderRange(0.0D, 36.0D).visible(this.facePlace::get)).build()); Objects.requireNonNull(this.facePlace); this.facePlaceDurability = this.sgFacePlace.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("face-place-durability")).description("The durability threshold percentage to be able to face-place.")).defaultValue(10.0D).range(1.0D, 100.0D).sliderRange(1.0D, 100.0D).visible(this.facePlace::get)).build()); Objects.requireNonNull(this.facePlace); this.facePlaceArmor = this.sgFacePlace.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("missing-armor")).description("Automatically starts face placing when a target misses a piece of armor.")).defaultValue(Boolean.valueOf(false))).visible(this.facePlace::get)).build()); this.burrowBreak = this.sgSurround.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("burrow-break")).description("Will try to break target's burrow.")).defaultValue(Boolean.valueOf(false))).build()); this.forceBurrowBreak = this.sgSurround.add((Setting)((KeybindSetting.Builder)((KeybindSetting.Builder)((KeybindSetting.Builder)(new KeybindSetting.Builder()).name("force-break")).description("Starts burrow breaking when this button is pressed.")).defaultValue(Keybind.none())).build()); Objects.requireNonNull(this.burrowBreak); this.burrowBreakDelay = this.sgSurround.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("burrow-break-delay")).description("Place delay in ticks for burrow break.")).defaultValue(Integer.valueOf(10))).range(0, 20).sliderRange(0, 20).visible(this.burrowBreak::get)).build()); Objects.requireNonNull(this.burrowBreak); this.burrowBWhen = this.sgSurround.add((Setting)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("burrow-break-when")).description("When to start burrow breaking.")).defaultValue(TrapType.Always)).visible(this.burrowBreak::get)).build()); this.surroundBreak = this.sgSurround.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("surround-break")).description("Will automatically places a crystal next to target's surround.")).defaultValue(Boolean.valueOf(false))).build()); this.forceSurroundBreak = this.sgSurround.add((Setting)((KeybindSetting.Builder)((KeybindSetting.Builder)((KeybindSetting.Builder)(new KeybindSetting.Builder()).name("force-break")).description("Starts surround breaking when this button is pressed.")).defaultValue(Keybind.none())).build()); Objects.requireNonNull(this.surroundBreak); this.surroundBWhen = this.sgSurround.add((Setting)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("surround-break-when")).description("When to start surround breaking.")).defaultValue(TrapType.FaceTrapped)).visible(this.surroundBreak::get)).build()); Objects.requireNonNull(this.surroundBreak); this.surroundBreakDelay = this.sgSurround.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("surround-break-delay")).description("Place delay in ticks for surround break.")).defaultValue(Integer.valueOf(10))).range(0, 20).sliderRange(0, 20).visible(this.surroundBreak::get)).build()); Objects.requireNonNull(this.surroundBreak); this.surroundBHorse = this.sgSurround.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("horse")).description("Allow horse sides of the target's surround to be surround broken.")).defaultValue(Boolean.valueOf(false))).visible(this.surroundBreak::get)).build()); Objects.requireNonNull(this.surroundBreak); this.surroundBDiagonal = this.sgSurround.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("diagonal")).description("Allow diagonal sides of the target's surround to be surround broken.")).defaultValue(Boolean.valueOf(false))).visible(this.surroundBreak::get)).build()); this.surroundHold = this.sgSurround.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("surround-hold")).description("Break crystals slower to make them harder to surround over.")).defaultValue(Boolean.valueOf(false))).build()); Objects.requireNonNull(this.surroundHold); this.surroundHWhen = this.sgSurround.add((Setting)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("surround-hold-when")).description("When to start surround holding.")).defaultValue(TrapType.AnyTrapped)).visible(this.surroundHold::get)).build()); Objects.requireNonNull(this.surroundHold); this.surroundHoldMode = this.sgSurround.add((Setting)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("surround-hold-mode")).description("Timing to use for surround hold.")).defaultValue(SlowMode.Both)).visible(this.surroundHold::get)).build()); this.surroundHoldDelay = this.sgSurround.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("surround-hold-delay")).description("The delay in ticks to wait to break a crystal for surround hold.")).defaultValue(Integer.valueOf(10))).min(0).sliderRange(0, 15).visible(() -> (((Boolean)this.surroundHold.get()).booleanValue() && this.surroundHoldMode.get() != SlowMode.Age))).build()); this.surroundHoldAge = this.sgSurround.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("surround-hold-age")).description("Crystal age for surround hold (to prevent unnecessary attacks when people are around.")).defaultValue(Integer.valueOf(3))).min(0).sliderRange(0, 15).visible(() -> (((Boolean)this.surroundHold.get()).booleanValue() && this.surroundHoldMode.get() != SlowMode.Delay))).build()); this.doBreak = this.sgBreak.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("break")).description("If the CA should break crystals.")).defaultValue(Boolean.valueOf(true))).build()); Objects.requireNonNull(this.doBreak); this.onlyBreakOwn = this.sgBreak.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-own")).description("Only break crystals that you placed.")).defaultValue(Boolean.valueOf(false))).visible(this.doBreak::get)).build()); Objects.requireNonNull(this.doBreak); this.antiWeakness = this.sgBreak.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anti-weakness")).description("Switches to tools with high enough damage to explode the crystal with weakness effect.")).defaultValue(Boolean.valueOf(true))).visible(this.doBreak::get)).build()); Objects.requireNonNull(this.doBreak); this.BminDamage = this.sgBreak.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("min-break-damage")).description("Minimum break damage the crystal needs to deal to your target.")).defaultValue(6.0D).range(0.0D, 36.0D).sliderRange(0.0D, 36.0D).visible(this.doBreak::get)).build()); Objects.requireNonNull(this.doBreak); this.BDamageIgnore = this.sgBreak.add((Setting)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("ignore-break-damage")).description("Whether to ignore self damage when breaking crystals.")).defaultValue(DamageIgnore.Never)).visible(this.doBreak::get)).build()); this.BmaxDamage = this.sgBreak.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("max-break-damage")).description("Maximum break damage crystals can deal to yourself.")).defaultValue(6.0D).range(0.0D, 36.0D).sliderRange(0.0D, 36.0D).visible(() -> (((Boolean)this.doBreak.get()).booleanValue() && this.BDamageIgnore.get() != DamageIgnore.Always))).build()); Objects.requireNonNull(this.doBreak); this.attackFrequency = this.sgBreak.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("attack-frequency")).description("Maximum hits to do per second.")).defaultValue(Integer.valueOf(25))).range(1, 30).sliderRange(1, 30).visible(this.doBreak::get)).build()); this.BantiSuicide = this.sgBreak.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anti-suicide-break")).description("Will not break crystals if they will pop or kill you.")).defaultValue(Boolean.valueOf(true))).visible(() -> (((Boolean)this.doBreak.get()).booleanValue() && this.BDamageIgnore.get() != DamageIgnore.Always))).build()); Objects.requireNonNull(this.doBreak); this.cancelCrystalMode = this.sgBreak.add((Setting)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("cancel-mode")).description("Mode to use for the crystals to be removed from the world.")).defaultValue(CancelCrystalMode.NoDesync)).visible(this.doBreak::get)).build()); Objects.requireNonNull(this.doBreak); this.cancelTicks = this.sgBreak.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("cancel-ticks")).description("How long a tick should exist before being canceled.")).defaultValue(Integer.valueOf(3))).range(1, 5).sliderRange(1, 5).visible(this.doBreak::get)).build()); Objects.requireNonNull(this.doBreak); this.breakDelay = this.sgBreak.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("break-delay")).description("The delay in ticks to wait to break a crystal after it's placed.")).defaultValue(Integer.valueOf(0))).min(0).sliderRange(0, 20).visible(this.doBreak::get)).build()); Objects.requireNonNull(this.doBreak); this.smartDelay = this.sgBreak.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("smart-delay")).description("Only breaks crystals when the target can receive damage.")).defaultValue(Boolean.valueOf(false))).visible(this.doBreak::get)).build()); Objects.requireNonNull(this.doBreak); this.switchDelay = this.sgBreak.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("switch-delay")).description("The delay in ticks to wait to break a crystal after switching hotbar slot.")).defaultValue(Integer.valueOf(0))).min(0).sliderMax(10).visible(this.doBreak::get)).build()); Objects.requireNonNull(this.doBreak); this.breakRange = this.sgBreak.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("break-range")).description("Range in which to break crystals.")).defaultValue(4.0D).range(0.0D, 6.0D).sliderRange(0.0D, 6.0D).visible(this.doBreak::get)).build()); Objects.requireNonNull(this.doBreak); this.breakWallsRange = this.sgBreak.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("break-walls-range")).description("Range in which to break crystals when behind blocks.")).defaultValue(4.0D).range(0.0D, 6.0D).sliderRange(0.0D, 6.0D).visible(this.doBreak::get)).build()); Objects.requireNonNull(this.doBreak); this.attemptCheck = this.sgBreak.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("break-attempt-check")).description("Whether to account for how many times you try to hit a crystal.")).defaultValue(Boolean.valueOf(false))).visible(this.doBreak::get)).build()); this.breakAttempts = this.sgBreak.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("break-attempts")).description("How many times to hit a crystal before finding a new placement.")).defaultValue(Integer.valueOf(2))).sliderRange(0, 5).visible(() -> (((Boolean)this.doBreak.get()).booleanValue() && ((Boolean)this.attemptCheck.get()).booleanValue()))).build()); Objects.requireNonNull(this.doBreak); this.ageCheck = this.sgBreak.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("crystal-age-check")).description("To check how old a crystal is server-side.")).defaultValue(Boolean.valueOf(true))).visible(this.doBreak::get)).build()); this.ticksExisted = this.sgBreak.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("ticks-existed")).description("Amount of ticks a crystal needs to have existed for it to be attacked.")).defaultValue(Integer.valueOf(1))).min(1).visible(() -> (((Boolean)this.doBreak.get()).booleanValue() && ((Boolean)this.ageCheck.get()).booleanValue()))).build()); this.fastBreak = this.sgFastBreak.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("fast-break")).description("Ignores break delay and tries to break the crystal as soon as it's spawned in the world.")).defaultValue(Boolean.valueOf(true))).build()); Objects.requireNonNull(this.fastBreak); this.freqCheck = this.sgFastBreak.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("frequency-check")).description("Will not try to fast break if your attack exceeds the attack frequency.")).defaultValue(Boolean.valueOf(true))).visible(this.fastBreak::get)).build()); Objects.requireNonNull(this.fastBreak); this.damageCheck = this.sgFastBreak.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("damage-check")).description("Check if the crystal meets min damage first.")).defaultValue(Boolean.valueOf(true))).visible(this.fastBreak::get)).build()); Objects.requireNonNull(this.fastBreak); this.smartCheck = this.sgFastBreak.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("smart-check")).description("Will not try to fast break for slow face place / surround hold.")).defaultValue(Boolean.valueOf(true))).visible(this.fastBreak::get)).build()); this.selfPopInvincibility = this.sgChainPop.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("self-pop-invincibility")).description("Ignores self damage if you just popped.")).defaultValue(Boolean.valueOf(false))).build()); Objects.requireNonNull(this.selfPopInvincibility); this.selfPopInvincibilityTime = this.sgChainPop.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("self-pop-time")).description("How many millisecond to consider for self-pop invincibility")).defaultValue(Integer.valueOf(300))).sliderRange(1, 2000).visible(this.selfPopInvincibility::get)).build()); Objects.requireNonNull(this.selfPopInvincibility); this.selfPopIgnore = this.sgChainPop.add((Setting)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("self-pop-ignore")).description("What to ignore when you just popped.")).defaultValue(SelfPopIgnore.Break)).visible(this.selfPopInvincibility::get)).build()); this.targetPopInvincibility = this.sgChainPop.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("target-pop-invincibility")).description("Tries to pause certain actions when your enemy just popped.")).defaultValue(Boolean.valueOf(false))).build()); Objects.requireNonNull(this.targetPopInvincibility); this.targetPopInvincibilityTime = this.sgChainPop.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("target-pop-time")).description("How many milliseconds to consider for target-pop invincibility")).defaultValue(Integer.valueOf(500))).sliderRange(1, 2000).visible(this.targetPopInvincibility::get)).build()); Objects.requireNonNull(this.targetPopInvincibility); this.popPause = this.sgChainPop.add((Setting)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("pop-pause-mode")).description("What to pause when your enemy just popped.")).defaultValue(PopPause.Break)).visible(this.targetPopInvincibility::get)).build()); this.pauseAtHealth = this.sgPause.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("pause-health")).description("Pauses when you go below a certain health.")).defaultValue(5.0D).min(0.0D).build()); this.eatPause = this.sgPause.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-eat")).description("Pauses Crystal Aura when eating.")).defaultValue(Boolean.valueOf(true))).build()); this.drinkPause = this.sgPause.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-drink")).description("Pauses Crystal Aura when drinking.")).defaultValue(Boolean.valueOf(true))).build()); this.minePause = this.sgPause.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-mine")).description("Pauses Crystal Aura when mining.")).defaultValue(Boolean.valueOf(false))).build()); this.renderSwing = this.sgRender.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render-swing")).description("Whether to swing your hand client side")).defaultValue(Boolean.valueOf(true))).build()); this.renderMode = this.sgRender.add((Setting)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("render-mode")).description("The mode to render in.")).defaultValue(RenderMode.Normal)).build()); this.shapeMode = this.sgRender.add((Setting)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).visible(() -> (this.renderMode.get() != RenderMode.None))).build()); this.fadeTime = this.sgRender.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("fade-time")).description("Tick duration for rendering placing.")).defaultValue(Integer.valueOf(8))).range(0, 20).sliderRange(0, 20).visible(() -> (this.renderMode.get() == RenderMode.Fade))).build()); this.fadeAmount = this.sgRender.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("fade-amount")).description("How strong the fade should be.")).defaultValue(Integer.valueOf(8))).range(0, 100).sliderRange(0, 100).visible(() -> (this.renderMode.get() == RenderMode.Fade))).build()); this.renderTime = this.sgRender.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("place-time")).description("How long to render placements for.")).defaultValue(Integer.valueOf(10))).range(0, 20).sliderRange(0, 20).visible(() -> (this.renderMode.get() == RenderMode.Normal))).build()); this.placeSideColor = this.sgRender.add((Setting)((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("place-side-color")).description("The side color of the block overlay.")).defaultValue(new SettingColor(255, 255, 255, 45))).visible(() -> (this.renderMode.get() != RenderMode.None && this.shapeMode.get() != ShapeMode.Lines))).build()); this.placeLineColor = this.sgRender.add((Setting)((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("place-line-color")).description("The line color of the block overlay.")).defaultValue(new SettingColor(255, 255, 255, 255))).visible(() -> (this.renderMode.get() != RenderMode.None && this.shapeMode.get() != ShapeMode.Sides))).build());
/*      */     this.renderBreak = this.sgRender.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("break")).description("Renders a block overlay over the block the crystals are broken on.")).defaultValue(Boolean.valueOf(false))).visible(() -> (this.renderMode.get() != RenderMode.None))).build());
/*      */     this.renderBreakTime = this.sgRender.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("break-time")).description("How long to render breaking for.")).defaultValue(Integer.valueOf(7))).range(0, 20).sliderRange(0, 20).visible(() -> (this.renderMode.get() != RenderMode.None && ((Boolean)this.renderBreak.get()).booleanValue() && this.renderMode.get() == RenderMode.Normal))).build());
/*      */     this.breakSideColor = this.sgRender.add((Setting)((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("break-side-color")).description("The side color of the block overlay.")).defaultValue(new SettingColor(255, 255, 255, 45))).visible(() -> (this.renderMode.get() != RenderMode.None && ((Boolean)this.renderBreak.get()).booleanValue() && this.shapeMode.get() != ShapeMode.Lines))).build());
/*      */     this.breakLineColor = this.sgRender.add((Setting)((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("break-line-color")).description("The line color of the block overlay.")).defaultValue(new SettingColor(255, 255, 255, 255))).visible(() -> (this.renderMode.get() != RenderMode.None && ((Boolean)this.renderBreak.get()).booleanValue() && this.shapeMode.get() != ShapeMode.Sides))).build());
/*      */     this.renderDamage = this.sgRender.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("damage")).description("Renders crystal damage text in the block overlay.")).defaultValue(Boolean.valueOf(true))).visible(() -> (this.renderMode.get() != RenderMode.None))).build());
/*      */     this.damageScale = this.sgRender.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("damage-scale")).description("How big the damage text should be.")).defaultValue(1.25D).min(1.0D).sliderMax(4.0D).visible(() -> (this.renderMode.get() != RenderMode.None && ((Boolean)this.renderDamage.get()).booleanValue()))).build());
/*      */     this.damageColor = this.sgRender.add((Setting)((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("damage-color")).description("What the color of the damage text should be.")).defaultValue(new SettingColor(255, 255, 255, 255))).visible(() -> (this.renderMode.get() != RenderMode.None && ((Boolean)this.renderDamage.get()).booleanValue()))).build());
/*  995 */     this.targets = new ArrayList<>();
/*      */     
/*  997 */     this.vec3d = new class_243(0.0D, 0.0D, 0.0D);
/*  998 */     this.playerEyePos = new class_243(0.0D, 0.0D, 0.0D);
/*  999 */     this.vec3 = new Vec3();
/* 1000 */     this.blockPos = new class_2338.class_2339();
/* 1001 */     this.box = new class_238(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
/*      */     
/* 1003 */     this.vec3dRayTraceEnd = new class_243(0.0D, 0.0D, 0.0D);
/*      */ 
/*      */     
/* 1006 */     this.placedCrystals = (IntSet)new IntOpenHashSet();
/*      */ 
/*      */     
/* 1009 */     this.placingCrystalBlockPos = new class_2338.class_2339();
/*      */     
/* 1011 */     this.removed = (IntSet)new IntOpenHashSet();
/* 1012 */     this.attemptedBreaks = (Int2IntMap)new Int2IntOpenHashMap();
/* 1013 */     this.waitingToExplode = (Int2IntMap)new Int2IntOpenHashMap();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1024 */     this.lastRotationPos = new class_243(0.0D, 0.0D, 0.0D);
/*      */ 
/*      */ 
/*      */     
/* 1028 */     this.selfPoppedTimer = new TimerUtils();
/* 1029 */     this.targetPoppedTimer = new TimerUtils();
/*      */ 
/*      */     
/* 1032 */     this.renderPos = new class_2338.class_2339();
/* 1033 */     this.breakRenderPos = new class_2338.class_2339();
/*      */     
/* 1035 */     this.renderBlockPool = new Pool(() -> new RenderBlock());
/* 1036 */     this.renderBlocks = new ArrayList<>();
/*      */     
/* 1038 */     this.renderBreakBlockPool = new Pool(() -> new RenderBlock());
/* 1039 */     this.renderBreakBlocks = new ArrayList<>();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void onActivate() {
/* 1046 */     this.breakTimer = 0;
/* 1047 */     this.placeTimer = 0;
/* 1048 */     this.ticksPassed = 0;
/*      */     
/* 1050 */     this.raycastContext = new class_3959(new class_243(0.0D, 0.0D, 0.0D), new class_243(0.0D, 0.0D, 0.0D), class_3959.class_3960.field_17558, class_3959.class_242.field_1348, (class_1297)this.mc.field_1724);
/*      */     
/* 1052 */     this.placing = false;
/* 1053 */     this.placingTimer = 0;
/*      */     
/* 1055 */     this.attacks = 0;
/*      */     
/* 1057 */     this.serverYaw = this.mc.field_1724.method_36454();
/*      */     
/* 1059 */     this.bestTargetDamage = 0.0F;
/* 1060 */     this.bestTargetTimer = 0;
/*      */     
/* 1062 */     this.lastRotationTimer = getLastRotationStopDelay();
/*      */     
/* 1064 */     this.renderTimer = 0;
/* 1065 */     this.breakRenderTimer = 0;
/*      */     
/* 1067 */     for (RenderBlock renderBlock : this.renderBlocks) this.renderBlockPool.free(renderBlock); 
/* 1068 */     this.renderBlocks.clear();
/*      */     
/* 1070 */     for (RenderBlock renderBlock : this.renderBreakBlocks) this.renderBreakBlockPool.free(renderBlock); 
/* 1071 */     this.renderBlocks.clear();
/*      */   }
/*      */ 
/*      */   
/*      */   public void onDeactivate() {
/* 1076 */     this.targets.clear();
/*      */     
/* 1078 */     this.placedCrystals.clear();
/*      */     
/* 1080 */     this.attemptedBreaks.clear();
/* 1081 */     this.waitingToExplode.clear();
/*      */     
/* 1083 */     this.removed.clear();
/*      */     
/* 1085 */     this.bestTarget = null;
/*      */     
/* 1087 */     for (RenderBlock renderBlock : this.renderBlocks) this.renderBlockPool.free(renderBlock); 
/* 1088 */     this.renderBlocks.clear();
/*      */     
/* 1090 */     for (RenderBlock renderBlock : this.renderBreakBlocks) this.renderBreakBlockPool.free(renderBlock); 
/* 1091 */     this.renderBlocks.clear();
/*      */   }
/*      */   
/*      */   private int getLastRotationStopDelay() {
/* 1095 */     return Math.max(10, ((Integer)this.placeDelay.get()).intValue() / 2 + ((Integer)this.breakDelay.get()).intValue() / 2 + 10);
/*      */   }
/*      */ 
/*      */   
/*      */   @EventHandler(priority = 100)
/*      */   private void onPreTick(TickEvent.Pre event) {
/* 1101 */     if (this.renderTimer > 0) this.renderTimer--; 
/* 1102 */     if (this.breakRenderTimer > 0) this.breakRenderTimer--;
/*      */ 
/*      */     
/* 1105 */     this.renderBlocks.forEach(RenderBlock::tick);
/* 1106 */     this.renderBlocks.removeIf(renderBlock -> (renderBlock.ticks <= 0));
/*      */     
/* 1108 */     this.renderBreakBlocks.forEach(RenderBlock::tick);
/* 1109 */     this.renderBreakBlocks.removeIf(renderBlock -> (renderBlock.ticks <= 0));
/*      */ 
/*      */     
/* 1112 */     this.didRotateThisTick = false;
/* 1113 */     this.lastRotationTimer++;
/*      */ 
/*      */     
/* 1116 */     if (this.placing) {
/* 1117 */       if (this.placingTimer > 0) { this.placingTimer--; }
/* 1118 */       else { this.placing = false; }
/*      */     
/*      */     }
/* 1121 */     if (this.ticksPassed < 20) { this.ticksPassed++; }
/*      */     else
/* 1123 */     { this.ticksPassed = 0;
/* 1124 */       this.attacks = 0; }
/*      */ 
/*      */ 
/*      */     
/* 1128 */     if (this.bestTargetTimer > 0) this.bestTargetTimer--; 
/* 1129 */     this.bestTargetDamage = 0.0F;
/*      */ 
/*      */     
/* 1132 */     if (this.breakTimer > 0) this.breakTimer--; 
/* 1133 */     if (this.placeTimer > 0) this.placeTimer--; 
/* 1134 */     if (this.switchTimer > 0) this.switchTimer--;
/*      */ 
/*      */     
/* 1137 */     for (IntIterator it = this.waitingToExplode.keySet().iterator(); it.hasNext(); ) {
/* 1138 */       int id = it.nextInt();
/* 1139 */       int ticks = this.waitingToExplode.get(id);
/*      */       
/* 1141 */       if (ticks >= ((Integer)this.cancelTicks.get()).intValue()) {
/* 1142 */         it.remove();
/* 1143 */         this.removed.remove(id);
/*      */         continue;
/*      */       } 
/* 1146 */       this.waitingToExplode.put(id, ticks + 1);
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1151 */     if (PlayerUtils.shouldPause(((Boolean)this.minePause.get()).booleanValue(), ((Boolean)this.eatPause.get()).booleanValue(), ((Boolean)this.drinkPause.get()).booleanValue()) || PlayerUtils.getTotalHealth() <= ((Double)this.pauseAtHealth.get()).doubleValue()) {
/* 1152 */       if (((Boolean)this.debug.get()).booleanValue()) warning("Pausing", new Object[0]);
/*      */ 
/*      */       
/*      */       return;
/*      */     } 
/* 1157 */     ((IVec3d)this.playerEyePos).set((this.mc.field_1724.method_19538()).field_1352, (this.mc.field_1724.method_19538()).field_1351 + this.mc.field_1724.method_18381(this.mc.field_1724.method_18376()), (this.mc.field_1724.method_19538()).field_1350);
/*      */ 
/*      */     
/* 1160 */     findTargets();
/*      */     
/* 1162 */     if (this.targets.size() > 0) {
/* 1163 */       doBreak();
/* 1164 */       doPlace();
/*      */     } 
/*      */     
/* 1167 */     if (this.cancelCrystalMode.get() == CancelCrystalMode.Hit) {
/* 1168 */       this.removed.forEach(id -> ((class_1297)Objects.<class_1297>requireNonNull(this.mc.field_1687.method_8469(id))).method_5768());
/* 1169 */       this.removed.clear();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   @EventHandler(priority = -866)
/*      */   private void onPreTickLast(TickEvent.Pre event) {
/* 1176 */     if (((Boolean)this.rotate.get()).booleanValue() && this.lastRotationTimer < getLastRotationStopDelay() && !this.didRotateThisTick) {
/* 1177 */       Rotations.rotate(this.isLastRotationPos ? Rotations.getYaw(this.lastRotationPos) : this.lastYaw, this.isLastRotationPos ? Rotations.getPitch(this.lastRotationPos) : this.lastPitch, -100, null);
/*      */     }
/*      */   }
/*      */   
/*      */   @EventHandler
/*      */   private void onEntityAdded(EntityAddedEvent event) {
/* 1183 */     if (!(event.entity instanceof net.minecraft.class_1511))
/*      */       return; 
/* 1185 */     if (this.placing && event.entity.method_24515().equals(this.placingCrystalBlockPos)) {
/* 1186 */       this.placing = false;
/* 1187 */       this.placingTimer = 0;
/* 1188 */       this.placedCrystals.add(event.entity.method_5628());
/*      */     } 
/*      */     
/* 1191 */     if (((Boolean)this.fastBreak.get()).booleanValue()) {
/* 1192 */       if (((Boolean)this.freqCheck.get()).booleanValue() && 
/* 1193 */         this.attacks > ((Integer)this.attackFrequency.get()).intValue()) {
/*      */         return;
/*      */       }
/* 1196 */       if (((Boolean)this.smartCheck.get()).booleanValue() && (
/* 1197 */         CrystalUtils.isSurroundHolding() || (((Boolean)this.slowFacePlace.get()).booleanValue() && CrystalUtils.isFacePlacing()) || (((Boolean)this.targetPopInvincibility.get()).booleanValue() && CrystalUtils.targetJustPopped()))) {
/*      */         return;
/*      */       }
/* 1200 */       float damage = getBreakDamage(event.entity, false);
/* 1201 */       if (((Boolean)this.damageCheck.get()).booleanValue() && 
/* 1202 */         damage < ((Double)this.BminDamage.get()).doubleValue()) {
/*      */         return;
/*      */       }
/* 1205 */       doBreak(event.entity);
/*      */     } 
/*      */   }
/*      */   
/*      */   @EventHandler
/*      */   private void onEntityRemoved(EntityRemovedEvent event) {
/* 1211 */     if (event.entity instanceof net.minecraft.class_1511) {
/* 1212 */       this.placedCrystals.remove(event.entity.method_5628());
/* 1213 */       this.removed.remove(event.entity.method_5628());
/* 1214 */       this.waitingToExplode.remove(event.entity.method_5628());
/*      */     } 
/*      */   }
/*      */   
/*      */   private void setRotation(boolean isPos, class_243 pos, double yaw, double pitch) {
/* 1219 */     this.didRotateThisTick = true;
/* 1220 */     this.isLastRotationPos = isPos;
/*      */     
/* 1222 */     if (isPos) { ((IVec3d)this.lastRotationPos).set(pos.field_1352, pos.field_1351, pos.field_1350); }
/*      */     else
/* 1224 */     { this.lastYaw = yaw;
/* 1225 */       this.lastPitch = pitch; }
/*      */ 
/*      */     
/* 1228 */     this.lastRotationTimer = 0;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void doBreak() {
/* 1234 */     if (!((Boolean)this.doBreak.get()).booleanValue() || this.breakTimer > 0 || this.switchTimer > 0 || this.attacks >= ((Integer)this.attackFrequency.get()).intValue() || (this.popPause.get() != PopPause.Place && CrystalUtils.targetJustPopped()))
/*      */       return; 
/* 1236 */     float bestDamage = 0.0F;
/* 1237 */     class_1297 crystal = null;
/*      */ 
/*      */     
/* 1240 */     for (class_1297 entity : this.mc.field_1687.method_18112()) {
/* 1241 */       float damage = getBreakDamage(entity, true);
/*      */       
/* 1243 */       if (damage > bestDamage) {
/* 1244 */         bestDamage = damage;
/* 1245 */         crystal = entity;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1250 */     if (crystal != null) doBreak(crystal); 
/*      */   }
/*      */   
/*      */   private float getBreakDamage(class_1297 entity, boolean checkCrystalAge) {
/* 1254 */     if (!(entity instanceof net.minecraft.class_1511)) return 0.0F;
/*      */ 
/*      */     
/* 1257 */     if (((Boolean)this.onlyBreakOwn.get()).booleanValue() && !this.placedCrystals.contains(entity.method_5628())) return 0.0F;
/*      */ 
/*      */     
/* 1260 */     if (this.removed.contains(entity.method_5628())) return 0.0F;
/*      */ 
/*      */     
/* 1263 */     if (((Boolean)this.attemptCheck.get()).booleanValue() && 
/* 1264 */       this.attemptedBreaks.get(entity.method_5628()) > ((Integer)this.breakAttempts.get()).intValue()) return 0.0F;
/*      */ 
/*      */ 
/*      */     
/* 1268 */     if (((Boolean)this.ageCheck.get()).booleanValue() && 
/* 1269 */       checkCrystalAge && entity.field_6012 < ((Integer)this.ticksExisted.get()).intValue()) return 0.0F;
/*      */ 
/*      */     
/* 1272 */     if (CrystalUtils.isSurroundHolding() && this.surroundHoldMode.get() != SlowMode.Delay && 
/* 1273 */       checkCrystalAge && entity.field_6012 < ((Integer)this.surroundHoldAge.get()).intValue()) return 0.0F;
/*      */ 
/*      */     
/* 1276 */     if (((Boolean)this.slowFacePlace.get()).booleanValue() && this.slowFPMode.get() != SlowMode.Delay && CrystalUtils.isFacePlacing() && this.bestTarget != null && this.bestTarget.method_23318() < this.placingCrystalBlockPos.method_10264() && 
/* 1277 */       checkCrystalAge && entity.field_6012 < ((Integer)this.slowFPAge.get()).intValue()) return 0.0F;
/*      */ 
/*      */ 
/*      */     
/* 1281 */     if (isOutOfBreakRange(entity)) return 0.0F;
/*      */ 
/*      */     
/* 1284 */     this.blockPos.method_10101((class_2382)entity.method_24515()).method_10100(0, -1, 0);
/*      */     
/* 1286 */     if (!CrystalUtils.shouldIgnoreSelfBreakDamage())
/* 1287 */     { float selfDamage = NDamageUtils.crystalDamage((class_1657)this.mc.field_1724, entity.method_19538(), ((Boolean)this.predictMovement.get()).booleanValue(), ((Double)this.breakRange.get()).floatValue(), ((Boolean)this.ignoreTerrain.get()).booleanValue(), ((Boolean)this.fullBlocks.get()).booleanValue());
/* 1288 */       if (selfDamage > ((Double)this.BmaxDamage.get()).doubleValue() || (((Boolean)this.BantiSuicide.get()).booleanValue() && selfDamage >= EntityUtils.getTotalHealth((class_1657)this.mc.field_1724)))
/* 1289 */         return 0.0F;  }
/* 1290 */     else if (((Boolean)this.debug.get()).booleanValue()) { warning("Ignoring self break dmg", new Object[0]); }
/*      */ 
/*      */     
/* 1293 */     float damage = getDamageToTargets(entity.method_19538(), true, false);
/* 1294 */     boolean facePlaced = ((((Boolean)this.facePlace.get()).booleanValue() && CrystalUtils.shouldFacePlace((class_2338)this.blockPos)) || ((Keybind)this.forceFacePlace.get()).isPressed());
/*      */     
/* 1296 */     if (!facePlaced && damage < ((Double)this.BminDamage.get()).doubleValue()) return 0.0F;
/*      */     
/* 1298 */     return damage;
/*      */   }
/*      */ 
/*      */   
/*      */   private void doBreak(class_1297 crystal) {
/* 1303 */     if (((Boolean)this.antiWeakness.get()).booleanValue()) {
/* 1304 */       class_1293 weakness = this.mc.field_1724.method_6112(class_1294.field_5911);
/* 1305 */       class_1293 strength = this.mc.field_1724.method_6112(class_1294.field_5910);
/*      */ 
/*      */       
/* 1308 */       if (weakness != null && (strength == null || strength.method_5578() <= weakness.method_5578()))
/*      */       {
/* 1310 */         if (!isValidWeaknessItem(this.mc.field_1724.method_6047())) {
/*      */           
/* 1312 */           if (!InvUtils.swap(InvUtils.findInHotbar(this::isValidWeaknessItem).slot(), false))
/*      */             return; 
/* 1314 */           this.switchTimer = 1;
/*      */           
/*      */           return;
/*      */         } 
/*      */       }
/*      */     } 
/*      */     
/* 1321 */     boolean attacked = true;
/*      */     
/* 1323 */     if (!((Boolean)this.rotate.get()).booleanValue()) {
/* 1324 */       CrystalUtils.attackCrystal(crystal);
/*      */     } else {
/*      */       
/* 1327 */       double yaw = Rotations.getYaw(crystal);
/* 1328 */       double pitch = Rotations.getPitch(crystal, Target.Feet);
/*      */       
/* 1330 */       if (doYawSteps(yaw, pitch)) {
/* 1331 */         setRotation(true, crystal.method_19538(), 0.0D, 0.0D);
/* 1332 */         Rotations.rotate(yaw, pitch, 50, () -> CrystalUtils.attackCrystal(crystal));
/*      */       } else {
/*      */         
/* 1335 */         attacked = false;
/*      */       } 
/*      */     } 
/*      */     
/* 1339 */     if (attacked) {
/*      */       
/* 1341 */       this.removed.add(crystal.method_5628());
/* 1342 */       this.attemptedBreaks.put(crystal.method_5628(), this.attemptedBreaks.get(crystal.method_5628()) + 1);
/* 1343 */       this.waitingToExplode.put(crystal.method_5628(), 0);
/*      */ 
/*      */       
/* 1346 */       this.renderBreakBlocks.add(((RenderBlock)this.renderBreakBlockPool.get()).set(crystal.method_24515().method_10074()));
/* 1347 */       this.breakRenderPos.method_10101((class_2382)crystal.method_24515().method_10074());
/* 1348 */       this.breakRenderTimer = ((Integer)this.renderBreakTime.get()).intValue();
/*      */     } 
/*      */   }
/*      */   
/*      */   private boolean isValidWeaknessItem(class_1799 itemStack) {
/* 1353 */     if (!(itemStack.method_7909() instanceof class_1831) || itemStack.method_7909() instanceof net.minecraft.class_1794) return false;
/*      */     
/* 1355 */     class_1832 material = ((class_1831)itemStack.method_7909()).method_8022();
/* 1356 */     return (material == class_1834.field_8930 || material == class_1834.field_22033);
/*      */   }
/*      */   
/*      */   @EventHandler
/*      */   private void onPacketSend(PacketEvent.Send event) {
/* 1361 */     if (event.packet instanceof net.minecraft.class_2868) {
/* 1362 */       this.switchTimer = ((Integer)this.switchDelay.get()).intValue();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void doPlace() {
/* 1369 */     if (!((Boolean)this.doPlace.get()).booleanValue() || this.placeTimer > 0 || (this.popPause.get() != PopPause.Break && CrystalUtils.targetJustPopped())) {
/*      */       return;
/*      */     }
/* 1372 */     if (!InvUtils.findInHotbar(new class_1792[] { class_1802.field_8301 }).found()) {
/*      */       return;
/*      */     }
/* 1375 */     if (this.autoSwitch.get() == AutoSwitchMode.None && this.mc.field_1724.method_6079().method_7909() != class_1802.field_8301 && this.mc.field_1724.method_6047().method_7909() != class_1802.field_8301) {
/*      */       return;
/*      */     }
/* 1378 */     for (class_1297 entity : this.mc.field_1687.method_18112()) {
/* 1379 */       if (getBreakDamage(entity, false) > 0.0F) {
/*      */         return;
/*      */       }
/*      */     } 
/* 1383 */     AtomicDouble bestDamage = new AtomicDouble(0.0D);
/* 1384 */     AtomicReference<class_2338.class_2339> bestBlockPos = new AtomicReference<>(new class_2338.class_2339());
/* 1385 */     AtomicBoolean isSupport = new AtomicBoolean((this.support.get() != SupportMode.Disabled));
/*      */ 
/*      */     
/* 1388 */     BlockIterator.register((int)Math.ceil(((Double)this.placeRange.get()).doubleValue()), (int)Math.ceil(((Double)this.placeRange.get()).doubleValue()), (bp, blockState) -> {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1394 */           boolean hasBlock = (blockState.method_27852(class_2246.field_9987) || blockState.method_27852(class_2246.field_10540));
/*      */           if (!hasBlock) {
/*      */             if (isSupport.get()) {
/*      */               if (!blockState.method_26207().method_15800()) {
/*      */                 return;
/*      */               }
/*      */             } else {
/*      */               return;
/*      */             } 
/*      */           }
/*      */           this.blockPos.method_10103(bp.method_10263(), bp.method_10264() + 1, bp.method_10260());
/*      */           if (!this.mc.field_1687.method_8320((class_2338)this.blockPos).method_26215()) {
/*      */             return;
/*      */           }
/*      */           if (((Boolean)this.placement112.get()).booleanValue()) {
/*      */             this.blockPos.method_10100(0, 1, 0);
/*      */             if (!this.mc.field_1687.method_8320((class_2338)this.blockPos).method_26215()) {
/*      */               return;
/*      */             }
/*      */           } 
/*      */           ((IVec3d)this.vec3d).set(bp.method_10263() + 0.5D, (bp.method_10264() + 1), bp.method_10260() + 0.5D);
/*      */           this.blockPos.method_10101((class_2382)bp).method_10100(0, 1, 0);
/*      */           if (isOutOfPlaceRange(this.vec3d, (class_2338)this.blockPos)) {
/*      */             return;
/*      */           }
/*      */           int x = bp.method_10263();
/*      */           int y = bp.method_10264() + 1;
/*      */           int z = bp.method_10260();
/*      */           ((IBox)this.box).set(x + 0.001D, y, z + 0.001D, x + 0.999D, (y + (((Boolean)this.smallBox.get()).booleanValue() ? 1 : 2)), z + 0.999D);
/*      */           if (intersectsWithEntities(this.box)) {
/*      */             return;
/*      */           }
/*      */           if (!CrystalUtils.shouldIgnoreSelfPlaceDamage()) {
/*      */             float selfDamage = NDamageUtils.crystalDamage((class_1657)this.mc.field_1724, this.vec3d, ((Boolean)this.predictMovement.get()).booleanValue(), ((Double)this.placeRange.get()).floatValue(), ((Boolean)this.ignoreTerrain.get()).booleanValue(), ((Boolean)this.fullBlocks.get()).booleanValue());
/*      */             if (selfDamage > ((Double)this.PmaxDamage.get()).doubleValue() || (((Boolean)this.PantiSuicide.get()).booleanValue() && selfDamage >= EntityUtils.getTotalHealth((class_1657)this.mc.field_1724))) {
/*      */               return;
/*      */             }
/*      */           } else if (((Boolean)this.debug.get()).booleanValue()) {
/*      */             warning("Ignoring self place dmg", new Object[0]);
/*      */           } 
/* 1434 */           float damage = getDamageToTargets(this.vec3d, false, (!hasBlock && this.support.get() == SupportMode.Fast));
/*      */           
/* 1436 */           boolean facePlaced = ((((Boolean)this.facePlace.get()).booleanValue() && CrystalUtils.shouldFacePlace((class_2338)this.blockPos)) || ((Keybind)this.forceFacePlace.get()).isPressed());
/*      */           
/* 1438 */           boolean burrowBreaking = (CrystalUtils.isBurrowBreaking() && CrystalUtils.shouldBurrowBreak((class_2338)this.blockPos));
/*      */           
/* 1440 */           boolean surroundBreaking = (CrystalUtils.isSurroundBreaking() && CrystalUtils.shouldSurroundBreak((class_2338)this.blockPos));
/*      */           
/*      */           if (!facePlaced && !surroundBreaking && !burrowBreaking && damage < ((Double)this.PminDamage.get()).doubleValue()) {
/*      */             return;
/*      */           }
/*      */           
/*      */           if (damage > bestDamage.get() || (isSupport.get() && hasBlock)) {
/*      */             bestDamage.set(damage);
/*      */             ((class_2338.class_2339)bestBlockPos.get()).method_10101((class_2382)bp);
/*      */           } 
/*      */           if (hasBlock) {
/*      */             isSupport.set(false);
/*      */           }
/*      */         });
/* 1454 */     BlockIterator.after(() -> {
/*      */           if (bestDamage.get() == 0.0D) {
/*      */             return;
/*      */           }
/*      */           class_3965 result = getPlaceInfo(bestBlockPos.get());
/*      */           ((IVec3d)this.vec3d).set(result.method_17777().method_10263() + 0.5D + result.method_17780().method_10163().method_10263() * 0.5D, result.method_17777().method_10264() + 0.5D + result.method_17780().method_10163().method_10264() * 0.5D, result.method_17777().method_10260() + 0.5D + result.method_17780().method_10163().method_10260() * 0.5D);
/*      */           if (((Boolean)this.rotate.get()).booleanValue()) {
/*      */             double yaw = Rotations.getYaw(this.vec3d);
/*      */             double pitch = Rotations.getPitch(this.vec3d);
/*      */             if (this.yawStepMode.get() == YawStepMode.Break || doYawSteps(yaw, pitch)) {
/*      */               setRotation(true, this.vec3d, 0.0D, 0.0D);
/*      */               Rotations.rotate(yaw, pitch, 50, ());
/*      */               this.placeTimer += CrystalUtils.getPlaceDelay();
/*      */             } 
/*      */           } else {
/*      */             placeCrystal(result, bestDamage.get(), isSupport.get() ? bestBlockPos.get() : null);
/*      */             this.placeTimer += CrystalUtils.getPlaceDelay();
/*      */           } 
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private class_3965 getPlaceInfo(class_2338 blockPos) {
/* 1484 */     ((IVec3d)this.vec3d).set(this.mc.field_1724.method_23317(), this.mc.field_1724.method_23318() + this.mc.field_1724.method_18381(this.mc.field_1724.method_18376()), this.mc.field_1724.method_23321());
/*      */     
/* 1486 */     for (class_2350 class_2350 : class_2350.values()) {
/* 1487 */       ((IVec3d)this.vec3dRayTraceEnd).set(blockPos
/* 1488 */           .method_10263() + 0.5D + class_2350.method_10163().method_10263() * 0.5D, blockPos
/* 1489 */           .method_10264() + 0.5D + class_2350.method_10163().method_10264() * 0.5D, blockPos
/* 1490 */           .method_10260() + 0.5D + class_2350.method_10163().method_10260() * 0.5D);
/*      */ 
/*      */       
/* 1493 */       ((IRaycastContext)this.raycastContext).set(this.vec3d, this.vec3dRayTraceEnd, class_3959.class_3960.field_17558, class_3959.class_242.field_1348, (class_1297)this.mc.field_1724);
/* 1494 */       class_3965 result = this.mc.field_1687.method_17742(this.raycastContext);
/*      */       
/* 1496 */       if (result != null && result.method_17783() == class_239.class_240.field_1332 && result.method_17777().equals(blockPos)) {
/* 1497 */         return result;
/*      */       }
/*      */     } 
/*      */     
/* 1501 */     class_2350 side = (blockPos.method_10264() > this.vec3d.field_1351) ? class_2350.field_11033 : class_2350.field_11036;
/* 1502 */     return new class_3965(this.vec3d, side, blockPos, false);
/*      */   }
/*      */ 
/*      */   
/*      */   private void placeCrystal(class_3965 result, double damage, class_2338 supportBlock) {
/* 1507 */     class_1792 targetItem = (supportBlock == null) ? class_1802.field_8301 : class_1802.field_8281;
/*      */     
/* 1509 */     FindItemResult item = InvUtils.findInHotbar(new class_1792[] { targetItem });
/* 1510 */     if (!item.found())
/*      */       return; 
/* 1512 */     int prevSlot = (this.mc.field_1724.method_31548()).field_7545;
/*      */     
/* 1514 */     if (!(this.mc.field_1724.method_6079().method_7909() instanceof net.minecraft.class_1774) && this.autoSwitch.get() == AutoSwitchMode.Normal && ((Boolean)this.noGapSwitch.get()).booleanValue() && this.mc.field_1724.method_6047().method_7909() instanceof net.minecraft.class_1775)
/* 1515 */       return;  if (this.autoSwitch.get() != AutoSwitchMode.None && !item.isOffhand()) InvUtils.swap(item.slot(), false);
/*      */     
/* 1517 */     class_1268 hand = item.getHand();
/* 1518 */     if (hand == null) {
/*      */       return;
/*      */     }
/* 1521 */     if (supportBlock == null) {
/*      */       
/* 1523 */       this.mc.field_1724.field_3944.method_2883((class_2596)new class_2885(hand, result, 0));
/*      */       
/* 1525 */       if (((Boolean)this.renderSwing.get()).booleanValue()) this.mc.field_1724.method_6104(hand); 
/* 1526 */       if (!((Boolean)this.hideSwings.get()).booleanValue()) this.mc.method_1562().method_2883((class_2596)new class_2879(hand));
/*      */       
/* 1528 */       if (((Boolean)this.debug.get()).booleanValue()) warning("Placing", new Object[0]);
/*      */       
/* 1530 */       this.placing = true;
/* 1531 */       this.placingTimer = 4;
/* 1532 */       this.placingCrystalBlockPos.method_10101((class_2382)result.method_17777()).method_10100(0, 1, 0);
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1537 */       this.renderBlocks.add(((RenderBlock)this.renderBlockPool.get()).set(result.method_17777()));
/*      */       
/* 1539 */       this.renderTimer = ((Integer)this.renderTime.get()).intValue();
/* 1540 */       this.renderPos.method_10101((class_2382)result.method_17777());
/* 1541 */       this.damage = damage;
/*      */     }
/*      */     else {
/*      */       
/* 1545 */       BlockUtils.place(supportBlock, item, false, 0, ((Boolean)this.renderSwing.get()).booleanValue(), true, false);
/* 1546 */       this.placeTimer += ((Integer)this.supportDelay.get()).intValue();
/*      */       
/* 1548 */       if (((Integer)this.supportDelay.get()).intValue() == 0) placeCrystal(result, damage, (class_2338)null);
/*      */     
/*      */     } 
/*      */     
/* 1552 */     if (this.autoSwitch.get() == AutoSwitchMode.Silent) InvUtils.swap(prevSlot, false);
/*      */   
/*      */   }
/*      */ 
/*      */   
/*      */   @EventHandler
/*      */   private void onPacketSent(PacketEvent.Sent event) {
/* 1559 */     if (event.packet instanceof class_2828) {
/* 1560 */       this.serverYaw = ((class_2828)event.packet).method_12271((float)this.serverYaw);
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean doYawSteps(double targetYaw, double targetPitch) {
/* 1565 */     targetYaw = class_3532.method_15338(targetYaw) + 180.0D;
/* 1566 */     double serverYaw = class_3532.method_15338(this.serverYaw) + 180.0D;
/*      */     
/* 1568 */     if (distanceBetweenAngles(serverYaw, targetYaw) <= ((Double)this.yawSteps.get()).doubleValue()) return true;
/*      */     
/* 1570 */     double delta = Math.abs(targetYaw - serverYaw);
/* 1571 */     double yaw = this.serverYaw;
/*      */     
/* 1573 */     if (serverYaw < targetYaw)
/* 1574 */     { if (delta < 180.0D) { yaw += ((Double)this.yawSteps.get()).doubleValue(); }
/* 1575 */       else { yaw -= ((Double)this.yawSteps.get()).doubleValue(); }
/*      */       
/*      */        }
/* 1578 */     else if (delta < 180.0D) { yaw -= ((Double)this.yawSteps.get()).doubleValue(); }
/* 1579 */     else { yaw += ((Double)this.yawSteps.get()).doubleValue(); }
/*      */ 
/*      */     
/* 1582 */     setRotation(false, (class_243)null, yaw, targetPitch);
/* 1583 */     Rotations.rotate(yaw, targetPitch, -100, null);
/* 1584 */     return false;
/*      */   }
/*      */   
/*      */   private static double distanceBetweenAngles(double alpha, double beta) {
/* 1588 */     double phi = Math.abs(beta - alpha) % 360.0D;
/* 1589 */     return (phi > 180.0D) ? (360.0D - phi) : phi;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean isOutOfPlaceRange(class_243 vec3d, class_2338 blockPos) {
/* 1595 */     ((IRaycastContext)this.raycastContext).set(this.playerEyePos, vec3d, class_3959.class_3960.field_17558, class_3959.class_242.field_1348, (class_1297)this.mc.field_1724);
/*      */     
/* 1597 */     class_3965 result = this.mc.field_1687.method_17742(this.raycastContext);
/* 1598 */     boolean behindWall = (result == null || !result.method_17777().equals(blockPos));
/* 1599 */     double distance = this.mc.field_1724.method_33571().method_1022(vec3d);
/*      */     
/* 1601 */     return (distance > (behindWall ? (Double)this.placeWallsRange.get() : (Double)this.placeRange.get()).doubleValue());
/*      */   }
/*      */   
/*      */   private boolean isOutOfBreakRange(class_1297 entity) {
/* 1605 */     boolean behindWall = !this.mc.field_1724.method_6057(entity);
/* 1606 */     double distance = NPlayerUtils.distanceFromEye(entity);
/*      */     
/* 1608 */     return (distance > (behindWall ? (Double)this.breakWallsRange.get() : (Double)this.breakRange.get()).doubleValue());
/*      */   }
/*      */   
/*      */   private class_1657 getNearestTarget() {
/* 1612 */     class_1657 nearestTarget = null;
/* 1613 */     double nearestDistance = ((Double)this.targetRange.get()).doubleValue();
/*      */     
/* 1615 */     for (class_1657 target : this.targets) {
/* 1616 */       double distance = target.method_5858((class_1297)this.mc.field_1724);
/*      */       
/* 1618 */       if (distance < nearestDistance) {
/* 1619 */         nearestTarget = target;
/* 1620 */         nearestDistance = distance;
/*      */       } 
/*      */     } 
/*      */     
/* 1624 */     return nearestTarget;
/*      */   }
/*      */   
/*      */   private float getDamageToTargets(class_243 vec3d, boolean breaking, boolean fast) {
/* 1628 */     float damage = 0.0F;
/*      */     
/* 1630 */     if (fast) {
/* 1631 */       class_1657 target = getNearestTarget();
/* 1632 */       if (!((Boolean)this.smartDelay.get()).booleanValue() || !breaking || target.field_6235 <= 0) damage = NDamageUtils.crystalDamage(target, vec3d, ((Boolean)this.predictMovement.get()).booleanValue(), ((Double)this.explosionRadiusToTarget.get()).floatValue(), ((Boolean)this.ignoreTerrain.get()).booleanValue(), ((Boolean)this.fullBlocks.get()).booleanValue());
/*      */     
/*      */     } else {
/* 1635 */       for (class_1657 target : this.targets) {
/* 1636 */         if (((Boolean)this.smartDelay.get()).booleanValue() && breaking && target.field_6235 > 0)
/*      */           continue; 
/* 1638 */         float dmg = NDamageUtils.crystalDamage(target, vec3d, ((Boolean)this.predictMovement.get()).booleanValue(), ((Double)this.explosionRadiusToTarget.get()).floatValue(), ((Boolean)this.ignoreTerrain.get()).booleanValue(), ((Boolean)this.fullBlocks.get()).booleanValue());
/*      */ 
/*      */         
/* 1641 */         if (dmg > this.bestTargetDamage) {
/* 1642 */           this.bestTarget = target;
/* 1643 */           this.bestTargetDamage = dmg;
/* 1644 */           this.bestTargetTimer = 10;
/*      */         } 
/*      */ 
/*      */         
/* 1648 */         damage += dmg;
/*      */       } 
/*      */     } 
/*      */     
/* 1652 */     return damage;
/*      */   }
/*      */ 
/*      */   
/*      */   public String getInfoString() {
/* 1657 */     return (this.bestTarget != null && this.bestTargetTimer > 0) ? this.bestTarget.method_7334().getName() : null;
/*      */   }
/*      */   
/*      */   private void findTargets() {
/* 1661 */     this.targets.clear();
/*      */ 
/*      */     
/* 1664 */     for (class_1657 player : this.mc.field_1687.method_18456()) {
/* 1665 */       if ((player.method_31549()).field_7477 || player == this.mc.field_1724)
/*      */         continue; 
/* 1667 */       if (!player.method_29504() && player.method_5805() && Friends.get().shouldAttack(player) && player.method_5739((class_1297)this.mc.field_1724) <= ((Double)this.targetRange.get()).doubleValue()) {
/* 1668 */         this.targets.add(player);
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   private boolean intersectsWithEntities(class_238 box) {
/* 1674 */     return EntityUtils.intersectsWithEntity(box, entity -> (!entity.method_7325() && !this.removed.contains(entity.method_5628())));
/*      */   }
/*      */   @EventHandler
/*      */   private void onReceivePacket(PacketEvent.Receive event) {
/*      */     class_2663 p;
/* 1679 */     class_2596 class_2596 = event.packet; if (class_2596 instanceof class_2663) { p = (class_2663)class_2596; }
/*      */     else { return; }
/* 1681 */      if (p.method_11470() != 35)
/*      */       return; 
/* 1683 */     class_1297 entity = p.method_11469((class_1937)this.mc.field_1687);
/*      */     
/* 1685 */     if (!(entity instanceof class_1657))
/*      */       return; 
/* 1687 */     if (entity.equals(this.mc.field_1724) && ((Boolean)this.selfPopInvincibility.get()).booleanValue()) this.selfPoppedTimer.reset();
/*      */     
/* 1689 */     if (entity.equals(this.bestTarget) && ((Boolean)this.targetPopInvincibility.get()).booleanValue()) this.targetPoppedTimer.reset();
/*      */   
/*      */   }
/*      */   
/*      */   @EventHandler(priority = -1200)
/*      */   private void onTick(TickEvent.Post event) {
/* 1695 */     if (((Boolean)this.debug.get()).booleanValue()) {
/* 1696 */       if (CrystalUtils.isFacePlacing() && this.bestTarget != null && this.bestTarget.method_23318() < this.placingCrystalBlockPos.method_10264()) {
/* 1697 */         if (((Boolean)this.slowFacePlace.get()).booleanValue()) { warning("Slow faceplacing", new Object[0]); }
/* 1698 */         else { warning("Faceplacing", new Object[0]); }
/*      */       
/*      */       }
/* 1701 */       if (CrystalUtils.isBurrowBreaking()) warning("Burrow breaking", new Object[0]);
/*      */       
/* 1703 */       if (CrystalUtils.isSurroundHolding()) warning("Surround holding", new Object[0]);
/*      */       
/* 1705 */       if (CrystalUtils.isSurroundBreaking()) warning("Surround breaking", new Object[0]);
/*      */     
/*      */     } 
/*      */   }
/*      */   
/*      */   @EventHandler
/*      */   private void onRender(Render3DEvent event) {
/* 1712 */     if (this.renderMode.get() == RenderMode.Fade) {
/* 1713 */       this.renderBlocks.sort(Comparator.comparingInt(o -> -o.ticks));
/* 1714 */       this.renderBlocks.forEach(renderBlock -> renderBlock.render(event, (Color)this.placeSideColor.get(), (Color)this.placeLineColor.get(), (ShapeMode)this.shapeMode.get()));
/*      */       
/* 1716 */       if (((Boolean)this.renderBreak.get()).booleanValue()) {
/* 1717 */         this.renderBreakBlocks.sort(Comparator.comparingInt(o -> -o.ticks));
/* 1718 */         this.renderBreakBlocks.forEach(renderBlock -> renderBlock.render(event, (Color)this.breakSideColor.get(), (Color)this.breakLineColor.get(), (ShapeMode)this.shapeMode.get()));
/*      */       } 
/* 1720 */     } else if (this.renderMode.get() == RenderMode.Normal) {
/* 1721 */       if (this.renderTimer > 0) {
/* 1722 */         event.renderer.box((class_2338)this.renderPos, (Color)this.placeSideColor.get(), (Color)this.placeLineColor.get(), (ShapeMode)this.shapeMode.get(), 0);
/*      */       }
/*      */       
/* 1725 */       if (this.breakRenderTimer > 0 && ((Boolean)this.renderBreak.get()).booleanValue() && !this.mc.field_1687.method_8320((class_2338)this.breakRenderPos).method_26215()) {
/* 1726 */         int preSideA = 0;
/* 1727 */         int preLineA = 0;
/* 1728 */         if (this.renderMode.get() == RenderMode.Fade) {
/* 1729 */           preSideA = ((SettingColor)this.placeSideColor.get()).a;
/* 1730 */           ((SettingColor)this.placeSideColor.get()).a -= 20;
/* 1731 */           ((SettingColor)this.placeSideColor.get()).validate();
/*      */           
/* 1733 */           preLineA = ((SettingColor)this.breakLineColor.get()).a;
/* 1734 */           ((SettingColor)this.breakLineColor.get()).a -= 20;
/* 1735 */           ((SettingColor)this.breakLineColor.get()).validate();
/*      */         } 
/* 1737 */         event.renderer.box((class_2338)this.breakRenderPos, (Color)this.breakSideColor.get(), (Color)this.breakLineColor.get(), (ShapeMode)this.shapeMode.get(), 0);
/*      */         
/* 1739 */         ((SettingColor)this.breakSideColor.get()).a = preSideA;
/* 1740 */         ((SettingColor)this.breakLineColor.get()).a = preLineA;
/*      */       } 
/*      */     } 
/*      */   }
/*      */   public class RenderBlock { public class_2338.class_2339 pos; public int ticks;
/*      */     
/*      */     public RenderBlock() {
/* 1747 */       this.pos = new class_2338.class_2339();
/*      */     }
/*      */     
/*      */     public RenderBlock set(class_2338 blockPos) {
/* 1751 */       this.pos.method_10101((class_2382)blockPos);
/* 1752 */       this.ticks = ((Integer)NovaCrystal.this.fadeTime.get()).intValue();
/*      */       
/* 1754 */       return this;
/*      */     }
/*      */     
/*      */     public void tick() {
/* 1758 */       this.ticks--;
/*      */     }
/*      */     
/*      */     public void render(Render3DEvent event, Color sides, Color lines, ShapeMode shapeMode) {
/* 1762 */       int preSideA = sides.a;
/* 1763 */       int preLineA = lines.a;
/*      */       
/* 1765 */       sides.a = (int)(sides.a * this.ticks / ((Integer)NovaCrystal.this.fadeAmount.get()).intValue());
/* 1766 */       lines.a = (int)(lines.a * this.ticks / ((Integer)NovaCrystal.this.fadeAmount.get()).intValue());
/*      */       
/* 1768 */       event.renderer.box((class_2338)this.pos, sides, lines, shapeMode, 0);
/*      */       
/* 1770 */       sides.a = preSideA;
/* 1771 */       lines.a = preLineA;
/*      */     } }
/*      */ 
/*      */   
/*      */   @EventHandler
/*      */   private void onRender2D(Render2DEvent event) {
/* 1777 */     if (this.renderMode.get() == RenderMode.None || this.renderTimer <= 0 || !((Boolean)this.renderDamage.get()).booleanValue())
/*      */       return; 
/* 1779 */     this.vec3.set(this.renderPos.method_10263() + 0.5D, this.renderPos.method_10264() + 0.5D, this.renderPos.method_10260() + 0.5D);
/*      */     
/* 1781 */     if (NametagUtils.to2D(this.vec3, ((Double)this.damageScale.get()).doubleValue())) {
/* 1782 */       NametagUtils.begin(this.vec3);
/* 1783 */       TextRenderer.get().begin(1.0D, false, true);
/*      */       
/* 1785 */       String text = String.format("%.1f", new Object[] { Double.valueOf(this.damage) });
/* 1786 */       double w = TextRenderer.get().getWidth(text) * 0.5D;
/* 1787 */       TextRenderer.get().render(text, -w, 0.0D, (Color)this.damageColor.get(), true);
/*      */       
/* 1789 */       TextRenderer.get().end();
/* 1790 */       NametagUtils.end();
/*      */     } 
/*      */   }
/*      */   
/*      */   public class_1657 getPlayerTarget() {
/* 1795 */     if (this.bestTarget != null) {
/* 1796 */       return this.bestTarget;
/*      */     }
/* 1798 */     return null;
/*      */   }
/*      */ }


/* Location:              C:\Users\Shotgun\OneDrive\Desktop\Nova-v-100000 (1).jar!\Nova\modules\combat\NovaCrystal.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */