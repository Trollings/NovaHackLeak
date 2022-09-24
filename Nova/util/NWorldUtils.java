/*     */ package Nova.util;
/*     */ import meteordevelopment.meteorclient.MeteorClient;
/*     */ import meteordevelopment.meteorclient.mixininterface.IVec3d;
/*     */ import meteordevelopment.meteorclient.utils.player.FindItemResult;
/*     */ import meteordevelopment.meteorclient.utils.player.InvUtils;
/*     */ import meteordevelopment.meteorclient.utils.player.Rotations;
/*     */ import net.minecraft.class_1268;
/*     */ import net.minecraft.class_1297;
/*     */ import net.minecraft.class_1538;
/*     */ import net.minecraft.class_1657;
/*     */ import net.minecraft.class_1937;
/*     */ import net.minecraft.class_2248;
/*     */ import net.minecraft.class_2338;
/*     */ import net.minecraft.class_2350;
/*     */ import net.minecraft.class_238;
/*     */ import net.minecraft.class_243;
/*     */ import net.minecraft.class_2596;
/*     */ import net.minecraft.class_2680;
/*     */ import net.minecraft.class_2828;
/*     */ import net.minecraft.class_2848;
/*     */ import net.minecraft.class_2868;
/*     */ import net.minecraft.class_3965;
/*     */ 
/*     */ public class NWorldUtils {
/*     */   public enum SwitchMode {
/*  26 */     Packet,
/*  27 */     Client,
/*  28 */     Both;
/*     */   }
/*     */   
/*     */   public enum PlaceMode {
/*  32 */     Packet,
/*  33 */     Client,
/*  34 */     Both;
/*     */   }
/*     */   
/*     */   public enum AirPlaceDirection {
/*  38 */     Up,
/*  39 */     Down;
/*     */   }
/*     */   
/*  42 */   private static final class_243 hitPos = new class_243(0.0D, 0.0D, 0.0D);
/*     */   
/*     */   public static boolean place(class_2338 blockPos, FindItemResult findItemResult, boolean rotate, int rotationPriority, SwitchMode switchMode, PlaceMode placeMode, boolean onlyAirplace, AirPlaceDirection airPlaceDirection, boolean swingHand, boolean checkEntities, boolean swapBack) {
/*  45 */     if (findItemResult.isOffhand())
/*  46 */       return place(blockPos, class_1268.field_5810, (MeteorClient.mc.field_1724.method_31548()).field_7545, (MeteorClient.mc.field_1724.method_31548()).field_7545, rotate, rotationPriority, switchMode, placeMode, onlyAirplace, airPlaceDirection, swingHand, checkEntities, swapBack); 
/*  47 */     if (findItemResult.isHotbar()) {
/*  48 */       return place(blockPos, class_1268.field_5808, (MeteorClient.mc.field_1724.method_31548()).field_7545, findItemResult.slot(), rotate, rotationPriority, switchMode, placeMode, onlyAirplace, airPlaceDirection, swingHand, checkEntities, swapBack);
/*     */     }
/*  50 */     return false;
/*     */   }
/*     */   public static boolean place(class_2338 blockPos, class_1268 hand, int oldSlot, int targetSlot, boolean rotate, int rotationPriority, SwitchMode switchMode, PlaceMode placeMode, boolean onlyAirplace, AirPlaceDirection airPlaceDirection, boolean swingHand, boolean checkEntities, boolean swapBack) {
/*     */     class_2338 neighbour;
/*  54 */     if (targetSlot < 0 || targetSlot > 8) return false; 
/*  55 */     if (!canPlace(blockPos, checkEntities)) return false;
/*     */     
/*  57 */     ((IVec3d)hitPos).set(blockPos.method_10263() + 0.5D, blockPos.method_10264() + 0.5D, blockPos.method_10260() + 0.5D);
/*     */ 
/*     */     
/*  60 */     class_2350 side = getPlaceSide(blockPos);
/*     */     
/*  62 */     if (side == null || onlyAirplace) {
/*  63 */       if (airPlaceDirection == AirPlaceDirection.Up) { side = class_2350.field_11036; }
/*  64 */       else { side = class_2350.field_11033; }
/*  65 */        neighbour = blockPos;
/*     */     } else {
/*  67 */       neighbour = blockPos.method_10093(side.method_10153());
/*  68 */       hitPos.method_1031(side.method_10148() * 0.5D, side.method_10164() * 0.5D, side.method_10165() * 0.5D);
/*     */     } 
/*     */     
/*  71 */     class_2350 s = side;
/*     */     
/*  73 */     if (rotate) { Rotations.rotate(Rotations.getYaw(hitPos), Rotations.getPitch(hitPos), rotationPriority, () -> place(new class_3965(hitPos, s, neighbour, false), hand, oldSlot, targetSlot, switchMode, placeMode, swingHand, swapBack)); }
/*  74 */     else { place(new class_3965(hitPos, s, neighbour, false), hand, oldSlot, targetSlot, switchMode, placeMode, swingHand, swapBack); }
/*     */     
/*  76 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   private static void place(class_3965 blockHitResult, class_1268 hand, int oldSlot, int targetSlot, SwitchMode switchMode, PlaceMode placeMode, boolean swing, boolean swapBack) {
/*  81 */     MeteorClient.mc.field_1724.field_3944.method_2883((class_2596)new class_2848((class_1297)MeteorClient.mc.field_1724, class_2848.class_2849.field_12979));
/*     */     
/*  83 */     if (switchMode != SwitchMode.Client) MeteorClient.mc.field_1724.field_3944.method_2883((class_2596)new class_2868(targetSlot)); 
/*  84 */     if (switchMode != SwitchMode.Packet) InvUtils.swap(targetSlot, swapBack);
/*     */     
/*  86 */     if (placeMode != PlaceMode.Client) MeteorClient.mc.field_1724.field_3944.method_2883((class_2596)new class_2885(hand, blockHitResult, 0)); 
/*  87 */     if (placeMode != PlaceMode.Packet) MeteorClient.mc.field_1761.method_2896(MeteorClient.mc.field_1724, hand, blockHitResult);
/*     */     
/*  89 */     if (swing) { MeteorClient.mc.field_1724.method_6104(hand); }
/*  90 */     else { MeteorClient.mc.method_1562().method_2883((class_2596)new class_2879(hand)); }
/*     */     
/*  92 */     if (swapBack) {
/*  93 */       if (switchMode != SwitchMode.Client) MeteorClient.mc.field_1724.field_3944.method_2883((class_2596)new class_2868(oldSlot)); 
/*  94 */       if (switchMode != SwitchMode.Packet) InvUtils.swapBack();
/*     */     
/*     */     } 
/*  97 */     MeteorClient.mc.field_1724.field_3944.method_2883((class_2596)new class_2848((class_1297)MeteorClient.mc.field_1724, class_2848.class_2849.field_12984));
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean canPlace(class_2338 blockPos, boolean checkEntities) {
/* 102 */     if (!class_1937.method_25953(blockPos)) return false;
/*     */ 
/*     */     
/* 105 */     if (!MeteorClient.mc.field_1687.method_8320(blockPos).method_26207().method_15800()) return false;
/*     */ 
/*     */     
/* 108 */     return (!checkEntities || MeteorClient.mc.field_1687.method_8628(class_2246.field_10540.method_9564(), blockPos, class_3726.method_16194()));
/*     */   }
/*     */   
/*     */   public static boolean canPlace(class_2338 blockPos) {
/* 112 */     return canPlace(blockPos, true);
/*     */   }
/*     */   
/*     */   public static class_2350 getPlaceSide(class_2338 blockPos) {
/* 116 */     for (class_2350 side : class_2350.values()) {
/* 117 */       class_2338 neighbor = blockPos.method_10093(side);
/* 118 */       class_2350 side2 = side.method_10153();
/*     */       
/* 120 */       class_2680 state = MeteorClient.mc.field_1687.method_8320(neighbor);
/*     */ 
/*     */       
/* 123 */       if (!state.method_26215() && !isClickable(state.method_26204()))
/*     */       {
/*     */         
/* 126 */         if (state.method_26227().method_15769())
/*     */         {
/* 128 */           return side2; } 
/*     */       }
/*     */     } 
/* 131 */     return null;
/*     */   }
/*     */   
/*     */   public static boolean isClickable(class_2248 block) {
/* 135 */     return (block instanceof net.minecraft.class_2304 || block instanceof net.minecraft.class_2199 || block instanceof net.minecraft.class_2269 || block instanceof net.minecraft.class_2231 || block instanceof net.minecraft.class_2237 || block instanceof net.minecraft.class_2244 || block instanceof net.minecraft.class_2349 || block instanceof net.minecraft.class_2323 || block instanceof net.minecraft.class_2428 || block instanceof net.minecraft.class_2533 || block instanceof net.minecraft.class_2406 || block instanceof net.minecraft.class_3711 || block instanceof net.minecraft.class_3713 || block instanceof net.minecraft.class_3718);
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
/*     */   
/*     */   public static void lookAtBlock(class_2338 blockToLookAt) {
/* 152 */     rotate(calculateLookFromPlayer(blockToLookAt.method_10263(), blockToLookAt.method_10264(), blockToLookAt.method_10260(), (class_1657)MeteorClient.mc.field_1724));
/*     */   }
/*     */   
/*     */   public static void rotate(float yaw, float pitch) {
/* 156 */     MeteorClient.mc.field_1724.method_36456(yaw);
/* 157 */     MeteorClient.mc.field_1724.method_36457(pitch);
/*     */   }
/*     */   
/*     */   public static void rotate(double[] rotations) {
/* 161 */     MeteorClient.mc.field_1724.method_36456((float)rotations[0]);
/* 162 */     MeteorClient.mc.field_1724.method_36457((float)rotations[1]);
/*     */   }
/*     */   
/*     */   public static void snapPlayer(class_2338 lastPos) {
/* 166 */     double xPos = (MeteorClient.mc.field_1724.method_19538()).field_1352;
/* 167 */     double zPos = (MeteorClient.mc.field_1724.method_19538()).field_1350;
/*     */     
/* 169 */     if (Math.abs(lastPos.method_10263() + 0.5D - (MeteorClient.mc.field_1724.method_19538()).field_1352) >= 0.2D) {
/* 170 */       int xDir = (lastPos.method_10263() + 0.5D - (MeteorClient.mc.field_1724.method_19538()).field_1352 > 0.0D) ? 1 : -1;
/* 171 */       xPos += 0.3D * xDir;
/*     */     } 
/*     */     
/* 174 */     if (Math.abs(lastPos.method_10260() + 0.5D - (MeteorClient.mc.field_1724.method_19538()).field_1350) >= 0.2D) {
/* 175 */       int zDir = (lastPos.method_10260() + 0.5D - (MeteorClient.mc.field_1724.method_19538()).field_1350 > 0.0D) ? 1 : -1;
/* 176 */       zPos += 0.3D * zDir;
/*     */     } 
/*     */     
/* 179 */     MeteorClient.mc.field_1724.method_18800(0.0D, 0.0D, 0.0D);
/* 180 */     MeteorClient.mc.field_1724.method_5814(xPos, MeteorClient.mc.field_1724.method_23318(), zPos);
/* 181 */     MeteorClient.mc.field_1724.field_3944.method_2883((class_2596)new class_2828.class_2829(MeteorClient.mc.field_1724.method_23317(), MeteorClient.mc.field_1724.method_23318(), MeteorClient.mc.field_1724.method_23321(), MeteorClient.mc.field_1724.method_24828()));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static class_2338 roundBlockPos(class_243 vec) {
/* 187 */     return new class_2338(vec.field_1352, (int)Math.round(vec.field_1351), vec.field_1350);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static double getEyeY(class_1657 player) {
/* 193 */     return player.method_23318() + player.method_18381(player.method_18376());
/*     */   }
/*     */   
/*     */   public static double[] calculateLookFromPlayer(double x, double y, double z, class_1657 player) {
/* 197 */     return calculateAngle(new class_243(player.method_23317(), getEyeY(player), player.method_23321()), new class_243(x, y, z));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static double[] calculateAngle(class_243 a, class_243 b) {
/* 204 */     double x = a.field_1352 - b.field_1352;
/* 205 */     double y = a.field_1351 - b.field_1351;
/* 206 */     double z = a.field_1350 - b.field_1350;
/* 207 */     double d = Math.sqrt(x * x + y * y + z * z);
/* 208 */     double pitch = Math.toDegrees(Math.asin(y / d));
/* 209 */     double yaw = Math.toDegrees(Math.atan2(z / d, x / d) + 1.5707963267948966D);
/*     */     
/* 211 */     return new double[] { yaw, pitch };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean doesBoxTouchBlock(class_238 box, class_2248 block) {
/* 218 */     for (int x = (int)Math.floor(box.field_1323); x < Math.ceil(box.field_1320); x++) {
/* 219 */       for (int y = (int)Math.floor(box.field_1322); y < Math.ceil(box.field_1325); y++) {
/* 220 */         for (int z = (int)Math.floor(box.field_1321); z < Math.ceil(box.field_1324); z++) {
/* 221 */           if (MeteorClient.mc.field_1687.method_8320(new class_2338(x, y, z)).method_26204() == block) {
/* 222 */             return true;
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 228 */     return false;
/*     */   }
/*     */   
/*     */   public static void spawnLightning(double x, double y, double z) {
/* 232 */     class_1538 lightning = new class_1538(class_1299.field_6112, (class_1937)MeteorClient.mc.field_1687);
/*     */     
/* 234 */     lightning.method_30634(x, y, z);
/* 235 */     lightning.method_24203(x, y, z);
/* 236 */     MeteorClient.mc.field_1687.method_2942(lightning.method_5628(), (class_1297)lightning);
/*     */   }
/*     */ }


/* Location:              C:\Users\Shotgun\OneDrive\Desktop\Nova-v-100000 (1).jar!\Nov\\util\NWorldUtils.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */