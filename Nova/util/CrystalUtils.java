/*     */ package Nova.util;
/*     */ 
/*     */ import Nova.modules.combat.NovaCrystal;
/*     */ import meteordevelopment.meteorclient.MeteorClient;
/*     */ import meteordevelopment.meteorclient.systems.modules.Modules;
/*     */ import meteordevelopment.meteorclient.utils.entity.EntityUtils;
/*     */ import meteordevelopment.meteorclient.utils.misc.Keybind;
/*     */ import net.minecraft.class_1268;
/*     */ import net.minecraft.class_1297;
/*     */ import net.minecraft.class_1657;
/*     */ import net.minecraft.class_1799;
/*     */ import net.minecraft.class_2338;
/*     */ import net.minecraft.class_2596;
/*     */ import net.minecraft.class_2824;
/*     */ import net.minecraft.class_2879;
/*     */ 
/*     */ public class CrystalUtils
/*     */ {
/*  19 */   static NovaCrystal BBomber = (NovaCrystal)Modules.get().get(NovaCrystal.class);
/*     */   
/*     */   public static int getPlaceDelay() {
/*  22 */     if (isBurrowBreaking()) return ((Integer)BBomber.burrowBreakDelay.get()).intValue(); 
/*  23 */     if (isSurroundBreaking()) return ((Integer)BBomber.surroundBreakDelay.get()).intValue(); 
/*  24 */     return ((Integer)BBomber.placeDelay.get()).intValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public static void attackCrystal(class_1297 entity) {
/*  29 */     MeteorClient.mc.field_1724.field_3944.method_2883((class_2596)class_2824.method_34206(entity, MeteorClient.mc.field_1724.method_5715()));
/*     */     
/*  31 */     if (((Boolean)BBomber.renderSwing.get()).booleanValue()) MeteorClient.mc.field_1724.method_6104(class_1268.field_5808); 
/*  32 */     if (!((Boolean)BBomber.hideSwings.get()).booleanValue()) MeteorClient.mc.method_1562().method_2883((class_2596)new class_2879(class_1268.field_5808)); 
/*  33 */     BBomber.attacks++;
/*     */     
/*  35 */     getBreakDelay();
/*     */     
/*  37 */     if (((Boolean)BBomber.debug.get()).booleanValue()) BBomber.warning("Breaking", new Object[0]);
/*     */   
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean targetJustPopped() {
/*  43 */     if (((Boolean)BBomber.targetPopInvincibility.get()).booleanValue()) {
/*  44 */       return !BBomber.targetPoppedTimer.passedMillis(((Integer)BBomber.targetPopInvincibilityTime.get()).intValue());
/*     */     }
/*     */     
/*  47 */     return false;
/*     */   }
/*     */   
/*     */   public static boolean shouldIgnoreSelfPlaceDamage() {
/*  51 */     return (BBomber.PDamageIgnore.get() == NovaCrystal.DamageIgnore.Always || (((Boolean)BBomber.selfPopInvincibility
/*     */       
/*  53 */       .get()).booleanValue() && BBomber.selfPopIgnore.get() != NovaCrystal.SelfPopIgnore.Break && !BBomber.selfPoppedTimer.passedMillis(((Integer)BBomber.selfPopInvincibilityTime.get()).intValue())));
/*     */   }
/*     */   
/*     */   public static boolean shouldIgnoreSelfBreakDamage() {
/*  57 */     return (BBomber.BDamageIgnore.get() == NovaCrystal.DamageIgnore.Always || (BBomber.BDamageIgnore
/*  58 */       .get() == NovaCrystal.DamageIgnore.WhileSafe && (NEntityUtils.isSurrounded((class_1657)MeteorClient.mc.field_1724, NEntityUtils.BlastResistantType.Any) || NEntityUtils.isBurrowed((class_1657)MeteorClient.mc.field_1724, NEntityUtils.BlastResistantType.Any))) || (((Boolean)BBomber.selfPopInvincibility
/*  59 */       .get()).booleanValue() && BBomber.selfPopIgnore.get() != NovaCrystal.SelfPopIgnore.Place && !BBomber.selfPoppedTimer.passedMillis(((Integer)BBomber.selfPopInvincibilityTime.get()).intValue())));
/*     */   }
/*     */   
/*     */   private static void getBreakDelay() {
/*  63 */     if (isSurroundHolding() && BBomber.surroundHoldMode.get() != NovaCrystal.SlowMode.Age)
/*  64 */     { BBomber.breakTimer = ((Integer)BBomber.surroundHoldDelay.get()).intValue(); }
/*  65 */     else if (((Boolean)BBomber.slowFacePlace.get()).booleanValue() && BBomber.slowFPMode.get() != NovaCrystal.SlowMode.Age && isFacePlacing() && BBomber.bestTarget != null && BBomber.bestTarget.method_23318() < BBomber.placingCrystalBlockPos.method_10264())
/*  66 */     { BBomber.breakTimer = ((Integer)BBomber.slowFPDelay.get()).intValue(); }
/*  67 */     else { BBomber.breakTimer = ((Integer)BBomber.breakDelay.get()).intValue(); }
/*     */   
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean shouldFacePlace(class_2338 crystal) {
/*  73 */     for (class_1657 target : BBomber.targets) {
/*  74 */       class_2338 pos = target.method_24515();
/*  75 */       if (NEntityUtils.isFaceSurrounded(target, NEntityUtils.BlastResistantType.Any)) return false; 
/*  76 */       if (((Boolean)BBomber.surrHoldPause.get()).booleanValue() && isSurroundHolding()) return false;
/*     */       
/*  78 */       if (crystal.method_10264() == pos.method_10264() + 1 && Math.abs(pos.method_10263() - crystal.method_10263()) <= 1 && Math.abs(pos.method_10260() - crystal.method_10260()) <= 1) {
/*  79 */         if (EntityUtils.getTotalHealth(target) <= ((Double)BBomber.facePlaceHealth.get()).doubleValue()) return true;
/*     */         
/*  81 */         for (class_1799 itemStack : target.method_5661()) {
/*  82 */           if (itemStack == null || itemStack.method_7960()) {
/*  83 */             if (((Boolean)BBomber.facePlaceArmor.get()).booleanValue()) return true; 
/*     */             continue;
/*     */           } 
/*  86 */           if (((itemStack.method_7936() - itemStack.method_7919()) / itemStack.method_7936() * 100.0F) <= ((Double)BBomber.facePlaceDurability.get()).doubleValue()) return true;
/*     */         
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/*  92 */     return false;
/*     */   }
/*     */   
/*     */   public static boolean isFacePlacing() {
/*  96 */     return (((Boolean)BBomber.facePlace.get()).booleanValue() || ((Keybind)BBomber.forceFacePlace.get()).isPressed());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean shouldBurrowBreak(class_2338 crystal) {
/* 102 */     class_2338 pos = BBomber.bestTarget.method_24515();
/*     */     
/* 104 */     if (!isBurrowBreaking()) return false;
/*     */     
/* 106 */     return ((crystal.method_10264() == pos.method_10264() - 1 || crystal.method_10264() == pos.method_10264()) && Math.abs(pos.method_10263() - crystal.method_10263()) <= 1 && Math.abs(pos.method_10260() - crystal.method_10260()) <= 1);
/*     */   }
/*     */   
/*     */   public static boolean isBurrowBreaking() {
/* 110 */     if ((((Boolean)BBomber.burrowBreak.get()).booleanValue() || ((Keybind)BBomber.forceBurrowBreak.get()).isPressed()) && 
/* 111 */       BBomber.bestTarget != null && NEntityUtils.isBurrowed(BBomber.bestTarget, NEntityUtils.BlastResistantType.Mineable)) {
/* 112 */       switch ((NovaCrystal.TrapType)BBomber.burrowBWhen.get()) {
/*     */         case BothTrapped:
/* 114 */           return NEntityUtils.isBothTrapped(BBomber.bestTarget, NEntityUtils.BlastResistantType.Any);
/*     */         
/*     */         case AnyTrapped:
/* 117 */           return NEntityUtils.isAnyTrapped(BBomber.bestTarget, NEntityUtils.BlastResistantType.Any);
/*     */         
/*     */         case TopTrapped:
/* 120 */           return NEntityUtils.isTopTrapped(BBomber.bestTarget, NEntityUtils.BlastResistantType.Any);
/*     */         
/*     */         case FaceTrapped:
/* 123 */           return NEntityUtils.isFaceSurrounded(BBomber.bestTarget, NEntityUtils.BlastResistantType.Any);
/*     */         
/*     */         case Always:
/* 126 */           return true;
/*     */       } 
/*     */ 
/*     */ 
/*     */     
/*     */     }
/* 132 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean shouldSurroundBreak(class_2338 crystal) {
/* 140 */     class_2338 pos = BBomber.bestTarget.method_24515();
/*     */ 
/*     */     
/* 143 */     if (!isSurroundBreaking()) return false;
/*     */ 
/*     */     
/* 146 */     return ((
/* 147 */       !NEntityUtils.isBedrock(pos.method_10076(1)) && (crystal
/* 148 */       .equals(pos.method_10076(2)) || (((Boolean)BBomber.surroundBHorse
/* 149 */       .get()).booleanValue() && (crystal.equals(pos.method_10076(2).method_10067()) || crystal.equals(pos.method_10076(2).method_10078()))) || (((Boolean)BBomber.surroundBDiagonal
/* 150 */       .get()).booleanValue() && (crystal.equals(pos.method_10095().method_10067()) || crystal.equals(pos.method_10095().method_10078()))))) || (
/*     */ 
/*     */       
/* 153 */       !NEntityUtils.isBedrock(pos.method_10077(1)) && (crystal
/* 154 */       .equals(pos.method_10077(2)) || (((Boolean)BBomber.surroundBHorse
/* 155 */       .get()).booleanValue() && (crystal.equals(pos.method_10077(2).method_10067()) || crystal.equals(pos.method_10077(2).method_10078()))) || (((Boolean)BBomber.surroundBDiagonal
/* 156 */       .get()).booleanValue() && (crystal.equals(pos.method_10072().method_10067()) || crystal.equals(pos.method_10072().method_10078()))))) || (
/*     */ 
/*     */       
/* 159 */       !NEntityUtils.isBedrock(pos.method_10088(1)) && (crystal
/* 160 */       .equals(pos.method_10088(2)) || (((Boolean)BBomber.surroundBHorse
/* 161 */       .get()).booleanValue() && (crystal.equals(pos.method_10088(2).method_10095()) || crystal.equals(pos.method_10088(2).method_10072()))) || (((Boolean)BBomber.surroundBDiagonal
/* 162 */       .get()).booleanValue() && (crystal.equals(pos.method_10067().method_10095()) || crystal.equals(pos.method_10067().method_10072()))))) || (
/*     */ 
/*     */       
/* 165 */       !NEntityUtils.isBedrock(pos.method_10089(1)) && (crystal
/* 166 */       .equals(pos.method_10089(2)) || (((Boolean)BBomber.surroundBHorse
/* 167 */       .get()).booleanValue() && (crystal.equals(pos.method_10089(2).method_10095()) || crystal.equals(pos.method_10089(2).method_10072()))) || (((Boolean)BBomber.surroundBDiagonal
/* 168 */       .get()).booleanValue() && (crystal.equals(pos.method_10078().method_10095()) || crystal.equals(pos.method_10078().method_10072()))))));
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isSurroundBreaking() {
/* 184 */     if ((((Boolean)BBomber.surroundBreak.get()).booleanValue() || ((Keybind)BBomber.forceSurroundBreak.get()).isPressed()) && 
/* 185 */       BBomber.bestTarget != null && NEntityUtils.isSurrounded(BBomber.bestTarget, NEntityUtils.BlastResistantType.Mineable)) {
/* 186 */       switch ((NovaCrystal.TrapType)BBomber.surroundBWhen.get()) {
/*     */         case BothTrapped:
/* 188 */           return NEntityUtils.isBothTrapped(BBomber.bestTarget, NEntityUtils.BlastResistantType.Any);
/*     */         
/*     */         case AnyTrapped:
/* 191 */           return NEntityUtils.isAnyTrapped(BBomber.bestTarget, NEntityUtils.BlastResistantType.Any);
/*     */         
/*     */         case TopTrapped:
/* 194 */           return NEntityUtils.isTopTrapped(BBomber.bestTarget, NEntityUtils.BlastResistantType.Any);
/*     */         
/*     */         case FaceTrapped:
/* 197 */           return NEntityUtils.isFaceSurrounded(BBomber.bestTarget, NEntityUtils.BlastResistantType.Any);
/*     */         
/*     */         case Always:
/* 200 */           return true;
/*     */       } 
/*     */ 
/*     */ 
/*     */     
/*     */     }
/* 206 */     return false;
/*     */   }
/*     */   
/*     */   public static boolean isSurroundHolding() {
/* 210 */     if (((Boolean)BBomber.surroundHold.get()).booleanValue() && BBomber.bestTarget != null && NEntityUtils.isSurroundBroken(BBomber.bestTarget, NEntityUtils.BlastResistantType.Any)) {
/* 211 */       switch ((NovaCrystal.TrapType)BBomber.surroundHWhen.get()) {
/*     */         case BothTrapped:
/* 213 */           return NEntityUtils.isBothTrapped(BBomber.bestTarget, NEntityUtils.BlastResistantType.Any);
/*     */         
/*     */         case AnyTrapped:
/* 216 */           return NEntityUtils.isAnyTrapped(BBomber.bestTarget, NEntityUtils.BlastResistantType.Any);
/*     */         
/*     */         case TopTrapped:
/* 219 */           return NEntityUtils.isTopTrapped(BBomber.bestTarget, NEntityUtils.BlastResistantType.Any);
/*     */         
/*     */         case FaceTrapped:
/* 222 */           return NEntityUtils.isFaceSurrounded(BBomber.bestTarget, NEntityUtils.BlastResistantType.Any);
/*     */         
/*     */         case Always:
/* 225 */           return true;
/*     */       } 
/*     */ 
/*     */     
/*     */     }
/* 230 */     return false;
/*     */   }
/*     */ }


/* Location:              C:\Users\Shotgun\OneDrive\Desktop\Nova-v-100000 (1).jar!\Nov\\util\CrystalUtils.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */