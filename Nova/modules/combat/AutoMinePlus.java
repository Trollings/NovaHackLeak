/*     */ package Nova.modules.combat;
/*     */ import Nova.util.NEntityUtils;
/*     */ import Nova.util.NPlayerUtils;
/*     */ import meteordevelopment.meteorclient.events.render.Render3DEvent;
/*     */ import meteordevelopment.meteorclient.renderer.ShapeMode;
/*     */ import meteordevelopment.meteorclient.settings.BoolSetting;
/*     */ import meteordevelopment.meteorclient.settings.ColorSetting;
/*     */ import meteordevelopment.meteorclient.settings.DoubleSetting;
/*     */ import meteordevelopment.meteorclient.settings.EnumSetting;
/*     */ import meteordevelopment.meteorclient.settings.IntSetting;
/*     */ import meteordevelopment.meteorclient.settings.Setting;
/*     */ import meteordevelopment.meteorclient.settings.SettingGroup;
/*     */ import meteordevelopment.meteorclient.systems.modules.Modules;
/*     */ import meteordevelopment.meteorclient.utils.entity.TargetUtils;
/*     */ import meteordevelopment.meteorclient.utils.player.FindItemResult;
/*     */ import meteordevelopment.meteorclient.utils.player.InvUtils;
/*     */ import meteordevelopment.meteorclient.utils.player.Rotations;
/*     */ import meteordevelopment.meteorclient.utils.render.color.SettingColor;
/*     */ import meteordevelopment.orbit.EventHandler;
/*     */ import net.minecraft.class_1268;
/*     */ import net.minecraft.class_1657;
/*     */ import net.minecraft.class_1799;
/*     */ import net.minecraft.class_1802;
/*     */ import net.minecraft.class_2338;
/*     */ import net.minecraft.class_2350;
/*     */ import net.minecraft.class_2596;
/*     */ import net.minecraft.class_2846;
/*     */ import net.minecraft.class_2879;
/*     */ 
/*     */ public class AutoMinePlus extends Module {
/*  31 */   private final SettingGroup sgGeneral = this.settings.getDefaultGroup();
/*  32 */   private final SettingGroup sgTarget = this.settings.createGroup("Targeting");
/*  33 */   private final SettingGroup sgToggles = this.settings.createGroup("Module Toggles");
/*  34 */   private final SettingGroup sgRender = this.settings.createGroup("Render");
/*     */   
/*     */   public enum Mode
/*     */   {
/*  38 */     Normal,
/*  39 */     Instant;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*  44 */   private final Setting<Mode> mode = this.sgGeneral.add((Setting)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder())
/*  45 */       .name("mode"))
/*  46 */       .description("How AutoCity should try and mine blocks."))
/*  47 */       .defaultValue(Mode.Normal))
/*  48 */       .build());
/*     */ 
/*     */   
/*  51 */   private final Setting<Integer> delay = this.sgGeneral.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder())
/*  52 */       .name("instamine-delay"))
/*  53 */       .description("Delay between mining a block in ticks."))
/*  54 */       .defaultValue(Integer.valueOf(1)))
/*  55 */       .min(0)
/*  56 */       .sliderMax(50)
/*  57 */       .visible(() -> (this.mode.get() == Mode.Instant)))
/*  58 */       .build());
/*     */ 
/*     */   
/*  61 */   private final Setting<Integer> amount = this.sgGeneral.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder())
/*  62 */       .name("mining-packets"))
/*  63 */       .description("The amount of mining packets to be sent in a bundle."))
/*  64 */       .defaultValue(Integer.valueOf(1)))
/*  65 */       .range(1, 5)
/*  66 */       .sliderRange(1, 5)
/*  67 */       .visible(() -> (this.mode.get() == Mode.Normal)))
/*  68 */       .build());
/*     */ 
/*     */   
/*  71 */   private final Setting<Boolean> autoSwitch = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder())
/*  72 */       .name("auto-switch"))
/*  73 */       .description("Switch to a pickaxe when AutoCity is enabled."))
/*  74 */       .defaultValue(Boolean.valueOf(true)))
/*  75 */       .build());
/*     */ 
/*     */   
/*  78 */   private final Setting<Boolean> support = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder())
/*  79 */       .name("support"))
/*  80 */       .description("Place a block below a cityable positions."))
/*  81 */       .defaultValue(Boolean.valueOf(true)))
/*  82 */       .build());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<Double> supportRange;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<Boolean> rotate;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<Boolean> chatInfo;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<Integer> instaToggle;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<Boolean> turnOnBBomber;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<Boolean> turnOffInstaMine;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<Double> targetRange;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<Double> mineRange;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<Boolean> prioBurrowed;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<Boolean> noCitySurrounded;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<Boolean> avoidSelf;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<Boolean> lastResort;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<Boolean> swing;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<Boolean> render;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<ShapeMode> shapeMode;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<SettingColor> sideColor;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Setting<SettingColor> lineColor;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private class_1657 playerTarget;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private class_2338 blockTarget;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean sentMessage;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean supportMessage;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean burrowMessage;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int delayLeft;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean mining;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int count;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private class_2350 direction;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AutoMinePlus() {
/* 224 */     super(NovaAddon.nova, "AutoMine-Plus", "Automatically mine a target's surround.");
/*     */     Objects.requireNonNull(this.support);
/*     */     this.supportRange = this.sgGeneral.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("support-range")).description("The range for placing support block.")).defaultValue(4.5D).range(0.0D, 6.0D).sliderRange(0.0D, 6.0D).visible(this.support::get)).build());
/*     */     this.rotate = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Automatically rotates you towards the city block.")).defaultValue(Boolean.valueOf(false))).build());
/*     */     this.chatInfo = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("chat-info")).description("Sends a message when it is trying to city someone.")).defaultValue(Boolean.valueOf(true))).build());
/*     */     this.instaToggle = this.sgGeneral.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("toggle-delay")).description("Amount of ticks the city block has to be air to auto toggle off.")).defaultValue(Integer.valueOf(40))).min(0).sliderMax(100).visible(() -> (this.mode.get() == Mode.Instant))).build());
/*     */     this.turnOnBBomber = this.sgToggles.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("turn-on-auto-crystal")).description("Automatically toggles Banana Bomber on if a block target is found.")).defaultValue(Boolean.valueOf(false))).build());
/*     */     this.turnOffInstaMine = this.sgToggles.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("turn-off-instamine")).description("Automatically toggles Instamine off if a block target is found.")).defaultValue(Boolean.valueOf(false))).build());
/*     */     this.targetRange = this.sgTarget.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("target-range")).description("The radius in which players get targeted.")).defaultValue(5.0D).range(0.0D, 6.0D).sliderRange(0.0D, 6.0D).build());
/*     */     this.mineRange = this.sgTarget.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("mining-range")).description("The radius which you can mine at.")).defaultValue(4.0D).range(0.0D, 6.0D).sliderRange(0.0D, 6.0D).build());
/*     */     this.prioBurrowed = this.sgTarget.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("mine-burrows")).description("Will mine a target's burrow before citying them.")).defaultValue(Boolean.valueOf(true))).build());
/*     */     this.noCitySurrounded = this.sgTarget.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("not-surrounded")).description("Will not city a target if they aren't surrounded.")).defaultValue(Boolean.valueOf(false))).build());
/*     */     this.avoidSelf = this.sgTarget.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("avoid-self")).description("Will avoid targeting your own surround.")).defaultValue(Boolean.valueOf(true))).build());
/*     */     Objects.requireNonNull(this.avoidSelf);
/*     */     this.lastResort = this.sgTarget.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("last-resort")).description("Will try to target your own surround if there are no other options.")).defaultValue(Boolean.valueOf(true))).visible(this.avoidSelf::get)).build());
/*     */     this.swing = this.sgRender.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render-swing")).description("Renders your swing client-side.")).defaultValue(Boolean.valueOf(false))).build());
/*     */     this.render = this.sgRender.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render-break")).description("Renders the block being broken.")).defaultValue(Boolean.valueOf(true))).build());
/*     */     this.shapeMode = this.sgRender.add((Setting)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Lines)).build());
/*     */     this.sideColor = this.sgRender.add((Setting)((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The color of the sides of the blocks being rendered.")).defaultValue(new SettingColor(255, 0, 0, 25))).build());
/* 243 */     this.lineColor = this.sgRender.add((Setting)((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The color of the lines of the blocks being rendered.")).defaultValue(new SettingColor(255, 0, 0, 255))).build()); } public void onActivate() { this.sentMessage = false;
/* 244 */     this.supportMessage = false;
/* 245 */     this.burrowMessage = false;
/* 246 */     this.count = 0;
/* 247 */     this.mining = false;
/* 248 */     this.delayLeft = 0;
/* 249 */     this.blockTarget = null;
/*     */     
/* 251 */     if (this.mode.get() == Mode.Instant) {
/* 252 */       if (TargetUtils.isBadTarget(this.playerTarget, ((Double)this.targetRange.get()).doubleValue())) {
/* 253 */         class_1657 search = TargetUtils.getPlayerTarget(((Double)this.targetRange.get()).doubleValue(), SortPriority.ClosestAngle);
/* 254 */         if (search != this.playerTarget) this.sentMessage = false; 
/* 255 */         this.playerTarget = search;
/*     */       } 
/*     */       
/* 258 */       if (TargetUtils.isBadTarget(this.playerTarget, ((Double)this.targetRange.get()).doubleValue())) {
/* 259 */         this.playerTarget = null;
/* 260 */         this.blockTarget = null;
/* 261 */         toggle();
/*     */         
/*     */         return;
/*     */       } 
/* 265 */       if (((Boolean)this.prioBurrowed.get()).booleanValue() && NEntityUtils.isBurrowed(this.playerTarget, NEntityUtils.BlastResistantType.Mineable))
/* 266 */       { this.blockTarget = this.playerTarget.method_24515();
/* 267 */         if (!this.burrowMessage && ((Boolean)this.chatInfo.get()).booleanValue()) {
/* 268 */           warning("Mining %s's burrow.", new Object[] { this.playerTarget.method_5820() });
/* 269 */           this.burrowMessage = true;
/*     */         }  }
/* 271 */       else if (((Boolean)this.avoidSelf.get()).booleanValue())
/* 272 */       { this.blockTarget = NEntityUtils.getTargetBlock(this.playerTarget);
/* 273 */         if (this.blockTarget == null && ((Boolean)this.lastResort.get()).booleanValue()) this.blockTarget = NEntityUtils.getCityBlock(this.playerTarget);  }
/* 274 */       else { this.blockTarget = NEntityUtils.getCityBlock(this.playerTarget); }
/*     */     
/*     */     }  }
/*     */ 
/*     */   
/*     */   public void onDeactivate() {
/* 280 */     if (this.mode.get() == Mode.Instant && this.blockTarget != null) {
/* 281 */       this.mc.method_1562().method_2883((class_2596)new class_2846(class_2846.class_2847.field_12971, this.blockTarget, this.direction));
/*     */     }
/* 283 */     this.blockTarget = null;
/* 284 */     this.playerTarget = null;
/*     */   }
/*     */ 
/*     */   
/*     */   @EventHandler
/*     */   private void onTick(TickEvent.Pre event) {
/* 290 */     if (this.mode.get() == Mode.Normal) {
/* 291 */       if (TargetUtils.isBadTarget(this.playerTarget, ((Double)this.targetRange.get()).doubleValue())) {
/* 292 */         class_1657 search = TargetUtils.getPlayerTarget(((Double)this.targetRange.get()).doubleValue(), SortPriority.LowestDistance);
/* 293 */         if (search != this.playerTarget) this.sentMessage = false; 
/* 294 */         this.playerTarget = search;
/*     */       } 
/*     */       
/* 297 */       if (TargetUtils.isBadTarget(this.playerTarget, ((Double)this.targetRange.get()).doubleValue())) {
/* 298 */         this.playerTarget = null;
/* 299 */         this.blockTarget = null;
/* 300 */         toggle();
/*     */         
/*     */         return;
/*     */       } 
/* 304 */       if (((Boolean)this.prioBurrowed.get()).booleanValue() && NEntityUtils.isBurrowed(this.playerTarget, NEntityUtils.BlastResistantType.Mineable))
/* 305 */       { this.blockTarget = this.playerTarget.method_24515();
/* 306 */         if (!this.burrowMessage && ((Boolean)this.chatInfo.get()).booleanValue()) {
/* 307 */           warning("Mining %s's burrow.", new Object[] { this.playerTarget.method_5820() });
/* 308 */           this.burrowMessage = true;
/*     */         }  }
/* 310 */       else { if (((Boolean)this.noCitySurrounded.get()).booleanValue() && !NEntityUtils.isSurrounded(this.playerTarget, NEntityUtils.BlastResistantType.Any)) {
/* 311 */           warning("%s is not surrounded... disabling", new Object[] { this.playerTarget.method_5820() });
/* 312 */           this.blockTarget = null;
/* 313 */           toggle(); return;
/*     */         } 
/* 315 */         if (((Boolean)this.avoidSelf.get()).booleanValue())
/* 316 */         { this.blockTarget = NEntityUtils.getTargetBlock(this.playerTarget);
/* 317 */           if (this.blockTarget == null && ((Boolean)this.lastResort.get()).booleanValue()) this.blockTarget = NEntityUtils.getCityBlock(this.playerTarget);  }
/* 318 */         else { this.blockTarget = NEntityUtils.getCityBlock(this.playerTarget); }
/*     */          }
/*     */     
/* 321 */     }  if (this.blockTarget == null) {
/* 322 */       error("No target block found... disabling.", new Object[0]);
/* 323 */       toggle();
/* 324 */       this.playerTarget = null; return;
/*     */     } 
/* 326 */     if (!this.sentMessage && ((Boolean)this.chatInfo.get()).booleanValue() && this.blockTarget != this.playerTarget.method_24515()) {
/* 327 */       warning("Attempting to city %s.", new Object[] { this.playerTarget.method_5820() });
/* 328 */       this.sentMessage = true;
/*     */     } 
/*     */     
/* 331 */     if (NPlayerUtils.distanceFromEye(this.blockTarget) > ((Double)this.mineRange.get()).doubleValue()) {
/* 332 */       error("Target block out of reach... disabling.", new Object[0]);
/* 333 */       toggle();
/*     */       
/*     */       return;
/*     */     } 
/* 337 */     if (((Boolean)this.turnOnBBomber.get()).booleanValue() && this.blockTarget != null && !((NovaCrystal)Modules.get().get(NovaCrystal.class)).isActive())
/* 338 */       ((NovaCrystal)Modules.get().get(NovaCrystal.class)).toggle(); 
/* 339 */     ((NovaCrystal)Modules.get().get(NovaCrystal.class)).toggle();
/*     */     
/* 341 */     FindItemResult pickaxe = InvUtils.find(itemStack -> (itemStack.method_7909() == class_1802.field_8377 || itemStack.method_7909() == class_1802.field_22024));
/*     */     
/* 343 */     if (!pickaxe.isHotbar()) {
/* 344 */       error("No pickaxe found... disabling.", new Object[0]);
/* 345 */       toggle();
/*     */       
/*     */       return;
/*     */     } 
/* 349 */     if (((Boolean)this.support.get()).booleanValue() && !NEntityUtils.isBurrowed(this.playerTarget, NEntityUtils.BlastResistantType.Any)) {
/* 350 */       if (NPlayerUtils.distanceFromEye(this.blockTarget.method_10087(1)) < ((Double)this.supportRange.get()).doubleValue()) {
/* 351 */         BlockUtils.place(this.blockTarget.method_10087(1), InvUtils.findInHotbar(new class_1792[] { class_1802.field_8281 }, ), ((Boolean)this.rotate.get()).booleanValue(), 0, true);
/* 352 */       } else if (!this.supportMessage && this.blockTarget != this.playerTarget.method_24515()) {
/* 353 */         warning("Unable to support %s... mining anyway.", new Object[] { this.playerTarget.method_5820() });
/* 354 */         this.supportMessage = true;
/*     */       } 
/*     */     }
/*     */     
/* 358 */     if (((Boolean)this.autoSwitch.get()).booleanValue()) InvUtils.swap(pickaxe.slot(), false);
/*     */     
/* 360 */     if (this.mode.get() == Mode.Normal) {
/* 361 */       if (((Boolean)this.rotate.get()).booleanValue()) { Rotations.rotate(Rotations.getYaw(this.blockTarget), Rotations.getPitch(this.blockTarget), () -> mine(this.blockTarget)); }
/* 362 */       else { mine(this.blockTarget); }
/*     */     
/*     */     }
/* 365 */     if (this.mode.get() == Mode.Instant) {
/* 366 */       if (this.playerTarget == null || !this.playerTarget.method_5805() || this.count >= ((Integer)this.instaToggle.get()).intValue()) {
/* 367 */         toggle();
/*     */       }
/*     */       
/* 370 */       this.direction = NEntityUtils.rayTraceCheck(this.blockTarget, true);
/* 371 */       if (!this.mc.field_1687.method_22347(this.blockTarget))
/* 372 */       { instamine(this.blockTarget); }
/* 373 */       else { this.count++; }
/*     */     
/*     */     } 
/*     */   }
/*     */   private void mine(class_2338 blockPos) {
/* 378 */     for (int packets = 0; packets < ((Integer)this.amount.get()).intValue(); packets++) {
/* 379 */       if (!this.mining) {
/* 380 */         this.mc.method_1562().method_2883((class_2596)new class_2846(class_2846.class_2847.field_12968, blockPos, class_2350.field_11036));
/* 381 */         this.mc.method_1562().method_2883((class_2596)new class_2879(class_1268.field_5808));
/* 382 */         if (((Boolean)this.swing.get()).booleanValue()) this.mc.field_1724.method_6104(class_1268.field_5808); 
/* 383 */         this.mc.method_1562().method_2883((class_2596)new class_2846(class_2846.class_2847.field_12973, blockPos, class_2350.field_11036));
/* 384 */         this.mining = true;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void instamine(class_2338 blockPos) {
/* 391 */     this.delayLeft--;
/* 392 */     if (!this.mining) {
/* 393 */       if (((Boolean)this.rotate.get()).booleanValue()) Rotations.rotate(Rotations.getYaw(this.blockTarget), Rotations.getPitch(this.blockTarget)); 
/* 394 */       this.mc.method_1562().method_2883((class_2596)new class_2879(class_1268.field_5808));
/* 395 */       if (((Boolean)this.swing.get()).booleanValue()) this.mc.field_1724.method_6104(class_1268.field_5808); 
/* 396 */       this.mc.method_1562().method_2883((class_2596)new class_2846(class_2846.class_2847.field_12968, blockPos, this.direction));
/* 397 */       this.mining = true;
/*     */     } 
/* 399 */     if (this.delayLeft <= 0) {
/* 400 */       if (((Boolean)this.rotate.get()).booleanValue()) Rotations.rotate(Rotations.getYaw(this.blockTarget), Rotations.getPitch(this.blockTarget)); 
/* 401 */       this.mc.method_1562().method_2883((class_2596)new class_2879(class_1268.field_5808));
/* 402 */       this.mc.method_1562().method_2883((class_2596)new class_2846(class_2846.class_2847.field_12973, blockPos, this.direction));
/* 403 */       this.delayLeft = ((Integer)this.delay.get()).intValue();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getInfoString() {
/* 410 */     return EntityUtils.getName((class_1297)this.playerTarget);
/*     */   }
/*     */   
/*     */   @EventHandler
/*     */   private void onRender(Render3DEvent event) {
/* 415 */     if (!((Boolean)this.render.get()).booleanValue() || this.blockTarget == null)
/* 416 */       return;  event.renderer.box(this.blockTarget, (Color)this.sideColor.get(), (Color)this.lineColor.get(), (ShapeMode)this.shapeMode.get(), 0);
/*     */   }
/*     */ }


/* Location:              C:\Users\Shotgun\OneDrive\Desktop\Nova-v-100000 (1).jar!\Nova\modules\combat\AutoMinePlus.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */