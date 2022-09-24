/*    */ package Nova.mixins.meteor;
/*    */ 
/*    */ import Nova.NovaAddon;
/*    */ import meteordevelopment.meteorclient.settings.BoolSetting;
/*    */ import meteordevelopment.meteorclient.settings.Setting;
/*    */ import meteordevelopment.meteorclient.settings.SettingGroup;
/*    */ import meteordevelopment.meteorclient.systems.modules.Module;
/*    */ import meteordevelopment.meteorclient.systems.modules.render.Zoom;
/*    */ import org.spongepowered.asm.mixin.Final;
/*    */ import org.spongepowered.asm.mixin.Mixin;
/*    */ import org.spongepowered.asm.mixin.Shadow;
/*    */ import org.spongepowered.asm.mixin.injection.At;
/*    */ import org.spongepowered.asm.mixin.injection.Inject;
/*    */ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/*    */ 
/*    */ @Mixin({Zoom.class})
/*    */ public class ZoomMixin
/*    */   extends Module {
/*    */   public ZoomMixin() {
/* 20 */     super(NovaAddon.nova, "zoom", "is there a better way 2 do this? idk");
/*    */   }
/*    */ 
/*    */   
/*    */   @Shadow(remap = false)
/*    */   @Final
/*    */   private SettingGroup sgGeneral;
/*    */   
/*    */   private Setting<Boolean> f1;
/*    */   
/*    */   @Inject(method = {"<init>"}, at = {@At("TAIL")}, remap = false)
/*    */   private void onInit(CallbackInfo ci) {
/* 32 */     this.f1 = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder())
/* 33 */         .name("toggle-hud"))
/* 34 */         .description("Toggles the Heads-Up Display."))
/* 35 */         .defaultValue(Boolean.valueOf(false)))
/* 36 */         .build());
/*    */   }
/*    */ 
/*    */   
/*    */   @Inject(method = {"onActivate"}, at = {@At("TAIL")}, remap = false)
/*    */   public void onActivate(CallbackInfo info) {
/* 42 */     if (((Boolean)this.f1.get()).booleanValue()) {
/* 43 */       this.mc.field_1690.field_1842 = true;
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public void onDeactivate() {
/* 49 */     if (((Boolean)this.f1.get()).booleanValue())
/* 50 */       this.mc.field_1690.field_1842 = false; 
/*    */   }
/*    */ }


/* Location:              C:\Users\Shotgun\OneDrive\Desktop\Nova-v-100000 (1).jar!\Nova\mixins\meteor\ZoomMixin.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */