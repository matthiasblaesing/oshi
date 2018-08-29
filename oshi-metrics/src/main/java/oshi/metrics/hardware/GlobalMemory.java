/**
 *
 * Copyright 2013, Somete Group, LLC. All rights reserved.
 *
 * $LastChangedDate$
 * $LastChangedBy$
 * $Revision$
 *
 */
package oshi.metrics.hardware;

public class GlobalMemory implements oshi.hardware.GlobalMemory {

    private static final long serialVersionUID = 1L;
    private static final oshi.hardware.GlobalMemory GM = HardwareAbstractionLayer.HAL.getMemory();

    public long available;
    public long total;
    public long swapTotal;
    public long swapUsed;
    public long swapPagesIn;
    public long swapPagesOut;
    public long pageSize;

    /**
     * @return Returns the available.
     */
    public long getAvailable() {
        available = GM.getAvailable();
        return available;
    }

    /**
     * @return Returns the total.
     */
    public long getTotal() {
        total = GM.getTotal();
        return total;
    }

    /**
     * @return Returns the swapTotal.
     */
    public long getSwapTotal() {
        swapTotal = GM.getSwapTotal();
        return swapTotal;
    }

    /**
     * @return Returns the swapUsed.
     */
    public long getSwapUsed() {
        swapUsed = GM.getSwapUsed();
        return swapUsed;
    }

    /**
     * @return Returns the swapPagesIn.
     */
    public long getSwapPagesIn() {
        swapPagesIn = GM.getSwapPagesIn();
        return swapPagesIn;
    }

    /**
     * @return Returns the swapPagesOut.
     */
    public long getSwapPagesOut() {
        swapPagesOut = GM.getSwapPagesOut();
        return swapPagesOut;
    }

    /**
     * @return Returns the pageSize.
     */
    public long getPageSize() {
        pageSize = GM.getPageSize();
        return pageSize;
    }
}
