/*     */ package Nova.modules.misc;
/*     */ 
/*     */ import Nova.NovaAddon;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import meteordevelopment.discordipc.DiscordIPC;
/*     */ import meteordevelopment.discordipc.RichPresence;
/*     */ import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
/*     */ import meteordevelopment.meteorclient.events.world.TickEvent;
/*     */ import meteordevelopment.meteorclient.gui.GuiTheme;
/*     */ import meteordevelopment.meteorclient.gui.utils.StarscriptTextBoxRenderer;
/*     */ import meteordevelopment.meteorclient.gui.widgets.WWidget;
/*     */ import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
/*     */ import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
/*     */ import meteordevelopment.meteorclient.settings.EnumSetting;
/*     */ import meteordevelopment.meteorclient.settings.IntSetting;
/*     */ import meteordevelopment.meteorclient.settings.Setting;
/*     */ import meteordevelopment.meteorclient.settings.SettingGroup;
/*     */ import meteordevelopment.meteorclient.settings.StringListSetting;
/*     */ import meteordevelopment.meteorclient.systems.modules.Module;
/*     */ import meteordevelopment.meteorclient.systems.modules.Modules;
/*     */ import meteordevelopment.meteorclient.systems.modules.misc.DiscordPresence;
/*     */ import meteordevelopment.meteorclient.utils.Utils;
/*     */ import meteordevelopment.meteorclient.utils.misc.MeteorStarscript;
/*     */ import meteordevelopment.meteorclient.utils.player.ChatUtils;
/*     */ import meteordevelopment.orbit.EventHandler;
/*     */ import meteordevelopment.starscript.Script;
/*     */ import meteordevelopment.starscript.compiler.Compiler;
/*     */ import meteordevelopment.starscript.compiler.Parser;
/*     */ import meteordevelopment.starscript.utils.StarscriptError;
/*     */ import net.minecraft.class_156;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RPC
/*     */   extends Module
/*     */ {
/*     */   public enum SelectMode
/*     */   {
/*  42 */     Random,
/*  43 */     Sequential;
/*     */   }
/*     */ 
/*     */   
/*  47 */   private final SettingGroup sgLine1 = this.settings.createGroup("Line 1");
/*  48 */   private final SettingGroup sgLine2 = this.settings.createGroup("Line 2");
/*     */ 
/*     */ 
/*     */   
/*  52 */   private final Setting<List<String>> line1Strings = this.sgLine1.add((Setting)((StringListSetting.Builder)((StringListSetting.Builder)((StringListSetting.Builder)(new StringListSetting.Builder())
/*  53 */       .name("line-1-messages"))
/*  54 */       .description("Messages used for the first line."))
/*  55 */       .defaultValue(new String[] {
/*     */           
/*     */           "Owning {server} Team Nova On Top", "EZZZZZ With Team Nova", "Team Nova On Top", "Owning {server}"
/*     */ 
/*     */ 
/*     */         
/*  61 */         }).onChanged(strings -> recompileLine1()))
/*  62 */       .renderer(StarscriptTextBoxRenderer.class)
/*  63 */       .build());
/*     */ 
/*     */   
/*  66 */   private final Setting<Integer> line1UpdateDelay = this.sgLine1.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder())
/*  67 */       .name("line-1-update-delay"))
/*  68 */       .description("How fast to update the first line in ticks."))
/*  69 */       .defaultValue(Integer.valueOf(50)))
/*  70 */       .range(10, 200)
/*  71 */       .sliderRange(10, 200)
/*  72 */       .build());
/*     */ 
/*     */   
/*  75 */   private final Setting<SelectMode> line1SelectMode = this.sgLine1.add((Setting)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder())
/*  76 */       .name("line-1-select-mode"))
/*  77 */       .description("How to select messages for the first line."))
/*  78 */       .defaultValue(SelectMode.Sequential))
/*  79 */       .build());
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  84 */   private final Setting<List<String>> line2Strings = this.sgLine2.add((Setting)((StringListSetting.Builder)((StringListSetting.Builder)((StringListSetting.Builder)(new StringListSetting.Builder())
/*  85 */       .name("line-2-messages"))
/*  86 */       .description("Messages used for the second line."))
/*  87 */       .defaultValue(new String[] {
/*     */           
/*     */           "Join Team Nova https://discord.gg/rPn7JMdyAZ", "Nova On Top", "Owning vf"
/*     */ 
/*     */         
/*  92 */         }).onChanged(strings -> recompileLine2()))
/*  93 */       .renderer(StarscriptTextBoxRenderer.class)
/*  94 */       .build());
/*     */ 
/*     */   
/*  97 */   private final Setting<Integer> line2UpdateDelay = this.sgLine2.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder())
/*  98 */       .name("line-2-update-delay"))
/*  99 */       .description("How fast to update the second line in ticks."))
/* 100 */       .defaultValue(Integer.valueOf(200)))
/* 101 */       .range(10, 200)
/* 102 */       .sliderRange(10, 200)
/* 103 */       .build());
/*     */ 
/*     */   
/* 106 */   private final Setting<SelectMode> line2SelectMode = this.sgLine2.add((Setting)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder())
/* 107 */       .name("line-2-select-mode"))
/* 108 */       .description("How to select messages for the second line."))
/* 109 */       .defaultValue(SelectMode.Sequential))
/* 110 */       .build());
/*     */ 
/*     */ 
/*     */   
/*     */   public RPC() {
/* 115 */     super(NovaAddon.nova, "Nova-RPC", "Displays Team Nova as your presence on Discord.");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 125 */     this.line1Scripts = new ArrayList<>();
/*     */ 
/*     */     
/* 128 */     this.line2Scripts = new ArrayList<>();
/*     */     this.runInMainMenu = true;
/*     */   }
/*     */   private static final RichPresence rpc = new RichPresence(); private int ticks; private boolean forceUpdate; private boolean lastWasInMainMenu; private final List<Script> line1Scripts;
/*     */   
/*     */   public void onActivate() {
/* 134 */     checkRPC();
/*     */     
/* 136 */     DiscordIPC.start(1016532646081335326L, null);
/*     */     
/* 138 */     rpc.setStart(System.currentTimeMillis() / 1000L);
/* 139 */     rpc.setLargeImage("nova", "Nova");
/*     */     
/* 141 */     recompileLine1();
/* 142 */     recompileLine2();
/*     */     
/* 144 */     this.ticks = 0;
/* 145 */     this.line1Ticks = 0;
/* 146 */     this.line2Ticks = 0;
/* 147 */     this.lastWasInMainMenu = false;
/*     */     
/* 149 */     this.line1I = 0;
/* 150 */     this.line2I = 0;
/*     */   }
/*     */   private int line1Ticks; private int line1I; private final List<Script> line2Scripts; private int line2Ticks; private int line2I;
/*     */   public void checkRPC() {
/* 154 */     DiscordPresence presence = (DiscordPresence)Modules.get().get(DiscordPresence.class);
/* 155 */     if (presence == null)
/* 156 */       return;  if (presence.isActive()) presence.toggle();
/*     */   
/*     */   }
/*     */   
/*     */   public void onDeactivate() {
/* 161 */     DiscordIPC.stop();
/*     */   }
/*     */   
/*     */   private void recompile(List<String> messages, List<Script> scripts) {
/* 165 */     scripts.clear();
/*     */     
/* 167 */     for (int i = 0; i < messages.size(); i++) {
/* 168 */       Parser.Result result = Parser.parse(messages.get(i));
/*     */       
/* 170 */       if (result.hasErrors()) {
/* 171 */         if (Utils.canUpdate()) {
/* 172 */           MeteorStarscript.printChatError(i, result.errors.get(0));
/*     */         
/*     */         }
/*     */       }
/*     */       else {
/*     */         
/* 178 */         scripts.add(Compiler.compile(result));
/*     */       } 
/*     */     } 
/* 181 */     this.forceUpdate = true;
/*     */   }
/*     */   
/*     */   private void recompileLine1() {
/* 185 */     recompile((List<String>)this.line1Strings.get(), this.line1Scripts);
/*     */   }
/*     */   
/*     */   private void recompileLine2() {
/* 189 */     recompile((List<String>)this.line2Strings.get(), this.line2Scripts);
/*     */   }
/*     */   
/*     */   @EventHandler
/*     */   private void onTick(TickEvent.Post event) {
/* 194 */     boolean update = false;
/*     */ 
/*     */     
/* 197 */     if (this.ticks >= 200 || this.forceUpdate) {
/* 198 */       update = true;
/*     */       
/* 200 */       this.ticks = 0;
/*     */     } else {
/* 202 */       this.ticks++;
/*     */     } 
/* 204 */     if (Utils.canUpdate()) {
/*     */       
/* 206 */       if (this.line1Ticks >= ((Integer)this.line1UpdateDelay.get()).intValue() || this.forceUpdate)
/* 207 */       { if (this.line1Scripts.size() > 0) {
/* 208 */           int i = Utils.random(0, this.line1Scripts.size());
/* 209 */           if (this.line1SelectMode.get() == SelectMode.Sequential) {
/* 210 */             if (this.line1I >= this.line1Scripts.size()) this.line1I = 0; 
/* 211 */             i = this.line1I++;
/*     */           } 
/*     */           
/*     */           try {
/* 215 */             rpc.setDetails(MeteorStarscript.ss.run(this.line1Scripts.get(i)).toString());
/* 216 */           } catch (StarscriptError e) {
/* 217 */             ChatUtils.error("Starscript", e.getMessage(), new Object[0]);
/*     */           } 
/*     */         } 
/* 220 */         update = true;
/*     */         
/* 222 */         this.line1Ticks = 0; }
/* 223 */       else { this.line1Ticks++; }
/*     */ 
/*     */       
/* 226 */       if (this.line2Ticks >= ((Integer)this.line2UpdateDelay.get()).intValue() || this.forceUpdate)
/* 227 */       { if (this.line2Scripts.size() > 0) {
/* 228 */           int i = Utils.random(0, this.line2Scripts.size());
/* 229 */           if (this.line2SelectMode.get() == SelectMode.Sequential) {
/* 230 */             if (this.line2I >= this.line2Scripts.size()) this.line2I = 0; 
/* 231 */             i = this.line2I++;
/*     */           } 
/*     */           
/*     */           try {
/* 235 */             rpc.setState(MeteorStarscript.ss.run(this.line2Scripts.get(i)).toString());
/* 236 */           } catch (StarscriptError e) {
/* 237 */             ChatUtils.error("Starscript", e.getMessage(), new Object[0]);
/*     */           } 
/*     */         } 
/* 240 */         update = true;
/*     */         
/* 242 */         this.line2Ticks = 0; }
/* 243 */       else { this.line2Ticks++; }
/*     */ 
/*     */     
/* 246 */     } else if (!this.lastWasInMainMenu) {
/* 247 */       rpc.setDetails("Nova On Top");
/*     */       
/* 249 */       if (this.mc.field_1755 instanceof net.minecraft.class_442) { rpc.setState("Looking at title screen"); }
/* 250 */       else if (this.mc.field_1755 instanceof net.minecraft.class_526) { rpc.setState("Selecting world"); }
/* 251 */       else if (this.mc.field_1755 instanceof net.minecraft.class_525 || this.mc.field_1755 instanceof net.minecraft.class_5235) { rpc.setState("Creating world"); }
/* 252 */       else if (this.mc.field_1755 instanceof net.minecraft.class_524) { rpc.setState("Editing world"); }
/* 253 */       else if (this.mc.field_1755 instanceof net.minecraft.class_3928) { rpc.setState("Loading world"); }
/* 254 */       else if (this.mc.field_1755 instanceof net.minecraft.class_500) { rpc.setState("Selecting server"); }
/* 255 */       else if (this.mc.field_1755 instanceof net.minecraft.class_422) { rpc.setState("Adding server"); }
/* 256 */       else if (this.mc.field_1755 instanceof net.minecraft.class_412 || this.mc.field_1755 instanceof net.minecraft.class_420) { rpc.setState("Connecting to server"); }
/* 257 */       else if (this.mc.field_1755 instanceof meteordevelopment.meteorclient.gui.WidgetScreen) { rpc.setState("Browsing the GUI"); }
/* 258 */       else if (this.mc.field_1755 instanceof net.minecraft.class_429 || this.mc.field_1755 instanceof net.minecraft.class_440 || this.mc.field_1755 instanceof net.minecraft.class_443 || this.mc.field_1755 instanceof net.minecraft.class_446 || this.mc.field_1755 instanceof net.minecraft.class_458 || this.mc.field_1755 instanceof net.minecraft.class_426 || this.mc.field_1755 instanceof net.minecraft.class_404 || this.mc.field_1755 instanceof net.minecraft.class_5375 || this.mc.field_1755 instanceof net.minecraft.class_4189) { rpc.setState("Changing options"); }
/* 259 */       else if (this.mc.field_1755 instanceof net.minecraft.class_445) { rpc.setState("Reading credits"); }
/* 260 */       else if (this.mc.field_1755 instanceof net.minecraft.class_4905) { rpc.setState("Browsing Realms"); }
/*     */       else
/* 262 */       { String className = this.mc.field_1755.getClass().getName();
/*     */         
/* 264 */         if (className.startsWith("com.terraformersmc.modmenu.gui")) { rpc.setState("Browsing mods"); }
/* 265 */         else if (className.startsWith("me.jellysquid.mods.sodium.client")) { rpc.setState("Changing options"); }
/* 266 */         else { rpc.setState("In main menu"); }
/*     */          }
/*     */       
/* 269 */       update = true;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 274 */     if (update) DiscordIPC.setActivity(rpc); 
/* 275 */     this.forceUpdate = false;
/* 276 */     this.lastWasInMainMenu = !Utils.canUpdate();
/*     */   }
/*     */   
/*     */   @EventHandler
/*     */   private void onOpenScreen(OpenScreenEvent event) {
/* 281 */     if (!Utils.canUpdate()) this.lastWasInMainMenu = false;
/*     */   
/*     */   }
/*     */   
/*     */   public WWidget getWidget(GuiTheme theme) {
/* 286 */     WHorizontalList buttons = theme.horizontalList();
/*     */     
/* 288 */     WButton meteor = (WButton)buttons.add((WWidget)theme.button("Meteor Placeholders")).widget();
/*     */ 
/*     */     
/* 291 */     meteor.action = (() -> class_156.method_668().method_670("https://github.com/MeteorDevelopment/meteor-client/wiki/Starscript"));
/*     */     
/* 293 */     return (WWidget)buttons;
/*     */   }
/*     */ }


/* Location:              C:\Users\Shotgun\OneDrive\Desktop\Nova-v-100000 (1).jar!\Nova\modules\misc\RPC.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */