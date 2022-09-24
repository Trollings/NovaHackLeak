/*    */ package Nova.util;
/*    */ 
/*    */ import meteordevelopment.meteorclient.utils.world.TickRate;
/*    */ 
/*    */ public class TimerUtils
/*    */ {
/*  7 */   private long nanoTime = -1L;
/*    */   
/*    */   public void reset() {
/* 10 */     this.nanoTime = System.nanoTime();
/*    */   }
/*    */   
/*    */   public void setTicks(long ticks) {
/* 14 */     this.nanoTime = System.nanoTime() - convertTicksToNano(ticks);
/* 15 */   } public void setNano(long time) { this.nanoTime = System.nanoTime() - time; }
/* 16 */   public void setMicro(long time) { this.nanoTime = System.nanoTime() - convertMicroToNano(time); }
/* 17 */   public void setMillis(long time) { this.nanoTime = System.nanoTime() - convertMillisToNano(time); } public void setSec(long time) {
/* 18 */     this.nanoTime = System.nanoTime() - convertSecToNano(time);
/*    */   }
/*    */   
/*    */   public long getTicks() {
/* 22 */     return convertNanoToTicks(this.nanoTime);
/* 23 */   } public long getNano() { return this.nanoTime; }
/* 24 */   public long getMicro() { return convertNanoToMicro(this.nanoTime); }
/* 25 */   public long getMillis() { return convertNanoToMillis(this.nanoTime); } public long getSec() {
/* 26 */     return convertNanoToSec(this.nanoTime);
/*    */   }
/*    */   
/*    */   public boolean passedTicks(long ticks) {
/* 30 */     return passedNano(convertTicksToNano(ticks));
/* 31 */   } public boolean passedNano(long time) { return (System.nanoTime() - this.nanoTime >= time); }
/* 32 */   public boolean passedMicro(long time) { return passedNano(convertMicroToNano(time)); }
/* 33 */   public boolean passedMillis(long time) { return passedNano(convertMillisToNano(time)); } public boolean passedSec(long time) {
/* 34 */     return passedNano(convertSecToNano(time));
/*    */   }
/*    */   
/*    */   public long convertMillisToTicks(long time) {
/* 38 */     return time / 50L;
/* 39 */   } public long convertTicksToMillis(long ticks) { return ticks * 50L; }
/* 40 */   public long convertNanoToTicks(long time) { return convertMillisToTicks(convertNanoToMillis(time)); } public long convertTicksToNano(long ticks) {
/* 41 */     return convertMillisToNano(convertTicksToMillis(ticks));
/*    */   }
/*    */   
/*    */   public long convertSecToMillis(long time) {
/* 45 */     return time * 1000L;
/* 46 */   } public long convertSecToMicro(long time) { return convertMillisToMicro(convertSecToMillis(time)); } public long convertSecToNano(long time) {
/* 47 */     return convertMicroToNano(convertMillisToMicro(convertSecToMillis(time)));
/*    */   }
/* 49 */   public long convertMillisToMicro(long time) { return time * 1000L; } public long convertMillisToNano(long time) {
/* 50 */     return convertMicroToNano(convertMillisToMicro(time));
/*    */   } public long convertMicroToNano(long time) {
/* 52 */     return time * 1000L;
/*    */   }
/*    */   
/*    */   public long convertNanoToMicro(long time) {
/* 56 */     return time / 1000L;
/* 57 */   } public long convertNanoToMillis(long time) { return convertMicroToMillis(convertNanoToMicro(time)); } public long convertNanoToSec(long time) {
/* 58 */     return convertMillisToSec(convertMicroToMillis(convertNanoToMicro(time)));
/*    */   }
/* 60 */   public long convertMicroToMillis(long time) { return time / 1000L; } public long convertMicroToSec(long time) {
/* 61 */     return convertMillisToSec(convertMicroToMillis(time));
/*    */   } public long convertMillisToSec(long time) {
/* 63 */     return time / 1000L;
/*    */   }
/*    */ 
/*    */   
/*    */   public static double getTPSMatch(boolean TPSSync) {
/* 68 */     return TPSSync ? (TickRate.INSTANCE.getTickRate() / 20.0F) : 1.0D;
/*    */   }
/*    */ }


/* Location:              C:\Users\Shotgun\OneDrive\Desktop\Nova-v-100000 (1).jar!\Nov\\util\TimerUtils.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */