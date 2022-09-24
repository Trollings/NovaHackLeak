/*    */ package Nova.mixins.meteor;
/*    */ 
/*    */ import meteordevelopment.meteorclient.MeteorClient;
/*    */ import meteordevelopment.meteorclient.settings.BoolSetting;
/*    */ import meteordevelopment.meteorclient.settings.Setting;
/*    */ import meteordevelopment.meteorclient.settings.SettingGroup;
/*    */ import meteordevelopment.meteorclient.systems.modules.render.Freecam;
/*    */ import meteordevelopment.meteorclient.utils.Utils;
/*    */ import org.spongepowered.asm.mixin.Final;
/*    */ import org.spongepowered.asm.mixin.Mixin;
/*    */ import org.spongepowered.asm.mixin.Shadow;
/*    */ import org.spongepowered.asm.mixin.injection.At;
/*    */ import org.spongepowered.asm.mixin.injection.Inject;
/*    */ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/*    */ 
/*    */ 
/*    */ 
/*    */ @Mixin({Freecam.class})
/*    */ public class FreecamMixin
/*    */ {
/*    */   @Shadow(remap = false)
/*    */   @Final
/*    */   private SettingGroup sgGeneral;
/*    */   @Final
/*    */   @Shadow(remap = false)
/*    */   private Setting<Boolean> rotate;
/*    */   private Setting<Boolean> parallelView;
/*    */   @Shadow(remap = false)
/*    */   public float yaw;
/*    */   @Shadow(remap = false)
/*    */   public float pitch;
/*    */   
/*    */   @Inject(method = {"<init>"}, at = {@At("TAIL")}, remap = false)
/*    */   private void onInit(CallbackInfo ci) {
/* 35 */     this.parallelView = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder())
/* 36 */         .name("parallel-view-B+"))
/* 37 */         .description("Rotates the player same way the camera does (good for building)."))
/* 38 */         .defaultValue(Boolean.valueOf(false)))
/* 39 */         .visible(() -> !((Boolean)this.rotate.get()).booleanValue()))
/* 40 */         .build());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private boolean forward;
/*    */ 
/*    */ 
/*    */   
/*    */   private boolean backward;
/*    */ 
/*    */ 
/*    */   
/*    */   private boolean right;
/*    */ 
/*    */   
/*    */   private boolean left;
/*    */ 
/*    */   
/*    */   private boolean up;
/*    */ 
/*    */   
/*    */   private boolean down;
/*    */ 
/*    */ 
/*    */   
/*    */   @Inject(method = {"onTick"}, at = {@At("TAIL")}, remap = false)
/*    */   public void onTick(CallbackInfo info) {
/* 69 */     if (((Boolean)this.parallelView.get()).booleanValue() && !((Boolean)this.rotate.get()).booleanValue()) {
/* 70 */       this.pitch = Utils.clamp(this.pitch, -90.0F, 90.0F);
/*    */       
/* 72 */       MeteorClient.mc.field_1724.method_36456(this.yaw);
/* 73 */       MeteorClient.mc.field_1724.method_36457(this.pitch);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Shotgun\OneDrive\Desktop\Nova-v-100000 (1).jar!\Nova\mixins\meteor\FreecamMixin.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */