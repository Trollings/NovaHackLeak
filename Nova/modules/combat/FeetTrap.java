/*     */ package Nova.modules.combat;
/*     */ import Nova.util.NWorldUtils;
/*     */ import Nova.util.PositionUtils;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import meteordevelopment.meteorclient.events.packets.PacketEvent;
/*     */ import meteordevelopment.meteorclient.events.render.Render3DEvent;
/*     */ import meteordevelopment.meteorclient.renderer.ShapeMode;
/*     */ import meteordevelopment.meteorclient.settings.BlockListSetting;
/*     */ import meteordevelopment.meteorclient.settings.BoolSetting;
/*     */ import meteordevelopment.meteorclient.settings.ColorSetting;
/*     */ import meteordevelopment.meteorclient.settings.EnumSetting;
/*     */ import meteordevelopment.meteorclient.settings.IntSetting;
/*     */ import meteordevelopment.meteorclient.settings.KeybindSetting;
/*     */ import meteordevelopment.meteorclient.settings.ModuleListSetting;
/*     */ import meteordevelopment.meteorclient.settings.Setting;
/*     */ import meteordevelopment.meteorclient.settings.SettingGroup;
/*     */ import meteordevelopment.meteorclient.systems.modules.Module;
/*     */ import meteordevelopment.meteorclient.utils.misc.Keybind;
/*     */ import meteordevelopment.meteorclient.utils.misc.Pool;
/*     */ import meteordevelopment.meteorclient.utils.player.InvUtils;
/*     */ import meteordevelopment.meteorclient.utils.player.PlayerUtils;
/*     */ import meteordevelopment.meteorclient.utils.render.color.Color;
/*     */ import meteordevelopment.meteorclient.utils.render.color.SettingColor;
/*     */ import meteordevelopment.orbit.EventHandler;
/*     */ import net.minecraft.class_1657;
/*     */ import net.minecraft.class_1799;
/*     */ import net.minecraft.class_2246;
/*     */ import net.minecraft.class_2248;
/*     */ import net.minecraft.class_2338;
/*     */ import net.minecraft.class_2350;
/*     */ import net.minecraft.class_2596;
/*     */ import net.minecraft.class_2620;
/*     */ import net.minecraft.class_2680;
/*     */ import net.minecraft.class_5892;
/*     */ 
/*     */ public class FeetTrap extends Module {
/*     */   public enum Mode {
/*  42 */     Normal,
/*  43 */     Russian,
/*  44 */     Autist;
/*     */   }
/*     */   
/*     */   public enum CenterMode {
/*  48 */     Center,
/*  49 */     Snap,
/*  50 */     None;
/*     */   }
/*     */   
/*     */   public enum AntiCityMode {
/*  54 */     Smart,
/*  55 */     All,
/*  56 */     None;
/*     */   }
/*     */   
/*     */   public enum AntiCityShape {
/*  60 */     Russian,
/*  61 */     Autist;
/*     */   }
/*     */   
/*     */   public enum RenderMode {
/*  65 */     None,
/*  66 */     Normal,
/*  67 */     Place,
/*  68 */     Both;
/*     */   }
/*     */   
/*  71 */   private final SettingGroup sgGeneral = this.settings.getDefaultGroup();
/*  72 */   private final SettingGroup sgPlacing = this.settings.createGroup("Placing");
/*  73 */   private final SettingGroup sgAntiCity = this.settings.createGroup("Anti City");
/*  74 */   private final SettingGroup sgForce = this.settings.createGroup("Force Keybinds");
/*  75 */   private final SettingGroup sgToggle = this.settings.createGroup("Toggle Modes");
/*  76 */   private final SettingGroup sgRender = this.settings.createGroup("Render");
/*     */ 
/*     */ 
/*     */   
/*  80 */   private final Setting<List<class_2248>> blocks = this.sgGeneral.add((Setting)((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder())
/*  81 */       .name("primary-blocks"))
/*  82 */       .description("What blocks to use for Surround+."))
/*  83 */       .defaultValue(new class_2248[] { class_2246.field_10540
/*  84 */         }).filter(this::blockFilter)
/*  85 */       .build());
/*     */ 
/*     */   
/*  88 */   private final Setting<List<class_2248>> fallbackBlocks = this.sgGeneral.add((Setting)((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder())
/*  89 */       .name("fallback-blocks"))
/*  90 */       .description("What blocks to use for Surround+ if no target block is found."))
/*  91 */       .defaultValue(new class_2248[] { class_2246.field_10443
/*  92 */         }).filter(this::blockFilter)
/*  93 */       .build());
/*     */ 
/*     */   
/*  96 */   private final Setting<Integer> delay = this.sgGeneral.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder())
/*  97 */       .name("delay"))
/*  98 */       .description("Tick delay between block placements."))
/*  99 */       .defaultValue(Integer.valueOf(1)))
/* 100 */       .range(0, 20)
/* 101 */       .sliderRange(0, 20)
/* 102 */       .build());
/*     */ 
/*     */   
/* 105 */   private final Setting<Integer> blocksPerTick = this.sgGeneral.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder())
/* 106 */       .name("blocks-per-tick"))
/* 107 */       .description("Blocks placed per tick."))
/* 108 */       .defaultValue(Integer.valueOf(4)))
/* 109 */       .range(1, 5)
/* 110 */       .sliderRange(1, 5)
/* 111 */       .build());
/*     */ 
/*     */   
/* 114 */   private final Setting<Mode> mode = this.sgGeneral.add((Setting)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder())
/* 115 */       .name("layout"))
/* 116 */       .description("Where Banana+ should place blocks."))
/* 117 */       .defaultValue(Mode.Normal))
/* 118 */       .build());
/*     */ 
/*     */   
/* 121 */   private final Setting<CenterMode> centerMode = this.sgGeneral.add((Setting)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder())
/* 122 */       .name("center"))
/* 123 */       .description("How Surround+ should center you."))
/* 124 */       .defaultValue(CenterMode.Center))
/* 125 */       .build());
/*     */ 
/*     */   
/* 128 */   private final Setting<Boolean> dynamic = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder())
/* 129 */       .name("dynamic"))
/* 130 */       .description("Will check for your hitbox to find placing positions."))
/* 131 */       .defaultValue(Boolean.valueOf(false)))
/* 132 */       .visible(() -> (this.centerMode.get() == CenterMode.None)))
/* 133 */       .build());
/*     */ 
/*     */   
/* 136 */   private final Setting<Boolean> onlyGround = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder())
/* 137 */       .name("only-on-ground"))
/* 138 */       .description("Will only try to place if you are on the ground."))
/* 139 */       .defaultValue(Boolean.valueOf(false)))
/* 140 */       .build());
/*     */ 
/*     */   
/* 143 */   private final Setting<Boolean> toggleModules = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder())
/* 144 */       .name("toggle-modules"))
/* 145 */       .description("Turn off other modules when surround is activated."))
/* 146 */       .defaultValue(Boolean.valueOf(false)))
/* 147 */       .build());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<Boolean> toggleBack;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<List<Module>> modules;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<NWorldUtils.SwitchMode> switchMode;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<Boolean> switchBack;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<NWorldUtils.PlaceMode> placeMode;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<Boolean> ignoreEntity;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<Boolean> airPlace;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<Boolean> onlyAirPlace;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<NWorldUtils.AirPlaceDirection> airPlaceDirection;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<Boolean> rotate;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<Integer> rotationPrio;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<Boolean> notifyBreak;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<AntiCityMode> antiCityMode;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<AntiCityShape> antiCityShape;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<Keybind> russianKeybind;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<Keybind> russianPlusKeybind;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<Keybind> centerKeybind;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<Boolean> toggleOnYChange;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<Boolean> toggleOnComplete;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<Boolean> toggleOnPearl;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<Boolean> toggleOnChorus;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<Boolean> toggleOnDeath;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<Boolean> renderSwing;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<ShapeMode> shapeMode;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<Boolean> renderPlace;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<SettingColor> placeSideColor;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<SettingColor> placeLineColor;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<Integer> renderTime;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<Integer> fadeAmount;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<Boolean> renderActive;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<SettingColor> safeSideColor;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<SettingColor> safeLineColor;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<SettingColor> normalSideColor;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<SettingColor> normalLineColor;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<SettingColor> unsafeSideColor;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<SettingColor> unsafeLineColor;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private class_2338 playerPos;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int ticksPassed;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int blocksPlaced;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean centered;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private class_2338 prevBreakPos;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   class_1657 prevBreakingPlayer;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean shouldRussianNorth;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean shouldRussianEast;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean shouldRussianSouth;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean shouldRussianWest;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean shouldRussianPlusNorth;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean shouldRussianPlusEast;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean shouldRussianPlusSouth;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean shouldRussianPlusWest;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArrayList<Module> toActivate;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final class_2338.class_2339 renderPos;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Pool<RenderBlock> renderBlockPool;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final List<RenderBlock> renderBlocks;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FeetTrap() {
/* 433 */     super(NovaAddon.nova, "Feet-Trap", "Trap u in obi"); Objects.requireNonNull(this.toggleModules); this.toggleBack = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("toggle-back-on")).description("Turn the other modules back on when surround is deactivated.")).defaultValue(Boolean.valueOf(false))).visible(this.toggleModules::get)).build()); Objects.requireNonNull(this.toggleModules); this.modules = this.sgGeneral.add((Setting)((ModuleListSetting.Builder)((ModuleListSetting.Builder)((ModuleListSetting.Builder)(new ModuleListSetting.Builder()).name("modules")).description("Which modules to disable on activation.")).visible(this.toggleModules::get)).build()); this.switchMode = this.sgPlacing.add((Setting)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("switch-mode")).description("How to switch to your target block.")).defaultValue(NWorldUtils.SwitchMode.Both)).build()); this.switchBack = this.sgPlacing.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("switch-back")).description("Switches back to your original slot after placing.")).defaultValue(Boolean.valueOf(true))).build()); this.placeMode = this.sgPlacing.add((Setting)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("place-mode")).description("How to switch to your target block.")).defaultValue(NWorldUtils.PlaceMode.Both)).build()); this.ignoreEntity = this.sgPlacing.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-entities")).description("Will try to place even if there is an entity in the way.")).defaultValue(Boolean.valueOf(false))).build()); this.airPlace = this.sgPlacing.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("air-place")).description("Whether to place blocks mid air or not.")).defaultValue(Boolean.valueOf(true))).build()); Objects.requireNonNull(this.airPlace); this.onlyAirPlace = this.sgPlacing.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-air-place")).description("Forces you to only airplace to help with stricter rotations.")).defaultValue(Boolean.valueOf(false))).visible(this.airPlace::get)).build()); Objects.requireNonNull(this.airPlace); this.airPlaceDirection = this.sgPlacing.add((Setting)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("place-direction")).description("Side to try to place at when you are trying to air place.")).defaultValue(NWorldUtils.AirPlaceDirection.Up)).visible(this.airPlace::get)).build()); this.rotate = this.sgPlacing.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Whether to face towards the block you are placing or not.")).defaultValue(Boolean.valueOf(false))).build()); Objects.requireNonNull(this.rotate); this.rotationPrio = this.sgPlacing.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("rotation-priority")).description("Rotation priority for Surround+.")).defaultValue(Integer.valueOf(100))).sliderRange(0, 200).visible(this.rotate::get)).build()); this.notifyBreak = this.sgAntiCity.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("notify-break")).description("Notifies you when someone is mining your surround.")).defaultValue(Boolean.valueOf(false))).build()); this.antiCityMode = this.sgAntiCity.add((Setting)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("anti-city-mode")).description("Behaviour of anti city.")).defaultValue(AntiCityMode.None)).build()); this.antiCityShape = this.sgAntiCity.add((Setting)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("anti-city-shape")).description("Shape mode to use for anti city.")).defaultValue(AntiCityShape.Russian)).visible(() -> (this.antiCityMode.get() != AntiCityMode.None))).build()); this.russianKeybind = this.sgForce.add((Setting)((KeybindSetting.Builder)((KeybindSetting.Builder)((KeybindSetting.Builder)(new KeybindSetting.Builder()).name("russian-keybind")).description("Turns on Russian surround when held.")).defaultValue(Keybind.none())).build()); this.russianPlusKeybind = this.sgForce.add((Setting)((KeybindSetting.Builder)((KeybindSetting.Builder)((KeybindSetting.Builder)(new KeybindSetting.Builder()).name("russian+-keybind")).description("Turns on Russian+ when held")).defaultValue(Keybind.none())).build()); this.centerKeybind = this.sgForce.add((Setting)((KeybindSetting.Builder)((KeybindSetting.Builder)((KeybindSetting.Builder)(new KeybindSetting.Builder()).name("center-keybind")).description("Re-center you when held.")).defaultValue(Keybind.none())).build()); this.toggleOnYChange = this.sgToggle.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("toggle-on-y-change")).description("Automatically disables when your Y level changes.")).defaultValue(Boolean.valueOf(true))).build()); this.toggleOnComplete = this.sgToggle.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("toggle-on-complete")).description("Automatically disables when all blocks are placed.")).defaultValue(Boolean.valueOf(false))).build()); this.toggleOnPearl = this.sgToggle.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("toggle-on-pearl")).description("Automatically disables when you throw a pearl (work if u use middle/bind click extra).")).defaultValue(Boolean.valueOf(true))).build()); this.toggleOnChorus = this.sgToggle.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("toggle-on-chorus")).description("Automatically disables after you eat a chorus.")).defaultValue(Boolean.valueOf(true))).build()); this.toggleOnDeath = this.sgToggle.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("toggle-on-death")).description("Automatically disables after you die.")).defaultValue(Boolean.valueOf(false))).build()); this.renderSwing = this.sgRender.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render-swing")).description("Renders hand swing when trying to place a block.")).defaultValue(Boolean.valueOf(true))).build()); this.shapeMode = this.sgRender.add((Setting)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Lines)).build()); this.renderPlace = this.sgRender.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render-place")).description("Will render where it is trying to place.")).defaultValue(Boolean.valueOf(true))).build()); this.placeSideColor = this.sgRender.add((Setting)((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("place-side-color")).description("The color of placing blocks.")).defaultValue(new SettingColor(255, 255, 255, 25))).visible(() -> (((Boolean)this.renderPlace.get()).booleanValue() && this.shapeMode.get() != ShapeMode.Lines))).build()); this.placeLineColor = this.sgRender.add((Setting)((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("place-line-color")).description("The color of placing line.")).defaultValue(new SettingColor(255, 255, 255, 150))).visible(() -> (((Boolean)this.renderPlace.get()).booleanValue() && this.shapeMode.get() != ShapeMode.Sides))).build());
/*     */     Objects.requireNonNull(this.renderPlace);
/*     */     this.renderTime = this.sgRender.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("render-time")).description("Tick duration for rendering placing.")).defaultValue(Integer.valueOf(8))).range(0, 20).sliderRange(0, 20).visible(this.renderPlace::get)).build());
/*     */     Objects.requireNonNull(this.renderPlace);
/*     */     this.fadeAmount = this.sgRender.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("fade-amount")).description("How long in ticks to fade out.")).defaultValue(Integer.valueOf(8))).range(0, 20).sliderRange(0, 20).visible(this.renderPlace::get)).build());
/*     */     this.renderActive = this.sgRender.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render-active")).description("Renders blocks that are being surrounded.")).defaultValue(Boolean.valueOf(true))).build());
/*     */     this.safeSideColor = this.sgRender.add((Setting)((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("safe-side-color")).description("The side color for safe blocks.")).defaultValue(new SettingColor(13, 255, 0, 15))).visible(() -> (((Boolean)this.renderActive.get()).booleanValue() && this.shapeMode.get() != ShapeMode.Lines))).build());
/*     */     this.safeLineColor = this.sgRender.add((Setting)((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("safe-line-color")).description("The line color for safe blocks.")).visible(() -> (((Boolean)this.renderActive.get()).booleanValue() && this.shapeMode.get() != ShapeMode.Sides))).build());
/*     */     this.normalSideColor = this.sgRender.add((Setting)((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("normal-side-color")).description("The side color for normal blocks.")).defaultValue(new SettingColor(0, 255, 238, 15))).visible(() -> (((Boolean)this.renderActive.get()).booleanValue() && this.shapeMode.get() != ShapeMode.Lines))).build());
/*     */     this.normalLineColor = this.sgRender.add((Setting)((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("normal-line-color")).description("The line color for normal blocks.")).defaultValue(new SettingColor(0, 255, 238, 125))).visible(() -> (((Boolean)this.renderActive.get()).booleanValue() && this.shapeMode.get() != ShapeMode.Sides))).build());
/*     */     this.unsafeSideColor = this.sgRender.add((Setting)((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("unsafe-side-color")).description("The side color for unsafe blocks.")).defaultValue(new SettingColor(204, 0, 0, 15))).visible(() -> (((Boolean)this.renderActive.get()).booleanValue() && this.shapeMode.get() != ShapeMode.Lines))).build());
/*     */     this.unsafeLineColor = this.sgRender.add((Setting)((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("unsafe-line-color")).description("The line color for unsafe blocks.")).defaultValue(new SettingColor(204, 0, 0, 125))).visible(() -> (((Boolean)this.renderActive.get()).booleanValue() && this.shapeMode.get() != ShapeMode.Sides))).build());
/* 445 */     this.prevBreakingPlayer = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 459 */     this.renderPos = new class_2338.class_2339();
/*     */     
/* 461 */     this.renderBlockPool = new Pool(() -> new RenderBlock());
/* 462 */     this.renderBlocks = new ArrayList<>();
/*     */   }
/*     */ 
/*     */   
/*     */   public void onActivate() {
/* 467 */     this.ticksPassed = 0;
/* 468 */     this.blocksPlaced = 0;
/*     */     
/* 470 */     this.centered = false;
/* 471 */     this.playerPos = NEntityUtils.playerPos((class_1657)this.mc.field_1724);
/*     */     
/* 473 */     this.toActivate = new ArrayList<>();
/*     */     
/* 475 */     if (this.centerMode.get() != CenterMode.None) {
/* 476 */       if (this.centerMode.get() == CenterMode.Snap) { NWorldUtils.snapPlayer(this.playerPos); }
/* 477 */       else { PlayerUtils.centerPlayer(); }
/*     */     
/*     */     }
/* 480 */     if (((Boolean)this.toggleModules.get()).booleanValue() && !((List)this.modules.get()).isEmpty() && this.mc.field_1687 != null && this.mc.field_1724 != null) {
/* 481 */       for (Module module : this.modules.get()) {
/* 482 */         if (module.isActive()) {
/* 483 */           module.toggle();
/* 484 */           this.toActivate.add(module);
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/* 489 */     for (RenderBlock renderBlock : this.renderBlocks) this.renderBlockPool.free(renderBlock); 
/* 490 */     this.renderBlocks.clear();
/*     */   }
/*     */ 
/*     */   
/*     */   public void onDeactivate() {
/* 495 */     if (((Boolean)this.toggleBack.get()).booleanValue() && !this.toActivate.isEmpty() && this.mc.field_1687 != null && this.mc.field_1724 != null) {
/* 496 */       for (Module module : this.toActivate) {
/* 497 */         if (!module.isActive()) {
/* 498 */           module.toggle();
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/* 503 */     this.shouldRussianNorth = false;
/* 504 */     this.shouldRussianEast = false;
/* 505 */     this.shouldRussianSouth = false;
/* 506 */     this.shouldRussianWest = false;
/*     */     
/* 508 */     this.shouldRussianPlusNorth = false;
/* 509 */     this.shouldRussianPlusEast = false;
/* 510 */     this.shouldRussianPlusSouth = false;
/* 511 */     this.shouldRussianPlusWest = false;
/*     */     
/* 513 */     for (RenderBlock renderBlock : this.renderBlocks) this.renderBlockPool.free(renderBlock); 
/* 514 */     this.renderBlocks.clear();
/*     */   }
/*     */ 
/*     */   
/*     */   @EventHandler
/*     */   public void onTick(TickEvent.Pre event) {
/* 520 */     if (this.ticksPassed >= 0) { this.ticksPassed--; }
/*     */     else
/* 522 */     { this.ticksPassed = ((Integer)this.delay.get()).intValue();
/* 523 */       this.blocksPlaced = 0; }
/*     */ 
/*     */ 
/*     */     
/* 527 */     this.playerPos = NEntityUtils.playerPos((class_1657)this.mc.field_1724);
/*     */     
/* 529 */     if (this.centerMode.get() != CenterMode.None && !this.centered && this.mc.field_1724.method_24828()) {
/* 530 */       if (this.centerMode.get() == CenterMode.Snap) { NWorldUtils.snapPlayer(this.playerPos); }
/* 531 */       else { PlayerUtils.centerPlayer(); }
/*     */       
/* 533 */       this.centered = true;
/*     */     } 
/*     */ 
/*     */     
/* 537 */     if (!this.mc.field_1724.method_24828()) this.centered = false;
/*     */     
/* 539 */     if (((Boolean)this.toggleOnYChange.get()).booleanValue() && 
/* 540 */       this.mc.field_1724.field_6036 < this.mc.field_1724.method_23318()) {
/* 541 */       toggle();
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 546 */     if (((Boolean)this.toggleOnComplete.get()).booleanValue() && 
/* 547 */       PositionUtils.allPlaced(placePos())) {
/* 548 */       toggle();
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 553 */     if (((Boolean)this.onlyGround.get()).booleanValue() && !this.mc.field_1724.method_24828())
/*     */       return; 
/* 555 */     if (!getTargetBlock().found())
/*     */       return; 
/* 557 */     if (this.ticksPassed <= 0) {
/* 558 */       for (class_2338 pos : centerPos()) {
/* 559 */         if (this.blocksPlaced >= ((Integer)this.blocksPerTick.get()).intValue())
/* 560 */           return;  if (NWorldUtils.place(pos, getTargetBlock(), ((Boolean)this.rotate.get()).booleanValue(), ((Integer)this.rotationPrio.get()).intValue(), (NWorldUtils.SwitchMode)this.switchMode.get(), (NWorldUtils.PlaceMode)this.placeMode.get(), ((Boolean)this.onlyAirPlace.get()).booleanValue(), (NWorldUtils.AirPlaceDirection)this.airPlaceDirection.get(), ((Boolean)this.renderSwing.get()).booleanValue(), !((Boolean)this.ignoreEntity.get()).booleanValue(), ((Boolean)this.switchBack.get()).booleanValue())) {
/* 561 */           this.renderBlocks.add(((RenderBlock)this.renderBlockPool.get()).set(pos));
/* 562 */           this.blocksPlaced++;
/*     */         } 
/*     */       } 
/*     */       
/* 566 */       for (class_2338 pos : extraPos()) {
/* 567 */         if (this.blocksPlaced >= ((Integer)this.blocksPerTick.get()).intValue())
/* 568 */           return;  if (NWorldUtils.place(pos, getTargetBlock(), ((Boolean)this.rotate.get()).booleanValue(), ((Integer)this.rotationPrio.get()).intValue(), (NWorldUtils.SwitchMode)this.switchMode.get(), (NWorldUtils.PlaceMode)this.placeMode.get(), ((Boolean)this.onlyAirPlace.get()).booleanValue(), (NWorldUtils.AirPlaceDirection)this.airPlaceDirection.get(), ((Boolean)this.renderSwing.get()).booleanValue(), true, ((Boolean)this.switchBack.get()).booleanValue())) {
/* 569 */           this.renderBlocks.add(((RenderBlock)this.renderBlockPool.get()).set(pos));
/* 570 */           this.blocksPlaced++;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 576 */     this.renderBlocks.forEach(RenderBlock::tick);
/* 577 */     this.renderBlocks.removeIf(renderBlock -> (renderBlock.ticks <= 0));
/*     */   }
/*     */   
/*     */   @EventHandler
/*     */   public void onTick(TickEvent.Post event) {
/* 582 */     if (((Keybind)this.centerKeybind.get()).isPressed()) {
/* 583 */       if (this.centerMode.get() == CenterMode.Snap) { NWorldUtils.snapPlayer(this.playerPos); }
/* 584 */       else { PlayerUtils.centerPlayer(); }
/*     */     
/*     */     }
/*     */   }
/*     */   
/*     */   private List<class_2338> placePos() {
/* 590 */     List<class_2338> pos = new ArrayList<>();
/*     */ 
/*     */     
/* 593 */     for (class_2338 centerPos : centerPos()) add(pos, centerPos);
/*     */     
/* 595 */     for (class_2338 extraPos : extraPos()) add(pos, extraPos);
/*     */     
/* 597 */     return pos;
/*     */   }
/*     */ 
/*     */   
/*     */   private List<class_2338> centerPos() {
/* 602 */     List<class_2338> pos = new ArrayList<>();
/*     */     
/* 604 */     if (!((Boolean)this.dynamic.get()).booleanValue()) {
/* 605 */       add(pos, this.playerPos.method_10074());
/* 606 */       add(pos, this.playerPos.method_10095());
/* 607 */       add(pos, this.playerPos.method_10078());
/* 608 */       add(pos, this.playerPos.method_10072());
/* 609 */       add(pos, this.playerPos.method_10067());
/*     */     } else {
/*     */       
/* 612 */       for (class_2338 dynamicBottomPos : PositionUtils.dynamicBottomPos((class_1657)this.mc.field_1724, false)) {
/* 613 */         if (PositionUtils.dynamicBottomPos((class_1657)this.mc.field_1724, false).contains(dynamicBottomPos)) pos.remove(dynamicBottomPos); 
/* 614 */         add(pos, dynamicBottomPos);
/*     */       } 
/*     */ 
/*     */       
/* 618 */       for (class_2338 dynamicFeetPos : PositionUtils.dynamicFeetPos((class_1657)this.mc.field_1724, false)) {
/* 619 */         if (PositionUtils.dynamicFeetPos((class_1657)this.mc.field_1724, false).contains(dynamicFeetPos)) pos.remove(dynamicFeetPos); 
/* 620 */         add(pos, dynamicFeetPos);
/*     */       } 
/*     */     } 
/*     */     
/* 624 */     return pos;
/*     */   }
/*     */ 
/*     */   
/*     */   private List<class_2338> extraPos() {
/* 629 */     List<class_2338> pos = new ArrayList<>();
/*     */ 
/*     */     
/* 632 */     if ((this.mode.get() != Mode.Normal || ((Keybind)this.russianKeybind.get()).isPressed() || ((Keybind)this.russianPlusKeybind.get()).isPressed() || this.shouldRussianNorth || this.shouldRussianPlusNorth) && 
/* 633 */       this.mc.field_1687.method_8320(this.playerPos.method_10095()).method_26204() != class_2246.field_9987) {
/* 634 */       if (!((Boolean)this.dynamic.get()).booleanValue()) { add(pos, this.playerPos.method_10076(2)); }
/*     */       else
/* 636 */       { for (class_2338 plusPos : PositionUtils.dynamicRussianNorth((class_1657)this.mc.field_1724, false)) add(pos, plusPos);
/*     */          }
/*     */     
/*     */     }
/* 640 */     if ((this.mode.get() == Mode.Autist || ((Keybind)this.russianPlusKeybind.get()).isPressed() || this.shouldRussianPlusNorth) && 
/* 641 */       this.mc.field_1687.method_8320(this.playerPos.method_10095()).method_26204() != class_2246.field_9987) {
/* 642 */       if (!((Boolean)this.dynamic.get()).booleanValue()) {
/* 643 */         add(pos, this.playerPos.method_10095().method_10067());
/* 644 */         add(pos, this.playerPos.method_10095().method_10078());
/*     */       } else {
/* 646 */         for (class_2338 plusPos : PositionUtils.dynamicRussianNorth((class_1657)this.mc.field_1724, true)) add(pos, plusPos);
/*     */       
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 652 */     if ((this.mode.get() != Mode.Normal || ((Keybind)this.russianKeybind.get()).isPressed() || ((Keybind)this.russianPlusKeybind.get()).isPressed() || this.shouldRussianEast || this.shouldRussianPlusEast) && 
/* 653 */       this.mc.field_1687.method_8320(this.playerPos.method_10078()).method_26204() != class_2246.field_9987) {
/* 654 */       if (!((Boolean)this.dynamic.get()).booleanValue()) { add(pos, this.playerPos.method_10089(2)); }
/*     */       else
/* 656 */       { for (class_2338 plusPos : PositionUtils.dynamicRussianEast((class_1657)this.mc.field_1724, false)) add(pos, plusPos);
/*     */          }
/*     */     
/*     */     }
/* 660 */     if ((this.mode.get() == Mode.Autist || ((Keybind)this.russianPlusKeybind.get()).isPressed() || this.shouldRussianPlusEast) && 
/* 661 */       this.mc.field_1687.method_8320(this.playerPos.method_10078()).method_26204() != class_2246.field_9987) {
/* 662 */       if (!((Boolean)this.dynamic.get()).booleanValue()) {
/* 663 */         add(pos, this.playerPos.method_10078().method_10095());
/* 664 */         add(pos, this.playerPos.method_10078().method_10072());
/*     */       } else {
/* 666 */         for (class_2338 plusPos : PositionUtils.dynamicRussianEast((class_1657)this.mc.field_1724, true)) add(pos, plusPos);
/*     */       
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 672 */     if ((this.mode.get() != Mode.Normal || ((Keybind)this.russianKeybind.get()).isPressed() || ((Keybind)this.russianPlusKeybind.get()).isPressed() || this.shouldRussianSouth || this.shouldRussianPlusSouth) && 
/* 673 */       this.mc.field_1687.method_8320(this.playerPos.method_10072()).method_26204() != class_2246.field_9987) {
/* 674 */       if (!((Boolean)this.dynamic.get()).booleanValue()) { add(pos, this.playerPos.method_10077(2)); }
/*     */       else
/* 676 */       { for (class_2338 plusPos : PositionUtils.dynamicRussianSouth((class_1657)this.mc.field_1724, false)) add(pos, plusPos);
/*     */          }
/*     */     
/*     */     }
/* 680 */     if ((this.mode.get() == Mode.Autist || ((Keybind)this.russianPlusKeybind.get()).isPressed() || this.shouldRussianPlusSouth) && 
/* 681 */       this.mc.field_1687.method_8320(this.playerPos.method_10072()).method_26204() != class_2246.field_9987) {
/* 682 */       if (!((Boolean)this.dynamic.get()).booleanValue()) {
/* 683 */         add(pos, this.playerPos.method_10072().method_10078());
/* 684 */         add(pos, this.playerPos.method_10072().method_10067());
/*     */       } else {
/* 686 */         for (class_2338 plusPos : PositionUtils.dynamicRussianSouth((class_1657)this.mc.field_1724, true)) add(pos, plusPos);
/*     */       
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 692 */     if ((this.mode.get() != Mode.Normal || ((Keybind)this.russianKeybind.get()).isPressed() || ((Keybind)this.russianPlusKeybind.get()).isPressed() || this.shouldRussianWest || this.shouldRussianPlusWest) && 
/* 693 */       this.mc.field_1687.method_8320(this.playerPos.method_10067()).method_26204() != class_2246.field_9987) {
/* 694 */       if (!((Boolean)this.dynamic.get()).booleanValue()) { add(pos, this.playerPos.method_10088(2)); }
/*     */       else
/* 696 */       { for (class_2338 plusPos : PositionUtils.dynamicRussianWest((class_1657)this.mc.field_1724, false)) add(pos, plusPos);
/*     */          }
/*     */     
/*     */     }
/* 700 */     if ((this.mode.get() == Mode.Autist || ((Keybind)this.russianPlusKeybind.get()).isPressed() || this.shouldRussianPlusWest) && 
/* 701 */       this.mc.field_1687.method_8320(this.playerPos.method_10067()).method_26204() != class_2246.field_9987) {
/* 702 */       if (!((Boolean)this.dynamic.get()).booleanValue()) {
/* 703 */         add(pos, this.playerPos.method_10067().method_10072());
/* 704 */         add(pos, this.playerPos.method_10067().method_10095());
/*     */       } else {
/* 706 */         for (class_2338 plusPos : PositionUtils.dynamicRussianWest((class_1657)this.mc.field_1724, true)) add(pos, plusPos);
/*     */       
/*     */       } 
/*     */     }
/*     */     
/* 711 */     return pos;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void add(List<class_2338> list, class_2338 pos) {
/* 717 */     if (this.mc.field_1687.method_8320(pos).method_26215() && 
/* 718 */       allAir(new class_2338[] { pos.method_10095(), pos.method_10078(), pos.method_10072(), pos.method_10067(), pos.method_10084(), pos.method_10074()
/* 719 */         }) && !((Boolean)this.airPlace.get()).booleanValue())
/* 720 */       list.add(pos.method_10074()); 
/* 721 */     list.add(pos);
/*     */   }
/*     */   
/*     */   private boolean allAir(class_2338... pos) {
/* 725 */     return Arrays.<class_2338>stream(pos).allMatch(blockPos -> this.mc.field_1687.method_8320(blockPos).method_26207().method_15800());
/*     */   }
/*     */   
/*     */   private boolean anyAir(class_2338... pos) {
/* 729 */     return Arrays.<class_2338>stream(pos).anyMatch(blockPos -> this.mc.field_1687.method_8320(blockPos).method_26207().method_15800());
/*     */   }
/*     */   
/*     */   private FindItemResult getTargetBlock() {
/* 733 */     if (!InvUtils.findInHotbar(itemStack -> ((List)this.blocks.get()).contains(class_2248.method_9503(itemStack.method_7909()))).found())
/* 734 */       return InvUtils.findInHotbar(itemStack -> ((List)this.fallbackBlocks.get()).contains(class_2248.method_9503(itemStack.method_7909()))); 
/* 735 */     return InvUtils.findInHotbar(itemStack -> ((List)this.blocks.get()).contains(class_2248.method_9503(itemStack.method_7909())));
/*     */   }
/*     */   
/*     */   private boolean blockFilter(class_2248 block) {
/* 739 */     return (block == class_2246.field_10540 || block == class_2246.field_22423 || block == class_2246.field_22109 || block == class_2246.field_22108 || block == class_2246.field_10443 || block == class_2246.field_23152 || block == class_2246.field_10535 || block == class_2246.field_10105 || block == class_2246.field_10414 || block == class_2246.field_10485);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @EventHandler
/*     */   public void onBreakPacket(PacketEvent.Receive event) {
/*     */     class_2620 bbpp;
/* 753 */     class_2596 class_2596 = event.packet; if (class_2596 instanceof class_2620) { bbpp = (class_2620)class_2596; } else { return; }
/* 754 */      class_2338 bbp = bbpp.method_11277();
/*     */     
/* 756 */     class_1657 breakingPlayer = (class_1657)this.mc.field_1687.method_8469(bbpp.method_11280());
/* 757 */     class_2338 playerBlockPos = this.mc.field_1724.method_24515();
/*     */     
/* 759 */     if (bbpp.method_11278() > 0)
/* 760 */       return;  if (bbp.equals(this.prevBreakPos)) {
/*     */       return;
/*     */     }
/* 763 */     if (breakingPlayer.equals(this.mc.field_1724))
/*     */       return; 
/* 765 */     if (bbp.equals(centerPos()) && 
/* 766 */       this.antiCityMode.get() == AntiCityMode.All) {
/* 767 */       if (this.antiCityShape.get() == AntiCityShape.Russian) {
/* 768 */         this.shouldRussianNorth = true;
/* 769 */         this.shouldRussianEast = true;
/* 770 */         this.shouldRussianSouth = true;
/* 771 */         this.shouldRussianWest = true;
/*     */       } else {
/* 773 */         this.shouldRussianPlusNorth = true;
/* 774 */         this.shouldRussianPlusEast = true;
/* 775 */         this.shouldRussianPlusSouth = true;
/* 776 */         this.shouldRussianPlusWest = true;
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 783 */     if (bbp.equals(playerBlockPos.method_10095())) {
/* 784 */       if (this.antiCityMode.get() == AntiCityMode.Smart)
/* 785 */         if (this.antiCityShape.get() == AntiCityShape.Russian) { this.shouldRussianNorth = true; }
/* 786 */         else { this.shouldRussianPlusNorth = true; }
/*     */          
/* 788 */       if (((Boolean)this.notifyBreak.get()).booleanValue()) notifySurroundBreak(class_2350.field_11043, breakingPlayer);
/*     */     
/*     */     } 
/* 791 */     if (bbp.equals(playerBlockPos.method_10078())) {
/* 792 */       if (this.antiCityMode.get() == AntiCityMode.Smart)
/* 793 */         if (this.antiCityShape.get() == AntiCityShape.Russian) { this.shouldRussianEast = true; }
/* 794 */         else { this.shouldRussianPlusEast = true; }
/*     */          
/* 796 */       if (((Boolean)this.notifyBreak.get()).booleanValue()) notifySurroundBreak(class_2350.field_11034, breakingPlayer);
/*     */     
/*     */     } 
/* 799 */     if (bbp.equals(playerBlockPos.method_10072())) {
/* 800 */       if (this.antiCityMode.get() == AntiCityMode.Smart)
/* 801 */         if (this.antiCityShape.get() == AntiCityShape.Russian) { this.shouldRussianSouth = true; }
/* 802 */         else { this.shouldRussianPlusSouth = true; }
/*     */          
/* 804 */       if (((Boolean)this.notifyBreak.get()).booleanValue()) notifySurroundBreak(class_2350.field_11035, breakingPlayer);
/*     */     
/*     */     } 
/* 807 */     if (bbp.equals(playerBlockPos.method_10067())) {
/* 808 */       if (this.antiCityMode.get() == AntiCityMode.Smart)
/* 809 */         if (this.antiCityShape.get() == AntiCityShape.Russian) { this.shouldRussianWest = true; }
/* 810 */         else { this.shouldRussianPlusWest = true; }
/*     */          
/* 812 */       if (((Boolean)this.notifyBreak.get()).booleanValue()) notifySurroundBreak(class_2350.field_11039, breakingPlayer);
/*     */     
/*     */     } 
/* 815 */     this.prevBreakingPlayer = breakingPlayer;
/* 816 */     this.prevBreakPos = bbp;
/*     */   }
/*     */   
/*     */   private void notifySurroundBreak(class_2350 direction, class_1657 player) {
/* 820 */     switch (direction) { case Safe:
/* 821 */         warning("Your north surround block is being broken by " + player.method_5820(), new Object[0]); break;
/* 822 */       case Normal: warning("Your east surround block is being broken by " + player.method_5820(), new Object[0]); break;
/* 823 */       case Unsafe: warning("Your south surround block is being broken by " + player.method_5820(), new Object[0]); break;
/* 824 */       case null: warning("Your west surround block is being broken by " + player.method_5820(), new Object[0]);
/*     */         break; }
/*     */   
/*     */   }
/*     */   
/*     */   @EventHandler
/*     */   private void onPacketReceive(PacketEvent.Receive event) {
/* 831 */     class_2596 class_2596 = event.packet; if (class_2596 instanceof class_5892) { class_5892 packet = (class_5892)class_2596;
/* 832 */       class_1297 entity = this.mc.field_1687.method_8469(packet.method_34144());
/* 833 */       if (entity == this.mc.field_1724 && ((Boolean)this.toggleOnDeath.get()).booleanValue()) {
/* 834 */         toggle();
/*     */       } }
/*     */   
/*     */   }
/*     */   
/*     */   @EventHandler
/*     */   private void onPacketSend(PacketEvent.Send event) {
/* 841 */     if (event.packet instanceof net.minecraft.class_2886 && (this.mc.field_1724.method_6079().method_7909() instanceof net.minecraft.class_1776 || this.mc.field_1724.method_6047().method_7909() instanceof net.minecraft.class_1776) && ((Boolean)this.toggleOnPearl.get()).booleanValue()) {
/* 842 */       toggle();
/*     */     }
/*     */   }
/*     */   
/*     */   @EventHandler
/*     */   private void onFinishUsingItem(FinishUsingItemEvent event) {
/* 848 */     if (event.itemStack.method_7909() instanceof net.minecraft.class_1757 && ((Boolean)this.toggleOnChorus.get()).booleanValue()) {
/* 849 */       toggle();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   @EventHandler
/*     */   private void onRender(Render3DEvent event) {
/* 856 */     for (Iterator<class_2338> iterator = placePos().iterator(); iterator.hasNext(); ) { class_2338 pos = iterator.next();
/* 857 */       this.renderPos.method_10101((class_2382)pos);
/* 858 */       Color color = getBlockColor((class_2338)this.renderPos);
/* 859 */       Color lineColor = getLineColor((class_2338)this.renderPos);
/*     */       
/* 861 */       if (((Boolean)this.renderActive.get()).booleanValue()) event.renderer.box((class_2338)this.renderPos, color, lineColor, (ShapeMode)this.shapeMode.get(), 0);
/*     */       
/* 863 */       if (((Boolean)this.renderPlace.get()).booleanValue()) {
/* 864 */         this.renderBlocks.sort(Comparator.comparingInt(o -> -o.ticks));
/* 865 */         this.renderBlocks.forEach(renderBlock -> renderBlock.render(event, (Color)this.placeSideColor.get(), (Color)this.placeLineColor.get(), (ShapeMode)this.shapeMode.get()));
/*     */       }  }
/*     */   
/*     */   }
/*     */   public class RenderBlock { public class_2338.class_2339 pos; public int ticks;
/*     */     public RenderBlock() {
/* 871 */       this.pos = new class_2338.class_2339();
/*     */     }
/*     */     
/*     */     public RenderBlock set(class_2338 blockPos) {
/* 875 */       this.pos.method_10101((class_2382)blockPos);
/* 876 */       this.ticks = ((Integer)FeetTrap.this.renderTime.get()).intValue();
/*     */       
/* 878 */       return this;
/*     */     }
/*     */     
/*     */     public void tick() {
/* 882 */       this.ticks--;
/*     */     }
/*     */     
/*     */     public void render(Render3DEvent event, Color sides, Color lines, ShapeMode shapeMode) {
/* 886 */       int preSideA = sides.a;
/* 887 */       int preLineA = lines.a;
/*     */       
/* 889 */       sides.a = (int)(sides.a * this.ticks / ((Integer)FeetTrap.this.fadeAmount.get()).intValue());
/* 890 */       lines.a = (int)(lines.a * this.ticks / ((Integer)FeetTrap.this.fadeAmount.get()).intValue());
/*     */       
/* 892 */       event.renderer.box((class_2338)this.pos, sides, lines, shapeMode, 0);
/*     */       
/* 894 */       sides.a = preSideA;
/* 895 */       lines.a = preLineA;
/*     */     } }
/*     */ 
/*     */   
/*     */   private BlockType getBlockType(class_2338 pos) {
/* 900 */     class_2680 blockState = this.mc.field_1687.method_8320(pos);
/*     */     
/* 902 */     if (blockState.method_26204().method_36555() < 0.0F) return BlockType.Safe;
/*     */     
/* 904 */     if (blockState.method_26204().method_9520() >= 600.0F) return BlockType.Normal;
/*     */     
/* 906 */     return BlockType.Unsafe;
/*     */   }
/*     */   
/*     */   private Color getLineColor(class_2338 pos) {
/* 910 */     switch (getBlockType(pos)) { default: throw new IncompatibleClassChangeError();case Safe: case Normal: case Unsafe: break; }  return 
/*     */ 
/*     */       
/* 913 */       (Color)this.unsafeLineColor.get();
/*     */   }
/*     */ 
/*     */   
/*     */   private Color getBlockColor(class_2338 pos) {
/* 918 */     switch (getBlockType(pos)) { default: throw new IncompatibleClassChangeError();case Safe: case Normal: case Unsafe: break; }  return 
/*     */ 
/*     */       
/* 921 */       (Color)this.unsafeSideColor.get();
/*     */   }
/*     */   
/*     */   public enum BlockType
/*     */   {
/* 926 */     Safe,
/* 927 */     Normal,
/* 928 */     Unsafe;
/*     */   }
/*     */ }


/* Location:              C:\Users\Shotgun\OneDrive\Desktop\Nova-v-100000 (1).jar!\Nova\modules\combat\FeetTrap.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */