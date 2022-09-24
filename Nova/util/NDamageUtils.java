/*     */ package Nova.util;
/*     */ 
/*     */ import java.util.Objects;
/*     */ import meteordevelopment.meteorclient.MeteorClient;
/*     */ import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
/*     */ import meteordevelopment.meteorclient.mixininterface.IExplosion;
/*     */ import meteordevelopment.meteorclient.mixininterface.IRaycastContext;
/*     */ import meteordevelopment.meteorclient.mixininterface.IVec3d;
/*     */ import meteordevelopment.meteorclient.systems.friends.Friends;
/*     */ import meteordevelopment.meteorclient.utils.PostInit;
/*     */ import meteordevelopment.meteorclient.utils.Utils;
/*     */ import meteordevelopment.meteorclient.utils.entity.EntityUtils;
/*     */ import meteordevelopment.meteorclient.utils.player.PlayerUtils;
/*     */ import meteordevelopment.meteorclient.utils.world.Dimension;
/*     */ import meteordevelopment.orbit.EventHandler;
/*     */ import net.minecraft.class_1267;
/*     */ import net.minecraft.class_1280;
/*     */ import net.minecraft.class_1282;
/*     */ import net.minecraft.class_1293;
/*     */ import net.minecraft.class_1294;
/*     */ import net.minecraft.class_1297;
/*     */ import net.minecraft.class_1309;
/*     */ import net.minecraft.class_1657;
/*     */ import net.minecraft.class_1802;
/*     */ import net.minecraft.class_1890;
/*     */ import net.minecraft.class_1893;
/*     */ import net.minecraft.class_1922;
/*     */ import net.minecraft.class_1927;
/*     */ import net.minecraft.class_1934;
/*     */ import net.minecraft.class_1937;
/*     */ import net.minecraft.class_2246;
/*     */ import net.minecraft.class_2338;
/*     */ import net.minecraft.class_2350;
/*     */ import net.minecraft.class_238;
/*     */ import net.minecraft.class_239;
/*     */ import net.minecraft.class_243;
/*     */ import net.minecraft.class_2586;
/*     */ import net.minecraft.class_259;
/*     */ import net.minecraft.class_265;
/*     */ import net.minecraft.class_2680;
/*     */ import net.minecraft.class_3532;
/*     */ import net.minecraft.class_3959;
/*     */ import net.minecraft.class_3965;
/*     */ import net.minecraft.class_5134;
/*     */ 
/*     */ 
/*     */ public class NDamageUtils
/*     */ {
/*  49 */   private static final class_243 vec3d = new class_243(0.0D, 0.0D, 0.0D);
/*     */   private static class_1927 explosion;
/*     */   private static class_3959 raycastContext;
/*     */   
/*     */   @PostInit
/*     */   public static void init() {
/*  55 */     MeteorClient.EVENT_BUS.subscribe(NDamageUtils.class);
/*     */   }
/*     */   
/*     */   @EventHandler
/*     */   private static void onGameJoined(GameJoinedEvent event) {
/*  60 */     explosion = new class_1927((class_1937)MeteorClient.mc.field_1687, null, 0.0D, 0.0D, 0.0D, 6.0F, false, class_1927.class_4179.field_18687);
/*  61 */     raycastContext = new class_3959(null, null, class_3959.class_3960.field_17558, class_3959.class_242.field_1347, (class_1297)MeteorClient.mc.field_1724);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static float crystalDamage(class_1657 player, class_243 crystal, boolean predictMovement, double explosionRadius, boolean ignoreTerrain, boolean fullBlocks) {
/*  67 */     if (player == null) return 0.0F; 
/*  68 */     if (EntityUtils.getGameMode(player) == class_1934.field_9220 && !(player instanceof meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerEntity)) return 0.0F;
/*     */     
/*  70 */     ((IVec3d)vec3d).set((player.method_19538()).field_1352, (player.method_19538()).field_1351, (player.method_19538()).field_1350);
/*  71 */     if (predictMovement) ((IVec3d)vec3d).set(vec3d.field_1352 + (player.method_18798()).field_1352, vec3d.field_1351 + (player.method_18798()).field_1351, vec3d.field_1350 + (player.method_18798()).field_1350);
/*     */     
/*  73 */     float modDistance = (float)Math.sqrt(vec3d.method_1025(crystal));
/*  74 */     if (modDistance > explosionRadius) return 0.0F;
/*     */     
/*  76 */     float exposure = getExposure(crystal, (class_1297)player, predictMovement, raycastContext, ignoreTerrain, fullBlocks);
/*  77 */     float impact = (1.0F - modDistance / 12.0F) * exposure;
/*  78 */     float damage = (impact * impact + impact) * 42.0F + 1.0F;
/*     */ 
/*     */     
/*  81 */     damage = getDamageForDifficulty(damage);
/*     */     
/*  83 */     damage = class_1280.method_5496(damage, player.method_6096(), (float)player.method_5996(class_5134.field_23725).method_6194());
/*     */     
/*  85 */     damage = resistanceReduction((class_1309)player, damage);
/*     */     
/*  87 */     ((IExplosion)explosion).set(crystal, 6.0F, false);
/*     */     
/*  89 */     damage = blastProtReduction((class_1297)player, damage, explosion);
/*     */     
/*  91 */     return (damage < 0.0F) ? 0.0F : damage;
/*     */   }
/*     */   
/*     */   public static float crystalDamage(class_1657 player, class_243 crystal, double explosionRadius) {
/*  95 */     return crystalDamage(player, crystal, false, explosionRadius, false, false);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static float bedDamage(class_1657 player, class_243 bed, boolean predictMovement, boolean ignoreTerrain, boolean fullBlocks) {
/* 101 */     if (EntityUtils.getGameMode(player) == class_1934.field_9220 && !(player instanceof meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerEntity)) return 0.0F;
/*     */     
/* 103 */     ((IVec3d)vec3d).set((player.method_19538()).field_1352, (player.method_19538()).field_1351, (player.method_19538()).field_1350);
/* 104 */     if (predictMovement) ((IVec3d)vec3d).set(vec3d.field_1352 + (player.method_18798()).field_1352, vec3d.field_1351 + (player.method_18798()).field_1351, vec3d.field_1350 + (player.method_18798()).field_1350);
/*     */     
/* 106 */     float modDistance = (float)Math.sqrt(player.method_5707(bed));
/*     */     
/* 108 */     float exposure = getExposure(bed, (class_1297)player, predictMovement, raycastContext, ignoreTerrain, fullBlocks);
/* 109 */     float impact = (1.0F - modDistance * 0.1F) * exposure;
/* 110 */     float damage = (impact * impact + impact) * 35.0F + 1.0F;
/*     */ 
/*     */     
/* 113 */     damage = getDamageForDifficulty(damage);
/*     */     
/* 115 */     damage = class_1280.method_5496(damage, player.method_6096(), (float)player.method_5996(class_5134.field_23725).method_6194());
/*     */     
/* 117 */     damage = resistanceReduction((class_1309)player, damage);
/*     */     
/* 119 */     ((IExplosion)explosion).set(bed, 5.0F, true);
/*     */     
/* 121 */     damage = blastProtReduction((class_1297)player, damage, explosion);
/*     */     
/* 123 */     return (damage < 0.0F) ? 0.0F : damage;
/*     */   }
/*     */   
/*     */   public static float bedDamage(class_1657 player, class_243 bed) {
/* 127 */     return bedDamage(player, bed, false, false, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static float getSwordDamage(class_1657 entity, boolean charged) {
/* 134 */     float damage = 0.0F;
/* 135 */     if (charged) {
/* 136 */       if (entity.method_6030().method_7909() == class_1802.field_22022) {
/* 137 */         damage += 8.0F;
/* 138 */       } else if (entity.method_6030().method_7909() == class_1802.field_8802) {
/* 139 */         damage += 7.0F;
/* 140 */       } else if (entity.method_6030().method_7909() == class_1802.field_8845) {
/* 141 */         damage += 4.0F;
/* 142 */       } else if (entity.method_6030().method_7909() == class_1802.field_8371) {
/* 143 */         damage += 6.0F;
/* 144 */       } else if (entity.method_6030().method_7909() == class_1802.field_8528) {
/* 145 */         damage += 5.0F;
/* 146 */       } else if (entity.method_6030().method_7909() == class_1802.field_8091) {
/* 147 */         damage += 4.0F;
/*     */       } 
/* 149 */       damage = (float)(damage * 1.5D);
/*     */     } 
/*     */     
/* 152 */     if (entity.method_6030().method_7921() != null && 
/* 153 */       class_1890.method_8222(entity.method_6030()).containsKey(class_1893.field_9118)) {
/* 154 */       int level = class_1890.method_8225(class_1893.field_9118, entity.method_6030());
/* 155 */       damage = (float)(damage + 0.5D * level + 0.5D);
/*     */     } 
/*     */ 
/*     */     
/* 159 */     if (entity.method_6088().containsKey(class_1294.field_5910)) {
/* 160 */       int strength = ((class_1293)Objects.<class_1293>requireNonNull(entity.method_6112(class_1294.field_5910))).method_5578() + 1;
/* 161 */       damage += (3 * strength);
/*     */     } 
/*     */ 
/*     */     
/* 165 */     damage = resistanceReduction((class_1309)entity, damage);
/*     */ 
/*     */     
/* 168 */     damage = class_1280.method_5496(damage, entity.method_6096(), (float)entity.method_5996(class_5134.field_23725).method_6194());
/*     */ 
/*     */     
/* 171 */     damage = normalProtReduction((class_1297)entity, damage);
/*     */     
/* 173 */     return (damage < 0.0F) ? 0.0F : damage;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static float getDamageForDifficulty(float damage) {
/* 179 */     switch (MeteorClient.mc.field_1687.method_8407()) { case field_5801: case field_5805: case field_5807:  }  return 
/*     */ 
/*     */ 
/*     */       
/* 183 */       damage;
/*     */   }
/*     */ 
/*     */   
/*     */   private static float normalProtReduction(class_1297 player, float damage) {
/* 188 */     int protLevel = class_1890.method_8219(player.method_5661(), class_1282.field_5869);
/* 189 */     if (protLevel > 20) protLevel = 20;
/*     */     
/* 191 */     damage = (float)(damage * (1.0D - protLevel * 0.04D));
/* 192 */     return (damage < 0.0F) ? 0.0F : damage;
/*     */   }
/*     */   
/*     */   private static float blastProtReduction(class_1297 player, float damage, class_1927 explosion) {
/* 196 */     int protLevel = class_1890.method_8219(player.method_5661(), class_1282.method_5531(explosion));
/* 197 */     if (protLevel > 20) protLevel = 20;
/*     */     
/* 199 */     damage = (float)(damage * (1.0D - protLevel * 0.04D));
/* 200 */     return (damage < 0.0F) ? 0.0F : damage;
/*     */   }
/*     */   
/*     */   private static float resistanceReduction(class_1309 player, float damage) {
/* 204 */     if (player.method_6059(class_1294.field_5907)) {
/* 205 */       int lvl = player.method_6112(class_1294.field_5907).method_5578() + 1;
/* 206 */       damage = (float)(damage * (1.0D - lvl * 0.2D));
/*     */     } 
/*     */     
/* 209 */     return (damage < 0.0F) ? 0.0F : damage;
/*     */   }
/*     */   
/*     */   private static float getExposure(class_243 source, class_1297 entity, boolean predictMovement, class_3959 raycastContext, boolean ignoreTerrain, boolean fullBlocks) {
/* 213 */     class_238 box = entity.method_5829();
/* 214 */     if (predictMovement) {
/* 215 */       class_243 v = entity.method_18798();
/* 216 */       box.method_989(v.field_1352, v.field_1351, v.field_1350);
/*     */     } 
/*     */     
/* 219 */     double d = 1.0D / ((box.field_1320 - box.field_1323) * 2.0D + 1.0D);
/* 220 */     double e = 1.0D / ((box.field_1325 - box.field_1322) * 2.0D + 1.0D);
/* 221 */     double f = 1.0D / ((box.field_1324 - box.field_1321) * 2.0D + 1.0D);
/* 222 */     double g = (1.0D - Math.floor(1.0D / d) * d) * 0.5D;
/* 223 */     double h = (1.0D - Math.floor(1.0D / f) * f) * 0.5D;
/*     */     
/* 225 */     if (d >= 0.0D && e >= 0.0D && f >= 0.0D) {
/* 226 */       int i = 0;
/* 227 */       int j = 0;
/*     */       float k;
/* 229 */       for (k = 0.0F; k <= 1.0F; k = (float)(k + d)) {
/* 230 */         float l; for (l = 0.0F; l <= 1.0F; l = (float)(l + e)) {
/* 231 */           float m; for (m = 0.0F; m <= 1.0F; m = (float)(m + f)) {
/* 232 */             double n = class_3532.method_16436(k, box.field_1323, box.field_1320);
/* 233 */             double o = class_3532.method_16436(l, box.field_1322, box.field_1325);
/* 234 */             double p = class_3532.method_16436(m, box.field_1321, box.field_1324);
/*     */             
/* 236 */             ((IVec3d)vec3d).set(n + g, o, p + h);
/* 237 */             ((IRaycastContext)raycastContext).set(vec3d, source, class_3959.class_3960.field_17558, class_3959.class_242.field_1348, entity);
/*     */             
/* 239 */             if (raycast(raycastContext, ignoreTerrain, fullBlocks).method_17783() == class_239.class_240.field_1333) i++;
/*     */             
/* 241 */             j++;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */       
/* 246 */       return i / j;
/*     */     } 
/*     */     
/* 249 */     return 0.0F;
/*     */   }
/*     */   
/*     */   private static class_3965 raycast(class_3959 context, boolean ignoreTerrain, boolean fullBlocks) {
/* 253 */     return (class_3965)class_1922.method_17744(context.method_17750(), context.method_17747(), context, (raycastContext, blockPos) -> {
/*     */           class_2680 blockState = MeteorClient.mc.field_1687.method_8320(blockPos);
/*     */           if (blockState.method_26204() instanceof net.minecraft.class_2199 && fullBlocks) {
/*     */             blockState = class_2246.field_10540.method_9564();
/*     */           } else if (blockState.method_26204() instanceof net.minecraft.class_2336 && fullBlocks) {
/*     */             blockState = class_2246.field_10540.method_9564();
/*     */           } else if (blockState.method_26204().method_9520() < 600.0F && ignoreTerrain) {
/*     */             blockState = class_2246.field_10124.method_9564();
/*     */           } 
/*     */           class_243 vec3d = raycastContext.method_17750();
/*     */           class_243 vec3d2 = raycastContext.method_17747();
/*     */           class_265 voxelShape = raycastContext.method_17748(blockState, (class_1922)MeteorClient.mc.field_1687, blockPos);
/*     */           class_3965 blockHitResult = MeteorClient.mc.field_1687.method_17745(vec3d, vec3d2, blockPos, voxelShape, blockState);
/*     */           class_265 voxelShape2 = class_259.method_1073();
/*     */           class_3965 blockHitResult2 = voxelShape2.method_1092(vec3d, vec3d2, blockPos);
/*     */           double d = (blockHitResult == null) ? Double.MAX_VALUE : raycastContext.method_17750().method_1025(blockHitResult.method_17784());
/*     */           double e = (blockHitResult2 == null) ? Double.MAX_VALUE : raycastContext.method_17750().method_1025(blockHitResult2.method_17784());
/*     */           return (d <= e) ? blockHitResult : blockHitResult2;
/*     */         }raycastContext -> {
/*     */           class_243 vec3d = raycastContext.method_17750().method_1020(raycastContext.method_17747());
/*     */           return class_3965.method_17778(raycastContext.method_17747(), class_2350.method_10142(vec3d.field_1352, vec3d.field_1351, vec3d.field_1350), new class_2338(raycastContext.method_17747()));
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static float possibleHealthReductions(boolean crystals, double explosionRadius, boolean swords, float enemyDistance) {
/* 280 */     float damageTaken = 0.0F;
/*     */     
/* 282 */     for (class_1297 entity : MeteorClient.mc.field_1687.method_18112()) {
/* 283 */       if (crystals)
/*     */       {
/* 285 */         if (entity instanceof net.minecraft.class_1511 && damageTaken < crystalDamage((class_1657)MeteorClient.mc.field_1724, entity.method_19538(), explosionRadius)) {
/* 286 */           damageTaken = crystalDamage((class_1657)MeteorClient.mc.field_1724, entity.method_19538(), explosionRadius);
/*     */         }
/*     */       }
/*     */       
/* 290 */       if (swords)
/*     */       {
/* 292 */         if (entity instanceof class_1657 && damageTaken < getSwordDamage((class_1657)entity, true) && 
/* 293 */           !Friends.get().isFriend((class_1657)entity) && MeteorClient.mc.field_1724.method_19538().method_1022(entity.method_19538()) < enemyDistance && (
/* 294 */           (class_1657)entity).method_6030().method_7909() instanceof net.minecraft.class_1829) {
/* 295 */           damageTaken = getSwordDamage((class_1657)entity, true);
/*     */         }
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 303 */     if (PlayerUtils.getDimension() != Dimension.Overworld) {
/* 304 */       for (class_2586 blockEntity : Utils.blockEntities()) {
/* 305 */         class_2338 bp = blockEntity.method_11016();
/* 306 */         class_243 pos = new class_243(bp.method_10263(), bp.method_10264(), bp.method_10260());
/*     */         
/* 308 */         if (blockEntity instanceof net.minecraft.class_2587 && damageTaken < bedDamage((class_1657)MeteorClient.mc.field_1724, pos)) {
/* 309 */           damageTaken = bedDamage((class_1657)MeteorClient.mc.field_1724, pos);
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/* 314 */     return damageTaken;
/*     */   }
/*     */ }


/* Location:              C:\Users\Shotgun\OneDrive\Desktop\Nova-v-100000 (1).jar!\Nov\\util\NDamageUtils.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */