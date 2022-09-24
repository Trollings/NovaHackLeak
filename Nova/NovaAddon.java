/*    */ package Nova;
/*    */ import Nova.modules.combat.AutoMinePlus;
/*    */ import Nova.modules.combat.FeetTrap;
/*    */ import Nova.modules.combat.NovaCrystal;
/*    */ import Nova.modules.combat.StrafePlus;
/*    */ import Nova.modules.misc.RPC;
/*    */ import Nova.modules.misc.RocketFly;
/*    */ import java.lang.invoke.MethodHandles;
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ import java.lang.reflect.Method;
/*    */ import meteordevelopment.meteorclient.MeteorClient;
/*    */ import meteordevelopment.meteorclient.addons.MeteorAddon;
/*    */ import meteordevelopment.meteorclient.systems.modules.Category;
/*    */ import meteordevelopment.meteorclient.systems.modules.Module;
/*    */ import meteordevelopment.meteorclient.systems.modules.Modules;
/*    */ import net.minecraft.class_1802;
/*    */ 
/*    */ public class NovaAddon extends MeteorAddon {
/* 19 */   public static final Category nova = new Category("Nova Combat/utils", class_1802.field_8367.method_7854());
/*    */ 
/*    */   
/*    */   public void onInitialize() {
/* 23 */     Log("Beginning initialization.");
/* 24 */     MeteorClient.EVENT_BUS.registerLambdaFactory("Nova", (lookupInMethod, klass) -> (MethodHandles.Lookup)lookupInMethod.invoke(null, new Object[] { klass, MethodHandles.lookup() }));
/*    */ 
/*    */     
/* 27 */     Log("Adding Starscript placeholders...");
/*    */ 
/*    */ 
/*    */     
/* 31 */     Log("Adding HUD modules...");
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 36 */     Modules.get().add((Module)new RPC());
/* 37 */     Modules.get().add((Module)new RocketFly());
/* 38 */     Modules.get().add((Module)new FeetTrap());
/* 39 */     Modules.get().add((Module)new NovaCrystal());
/* 40 */     Modules.get().add((Module)new StrafePlus());
/* 41 */     Modules.get().add((Module)new AutoMinePlus());
/*    */     
/* 43 */     Log("Initialized successfully!");
/*    */   }
/*    */ 
/*    */   
/*    */   public void onRegisterCategories() {
/* 48 */     Modules.registerCategory(nova);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getPackage() {
/* 53 */     return "nova";
/*    */   }
/*    */   
/*    */   public static void Log(String text) {
/* 57 */     System.out.println("[Nova] " + text);
/*    */   }
/*    */ }


/* Location:              C:\Users\Shotgun\OneDrive\Desktop\Nova-v-100000 (1).jar!\Nova\NovaAddon.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */