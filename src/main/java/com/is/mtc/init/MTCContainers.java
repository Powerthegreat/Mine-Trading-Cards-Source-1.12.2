package com.is.mtc.init;

import com.is.mtc.binder.BinderItemContainer;
import com.is.mtc.displayer.DisplayerBlockContainer;
import com.is.mtc.displayer_mono.MonoDisplayerBlockContainer;
import com.is.mtc.util.Reference;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class MTCContainers {
	public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, Reference.MODID);
	public static RegistryObject<ContainerType<DisplayerBlockContainer>> displayerBlockContainer = CONTAINER_TYPES
			.register("block_displayer",
					() -> IForgeContainerType.create(DisplayerBlockContainer::new));
	public static RegistryObject<ContainerType<MonoDisplayerBlockContainer>> monoDisplayerBlockContainer = CONTAINER_TYPES
			.register("block_monodisplayer",
					() -> IForgeContainerType.create(MonoDisplayerBlockContainer::new));
	;
	public static RegistryObject<ContainerType<BinderItemContainer>> binderContainer = CONTAINER_TYPES
			.register("item_binder",
					() -> IForgeContainerType.create(BinderItemContainer::new));
	;
}
