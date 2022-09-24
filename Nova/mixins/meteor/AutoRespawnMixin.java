/*     */ package Nova.mixins.meteor;
/*     */ 
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.Random;
/*     */ import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
/*     */ import meteordevelopment.meteorclient.events.world.TickEvent;
/*     */ import meteordevelopment.meteorclient.settings.BoolSetting;
/*     */ import meteordevelopment.meteorclient.settings.IntSetting;
/*     */ import meteordevelopment.meteorclient.settings.Setting;
/*     */ import meteordevelopment.meteorclient.settings.SettingGroup;
/*     */ import meteordevelopment.meteorclient.settings.StringListSetting;
/*     */ import meteordevelopment.meteorclient.settings.StringSetting;
/*     */ import meteordevelopment.meteorclient.systems.modules.Category;
/*     */ import meteordevelopment.meteorclient.systems.modules.Module;
/*     */ import meteordevelopment.meteorclient.systems.modules.Modules;
/*     */ import meteordevelopment.meteorclient.systems.modules.misc.AutoRespawn;
/*     */ import meteordevelopment.meteorclient.systems.modules.render.WaypointsModule;
/*     */ import meteordevelopment.orbit.EventHandler;
/*     */ import net.minecraft.class_2561;
/*     */ import org.spongepowered.asm.mixin.Mixin;
/*     */ import org.spongepowered.asm.mixin.injection.At;
/*     */ import org.spongepowered.asm.mixin.injection.Inject;
/*     */ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/*     */ 
/*     */ 
/*     */ @Mixin({AutoRespawn.class})
/*     */ public class AutoRespawnMixin
/*     */   extends Module
/*     */ {
/*     */   private Setting<Boolean> autoRekit;
/*     */   private Setting<Boolean> chatInfo;
/*     */   private Setting<String> kitName;
/*     */   private boolean shouldRekit;
/*     */   private int rekitWait;
/*     */   private Setting<Boolean> autoCope;
/*     */   private Setting<Integer> copeDelay;
/*     */   private Setting<List<String>> messages;
/*     */   private boolean shouldCope;
/*     */   private int copeWait;
/*     */   
/*     */   public AutoRespawnMixin(Category category, String name, String description) {
/*  43 */     super(category, name, description);
/*     */   }
/*     */   
/*     */   @Inject(method = {"<init>"}, at = {@At("TAIL")}, remap = false)
/*     */   private void onInit(CallbackInfo ci) {
/*  48 */     SettingGroup sgGeneral = this.settings.getDefaultGroup();
/*  49 */     SettingGroup sgCope = this.settings.createGroup("Auto Cope");
/*     */ 
/*     */     
/*  52 */     this.autoRekit = sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder())
/*  53 */         .name("rekit"))
/*  54 */         .description("Whether to automatically get a kit after dying."))
/*  55 */         .defaultValue(Boolean.valueOf(false)))
/*  56 */         .build());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  63 */     Objects.requireNonNull(this.autoRekit); this.chatInfo = sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("chat-info")).description("Whether to send info about rekitting.")).defaultValue(Boolean.valueOf(true))).visible(this.autoRekit::get))
/*  64 */         .build());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  71 */     Objects.requireNonNull(this.autoRekit); this.kitName = sgGeneral.add((Setting)((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)(new StringSetting.Builder()).name("kit-name")).description("The name of your kit.")).defaultValue("")).visible(this.autoRekit::get))
/*  72 */         .build());
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  77 */     this.autoCope = sgCope.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder())
/*  78 */         .name("auto-cope"))
/*  79 */         .description("Automatically make excuses after you die."))
/*  80 */         .defaultValue(Boolean.valueOf(false)))
/*  81 */         .build());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  90 */     Objects.requireNonNull(this.autoCope); this.copeDelay = sgCope.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("cope-delay")).description("How long to wait in seconds after you die to cope.")).defaultValue(Integer.valueOf(1))).range(0, 5).sliderRange(0, 5).visible(this.autoCope::get))
/*  91 */         .build());
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
/* 104 */     Objects.requireNonNull(this.autoCope); this.messages = sgCope.add((Setting)((StringListSetting.Builder)((StringListSetting.Builder)((StringListSetting.Builder)((StringListSetting.Builder)(new StringListSetting.Builder()).name("cope-messages")).description("What messages to choose from after you die.")).defaultValue(List.of("Why am I lagging so hard wtf??", "I totem failed that doesn't count", "Leave the green hole challenge", "You're actually so braindead", "How many totems do you have??"))).visible(this.autoCope::get))
/* 105 */         .build());
/*     */   }
/*     */ 
/*     */   
/*     */   @EventHandler(priority = 100)
/*     */   private void onOpenScreenEvent(OpenScreenEvent event) {
/* 111 */     if (!(event.screen instanceof net.minecraft.class_418))
/*     */       return; 
/* 113 */     ((WaypointsModule)Modules.get().get(WaypointsModule.class)).addDeath(this.mc.field_1724.method_19538());
/* 114 */     this.mc.field_1724.method_7331();
/* 115 */     event.cancel();
/*     */     
/* 117 */     if (((Boolean)this.autoRekit.get()).booleanValue()) this.shouldRekit = true;
/*     */     
/* 119 */     if (((Boolean)this.autoCope.get()).booleanValue()) {
/* 120 */       this.copeWait = ((Integer)this.copeDelay.get()).intValue() * 20;
/* 121 */       this.shouldCope = true;
/*     */     } 
/*     */   }
/*     */   
/*     */   @EventHandler
/*     */   private void onTick(TickEvent.Post event) {
/* 127 */     if (this.rekitWait == 0 && this.shouldRekit) {
/* 128 */       if (((Boolean)this.chatInfo.get()).booleanValue()) info("Rekitting with kit " + (String)this.kitName.get() + ".", new Object[0]); 
/* 129 */       this.mc.field_1724.method_44096("/kit " + (String)this.kitName.get(), (class_2561)class_2561.method_43470("/kit" + (String)this.kitName.get()));
/* 130 */       this.shouldRekit = false;
/* 131 */       this.rekitWait = 60;
/* 132 */     } else if (this.rekitWait > 0) {
/* 133 */       this.rekitWait--;
/*     */     } 
/*     */     
/* 136 */     if (this.copeWait <= 0 && this.shouldCope) {
/* 137 */       this.mc.field_1724.method_44096(getExcuseMessage(), (class_2561)class_2561.method_43470(getExcuseMessage()));
/* 138 */       this.shouldCope = false;
/* 139 */     } else if (this.copeWait > 0) {
/* 140 */       this.copeWait--;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private String getExcuseMessage() {
/* 147 */     if (((List)this.messages.get()).isEmpty()) {
/* 148 */       error("Your message list is empty!", new Object[0]);
/* 149 */       return "Literally how??";
/* 150 */     }  String excuseMessage = ((List<String>)this.messages.get()).get((new Random()).nextInt(((List)this.messages.get()).size()));
/*     */     
/* 152 */     return excuseMessage;
/*     */   }
/*     */ }


/* Location:              C:\Users\Shotgun\OneDrive\Desktop\Nova-v-100000 (1).jar!\Nova\mixins\meteor\AutoRespawnMixin.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */