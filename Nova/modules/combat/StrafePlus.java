/*     */ package Nova.modules.combat;
/*     */ import Nova.util.NEntityUtils;
/*     */ import Nova.util.NWorldUtils;
/*     */ import Nova.util.TimerUtils;
/*     */ import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
/*     */ import meteordevelopment.meteorclient.events.packets.PacketEvent;
/*     */ import meteordevelopment.meteorclient.mixininterface.IVec3d;
/*     */ import meteordevelopment.meteorclient.settings.BoolSetting;
/*     */ import meteordevelopment.meteorclient.settings.DoubleSetting;
/*     */ import meteordevelopment.meteorclient.settings.EnumSetting;
/*     */ import meteordevelopment.meteorclient.settings.IntSetting;
/*     */ import meteordevelopment.meteorclient.settings.Setting;
/*     */ import meteordevelopment.meteorclient.settings.SettingGroup;
/*     */ import meteordevelopment.meteorclient.systems.modules.Module;
/*     */ import meteordevelopment.meteorclient.systems.modules.Modules;
/*     */ import meteordevelopment.meteorclient.systems.modules.movement.Flight;
/*     */ import meteordevelopment.meteorclient.systems.modules.movement.LongJump;
/*     */ import meteordevelopment.meteorclient.systems.modules.movement.elytrafly.ElytraFly;
/*     */ import meteordevelopment.meteorclient.systems.modules.world.Timer;
/*     */ import meteordevelopment.meteorclient.utils.misc.Vec2;
/*     */ import meteordevelopment.meteorclient.utils.player.PlayerUtils;
/*     */ import meteordevelopment.orbit.EventHandler;
/*     */ import net.minecraft.class_1293;
/*     */ import net.minecraft.class_1294;
/*     */ import net.minecraft.class_1657;
/*     */ import net.minecraft.class_2246;
/*     */ import net.minecraft.class_243;
/*     */ 
/*     */ public class StrafePlus extends Module {
/*     */   public enum Mode {
/*  31 */     Vanilla,
/*  32 */     NCP,
/*  33 */     Smart;
/*     */   }
/*     */   
/*     */   public enum HopMode {
/*  37 */     Auto,
/*  38 */     Custom;
/*     */   }
/*     */   
/*     */   public enum WebbedPause {
/*  42 */     Always,
/*  43 */     OnAir,
/*  44 */     None;
/*     */   }
/*     */   
/*  47 */   private final SettingGroup sgGeneral = this.settings.getDefaultGroup();
/*  48 */   private final SettingGroup sgVanilla = this.settings.createGroup("Vanilla");
/*  49 */   private final SettingGroup sgNCP = this.settings.createGroup("NCP");
/*  50 */   private final SettingGroup sgPotion = this.settings.createGroup("Potions");
/*  51 */   private final SettingGroup sgPause = this.settings.createGroup("Pause");
/*  52 */   private final SettingGroup sgAC = this.settings.createGroup("Anti Cheat");
/*     */ 
/*     */   
/*  55 */   private final Setting<Mode> mode = this.sgGeneral.add((Setting)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder())
/*  56 */       .name("mode"))
/*  57 */       .description("Behaviour of your movements."))
/*  58 */       .defaultValue(Mode.Smart))
/*  59 */       .build());
/*     */ 
/*     */   
/*  62 */   private final Setting<Double> groundTimer = this.sgGeneral.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder())
/*  63 */       .name("ground-timer"))
/*  64 */       .description("Ground timer override."))
/*  65 */       .defaultValue(1.0D)
/*  66 */       .sliderRange(0.001D, 10.0D)
/*  67 */       .build());
/*     */ 
/*     */   
/*  70 */   private final Setting<Double> airTimer = this.sgGeneral.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder())
/*  71 */       .name("air-timer"))
/*  72 */       .description("Air timer override."))
/*  73 */       .defaultValue(1.088D)
/*  74 */       .sliderRange(0.001D, 10.0D)
/*  75 */       .build());
/*     */ 
/*     */   
/*  78 */   private final Setting<Boolean> autoSprint = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder())
/*  79 */       .name("auto-sprint"))
/*  80 */       .description("Makes you sprint if you are moving forward."))
/*  81 */       .defaultValue(Boolean.valueOf(false)))
/*  82 */       .build());
/*     */ 
/*     */   
/*  85 */   private final Setting<Boolean> TPSSync = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder())
/*  86 */       .name("TPS-sync"))
/*  87 */       .description("Tries to sync movement with the server's TPS."))
/*  88 */       .defaultValue(Boolean.valueOf(false)))
/*  89 */       .build());
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  94 */   private final Setting<Double> vanillaSneakSpeed = this.sgVanilla.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder())
/*  95 */       .name("vanilla-sneak-speed"))
/*  96 */       .description("The speed in blocks per second (on ground and sneaking)."))
/*  97 */       .defaultValue(2.6D)
/*  98 */       .min(0.0D)
/*  99 */       .sliderMax(20.0D)
/* 100 */       .visible(() -> (this.mode.get() == Mode.Vanilla)))
/* 101 */       .build());
/*     */ 
/*     */   
/* 104 */   private final Setting<Double> vanillaGroundSpeed = this.sgVanilla.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder())
/* 105 */       .name("vanilla-ground-speed"))
/* 106 */       .description("The speed in blocks per second (on ground)."))
/* 107 */       .defaultValue(5.6D)
/* 108 */       .min(0.0D)
/* 109 */       .sliderMax(20.0D)
/* 110 */       .visible(() -> (this.mode.get() == Mode.Vanilla)))
/* 111 */       .build());
/*     */ 
/*     */   
/* 114 */   private final Setting<Double> vanillaAirSpeed = this.sgVanilla.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder())
/* 115 */       .name("vanilla-air-speed"))
/* 116 */       .description("The speed in blocks per second (on air)."))
/* 117 */       .defaultValue(6.0D)
/* 118 */       .min(0.0D)
/* 119 */       .sliderMax(20.0D)
/* 120 */       .visible(() -> (this.mode.get() == Mode.Vanilla)))
/* 121 */       .build());
/*     */ 
/*     */   
/* 124 */   private final Setting<Boolean> rubberbandPause = this.sgVanilla.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder())
/* 125 */       .name("pause-on-rubberband"))
/* 126 */       .description("Will pause Vanilla mode when you rubberband."))
/* 127 */       .defaultValue(Boolean.valueOf(false)))
/* 128 */       .visible(() -> (this.mode.get() == Mode.Vanilla)))
/* 129 */       .build());
/*     */ 
/*     */   
/* 132 */   private final Setting<Integer> rubberbandTime = this.sgVanilla.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder())
/* 133 */       .name("pause-time"))
/* 134 */       .description("Pauses vanilla mode for x ticks when a rubberband is detected."))
/* 135 */       .defaultValue(Integer.valueOf(30)))
/* 136 */       .min(0)
/* 137 */       .sliderMax(100)
/* 138 */       .visible(() -> (this.mode.get() == Mode.Vanilla && ((Boolean)this.rubberbandPause.get()).booleanValue())))
/* 139 */       .build());
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 144 */   private final Setting<Double> ncpSpeed = this.sgNCP.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder())
/* 145 */       .name("NCP-speed"))
/* 146 */       .description("The speed."))
/* 147 */       .defaultValue(2.0D)
/* 148 */       .min(0.0D)
/* 149 */       .sliderMax(3.0D)
/* 150 */       .visible(() -> (this.mode.get() != Mode.Vanilla)))
/* 151 */       .build());
/*     */ 
/*     */   
/* 154 */   private final Setting<Boolean> ncpSpeedLimit = this.sgNCP.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder())
/* 155 */       .name("speed-limit"))
/* 156 */       .description("Limits your speed on servers with very strict anticheats."))
/* 157 */       .defaultValue(Boolean.valueOf(false)))
/* 158 */       .visible(() -> (this.mode.get() != Mode.Vanilla)))
/* 159 */       .build());
/*     */ 
/*     */   
/* 162 */   private final Setting<Double> startingSpeed = this.sgNCP.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder())
/* 163 */       .name("starting-speed"))
/* 164 */       .description("Initial speed when starting (recommended 1.18 on NCP, 1.080 on Smart)."))
/* 165 */       .defaultValue(1.08D)
/* 166 */       .min(0.0D)
/* 167 */       .sliderMax(2.0D)
/* 168 */       .visible(() -> (this.mode.get() != Mode.Vanilla)))
/* 169 */       .build());
/*     */ 
/*     */   
/* 172 */   private final Setting<HopMode> hopMode = this.sgNCP.add((Setting)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder())
/* 173 */       .name("hop-mode"))
/* 174 */       .description("Mode to use for the hop height."))
/* 175 */       .defaultValue(HopMode.Auto))
/* 176 */       .visible(() -> (this.mode.get() != Mode.Vanilla)))
/* 177 */       .build());
/*     */ 
/*     */   
/* 180 */   private final Setting<Double> hopHeight = this.sgNCP.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder())
/* 181 */       .name("hop-height"))
/* 182 */       .description("The hop intensity."))
/* 183 */       .defaultValue(0.401D)
/* 184 */       .min(0.0D)
/* 185 */       .sliderMax(1.0D)
/* 186 */       .visible(() -> (this.hopMode.get() == HopMode.Custom && this.mode.get() != Mode.Vanilla)))
/* 187 */       .build());
/*     */ 
/*     */   
/* 190 */   private final Setting<Integer> jumpTime = this.sgNCP.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder())
/* 191 */       .name("jump-time"))
/* 192 */       .description("How many ticks to recognise that you have jumped for smart mode."))
/* 193 */       .defaultValue(Integer.valueOf(20)))
/* 194 */       .min(0)
/* 195 */       .sliderMax(30)
/* 196 */       .visible(() -> (this.mode.get() == Mode.Smart)))
/* 197 */       .build());
/*     */ 
/*     */   
/* 200 */   private final Setting<Double> jumpedSlowDown = this.sgNCP.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder())
/* 201 */       .name("jumped-slow-down"))
/* 202 */       .description("How much to slow down by after jumping."))
/* 203 */       .defaultValue(0.76D)
/* 204 */       .min(0.0D)
/* 205 */       .sliderMax(1.0D)
/* 206 */       .visible(() -> (this.mode.get() != Mode.Vanilla)))
/* 207 */       .build());
/*     */ 
/*     */   
/* 210 */   private final Setting<Double> resetDivisor = this.sgNCP.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder())
/* 211 */       .name("reset-divisor"))
/* 212 */       .description("Speed value get divided by this amount on rubberband or collision."))
/* 213 */       .defaultValue(159.0D)
/* 214 */       .min(0.0D)
/* 215 */       .sliderMax(200.0D)
/* 216 */       .visible(() -> (this.mode.get() != Mode.Vanilla)))
/* 217 */       .build());
/*     */ 
/*     */ 
/*     */   
/* 221 */   private final Setting<Boolean> applyJumpBoost = this.sgPotion.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder())
/* 222 */       .name("jump-boost"))
/* 223 */       .description("Apply jump boost effect if the player has it."))
/* 224 */       .defaultValue(Boolean.valueOf(true)))
/* 225 */       .visible(() -> (this.mode.get() != Mode.Vanilla)))
/* 226 */       .build());
/*     */ 
/*     */   
/* 229 */   private final Setting<Boolean> applySpeed = this.sgPotion.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder())
/* 230 */       .name("speed-effect"))
/* 231 */       .description("Apply speed effect if the player has it."))
/* 232 */       .defaultValue(Boolean.valueOf(true)))
/* 233 */       .build());
/*     */ 
/*     */   
/* 236 */   private final Setting<Boolean> applySlowness = this.sgPotion.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder())
/* 237 */       .name("slowness-effect"))
/* 238 */       .description("Apply slowness effect if the player has it."))
/* 239 */       .defaultValue(Boolean.valueOf(true)))
/* 240 */       .build());
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 245 */   private final Setting<Boolean> longJumpPause = this.sgPause.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder())
/* 246 */       .name("pause-on-long-jump"))
/* 247 */       .description("Pauses the module if long jump is active."))
/* 248 */       .defaultValue(Boolean.valueOf(false)))
/* 249 */       .build());
/*     */ 
/*     */   
/* 252 */   private final Setting<Boolean> flightPause = this.sgPause.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder())
/* 253 */       .name("pause-on-flight"))
/* 254 */       .description("Pauses the module if flight is active."))
/* 255 */       .defaultValue(Boolean.valueOf(false)))
/* 256 */       .build());
/*     */ 
/*     */   
/* 259 */   private final Setting<Boolean> eFlyPause = this.sgPause.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder())
/* 260 */       .name("pause-on-elytra-fly"))
/* 261 */       .description("Pauses the module if elytra fly is active."))
/* 262 */       .defaultValue(Boolean.valueOf(false)))
/* 263 */       .build());
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 268 */   private final Setting<Boolean> inWater = this.sgAC.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder())
/* 269 */       .name("in-water"))
/* 270 */       .description("Uses speed when in water."))
/* 271 */       .defaultValue(Boolean.valueOf(false)))
/* 272 */       .build());
/*     */ 
/*     */   
/* 275 */   private final Setting<Boolean> inLava = this.sgAC.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder())
/* 276 */       .name("in-lava"))
/* 277 */       .description("Uses speed when in lava."))
/* 278 */       .defaultValue(Boolean.valueOf(false)))
/* 279 */       .build());
/*     */ 
/*     */   
/* 282 */   private final Setting<Boolean> whenSneaking = this.sgAC.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder())
/* 283 */       .name("when-sneaking"))
/* 284 */       .description("Uses speed when sneaking."))
/* 285 */       .defaultValue(Boolean.valueOf(false)))
/* 286 */       .build());
/*     */ 
/*     */   
/* 289 */   private final Setting<Boolean> hungerCheck = this.sgAC.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder())
/* 290 */       .name("hunger-check"))
/* 291 */       .description("Pauses when hunger reaches 3 or less drumsticks."))
/* 292 */       .defaultValue(Boolean.valueOf(true)))
/* 293 */       .build());
/*     */ 
/*     */   
/* 296 */   private final Setting<WebbedPause> webbedPause = this.sgAC.add((Setting)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder())
/* 297 */       .name("pause-on-webbed"))
/* 298 */       .description("Pauses when you are webbed."))
/* 299 */       .defaultValue(WebbedPause.OnAir))
/* 300 */       .build()); private int stage; private double distance; private double speed; private long timer;
/*     */   private int rubberbandTicks;
/*     */   private boolean rubberbanded;
/*     */   
/*     */   public StrafePlus() {
/* 305 */     super(NovaAddon.nova, "Straf-Plus", "Strafeeeeeeeee");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 311 */     this.timer = 0L;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 317 */     this.timerClass = (Timer)Modules.get().get(Timer.class);
/* 318 */     this.longJump = (LongJump)Modules.get().get(LongJump.class);
/* 319 */     this.flight = (Flight)Modules.get().get(Flight.class);
/* 320 */     this.efly = (ElytraFly)Modules.get().get(ElytraFly.class);
/*     */   }
/*     */   private boolean sentMessage; private int jumpTicks; private boolean jumped; Timer timerClass; LongJump longJump; Flight flight; ElytraFly efly;
/*     */   
/*     */   public void onDeactivate() {
/* 325 */     this.timerClass.setOverride(1.0D);
/*     */   }
/*     */   
/*     */   @EventHandler
/*     */   private void onPlayerMove(PlayerMoveEvent event) {
/* 330 */     if (event.type != class_1313.field_6308 || this.mc.field_1724.method_6128() || this.mc.field_1724.method_6101() || this.mc.field_1724.method_5854() != null)
/* 331 */       return;  if (!((Boolean)this.whenSneaking.get()).booleanValue() && this.mc.field_1724.method_5715())
/* 332 */       return;  if (!((Boolean)this.inWater.get()).booleanValue() && this.mc.field_1724.method_5799())
/* 333 */       return;  if (!((Boolean)this.inLava.get()).booleanValue() && this.mc.field_1724.method_5771())
/* 334 */       return;  if (((Boolean)this.hungerCheck.get()).booleanValue() && this.mc.field_1724.method_7344().method_7586() <= 6)
/*     */       return; 
/* 336 */     if (((Boolean)this.longJumpPause.get()).booleanValue() && this.longJump.isActive())
/* 337 */       return;  if (((Boolean)this.flightPause.get()).booleanValue() && this.flight.isActive())
/* 338 */       return;  if (((Boolean)this.eFlyPause.get()).booleanValue() && this.efly.isActive())
/*     */       return; 
/* 340 */     if (NEntityUtils.isWebbed((class_1657)this.mc.field_1724) && this.webbedPause.get() == WebbedPause.Always)
/* 341 */       return;  if (NEntityUtils.isWebbed((class_1657)this.mc.field_1724) && !this.mc.field_1724.method_24828() && this.webbedPause.get() == WebbedPause.OnAir)
/*     */       return; 
/* 343 */     if (this.mc.field_1724.method_24828()) {
/* 344 */       this.timerClass.setOverride(PlayerUtils.isMoving() ? (((Double)this.groundTimer.get()).doubleValue() * TimerUtils.getTPSMatch(((Boolean)this.TPSSync.get()).booleanValue())) : 1.0D);
/*     */     } else {
/* 346 */       this.timerClass.setOverride(PlayerUtils.isMoving() ? (((Double)this.airTimer.get()).doubleValue() * TimerUtils.getTPSMatch(((Boolean)this.TPSSync.get()).booleanValue())) : 1.0D);
/*     */     } 
/*     */     
/* 349 */     if (this.mode.get() == Mode.Vanilla && !this.rubberbanded) {
/* 350 */       if (this.mc.field_1724.method_24828()) {
/* 351 */         if (this.mc.field_1724.method_5715()) {
/* 352 */           class_243 vel = PlayerUtils.getHorizontalVelocity(((Double)this.vanillaSneakSpeed.get()).doubleValue());
/* 353 */           double velX = vel.method_10216();
/* 354 */           double velZ = vel.method_10215();
/*     */           
/* 356 */           if (this.mc.field_1724.method_6059(class_1294.field_5904) && ((Boolean)this.applySpeed.get()).booleanValue()) {
/* 357 */             double value = (this.mc.field_1724.method_6112(class_1294.field_5904).method_5578() + 1) * 0.205D;
/* 358 */             velX += velX * value;
/* 359 */             velZ += velZ * value;
/*     */           } 
/*     */           
/* 362 */           if (this.mc.field_1724.method_6059(class_1294.field_5909) && ((Boolean)this.applySlowness.get()).booleanValue()) {
/* 363 */             double value = (this.mc.field_1724.method_6112(class_1294.field_5909).method_5578() + 1) * 0.205D;
/* 364 */             velX -= velX * value;
/* 365 */             velZ -= velZ * value;
/*     */           } 
/*     */ 
/*     */           
/* 369 */           ((IVec3d)event.movement).set(velX, event.movement.field_1351, velZ);
/*     */         } else {
/* 371 */           class_243 vel = PlayerUtils.getHorizontalVelocity(((Double)this.vanillaGroundSpeed.get()).doubleValue());
/* 372 */           double velX = vel.method_10216();
/* 373 */           double velZ = vel.method_10215();
/*     */           
/* 375 */           if (this.mc.field_1724.method_6059(class_1294.field_5904) && ((Boolean)this.applySpeed.get()).booleanValue()) {
/* 376 */             double value = (this.mc.field_1724.method_6112(class_1294.field_5904).method_5578() + 1) * 0.205D;
/* 377 */             velX += velX * value;
/* 378 */             velZ += velZ * value;
/*     */           } 
/*     */           
/* 381 */           if (this.mc.field_1724.method_6059(class_1294.field_5909) && ((Boolean)this.applySlowness.get()).booleanValue()) {
/* 382 */             double value = (this.mc.field_1724.method_6112(class_1294.field_5909).method_5578() + 1) * 0.205D;
/* 383 */             velX -= velX * value;
/* 384 */             velZ -= velZ * value;
/*     */           } 
/*     */ 
/*     */           
/* 388 */           ((IVec3d)event.movement).set(velX, event.movement.field_1351, velZ);
/*     */         } 
/*     */       } else {
/* 391 */         class_243 vel = PlayerUtils.getHorizontalVelocity(((Double)this.vanillaAirSpeed.get()).doubleValue());
/* 392 */         double velX = vel.method_10216();
/* 393 */         double velZ = vel.method_10215();
/*     */         
/* 395 */         if (this.mc.field_1724.method_6059(class_1294.field_5904) && ((Boolean)this.applySpeed.get()).booleanValue()) {
/* 396 */           double value = (this.mc.field_1724.method_6112(class_1294.field_5904).method_5578() + 1) * 0.205D;
/* 397 */           velX += velX * value;
/* 398 */           velZ += velZ * value;
/*     */         } 
/*     */         
/* 401 */         if (this.mc.field_1724.method_6059(class_1294.field_5909) && ((Boolean)this.applySlowness.get()).booleanValue()) {
/* 402 */           double value = (this.mc.field_1724.method_6112(class_1294.field_5909).method_5578() + 1) * 0.205D;
/* 403 */           velX -= velX * value;
/* 404 */           velZ -= velZ * value;
/*     */         } 
/*     */ 
/*     */         
/* 408 */         ((IVec3d)event.movement).set(velX, event.movement.field_1351, velZ);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 413 */     if (this.mode.get() == Mode.NCP) {
/* 414 */       switch (this.stage) {
/*     */         case 0:
/* 416 */           if (PlayerUtils.isMoving()) {
/* 417 */             this.stage++;
/* 418 */             this.speed = ((Double)this.startingSpeed.get()).doubleValue() * getDefaultSpeed() - 0.01D;
/*     */           } 
/*     */         case 1:
/* 421 */           if (!PlayerUtils.isMoving() || !this.mc.field_1724.method_24828())
/*     */             break; 
/* 423 */           if (this.hopMode.get() == HopMode.Auto) { ((IVec3d)event.movement).setY(getHop(0.40123128D)); }
/* 424 */           else { ((IVec3d)event.movement).setY(getHop(((Double)this.hopHeight.get()).doubleValue())); }
/* 425 */            this.speed *= ((Double)this.ncpSpeed.get()).doubleValue();
/* 426 */           this.stage++;
/*     */           break;
/*     */         case 2:
/* 429 */           this.speed = this.distance - ((Double)this.jumpedSlowDown.get()).doubleValue() * (this.distance - getDefaultSpeed());
/* 430 */           this.stage++;
/*     */           break;
/*     */         case 3:
/* 433 */           if (!this.mc.field_1687.method_18026(this.mc.field_1724.method_5829().method_989(0.0D, (this.mc.field_1724.method_18798()).field_1351, 0.0D)) || (this.mc.field_1724.field_5992 && this.stage > 0)) {
/* 434 */             this.stage = 0;
/*     */           }
/* 436 */           this.speed = this.distance - this.distance / ((Double)this.resetDivisor.get()).doubleValue();
/*     */           break;
/*     */       } 
/*     */       
/* 440 */       this.speed = Math.max(this.speed, getDefaultSpeed());
/*     */       
/* 442 */       if (((Boolean)this.ncpSpeedLimit.get()).booleanValue()) {
/* 443 */         if (System.currentTimeMillis() - this.timer > 2500L) {
/* 444 */           this.timer = System.currentTimeMillis();
/*     */         }
/*     */         
/* 447 */         this.speed = Math.min(this.speed, (System.currentTimeMillis() - this.timer > 1250L) ? 0.44D : 0.43D);
/*     */       } 
/*     */       
/* 450 */       Vec2 change = transformStrafe(this.speed);
/*     */       
/* 452 */       double velX = change.x;
/* 453 */       double velZ = change.y;
/*     */ 
/*     */ 
/*     */       
/* 457 */       ((IVec3d)event.movement).setXZ(velX, velZ);
/*     */     } 
/*     */     
/* 460 */     if (this.mode.get() == Mode.Smart) {
/* 461 */       switch (this.stage) {
/*     */         case 0:
/* 463 */           if (PlayerUtils.isMoving()) {
/* 464 */             this.stage++;
/* 465 */             this.speed = ((Double)this.startingSpeed.get()).doubleValue() * getDefaultSpeed() - 0.01D;
/*     */           } 
/*     */         case 1:
/* 468 */           if (!PlayerUtils.isMoving() || !this.mc.field_1724.method_24828())
/*     */             break; 
/* 470 */           if (this.jumped) {
/* 471 */             if (this.hopMode.get() == HopMode.Auto) { ((IVec3d)event.movement).setY(getHop(0.40123128D)); }
/* 472 */             else { ((IVec3d)event.movement).setY(getHop(((Double)this.hopHeight.get()).doubleValue())); }
/* 473 */              this.speed *= ((Double)this.ncpSpeed.get()).doubleValue();
/* 474 */             this.stage++;
/*     */           } 
/*     */           break;
/*     */         case 2:
/* 478 */           this.speed = this.distance - ((Double)this.jumpedSlowDown.get()).doubleValue() * (this.distance - getDefaultSpeed());
/* 479 */           this.stage++;
/*     */           break;
/*     */         case 3:
/* 482 */           if (!this.mc.field_1687.method_18026(this.mc.field_1724.method_5829().method_989(0.0D, (this.mc.field_1724.method_18798()).field_1351, 0.0D)) || (this.mc.field_1724.field_5992 && this.stage > 0)) {
/* 483 */             this.stage = 0;
/*     */           }
/* 485 */           this.speed = this.distance - this.distance / ((Double)this.resetDivisor.get()).doubleValue();
/*     */           break;
/*     */       } 
/*     */       
/* 489 */       this.speed = Math.max(this.speed, getDefaultSpeed());
/*     */       
/* 491 */       if (((Boolean)this.ncpSpeedLimit.get()).booleanValue()) {
/* 492 */         if (System.currentTimeMillis() - this.timer > 2500L) {
/* 493 */           this.timer = System.currentTimeMillis();
/*     */         }
/*     */         
/* 496 */         this.speed = Math.min(this.speed, (System.currentTimeMillis() - this.timer > 1250L) ? 0.44D : 0.43D);
/*     */       } 
/*     */       
/* 499 */       Vec2 change = transformStrafe(this.speed);
/*     */       
/* 501 */       double velX = change.x;
/* 502 */       double velZ = change.y;
/*     */ 
/*     */ 
/*     */       
/* 506 */       ((IVec3d)event.movement).setXZ(velX, velZ);
/*     */     } 
/*     */   }
/*     */   
/*     */   @EventHandler
/*     */   private void onPreTick(TickEvent.Pre event) {
/* 512 */     if (this.mc.field_1724.method_6128() || this.mc.field_1724.method_6101() || this.mc.field_1724.method_5854() != null)
/* 513 */       return;  if (!((Boolean)this.whenSneaking.get()).booleanValue() && this.mc.field_1724.method_5715())
/* 514 */       return;  if (!((Boolean)this.inWater.get()).booleanValue() && this.mc.field_1724.method_5799())
/* 515 */       return;  if (!((Boolean)this.inLava.get()).booleanValue() && this.mc.field_1724.method_5771())
/* 516 */       return;  if (((Boolean)this.hungerCheck.get()).booleanValue() && this.mc.field_1724.method_7344().method_7586() <= 6)
/*     */       return; 
/* 518 */     if (((Boolean)this.longJumpPause.get()).booleanValue() && this.longJump.isActive())
/* 519 */       return;  if (((Boolean)this.flightPause.get()).booleanValue() && this.flight.isActive())
/* 520 */       return;  if (((Boolean)this.eFlyPause.get()).booleanValue() && this.efly.isActive())
/*     */       return; 
/* 522 */     if (NWorldUtils.doesBoxTouchBlock(this.mc.field_1724.method_5829(), class_2246.field_10343) && this.webbedPause.get() == WebbedPause.Always)
/* 523 */       return;  if (NWorldUtils.doesBoxTouchBlock(this.mc.field_1724.method_5829(), class_2246.field_10343) && !this.mc.field_1724.method_24828() && this.webbedPause.get() == WebbedPause.OnAir)
/*     */       return; 
/* 525 */     if (this.mc.field_1724.field_6250 > 0.0F && ((Boolean)this.autoSprint.get()).booleanValue()) this.mc.field_1724.method_5728(true);
/*     */     
/* 527 */     if (((Boolean)this.rubberbandPause.get()).booleanValue() && this.mode.get() == Mode.Vanilla) {
/* 528 */       if (this.rubberbandTicks > 0) {
/* 529 */         this.rubberbandTicks--;
/* 530 */         this.rubberbanded = true;
/* 531 */         info("Rubberband detected... pausing", new Object[0]);
/* 532 */         this.sentMessage = false;
/*     */       } else {
/* 534 */         this.rubberbanded = false;
/* 535 */         if (!this.sentMessage) info("Continued", new Object[0]); 
/* 536 */         this.sentMessage = true;
/*     */       } 
/*     */     }
/*     */     
/* 540 */     if (this.mode.get() == Mode.Smart) {
/* 541 */       if (this.mc.field_1690.field_1903.method_1434() && this.mc.field_1724.method_24828()) this.jumpTicks = ((Integer)this.jumpTime.get()).intValue(); 
/* 542 */       if (this.jumpTicks > 0)
/* 543 */       { this.jumped = true;
/* 544 */         this.jumpTicks--; }
/* 545 */       else { this.jumped = false; }
/*     */       
/* 547 */       if (this.mc.field_1724.method_24828()) this.jumpTicks = 0;
/*     */     
/*     */     } 
/* 550 */     if (this.mode.get() != Mode.Vanilla) {
/* 551 */       this.distance = Math.sqrt((this.mc.field_1724.method_23317() - this.mc.field_1724.field_6014) * (this.mc.field_1724.method_23317() - this.mc.field_1724.field_6014) + (this.mc.field_1724.method_23321() - this.mc.field_1724.field_5969) * (this.mc.field_1724.method_23321() - this.mc.field_1724.field_5969));
/*     */     }
/*     */   }
/*     */   
/*     */   @EventHandler
/*     */   private void onPacketReceive(PacketEvent.Receive event) {
/* 557 */     if (event.packet instanceof net.minecraft.class_2708) {
/* 558 */       this.rubberbandTicks = ((Integer)this.rubberbandTime.get()).intValue();
/* 559 */       reset();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private double getDefaultSpeed() {
/* 565 */     double defaultSpeed = 0.2873D;
/* 566 */     if (this.mc.field_1724.method_6059(class_1294.field_5904) && ((Boolean)this.applySpeed.get()).booleanValue()) {
/* 567 */       int amplifier = this.mc.field_1724.method_6112(class_1294.field_5904).method_5578();
/* 568 */       defaultSpeed *= 1.0D + 0.2D * (amplifier + 1);
/*     */     } 
/* 570 */     if (this.mc.field_1724.method_6059(class_1294.field_5909) && ((Boolean)this.applySlowness.get()).booleanValue()) {
/* 571 */       int amplifier = this.mc.field_1724.method_6112(class_1294.field_5909).method_5578();
/* 572 */       defaultSpeed /= 1.0D + 0.2D * (amplifier + 1);
/*     */     } 
/* 574 */     return defaultSpeed;
/*     */   }
/*     */   
/*     */   private void reset() {
/* 578 */     this.stage = 0;
/* 579 */     this.distance = 0.0D;
/* 580 */     this.speed = 0.2873D;
/*     */   }
/*     */   
/*     */   private double getHop(double height) {
/* 584 */     class_1293 jumpBoost = this.mc.field_1724.method_6059(class_1294.field_5913) ? this.mc.field_1724.method_6112(class_1294.field_5913) : null;
/* 585 */     if (jumpBoost != null && ((Boolean)this.applyJumpBoost.get()).booleanValue()) height += ((jumpBoost.method_5578() + 1) * 0.1F); 
/* 586 */     return height;
/*     */   }
/*     */   
/*     */   private Vec2 transformStrafe(double speed) {
/* 590 */     float forward = this.mc.field_1724.field_3913.field_3905;
/* 591 */     float side = this.mc.field_1724.field_3913.field_3907;
/* 592 */     float yaw = this.mc.field_1724.field_5982 + (this.mc.field_1724.method_36454() - this.mc.field_1724.field_5982) * this.mc.method_1488();
/*     */ 
/*     */ 
/*     */     
/* 596 */     if (forward == 0.0F && side == 0.0F) return new Vec2(0.0D, 0.0D);
/*     */     
/* 598 */     if (forward != 0.0F) {
/* 599 */       if (side >= 1.0F) {
/* 600 */         yaw += ((forward > 0.0F) ? -45 : 45);
/* 601 */         side = 0.0F;
/* 602 */       } else if (side <= -1.0F) {
/* 603 */         yaw += ((forward > 0.0F) ? 45 : -45);
/* 604 */         side = 0.0F;
/*     */       } 
/*     */       
/* 607 */       if (forward > 0.0F) {
/* 608 */         forward = 1.0F;
/*     */       }
/* 610 */       else if (forward < 0.0F) {
/* 611 */         forward = -1.0F;
/*     */       } 
/*     */     } 
/* 614 */     double mx = Math.cos(Math.toRadians((yaw + 90.0F)));
/* 615 */     double mz = Math.sin(Math.toRadians((yaw + 90.0F)));
/*     */     
/* 617 */     double velX = forward * speed * mx + side * speed * mz;
/* 618 */     double velZ = forward * speed * mz - side * speed * mx;
/*     */     
/* 620 */     return new Vec2(velX, velZ);
/*     */   }
/*     */ }


/* Location:              C:\Users\Shotgun\OneDrive\Desktop\Nova-v-100000 (1).jar!\Nova\modules\combat\StrafePlus.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */