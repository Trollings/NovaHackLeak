/*    */ package Nova.util;
/*    */ 
/*    */ import meteordevelopment.meteorclient.MeteorClient;
/*    */ import net.minecraft.class_1297;
/*    */ import net.minecraft.class_2338;
/*    */ import net.minecraft.class_2350;
/*    */ import net.minecraft.class_243;
/*    */ 
/*    */ public class NPlayerUtils
/*    */ {
/*    */   public static class_243 playerEyePos() {
/* 12 */     return new class_243(MeteorClient.mc.field_1724.method_23317(), MeteorClient.mc.field_1724.method_23318() + MeteorClient.mc.field_1724.method_18381(MeteorClient.mc.field_1724.method_18376()), MeteorClient.mc.field_1724.method_23321());
/*    */   }
/*    */   
/*    */   public static double distanceFromEye(class_1297 entity) {
/* 16 */     double feet = distanceFromEye(entity.method_23317(), entity.method_23318(), entity.method_23321());
/* 17 */     double head = distanceFromEye(entity.method_23317(), entity.method_23318() + entity.method_17682(), entity.method_23321());
/* 18 */     return Math.min(head, feet);
/*    */   }
/*    */   
/*    */   public static double distanceFromEye(class_2338 blockPos) {
/* 22 */     return distanceFromEye(blockPos.method_10263(), blockPos.method_10264(), blockPos.method_10260());
/*    */   }
/*    */   
/*    */   public static double distanceFromEye(class_243 vec3d) {
/* 26 */     return distanceFromEye(vec3d.method_10216(), vec3d.method_10214(), vec3d.method_10215());
/*    */   }
/*    */   
/*    */   public static double distanceFromEye(double x, double y, double z) {
/* 30 */     double f = MeteorClient.mc.field_1724.method_23317() - x;
/* 31 */     double g = MeteorClient.mc.field_1724.method_23318() + MeteorClient.mc.field_1724.method_18381(MeteorClient.mc.field_1724.method_18376()) - y;
/* 32 */     double h = MeteorClient.mc.field_1724.method_23321() - z;
/* 33 */     return Math.sqrt(f * f + g * g + h * h);
/*    */   }
/*    */   
/*    */   public static double[] directionSpeed(float speed) {
/* 37 */     float forward = MeteorClient.mc.field_1724.field_3913.field_3905;
/* 38 */     float side = MeteorClient.mc.field_1724.field_3913.field_3907;
/* 39 */     float yaw = MeteorClient.mc.field_1724.field_5982 + MeteorClient.mc.field_1724.method_36454() - MeteorClient.mc.field_1724.field_5982;
/*    */     
/* 41 */     if (forward != 0.0F) {
/* 42 */       if (side > 0.0F) {
/* 43 */         yaw += ((forward > 0.0F) ? -45 : 45);
/* 44 */       } else if (side < 0.0F) {
/* 45 */         yaw += ((forward > 0.0F) ? 45 : -45);
/*    */       } 
/*    */       
/* 48 */       side = 0.0F;
/*    */       
/* 50 */       if (forward > 0.0F) {
/* 51 */         forward = 1.0F;
/* 52 */       } else if (forward < 0.0F) {
/* 53 */         forward = -1.0F;
/*    */       } 
/*    */     } 
/*    */     
/* 57 */     double sin = Math.sin(Math.toRadians((yaw + 90.0F)));
/* 58 */     double cos = Math.cos(Math.toRadians((yaw + 90.0F)));
/* 59 */     double dx = (forward * speed) * cos + (side * speed) * sin;
/* 60 */     double dz = (forward * speed) * sin - (side * speed) * cos;
/*    */     
/* 62 */     return new double[] { dx, dz };
/*    */   }
/*    */   
/*    */   public static class_2350 direction(float yaw) {
/* 66 */     yaw %= 360.0F;
/* 67 */     if (yaw < 0.0F) yaw += 360.0F;
/*    */     
/* 69 */     if (yaw >= 315.0F || yaw < 45.0F) return class_2350.field_11035; 
/* 70 */     if (yaw >= 45.0F && yaw < 135.0F) return class_2350.field_11039; 
/* 71 */     if (yaw >= 135.0F && yaw < 225.0F) return class_2350.field_11043; 
/* 72 */     if (yaw >= 225.0F && yaw < 315.0F) return class_2350.field_11034;
/*    */     
/* 74 */     return class_2350.field_11035;
/*    */   }
/*    */ }


/* Location:              C:\Users\Shotgun\OneDrive\Desktop\Nova-v-100000 (1).jar!\Nov\\util\NPlayerUtils.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */