package de.thm.mni.compilerbau.table

/**
 * This class describes the stack frame layout of a procedure.
 * It contains the sizes of the various subareas and provides methods to retrieve information about the stack frame required to generate code for the procedure.
 */
class StackLayout {
    // The following values have to be set in phase 5
    var argumentAreaSize: Int? = null
    var localVarAreaSize: Int? = null
    var outgoingAreaSize: Int? = null

    /**
     * A leaf procedure is a procedure that does not call any other procedure in its body.
     *
     * @return whether the procedure this stack layout describes is a leaf procedure.
     */
    val isLeafProcedure: Boolean
        get() = outgoingAreaSize!! < 0

    /**
     * @return The total size of the stack frame described by this object.
     */
    val frameSize
        get() = localVarAreaSize!! + (if (isLeafProcedure) 4 else outgoingAreaSize!! + 8)

    /**
     * @return The offset (starting from the new stack pointer) where the old frame pointer is stored in this stack frame.
     */
    val oldFramePointerOffset
        get() = if (isLeafProcedure) 0 else outgoingAreaSize!! + 4

    /**
     * @return The offset (starting from the new frame pointer) where the old return adress is stored in this stack frame.
     */
    val oldReturnAddressOffset
        get() = -(localVarAreaSize!! + 8)
}
