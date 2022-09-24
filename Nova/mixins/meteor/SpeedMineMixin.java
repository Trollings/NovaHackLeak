/*    */ package Nova.mixins.meteor;
/*    */ 
/*    */ import meteordevelopment.meteorclient.events.entity.player.BreakBlockEvent;
/*    */ import meteordevelopment.meteorclient.settings.BoolSetting;
/*    */ import meteordevelopment.meteorclient.settings.Setting;
/*    */ import meteordevelopment.meteorclient.settings.SettingGroup;
/*    */ import meteordevelopment.meteorclient.systems.modules.Category;
/*    */ import meteordevelopment.meteorclient.systems.modules.Module;
/*    */ import meteordevelopment.meteorclient.systems.modules.player.SpeedMine;
/*    */ import meteordevelopment.orbit.EventHandler;
/*    */ import net.minecraft.class_2350;
/*    */ import net.minecraft.class_2596;
/*    */ import net.minecraft.class_2846;
/*    */ import org.spongepowered.asm.mixin.Mixin;
/*    */ import org.spongepowered.asm.mixin.injection.At;
/*    */ import org.spongepowered.asm.mixin.injection.Inject;
/*    */ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/*    */ 
/*    */ @Mixin(value = {SpeedMine.class}, remap = false)
/*    */ public class SpeedMineMixin extends Module {
/*    */   private Setting<Boolean> confirmBreak;
/*    */   
/*    */   public SpeedMineMixin(Category category, String name, String description) {
/* 24 */     super(category, name, description);
/*    */   }
/*    */   
/*    */   @Inject(method = {"<init>"}, at = {@At("TAIL")}, remap = false)
/*    */   private void onInit(CallbackInfo ci) {
/* 29 */     SettingGroup sgGeneral = this.settings.getDefaultGroup();
/*    */ 
/*    */     
/* 32 */     this.confirmBreak = sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder())
/* 33 */         .name("check-breaks"))
/* 34 */         .description("Confirm blocks as broken with the server."))
/* 35 */         .defaultValue(Boolean.valueOf(true)))
/* 36 */         .build());
/*    */   }
/*    */ 
/*    */   
/*    */   @EventHandler
/*    */   private void onBlockBreak(BreakBlockEvent event) {
/* 42 */     if (((Boolean)this.confirmBreak.get()).booleanValue() && 
/* 43 */       event.blockPos != null)
/* 44 */       this.mc.field_1724.field_3944.method_2883((class_2596)new class_2846(class_2846.class_2847.field_12973, event.blockPos, class_2350.field_11033)); 
/*    */   }
/*    */ }


/* Location:              C:\Users\Shotgun\OneDrive\Desktop\Nova-v-100000 (1).jar!\Nova\mixins\meteor\SpeedMineMixin.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */