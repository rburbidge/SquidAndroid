package com.sirnommington.squid.activity.share;

import com.sirnommington.squid.services.squid.SquidService;

/**
 * Provides access to the SquidService.
 */
public interface SquidServiceProvider {
    SquidService getSquidService();
}
