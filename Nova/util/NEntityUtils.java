/*     */ package Nova.util;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import meteordevelopment.meteorclient.MeteorClient;
/*     */ import meteordevelopment.meteorclient.events.packets.PacketEvent;
/*     */ import meteordevelopment.meteorclient.utils.Utils;
/*     */ import net.minecraft.class_1292;
/*     */ import net.minecraft.class_1294;
/*     */ import net.minecraft.class_1297;
/*     */ import net.minecraft.class_1309;
/*     */ import net.minecraft.class_1511;
/*     */ import net.minecraft.class_1657;
/*     */ import net.minecraft.class_1792;
/*     */ import net.minecraft.class_1802;
/*     */ import net.minecraft.class_1890;
/*     */ import net.minecraft.class_1893;
/*     */ import net.minecraft.class_1922;
/*     */ import net.minecraft.class_1937;
/*     */ import net.minecraft.class_2246;
/*     */ import net.minecraft.class_2248;
/*     */ import net.minecraft.class_2338;
/*     */ import net.minecraft.class_2350;
/*     */ import net.minecraft.class_239;
/*     */ import net.minecraft.class_243;
/*     */ import net.minecraft.class_2596;
/*     */ import net.minecraft.class_2663;
/*     */ import net.minecraft.class_2680;
/*     */ import net.minecraft.class_3486;
/*     */ import net.minecraft.class_3959;
/*     */ import net.minecraft.class_3965;
/*     */ import net.minecraft.class_746;
/*     */ 
/*     */ public class NEntityUtils {
/*     */   public static boolean isDeathPacket(PacketEvent.Receive event) {
/*  36 */     class_2596 class_2596 = event.packet; if (class_2596 instanceof class_2663) { class_2663 packet = (class_2663)class_2596;
/*  37 */       if (packet.method_11470() == 3) {
/*  38 */         deadEntity = packet.method_11469((class_1937)MeteorClient.mc.field_1687);
/*  39 */         return deadEntity instanceof class_1657;
/*     */       }  }
/*     */     
/*  42 */     return false;
/*     */   }
/*     */   public static class_1297 deadEntity;
/*     */   public static class_2350 rayTraceCheck(class_2338 pos, boolean forceReturn) {
/*  46 */     class_243 eyesPos = new class_243(MeteorClient.mc.field_1724.method_23317(), MeteorClient.mc.field_1724.method_23318() + MeteorClient.mc.field_1724.method_18381(MeteorClient.mc.field_1724.method_18376()), MeteorClient.mc.field_1724.method_23321());
/*  47 */     class_2350[] var3 = class_2350.values();
/*     */     
/*  49 */     for (class_2350 direction : var3) {
/*  50 */       class_3959 raycastContext = new class_3959(eyesPos, new class_243(pos.method_10263() + 0.5D + direction.method_10163().method_10263() * 0.5D, pos.method_10264() + 0.5D + direction.method_10163().method_10264() * 0.5D, pos.method_10260() + 0.5D + direction.method_10163().method_10260() * 0.5D), class_3959.class_3960.field_17558, class_3959.class_242.field_1348, (class_1297)MeteorClient.mc.field_1724);
/*  51 */       class_3965 result = MeteorClient.mc.field_1687.method_17742(raycastContext);
/*  52 */       if (result != null && result.method_17783() == class_239.class_240.field_1332 && result.method_17777().equals(pos)) {
/*  53 */         return direction;
/*     */       }
/*     */     } 
/*     */     
/*  57 */     if (forceReturn) {
/*  58 */       if (pos.method_10264() > eyesPos.field_1351) {
/*  59 */         return class_2350.field_11033;
/*     */       }
/*  61 */       return class_2350.field_11036;
/*     */     } 
/*     */     
/*  64 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public static int getBlockBreakingSpeed(class_2680 block, class_2338 pos, int slot) {
/*  69 */     class_746 class_746 = MeteorClient.mc.field_1724;
/*     */     
/*  71 */     float f = class_746.method_31548().method_5438(slot).method_7924(block);
/*  72 */     if (f > 1.0F) {
/*  73 */       int i = ((Integer)class_1890.method_8222(class_746.method_31548().method_5438(slot)).getOrDefault(class_1893.field_9131, Integer.valueOf(0))).intValue();
/*  74 */       if (i > 0) {
/*  75 */         f += (i * i + 1);
/*     */       }
/*     */     } 
/*     */     
/*  79 */     if (class_1292.method_5576((class_1309)class_746)) {
/*  80 */       f *= 1.0F + (class_1292.method_5575((class_1309)class_746) + 1) * 0.2F;
/*     */     }
/*     */     
/*  83 */     if (class_746.method_6059(class_1294.field_5901)) {
/*     */       float k;
/*  85 */       switch (class_746.method_6112(class_1294.field_5901).method_5578()) {
/*     */         case 0:
/*  87 */           k = 0.3F;
/*     */           break;
/*     */         case 1:
/*  90 */           k = 0.09F;
/*     */           break;
/*     */         case 2:
/*  93 */           k = 0.0027F;
/*     */           break;
/*     */         
/*     */         default:
/*  97 */           k = 8.1E-4F;
/*     */           break;
/*     */       } 
/* 100 */       f *= k;
/*     */     } 
/*     */     
/* 103 */     if (class_746.method_5777(class_3486.field_15517) && !class_1890.method_8200((class_1309)class_746)) {
/* 104 */       f /= 5.0F;
/*     */     }
/*     */     
/* 107 */     if (!class_746.method_24828()) {
/* 108 */       f /= 5.0F;
/*     */     }
/*     */     
/* 111 */     float t = block.method_26214((class_1922)MeteorClient.mc.field_1687, pos);
/* 112 */     if (t == -1.0F) {
/* 113 */       return 0;
/*     */     }
/* 115 */     return (int)Math.ceil((1.0F / f / t / 30.0F));
/*     */   }
/*     */ 
/*     */   
/*     */   public static class_243 crystalEdgePos(class_1511 crystal) {
/* 120 */     class_243 crystalPos = crystal.method_19538();
/* 121 */     return new class_243(
/* 122 */         (crystalPos.field_1352 < MeteorClient.mc.field_1724.method_23317()) ? (crystalPos.method_1031(Math.min(1.0D, MeteorClient.mc.field_1724.method_23317() - crystalPos.field_1352), 0.0D, 0.0D)).field_1352 : ((crystalPos.field_1352 > MeteorClient.mc.field_1724.method_23317()) ? (crystalPos.method_1031(Math.max(-1.0D, MeteorClient.mc.field_1724.method_23317() - crystalPos.field_1352), 0.0D, 0.0D)).field_1352 : crystalPos.field_1352), 
/* 123 */         (crystalPos.field_1351 < MeteorClient.mc.field_1724.method_23318()) ? (crystalPos.method_1031(0.0D, Math.min(1.0D, MeteorClient.mc.field_1724.method_23318() - crystalPos.field_1351), 0.0D)).field_1351 : crystalPos.field_1351, 
/* 124 */         (crystalPos.field_1350 < MeteorClient.mc.field_1724.method_23321()) ? (crystalPos.method_1031(0.0D, 0.0D, Math.min(1.0D, MeteorClient.mc.field_1724.method_23321() - crystalPos.field_1350))).field_1350 : ((crystalPos.field_1350 > MeteorClient.mc.field_1724.method_23321()) ? (crystalPos.method_1031(0.0D, 0.0D, Math.max(-1.0D, MeteorClient.mc.field_1724.method_23321() - crystalPos.field_1350))).field_1350 : crystalPos.field_1350));
/*     */   }
/*     */   
/*     */   public static boolean isBedrock(class_2338 pos) {
/* 128 */     return MeteorClient.mc.field_1687.method_8320(pos).method_27852(class_2246.field_9987);
/*     */   }
/*     */   
/*     */   public enum BlastResistantType {
/* 132 */     Any,
/* 133 */     Unbreakable,
/* 134 */     Mineable,
/* 135 */     NotAir;
/*     */   }
/*     */   
/*     */   public static boolean isBlastResistant(class_2338 pos, BlastResistantType type) {
/* 139 */     class_2248 block = MeteorClient.mc.field_1687.method_8320(pos).method_26204();
/* 140 */     switch (type) { case Any:
/*     */       case Mineable:
/* 142 */         return (block == class_2246.field_10540 || block == class_2246.field_22423 || block instanceof net.minecraft.class_2199 || block == class_2246.field_22108 || block == class_2246.field_10443 || block == class_2246.field_23152 || block == class_2246.field_22109 || block == class_2246.field_10485 || (block == class_2246.field_9987 && type == BlastResistantType.Any) || (block == class_2246.field_10398 && type == BlastResistantType.Any));
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
/*     */       case Unbreakable:
/* 154 */         return (block == class_2246.field_9987 || block == class_2246.field_10398);
/*     */ 
/*     */       
/*     */       case NotAir:
/* 158 */         return (block != class_2246.field_10124); }
/*     */ 
/*     */     
/* 161 */     return false;
/*     */   }
/*     */   
/*     */   public static List<class_2338> getSurroundBlocks(class_1657 player) {
/* 165 */     if (player == null) return null;
/*     */     
/* 167 */     List<class_2338> positions = new ArrayList<>();
/*     */     
/* 169 */     for (class_2350 direction : class_2350.values()) {
/* 170 */       if (direction != class_2350.field_11036 && direction != class_2350.field_11033) {
/*     */         
/* 172 */         class_2338 pos = playerPos(player).method_10093(direction);
/*     */         
/* 174 */         if (isBlastResistant(pos, BlastResistantType.Mineable)) positions.add(pos); 
/*     */       } 
/*     */     } 
/* 177 */     return positions;
/*     */   }
/*     */   
/*     */   public static class_2338 getCityBlock(class_1657 player) {
/* 181 */     List<class_2338> posList = getSurroundBlocks(player);
/* 182 */     posList.sort(Comparator.comparingDouble(NPlayerUtils::distanceFromEye));
/* 183 */     return posList.isEmpty() ? null : posList.get(0);
/*     */   }
/*     */   
/*     */   public static class_2338 getTargetBlock(class_1657 player) {
/* 187 */     class_2338 finalPos = null;
/*     */     
/* 189 */     List<class_2338> positions = getSurroundBlocks(player);
/* 190 */     List<class_2338> myPositions = getSurroundBlocks((class_1657)MeteorClient.mc.field_1724);
/*     */     
/* 192 */     if (positions == null) return null;
/*     */     
/* 194 */     for (class_2338 pos : positions) {
/*     */       
/* 196 */       if (myPositions != null && !myPositions.isEmpty() && myPositions.contains(pos))
/*     */         continue; 
/* 198 */       if (finalPos == null) {
/* 199 */         finalPos = pos;
/*     */         
/*     */         continue;
/*     */       } 
/* 203 */       if (MeteorClient.mc.field_1724.method_5707(Utils.vec3d(pos)) < MeteorClient.mc.field_1724.method_5707(Utils.vec3d(finalPos))) {
/* 204 */         finalPos = pos;
/*     */       }
/*     */     } 
/*     */     
/* 208 */     return finalPos;
/*     */   }
/*     */   
/*     */   public static String getName(class_1297 entity) {
/* 212 */     if (entity == null) return null; 
/* 213 */     if (entity instanceof class_1657) return entity.method_5820(); 
/* 214 */     return entity.method_5864().method_5897().getString();
/*     */   }
/*     */   
/*     */   public static class_2338 playerPos(class_1657 targetEntity) {
/* 218 */     return NWorldUtils.roundBlockPos(targetEntity.method_19538());
/*     */   }
/*     */   
/*     */   public static boolean isTopTrapped(class_1657 targetEntity, BlastResistantType type) {
/* 222 */     return isBlastResistant(playerPos(targetEntity).method_10069(0, 2, 0), type);
/*     */   }
/*     */   
/*     */   public static boolean isFaceSurrounded(class_1657 targetEntity, BlastResistantType type) {
/* 226 */     return (isBlastResistant(playerPos(targetEntity).method_10069(1, 1, 0), type) && 
/* 227 */       isBlastResistant(playerPos(targetEntity).method_10069(-1, 1, 0), type) && 
/* 228 */       isBlastResistant(playerPos(targetEntity).method_10069(0, 1, 1), type) && 
/* 229 */       isBlastResistant(playerPos(targetEntity).method_10069(0, 1, -1), type));
/*     */   }
/*     */   
/*     */   public static boolean isBothTrapped(class_1657 targetEntity, BlastResistantType type) {
/* 233 */     return (isTopTrapped(targetEntity, type) && isFaceSurrounded(targetEntity, type));
/*     */   }
/*     */   
/*     */   public static boolean isAnyTrapped(class_1657 targetEntity, BlastResistantType type) {
/* 237 */     return (isTopTrapped(targetEntity, type) || isFaceSurrounded(targetEntity, type));
/*     */   }
/*     */   
/*     */   public static boolean isSurrounded(class_1657 targetEntity, BlastResistantType type) {
/* 241 */     return (isBlastResistant(playerPos(targetEntity).method_10069(1, 0, 0), type) && 
/* 242 */       isBlastResistant(playerPos(targetEntity).method_10069(-1, 0, 0), type) && 
/* 243 */       isBlastResistant(playerPos(targetEntity).method_10069(0, 0, 1), type) && 
/* 244 */       isBlastResistant(playerPos(targetEntity).method_10069(0, 0, -1), type));
/*     */   }
/*     */   
/*     */   public static boolean isSurroundBroken(class_1657 targetEntity, BlastResistantType type) {
/* 248 */     return ((!isBlastResistant(playerPos(targetEntity).method_10069(1, 0, 0), type) && 
/* 249 */       isBlastResistant(playerPos(targetEntity).method_10069(-1, 0, 0), type) && 
/* 250 */       isBlastResistant(playerPos(targetEntity).method_10069(0, 0, 1), type) && 
/* 251 */       isBlastResistant(playerPos(targetEntity).method_10069(0, 0, -1), type)) || (
/*     */       
/* 253 */       isBlastResistant(playerPos(targetEntity).method_10069(1, 0, 0), type) && 
/* 254 */       !isBlastResistant(playerPos(targetEntity).method_10069(-1, 0, 0), type) && 
/* 255 */       isBlastResistant(playerPos(targetEntity).method_10069(0, 0, 1), type) && 
/* 256 */       isBlastResistant(playerPos(targetEntity).method_10069(0, 0, -1), type)) || (
/*     */       
/* 258 */       isBlastResistant(playerPos(targetEntity).method_10069(1, 0, 0), type) && 
/* 259 */       isBlastResistant(playerPos(targetEntity).method_10069(-1, 0, 0), type) && 
/* 260 */       !isBlastResistant(playerPos(targetEntity).method_10069(0, 0, 1), type) && 
/* 261 */       isBlastResistant(playerPos(targetEntity).method_10069(0, 0, -1), type)) || (
/*     */       
/* 263 */       isBlastResistant(playerPos(targetEntity).method_10069(1, 0, 0), type) && 
/* 264 */       isBlastResistant(playerPos(targetEntity).method_10069(-1, 0, 0), type) && 
/* 265 */       isBlastResistant(playerPos(targetEntity).method_10069(0, 0, 1), type) && 
/* 266 */       !isBlastResistant(playerPos(targetEntity).method_10069(0, 0, -1), type)));
/*     */   }
/*     */   
/*     */   public static boolean isBurrowed(class_1657 targetEntity, BlastResistantType type) {
/* 270 */     class_2338 playerPos = NWorldUtils.roundBlockPos(new class_243(targetEntity.method_23317(), targetEntity.method_23318() + 0.4D, targetEntity.method_23321()));
/*     */     
/* 272 */     return isBlastResistant(playerPos, type);
/*     */   }
/*     */   
/*     */   public static boolean isWebbed(class_1657 targetEntity) {
/* 276 */     return NWorldUtils.doesBoxTouchBlock(targetEntity.method_5829(), class_2246.field_10343);
/*     */   }
/*     */   
/*     */   public static boolean isInHole(class_1657 targetEntity, boolean doubles, BlastResistantType type) {
/* 280 */     if (!Utils.canUpdate()) return false;
/*     */     
/* 282 */     class_2338 blockPos = playerPos(targetEntity);
/* 283 */     int air = 0;
/*     */     
/* 285 */     for (class_2350 direction : class_2350.values()) {
/* 286 */       if (direction != class_2350.field_11036)
/*     */       {
/* 288 */         if (!isBlastResistant(blockPos.method_10093(direction), type)) {
/* 289 */           if (!doubles || direction == class_2350.field_11033) return false;
/*     */           
/* 291 */           air++;
/*     */           
/* 293 */           for (class_2350 dir : class_2350.values()) {
/* 294 */             if (dir != direction.method_10153() && dir != class_2350.field_11036)
/*     */             {
/* 296 */               if (!isBlastResistant(blockPos.method_10093(direction).method_10093(dir), type))
/* 297 */                 return false; 
/*     */             }
/*     */           } 
/*     */         } 
/*     */       }
/*     */     } 
/* 303 */     return (air < 2);
/*     */   }
/*     */   
/*     */   public static boolean isHelmet(class_1792 item) {
/* 307 */     return (item == class_1802.field_22027 || item == class_1802.field_8805 || item == class_1802.field_8743 || item == class_1802.field_8862 || item == class_1802.field_8283 || item == class_1802.field_8267 || item == class_1802.field_8090);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isChestplate(class_1792 item) {
/* 317 */     return (item == class_1802.field_22028 || item == class_1802.field_8058 || item == class_1802.field_8523 || item == class_1802.field_8678 || item == class_1802.field_8873 || item == class_1802.field_8577);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isLeggings(class_1792 item) {
/* 326 */     return (item == class_1802.field_22029 || item == class_1802.field_8348 || item == class_1802.field_8396 || item == class_1802.field_8416 || item == class_1802.field_8218 || item == class_1802.field_8570);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isBoots(class_1792 item) {
/* 335 */     return (item == class_1802.field_22030 || item == class_1802.field_8285 || item == class_1802.field_8660 || item == class_1802.field_8753 || item == class_1802.field_8313 || item == class_1802.field_8370);
/*     */   }
/*     */ }


/* Location:              C:\Users\Shotgun\OneDrive\Desktop\Nova-v-100000 (1).jar!\Nov\\util\NEntityUtils.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */