package Simp.mixins.minecraft;

import net.minecraft.class_1922;
import net.minecraft.class_2199;
import net.minecraft.class_2338;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_3726;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_2199.class})
public class AnvilBlockMixin {
  @Inject(method = {"getOutlineShape"}, at = {@At("HEAD")}, cancellable = true)
  public void getOutlineShape(class_2680 state, class_1922 world, class_2338 pos, class_3726 context, CallbackInfoReturnable<class_265> cir) {}
}


/* Location:              C:\Users\Shotgun\OneDrive\Desktop\Nova-v-100000 (1).jar!\Simp\mixins\minecraft\AnvilBlockMixin.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */