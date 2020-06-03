package tschallacka.mods.soulchoirnoteblock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NoteBlock;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.properties.NoteBlockInstrument;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.NoteBlockEvent.Octave;
import net.minecraftforge.event.world.NoteBlockEvent.Play;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SoulChoirNoteBlock.MODID)
public class SoulChoirNoteBlock
{
	public final static String MODID = "soulchoirnoteblock";
	public final static String CHOIR = "soul_sand_choir";
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public SoulChoirNoteBlock() {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    

    @SubscribeEvent 
	public void onNotePlayed(final Play played) 
    {
		BlockPos pos = played.getPos();
		BlockPos down = pos.down();
		IWorld world = played.getWorld();
		if(!world.isRemote()) {
    		Block block = world.getBlockState(down).getBlock();
    		if(block == Blocks.SOUL_SAND) {    			
    			
    			ResourceLocation soundLocation = new ResourceLocation(MODID, CHOIR);
    			SoundEvent sound = new SoundEvent(soundLocation);

    			BlockState state = played.getState()
    								.with(NoteBlock.NOTE, played.getVanillaNoteId())
    								.with(NoteBlock.INSTRUMENT, played.getInstrument());
    			
    		    int height = state.get(NoteBlock.NOTE);	    		    
    		    float pitch = (float)Math.pow(2.0D, (double)(height - 12) / 12.0D);
    			world.playSound(null, played.getPos(), RegistryEvents.CHOIR_SOUND, SoundCategory.MUSIC,3.0f, pitch);
    			world.addParticle(ParticleTypes.NOTE, (double)pos.getX() + 0.5D, (double)pos.getY() + 1.2D, (double)pos.getZ() + 0.5D, (double)height / 24.0D, 0.0D, 0.0D);
    			played.setCanceled(true);
    			;
    		}
    		else if(block == Blocks.QUARTZ_BLOCK) 
    		{    			
    			played.setInstrument(NoteBlockInstrument.COW_BELL);
    		}
		}
		
	}

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents { 
    	static final SoundEvent CHOIR_SOUND;
    	
    	static {
    		ResourceLocation choir = new ResourceLocation(MODID, CHOIR);
    	    CHOIR_SOUND  = new SoundEvent(choir).setRegistryName(CHOIR);
    	}
    	
    	@SubscribeEvent
    	public static void registerSounds(RegistryEvent.Register<SoundEvent> event){
    		final SoundEvent[] soundEvents = {
    				CHOIR_SOUND
    		};
    		event.getRegistry().registerAll(soundEvents);
    	}
    }
}
