package charginggadgets.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import reborncore.api.blockentity.IMachineGuiHandler;
import reborncore.common.blocks.BlockMachineBase;

import java.util.function.Supplier;

public class GenericMachineBlock extends BlockMachineBase {

    private final IMachineGuiHandler gui;
    Supplier<BlockEntity> blockEntityClass;

    public GenericMachineBlock(IMachineGuiHandler gui, Supplier<BlockEntity> blockEntityClass) {
        super();
        this.blockEntityClass = blockEntityClass;
        this.gui = gui;
    }

    public GenericMachineBlock(Block.Settings settings, IMachineGuiHandler gui, Supplier<BlockEntity> blockEntityClass) {
        super(settings);
        this.blockEntityClass = blockEntityClass;
        this.gui = gui;
    }

    @Override
    public BlockEntity createBlockEntity(BlockView worldIn) {
        if (blockEntityClass == null) {
            return null;
        }
        return blockEntityClass.get();
    }


    @Override
    public IMachineGuiHandler getGui() {
        return gui;
    }
}
