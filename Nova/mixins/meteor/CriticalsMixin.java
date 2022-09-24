/*    */ package Nova.mixins.meteor;
/*    */ 
/*    */ import meteordevelopment.meteorclient.MeteorClient;
/*    */ import meteordevelopment.meteorclient.events.packets.PacketEvent;
/*    */ import meteordevelopment.meteorclient.mixininterface.IPlayerInteractEntityC2SPacket;
/*    */ import meteordevelopment.meteorclient.mixininterface.IVec3d;
/*    */ import meteordevelopment.meteorclient.settings.Setting;
/*    */ import meteordevelopment.meteorclient.systems.modules.combat.Criticals;
/*    */ import meteordevelopment.orbit.EventHandler;
/*    */ import net.minecraft.class_1297;
/*    */ import net.minecraft.class_2596;
/*    */ import net.minecraft.class_2824;
/*    */ import net.minecraft.class_2879;
/*    */ import org.spongepowered.asm.mixin.Final;
/*    */ import org.spongepowered.asm.mixin.Mixin;
/*    */ import org.spongepowered.asm.mixin.Shadow;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Mixin({Criticals.class})
/*    */ public abstract class CriticalsMixin
/*    */ {
/*    */   @Shadow(remap = false)
/*    */   @Final
/*    */   private Setting<Criticals.Mode> mode;
/*    */   @Shadow
/*    */   private boolean sendPackets;
/*    */   @Shadow
/*    */   private int sendTimer;
/*    */   @Shadow
/*    */   private class_2824 attackPacket;
/*    */   
/*    */   @EventHandler
/*    */   private void onSendPacket(PacketEvent.Send event) {
/* 40 */     class_2596 class_2596 = event.packet; if (class_2596 instanceof IPlayerInteractEntityC2SPacket) { IPlayerInteractEntityC2SPacket packet = (IPlayerInteractEntityC2SPacket)class_2596; if (packet.getType() == class_2824.class_5907.field_29172) {
/* 41 */         if (skipCrit())
/*    */           return; 
/* 43 */         class_1297 entity = packet.getEntity();
/*    */ 
/*    */ 
/*    */ 
/*    */         
/* 48 */         switch ((Criticals.Mode)this.mode.get()) {
/*    */           case Packet:
/* 50 */             sendPacket(0.0625D);
/* 51 */             sendPacket(0.0D);
/*    */             return;
/*    */           case Bypass:
/* 54 */             sendPacket(0.11D);
/* 55 */             sendPacket(0.1100013579D);
/* 56 */             sendPacket(1.3579E-6D);
/*    */             return;
/*    */         } 
/* 59 */         if (!this.sendPackets) {
/* 60 */           this.sendPackets = true;
/* 61 */           this.sendTimer = (this.mode.get() == Criticals.Mode.Jump) ? 6 : 4;
/* 62 */           this.attackPacket = (class_2824)event.packet;
/*    */           
/* 64 */           if (this.mode.get() == Criticals.Mode.Jump) { MeteorClient.mc.field_1724.method_6043(); }
/* 65 */           else { ((IVec3d)MeteorClient.mc.field_1724.method_18798()).setY(0.25D); }
/* 66 */            event.cancel();
/*    */         }  return;
/*    */       }  }
/*    */     
/* 70 */     if (event.packet instanceof class_2879 && this.mode.get() != Criticals.Mode.Packet) {
/* 71 */       if (skipCrit())
/*    */         return; 
/* 73 */       if (this.sendPackets && this.swingPacket == null) {
/* 74 */         this.swingPacket = (class_2879)event.packet;
/*    */         
/* 76 */         event.cancel();
/*    */       } 
/*    */     } 
/*    */   }
/*    */   
/*    */   @Shadow
/*    */   private class_2879 swingPacket;
/*    */   @Shadow
/*    */   @Final
/*    */   private Setting<Boolean> ka;
/*    */   
/*    */   @Shadow
/*    */   protected abstract boolean skipCrit();
/*    */   
/*    */   @Shadow
/*    */   protected abstract void sendPacket(double paramDouble);
/*    */ }


/* Location:              C:\Users\Shotgun\OneDrive\Desktop\Nova-v-100000 (1).jar!\Nova\mixins\meteor\CriticalsMixin.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */