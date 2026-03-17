package com.tegonal.variist.providers

import com.tegonal.variist.config.ArgsRangeOptions
import com.tegonal.variist.testutils.RequestedMinAndMaxArgsTest

class ArgsRangeOptionsTest : RequestedMinAndMaxArgsTest {

	override fun setupRequestedMinArgsMaxArgs(requestedMinArgs: Int?, maxArgs: Int?) {
		ArgsRangeOptions(requestedMinArgs = requestedMinArgs, maxArgs = maxArgs)
	}

	//TODO 2.2.0 check profile is not blank
}
