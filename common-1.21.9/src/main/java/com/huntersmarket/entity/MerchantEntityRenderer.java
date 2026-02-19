package com.huntersmarket.entity;

import com.huntersmarket.HuntersMarket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class MerchantEntityRenderer extends MobRenderer<MerchantEntity, MerchantRenderState, MerchantModel> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(HuntersMarket.MOD_ID, "textures/entity/merchant.png");

    public MerchantEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new MerchantModel(context.bakeLayer(MerchantModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public MerchantRenderState createRenderState() {
        return new MerchantRenderState();
    }

    @Override
    public void extractRenderState(MerchantEntity entity, MerchantRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.unhappyCounter = entity.getUnhappyCounter();
    }

    @Override
    public ResourceLocation getTextureLocation(MerchantRenderState state) {
        return TEXTURE;
    }
}
