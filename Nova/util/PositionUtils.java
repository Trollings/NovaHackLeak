/*     */ package Nova.util;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import meteordevelopment.meteorclient.MeteorClient;
/*     */ import net.minecraft.class_1657;
/*     */ import net.minecraft.class_2246;
/*     */ import net.minecraft.class_2338;
/*     */ import net.minecraft.class_238;
/*     */ import net.minecraft.class_243;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PositionUtils
/*     */ {
/*     */   public static boolean allPlaced(List<class_2338> posList) {
/*  17 */     for (class_2338 pos : posList) {
/*  18 */       if (MeteorClient.mc.field_1687.method_8320(pos).method_26204() == class_2246.field_10124) return false; 
/*     */     } 
/*  20 */     return true;
/*     */   }
/*     */   
/*     */   public static List<class_2338> dynamicTopPos(class_1657 targetEntity, boolean predictMovement) {
/*  24 */     List<class_2338> pos = new ArrayList<>();
/*     */     
/*  26 */     class_238 box = targetEntity.method_5829().method_35580(0.001D, 0.0D, 0.001D);
/*  27 */     if (predictMovement) {
/*  28 */       class_243 v = targetEntity.method_18798();
/*  29 */       box.method_989(v.field_1352, v.field_1351, v.field_1350);
/*     */     } 
/*     */ 
/*     */     
/*  33 */     pos.add(new class_2338(box.field_1323, box.field_1322 + 2.5D, box.field_1321));
/*  34 */     pos.add(new class_2338(box.field_1320, box.field_1322 + 2.5D, box.field_1321));
/*  35 */     pos.add(new class_2338(box.field_1320, box.field_1322 + 2.5D, box.field_1324));
/*  36 */     pos.add(new class_2338(box.field_1323, box.field_1322 + 2.5D, box.field_1324));
/*     */     
/*  38 */     return pos;
/*     */   }
/*     */   
/*     */   public static List<class_2338> dynamicHeadPos(class_1657 targetEntity, boolean predictMovement) {
/*  42 */     List<class_2338> pos = new ArrayList<>();
/*     */     
/*  44 */     class_238 box = targetEntity.method_5829().method_35580(0.001D, 0.0D, 0.001D);
/*  45 */     if (predictMovement) {
/*  46 */       class_243 v = targetEntity.method_18798();
/*  47 */       box.method_989(v.field_1352, v.field_1351, v.field_1350);
/*     */     } 
/*     */ 
/*     */     
/*  51 */     pos.add(new class_2338(box.field_1323, box.field_1322 + 1.5D, box.field_1321 - 1.0D));
/*  52 */     pos.add(new class_2338(box.field_1320, box.field_1322 + 1.5D, box.field_1321 - 1.0D));
/*     */     
/*  54 */     pos.add(new class_2338(box.field_1320 + 1.0D, box.field_1322 + 1.5D, box.field_1321));
/*  55 */     pos.add(new class_2338(box.field_1320 + 1.0D, box.field_1322 + 1.5D, box.field_1324));
/*     */     
/*  57 */     pos.add(new class_2338(box.field_1320, box.field_1322 + 1.5D, box.field_1324 + 1.0D));
/*  58 */     pos.add(new class_2338(box.field_1323, box.field_1322 + 1.5D, box.field_1324 + 1.0D));
/*     */     
/*  60 */     pos.add(new class_2338(box.field_1323 - 1.0D, box.field_1322 + 1.5D, box.field_1324));
/*  61 */     pos.add(new class_2338(box.field_1323 - 1.0D, box.field_1322 + 1.5D, box.field_1321));
/*     */     
/*  63 */     return pos;
/*     */   }
/*     */   
/*     */   public static List<class_2338> dynamicBottomPos(class_1657 targetEntity, boolean predictMovement) {
/*  67 */     List<class_2338> pos = new ArrayList<>();
/*     */     
/*  69 */     class_238 box = targetEntity.method_5829().method_35580(0.001D, 0.0D, 0.001D);
/*  70 */     if (predictMovement) {
/*  71 */       class_243 v = targetEntity.method_18798();
/*  72 */       box.method_989(v.field_1352, v.field_1351, v.field_1350);
/*     */     } 
/*     */ 
/*     */     
/*  76 */     pos.add(new class_2338(box.field_1323, box.field_1322 - 0.5D, box.field_1321));
/*  77 */     pos.add(new class_2338(box.field_1320, box.field_1322 - 0.5D, box.field_1321));
/*  78 */     pos.add(new class_2338(box.field_1320, box.field_1322 - 0.5D, box.field_1324));
/*  79 */     pos.add(new class_2338(box.field_1323, box.field_1322 - 0.5D, box.field_1324));
/*     */     
/*  81 */     return pos;
/*     */   }
/*     */   
/*     */   public static List<class_2338> dynamicFeetPos(class_1657 targetEntity, boolean predictMovement) {
/*  85 */     List<class_2338> pos = new ArrayList<>();
/*     */     
/*  87 */     class_238 box = targetEntity.method_5829().method_35580(0.001D, 0.0D, 0.001D);
/*  88 */     if (predictMovement) {
/*  89 */       class_243 v = targetEntity.method_18798();
/*  90 */       box.method_989(v.field_1352, v.field_1351, v.field_1350);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*  95 */     pos.add(new class_2338(box.field_1323, box.field_1322 + 0.4D, box.field_1321 - 1.0D));
/*  96 */     pos.add(new class_2338(box.field_1320, box.field_1322 + 0.4D, box.field_1321 - 1.0D));
/*     */     
/*  98 */     pos.add(new class_2338(box.field_1320 + 1.0D, box.field_1322 + 0.4D, box.field_1321));
/*  99 */     pos.add(new class_2338(box.field_1320 + 1.0D, box.field_1322 + 0.4D, box.field_1324));
/*     */     
/* 101 */     pos.add(new class_2338(box.field_1320, box.field_1322 + 0.4D, box.field_1324 + 1.0D));
/* 102 */     pos.add(new class_2338(box.field_1323, box.field_1322 + 0.4D, box.field_1324 + 1.0D));
/*     */     
/* 104 */     pos.add(new class_2338(box.field_1323 - 1.0D, box.field_1322 + 0.4D, box.field_1324));
/* 105 */     pos.add(new class_2338(box.field_1323 - 1.0D, box.field_1322 + 0.4D, box.field_1321));
/*     */     
/* 107 */     return pos;
/*     */   }
/*     */   
/*     */   public static List<class_2338> dynamicRussianNorth(class_1657 targetEntity, boolean plus) {
/* 111 */     List<class_2338> pos = new ArrayList<>();
/*     */     
/* 113 */     class_238 box = targetEntity.method_5829().method_35580(0.001D, 0.0D, 0.001D);
/*     */     
/* 115 */     pos.add((new class_2338(box.field_1323, box.field_1322 + 0.4D, box.field_1321 - 1.0D)).method_10095());
/* 116 */     pos.add((new class_2338(box.field_1320, box.field_1322 + 0.4D, box.field_1321 - 1.0D)).method_10095());
/* 117 */     if (plus) {
/* 118 */       pos.add((new class_2338(box.field_1323, box.field_1322 + 0.4D, box.field_1321 - 1.0D)).method_10067());
/* 119 */       pos.add((new class_2338(box.field_1320, box.field_1322 + 0.4D, box.field_1321 - 1.0D)).method_10078());
/*     */     } 
/*     */     
/* 122 */     return pos;
/*     */   }
/*     */   
/*     */   public static List<class_2338> dynamicRussianEast(class_1657 targetEntity, boolean plus) {
/* 126 */     List<class_2338> pos = new ArrayList<>();
/*     */     
/* 128 */     class_238 box = targetEntity.method_5829().method_35580(0.001D, 0.0D, 0.001D);
/*     */     
/* 130 */     pos.add((new class_2338(box.field_1320 + 1.0D, box.field_1322 + 0.4D, box.field_1321)).method_10078());
/* 131 */     pos.add((new class_2338(box.field_1320 + 1.0D, box.field_1322 + 0.4D, box.field_1324)).method_10078());
/* 132 */     if (plus) {
/* 133 */       pos.add((new class_2338(box.field_1320 + 1.0D, box.field_1322 + 0.4D, box.field_1321)).method_10095());
/* 134 */       pos.add((new class_2338(box.field_1320 + 1.0D, box.field_1322 + 0.4D, box.field_1324)).method_10072());
/*     */     } 
/*     */     
/* 137 */     return pos;
/*     */   }
/*     */   
/*     */   public static List<class_2338> dynamicRussianSouth(class_1657 targetEntity, boolean plus) {
/* 141 */     List<class_2338> pos = new ArrayList<>();
/*     */     
/* 143 */     class_238 box = targetEntity.method_5829().method_35580(0.001D, 0.0D, 0.001D);
/*     */     
/* 145 */     pos.add((new class_2338(box.field_1320, box.field_1322 + 0.4D, box.field_1324 + 1.0D)).method_10072());
/* 146 */     pos.add((new class_2338(box.field_1323, box.field_1322 + 0.4D, box.field_1324 + 1.0D)).method_10072());
/* 147 */     if (plus) {
/* 148 */       pos.add((new class_2338(box.field_1320, box.field_1322 + 0.4D, box.field_1324 + 1.0D)).method_10078());
/* 149 */       pos.add((new class_2338(box.field_1323, box.field_1322 + 0.4D, box.field_1324 + 1.0D)).method_10067());
/*     */     } 
/*     */     
/* 152 */     return pos;
/*     */   }
/*     */   
/*     */   public static List<class_2338> dynamicRussianWest(class_1657 targetEntity, boolean plus) {
/* 156 */     List<class_2338> pos = new ArrayList<>();
/*     */     
/* 158 */     class_238 box = targetEntity.method_5829().method_35580(0.001D, 0.0D, 0.001D);
/*     */     
/* 160 */     pos.add((new class_2338(box.field_1323 - 1.0D, box.field_1322 + 0.4D, box.field_1324)).method_10067());
/* 161 */     pos.add((new class_2338(box.field_1323 - 1.0D, box.field_1322 + 0.4D, box.field_1321)).method_10067());
/* 162 */     if (plus) {
/* 163 */       pos.add((new class_2338(box.field_1323 - 1.0D, box.field_1322 + 0.4D, box.field_1324)).method_10072());
/* 164 */       pos.add((new class_2338(box.field_1323 - 1.0D, box.field_1322 + 0.4D, box.field_1321)).method_10095());
/*     */     } 
/*     */     
/* 167 */     return pos;
/*     */   }
/*     */ }


/* Location:              C:\Users\Shotgun\OneDrive\Desktop\Nova-v-100000 (1).jar!\Nov\\util\PositionUtils.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */