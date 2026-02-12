package com.tegonal.variist.config

import com.tegonal.variist.testutils.RequestedMinAndMaxArgsTest

class VariistConfigTest : RequestedMinAndMaxArgsTest {

	override fun setupRequestedMinArgsMaxArgs(requestedMinArgs: Int?, maxArgs: Int?) {
		VariistConfig(requestedMinArgs = requestedMinArgs, maxArgs = maxArgs)
	}

	//TODO 2.1.0 add good first issues for the tests of the remaining invariants?
}
