/*     */ package Nova.modules.misc;
/*     */ import Nova.util.NPlayerUtils;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Random;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
/*     */ import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
/*     */ import meteordevelopment.meteorclient.events.game.GameLeftEvent;
/*     */ import meteordevelopment.meteorclient.events.packets.PacketEvent;
/*     */ import meteordevelopment.meteorclient.events.world.CollisionShapeEvent;
/*     */ import meteordevelopment.meteorclient.events.world.TickEvent;
/*     */ import meteordevelopment.meteorclient.mixin.PlayerPositionLookS2CPacketAccessor;
/*     */ import meteordevelopment.meteorclient.mixininterface.IVec3d;
/*     */ import meteordevelopment.meteorclient.settings.BoolSetting;
/*     */ import meteordevelopment.meteorclient.settings.DoubleSetting;
/*     */ import meteordevelopment.meteorclient.settings.EnumSetting;
/*     */ import meteordevelopment.meteorclient.settings.IntSetting;
/*     */ import meteordevelopment.meteorclient.settings.KeybindSetting;
/*     */ import meteordevelopment.meteorclient.settings.Setting;
/*     */ import meteordevelopment.meteorclient.settings.SettingGroup;
/*     */ import meteordevelopment.meteorclient.systems.modules.Module;
/*     */ import meteordevelopment.meteorclient.systems.modules.Modules;
/*     */ import meteordevelopment.meteorclient.systems.modules.world.Timer;
/*     */ import meteordevelopment.meteorclient.utils.entity.EntityUtils;
/*     */ import meteordevelopment.meteorclient.utils.misc.Keybind;
/*     */ import meteordevelopment.meteorclient.utils.player.ChatUtils;
/*     */ import meteordevelopment.orbit.EventHandler;
/*     */ import net.minecraft.class_1297;
/*     */ import net.minecraft.class_1657;
/*     */ import net.minecraft.class_1934;
/*     */ import net.minecraft.class_243;
/*     */ import net.minecraft.class_2561;
/*     */ import net.minecraft.class_2596;
/*     */ import net.minecraft.class_2708;
/*     */ import net.minecraft.class_2793;
/*     */ import net.minecraft.class_2828;
/*     */ import net.minecraft.class_2848;
/*     */ 
/*     */ public class RocketFly extends Module {
/*     */   public enum Type {
/*  41 */     FACTOR,
/*  42 */     SETBACK,
/*  43 */     FAST,
/*  44 */     SLOW,
/*  45 */     ELYTRA,
/*  46 */     DESYNC,
/*  47 */     VECTOR,
/*  48 */     OFFGROUND,
/*  49 */     ONGROUND;
/*     */   }
/*     */   
/*     */   public enum Mode {
/*  53 */     PRESERVE,
/*  54 */     UP,
/*  55 */     DOWN,
/*  56 */     LIMITJITTER,
/*  57 */     BYPASS,
/*  58 */     OBSCURE;
/*     */   }
/*     */   
/*     */   public enum Bypass {
/*  62 */     NONE,
/*  63 */     DEFAULT,
/*  64 */     NCP;
/*     */   }
/*     */   
/*     */   public enum Phase {
/*  68 */     NONE,
/*  69 */     VANILLA,
/*  70 */     NCP;
/*     */   }
/*     */   
/*     */   public enum AntiKick {
/*  74 */     NONE,
/*  75 */     NORMAL,
/*  76 */     LIMITED,
/*  77 */     STRICT;
/*     */   }
/*     */   
/*     */   public enum Limit {
/*  81 */     NONE,
/*  82 */     STRONG,
/*  83 */     STRICT;
/*     */   }
/*     */ 
/*     */   
/*  87 */   private final SettingGroup sgGeneral = this.settings.getDefaultGroup();
/*  88 */   private final SettingGroup sgFly = this.settings.createGroup("Fly");
/*  89 */   private final SettingGroup sgAntiKick = this.settings.createGroup("Anti Kick");
/*  90 */   private final SettingGroup sgKeybind = this.settings.createGroup("Keybind");
/*  91 */   private final SettingGroup sgPhase = this.settings.createGroup("Phase");
/*     */ 
/*     */ 
/*     */   
/*  95 */   private final Setting<Type> type = this.sgGeneral.add((Setting)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder())
/*  96 */       .name("fly-type"))
/*  97 */       .description("The way you are moved by this module."))
/*  98 */       .defaultValue(Type.FACTOR))
/*  99 */       .onChanged(this::updateFlying))
/* 100 */       .build());
/*     */ 
/*     */   
/* 103 */   private final Setting<Mode> packetMode = this.sgGeneral.add((Setting)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder())
/* 104 */       .name("packet-mode"))
/* 105 */       .description("Which packets to send to the server."))
/* 106 */       .defaultValue(Mode.DOWN))
/* 107 */       .build());
/*     */ 
/*     */   
/* 110 */   private final Setting<Bypass> bypass = this.sgGeneral.add((Setting)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder())
/* 111 */       .name("bypass-mode"))
/* 112 */       .description("What bypass mode to use."))
/* 113 */       .defaultValue(Bypass.NONE))
/* 114 */       .build());
/*     */ 
/*     */   
/* 117 */   private final Setting<Boolean> onlyOnMove = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder())
/* 118 */       .name("only-on-move"))
/* 119 */       .description("Only sends packets if your moving."))
/* 120 */       .defaultValue(Boolean.valueOf(true)))
/* 121 */       .build());
/*     */ 
/*     */   
/* 124 */   private final Setting<Boolean> stopOnGround = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder())
/* 125 */       .name("stop-on-ground"))
/* 126 */       .description("Disables Anti Kick when you are on ground."))
/* 127 */       .defaultValue(Boolean.valueOf(true)))
/* 128 */       .build());
/*     */ 
/*     */   
/* 131 */   private final Setting<Boolean> strict = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder())
/* 132 */       .name("strict"))
/* 133 */       .description("How to handle certain packets."))
/* 134 */       .defaultValue(Boolean.valueOf(false)))
/* 135 */       .build());
/*     */ 
/*     */   
/* 138 */   private final Setting<Boolean> bounds = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder())
/* 139 */       .name("bounds"))
/* 140 */       .description("Bounds for the player."))
/* 141 */       .defaultValue(Boolean.valueOf(true)))
/* 142 */       .build());
/*     */ 
/*     */   
/* 145 */   private final Setting<Boolean> multiAxis = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder())
/* 146 */       .name("multi-axis"))
/* 147 */       .description("Whether or not to phase in every direction."))
/* 148 */       .defaultValue(Boolean.valueOf(true)))
/* 149 */       .build());
/*     */ 
/*     */   
/* 152 */   private final Setting<Boolean> autoToggle = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder())
/* 153 */       .name("toggle"))
/* 154 */       .description("Toggles the module on join and leave."))
/* 155 */       .defaultValue(Boolean.valueOf(true)))
/* 156 */       .build());
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 161 */   private final Setting<Double> factor = this.sgFly.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder())
/* 162 */       .name("factor"))
/* 163 */       .description("Your flight factor."))
/* 164 */       .defaultValue(5.0D)
/* 165 */       .min(0.0D)
/* 166 */       .visible(() -> (this.type.get() == Type.FACTOR || this.type.get() == Type.DESYNC)))
/* 167 */       .build());
/*     */ 
/*     */   
/* 170 */   private final Setting<Integer> ignoreSteps = this.sgFly.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder())
/* 171 */       .name("ignore-steps"))
/* 172 */       .description("How many steps in a row should be ignored."))
/* 173 */       .defaultValue(Integer.valueOf(0)))
/* 174 */       .min(0)
/* 175 */       .visible(() -> (this.type.get() == Type.FACTOR || this.type.get() == Type.DESYNC)))
/* 176 */       .build());
/*     */ 
/*     */   
/* 179 */   private final Setting<Integer> exponent = this.sgFly.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder())
/* 180 */       .name("exponent"))
/* 181 */       .description("How far to go per loop."))
/* 182 */       .defaultValue(Integer.valueOf(1)))
/* 183 */       .min(1)
/* 184 */       .visible(() -> (this.type.get() == Type.FACTOR || this.type.get() == Type.DESYNC)))
/* 185 */       .build());
/*     */ 
/*     */   
/* 188 */   private final Setting<Keybind> factorize = this.sgFly.add((Setting)((KeybindSetting.Builder)((KeybindSetting.Builder)((KeybindSetting.Builder)(new KeybindSetting.Builder())
/* 189 */       .name("factorize"))
/* 190 */       .description("Key to toggle factor mode."))
/* 191 */       .defaultValue(Keybind.fromKey(-1)))
/* 192 */       .build());
/*     */ 
/*     */   
/* 195 */   private final Setting<Boolean> boost = this.sgFly.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder())
/* 196 */       .name("boost"))
/* 197 */       .description("Boost the player."))
/* 198 */       .defaultValue(Boolean.valueOf(false)))
/* 199 */       .build());
/*     */ 
/*     */   
/* 202 */   private final Setting<Double> speed = this.sgFly.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder())
/* 203 */       .name("horizontal-speed"))
/* 204 */       .description("Your flight speed when flying horizontal."))
/* 205 */       .defaultValue(1.0D)
/* 206 */       .min(0.0D)
/* 207 */       .build());
/*     */ 
/*     */   
/* 210 */   private final Setting<Double> vspeed = this.sgFly.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder())
/* 211 */       .name("vertical-speed"))
/* 212 */       .description("Your flight speed when flying up and down."))
/* 213 */       .defaultValue(2.0D)
/* 214 */       .min(0.0D)
/* 215 */       .build());
/*     */ 
/*     */   
/* 218 */   private final Setting<Double> motion = this.sgFly.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder())
/* 219 */       .name("factorize-motion"))
/* 220 */       .description("The motion applied when factorize is pressed."))
/* 221 */       .defaultValue(100.0D)
/* 222 */       .min(0.0D)
/* 223 */       .sliderMin(50.0D)
/* 224 */       .sliderMax(200.0D)
/* 225 */       .visible(() -> (this.type.get() == Type.FACTOR || this.type.get() == Type.DESYNC)))
/* 226 */       .build());
/*     */ 
/*     */   
/*     */   private final Setting<Double> boostTimer;
/*     */ 
/*     */   
/*     */   private final Setting<AntiKick> antiKick;
/*     */ 
/*     */   
/*     */   private final Setting<Limit> limit;
/*     */ 
/*     */   
/*     */   private final Setting<Boolean> constrict;
/*     */ 
/*     */   
/*     */   private final Setting<Boolean> jitter;
/*     */ 
/*     */   
/*     */   private final Setting<Boolean> message;
/*     */ 
/*     */   
/*     */   private final Setting<Keybind> toggleLimit;
/*     */ 
/*     */   
/*     */   private final Setting<Keybind> toggleAntiKick;
/*     */ 
/*     */   
/*     */   private final Setting<Phase> phase;
/*     */ 
/*     */   
/*     */   private final Setting<Boolean> noPhaseSlow;
/*     */ 
/*     */   
/*     */   private final Setting<Boolean> noCollision;
/*     */ 
/*     */   
/*     */   private int teleportId;
/*     */ 
/*     */   
/*     */   private class_2828.class_2829 startingOutOfBoundsPos;
/*     */ 
/*     */   
/*     */   private ArrayList<class_2828> packets;
/*     */ 
/*     */   
/*     */   private Map<Integer, TimeVec3d> posLooks;
/*     */ 
/*     */   
/*     */   private int antiKickTicks;
/*     */ 
/*     */   
/*     */   private int vDelay;
/*     */ 
/*     */   
/*     */   private int hDelay;
/*     */ 
/*     */   
/*     */   private boolean limitStrict;
/*     */ 
/*     */   
/*     */   private int limitTicks;
/*     */ 
/*     */   
/*     */   private int jitterTicks;
/*     */ 
/*     */   
/*     */   private int ticksExisted;
/*     */ 
/*     */   
/*     */   private boolean oddJitter;
/*     */ 
/*     */   
/*     */   private boolean forceAntiKick;
/*     */ 
/*     */   
/*     */   private boolean forceLimit;
/*     */ 
/*     */   
/*     */   double speedX;
/*     */   
/*     */   double speedY;
/*     */   
/*     */   double speedZ;
/*     */   
/*     */   private int factorCounter;
/*     */   
/*     */   private final SystemTimer intervalTimer;
/*     */ 
/*     */   
/*     */   public RocketFly() {
/* 316 */     super(NovaAddon.nova, "Rocket-Fly", "Fly like a rocket");
/*     */ 
/*     */     
/*     */     Objects.requireNonNull(this.boost);
/*     */ 
/*     */     
/*     */     this.boostTimer = this.sgFly.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("boost-timer")).description("The timer for boost.")).defaultValue(1.1D).min(0.0D).visible(this.boost::get)).build());
/*     */ 
/*     */     
/*     */     this.antiKick = this.sgAntiKick.add((Setting)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("anti-kick")).description("The anti kick mode.")).defaultValue(AntiKick.NORMAL)).build());
/*     */ 
/*     */     
/*     */     this.limit = this.sgAntiKick.add((Setting)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("limit")).description("The flight limit.")).defaultValue(Limit.STRICT)).build());
/*     */     
/*     */     this.constrict = this.sgAntiKick.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("constrict")).description("Already send the packets before the tick (only if the limit is none).")).defaultValue(Boolean.valueOf(false))).build());
/*     */     
/*     */     this.jitter = this.sgAntiKick.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("jitter")).description("Randomize the movement.")).defaultValue(Boolean.valueOf(false))).build());
/*     */     
/*     */     this.message = this.sgKeybind.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("keybind-message")).description("Whether or not to send you a message when toggled a mode.")).defaultValue(Boolean.valueOf(true))).build());
/*     */     
/*     */     this.toggleLimit = this.sgKeybind.add((Setting)((KeybindSetting.Builder)((KeybindSetting.Builder)((KeybindSetting.Builder)(new KeybindSetting.Builder()).name("toggle-limit")).description("Key to toggle Limit on or off.")).defaultValue(Keybind.fromKey(-1))).build());
/*     */     
/*     */     this.toggleAntiKick = this.sgKeybind.add((Setting)((KeybindSetting.Builder)((KeybindSetting.Builder)((KeybindSetting.Builder)(new KeybindSetting.Builder()).name("toggle-anti-kick")).description("Key to toggle anti kick on or off.")).defaultValue(Keybind.fromKey(-1))).build());
/*     */     
/*     */     this.phase = this.sgPhase.add((Setting)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("phase")).description("Whether or not to phase through blocks.")).defaultValue(Phase.NONE)).build());
/*     */     
/*     */     this.noPhaseSlow = this.sgPhase.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("no-phase-slow")).description("Whether or not to phase fast or slow.")).defaultValue(Boolean.valueOf(true))).build());
/*     */     
/*     */     this.noCollision = this.sgPhase.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("no-collision")).description("Whether or not to disable block collisions.")).defaultValue(Boolean.valueOf(false))).build());
/*     */     
/* 346 */     this.intervalTimer = new SystemTimer();
/* 347 */   } private static final Random random = new Random();
/*     */ 
/*     */ 
/*     */   
/*     */   public void onActivate() {
/* 352 */     this.packets = new ArrayList<>();
/* 353 */     this.posLooks = new ConcurrentHashMap<>();
/* 354 */     this.teleportId = 0;
/* 355 */     this.vDelay = 0;
/* 356 */     this.hDelay = 0;
/* 357 */     this.antiKickTicks = 0;
/* 358 */     this.limitTicks = 0;
/* 359 */     this.jitterTicks = 0;
/* 360 */     this.ticksExisted = 0;
/* 361 */     this.limitStrict = false;
/* 362 */     this.speedX = 0.0D;
/* 363 */     this.speedY = 0.0D;
/* 364 */     this.speedZ = 0.0D;
/* 365 */     this.oddJitter = false;
/* 366 */     this.forceAntiKick = true;
/* 367 */     this.forceLimit = true;
/* 368 */     this.startingOutOfBoundsPos = null;
/* 369 */     this.startingOutOfBoundsPos = new class_2828.class_2829(randomHorizontal(), 1.0D, randomHorizontal(), this.mc.field_1724.method_24828());
/* 370 */     this.packets.add(this.startingOutOfBoundsPos);
/* 371 */     this.mc.method_1562().method_2883((class_2596)this.startingOutOfBoundsPos);
/*     */   }
/*     */ 
/*     */   
/*     */   public void onDeactivate() {
/* 376 */     if (this.mc.field_1724 != null) {
/* 377 */       this.mc.field_1724.method_18800(0.0D, 0.0D, 0.0D);
/*     */     }
/*     */     
/* 380 */     class_1934 mode = EntityUtils.getGameMode((class_1657)this.mc.field_1724);
/* 381 */     if (mode != class_1934.field_9220 && mode != class_1934.field_9219) {
/* 382 */       (this.mc.field_1724.method_31549()).field_7479 = false;
/* 383 */       (this.mc.field_1724.method_31549()).field_7478 = false;
/*     */     } 
/*     */     
/* 386 */     ((Timer)Modules.get().get(Timer.class)).setOverride(1.0D);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getInfoString() {
/* 393 */     String info = "";
/*     */     
/* 395 */     info = info + "[" + info + ((Type)this.type.get()).name().substring(0, 1).toUpperCase() + "] ";
/* 396 */     if (this.forceAntiKick) info = info + "[Anti Kick] "; 
/* 397 */     if (this.forceLimit) info = info + "[Limit]";
/*     */     
/* 399 */     return info;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @EventHandler
/*     */   private void onGameJoin(GameJoinedEvent event) {
/* 406 */     if (((Boolean)this.autoToggle.get()).booleanValue()) toggle(); 
/*     */   }
/*     */   
/*     */   @EventHandler
/*     */   private void onGameLeave(GameLeftEvent event) {
/* 411 */     if (((Boolean)this.autoToggle.get()).booleanValue()) toggle();
/*     */   
/*     */   }
/*     */ 
/*     */   
/*     */   @EventHandler
/*     */   public void isCube(CollisionShapeEvent event) {
/* 418 */     if (this.phase.get() == Phase.VANILLA) this.mc.field_1724.field_5960 = true; 
/* 419 */     if (this.phase.get() != Phase.NONE && ((Boolean)this.noCollision.get()).booleanValue()) event.shape = class_259.method_1073();
/*     */   
/*     */   }
/*     */ 
/*     */   
/*     */   @EventHandler
/*     */   private void onKey(KeyEvent event) {
/* 426 */     if (this.mc.field_1755 != null)
/*     */       return; 
/* 428 */     if (((Keybind)this.toggleLimit.get()).isPressed()) {
/* 429 */       this.forceLimit = !this.forceLimit;
/* 430 */       if (((Boolean)this.message.get()).booleanValue()) ChatUtils.sendMsg(class_2561.method_30163(this.forceLimit ? "Activated Packet Limit" : "Disabled Packet Limit")); 
/*     */     } 
/* 432 */     if (((Keybind)this.toggleAntiKick.get()).isPressed()) {
/* 433 */       this.forceAntiKick = !this.forceAntiKick;
/* 434 */       if (((Boolean)this.message.get()).booleanValue()) ChatUtils.sendMsg(class_2561.method_30163(this.forceAntiKick ? "Activated Anti Kick" : "Disabled Anti Kick"));
/*     */     
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @EventHandler
/*     */   private void onPreTick(TickEvent.Pre event) {
/* 442 */     if (((Boolean)this.boost.get()).booleanValue()) {
/* 443 */       ((Timer)Modules.get().get(Timer.class)).setOverride(((Double)this.boostTimer.get()).floatValue());
/*     */     } else {
/* 445 */       ((Timer)Modules.get().get(Timer.class)).setOverride(1.0D);
/*     */     }  } @EventHandler
/*     */   public void onPostTick(TickEvent.Post event) { float rawFactor;
/*     */     double i;
/*     */     class_243 vel;
/*     */     int factorInt;
/*     */     double d1;
/*     */     int ignore, j;
/* 453 */     if (this.type.get() == Type.ELYTRA) {
/* 454 */       class_243 vec3d = new class_243(0.0D, 0.0D, 0.0D);
/*     */       
/* 456 */       if (this.mc.field_1724.field_6017 <= 0.2D)
/*     */         return; 
/* 458 */       if (this.mc.field_1690.field_1894.method_1434()) {
/* 459 */         vec3d.method_1031(0.0D, 0.0D, ((Double)this.speed.get()).doubleValue());
/* 460 */         vec3d.method_1024(-((float)Math.toRadians(this.mc.field_1724.method_36454())));
/* 461 */       } else if (this.mc.field_1690.field_1881.method_1434()) {
/* 462 */         vec3d.method_1031(0.0D, 0.0D, ((Double)this.speed.get()).doubleValue());
/* 463 */         vec3d.method_1024((float)Math.toRadians(this.mc.field_1724.method_36454()));
/*     */       } 
/*     */       
/* 466 */       if (this.mc.field_1690.field_1903.method_1434()) {
/* 467 */         vec3d.method_1031(0.0D, ((Double)this.vspeed.get()).doubleValue(), 0.0D);
/* 468 */       } else if (this.mc.field_1690.field_1832.method_1434()) {
/* 469 */         vec3d.method_1031(0.0D, -((Double)this.vspeed.get()).doubleValue(), 0.0D);
/*     */       } 
/*     */       
/* 472 */       this.mc.field_1724.method_18799(vec3d);
/* 473 */       this.mc.field_1724.field_3944.method_2883((class_2596)new class_2848((class_1297)this.mc.field_1724, class_2848.class_2849.field_12982));
/* 474 */       this.mc.field_1724.field_3944.method_2883((class_2596)new class_2828.class_5911(true));
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 479 */     if (this.ticksExisted % 20 == 0) {
/* 480 */       this.posLooks.forEach((tp, timeVec3d) -> {
/*     */             if (System.currentTimeMillis() - timeVec3d.getTime() > TimeUnit.SECONDS.toMillis(30L)) {
/*     */               this.posLooks.remove(tp);
/*     */             }
/*     */           });
/*     */     }
/*     */     
/* 487 */     this.ticksExisted++;
/*     */     
/* 489 */     this.mc.field_1724.method_18800(0.0D, 0.0D, 0.0D);
/*     */     
/* 491 */     if (this.teleportId <= 0 && this.type.get() != Type.SETBACK) {
/* 492 */       this.startingOutOfBoundsPos = new class_2828.class_2829(randomHorizontal(), 1.0D, randomHorizontal(), this.mc.field_1724.method_24828());
/* 493 */       this.packets.add(this.startingOutOfBoundsPos);
/* 494 */       this.mc.method_1562().method_2883((class_2596)this.startingOutOfBoundsPos);
/*     */       
/*     */       return;
/*     */     } 
/* 498 */     boolean phasing = checkCollisionBox();
/*     */     
/* 500 */     this.speedX = 0.0D;
/* 501 */     this.speedY = 0.0D;
/* 502 */     this.speedZ = 0.0D;
/*     */     
/* 504 */     if (this.mc.field_1690.field_1903.method_1434() && (this.hDelay < 1 || (((Boolean)this.multiAxis.get()).booleanValue() && phasing))) {
/* 505 */       if (this.ticksExisted % ((this.type.get() == Type.SETBACK || this.type.get() == Type.SLOW || (this.limit.get() == Limit.STRICT && this.forceLimit)) ? 10 : 20) == 0) {
/* 506 */         this.speedY = (this.antiKick.get() != AntiKick.NONE && this.forceAntiKick && onGround()) ? -0.032D : 0.062D;
/*     */       } else {
/* 508 */         this.speedY = 0.062D;
/*     */       } 
/* 510 */       this.antiKickTicks = 0;
/* 511 */       this.vDelay = 5;
/* 512 */     } else if (this.mc.field_1690.field_1832.method_1434() && (this.hDelay < 1 || (((Boolean)this.multiAxis.get()).booleanValue() && phasing))) {
/* 513 */       this.speedY = -0.062D;
/* 514 */       this.antiKickTicks = 0;
/* 515 */       this.vDelay = 5;
/*     */     } 
/*     */     
/* 518 */     if ((((Boolean)this.multiAxis.get()).booleanValue() && phasing) || !this.mc.field_1690.field_1832.method_1434() || !this.mc.field_1690.field_1903.method_1434()) {
/* 519 */       if (isPlayerMoving()) {
/* 520 */         double[] dir = directionSpeed((((phasing && this.phase.get() == Phase.NCP) || this.bypass.get() == Bypass.NCP) ? (((Boolean)this.noPhaseSlow.get()).booleanValue() ? (((Boolean)this.multiAxis.get()).booleanValue() ? 0.0465D : 0.062D) : 0.031D) : 0.26D) * ((Double)this.speed.get()).doubleValue());
/* 521 */         if ((dir[0] != 0.0D || dir[1] != 0.0D) && (this.vDelay < 1 || (((Boolean)this.multiAxis.get()).booleanValue() && phasing))) {
/* 522 */           this.speedX = dir[0];
/* 523 */           this.speedZ = dir[1];
/* 524 */           this.hDelay = 5;
/*     */         } 
/*     */       } 
/*     */       
/* 528 */       if (this.antiKick.get() != AntiKick.NONE && this.forceAntiKick && onGround() && ((this.limit.get() == Limit.NONE && this.forceLimit) || this.limitTicks != 0)) {
/* 529 */         if (this.antiKickTicks < ((this.packetMode.get() == Mode.BYPASS && !((Boolean)this.bounds.get()).booleanValue()) ? 1 : 3)) {
/* 530 */           this.antiKickTicks++;
/*     */         } else {
/* 532 */           this.antiKickTicks = 0;
/* 533 */           if ((this.antiKick.get() != AntiKick.LIMITED && this.forceAntiKick && onGround()) || !phasing) {
/* 534 */             this.speedY = (this.antiKick.get() == AntiKick.STRICT && this.forceAntiKick && onGround()) ? -0.08D : -0.04D;
/*     */           }
/*     */         } 
/*     */       }
/*     */     } 
/*     */     
/* 540 */     if ((((phasing && this.phase.get() == Phase.NCP) || this.bypass.get() == Bypass.NCP) && this.mc.field_1724.field_6250 != 0.0D) || (this.mc.field_1724.field_6212 != 0.0D && this.speedY != 0.0D)) {
/* 541 */       this.speedY /= 2.5D;
/*     */     }
/*     */     
/* 544 */     if (this.limit.get() != Limit.NONE && this.forceLimit) {
/* 545 */       if (this.limitTicks == 0) {
/* 546 */         this.speedX = 0.0D;
/* 547 */         this.speedY = 0.0D;
/* 548 */         this.speedZ = 0.0D;
/* 549 */       } else if (this.limitTicks == 2 && ((Boolean)this.jitter.get()).booleanValue()) {
/* 550 */         if (this.oddJitter) {
/* 551 */           this.speedX = 0.0D;
/* 552 */           this.speedY = 0.0D;
/* 553 */           this.speedZ = 0.0D;
/*     */         } 
/* 555 */         this.oddJitter = !this.oddJitter;
/*     */       } 
/* 557 */     } else if (((Boolean)this.jitter.get()).booleanValue() && this.jitterTicks == 7) {
/* 558 */       this.speedX = 0.0D;
/* 559 */       this.speedY = 0.0D;
/* 560 */       this.speedZ = 0.0D;
/*     */     } 
/*     */     
/* 563 */     switch ((Type)this.type.get()) {
/*     */       case UP:
/* 565 */         if (!isMoving())
/* 566 */           break;  this.mc.field_1724.method_18800(this.speedX, this.speedY, this.speedZ);
/* 567 */         sendPackets(this.speedX, this.speedY, this.speedZ, (Mode)this.packetMode.get(), true, false);
/*     */         break;
/*     */       case PRESERVE:
/* 570 */         if (!isMoving())
/* 571 */           break;  sendPackets(this.speedX, this.speedY, this.speedZ, (Mode)this.packetMode.get(), true, false);
/*     */         break;
/*     */       case LIMITJITTER:
/* 574 */         if (!isMoving())
/* 575 */           break;  this.mc.field_1724.method_18800(this.speedX, this.speedY, this.speedZ);
/* 576 */         sendPackets(this.speedX, this.speedY, this.speedZ, (Mode)this.packetMode.get(), false, false);
/*     */         break;
/*     */       case BYPASS:
/* 579 */         if (!isMoving())
/* 580 */           break;  this.mc.field_1724.method_18800(this.speedX, this.speedY, this.speedZ);
/* 581 */         sendPackets(this.speedX, this.speedY, this.speedZ, (Mode)this.packetMode.get(), true, true); break;
/*     */       case OBSCURE:
/*     */       case null:
/* 584 */         rawFactor = ((Double)this.factor.get()).floatValue();
/* 585 */         if (((Keybind)this.factorize.get()).isPressed() && this.intervalTimer.hasPassed(3500.0D)) {
/* 586 */           this.intervalTimer.reset();
/* 587 */           rawFactor = ((Double)this.motion.get()).floatValue();
/*     */         } 
/* 589 */         factorInt = (int)Math.floor(rawFactor);
/* 590 */         ignore = 0;
/* 591 */         this.factorCounter++;
/* 592 */         if (this.factorCounter > (int)(20.0D / (rawFactor - factorInt) * 20.0D)) {
/* 593 */           factorInt++;
/* 594 */           this.factorCounter = 0;
/*     */         } 
/* 596 */         for (j = 1; j <= factorInt; j++) {
/* 597 */           if (ignore <= 0) {
/* 598 */             j *= ((Integer)this.exponent.get()).intValue();
/* 599 */             ignore = ((Integer)this.ignoreSteps.get()).intValue();
/* 600 */             this.mc.field_1724.method_18800(this.speedX * j, this.speedY * j, this.speedZ * j);
/* 601 */             sendPackets(isMoving() ? (this.speedX * j) : 0.0D, this.speedY * j, isMoving() ? (this.speedZ * j) : 0.0D, (Mode)this.packetMode.get(), true, false);
/*     */           } else {
/* 603 */             ignore--;
/*     */           } 
/*     */         } 
/* 606 */         this.speedX = (this.mc.field_1724.method_18798()).field_1352;
/* 607 */         this.speedY = (this.mc.field_1724.method_18798()).field_1351;
/* 608 */         this.speedZ = (this.mc.field_1724.method_18798()).field_1350;
/*     */         break;
/*     */       case null:
/* 611 */         if (!isMoving())
/* 612 */           break;  for (i = 0.0625D; i < ((Double)this.speed.get()).doubleValue(); i += 0.262D) {
/* 613 */           sendPackets(this.speedX, this.speedY, this.speedZ, (Mode)this.packetMode.get(), false, false);
/* 614 */           sendPackets(this.speedX, this.speedY, this.speedZ, (Mode)this.packetMode.get(), true, true);
/*     */         } 
/* 616 */         this.mc.method_1562().method_2883((class_2596)new class_2828.class_2829(this.mc.field_1724.method_23317() + this.speedX, this.mc.field_1724.method_23318() + (((Boolean)this.strict.get()).booleanValue() ? true : 5), this.mc.field_1724.method_23321() + this.speedZ, this.mc.field_1724.method_24828()));
/*     */         break;
/*     */       case null:
/* 619 */         if (!isMoving())
/* 620 */           break;  vel = this.mc.field_1724.method_18798();
/* 621 */         for (d1 = 0.0625D; d1 < ((Double)this.speed.get()).doubleValue(); d1 += 0.262D) {
/* 622 */           double[] dir = NPlayerUtils.directionSpeed((float)d1);
/* 623 */           this.mc.method_1562().method_2883((class_2596)new class_2828.class_2829(this.mc.field_1724.method_23317() + dir[0], this.mc.field_1724.method_23318(), this.mc.field_1724.method_23321() + dir[1], this.mc.field_1724.method_24828()));
/*     */         } 
/* 625 */         if (this.bypass.get() == Bypass.DEFAULT) {
/* 626 */           this.mc.method_1562().method_2883((class_2596)new class_2828.class_2829(this.mc.field_1724.method_23317() + vel.field_1352, (this.mc.field_1724.method_23318() <= 10.0D) ? (((Boolean)this.strict.get()).booleanValue() ? 10 : 'ÿ') : (((Boolean)this.strict.get()).booleanValue() ? 0.5D : 1.0D), this.mc.field_1724.method_23321() + vel.field_1350, this.mc.field_1724.method_24828()));
/*     */         }
/*     */         break;
/*     */     } 
/* 630 */     this.vDelay--;
/* 631 */     this.hDelay--;
/*     */     
/* 633 */     if (((Boolean)this.constrict.get()).booleanValue() && ((this.limit.get() == Limit.NONE && this.forceLimit) || this.limitTicks > 1)) {
/* 634 */       this.mc.method_1562().method_2883((class_2596)new class_2828.class_2829(this.mc.field_1724.method_23317(), this.mc.field_1724.method_23318(), this.mc.field_1724.method_23321(), false));
/*     */     }
/*     */     
/* 637 */     this.limitTicks++;
/* 638 */     this.jitterTicks++;
/*     */     
/* 640 */     if (this.limitTicks > ((this.limit.get() == Limit.STRICT && this.forceLimit) ? (this.limitStrict ? 1 : 2) : 3)) {
/* 641 */       this.limitTicks = 0;
/* 642 */       this.limitStrict = !this.limitStrict;
/*     */     } 
/*     */     
/* 645 */     if (this.jitterTicks > 7) {
/* 646 */       this.jitterTicks = 0;
/*     */     } }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @EventHandler
/*     */   public void onReceivePacket(PacketEvent.Receive event) {
/* 654 */     if (this.type.get() == Type.ELYTRA)
/* 655 */       return;  class_2596 class_2596 = event.packet; if (class_2596 instanceof class_2708) { class_2708 packet = (class_2708)class_2596;
/* 656 */       if (this.mc.field_1724.method_5805()) {
/* 657 */         if (this.teleportId <= 0) {
/* 658 */           this.teleportId = ((class_2708)event.packet).method_11737();
/*     */         }
/* 660 */         else if (this.mc.field_1687.method_33598(this.mc.field_1724.method_31477(), this.mc.field_1724.method_31479()) && this.type.get() != Type.SETBACK) {
/* 661 */           if (this.type.get() == Type.DESYNC) {
/* 662 */             this.posLooks.remove(Integer.valueOf(packet.method_11737()));
/* 663 */             event.cancel();
/* 664 */             if (this.type.get() == Type.SLOW)
/* 665 */               this.mc.field_1724.method_5814(packet.method_11734(), packet.method_11735(), packet.method_11738()); 
/*     */             return;
/*     */           } 
/* 668 */           if (this.posLooks.containsKey(Integer.valueOf(packet.method_11737()))) {
/* 669 */             TimeVec3d vec = this.posLooks.get(Integer.valueOf(packet.method_11737()));
/* 670 */             if (vec.field_1352 == packet.method_11734() && vec.field_1351 == packet.method_11735() && vec.field_1350 == packet.method_11738()) {
/* 671 */               this.posLooks.remove(Integer.valueOf(packet.method_11737()));
/* 672 */               event.cancel();
/* 673 */               if (this.type.get() == Type.SLOW) {
/* 674 */                 this.mc.field_1724.method_5814(packet.method_11734(), packet.method_11735(), packet.method_11738());
/*     */               }
/*     */               
/*     */               return;
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       }
/*     */       
/* 683 */       ((PlayerPositionLookS2CPacketAccessor)event.packet).setYaw(this.mc.field_1724.method_36454());
/* 684 */       ((PlayerPositionLookS2CPacketAccessor)event.packet).setPitch(this.mc.field_1724.method_36455());
/* 685 */       packet.method_11733().remove(class_2708.class_2709.field_12397);
/* 686 */       packet.method_11733().remove(class_2708.class_2709.field_12401);
/* 687 */       this.teleportId = packet.method_11737(); }
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @EventHandler
/*     */   public void onPlayerMove(PlayerMoveEvent event) {
/* 695 */     if (this.type.get() == Type.ELYTRA) {
/* 696 */       (this.mc.field_1724.method_31549()).field_7479 = true;
/* 697 */       this.mc.field_1724.method_31549().method_7248(((Double)this.speed.get()).floatValue() / 20.0F);
/*     */       
/*     */       return;
/*     */     } 
/* 701 */     if (this.type.get() != Type.SETBACK && this.teleportId <= 0)
/* 702 */       return;  if (this.type.get() != Type.SLOW) ((IVec3d)event.movement).set(this.speedX, this.speedY, this.speedZ); 
/*     */   }
/*     */   
/*     */   @EventHandler
/*     */   public void onSend(PacketEvent.Send event) {
/* 707 */     if (this.type.get() == Type.ELYTRA && event.packet instanceof class_2828) {
/* 708 */       this.mc.field_1724.field_3944.method_2883((class_2596)new class_2848((class_1297)this.mc.field_1724, class_2848.class_2849.field_12982));
/*     */       
/*     */       return;
/*     */     } 
/* 712 */     if (event.packet instanceof class_2828 && !(event.packet instanceof class_2828.class_2829)) {
/* 713 */       event.cancel();
/*     */     }
/*     */     
/* 716 */     class_2596 class_2596 = event.packet; if (class_2596 instanceof class_2828) { class_2828 packet = (class_2828)class_2596;
/* 717 */       if (this.packets.contains(packet)) {
/* 718 */         this.packets.remove(packet);
/*     */         return;
/*     */       } 
/* 721 */       event.cancel(); }
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void updateFlying(Type type) {
/* 728 */     if (this.mc.field_1687 != null && this.mc.field_1724 != null && type != Type.ELYTRA) {
/* 729 */       (this.mc.field_1724.method_31549()).field_7479 = false;
/* 730 */       (this.mc.field_1724.method_31549()).field_7478 = false;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void sendPackets(double x, double y, double z, Mode mode, boolean confirmTeleport, boolean sendExtraConfirmTeleport) {
/* 735 */     class_243 nextPos = new class_243(this.mc.field_1724.method_23317() + x, this.mc.field_1724.method_23318() + y, this.mc.field_1724.method_23321() + z);
/* 736 */     class_243 bounds = getBoundsVec(x, y, z, mode);
/*     */     
/* 738 */     class_2828.class_2829 class_28291 = new class_2828.class_2829(nextPos.field_1352, nextPos.field_1351, nextPos.field_1350, this.mc.field_1724.method_24828());
/* 739 */     this.packets.add(class_28291);
/* 740 */     this.mc.method_1562().method_2883((class_2596)class_28291);
/*     */     
/* 742 */     if (this.limit.get() != Limit.NONE && this.forceLimit && this.limitTicks == 0)
/*     */       return; 
/* 744 */     class_2828.class_2829 class_28292 = new class_2828.class_2829(bounds.field_1352, bounds.field_1351, bounds.field_1350, this.mc.field_1724.method_24828());
/* 745 */     this.packets.add(class_28292);
/* 746 */     this.mc.method_1562().method_2883((class_2596)class_28292);
/*     */     
/* 748 */     if (confirmTeleport) {
/* 749 */       this.teleportId++;
/*     */       
/* 751 */       if (sendExtraConfirmTeleport) {
/* 752 */         this.mc.method_1562().method_2883((class_2596)new class_2793(this.teleportId - 1));
/*     */       }
/*     */       
/* 755 */       this.mc.method_1562().method_2883((class_2596)new class_2793(this.teleportId));
/*     */       
/* 757 */       this.posLooks.put(Integer.valueOf(this.teleportId), new TimeVec3d(nextPos.field_1352, nextPos.field_1351, nextPos.field_1350, System.currentTimeMillis()));
/*     */       
/* 759 */       if (sendExtraConfirmTeleport) {
/* 760 */         this.mc.method_1562().method_2883((class_2596)new class_2793(this.teleportId + 1));
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private class_243 getBoundsVec(double x, double y, double z, Mode mode) {
/* 766 */     switch (mode) {
/*     */       case UP:
/* 768 */         return new class_243(this.mc.field_1724.method_23317() + x, ((Boolean)this.bounds.get()).booleanValue() ? (((Boolean)this.strict.get()).booleanValue() ? 'ÿ' : 'Ā') : (this.mc.field_1724.method_23318() + 420.0D), this.mc.field_1724.method_23321() + z);
/*     */       case PRESERVE:
/* 770 */         return new class_243(((Boolean)this.bounds.get()).booleanValue() ? (this.mc.field_1724.method_23317() + randomHorizontal()) : randomHorizontal(), ((Boolean)this.strict.get()).booleanValue() ? Math.max(this.mc.field_1724.method_23318(), 2.0D) : this.mc.field_1724.method_23318(), ((Boolean)this.bounds.get()).booleanValue() ? (this.mc.field_1724.method_23321() + randomHorizontal()) : randomHorizontal());
/*     */       case LIMITJITTER:
/* 772 */         return new class_243(this.mc.field_1724.method_23317() + (((Boolean)this.strict.get()).booleanValue() ? x : randomLimitedHorizontal()), this.mc.field_1724.method_23318() + randomLimitedVertical(), this.mc.field_1724.method_23321() + (((Boolean)this.strict.get()).booleanValue() ? z : randomLimitedHorizontal()));
/*     */       case BYPASS:
/* 774 */         if (((Boolean)this.bounds.get()).booleanValue()) {
/* 775 */           double rawY = y * 510.0D;
/* 776 */           return new class_243(this.mc.field_1724.method_23317() + x, this.mc.field_1724.method_23318() + ((rawY > ((PlayerUtils.getDimension() == Dimension.End) ? 127 : 'ÿ')) ? -rawY : ((rawY < 1.0D) ? -rawY : rawY)), this.mc.field_1724.method_23321() + z);
/*     */         } 
/* 778 */         return new class_243(this.mc.field_1724.method_23317() + ((x == 0.0D) ? (random.nextBoolean() ? -10 : 10) : (x * 38.0D)), this.mc.field_1724.method_23318() + y, this.mc.field_1724.method_23317() + ((z == 0.0D) ? (random.nextBoolean() ? -10 : 10) : (z * 38.0D)));
/*     */       
/*     */       case OBSCURE:
/* 781 */         return new class_243(this.mc.field_1724.method_23317() + randomHorizontal(), Math.max(1.5D, Math.min(this.mc.field_1724.method_23318() + y, 253.5D)), this.mc.field_1724.method_23321() + randomHorizontal());
/*     */     } 
/* 783 */     return new class_243(this.mc.field_1724.method_23317() + x, ((Boolean)this.bounds.get()).booleanValue() ? (((Boolean)this.strict.get()).booleanValue() ? true : false) : (this.mc.field_1724.method_23318() - 1337.0D), this.mc.field_1724.method_23321() + z);
/*     */   }
/*     */ 
/*     */   
/*     */   private double randomHorizontal() {
/* 788 */     int randomValue = random.nextInt(((Boolean)this.bounds.get()).booleanValue() ? 80 : ((this.packetMode.get() == Mode.OBSCURE) ? ((this.ticksExisted % 2 == 0) ? 480 : 100) : 29000000)) + (((Boolean)this.bounds.get()).booleanValue() ? 5 : 500);
/* 789 */     if (random.nextBoolean()) {
/* 790 */       return randomValue;
/*     */     }
/* 792 */     return -randomValue;
/*     */   }
/*     */   
/*     */   public static double randomLimitedVertical() {
/* 796 */     int randomValue = random.nextInt(22);
/* 797 */     randomValue += 70;
/* 798 */     if (random.nextBoolean()) {
/* 799 */       return randomValue;
/*     */     }
/* 801 */     return -randomValue;
/*     */   }
/*     */   
/*     */   public static double randomLimitedHorizontal() {
/* 805 */     int randomValue = random.nextInt(10);
/* 806 */     if (random.nextBoolean()) {
/* 807 */       return randomValue;
/*     */     }
/* 809 */     return -randomValue;
/*     */   }
/*     */   
/*     */   private double[] directionSpeed(double speed) {
/* 813 */     float forward = this.mc.field_1724.field_6250;
/* 814 */     float side = this.mc.field_1724.field_6212;
/* 815 */     float yaw = this.mc.field_1724.field_5982 + this.mc.field_1724.method_36454() - this.mc.field_1724.field_5982;
/*     */     
/* 817 */     if (forward != 0.0F) {
/* 818 */       if (side > 0.0F) {
/* 819 */         yaw += ((forward > 0.0F) ? -45 : 45);
/* 820 */       } else if (side < 0.0F) {
/* 821 */         yaw += ((forward > 0.0F) ? 45 : -45);
/*     */       } 
/* 823 */       side = 0.0F;
/* 824 */       if (forward > 0.0F) {
/* 825 */         forward = 1.0F;
/* 826 */       } else if (forward < 0.0F) {
/* 827 */         forward = -1.0F;
/*     */       } 
/*     */     } 
/*     */     
/* 831 */     double sin = Math.sin(Math.toRadians((yaw + 90.0F)));
/* 832 */     double cos = Math.cos(Math.toRadians((yaw + 90.0F)));
/* 833 */     double posX = forward * speed * cos + side * speed * sin;
/* 834 */     double posZ = forward * speed * sin - side * speed * cos;
/* 835 */     return new double[] { posX, posZ };
/*     */   }
/*     */   
/*     */   private boolean checkCollisionBox() {
/* 839 */     return this.mc.field_1687.method_20812((class_1297)this.mc.field_1724, this.mc.field_1724.method_5829()).iterator().hasNext();
/*     */   }
/*     */   
/*     */   private boolean onGround() {
/* 843 */     if (((Boolean)this.stopOnGround.get()).booleanValue()) return !this.mc.field_1687.method_20812((class_1297)this.mc.field_1724, this.mc.field_1724.method_5829().method_989(0.0D, -0.01D, 0.0D)).iterator().hasNext();
/*     */     
/* 845 */     return true;
/*     */   }
/*     */   
/*     */   private boolean isPlayerMoving() {
/* 849 */     if (this.mc.field_1690.field_1903.method_1434()) return true; 
/* 850 */     if (this.mc.field_1690.field_1894.method_1434()) return true; 
/* 851 */     if (this.mc.field_1690.field_1881.method_1434()) return true; 
/* 852 */     if (this.mc.field_1690.field_1913.method_1434()) return true; 
/* 853 */     return this.mc.field_1690.field_1849.method_1434();
/*     */   }
/*     */   
/*     */   private boolean isMoving() {
/* 857 */     if (((Boolean)this.onlyOnMove.get()).booleanValue()) {
/* 858 */       if (this.mc.field_1690.field_1903.method_1434()) return true; 
/* 859 */       if (this.mc.field_1690.field_1832.method_1434()) return true; 
/* 860 */       if (this.mc.field_1690.field_1894.method_1434()) return true; 
/* 861 */       if (this.mc.field_1690.field_1881.method_1434()) return true; 
/* 862 */       if (this.mc.field_1690.field_1913.method_1434()) return true; 
/* 863 */       return this.mc.field_1690.field_1849.method_1434();
/*     */     } 
/*     */     
/* 866 */     return true;
/*     */   }
/*     */   
/*     */   private static class TimeVec3d extends class_243 {
/*     */     private final long time;
/*     */     
/*     */     public TimeVec3d(double xIn, double yIn, double zIn, long time) {
/* 873 */       super(xIn, yIn, zIn);
/* 874 */       this.time = time;
/*     */     }
/*     */     
/*     */     public long getTime() {
/* 878 */       return this.time;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class SystemTimer {
/*     */     private long time;
/*     */     
/*     */     public SystemTimer() {
/* 886 */       this.time = System.currentTimeMillis();
/*     */     }
/*     */     
/*     */     public boolean hasPassed(double ms) {
/* 890 */       return ((System.currentTimeMillis() - this.time) >= ms);
/*     */     }
/*     */     
/*     */     public void reset() {
/* 894 */       this.time = System.currentTimeMillis();
/*     */     }
/*     */     
/*     */     public long getTime() {
/* 898 */       return this.time;
/*     */     }
/*     */     
/*     */     public void setTime(long time) {
/* 902 */       this.time = time;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Shotgun\OneDrive\Desktop\Nova-v-100000 (1).jar!\Nova\modules\misc\RocketFly.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */