package com.snowy.balancehud.mixin;

import com.snowy.balancehud.DashboardRakeback;

import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Connect only the existing dashboard's rakeback view to the HUD's reset state. */
@Pseudo
@Mixin(targets = "dev.profit.dashboard.OverviewScreen", remap = false)
abstract class DashboardRakebackMixin {
    @Redirect(method = "drawRakeback", at = @At(value = "INVOKE",
            target = "Ldev/profit/dashboard/PaymentLedger;entries(Z)Ljava/util/List;", remap = false),
            remap = false, require = 1)
    private List<?> balancehud$rakebackRows(boolean session) {
        return DashboardRakeback.entries();
    }
}
